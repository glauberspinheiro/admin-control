package com.revitalize.admincontrol.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class JsonAuthHandlers {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static class EntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex) throws IOException {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            MAPPER.writeValue(res.getOutputStream(), Map.of(
                    "status", 401,
                    "message", "Usuário não autenticado ou sessão expirada.",
                    "path", req.getRequestURI()
            ));
        }
    }

    public static class Denied implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex) throws IOException {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            MAPPER.writeValue(res.getOutputStream(), Map.of(
                    "status", 403,
                    "message", "Acesso negado. Permissão insuficiente.",
                    "path", req.getRequestURI()
            ));
        }
    }
}
