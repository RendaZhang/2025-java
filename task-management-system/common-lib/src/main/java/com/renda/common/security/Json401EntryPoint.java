package com.renda.common.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class Json401EntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        /* Without the WWW-Authenticate header, the browser won't display a popup */
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        /* A simple JSON response; you can return a unified error structure as needed */
        response.getWriter().write("""
                {"status":401,"message":"Unauthorized"}
                """);
    }

}
