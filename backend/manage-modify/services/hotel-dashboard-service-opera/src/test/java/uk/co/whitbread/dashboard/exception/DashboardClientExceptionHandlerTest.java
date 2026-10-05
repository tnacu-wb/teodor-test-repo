package uk.co.whitbread.dashboard.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.dashboard.infrastructure.rest.client.exception.DashboardClientError;
import uk.co.whitbread.dashboard.infrastructure.rest.client.exception.DashboardClientException;
import uk.co.whitbread.dashboard.infrastructure.rest.client.exception.DashboardClientExceptionHandler;

class DashboardClientExceptionHandlerTest {

    @Test
    void testHandleException() {
        final DashboardClientExceptionHandler handler = new DashboardClientExceptionHandler();
        final DashboardClientException exception = new DashboardClientException(404, "Not found", "150");

        ResponseEntity<DashboardClientError> response = handler.handleException(exception);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()),
                () -> assertEquals("Not found", Arrays.stream(Objects.requireNonNull(response.getBody()).getDetails()).findFirst().orElse(null)),
                () -> assertEquals("150", Objects.requireNonNull(response.getBody()).getCode())

        );


    }
}