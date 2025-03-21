package com.ludogorieSoft.budgetnik.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ludogorieSoft.budgetnik.exception.AccessDeniedException;
import com.ludogorieSoft.budgetnik.helper.ObjectMapperHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper, MessageSource messageSource) {
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;
    }

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException {

        ObjectMapperHelper
                .writeExceptionToObjectMapper(
                        objectMapper,
                        new AccessDeniedException(messageSource),
                        httpServletResponse
                );
    }
}
