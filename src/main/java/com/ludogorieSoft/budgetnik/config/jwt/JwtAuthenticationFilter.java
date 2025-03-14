package com.ludogorieSoft.budgetnik.config.jwt;

import com.ludogorieSoft.budgetnik.dto.response.UserResponse;
import com.ludogorieSoft.budgetnik.exception.InvalidTokenException;
import com.ludogorieSoft.budgetnik.model.Token;
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
  public static final String PUSH_TOKEN_PATH = "/api/users/exponentPushToken";

  private final JwtService jwtService;
  private final UserService userService;
  private final ModelMapper modelMapper;
  private final TokenRepository tokenRepository;
  private final TokenService tokenService;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    return path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui") || path.startsWith(PUSH_TOKEN_PATH);
  }

  @Override
  protected void doFilterInternal(
          @NonNull HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain)
          throws ServletException, IOException {
    if (request.getServletPath().contains(AUTH_PATH)) {
      filterChain.doFilter(request, response);
      return;
    }

    request.setAttribute(USER_KEY, null);

    final String authHeader = request.getHeader(JWT_HEADER);

    if (authHeader == null || !authHeader.startsWith(JWT_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }

    final String jwt = authHeader.substring(7);

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

     boolean isValid = tokenService.isTokenValid(jwt, userDetails);

      if (isValid && isTokenValid) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
      }

      request.setAttribute(USER_KEY, modelMapper.map(userDetails, UserResponse.class));
    }

    filterChain.doFilter(request, response);
  }
}