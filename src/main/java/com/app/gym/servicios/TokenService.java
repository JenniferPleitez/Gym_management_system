package com.app.gym.servicios;

import com.app.gym.excepciones.RecursoNoEncontradoExcepcion;
//import com.app.gym.modelos.Account;
import com.app.gym.repositorios.AccountRepositorio;
import com.app.gym.servicios.cache.AccessTokenCacheService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TokenService {

    private final AccountRepositorio accountRepositorio;
    private final JwtEncoder jwtEncoder;
    private final AccessTokenCacheService accessTokenCacheService;

    public TokenService(AccountRepositorio accountRepositorio, 
                        JwtEncoder jwtEncoder,
                        AccessTokenCacheService accessTokenCacheService) {
        this.accountRepositorio = accountRepositorio;
        this.jwtEncoder = jwtEncoder;
        this.accessTokenCacheService = accessTokenCacheService;
    }

    // ================================================================= 
    // GENERACIÓN DEL ACCESS TOKEN JWT
    // =================================================================
    public String generateAccessToken(UUID publicId, Integer tokenVersion, List<String> roles){
        Instant now = Instant.now();
        Instant expiry = now.plus(accessTokenCacheService.getTokenTtl());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("gym-app")
                .issuedAt(now)
                .expiresAt(expiry)
                .subject(publicId.toString())
                .claim("roles", roles)
                .claim("token_version", tokenVersion)
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        cacheAccountTokenVersion(publicId, tokenVersion);

        return token;
    }

    public Instant getExpiresAt() {
        Instant now = Instant.now();
        return now.plus(accessTokenCacheService.getTokenTtl());
    }
    
    // =================================================================
    // VALIDACIÓN DE TOKEN VERSION
    // =================================================================

    //public boolean isTokenVersionValid(Long accountId, Integer versionInJwt){
    public boolean isTokenVersionValid(UUID publicId, Integer versionInJwt){
        String cachedTokenVersion = accessTokenCacheService.getTokenVersion(publicId);

        if (cachedTokenVersion != null) {
            try {
                log.info("Token version saved in redis: {}", cachedTokenVersion);
                return Integer.parseInt(cachedTokenVersion) == versionInJwt;
            } catch (NumberFormatException e) {
                // Si hay un error al convertirlo, continuamos para obtener la versión desde la db
                log.error("Error parsing cached token version for account {}: {}", publicId, e.getMessage());
            }
        }

        var account = accountRepositorio.findByPublicId(publicId)
                .orElseThrow(() -> new RecursoNoEncontradoExcepcion("Account not found"));

        log.info("Token version from DB: {}", account.getTokenVersion());
        Integer dbVersion = account.getTokenVersion();

        cacheAccountTokenVersion(publicId, dbVersion);

        return dbVersion.equals(versionInJwt);
    }

    // Cachea la versión del token de la cuenta en Redis
    public void cacheAccountTokenVersion(UUID publicId, Integer version) {
        accessTokenCacheService.cacheAccountTokenVersion(publicId, version);
    }

    // Elimina la versión del token de la cuenta de Redis (por ejemplo, al incrementar la versión)
    public void deleteAccountTokenVersion(Long accountId) {
        accessTokenCacheService.deleteTokenVersion(accountId);
    }
}