package uk.co.whitbread.common.exceptions.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import uk.co.whitbread.common.exceptions.MALAuth0Exception;
import uk.co.whitbread.common.exceptions.MALBartException;
import uk.co.whitbread.common.exceptions.MALException;
import uk.co.whitbread.common.exceptions.MALWarning;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.common.exceptions.http.MALHttpException;
import uk.co.whitbread.common.exceptions.mapping.ErrorCodeMapping;
import uk.co.whitbread.common.exceptions.mapping.ErrorCodeMappingConfig;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.util.StringUtils.isEmpty;
import static uk.co.whitbread.common.exceptions.ErrorCodes.*;

/**
 * Captures exceptions that are thrown from code the Controllers call, and
 * returns an ErrorBean representation of the error with the appropriate HTTP response code.
 * <p>
 * Created by KrakenDevTeam on 02/12/2016.
 */
@RestControllerAdvice
@Slf4j
public class ErrorProcessor {

    private static final String STACKTRACE_NDX = " at [Source:";
   
    private final ErrorCodeMappingConfig errorCodeMappingConfig;

    private final Map<String, ErrorCodeMapping> bartErrorsMap = new HashMap<>();

    public ErrorProcessor(ErrorCodeMappingConfig errorCodeMappingConfig) {

        errorCodeMappingConfig = ofNullable(errorCodeMappingConfig).orElseGet(ErrorCodeMappingConfig::new);

        this.errorCodeMappingConfig = errorCodeMappingConfig;

        ofNullable(errorCodeMappingConfig.getMappings()).ifPresent(errorCodeMappings -> errorCodeMappings.forEach(errorCodeMapping -> {

            ofNullable(errorCodeMapping.getBartCodes()).ifPresent(bartCodes -> bartCodes.forEach(bartCode -> {

                bartErrorsMap.put(bartCode, errorCodeMapping);

            }));

        }));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoResourceFoundException(NoResourceFoundException exception) {
        log.error("Processing NoResourceFoundException - resource not found", exception);
        return new ErrorResponse(NO_RESOURCE_FOUND_EXCEPTION.getCode(), "Resource not found: " + exception.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoHandlerFoundException(NoHandlerFoundException exception) {
        log.error("Processing NoHandlerFoundException - no handler found", exception);
        return new ErrorResponse(NO_HANDLER_FOUND_EXCEPTION.getCode(), "No handler found: " + exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(value = INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServerException(Exception exception) {
        log.error("Processing General Exception", exception);
        return new ErrorResponse(UNCLASSIFIED_ERROR_CODE.getCode(), exception.getMessage());
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ErrorResponse handleRuntimeException(HttpServletResponse response, RuntimeException exception) {
        if (MALHttpException.class.isAssignableFrom(exception.getClass())) {
            return handleHTTPException(response, (MALHttpException) exception);
        }

        if ((exception.getCause() != null) &&
                (MALHttpException.class.isAssignableFrom(exception.getCause().getClass()))) {
            return handleHTTPException(response, (MALHttpException) exception.getCause());
        }

        if (MALBartException.class.isAssignableFrom(exception.getClass())) {
            return handleBartServiceException(response, (MALBartException) exception);
        }

        if (MALAuth0Exception.class.isAssignableFrom(exception.getClass())) {
            return handleAuth0Exception(response, (MALAuth0Exception) exception);
        }

        if (exception.getCause() != null) {
            if ((MALBartException.class.isAssignableFrom(exception.getCause().getClass()))) {
                return handleBartServiceException(response, (MALBartException) exception.getCause());
            }
        }

        return handleServerException(response, exception);
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRequestBindingException(ServletRequestBindingException exception) {
        log.error("Processing ServletRequestBindingException", exception);
        return new ErrorResponse(REQUIRED_HEADER_MISSING_CODE.getCode(), exception.getMessage());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBindException(BindException exception) {
        log.error("Processing BindException", exception);
        return new ErrorResponse(VALIDATION_ERROR_CODE.getCode(), exception);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException exception) {
        log.error("Processing ValidationException", exception);
        return new ErrorResponse(VALIDATION_ERROR_CODE.getCode(), exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException exception) {
        log.error("Processing MethodArgumentNotValidException", exception);
        return new ErrorResponse(VALIDATION_ERROR_CODE.getCode(), exception.getBindingResult());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentTypeMismatchException exception) {
        log.error("Processing MethodArgumentNotValidException", exception);

        Class<?> type = exception.getRequiredType();
        String typeName = type.getTypeName();
        if(type.isEnum()) {
            typeName = Arrays.toString(type.getEnumConstants());
        }
        String typeErrorMessage = String.format("The parameter '%s' must be of type '%s'", exception.getName(), typeName);

        return new ErrorResponse(VALIDATION_ERROR_CODE.getCode(), typeErrorMessage);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(HttpMessageNotReadableException exception) {
        log.error("Processing HttpMessageNotReadableException", exception);

        String message = ofNullable(exception.getCause())
                .map(Throwable::getMessage)
                .orElse(exception.getMessage());

        return stripStackTrace(message);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ConstraintViolationException exception) {
        log.error("Processing ConstraintViolationExceptionException", exception);
        return new ErrorResponse(VALIDATION_ERROR_CODE.getCode(), extractConstraintViolations(exception));
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse handleMethodNotSupportedException(Exception exception) {

        log.error("Processing HttpRequestMethodNotSupportedException", exception);
        return new ErrorResponse(HTTP_METHOD_NOT_SUPPORTED.getCode(), exception.getMessage());
    }

    private String[] extractConstraintViolations(ConstraintViolationException exception) {
        return exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toArray(String[]::new);
    }

    private ErrorResponse handleServerException(HttpServletResponse response, Exception exception) {
        log.error("Processing General Exception", exception);
        response.setStatus(INTERNAL_SERVER_ERROR.value());
        return new ErrorResponse(UNCLASSIFIED_ERROR_CODE.getCode(), exception.getMessage());
    }

    private ErrorResponse handleAuth0Exception(HttpServletResponse response, MALAuth0Exception exception) {
        log.error("Processing MALAuth0Exception Exception", exception);

        String errorCode = exception.getErrorCode();
        if (isEmpty(errorCode)) {
            errorCode = AUTH0_GENERIC_ERROR_CODE.getCode();
        }
        response.setStatus(
                ofNullable(exception.getHttpStatus())
                .map(HttpStatus::value)
                .orElse(INTERNAL_SERVER_ERROR.value()));

        return new ErrorResponse(errorCode, exception.getMessage());
    }

    private ErrorResponse handleBartServiceException(HttpServletResponse response, MALBartException exception) {
        log.error("Processing MALBartException {}", exception);

        ErrorCodeMapping errorMapping = bartErrorsMap.get(exception.getErrorCode());

        if (errorMapping != null) {
            response.setStatus(errorMapping.getHttpStatus());
            return new ErrorResponse(errorMapping.getCode(), getErrorMessage(exception));
        } else {
            // Some business booker Microservices are using the method withError()
            // in BartServiceException. For this cases the errorCode should be considered.
            // For the other cases, if the errorCode contains the errorMessage it means that
            // the message is not in the error mapping and the withError()
            // method is not being used. So the Bart generic code should be used.
            String errorCode = exception.getErrorCode();
            if (!isEmpty(errorCode)
                && !isEmpty(exception.getMessage())
                && exception.getMessage().contains(errorCode)
            ) {
                errorCode = BART_GENERIC_ERROR_CODE.getCode();
            }
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return new ErrorResponse(errorCode, getErrorMessage(exception));
        }

    }

    private String getErrorMessage(MALException malException) {
        if(!isEmpty(malException.getMessage())) {
            return malException.getMessage();
        }
        return malException.getErrorCode();
    }

    private ErrorResponse handleHTTPException(HttpServletResponse response, MALHttpException exception) {
        if (MALWarning.class.isAssignableFrom(exception.getClass())) {
            log.warn("Processing MALHttpException {}", exception);
        } else {
            log.error("Processing MALHttpException {}", exception);
        }
        response.setStatus(exception.getStatus());
        return new ErrorResponse(exception.getErrorCode(), exception.getMessage());
    }

    private ErrorResponse stripStackTrace(String message) {
        int ndx = message.indexOf(STACKTRACE_NDX);
        if (ndx < 0) {
            return new ErrorResponse(VALIDATION_ERROR_CODE.getCode(), message);
        } else {
            return new ErrorResponse(VALIDATION_ERROR_CODE.getCode(), message.substring(0, ndx - 1));
        }
    }
}
