package com.grocery.quickbasket.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.Cookie;
import lombok.extern.java.Log;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Log
public class SecurityConfig {
    private final RsaConfigProperties rsaConfigProperties;
    private final UserDetailsService userDetailsService;
    private final CorsConfigurationSourceImpl corsConfigurationSource;

    public SecurityConfig(RsaConfigProperties rsaConfigProperties, UserDetailsService userDetailsService, CorsConfigurationSourceImpl corsConfigurationSource) {
        this.rsaConfigProperties = rsaConfigProperties;
        this.userDetailsService = userDetailsService;
        this.corsConfigurationSource = corsConfigurationSource;
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
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/v1/auth/**").permitAll();
                    auth.requestMatchers("/api/v1/products/**").permitAll();
                    auth.requestMatchers("/api/v1/products/stores/**").permitAll();
                    auth.requestMatchers("/api/v1/category/**").permitAll();
                    auth.requestMatchers("/api/v1/stores").hasAuthority("SCOPE_super_admin");
                    auth.requestMatchers("/api/v1/stores/**").hasAuthority("SCOPE_super_admin");
                    auth.requestMatchers("/api/v1/inventory/**").permitAll();
                    auth.requestMatchers("/api/v1/discounts/**").permitAll();
                    auth.requestMatchers("/api/v1/inventory-journals/**").permitAll();
                    auth.requestMatchers("/api/v1/vouchers/**").permitAll();
                    /*
                    Kalau mau tambahin Role Based access
                    example:
                    auth.requestMatchers(HttpMethod.GET,"api/v1/auth").hasAuthority("SCOPE_store_admin");

                    SCOPE_ JANGAN LUPA DIDEPAN
                    ITU BISA DIKASI SPESIFIK METHOD HTTPNYA, kalau yg store admin khusus get aja. super admin kasi post
                     */
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer((oauth2) -> {
                    oauth2.jwt((jwt) -> jwt.decoder(jwtDecoder()));
                    oauth2.bearerTokenResolver((request) -> {
                        Cookie[] cookies = request.getCookies();
                        var authHeader = request.getHeader("Authorization");
                        if (cookies != null) {
                            for (Cookie cookie : cookies) {
                                if ("sid".equals(cookie.getName())) {
                                    return cookie.getValue();
                                }
                            }
                        } else if (authHeader!= null && !authHeader.isEmpty()) {
                            return authHeader.replace("Bearer ", "");
                        }
                        return null;
                    });
                })
                .userDetailsService(userDetailsService)
                .httpBasic(Customizer.withDefaults())
                .build();

    }

}
