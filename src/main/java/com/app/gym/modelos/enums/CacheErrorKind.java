package com.app.gym.modelos.enums;

public enum CacheErrorKind {
    SERIALIZATION,
    DESERIALIZATION,
    REDIS_FAILURE,
    TIMEOUT,
    VALIDATION,
    INVALIDATION,
    RECACHING,
    UPDATING,
    UNKNOWN
}
