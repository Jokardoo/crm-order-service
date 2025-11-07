package com.jokardo.crm.order_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.warn("Authentication required for: {}", request.getRequestURI());

        if (isApiRequest(request)) {
            handleApiAuthenticationRequired(response, authException);
        } else {
            handleWebAuthenticationRequired(request, response, authException);
        }
    }

    private void handleApiAuthenticationRequired(HttpServletResponse response,
                                                 AuthenticationException exception) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", "Authentication required");
        errorResponse.put("path", "API endpoint");

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), errorResponse);
    }

    private void handleWebAuthenticationRequired(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 AuthenticationException exception) throws IOException {
        // Сохраняем запрошенный URL для редиректа после логина
        String redirectUrl = request.getRequestURI();
        if (request.getQueryString() != null) {
            redirectUrl += "?" + request.getQueryString();
        }

        request.getSession().setAttribute("REDIRECT_URL", redirectUrl);
        response.sendRedirect(request.getContextPath() + "/login");
    }

    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/");
    }
}
