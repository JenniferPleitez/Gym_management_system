package com.app.gym.modelos.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Validated
@ConfigurationProperties(prefix = "rate-limit")
public record RateLimitProperties(
    @Positive(message = "Cannot be negative")
    long loginFailWindow,

    @Positive(message = "Cannot be negative")
    int loginFailLimit,

    @NotBlank(message = "Cannot be blank")
    String loginFailPrefix,

    @Positive(message = "Cannot be negative")
    long pwdResetWindow,

    @Positive(message = "Cannot be negative")
    int pwdResetLimit,

    @NotBlank(message = "Cannot be blank")
    String pwdResetPrefix,

    @NotBlank(message = "Cannot be blank")
    String ipPrefix,

    @Positive(message = "Cannot be negative")
    int ipLimit,

    @Positive(message = "Cannot be negative")
    long ipWindow,

    @NotBlank(message = "Cannot be blank")
    String verifyEmailPrefix,

    @Positive(message = "Cannot be negative")
    int verifyEmailLimit,

    @Positive(message = "Cannot be negative")
    long verifyEmailWindow,

    @NotBlank(message = "Cannot be blank")
    String changePasswordPrefix,

    @Positive(message = "Cannot be negative")
    int changePasswordLimit,

    @Positive(message = "Cannot be negative")
    long changePasswordWindow,

    @NotBlank(message = "Cannot be blank")
    String globalPrefix
) {}