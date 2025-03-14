package com.ludogorieSoft.budgetnik.exception;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ludogorieSoft.budgetnik.helper.ObjectMapperHelper;
import com.ludogorieSoft.budgetnik.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutHandler implements org.springframework.security.web.authentication.logout.LogoutHandler {

    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";

    private final TokenService tokenService;
    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader(JWT_HEADER);
        final String deviceHeader = request.getHeader("DeviceId");
        if (authHeader == null || !authHeader.startsWith(JWT_PREFIX)) {
            throw new InvalidTokenException(messageSource);
        }

        final String jwt = authHeader.substring(7);


        if (jwt == null || jwt.isEmpty()) {
            try {
                ObjectMapperHelper.writeExceptionToObjectMapper(objectMapper, new InvalidTokenException(messageSource), response);
                return;
            } catch (IOException exception) {
                return;
            }
        }

        tokenService.logoutToken(jwt, deviceHeader);
    }
}
