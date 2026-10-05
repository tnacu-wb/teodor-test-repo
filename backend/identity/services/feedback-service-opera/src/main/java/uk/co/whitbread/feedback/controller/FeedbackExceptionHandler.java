package uk.co.whitbread.feedback.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;

import java.util.concurrent.CancellationException;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FeedbackExceptionHandler {

    @ExceptionHandler(CancellationException.class)
    public ResponseEntity<ErrorResponse> handleCancellationException(CancellationException ex) {
        log.error("Downstream service timeout - request was cancelled", ex);
        ErrorResponse errorResponse = new ErrorResponse("GATEWAY_TIMEOUT",
                "Downstream service did not respond in time");
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(errorResponse);
    }
}
