package uk.co.whitbread.payments.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class BookingServiceException extends ResponseStatusException {

    private final ErrorCodes errorCode;

    public BookingServiceException(HttpStatus status, String reason, ErrorCodes errorCode) {
        super(status, reason);
        this.errorCode = errorCode;
    }
}
