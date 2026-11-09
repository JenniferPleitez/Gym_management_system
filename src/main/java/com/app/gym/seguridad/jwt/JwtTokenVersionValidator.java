package com.app.gym.seguridad.jwt;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.app.gym.servicios.TokenService;

@Component
public class JwtTokenVersionValidator implements OAuth2TokenValidator<Jwt> {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenVersionValidator.class);
    private final TokenService tokenService;
    private static final String VERSION_CLAIM = "token_version";

    public JwtTokenVersionValidator(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        try {
            // Validamos que el token tenga un subject válido (que sea un ID numérico) (NEW: que sea un pulic ID)
            var subjectError = validateSubject(token);
            if (subjectError.isPresent()) {
                return OAuth2TokenValidatorResult.failure(subjectError.get());
            }

            //Long accountId = Long.parseLong(token.getSubject());
            UUID publicId = UUID.fromString(token.getSubject());

            // Validamos que el token tenga el claim de versión
            var versionClaimError = validateVersionClaim(token);
            if (versionClaimError.isPresent()) {
                return OAuth2TokenValidatorResult.failure(versionClaimError.get());
            }

            // Convertimos el claim a int para comparar luego con la versión en DB/Redis
            int versionInJwt = parseVersion(token.getClaim(VERSION_CLAIM));

            // Validamos que la versión del token coincida con la versión actual del usuario
            if (!tokenService.isTokenVersionValid(publicId, versionInJwt)) {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("session_revoked", "La sesión fue revocada", null)
                );
            }

            return OAuth2TokenValidatorResult.success();

        } catch (Exception e) {
            log.error("Error validando JWT", e);
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Token inválido", null)
            );
        }
    }

    // ----------------------------
    // Helpers funcionales
    // ----------------------------

    private Optional<OAuth2Error> validateSubject(Jwt token) {
        String subject = token.getSubject();

        if (subject == null) {
            return Optional.of(new OAuth2Error("missing_subject", "El token no tiene subject", null));
        }

        /*try {
            Long.parseLong(subject);*/
            return Optional.empty();
        /*} catch (NumberFormatException e) {
            return Optional.of(new OAuth2Error("invalid_subject", "Subject inválido (debe ser ID numérico)", null));
        }*/
    }

    private Optional<OAuth2Error> validateVersionClaim(Jwt token) {
        Object raw = token.getClaim(VERSION_CLAIM);

        if (raw == null) {
            return Optional.of(new OAuth2Error("missing_claim", "El token no tiene token_version", null));
        }

        if (!(raw instanceof Number) && !(raw instanceof String)) {
            return Optional.of(new OAuth2Error("invalid_claim", "token_version tiene un tipo inválido", null));
        }

        return Optional.empty();
    }

    private int parseVersion(Object raw) {
        if (raw instanceof Number) {
            return ((Number) raw).intValue();
        }
        return Integer.parseInt(raw.toString());
    }
}