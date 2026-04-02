package com.solevictus.adityaroshanjha.service.intf;


import com.solevictus.adityaroshanjha.io.request.ProfileRequest;
import com.solevictus.adityaroshanjha.io.response.ProfileResponse;
import jakarta.validation.Valid;

public interface ProfileService {
   ProfileResponse createProfile(@Valid ProfileRequest request);
   ProfileResponse getProfile(String email);
   void sendResetOtp(String email);
   void resetPassword(String email, String otp, String newPassword);
}
