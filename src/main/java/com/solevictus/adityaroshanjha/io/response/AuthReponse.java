package com.solevictus.adityaroshanjha.io.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthReponse {
    private String email;
    private String token;

}
