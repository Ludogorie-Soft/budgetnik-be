package com.ludogorieSoft.budgetnik.controller;

import com.ludogorieSoft.budgetnik.dto.request.IncomeRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.IncomeResponseDto;
import com.ludogorieSoft.budgetnik.model.enums.Type;
import com.ludogorieSoft.budgetnik.service.IncomeService;
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
@RequestMapping("/api/incomes")
@RequiredArgsConstructor
public class IncomesController {

  private final IncomeService incomeService;

  @PostMapping
  public ResponseEntity<IncomeResponseDto> createIncome(
      @RequestBody IncomeRequestDto incomeRequestDto) {
    IncomeResponseDto response = incomeService.createIncome(incomeRequestDto);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<IncomeResponseDto> getIncome(@RequestParam("id") UUID id) {
    IncomeResponseDto response = incomeService.getIncome(id);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PutMapping
  public ResponseEntity<IncomeResponseDto> editIncome(
      @RequestParam("id") UUID id, @RequestBody IncomeRequestDto requestDto) {
    IncomeResponseDto response = incomeService.editIncome(id, requestDto);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/users")
  public ResponseEntity<List<IncomeResponseDto>> getAllIncomesOfUser(@RequestParam("id") UUID id) {
    List<IncomeResponseDto> response = incomeService.getAllIncomesOfUser(id);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @DeleteMapping
  public ResponseEntity<IncomeResponseDto> deleteIncome(@RequestParam("id") UUID id) {
    IncomeResponseDto response = incomeService.deleteIncome(id);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/users/sum")
  public ResponseEntity<BigDecimal> getTheSumOfAllIncomesOfUser(@RequestParam("id") UUID id) {
    BigDecimal sum = incomeService.calculateSumOfAllIncomesOfUser(id);
    return new ResponseEntity<>(sum, HttpStatus.OK);
  }

  @GetMapping("/users/type/sum")
  public ResponseEntity<BigDecimal> getTheSumOfUserIncomesByType(
      @RequestParam("id") UUID id, @RequestParam("type") Type type) {
    BigDecimal sum = incomeService.calculateSumOfUserIncomesByType(id, type);
    return new ResponseEntity<>(sum, HttpStatus.OK);
  }

  @GetMapping("/users/category/sum")
  public ResponseEntity<BigDecimal> getTheSumOfAllIncomesOfUser(
      @RequestParam("id") UUID id, @RequestParam("category") String category) {
    BigDecimal sum = incomeService.calculateSumOfAllIncomesOfUserByCategory(id, category);
    return new ResponseEntity<>(sum, HttpStatus.OK);
  }

  @GetMapping("/users/type")
  public ResponseEntity<List<IncomeResponseDto>> getAllUserIncomesByType(
      @RequestParam("id") UUID id, @RequestParam("type") Type type) {
    List<IncomeResponseDto> response = incomeService.findAllByUserAndType(id, type);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/users/period")
  public ResponseEntity<List<IncomeResponseDto>> getAllUserIncomesForPeriod(
      @RequestParam("id") UUID id,
      @RequestParam("startDate") LocalDate startDate,
      @RequestParam("endDate") LocalDate endDate) {
    List<IncomeResponseDto> response =
        incomeService.getAllIncomesOfUserForPeriod(id, startDate, endDate);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/users/category/period")
  public ResponseEntity<BigDecimal> getAllUserIncomesForPeriodByCategory(
      @RequestParam("id") UUID id,
      @RequestParam("category") String category,
      @RequestParam("startDate") LocalDate startDate,
      @RequestParam("endDate") LocalDate endDate) {
    BigDecimal response =
        incomeService.getSumOfAllIncomesOfUserForPeriodByCategory(id, category, startDate, endDate);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/users/type/period")
  public ResponseEntity<BigDecimal> calculateSumOfUserIncomesByTypeAndPeriod(
      @RequestParam("id") UUID id,
      @RequestParam("type") Type type,
      @RequestParam("startDate") LocalDate startDate,
      @RequestParam("endDate") LocalDate endDate) {
    BigDecimal response =
        incomeService.calculateSumOfUserIncomesByTypeAndPeriod(id, type, startDate, endDate);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/users/due-date")
  public ResponseEntity<List<IncomeResponseDto>> findAllFixedIncomesByDueDate(@RequestParam("date") LocalDate date) {
    List<IncomeResponseDto> response = incomeService.findAllFixedIncomesBeforeThanEqualDueDate(date, Type.FIXED);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
