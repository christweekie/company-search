package org.chrisfaulkner.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import static org.assertj.core.api.Assertions.assertThat;

class RestResponseExceptionHandlerTest {

    private final RestResponseExceptionHandler handler = new RestResponseExceptionHandler();

    @Test
    void whenHandleConflict_thenReturnsBadRequestWithExceptionMessage() {
        // Setup test case
        String exceptionMessage = "Test exception message";
        RuntimeException exception = new IllegalArgumentException(exceptionMessage);
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        // Exercise unit under test
        var result = handler.handleConflict(exception, request);

        // Verify the results
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isEqualTo(exceptionMessage);
    }


}
