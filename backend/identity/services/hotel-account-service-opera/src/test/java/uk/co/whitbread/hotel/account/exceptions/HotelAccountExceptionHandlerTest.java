package uk.co.whitbread.hotel.account.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.co.whitbread.common.exceptions.ErrorCodes.NO_HANDLER_FOUND_EXCEPTION;
import static uk.co.whitbread.common.exceptions.ErrorCodes.NO_RESOURCE_FOUND_EXCEPTION;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;

class HotelAccountExceptionHandlerTest {

    private final HotelAccountExceptionHandler handler = new HotelAccountExceptionHandler();

    @Test
    void shouldReturn404ForNoResourceFoundException() {
        NoResourceFoundException exception = new NoResourceFoundException(
            HttpMethod.GET,
            "/invalid-url",
            "not found"
        );

        ResponseEntity<ErrorResponse> response = handler.handleNoResourceFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(NO_RESOURCE_FOUND_EXCEPTION.getCode(), response.getBody().getCode());
    }

    @Test
    void shouldReturn404ForNoHandlerFoundException() {
        NoHandlerFoundException exception = new NoHandlerFoundException("GET", "/invalid-url", new HttpHeaders());

        ResponseEntity<ErrorResponse> response = handler.handleNoHandlerFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(NO_HANDLER_FOUND_EXCEPTION.getCode(), response.getBody().getCode());
    }
}
