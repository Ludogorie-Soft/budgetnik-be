package com.ludogorieSoft.budgetnik.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LocaleInterceptor implements HandlerInterceptor {
  public boolean preHandle(HttpServletRequest request) {
    Locale locale = request.getLocale();
    LocaleContextHolder.setLocale(locale);
    return true;
  }
}
