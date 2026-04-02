package com.solevictus.adityaroshanjha.service.impl;

import com.solevictus.adityaroshanjha.entity.UserEntity;
import com.solevictus.adityaroshanjha.io.request.ProfileRequest;
import com.solevictus.adityaroshanjha.io.response.ProfileResponse;
import com.solevictus.adityaroshanjha.repository.UserRepository;
import com.solevictus.adityaroshanjha.service.intf.ProfileService;
import com.solevictus.adityaroshanjha.utils.enums.EmailTemplates;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public ProfileResponse createProfile(@Valid ProfileRequest request) {
        UserEntity newProfile = convertToUserEntity(request);
        if(!userRepository.existsByEmail(request.getEmail())){
            newProfile = userRepository.save(newProfile);
            return convertToProfileResponse(newProfile);
        }

        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
    }

    @Override
    public ProfileResponse getProfile(String email) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : "+email));

        return convertToProfileResponse(existingUser);
    }

    @Override
    public void sendResetOtp(String email) {
        UserEntity existingEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : "+email));

        // Generate 6-digit OTP and set it to the user entity, then save it
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

        //expiration time for otp is 5 minutes
        existingEntity.setResetOtp(otp);
        existingEntity.setResetOtpExpireAt(System.currentTimeMillis() + 5 * 60 * 1000); // OTP valid for 5 minutes
        userRepository.save(existingEntity);

        // Here you would also send the OTP to the user's email using your EmailService
        try{
            emailService.sendResetOtpEmail(existingEntity.getEmail(), otp);
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send OTP email");
        }
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        UserEntity existingEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : "+email));

        if(existingEntity.getResetOtp() == null || !existingEntity.getResetOtp().equals(otp) || existingEntity.getResetOtpExpireAt() < System.currentTimeMillis()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired OTP");
        }

        // Update the user's password and clear the OTP fields
        existingEntity.setPassword(passwordEncoder.encode(newPassword));
        existingEntity.setResetOtp(null);
        existingEntity.setResetOtpExpireAt(0L);
        userRepository.save(existingEntity);
    }

    @Override
    public void sendAccountVerficationOtp(String email) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : "+email));

        if(existingUser.getIsAccountVerified() != null && existingUser.getIsAccountVerified()){
            return; // Account is already verified, no need to send OTP
        }

         // Generate 6-digit OTP and set it to the user entity, then save it
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

        existingUser.setVerifyOtp(otp);
        existingUser.setVerifyOtpExpireAt(System.currentTimeMillis() + 24 * 60 * 60 * 1000); // OTP valid for 24 hours
        userRepository.save(existingUser);

        // Here you would also send the OTP to the user's email using your EmailService
        try{
            emailService.sendAccountVerificationOTP(
                    existingUser.getEmail(),
                    otp
            );
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send OTP email");
        }
    }

    @Override
    public void verifyAccount(String email, String otp) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : "+email));

        if(existingUser.getVerifyOtp() == null || !existingUser.getVerifyOtp().equals(otp) || existingUser.getVerifyOtpExpireAt() < System.currentTimeMillis()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired OTP");
        }

        existingUser.setIsAccountVerified(true);
        existingUser.setVerifyOtp(null);
        existingUser.setVerifyOtpExpireAt(0L);
        userRepository.save(existingUser);

        // Optionally, you can also send a confirmation email to the user here using your EmailService
        try{
            emailService.sendVerificationSuccessEmail(existingUser.getEmail());
        }catch (Exception ex){
            // Log the error but don't fail the verification process
            System.err.println("Failed to send verification success email: " + ex.getMessage());
        }
    }

    @Override
    public String getLoggedInUserId(String email) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : "+email));
        return existingUser.getUserId();
    }

    private ProfileResponse convertToProfileResponse(UserEntity newProfile) {
        return ProfileResponse.builder()
                .name(newProfile.getName())
                .email(newProfile.getEmail())
                .userId(newProfile.getUserId())
                .isAccountVerified(newProfile.getIsAccountVerified())
                .build();
    }

    private UserEntity convertToUserEntity(ProfileRequest request) {
        return UserEntity.builder()
                .email(request.getEmail())
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountVerified(false)
                .resetOtpExpireAt(0L)
                .verifyOtp(null)
                .verifyOtpExpireAt(0L)
                .resetOtp(null)
                .build();
    }
}
