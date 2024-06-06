package com.database.federation.configurations;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.database.federation.utils.JwtObject;
import com.database.federation.utils.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.getMethodAnnotation(Protected.class) != null) {
                // Check authentication and authorization logic here
                // If not authenticated, you can send a 401 Unauthorized response
                // Otherwise, allow access
                if (!isAuthenticated(request)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isAuthenticated(HttpServletRequest request) {
        // Implement your JWT token validation logic here
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("Token: " + token);
            // Validate the token and extract user info
            // Return true if valid, false otherwise
            return validateToken(token, request);
        }
        return false;
    }

    private boolean validateToken(String token, HttpServletRequest request) {
        try {
            JwtUtils jwtUtils = new JwtUtils();
            JwtObject tokenData = jwtUtils.parseToken(token, JwtObject.class);
            request.setAttribute("userAfm", tokenData.getAfm());
            return true;
        } catch (Exception e) {
            System.out.println("Invalid token: " + e.getMessage());
            return false;
        }
    }
}
