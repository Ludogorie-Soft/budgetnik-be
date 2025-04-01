package com.ludogorieSoft.budgetnik.exception;

import com.ludogorieSoft.budgetnik.dto.response.ExceptionResponse;
import com.ludogorieSoft.budgetnik.service.impl.SlackService;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private final MessageSource messageSource;
  private final SlackService slackService;

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ExceptionResponse> handleRuntimeExceptions(RuntimeException exception) {
    exception.printStackTrace();
    slackService.sendMessage("Error: " + exception.getMessage());
    return handleApiExceptions(new InternalServerErrorException(messageSource));
  }

  @ExceptionHandler(TransactionException.class)
  public ResponseEntity<ExceptionResponse> handleTransactionExceptions(
      org.springframework.transaction.TransactionException exception) {
    if (exception.getRootCause() instanceof ConstraintViolationException) {
      return handleConstraintValidationExceptions(
          (ConstraintViolationException) exception.getRootCause());
    }
    return handleRuntimeExceptions(exception);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ExceptionResponse> handleConstraintValidationExceptions(
      ConstraintViolationException exception) {
    return handleApiExceptions(new ValidationException(exception.getConstraintViolations()));
  }

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ExceptionResponse> handleApiExceptions(ApiException exception) {
    ExceptionResponse apiException = ApiExceptionParser.parseException(exception);
    slackService.sendMessage("Error: " + exception.getMessage());
    return ResponseEntity.status(apiException.getStatus()).body(apiException);
  }

  @ExceptionHandler({Exception.class})
  public void alertSlackChannelWhenUnexpectedErrorOccurs(Exception ex) {
    slackService.sendMessage("Error: " + ex.getMessage());
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
          MethodArgumentNotValidException ex,
          HttpHeaders headers,
          HttpStatusCode status,
          WebRequest request) {

    Map<String, String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage,
                    (existing, replacement) -> existing
            ));

    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("timestamp", LocalDateTime.now());
    responseBody.put("status", status.value());
    responseBody.put("errors", errors); // Тук ще са съобщенията за грешките
    responseBody.put("path", request.getDescription(false));

    return new ResponseEntity<>(responseBody, headers, status);
  }
}
