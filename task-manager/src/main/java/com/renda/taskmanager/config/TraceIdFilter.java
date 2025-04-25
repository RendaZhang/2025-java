package com.renda.taskmanager.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter extends OncePerRequestFilter {

    private static final String KEY = "traceId";
    private static final String HEADER = "X-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws IOException, ServletException {

        try {
            String id = req.getHeader(HEADER);
            if (id == null || id.isBlank()) {
                id = UUID.randomUUID().toString().replace("-", "");
            }
            MDC.put(KEY, id);
            // 让客户端也拿得到
            res.setHeader(HEADER, id);
            chain.doFilter(req, res);
        } finally {
            MDC.clear();
        }
    }
}
