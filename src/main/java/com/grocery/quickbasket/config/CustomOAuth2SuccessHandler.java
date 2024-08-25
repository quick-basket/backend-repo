package com.grocery.quickbasket.config;

import com.grocery.quickbasket.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;

    public CustomOAuth2SuccessHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, IOException {
        // Generate JWT using the AuthService
        String token = authService.generateToken(authentication);

        // Set the JWT in the response header
        response.setHeader("Authorization", "Bearer " + token);

        // Optionally, redirect to a frontend URL with the token as a query parameter
        String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:3002/")  // Frontend URL
                .queryParam("token", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
