package com.fieldservicemanagement.fieldservicemanagement.config;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        // =========================================================
        // PASSWORD ENCODER
        // =========================================================

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // =========================================================
        // CORS CONFIGURATION
        // =========================================================

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(
                                List.of(
                                                "http://localhost:5173",
                                                "https://field-service-management-mu.vercel.app"));

                configuration.setAllowedMethods(
                                List.of(
                                                "GET",
                                                "POST",
                                                "PUT",
                                                "DELETE",
                                                "PATCH",
                                                "OPTIONS"));

                configuration.setAllowedHeaders(
                                List.of("*"));

                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration(
                                "/**",
                                configuration);

                return source;
        }

        // =========================================================
        // SECURITY FILTER CHAIN
        // =========================================================

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http

                                // Disable CSRF because we are using JWT
                                .csrf(csrf -> csrf.disable())

                                // Enable CORS
                                .cors(cors -> cors.configurationSource(
                                                corsConfigurationSource()))

                                // JWT based application
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(
                                                                SessionCreationPolicy.STATELESS))

                                // =================================================
                                // AUTHORIZATION
                                // =================================================

                                .authorizeHttpRequests(auth -> auth

                                                // CORS preflight requests
                                                .requestMatchers(
                                                                org.springframework.http.HttpMethod.OPTIONS,
                                                                "/**")
                                                .permitAll()

                                                // Swagger
                                                .requestMatchers(
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/v3/api-docs/**")
                                                .permitAll()

                                                // Login
                                                .requestMatchers(
                                                                "/api/auth/login")
                                                .permitAll()

                                                // User registration
                                                .requestMatchers(
                                                                "/api/users")
                                                .permitAll()

                                                // Everything else requires JWT
                                                .anyRequest().authenticated())

                                // =================================================
                                // UNAUTHORIZED RESPONSE
                                // =================================================

                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(
                                                                (request, response, authException) -> {

                                                                        response.setStatus(
                                                                                        HttpServletResponse.SC_UNAUTHORIZED);

                                                                        response.setContentType(
                                                                                        "application/json");

                                                                        response.getWriter().write(
                                                                                        "{\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}");
                                                                }))

                                // =================================================
                                // JWT FILTER
                                // =================================================

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}