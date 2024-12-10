package com.ludogorieSoft.budgetnik.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ludogorieSoft.budgetnik.config.jwt.JwtAuthenticationEntryPoint;
import com.ludogorieSoft.budgetnik.config.jwt.JwtAuthenticationFilter;
import com.ludogorieSoft.budgetnik.model.enums.Role;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

  private final ObjectMapper objectMapper;
  private final AuthenticationProvider authenticationProvider;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final LogoutHandler logoutHandler;

  @Value("${cors.allowedOrigins}")
  private String[] allowedOrigins;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/auth/**")
                    .permitAll()
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                    .permitAll()
                    .requestMatchers("/api/admin/**")
                    .hasAnyRole(Role.ADMIN.name())
                    .anyRequest()
                    .authenticated())
        .cors(cors -> cors.configurationSource(configurationSource()))
        .exceptionHandling(
            httpSecurityExceptionHandlingConfigurer ->
                httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(
                    new JwtAuthenticationEntryPoint(objectMapper)))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .logout(
            httpSecurityLogoutConfigurer -> {
              httpSecurityLogoutConfigurer.logoutUrl("/api/auth/logout");
              httpSecurityLogoutConfigurer.addLogoutHandler(logoutHandler);
              httpSecurityLogoutConfigurer.logoutSuccessHandler(
                  (request, response, authentication) -> SecurityContextHolder.clearContext());
            });

    return http.build();
  }

  private CorsConfigurationSource configurationSource() {
    return request -> {
      CorsConfiguration corsConfiguration = new CorsConfiguration();
      corsConfiguration.setAllowedOrigins(List.of(allowedOrigins));
      corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
      corsConfiguration.setAllowCredentials(true);
      corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
      corsConfiguration.setMaxAge(3600L);
      return corsConfiguration;
    };
  }
}
