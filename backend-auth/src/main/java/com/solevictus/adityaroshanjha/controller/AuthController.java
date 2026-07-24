package com.solevictus.adityaroshanjha.controller;


import com.solevictus.adityaroshanjha.io.request.AccountVerificationRequest;
import com.solevictus.adityaroshanjha.io.request.AuthRequest;
import com.solevictus.adityaroshanjha.io.request.ResetPasswordRequest;
import com.solevictus.adityaroshanjha.io.response.AuthReponse;
import com.solevictus.adityaroshanjha.jwtUtil.JwtUtil;
import com.solevictus.adityaroshanjha.service.impl.AppUserDetailsService;
import com.solevictus.adityaroshanjha.service.intf.ProfileService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AppUserDetailsService appUserDetailsService;
    private final JwtUtil jwtUtil;
    private final ProfileService profileService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request){
        try{
            authenticate(request.getEmail(), request.getPassword());
            final UserDetails userDetails = appUserDetailsService.loadUserByUsername(request.getEmail());
            final String jwtToken = jwtUtil.generateToken(userDetails);
            ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("Strict")
                    .build();

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new AuthReponse(request.getEmail(), jwtToken));
        }catch (BadCredentialsException ex){
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message", "Email or password is incorrect");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }catch (DisabledException ex){
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message", "Account is disabled");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }catch (Exception ex){
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message", "Authentication has failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }


    }

    private void authenticate(String email, String password){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }

    @GetMapping("/is-authenticated")
    public ResponseEntity<Boolean> isAuthenticated(
            @CurrentSecurityContext(expression = "authentication?.name") String email
    ){
        return ResponseEntity.ok(email != null);
    }

    @PostMapping("/send-reset-otp")
    public void sendResetOtp(@RequestParam String email){
        try{
            profileService.sendResetOtp(email);
        }catch (Exception ex){
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message", "Something went wrong while sending OTP");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request){
        try{
            profileService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Password reset successful"));
        }catch (ResponseStatusException ex){
            throw ex; // Re-throw the exception to be handled by the global exception handler
        }catch (Exception ex){
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message", "Something went wrong while resetting password");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @PostMapping("/send-otp")
    public void sendAccountVerificationOtp(
            @CurrentSecurityContext(expression = "authentication?.name") String email){
        try{
            profileService.sendAccountVerficationOtp(email);
        }catch (Exception ex){
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message", "Something went wrong while sending OTP");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @PostMapping("/verify-account")
    public ResponseEntity<?> verifyAccount(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @RequestBody AccountVerificationRequest request
    ) {
        try {
            profileService.verifyAccount(email, request.getOtp());
            return ResponseEntity.ok(Map.of("message", "Account verification successful"));
        } catch (ResponseStatusException ex) {
            throw ex; // Re-throw the exception to be handled by the global exception handler
        } catch (Exception ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message", "Something went wrong while verifying account");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        {
            ResponseCookie cookie = ResponseCookie.from("jwt", "")
                    .httpOnly(true)
                    .secure(false) // Set to true in production with HTTPS
                    .path("/")
                    .maxAge(0)
                    .sameSite("Strict")
                    .build();

            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(Map.of("message", "Logged out successfully"));
        }
    }
}
