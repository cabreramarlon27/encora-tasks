package com.encora.task_manager_service.security;

import jakarta.servlet.http. HttpServletRequest;  import jakarta.servlet.http. HttpServletResponse;  import org.springframework. security. core. AuthenticationException;  import org.springframework. security. web. AuthenticationEntryPoint;  import org.springframework. stereotype. Component;
import java.io.IOException;
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        //todo: do I need this class?
        // Log the exception for debugging (optional but recommended)
//         System.out.println("JwtAuthenticationEntryPoint - Exception: " + authException.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{ \"message\": \"JWT token is expired or invalid\" }");
    }
}