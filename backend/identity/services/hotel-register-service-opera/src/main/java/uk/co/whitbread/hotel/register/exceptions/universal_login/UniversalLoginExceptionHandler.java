package uk.co.whitbread.hotel.register.exceptions.universal_login;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.co.whitbread.hotel.register.controller.PIUniversalLoginController;

import java.util.List;

import static uk.co.whitbread.hotel.register.exceptions.universal_login.ApiProblemFactory.createProblem;
import static uk.co.whitbread.hotel.register.exceptions.universal_login.ApiProblemFactory.createValidationProblem;
import static uk.co.whitbread.hotel.register.exceptions.universal_login.UniversalLoginErrorCode.*;

/**
 * Global exception handler for the Universal Login controller.
 * Uses the ApiProblemFactory to create standardized RFC 7807 problem responses.
 */
@Slf4j
@ControllerAdvice(assignableTypes = PIUniversalLoginController.class)
public class UniversalLoginExceptionHandler {

    @ExceptionHandler(UniversalLoginException.class)
    public ResponseEntity<ProblemDetail> handleUniversalLoginException(UniversalLoginException ex) {
        log.warn("Universal Login exception: {}", ex.getMessage());
        return createProblem(EXCEPTION_OCCURRED, ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> details = ex.getConstraintViolations()
                .stream()
                .map(cv -> capitalize(cv.getMessage()))
                .toList();

        log.warn("Constraint violation: {}", details);
        return createValidationProblem(VALIDATION_FAILED, details);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> capitalize(error.getDefaultMessage()))
                .toList();

        log.warn("Method argument validation failed: {}", details);
        return createValidationProblem(VALIDATION_FAILED, details);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAllExceptions(Exception ex) {
        log.error("Unexpected error in Universal Login", ex);
        return createProblem(INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private String capitalize(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }
        return Character.toUpperCase(message.charAt(0)) + message.substring(1);
    }
}