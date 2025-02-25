package com.ludogorieSoft.budgetnik.config.jwt;

import com.ludogorieSoft.budgetnik.dto.response.UserResponse;
import com.ludogorieSoft.budgetnik.exception.InvalidTokenException;
import com.ludogorieSoft.budgetnik.model.Token;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.enums.TokenType;
import com.ludogorieSoft.budgetnik.repository.TokenRepository;
import com.ludogorieSoft.budgetnik.service.JwtService;
import com.ludogorieSoft.budgetnik.service.TokenService;
import com.ludogorieSoft.budgetnik.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  public static final String JWT_HEADER = "Authorization";
  public static final String JWT_PREFIX = "Bearer ";
  public static final String USER_KEY = "user";
  public static final String AUTH_PATH = "/api/auth";

  private final JwtService jwtService;
  private final UserService userService;
  private final ModelMapper modelMapper;
  private final TokenRepository tokenRepository;
  private final TokenService tokenService;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    return path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui");
  }

  @Override
  protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain)
          throws ServletException, IOException {

    // Изключваме path за авторизация, защото този филтър не трябва да се прилага на тези ендпойнти
    if (request.getServletPath().contains(AUTH_PATH)) {
      filterChain.doFilter(request, response);
      return;
    }

    request.setAttribute(USER_KEY, null);

    final String authHeader = request.getHeader(JWT_HEADER);

    if (authHeader == null || !authHeader.startsWith(JWT_PREFIX)) {
      filterChain.doFilter(request, response);
      return; // Няма Authorization header
    }

    final String jwt = authHeader.substring(7);  // Отстраняваме "Bearer " частта

    final String userEmail = jwtService.extractUsername(jwt);

    if (jwt.isEmpty()) {
      filterChain.doFilter(request, response);
      return;
    }

    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = userService.findByEmail(userEmail);

      boolean isTokenValid =
              tokenRepository
                      .findByToken(jwt)
                      .map(token -> !token.isExpired() && !token.isRevoked())
                      .orElse(false);

      if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
        // Ако токенът е валиден, поставяме го в SecurityContext
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
      } else {
        // Ако access токенът е невалиден, проверяваме refresh токен
        String refreshToken = request.getHeader("Refresh-Token");

        if (refreshToken != null && jwtService.isTokenValid(refreshToken, userDetails)) {
          // Генерираме нов access токен
          String newAccessToken = jwtService.generateToken(userDetails);
          String newRefreshToken = jwtService.generateRefreshToken(userDetails);

          User user = userService.findByEmail(userDetails.getUsername());

          // Записваме новите токени в базата
          tokenService.saveToken(user, newAccessToken, TokenType.ACCESS);
          tokenService.saveToken(user, newRefreshToken, TokenType.REFRESH);

          // Връщаме новите токени в response хедъри
          response.setHeader("Authorization", "Bearer " + newAccessToken);
          response.setHeader("Refresh-Token", newRefreshToken);

          // Обновяваме SecurityContext с новия access token
          UsernamePasswordAuthenticationToken newAuthToken =
                  new UsernamePasswordAuthenticationToken(
                          userDetails, null, userDetails.getAuthorities());

          newAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(newAuthToken);
        } else {
          throw new InvalidTokenException();  // Ако refresh токенът е невалиден
        }
      }

      request.setAttribute(USER_KEY, modelMapper.map(userDetails, UserResponse.class));
    }

    filterChain.doFilter(request, response);
  }
}
