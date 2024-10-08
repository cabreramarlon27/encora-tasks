package com.encora.task_manager_service.security;

import com.encora.task_manager_service.services.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtils;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (isRefreshTokenRequest(request)) {
                filterChain.doFilter(request, response);
                return;
            }
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.extractEmail(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                jwtUtils.validateToken(jwt, userDetails);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {} token: {}", e.getMessage(), e.getClaims());
        } catch (AccessDeniedException e) { // Catch AccessDeniedException
            logger.error("Access Denied: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
            response.getWriter().write("Access Denied");
        } catch (Exception e) {
            logger.error("Cannot set user authentication", e);
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private boolean isRefreshTokenRequest(HttpServletRequest request) {
        return request.getRequestURI().equals("/api/auth/refresh");
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}