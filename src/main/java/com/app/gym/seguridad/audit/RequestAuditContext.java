package com.app.gym.seguridad.audit;

import com.app.gym.dtos.audit.RequestContext;

public class RequestAuditContext {
    private static final ThreadLocal<RequestContext> CONTEXT = ThreadLocal.withInitial(() -> RequestContext.SYSTEM);
    
    public static void set(String ip, String userAgent) {
        CONTEXT.set(new RequestContext(ip != null ? ip : "0.0.0.0", userAgent != null ? userAgent : "Unknown"));
    }
    
    public static RequestContext get() {
        return CONTEXT.get();
    }
    
    public static void clear() {
        CONTEXT.remove();
    }

    public static String getIp() {
        return get().ip();
    }
    
    public static String getUserAgent() {
        return get().userAgent();
    }
}
