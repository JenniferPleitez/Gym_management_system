package com.app.gym.dtos.auth;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record CacheAccountDTO(
    Long idAccount,
    UUID publicId,
    String email,

    Boolean enabled,
    Boolean locked,
    Boolean emailVerified,

    Integer tokenVersion,
    List<String> roles,

    OffsetDateTime createdAt,
    OffsetDateTime lastLogin
) {}
