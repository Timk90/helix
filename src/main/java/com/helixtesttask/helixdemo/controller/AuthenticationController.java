package com.helixtesttask.helixdemo.controller;

import com.helixtesttask.helixdemo.dto.Credentials;
import com.helixtesttask.helixdemo.service.AuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private static final String AUTH_PATH = "authenticate";

    private final AuthService authService;

    @PostMapping(value = AUTH_PATH)
    public JwtToken getAuthToken(@RequestBody Credentials credentials) {
        return new JwtToken(authService.getJwt(credentials));
    }

    @Data
    private static class JwtToken {
        private final String token;
    }
}
