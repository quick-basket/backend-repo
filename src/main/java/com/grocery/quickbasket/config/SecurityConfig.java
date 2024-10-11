package com.grocery.quickbasket.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Log
public class SecurityConfig {
    private final RsaConfigProperties rsaConfigProperties;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistFilter tokenBlacklistFilter;

    public SecurityConfig(RsaConfigProperties rsaConfigProperties, UserDetailsService userDetailsService, TokenBlacklistFilter tokenBlacklistFilter) {
        this.rsaConfigProperties = rsaConfigProperties;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistFilter = tokenBlacklistFilter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        int saltLength = 16; // 16 bytes
        int hashLength = 32; // 32 bytes
        int parallelism = 1; // Currently 1 thread
        int memoryCost = 1 << 12; // 4096 kibibytes (4 MB)
        int iterations = 3; // 3 iterations
        return new Argon2PasswordEncoder(
                saltLength,
                hashLength,
                parallelism,
                memoryCost,
                iterations
        );
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return new ProviderManager(authProvider);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaConfigProperties.publicKey()).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaConfigProperties.publicKey()).
                privateKey(rsaConfigProperties.privateKey()).build();

        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers(HttpMethod.GET, "api/v1/stores/{id}").permitAll()
                            .requestMatchers("/api/v1/auth/**",
                                    "/api/v1/products/**",
                                    "/api/v1/category/**",
                                    "/api/v1/stores/**",
                                    "/api/v1/inventory/**",
                                    "/api/v1/discounts/**",
                                    "/api/v1/location/**",
                                    "/api/v1/vouchers/**",
                                    "/api/v1/inventory-journals/**",
                                    "/api/v1/midtrans/**").permitAll()
                            .requestMatchers(
                                "/api/v1/stores", 
                                    "api/v1/orders/total-amounts-all-store",
                                    "api/v1/orders/total-amount-last-week",
                                    "api/v1/orders/total-amount-last-month",
                                    "/api/v1/inventory-journals/**",
                                    "api/v1/orders/total-amounts-storeid**",
                                    "/api/v1/stores/**"
                                    ).hasAuthority("SCOPE_super_admin")
                            .requestMatchers(
                                    "api/v1/orders/total-amounts-storeid**",
                                    "api/v1/category",
                                    "/api/v1/products/stores/**",
                                    "api/v1/products/not-in-inventory**",
                                    "api/v1/inventory/store/**",
                                    "api/v1/inventory/store/without-discount/**",
                                    "api/v1/discounts/store/**",
                                    "/api/v1/inventory-journals/**").hasAuthority("SCOPE_store_admin")
                            .anyRequest().authenticated();
                })
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder()))
                        .bearerTokenResolver(request -> {
                            if (isPublicEndpoint(request)) {
                                return null;
                            }

                            Cookie[] cookies = request.getCookies();
                            String authHeader = request.getHeader("Authorization");
                            if (cookies != null) {
                                for (Cookie cookie : cookies) {
                                    if ("sid".equals(cookie.getName())) {
                                        return cookie.getValue();
                                    }
                                }
                            } else if (authHeader != null && !authHeader.isEmpty()) {
                                return authHeader.replace("Bearer ", "");
                            }
                            return null;
                        })
                )
                .addFilterBefore(tokenBlacklistFilter, UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(userDetailsService)
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth/") ||
                path.startsWith("/api/v1/products/") ||
                path.startsWith("/api/v1/category/") ||
                path.startsWith("/api/v1/inventory/") ||
                path.startsWith("/api/v1/discounts/") ||
                path.startsWith("/api/v1/inventory-journals/") ||
                path.startsWith("/api/v1/vouchers/") ||
                path.startsWith("/api/v1/location/");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowedOriginPatterns(List.of("http://localhost:3000", "https://quick-basket-fe-754136654186.asia-southeast1.run.app", "https://frontend-repo-jrairw7l7-fiqra-wardanas-projects.vercel.app/", "https://frontend-repo-mu.vercel.app/"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT", "OPTIONS", "PATCH", "DELETE"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}