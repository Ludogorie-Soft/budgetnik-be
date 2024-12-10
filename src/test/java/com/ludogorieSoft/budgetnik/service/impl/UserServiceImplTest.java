package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.dto.request.RegisterRequest;
import com.ludogorieSoft.budgetnik.dto.response.UserResponse;
import com.ludogorieSoft.budgetnik.exception.AccessDeniedException;
import com.ludogorieSoft.budgetnik.exception.PasswordException;
import com.ludogorieSoft.budgetnik.exception.UserExistsException;
import com.ludogorieSoft.budgetnik.exception.UserNotFoundException;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.VerificationToken;
import com.ludogorieSoft.budgetnik.repository.UserRepository;
import com.ludogorieSoft.budgetnik.repository.VerificationTokenRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private VerificationTokenRepository verificationTokenRepository;

  @InjectMocks private UserServiceImpl userService;

  @Test
  void testCreateUser() {
    RegisterRequest request = new RegisterRequest();
    User user = new User();

    String encodedPassword = passwordEncoder.encode(request.getPassword());
    when(passwordEncoder.matches(request.getConfirmPassword(), encodedPassword)).thenReturn(true);
    when(userRepository.save(any(User.class))).thenReturn(user);

    User createdUser = userService.createUser(request);

    verify(userRepository, times(1)).save(any(User.class));
    Assertions.assertEquals(user, createdUser);
  }

  @Test
  void testFindByEmail() {
    String userEmail = "user@example.com";
    User user = new User();

    when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

    User foundUser = userService.findByEmail(userEmail);

    verify(userRepository, times(1)).findByEmail(userEmail);
    Assertions.assertEquals(user, foundUser);
  }

  @Test
  void testFindByEmail_UserNotFound() {
    String userEmail = "nonexistent@example.com";

    when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.findByEmail(userEmail));

    verify(userRepository, times(1)).findByEmail(userEmail);
  }
  @Test
  void testCreateUser_DataIntegrityViolationException() {
    RegisterRequest request = new RegisterRequest();

    String encodedPassword = passwordEncoder.encode(request.getPassword());
    when(passwordEncoder.matches(request.getConfirmPassword(), encodedPassword)).thenReturn(true);
    when(userRepository.save(any(User.class))).thenThrow(new UserExistsException("User already exists!"));

    assertThrows(UserExistsException.class, () -> userService.createUser(request));

    verify(userRepository).save(any(User.class));
  }

  @Test
  void testCreateUser_PasswordException() {
    RegisterRequest request = new RegisterRequest();

    assertThrows(PasswordException.class, () -> userService.createUser(request));
  }

  @Test
  void testCreateUser_ConstraintViolationException() {
    RegisterRequest request = new RegisterRequest();

    Set constraintViolations = Collections.singleton(mock(ConstraintViolation.class));
    ConstraintViolationException exception = new ConstraintViolationException("Constraint violation", constraintViolations);
    String encodedPassword = passwordEncoder.encode(request.getPassword());
    when(passwordEncoder.matches(request.getConfirmPassword(), encodedPassword)).thenReturn(true);
    when(userRepository.save(any(User.class))).thenThrow(exception);

    assertThrows(ConstraintViolationException.class, () -> userService.createUser(request));

    verify(userRepository).save(any(User.class));
  }

  @Test
  void testDeleteUserById_AccessDeniedException() {
    UUID userId = UUID.randomUUID();
    UserResponse currentUser = new UserResponse();
    currentUser.setId(UUID.randomUUID());
    User user = new User();
    user.setId(currentUser.getId());
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    assertThrows(AccessDeniedException.class, () -> userService.deleteUserById(userId, currentUser));
    verify(userRepository).findById(userId);
  }

  @Test
  void deleteUserById_Success() {
    UUID userId = UUID.randomUUID();
    UserResponse currentUser = new UserResponse();
    currentUser.setId(UUID.randomUUID());

    User mockUser = new User();
    mockUser.setId(userId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    userService.deleteUserById(userId, currentUser);

    verify(userRepository, times(1)).findById(userId);
    verify(userRepository, times(1)).delete(mockUser);
  }

  @Test
  void testCreateVerificationToken_Success() {
    User user = new User();
    VerificationToken verificationToken = new VerificationToken();

    when(verificationTokenRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);

    userService.createVerificationToken(user, String.valueOf(verificationToken));

    verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
  }

  @Test
  void testFindVerificationToken_Success() {
    VerificationToken verificationToken = new VerificationToken();

    when(verificationTokenRepository.findByToken(any())).thenReturn(verificationToken);

    VerificationToken actual = userService.getVerificationToken(String.valueOf(verificationToken));

    verify(verificationTokenRepository, times(1)).findByToken(String.valueOf(verificationToken));
    assertEquals(verificationToken, actual);
  }

  @Test
  void testCreateUser_UserExistsException() {
    RegisterRequest request = new RegisterRequest();
    when(userRepository.existsByEmail(any())).thenReturn(true);
    assertThrows(UserExistsException.class, () -> userService.createUser(request));
  }
}

