package com.ludogorieSoft.budgetnik.exception;

import com.ludogorieSoft.budgetnik.dto.response.ExceptionResponse;
import com.ludogorieSoft.budgetnik.service.impl.slack.SlackService;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private final MessageSource messageSource;
  private final SlackService slackService;

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ExceptionResponse> handleRuntimeExceptions(RuntimeException exception) {
    exception.printStackTrace();
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

    return ResponseEntity.status(apiException.getStatus()).body(apiException);
  }

  @ExceptionHandler(Exception.class)
  public void alertSlackChannelWhenUnexpectedErrorOccurs(Exception ex) {
    slackService.sendMessage("Error ->" + ex.getMessage());
  }
}