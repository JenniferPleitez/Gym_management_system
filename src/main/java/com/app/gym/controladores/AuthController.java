package com.app.gym.controladores;

import com.app.gym.application.AccountVerificationTokenAppService;
import com.app.gym.application.AuthAppService;
import com.app.gym.application.PasswordResetAppService;
import com.app.gym.dtos.account.AccountCrearDTO;
import com.app.gym.dtos.auth.LogoutRequest;
import com.app.gym.dtos.auth.RequestPasswordResetRequest;
import com.app.gym.dtos.auth.ResendVerificationRequest;
import com.app.gym.dtos.auth.ResetPasswordRequest;
import com.app.gym.dtos.auth.VerifyEmailRequest;
import com.app.gym.dtos.login.AuthResponse;
import com.app.gym.dtos.login.LoginRequestDTO;
import com.app.gym.excepciones.UnauthorizedException;
import com.app.gym.utils.cookie.AuthCookieManager;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AccountVerificationTokenAppService accountVerificationTokenAppService;
    private final AuthAppService authAppService;
    private final PasswordResetAppService passwordResetAppService;
    private final AuthCookieManager authCookieManager;

    public AuthController(AccountVerificationTokenAppService accountVerificationTokenAppService,
                        AuthAppService authAppService,
                        PasswordResetAppService passwordResetAppService,
                        AuthCookieManager authCookieManager) {
        this.accountVerificationTokenAppService = accountVerificationTokenAppService;
        this.authAppService = authAppService;
        this.passwordResetAppService = passwordResetAppService;
        this.authCookieManager = authCookieManager;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authAppService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AccountCrearDTO request) {
        authAppService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Please verify your account by following the instructions that have been sent to your email.");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(/*@RequestBody @Valid RefreshTokenRequest refreshTokenRequest*/
                                                        @CookieValue(name = "refresh_token", required = false) String refreshToken,
                                                        HttpServletResponse response
    ) {
        if(refreshToken == null){
            throw new UnauthorizedException("No refresh token provided.");
        }

        AuthResponse authResponse = authAppService.refresh(refreshToken);
        authCookieManager.setRefreshTokenCookie(response, authResponse.getRefreshToken());
        
        return ResponseEntity.ok(authResponse);
    }
    
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        accountVerificationTokenAppService.verifyEmail(request);
        return ResponseEntity.ok("Your account has been successfully verified. Please log in!");
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody @Valid ResendVerificationRequest resendVerificationRequest) {
        return ResponseEntity.ok(accountVerificationTokenAppService.resendEmailVerificationToken(resendVerificationRequest));
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody @Valid RequestPasswordResetRequest requestPasswordResetRequest) {
        passwordResetAppService.requestResetPasswordToken(requestPasswordResetRequest);
        return ResponseEntity.ok("An email has been sent with instructions for resetting your password.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        passwordResetAppService.verifyResetPasswordToken(resetPasswordRequest);
        return ResponseEntity.ok("Password successfully reset.");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody @Valid LogoutRequest logoutRequest) {
        authAppService.logout(logoutRequest);
        return ResponseEntity.ok("Session successfully closed.");
    }

    @PostMapping("/logout-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logoutAllSessions() {
        authAppService.logoutAll();
        return ResponseEntity.ok("All sessions have been closed.");
    }
}