package uk.co.whitbread.commons.exceptions.advice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import uk.co.whitbread.commons.exceptions.enums.ValidationError;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException exception,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    log.error(String.format("%s handleNoResourceFoundException(): resource not found",
            globalMessage(ValidationError.NO_RESOURCE_FOUND_EXCEPTION)),
        exception);
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorResponse.builder()
            .errCode(ValidationError.NO_RESOURCE_FOUND_EXCEPTION.getErrorCode())
            .debugMessage(exception.getMessage())
            .globalErrTextTemplate(ValidationError.NO_RESOURCE_FOUND_EXCEPTION.getMessage())
            .build());
  }

  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException exception,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    log.error(String.format("%s handleNoHandlerFoundException(): no handler found",
            globalMessage(ValidationError.NO_HANDLER_FOUND_EXCEPTION)),
        exception);
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorResponse.builder()
            .errCode(ValidationError.NO_HANDLER_FOUND_EXCEPTION.getErrorCode())
            .debugMessage(exception.getMessage())
            .globalErrTextTemplate(ValidationError.NO_HANDLER_FOUND_EXCEPTION.getMessage())
            .build());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleAllUncaughtException(Exception exception,
      WebRequest request) {
    log.error(String.format("%s handleAllUncaughtException(): uncaught error occurred",
            globalMessage(ValidationError.GENERIC_EXCEPTION)),
        exception);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.builder()
            .errCode(ValidationError.GENERIC_EXCEPTION.getErrorCode())
            .debugMessage(exception.getMessage())
            .globalErrTextTemplate(ValidationError.GENERIC_EXCEPTION.getMessage())
            .build());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
    log.error(
        String.format("%s handleConstraintViolation(): constraint violation exception encountered",
            globalMessage(ValidationError.GENERIC_CONSTRAINT_VIOLATION_EXCEPTION)),
        ex
    );
    var errorResponse = ErrorResponse.builder()
        .errCode(ValidationError.GENERIC_CONSTRAINT_VIOLATION_EXCEPTION.getErrorCode())
        .debugMessage(ex.getMessage())
        .globalErrTextTemplate(ValidationError.GENERIC_CONSTRAINT_VIOLATION_EXCEPTION.getMessage())
        .build();

    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      errorResponse.addValidationError(
          violation.getRootBeanClass().getName() + " " + violation.getPropertyPath(),
          violation.getMessage());
    }
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(errorResponse);
  }

  @Override
  protected ResponseEntity<Object> handleServletRequestBindingException(
      ServletRequestBindingException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    log.error(new StringBuilder()
        .append(globalMessage(ValidationError.GENERIC_BINDING_EXCEPTION))
        .append(" handleMethodArgumentNotValid(): exception occurred: ").append(ex)
        .append(" with the error: ").append(ex.getBody().getDetail()).toString());
    var errorResponse = ErrorResponse.builder()
        .errCode(ValidationError.GENERIC_BINDING_EXCEPTION.getErrorCode())
        .debugMessage(ex.getMessage())
        .globalErrTextTemplate(ValidationError.GENERIC_BINDING_EXCEPTION.getMessage())
        .build();
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(errorResponse);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      final MethodArgumentNotValidException ex,
      final HttpHeaders headers, final HttpStatusCode status, final WebRequest request) {
    var errorResponse = handleBindingExceptions(ex);
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(errorResponse);
  }


  @Override
  public ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status,
      WebRequest request) {
    String unsupported = "Unsupported content type: " + ex.getContentType();
    String supported =
        "Supported content types: " + MediaType.toString(ex.getSupportedMediaTypes());
    log.error(new StringBuilder()
        .append(globalMessage(ValidationError.HTTP_MEDIA_TYPE_NOT_SUPPORTED_EXCEPTION))
        .append(" handleHttpMediaTypeNotSupported(): exception found : ").append(ex)
        .append(", unsupported content type: ").append(unsupported)
        .append(", supported content types are: ")
        .append(supported).toString());
    var errorResponse = ErrorResponse.builder()
        .errCode(ValidationError.HTTP_MEDIA_TYPE_NOT_SUPPORTED_EXCEPTION.getErrorCode())
        .debugMessage(ex.getMessage())
        .globalErrTextTemplate(
            ValidationError.HTTP_MEDIA_TYPE_NOT_SUPPORTED_EXCEPTION.getMessage())
        .build();
    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponse);
  }

  @Override
  public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    String exceptionMessage = " ";
    Throwable mostSpecificCause = ex.getMostSpecificCause();
    if (mostSpecificCause != null) {
      String exceptionName = mostSpecificCause.getClass().getName();
      exceptionMessage = mostSpecificCause.getMessage();
      log.error(new StringBuilder()
          .append(globalMessage(ValidationError.HTTP_MESSAGE_NOT_READABLE_EXCEPTION))
          .append(" handleHttpMessageNotReadable() exception found: ").append(exceptionName)
          .append(" with message: ").append(exceptionMessage).toString());
    } else {
      log.error(String.format("%s handleHttpMessageNotReadable() exception found",
              globalMessage(ValidationError.HTTP_MESSAGE_NOT_READABLE_EXCEPTION)),
          ex.getMessage());
      exceptionMessage = ex.getMessage();
    }
    var errorResponse = ErrorResponse.builder()
        .errCode(ValidationError.HTTP_MESSAGE_NOT_READABLE_EXCEPTION.getErrorCode())
        .debugMessage("Exception found: " + exceptionMessage)
        .globalErrTextTemplate(ValidationError.HTTP_MESSAGE_NOT_READABLE_EXCEPTION.getMessage())
        .build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @Override
  protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
      HttpStatusCode status, WebRequest request) {
    String error =
        ex.getPropertyName() + " should be of type " + ex.getRequiredType().getName();
    log.error(String.format("%s handleTypeMismatch(): type mismatch exception",
            globalMessage(ValidationError.TYPE_MISMATCH_EXCEPTION)),
        error);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.builder()
            .errCode(ValidationError.TYPE_MISMATCH_EXCEPTION.getErrorCode())
            .debugMessage(ex.getMessage())
            .globalErrTextTemplate(ValidationError.TYPE_MISMATCH_EXCEPTION.getMessage())
            .build());
  }

  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    StringBuilder builder = new StringBuilder();
    builder.append(ex.getMethod());
    builder.append(
        " method is not supported for this request. Supported methods are ");
    ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

    log.error(
        String.format(
            "%s handleHttpRequestMethodNotSupported(): HTTP Request method not supported exception",
            globalMessage(ValidationError.HTTP_REQUEST_NOT_SUPPORTED_EXCEPTION)),
        builder);

    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .body(ErrorResponse.builder()
            .errCode(ValidationError.HTTP_REQUEST_NOT_SUPPORTED_EXCEPTION.getErrorCode())
            .debugMessage(builder.toString())
            .globalErrTextTemplate(
                ValidationError.HTTP_REQUEST_NOT_SUPPORTED_EXCEPTION.getMessage())
            .build());
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    String error = ex.getParameterName() + " parameter is missing";
    log.error(
        String.format(
            "%s handleMissingServletRequestParameter(): missing servlet request parameter exception",
            globalMessage(ValidationError.MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION)),
        ex);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.builder()
            .errCode(ValidationError.MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION.getErrorCode())
            .debugMessage(error)
            .globalErrTextTemplate(
                ValidationError.MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION.getMessage())
            .build());
  }

  @Override
  protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    String error = ex.getVariableName() + " is missing";
    log.error(String.format("%s handleMissingPathVariable(): Missing path variable exception",
            globalMessage(ValidationError.MISSING_PATH_VARIABLE_EXCEPTION)),
        ex);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.builder()
            .errCode(ValidationError.MISSING_PATH_VARIABLE_EXCEPTION.getErrorCode())
            .debugMessage(error)
            .globalErrTextTemplate(ValidationError.MISSING_PATH_VARIABLE_EXCEPTION.getMessage())
            .build());
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    log.error(
        String.format("%s handleHttpMessageNotWritable(): HTTP message not writable exception",
            globalMessage(ValidationError.HTTP_MESSAGE_NOT_WRITABLE_EXCEPTION)),
        ex);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.builder()
            .errCode(ValidationError.HTTP_MESSAGE_NOT_WRITABLE_EXCEPTION.getErrorCode())
            .debugMessage(ex.getMessage())
            .globalErrTextTemplate(
                ValidationError.HTTP_MESSAGE_NOT_WRITABLE_EXCEPTION.getMessage())
            .build());
  }

  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
      HttpMediaTypeNotAcceptableException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    StringBuilder builder = new StringBuilder();
    builder.append(globalMessage(ValidationError.HTTP_MEDIA_TYPE_NOT_ACCEPTABLE_EXCEPTION))
        .append(" handleHttpMediaTypeNotAcceptable(): HTTP Media Type Not Acceptable exception : ")
        .append(ex.getMessage()).append(" , supported media types are: ")
        .append(ex.getSupportedMediaTypes());
    log.error(builder.toString());
    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
        .body(ErrorResponse.builder()
            .errCode(ValidationError.HTTP_MEDIA_TYPE_NOT_ACCEPTABLE_EXCEPTION.getErrorCode())
            .debugMessage(builder.toString())
            .globalErrTextTemplate(
                ValidationError.HTTP_MEDIA_TYPE_NOT_ACCEPTABLE_EXCEPTION.getMessage())
            .build());
  }

  @Override
  public ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    log.error(globalMessage(ValidationError.GENERIC_UNKNOWN_EXCEPTION), ex);

    return ResponseEntity.status(status).body(ErrorResponse.builder()
        .errCode(ValidationError.GENERIC_UNKNOWN_EXCEPTION.getErrorCode())
        .debugMessage(ex.getMessage())
        .globalErrTextTemplate(ValidationError.GENERIC_UNKNOWN_EXCEPTION.getMessage())
        .build());
  }

  private ErrorResponse handleBindingExceptions(BindException ex) {
    var errorResponse = ErrorResponse.builder()
        .errCode(ValidationError.GENERIC_VALIDATION_EXCEPTION.getErrorCode())
        .debugMessage(ex.getMessage())
        .globalErrTextTemplate(ValidationError.GENERIC_VALIDATION_EXCEPTION.getMessage())
        .build();

    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      log.error(new StringBuilder()
          .append(globalMessage(ValidationError.GENERIC_VALIDATION_EXCEPTION))
          .append(" handleMethodArgumentNotValid(): exception occurred: ").append(ex)
          .append(", error for field name: ").append(fieldName).append(" with the error: ")
          .append(errorMessage).toString());
      errorResponse.addValidationError(fieldName, errorMessage);
    });
    return errorResponse;
  }

  private String globalMessage(ValidationError error) {
    return String.format(
        "Global exception error code: %s, message: %s",
        error.getErrorCode(),
        error.getMessage()
    );
  }
}
