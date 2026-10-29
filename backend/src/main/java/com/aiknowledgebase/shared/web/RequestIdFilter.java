package com.aiknowledgebase.shared.web;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {
    public static final String HEADER = "X-Request-ID";
    private static final String ATTRIBUTE = RequestIdFilter.class.getName() + ".requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        response.setHeader(HEADER, requestId(request).toString());
        filterChain.doFilter(request, response);
    }

    public static UUID requestId(HttpServletRequest request) {
        if (request.getAttribute(ATTRIBUTE) instanceof UUID requestId) {
            return requestId;
        }
        UUID requestId = UUID.randomUUID();
        request.setAttribute(ATTRIBUTE, requestId);
        return requestId;
    }
}
