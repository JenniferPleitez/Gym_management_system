package com.app.gym.dtos.audit;

import java.util.Objects;

public record RequestContext(String ip, String userAgent) { 
    public static final RequestContext SYSTEM = new RequestContext("0.0.0.0", "System");
    public static final RequestContext TEST = new RequestContext("127.0.0.1", "Test");

    public RequestContext {
        Objects.requireNonNull(ip, "IP cannot be null");
        Objects.requireNonNull(userAgent, "UserAgent cannot be null");
    }
    
    // Factory method para tests
    public static RequestContext forTest() {
        return new RequestContext("127.0.0.1", "JUnit-Test");
    }
}
