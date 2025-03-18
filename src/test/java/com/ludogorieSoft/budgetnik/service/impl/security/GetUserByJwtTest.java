package com.ludogorieSoft.budgetnik.service.impl.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ludogorieSoft.budgetnik.dto.response.AuthResponse;
import com.ludogorieSoft.budgetnik.dto.response.UserResponse;
import com.ludogorieSoft.budgetnik.model.Token;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.enums.TokenType;
import com.ludogorieSoft.budgetnik.service.JwtService;
import com.ludogorieSoft.budgetnik.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class GetUserByJwtTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private JwtService jwtService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private final String JWT_PREFIX = "Bearer ";
    private final String TEST_TOKEN = "mockedJwtToken";
    private final String DEVICE_ID = "mockedDevice";
    private User mockUser;
    private Token mockAccessToken;
    private Token mockRefreshToken;
    private UserResponse mockUserResponse;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setEmail("test@example.com");

        mockAccessToken = new Token();
        mockAccessToken.setUser(mockUser);
        mockAccessToken.setToken(TEST_TOKEN);
        mockAccessToken.setTokenType(TokenType.ACCESS);
        mockAccessToken.setExpired(false);
        mockAccessToken.setRevoked(false);

        mockRefreshToken = new Token();
        mockRefreshToken.setUser(mockUser);
        mockRefreshToken.setToken("mockedRefreshToken");
        mockRefreshToken.setTokenType(TokenType.REFRESH);
        mockRefreshToken.setExpired(false);
        mockRefreshToken.setRevoked(false);

        mockUserResponse = new UserResponse();
    }

    @Test
    void getUserByJwt_ShouldReturnAuthResponse_WhenAccessTokenIsValid() {
        when(tokenService.findByToken(TEST_TOKEN)).thenReturn(mockAccessToken);
        when(tokenService.isTokenValid(TEST_TOKEN, mockUser)).thenReturn(true);
        when(tokenService.getLastValidToken(mockUser, TokenType.REFRESH, DEVICE_ID)).thenReturn(mockRefreshToken);
        when(tokenService.isTokenValid(mockRefreshToken.getToken(), mockUser)).thenReturn(true);
        when(modelMapper.map(mockUser, UserResponse.class)).thenReturn(mockUserResponse);

        AuthResponse response = authService.getUserByJwt(TEST_TOKEN, DEVICE_ID);

        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.getToken());
        assertEquals(mockUserResponse, response.getUser());
    }

    @Test
    void getUserByJwt_ShouldGenerateNewAccessToken_WhenAccessTokenIsInvalid() {
        String newAccessToken = "newAccessToken";

        when(tokenService.findByToken(TEST_TOKEN)).thenReturn(mockAccessToken);
        when(tokenService.isTokenValid(TEST_TOKEN, mockUser)).thenReturn(false);
        when(jwtService.generateToken(mockUser)).thenReturn(newAccessToken);
        when(tokenService.getLastValidToken(mockUser, TokenType.REFRESH, DEVICE_ID)).thenReturn(mockRefreshToken);
        when(tokenService.isTokenValid(mockRefreshToken.getToken(), mockUser)).thenReturn(true);
        when(modelMapper.map(mockUser, UserResponse.class)).thenReturn(mockUserResponse);

        AuthResponse response = authService.getUserByJwt(TEST_TOKEN, DEVICE_ID);

        verify(tokenService, times(1)).setTokenAsExpiredAndRevoked(mockAccessToken);
        verify(tokenService, times(1)).saveToken(mockUser, newAccessToken, TokenType.ACCESS, DEVICE_ID);

        assertNotNull(response);
        assertEquals(newAccessToken, response.getToken());
        assertEquals(mockUserResponse, response.getUser());
    }

    @Test
    void getUserByJwt_ShouldGenerateNewRefreshToken_WhenRefreshTokenIsInvalid() {
        String newRefreshToken = "newRefreshToken";

        when(tokenService.findByToken(TEST_TOKEN)).thenReturn(mockAccessToken);
        when(tokenService.isTokenValid(TEST_TOKEN, mockUser)).thenReturn(true);
        when(tokenService.getLastValidToken(mockUser, TokenType.REFRESH, DEVICE_ID)).thenReturn(mockRefreshToken);
        when(tokenService.isTokenValid(mockRefreshToken.getToken(), mockUser)).thenReturn(false);
        when(jwtService.generateRefreshToken(mockUser)).thenReturn(newRefreshToken);
        when(modelMapper.map(mockUser, UserResponse.class)).thenReturn(mockUserResponse);

        AuthResponse response = authService.getUserByJwt(TEST_TOKEN, DEVICE_ID);

        verify(tokenService, times(1)).setTokenAsExpiredAndRevoked(mockRefreshToken);
        verify(tokenService, times(1)).saveToken(mockUser, newRefreshToken, TokenType.REFRESH, DEVICE_ID);

        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.getToken());
        assertEquals(mockUserResponse, response.getUser());
    }
}

