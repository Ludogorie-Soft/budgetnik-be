package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.dto.request.RegisterRequest;
import com.ludogorieSoft.budgetnik.dto.response.UserResponse;
import com.ludogorieSoft.budgetnik.exception.AccessDeniedException;
import com.ludogorieSoft.budgetnik.exception.PasswordException;
import com.ludogorieSoft.budgetnik.exception.UserExistsException;
import com.ludogorieSoft.budgetnik.exception.UserNotFoundException;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.VerificationToken;
import com.ludogorieSoft.budgetnik.model.enums.Role;
import com.ludogorieSoft.budgetnik.repository.UserRepository;
import com.ludogorieSoft.budgetnik.repository.VerificationTokenRepository;
import com.ludogorieSoft.budgetnik.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final VerificationTokenRepository verificationTokenRepository;

  @Override
  public User createUser(RegisterRequest registerRequest) {

    if (userRepository.existsByEmail(registerRequest.getEmail())) {
      throw new UserExistsException("Вече съществува потребител с такъв имейл!");
    }

    Role role = Role.USER;
    if (userRepository.count() == 0) {
      role = Role.ADMIN;
    }

    String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

    if (!passwordEncoder.matches(registerRequest.getConfirmPassword(), encodedPassword)) {
      throw new PasswordException("Паролата не съвпада!");
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

    return userRepository.save(user);
  }

  @Override
  public User findByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
  }

  @Override
  public User findById(UUID id) {
    return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
  }

  @Override
  public VerificationToken getVerificationToken(String verificationToken) {
    return verificationTokenRepository.findByToken(verificationToken);
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
      throw new AccessDeniedException();
    }

    userRepository.delete(user);
  }

  private void cleanUserVerificationTokens(User user) {
    List<VerificationToken> userTokens = verificationTokenRepository.findByUserId(user.getId());
    userTokens.forEach(verificationTokenRepository::delete);
  }
}
