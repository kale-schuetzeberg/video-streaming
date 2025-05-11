package com.baddog.awssimplestorageservice.exception;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GlobalExceptionHandlerTest {
  @Test
  void handleGeneric_shouldReturnInternalServerErrorAndGenericMessage() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    Exception ex = new RuntimeException("something went wrong");
    ResponseEntity<String> response = handler.handleGeneric(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isEqualTo("An internal error occurred.");
  }
}
