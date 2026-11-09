package com.app.gym.servicios;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.gym.dtos.account.AccountCrearDTO;
import com.app.gym.dtos.login.AuthResponse;
import com.app.gym.dtos.login.LoginRequestDTO;
import com.app.gym.modelos.Account;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final RateLimitService rateLimitService;

    // -------------------------
    // LOGIN
    // -------------------------
    @Transactional
    public AuthResponse login(LoginRequestDTO request) {
        Account account = accountService.findByEmail(request.email());

        rateLimitService.checkLoginAttemps(account.getEmail());

        Authentication auth;
        
        try {
            auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException ex) {
            rateLimitService.registerLoginFailure(account.getEmail());
            throw new BadCredentialsException("Invalid credentials.");
        }

        rateLimitService.resetLoginFailures(account.getEmail());

        // Roles
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        //String accessToken = tokenService.generateAccessToken(account, roles);
        String accessToken = tokenService.generateAccessToken(account.getPublicId(), account.getTokenVersion(), roles);

        // Crear refresh token en DB y Redis. Y devuelve RAW token para el cliente
        String rawRefresh = refreshTokenService.createRefreshToken(account);

        AuthResponse response = new AuthResponse();
        response.setAccountId(account.getIdAccount());
        response.setPublicId(account.getPublicId());
        response.setToken(accessToken);
        response.setRefreshToken(rawRefresh);
        response.setExpiresAt(tokenService.getExpiresAt());
        response.setRoles(roles);

        return response;
    }

    // -------------------------
    // REFRESH (rotación)
    // -------------------------
    @Transactional
    public AuthResponse refresh(String presentedRefresh) {
        // Validar token presentado y obtener entidad (validación Redis + DB)
        String newRawRefresh = refreshTokenService.rotateRefreshToken(presentedRefresh);
        Account account = refreshTokenService.getAccountFromRefreshToken(presentedRefresh);

        // Generar nuevo access token
        List<String> roles = account.getRoles().stream().map(r -> r.getRoleName()).toList();
        String newAccessToken = tokenService.generateAccessToken(account.getPublicId(), account.getTokenVersion(), roles);

        AuthResponse response = new AuthResponse();
        response.setAccountId(account.getIdAccount());
        response.setPublicId(account.getPublicId());
        response.setToken(newAccessToken);
        response.setRefreshToken(newRawRefresh);
        response.setExpiresAt(tokenService.getExpiresAt());
        response.setRoles(roles);

        return response;
    }

    // -------------------------
    // REGISTER (público)
    // -------------------------
    @Transactional
    public Account register(AccountCrearDTO dto) {
        Account createdAccount = accountService.createAccount(dto);
        return createdAccount;
    }

    // -------------------------
    // LOGOUT (revocar refresh token concreto)
    // -------------------------
    @Transactional
    public void logout(String presentedRefresh) {
        refreshTokenService.revokeRefreshTokenByRaw(presentedRefresh);
    }

    // -------------------------
    // LOGOUT ALL (global). Retorna el ID de la cuenta deslogeada para poder auditar.
    // -------------------------
    @Transactional
    public void logoutAll(Long id) {
        refreshTokenService.revokeAllByAccount(id);
        accountService.incrementarTokenVersion(id);
    }

    @Transactional
    public void deleteAccountTokenVersion(Long id){
        tokenService.deleteAccountTokenVersion(id);
    }
}
