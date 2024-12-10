package com.hari.features.authentication.filter;

import com.hari.features.authentication.model.AuthUser;
import com.hari.features.authentication.service.AuthService;
import com.hari.features.authentication.utils.JsonWebToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthFilter extends HttpFilter {
    private final List<String> unsecuredEndpoints = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/send-password-reset-token",
            "/api/auth/reset-password"
    );
    private final JsonWebToken jsonWebToken;
    private final AuthService authService;

    public AuthFilter(JsonWebToken jsonWebToken, AuthService authService) {
        this.jsonWebToken = jsonWebToken;
        this.authService = authService;
    }
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        String path = request.getRequestURI();
        if (unsecuredEndpoints.contains(path)) {
            chain.doFilter(request, response);
            return;
        }
        try{
            String auth=request.getHeader("Authorization");
            if (auth==null||!auth.startsWith("Bearer ")){
                throw new ServletException("Invalid authentication token, or token missing.");
            }
            String token=auth.substring(7);
            if (jsonWebToken.isTokenExpired(token)){
                throw new ServletException("Token invalid");
            }
            String email = jsonWebToken.getEmailFromToken(token);
            AuthUser user = authService.getUser(email);
            request.setAttribute("authenticatedUser", user);
            chain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Invalid authentication token, or token missing.\"}");
        }
    }
}
