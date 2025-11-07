package com.jokardo.crm.order_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.warn("Access denied for user: {}, URL: {}, IP: {}",
                getCurrentUsername(),
                request.getRequestURI(),
                getClientIpAddress(request));

        if (isApiRequest(request)) {
            handleApiAccessDenied(response, accessDeniedException);
        } else {
            handleWebAccessDenied(request, response, accessDeniedException);
        }

    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        return "anonymous";
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/") ||
                "application/json".equals(request.getHeader("Accept")) ||
                "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    private void handleWebAccessDenied(HttpServletRequest request,
                                       HttpServletResponse response,
                                       AccessDeniedException exception) throws IOException {

        // Сохраняем сообщение об ошибке в сессии для отображения на странице
        request.getSession().setAttribute("errorMessage", "Access Denied: " + exception.getMessage());

        // Перенаправляем на кастомную страницу ошибки
        response.sendRedirect(request.getContextPath() + "/access-denied");
    }

    private void handleApiAccessDenied(HttpServletResponse response,
                                       AccessDeniedException exception) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.FORBIDDEN.value());
        errorResponse.put("error", "Forbidden");
        errorResponse.put("message", "You don't have permission to access this resource");
        errorResponse.put("path", "API endpoint");

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), errorResponse);
    }


}
