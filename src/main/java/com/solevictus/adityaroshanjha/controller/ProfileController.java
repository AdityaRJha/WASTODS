package com.solevictus.adityaroshanjha.controller;

import com.solevictus.adityaroshanjha.io.request.ProfileRequest;
import com.solevictus.adityaroshanjha.io.response.ProfileResponse;
import com.solevictus.adityaroshanjha.service.intf.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(@Valid @RequestBody ProfileRequest request){
        ProfileResponse response = profileService.createProfile(request);
        //TODO: send welcome email
        return response;
    }

    @GetMapping("/test")
    @ResponseBody
    public String test(Authentication auth){
        return "Auth is working fine";
    }
}
