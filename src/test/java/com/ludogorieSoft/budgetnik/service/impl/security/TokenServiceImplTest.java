package com.ludogorieSoft.budgetnik.service.impl.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ludogorieSoft.budgetnik.dto.response.AuthResponse;
import com.ludogorieSoft.budgetnik.dto.response.UserResponse;
import com.ludogorieSoft.budgetnik.model.Token;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.enums.TokenType;
import com.ludogorieSoft.budgetnik.repository.TokenRepository;
import com.ludogorieSoft.budgetnik.service.JwtService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {
  @Mock private TokenRepository tokenRepository;

  @InjectMocks private TokenServiceImpl tokenService;
  @Mock private JwtService jwtService;

  @Mock private ModelMapper modelMapper;

  private static final String DEVICE_ID = "Device_id";

  @Test
  void testSaveToken() {
    User mockUser = new User();
    String jwtToken = "mockJwtToken";
    TokenType tokenType = TokenType.ACCESS;

    tokenService.saveToken(mockUser, jwtToken, tokenType, DEVICE_ID);

    verify(tokenRepository)
        .save(
            Mockito.argThat(
                token ->
                    token.getUser() == mockUser
                        && token.getToken().equals(jwtToken)
                        && token.getTokenType() == tokenType
                        && !token.isExpired()
                        && !token.isRevoked()));
  }

  @Test
  void testFindByToken() {
    String jwt = "mockJwt";
    Token mockToken = new Token();
    when(tokenRepository.findByToken(jwt)).thenReturn(java.util.Optional.of(mockToken));
    Token result = tokenService.findByToken(jwt);
    assertEquals(mockToken, result);
  }

  @Test
  void testFindByUser() {
    User mockUser = new User();
    Token mockToken1 = new Token();
    Token mockToken2 = new Token();

    when(tokenRepository.findAllByUser(mockUser)).thenReturn(List.of(mockToken1, mockToken2));
    List<Token> result = tokenService.findByUser(mockUser);
    assertEquals(2, result.size());
    assertEquals(mockToken1, result.get(0));
    assertEquals(mockToken2, result.get(1));
  }

  @Test
  void testLogoutToken() {
    String jwtToken = "mockedJwtToken";
    User user = new User();
    Token storedToken = new Token();
    storedToken.setUser(user);

    Token refreshToken = new Token();
    refreshToken.setUser(user);
    refreshToken.setExpired(false);
    refreshToken.setRevoked(false);

    when(tokenRepository.findByToken(jwtToken)).thenReturn(Optional.of(storedToken));
    when(tokenRepository.findByUserAndTokenTypeAndDeviceAndExpiredFalseAndRevokedFalse(
            any(User.class), eq(TokenType.REFRESH), anyString())).thenReturn(List.of(refreshToken));

    tokenService.logoutToken(jwtToken, DEVICE_ID);

    verify(tokenRepository, times(1)).findByToken(jwtToken);
  }

  @Test
  void testLogoutToken_NoTokenFound() {
    String jwtToken = "nonExistentJwtToken";

    when(tokenRepository.findByToken(jwtToken)).thenReturn(Optional.empty());

    tokenService.logoutToken(jwtToken, DEVICE_ID);

    verify(tokenRepository, never()).findAllByUser(any(User.class));
    verify(tokenRepository, never()).deleteAll(anyList());
  }

  @Test
  void testGenerateAuthResponse() {
    // GIVEN
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("testUser@test.com");
    UserResponse mockUserResponse = new UserResponse();
    mockUserResponse.setId(user.getId());
    mockUserResponse.setEmail("testUser@test.com");
    String mockJwtToken = "mock-jwt-token";
    when(jwtService.generateToken(user)).thenReturn(mockJwtToken);
    when(modelMapper.map(user, UserResponse.class)).thenReturn(mockUserResponse);

    // WHEN
    AuthResponse response = tokenService.generateAuthResponse(user, DEVICE_ID);

    // THEN
    assertNotNull(response);
    assertEquals(mockJwtToken, response.getToken());
    assertEquals(mockUserResponse, response.getUser());
    verify(jwtService, times(1)).generateToken(user);
    verify(modelMapper, times(1)).map(user, UserResponse.class);
  }
}
