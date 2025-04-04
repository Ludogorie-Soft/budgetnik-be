package com.ludogorieSoft.budgetnik.controller;

import com.ludogorieSoft.budgetnik.dto.response.TransactionCountResponseDto;
import com.ludogorieSoft.budgetnik.dto.response.UserResponse;
import com.ludogorieSoft.budgetnik.service.AdminService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

  private final AdminService adminService;

  @GetMapping("/users/count")
  public ResponseEntity<Long> getCountOfUsers() {
    Long response = adminService.getCountOfRegisteredUsers();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/users/search")
  public ResponseEntity<Page<UserResponse>> searchUsers(
      @RequestParam("q") String q,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    Page<UserResponse> response = adminService.searchUsers(q, page, size);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/users/logged")
  public ResponseEntity<List<UserResponse>> getLastTenLoggedInUsers() {
    List<UserResponse> response = adminService.getLastTenLoggedInUsers();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/users/registered")
  public ResponseEntity<List<UserResponse>> getLastTenRegisteredUsers() {
    List<UserResponse> response = adminService.getLastTenRegisteredUsers();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/incomes-count")
  public ResponseEntity<TransactionCountResponseDto> getCountOfEnteredIncomes() {
    TransactionCountResponseDto response = adminService.getCountOfEnteredIncomes();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/expenses-count")
  public ResponseEntity<TransactionCountResponseDto> getCountOfEnteredExpenses() {
    TransactionCountResponseDto response = adminService.getCountOfEnteredExpenses();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
