package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.dto.request.RegisterRequest;
import com.ludogorieSoft.budgetnik.dto.response.UserResponse;
import com.ludogorieSoft.budgetnik.exception.AccessDeniedException;
import com.ludogorieSoft.budgetnik.exception.PasswordException;
import com.ludogorieSoft.budgetnik.exception.UserExistsException;
import com.ludogorieSoft.budgetnik.exception.UserNotFoundException;
import com.ludogorieSoft.budgetnik.model.ExpoPushToken;
import com.ludogorieSoft.budgetnik.model.Subscription;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.VerificationToken;
import com.ludogorieSoft.budgetnik.model.enums.Role;
import com.ludogorieSoft.budgetnik.repository.ExponentPushTokenRepository;
import com.ludogorieSoft.budgetnik.repository.SubscriptionRepository;
import com.ludogorieSoft.budgetnik.repository.TokenRepository;
import com.ludogorieSoft.budgetnik.repository.UserRepository;
import com.ludogorieSoft.budgetnik.repository.VerificationTokenRepository;
import com.ludogorieSoft.budgetnik.service.UserService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final VerificationTokenRepository verificationTokenRepository;
  private final MessageSource messageSource;
  private final ExponentPushTokenRepository exponentPushTokenRepository;
  private final ModelMapper modelMapper;
  private final SubscriptionRepository subscriptionRepository;
  private final TokenRepository tokenRepository;

  @Override
  public User createUser(RegisterRequest registerRequest) {

    if (userRepository.existsByEmail(registerRequest.getEmail())) {
      throw new UserExistsException(messageSource);
    }

    Role role = Role.USER;
    if (userRepository.count() == 0) {
      role = Role.ADMIN;
    }

    String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

    if (!passwordEncoder.matches(registerRequest.getConfirmPassword(), encodedPassword)) {
      throw new PasswordException(messageSource);
    }

    User user =
        User.builder()
            .createdAt(LocalDateTime.now())
            .name(registerRequest.getName())
            .email(registerRequest.getEmail())
            .password(encodedPassword)
            .role(role)
            .activated(false)
            .build();

    User createdUser = userRepository.save(user);
    logger.info("Created user with id " + createdUser.getId());

    return createdUser;
  }

  @Override
  public User findByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException(messageSource));
  }

  @Override
  public User findById(UUID id) {
    return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(messageSource));
  }

  public Page<UserResponse> getAllUsersPaginated(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);

    Page<User> userPage = userRepository.findAll(pageable);

    return userPage.map(user -> modelMapper.map(user, UserResponse.class));
  }

  @Override
  public VerificationToken getVerificationToken(String verificationToken) {
    return verificationTokenRepository.findByToken(verificationToken);
  }

  @Override
  @Transactional
  public void deleteUserSubscription(User user) {
    Subscription subscription = user.getSubscription();
    if (subscription != null) {
      user.setSubscription(null);
      userRepository.save(user);
      subscriptionRepository.delete(subscription);
    }
  }

  @Override
  public void createVerificationToken(User user, String token) {
    cleanUserVerificationTokens(user);
    VerificationToken verificationToken = new VerificationToken(token, user);
    verificationTokenRepository.save(verificationToken);
  }

  @Override
  public void deleteUserById(UUID id, UserResponse currentUser) {
    User user = findById(id);

    if (user.getId().equals(currentUser.getId())) {
      throw new AccessDeniedException(messageSource);
    }
    logger.info("Deleted user with id " + user.getId());
    userRepository.delete(user);
  }

  @Override
  public void saveExponentPushToken(UUID id, String token) {
    User user = findById(id);

    exponentPushTokenRepository
        .findByTokenAndUser(token, user)
        .ifPresent(exponentPushTokenRepository::delete);

    ExpoPushToken expoPushToken = new ExpoPushToken();
    expoPushToken.setUser(user);
    expoPushToken.setToken(token);
    exponentPushTokenRepository.saveAndFlush(expoPushToken);
    logger.info("Expo push token updated!");
  }

  @Override
  public void deleteExponentPushToken(String token) {
    exponentPushTokenRepository.findByToken(token).ifPresent(exponentPushTokenRepository::delete);
  }

  @Override
  public void saveUser(User user) {
    userRepository.save(user);
  }

  @Override
  public User findByCustomerId(String customerId) {
    User user = userRepository.findByCustomerId(customerId);
    if (user == null) {
      throw new UserNotFoundException(messageSource);
    }
    return user;
  }

  private void cleanUserVerificationTokens(User user) {
    List<VerificationToken> userTokens = verificationTokenRepository.findByUserId(user.getId());
    userTokens.forEach(verificationTokenRepository::delete);
  }

  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.isAuthenticated()) {
      Object principal = authentication.getPrincipal();

      if (principal instanceof User) {
        return (User) principal;
      } else if (principal instanceof UserDetails) {
        UserDetails userDetails = (UserDetails) principal;
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
      }
    }
    return null;
  }
}
