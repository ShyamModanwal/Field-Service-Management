package com.fieldservicemanagement.fieldservicemanagement.config;

import com.fieldservicemanagement.fieldservicemanagement.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    // =========================================================
    // SKIP JWT FILTER FOR PUBLIC / CORS ENDPOINTS
    // =========================================================

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request) {

        String path = request.getServletPath();
        String method = request.getMethod();

        // CORS preflight request
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        // Login
        if (path.equals("/api/auth/login")) {
            return true;
        }

        // User creation
        if (path.equals("/api/users")
                || path.startsWith("/api/users/")) {
            return true;
        }

        // Swagger UI
        if (path.startsWith("/swagger-ui/")) {
            return true;
        }

        // Swagger HTML
        if (path.equals("/swagger-ui.html")) {
            return true;
        }

        // OpenAPI JSON
        if (path.startsWith("/v3/api-docs")) {
            return true;
        }

        return false;
    }

    // =========================================================
    // JWT FILTER
    // =========================================================

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // =====================================================
        // AUTHORIZATION HEADER CHECK
        // =====================================================

        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // =====================================================
        // EXTRACT JWT TOKEN
        // =====================================================

        String token = authHeader.substring(7);

        try {

            Claims claims = jwtService.extractClaims(token);

            // JWT se user information
            String email = claims.getSubject();

            String role = claims.get(
                    "role",
                    String.class);

            Long userId = claims.get(
                    "userId",
                    Long.class);

            // =================================================
            // ROLE AUTHORITY
            // =================================================

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                    "ROLE_" + role);

            /*
             * Principal format:
             *
             * userId|email
             *
             * Example:
             *
             * 15|admin@test4.com
             */

            String principal = userId + "|" + email;

            // =================================================
            // CREATE AUTHENTICATION
            // =================================================

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    List.of(authority));

            // =================================================
            // STORE AUTHENTICATION
            // =================================================

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

        } catch (Exception e) {

            SecurityContextHolder.clearContext();

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);

            response.setContentType(
                    "application/json");

            response.getWriter().write(
                    "{\"error\":\"Unauthorized\",\"message\":\"Invalid or expired JWT token\"}");

            return;
        }

        // =====================================================
        // CONTINUE REQUEST
        // =====================================================

        filterChain.doFilter(
                request,
                response);
    }
}