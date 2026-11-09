package com.app.gym.controladores;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.gym.application.GoogleAuthAppService;
import com.app.gym.dtos.login.GoogleAuthResult;
import com.app.gym.dtos.login.google.GoogleLoginRequest;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/google")
public class GoogleAuthControlador {
    private final GoogleAuthAppService googleAuthAppService;

    public GoogleAuthControlador(GoogleAuthAppService googleAuthAppService) {
        this.googleAuthAppService = googleAuthAppService;
    }

    @PostMapping("/login")
    public GoogleAuthResult loginWithGoogle(@Valid @RequestBody GoogleLoginRequest request, HttpServletResponse response) {
        return googleAuthAppService.authenticateWithGoogle(request.idToken(), response);
    }
}
