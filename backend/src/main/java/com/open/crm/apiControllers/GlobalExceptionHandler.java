package com.open.crm.apiControllers;

import com.open.crm.apiControllers.dto.ApplicationErrorDto;
import com.open.crm.components.errors.SessionException;
import com.open.crm.core.application.errors.ApplicationException;
import com.open.crm.core.application.errors.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({
    NotFoundException.class,
    org.springframework.data.crossstore.ChangeSetPersister.NotFoundException.class
  })
  public ResponseEntity<ApplicationErrorDto> handleNotFoundException(Exception ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApplicationErrorDto(ex.getMessage()));
  }

  @ExceptionHandler({ApplicationException.class})
  public ResponseEntity<ApplicationErrorDto> handleApplicationException(ApplicationException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApplicationErrorDto(ex.getMessage()));
  }

  @ExceptionHandler({SessionException.class})
  public ResponseEntity<ApplicationErrorDto> handleSessionException(SessionException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApplicationErrorDto(ex.getMessage()));
  }
}
