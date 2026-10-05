package uk.co.whitbread.hotel.account.exceptions;

import static uk.co.whitbread.common.exceptions.ErrorCodes.REQUIRED_HEADER_MISSING_CODE;
import static uk.co.whitbread.common.exceptions.ErrorCodes.NO_HANDLER_FOUND_EXCEPTION;
import static uk.co.whitbread.common.exceptions.ErrorCodes.NO_RESOURCE_FOUND_EXCEPTION;
import static uk.co.whitbread.common.exceptions.ErrorCodes.UNCLASSIFIED_ERROR_CODE;
import static uk.co.whitbread.common.exceptions.ErrorCodes.VALIDATION_ERROR_CODE;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.MALAuth0Exception;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.common.exceptions.http.MALHttpException;

@RestControllerAdvice
public class HotelAccountExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        List<String> details = exception.getBindingResult().getFieldErrors().stream()
            .map(this::formatFieldError)
            .toList();

        return error(HttpStatus.BAD_REQUEST, VALIDATION_ERROR_CODE.getCode(), details);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidation(HandlerMethodValidationException exception) {
        return error(HttpStatus.BAD_REQUEST, VALIDATION_ERROR_CODE.getCode(), exception.getReason());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
        List<String> details = exception.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
            .toList();

        return error(HttpStatus.BAD_REQUEST, VALIDATION_ERROR_CODE.getCode(), details);
    }

    @ExceptionHandler({MissingRequestHeaderException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponse> handleMissingRequestValue(Exception exception) {
        return error(HttpStatus.BAD_REQUEST, REQUIRED_HEADER_MISSING_CODE.getCode(), exception.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException exception) {
        return error(HttpStatus.NOT_FOUND, NO_RESOURCE_FOUND_EXCEPTION.getCode(), exception.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException exception) {
        return error(HttpStatus.NOT_FOUND, NO_HANDLER_FOUND_EXCEPTION.getCode(), exception.getMessage());
    }

    @ExceptionHandler(AbstractMALException.class)
    public ResponseEntity<ErrorResponse> handleMal(AbstractMALException exception) {
        if (exception instanceof MALHttpException malHttpException) {
            return error(HttpStatus.valueOf(malHttpException.getStatus()), exception.getErrorCode(), exception.getMessage());
        }
        if (exception instanceof MALAuth0Exception malAuth0Exception) {
            HttpStatus status = malAuth0Exception.getHttpStatus() != null
                ? malAuth0Exception.getHttpStatus()
                : HttpStatus.INTERNAL_SERVER_ERROR;
            return error(status, exception.getErrorCode(), exception.getMessage());
        }
        return error(HttpStatus.INTERNAL_SERVER_ERROR, exception.getErrorCode(), exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, UNCLASSIFIED_ERROR_CODE.getCode(), exception.getMessage());
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + " " + fieldError.getDefaultMessage();
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String code, String detail) {
        return error(status, code, List.of(detail));
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String code, List<String> details) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(code);
        errorResponse.setDetails(details);
        return ResponseEntity.status(status).body(errorResponse);
    }
}