package com.ludogorieSoft.budgetnik.controller;

import com.ludogorieSoft.budgetnik.dto.request.ExpenseRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.ExpenseResponseDto;
import com.ludogorieSoft.budgetnik.model.enums.Type;
import com.ludogorieSoft.budgetnik.service.ExpenseService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpensesController {

  private final ExpenseService expenseService;

  @PostMapping
  public ResponseEntity<ExpenseResponseDto> createExpense(
      @RequestBody ExpenseRequestDto expenseRequestDto) {
    ExpenseResponseDto response = expenseService.createExpense(expenseRequestDto);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<ExpenseResponseDto> getExpense(@RequestParam("id") UUID id) {
    ExpenseResponseDto response = expenseService.getExpense(id);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PutMapping
  public ResponseEntity<ExpenseResponseDto> editExpense(
      @RequestParam("id") UUID id, @RequestBody ExpenseRequestDto expenseRequestDto) {
    ExpenseResponseDto response = expenseService.editExpense(id, expenseRequestDto);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/users")
  public ResponseEntity<List<ExpenseResponseDto>> getAllExpensesOfUser(
      @RequestParam("id") UUID id) {
    List<ExpenseResponseDto> response = expenseService.getAllExpensesOfUser(id);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @DeleteMapping
  public ResponseEntity<ExpenseResponseDto> deleteExpense(@RequestParam("id") UUID id) {
    ExpenseResponseDto response = expenseService.deleteExpense(id);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/users/sum")
  public ResponseEntity<BigDecimal> getTheSumOfAllExpensesOfUser(@RequestParam("id") UUID id) {
    BigDecimal sum = expenseService.calculateSumOfAllExpensesOfUser(id);
    return new ResponseEntity<>(sum, HttpStatus.OK);
  }

  @GetMapping("/users/category/sum")
  public ResponseEntity<BigDecimal> getTheSumOfAllExpensesOfUserByCategory(
      @RequestParam("id") UUID id, @RequestParam("category") String category) {
    BigDecimal sum = expenseService.calculateSumOfAllExpensesOfUserByCategory(id, category);
    return new ResponseEntity<>(sum, HttpStatus.OK);
  }

  @GetMapping("/users/type/sum")
  public ResponseEntity<BigDecimal> getTheSumOfUserExpensesByType(
      @RequestParam("id") UUID id, @RequestParam("type") Type type) {
    BigDecimal sum = expenseService.calculateSumOfUserExpensesByType(id, type);
    return new ResponseEntity<>(sum, HttpStatus.OK);
  }

  @GetMapping("/users/type")
  public ResponseEntity<List<ExpenseResponseDto>> getAllExpensesOfUserByType(
      @RequestParam("id") UUID id, @RequestParam("type") Type type) {
    List<ExpenseResponseDto> response = expenseService.getAllExpensesOfUserByType(id, type);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/users/period")
  public ResponseEntity<List<ExpenseResponseDto>> getAllExpensesOfUserForPeriod(
      @RequestParam("id") UUID id,
      @RequestParam("startDate") LocalDate startDate,
      @RequestParam("endDate") LocalDate endDate) {
    List<ExpenseResponseDto> response =
        expenseService.getAllExpensesOfUserForPeriod(id, startDate, endDate);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/users/category/period")
  public ResponseEntity<BigDecimal> getAllExpensesOfUserForPeriodByCategory(
      @RequestParam("id") UUID id,
      @RequestParam("category") String category,
      @RequestParam("startDate") LocalDate startDate,
      @RequestParam("endDate") LocalDate endDate) {
    BigDecimal response =
        expenseService.getSumOfAllExpensesOfUserForPeriodByCategory(
            id, category, startDate, endDate);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/users/type/period")
  public ResponseEntity<BigDecimal> calculateSumOfUserExpensesByTypeAndPeriod(
      @RequestParam("id") UUID id,
      @RequestParam("type") Type type,
      @RequestParam("startDate") LocalDate startDate,
      @RequestParam("endDate") LocalDate endDate) {
    BigDecimal response =
        expenseService.calculateSumOfUserExpensesByTypeAndPeriod(id, type, startDate, endDate);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
