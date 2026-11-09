package com.app.gym.modelos.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Validated
@ConfigurationProperties(prefix = "token")
public record TokenProperties (
    @NotBlank(message = "Cannot be blank")
    String verifyPrefix,

    @NotBlank(message = "Cannot be blank")
    String pwdResetPrefix,

    @NotBlank(message = "Cannot be blank")
    String userVersionKey,

    @NotBlank(message = "Cannot be blank")
    String verifyTokenPrefix,

    @NotBlank(message = "Cannot be blank")
    String pwdResetTokenPrefix,

    @Positive(message = "Cannot be negative")
    long verifyTokenTtl,

    @Positive(message = "Cannot be negative")
    long accessTokenTtl,

    @Positive(message = "Cannot be negative")
    long refreshTokenTtl,

    @Positive(message = "Cannot be negative")
    long parellelWindow,

    @NotBlank(message = "Cannot be blank")
    String refreshReplacedKey,

    @NotBlank(message = "Cannot be blank")
    String refreshHashKey,

    @NotBlank(message = "Cannot be blank")
    String refreshIdsPrefix
){}
