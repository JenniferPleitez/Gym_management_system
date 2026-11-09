package com.app.gym.seguridad.audit;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 * Filtro para capturar la IP y User-Agent de cada solicitud entrante
 * y almacenarlos en el contexto de auditoría para su uso posterior.
 */
@Component
public class AuditRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        String ip = req.getRemoteAddr();
        String userAgent = req.getHeader("User-Agent");

        // Guardamos en el contexto
        RequestAuditContext.set(ip, userAgent);

        try {
            chain.doFilter(request, response);
        } finally {
            // Limpiamos para evitar fugas entre hilos
            RequestAuditContext.clear();
        }
    }
}