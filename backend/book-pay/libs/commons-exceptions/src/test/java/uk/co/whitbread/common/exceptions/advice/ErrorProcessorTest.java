package uk.co.whitbread.common.exceptions.advice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import uk.co.whitbread.common.exceptions.MALAuth0Exception;
import uk.co.whitbread.common.exceptions.MALBartException;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.common.exceptions.mapping.ErrorCodeMapping;
import uk.co.whitbread.common.exceptions.mapping.ErrorCodeMappingConfig;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.verify;
import static uk.co.whitbread.common.exceptions.ErrorCodes.NO_HANDLER_FOUND_EXCEPTION;
import static uk.co.whitbread.common.exceptions.ErrorCodes.NO_RESOURCE_FOUND_EXCEPTION;


@ExtendWith(MockitoExtension.class)
public class ErrorProcessorTest {

    @Mock
    HttpServletResponse httpServletResponse;

    @Test
    public void shouldLoadWhenConfigIsNull(){
        ErrorProcessor errorProcessor = new ErrorProcessor(null);
        assertThat("Created", errorProcessor, notNullValue());
    }

    @Test
    public void shouldLoadWhenMappingsAreNull(){

        ErrorCodeMappingConfig errorCodeMappingConfig = new ErrorCodeMappingConfig();
        ErrorProcessor errorProcessor = new ErrorProcessor(errorCodeMappingConfig);
        assertThat("Created", errorProcessor, notNullValue());
    }

    @Test
    public void shouldHandleBartServiceExceptionWhenCodeIsMapped(){

        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage("Test Bart Error Message");
        BartServiceException bartServiceException = new BartServiceException("BART_ERROR_CODE", errorDetails);

        ErrorCodeMappingConfig errorCodeMappingConfig = createErrorCodeMappingConfig();

        ErrorProcessor errorProcessor = new ErrorProcessor(errorCodeMappingConfig);

        assertThat("Created", errorProcessor, notNullValue());

        ErrorResponse errorResponse = errorProcessor.handleRuntimeException(httpServletResponse, bartServiceException);

        assertThat("ErrorResponse", errorResponse, notNullValue());

        assertThat("Error Code", errorResponse.getCode(), equalTo("100"));
        assertThat("Error Details", errorResponse.getDetails(), hasItem("BART_ERROR_CODE: Test Bart Error Message"));

        verify(httpServletResponse).setStatus(400);

    }

    @Test
    public void shouldHandleBartServiceExceptionWhenCodeIsNotMapped(){


        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setErrorMessage("Test Bart Error Message");
        BartServiceException bartServiceException = new BartServiceException("BART_ERROR_CODE", errorDetails);

        ErrorCodeMappingConfig errorCodeMappingConfig = createErrorCodeMappingConfig();

        ErrorProcessor errorProcessor = new ErrorProcessor(errorCodeMappingConfig);

        assertThat("Created", errorProcessor, notNullValue());


        ErrorResponse errorResponse = errorProcessor.handleRuntimeException(httpServletResponse, bartServiceException);

        assertThat("ErrorResponse", errorResponse, notNullValue());

        assertThat("Error Code", errorResponse.getCode(), equalTo("100"));
        assertThat("Error Details", errorResponse.getDetails(), hasItem("BART_ERROR_CODE: Test Bart Error Message"));

        verify(httpServletResponse).setStatus(400);

    }

    @Test
    public void shouldHandleAuth0ExceptionWhenCodeIsEmpty(){

        Auth0ServiceException auth0ServiceException = new Auth0ServiceException("Test Auth0 Error Message");

        ErrorCodeMappingConfig errorCodeMappingConfig = createErrorCodeMappingConfig();

        ErrorProcessor errorProcessor = new ErrorProcessor(errorCodeMappingConfig);

        assertThat("Created", errorProcessor, notNullValue());

        ErrorResponse errorResponse = errorProcessor.handleRuntimeException(httpServletResponse, auth0ServiceException);

        assertThat("ErrorResponse", errorResponse, notNullValue());

        assertThat("Error Code", errorResponse.getCode(), equalTo("7000"));
        assertThat("Error Details", errorResponse.getDetails(), hasItem("Test Auth0 Error Message"));

        verify(httpServletResponse).setStatus(500);
    }

    @Test
    public void shouldHandleValidationException(){

        ValidationException validationException = new ValidationException("Test ValidationException Error Message");

        ErrorCodeMappingConfig errorCodeMappingConfig = createErrorCodeMappingConfig();

        ErrorProcessor errorProcessor = new ErrorProcessor(errorCodeMappingConfig);

        assertThat("Created", errorProcessor, notNullValue());

        ErrorResponse errorResponse = errorProcessor.handleValidationException(validationException);

        assertThat("ErrorResponse", errorResponse, notNullValue());

        assertThat("Error Code", errorResponse.getCode(), equalTo("001"));
        assertThat("Error Details", errorResponse.getDetails(), hasItem("Test ValidationException Error Message"));
    }

    @Test
    public void shouldHandleAuth0ExceptionWhenCodeIsNotEmpty(){

        Auth0ServiceException amadeusServiceException = new Auth0ServiceException("3002", "Test Auth0 Error Message", null);

        ErrorCodeMappingConfig errorCodeMappingConfig = createErrorCodeMappingConfig();

        ErrorProcessor errorProcessor = new ErrorProcessor(errorCodeMappingConfig);

        assertThat("Created", errorProcessor, notNullValue());

        ErrorResponse errorResponse = errorProcessor.handleRuntimeException(httpServletResponse, amadeusServiceException);

        assertThat("ErrorResponse", errorResponse, notNullValue());

        assertThat("Error Code", errorResponse.getCode(), equalTo("3002"));
        assertThat("Error Details", errorResponse.getDetails(), hasItem("Test Auth0 Error Message"));

        verify(httpServletResponse).setStatus(500);
    }

    @Test
    public void shouldHandleAuth0ExceptionWhenHttpStatusIsNotEmpty(){

        Auth0ServiceException amadeusServiceException = new Auth0ServiceException("3002", "Test Auth0 Error Message", HttpStatus.BAD_REQUEST);

        ErrorCodeMappingConfig errorCodeMappingConfig = createErrorCodeMappingConfig();

        ErrorProcessor errorProcessor = new ErrorProcessor(errorCodeMappingConfig);

        assertThat("Created", errorProcessor, notNullValue());

        ErrorResponse errorResponse = errorProcessor.handleRuntimeException(httpServletResponse, amadeusServiceException);

        assertThat("ErrorResponse", errorResponse, notNullValue());

        assertThat("Error Code", errorResponse.getCode(), equalTo("3002"));
        assertThat("Error Details", errorResponse.getDetails(), hasItem("Test Auth0 Error Message"));

        verify(httpServletResponse).setStatus(400);
    }

    @Test
    public void shouldHandleNoResourceFoundExceptionAs404() {
        NoResourceFoundException exception = new NoResourceFoundException(
            org.springframework.http.HttpMethod.GET, 
            "/test-resource", 
            "test message"
        );
        
        ErrorProcessor errorProcessor = new ErrorProcessor(null);
        ErrorResponse errorResponse = errorProcessor.handleNoResourceFoundException(exception);
        
        assertThat("ErrorResponse", errorResponse, notNullValue());
        assertThat("Error Code", errorResponse.getCode(), equalTo(NO_RESOURCE_FOUND_EXCEPTION.getCode()));
    }

    @Test
    public void shouldHandleNoHandlerFoundExceptionAs404() {
        NoHandlerFoundException exception = new NoHandlerFoundException("GET", "/unknown/path", null);
        
        ErrorProcessor errorProcessor = new ErrorProcessor(null);
        ErrorResponse errorResponse = errorProcessor.handleNoHandlerFoundException(exception);
        
        assertThat("ErrorResponse", errorResponse, notNullValue());
        assertThat("Error Code", errorResponse.getCode(), equalTo(NO_HANDLER_FOUND_EXCEPTION.getCode()));
    }


    private ErrorCodeMappingConfig createErrorCodeMappingConfig(){


        ErrorCodeMappingConfig errorCodeMappingConfig = new ErrorCodeMappingConfig();
        ErrorCodeMapping errorCodeMapping = new ErrorCodeMapping();
        errorCodeMapping.setMessage("Test Mapped Error Message");
        errorCodeMapping.setHttpStatus(400);
        errorCodeMapping.setCode("100");
        errorCodeMapping.setBartCodes(Collections.singletonList("BART_ERROR_CODE"));
        errorCodeMappingConfig.setMappings(Collections.singletonList(errorCodeMapping));
        return errorCodeMappingConfig;
    }

    private static class BartServiceException extends RuntimeException implements MALBartException{

        String errorCode;

        BartServiceException(String methodError, ErrorDetails errorDetail) {
            super(createErrorMessageFromErrorDetail(methodError, errorDetail));
            this.errorCode = methodError;
        }

        public String getErrorCode() {
            return errorCode;
        }
        private static String createErrorMessageFromErrorDetail(String methodError, ErrorDetails errorDetail) {
            List<String> errors = new ArrayList<>();
            if (!StringUtils.isEmpty(methodError)) {
                errors.add(methodError);
            }
            if (errorDetail != null && !StringUtils.isEmpty(errorDetail.getErrorMessage())) {
                errors.add(errorDetail.getErrorMessage());
            }
            return String.join(": ", errors);
        }


    }

    private static class Auth0ServiceException extends RuntimeException implements MALAuth0Exception {

        String message;
        String errorCode;
        HttpStatus httpStatus;

        Auth0ServiceException(String errorCode, String message, HttpStatus httpStatus){
            this.errorCode = errorCode;
            this.message = message;
            this.httpStatus = httpStatus;
        }

        Auth0ServiceException(String message){
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public String getErrorCode() {
            return errorCode;
        }

        @Override
        public HttpStatus getHttpStatus() {
            return httpStatus;
        }
    }
    

    private static class ErrorDetails {

        String errorMessage;

        String getErrorMessage() {
            return errorMessage;
        }

        void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }



}
