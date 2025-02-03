package com.ludogorieSoft.budgetnik.service;

import com.ludogorieSoft.budgetnik.dto.response.AuthResponse;
import com.ludogorieSoft.budgetnik.model.Token;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.enums.TokenType;
import java.util.List;

public interface TokenService {
    AuthResponse generateAuthResponse(User user);
    Token findByToken(String jwt);
    List<Token> findByUser(User user);
    void saveToken(User user, String jwtToken, TokenType tokenType);
//    void revokeToken(Token token);
//    void revokeAllUserTokens(User user);
    void logoutToken(String jwt);
}
