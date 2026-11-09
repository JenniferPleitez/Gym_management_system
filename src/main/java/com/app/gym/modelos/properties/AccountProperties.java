package com.app.gym.modelos.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@ConfigurationProperties(prefix = "account")
@Validated
public record AccountProperties(
    @NotBlank(message = "Cannot be blank")
    String cachePrefix,

    @NotBlank(message = "Cannot be blank")
    String globalPrefix,

    @NotBlank(message = "Cannot be null")
    String idPrefix,

    @Positive(message = "Cannot be negative")
    long cacheTtl
){}
