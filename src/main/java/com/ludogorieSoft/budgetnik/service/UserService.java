package com.ludogorieSoft.budgetnik.service;

import com.ludogorieSoft.budgetnik.dto.request.RegisterRequest;
import com.ludogorieSoft.budgetnik.dto.response.UserResponse;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.VerificationToken;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface UserService {
  User createUser(RegisterRequest registerRequest);
  User findByEmail(String email);
  User findById(UUID id);
  List<UserResponse> getAllUsersPaginated(int page, int size);
  void createVerificationToken(User user, String token);
  void deleteUserById(UUID id, UserResponse currentUser);
  void updateExponentPushToken(UUID id, String token);
  void deleteExponentPushToken(String token);

  VerificationToken getVerificationToken(String VerificationToken);
}
