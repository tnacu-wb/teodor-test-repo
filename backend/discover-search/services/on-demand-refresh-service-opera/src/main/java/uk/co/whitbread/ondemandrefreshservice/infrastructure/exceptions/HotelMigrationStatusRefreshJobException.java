package uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions;

import org.springframework.http.HttpStatus;

public class HotelMigrationStatusRefreshJobException extends RuntimeException {

    private HttpStatus status;

    public HotelMigrationStatusRefreshJobException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HotelMigrationStatusRefreshJobException(String message, Throwable cause) {
        super(message, cause);
    }

    public HotelMigrationStatusRefreshJobException(String message) {
        super(message);
    }

    public HttpStatus getStatus() {
        return status;
    }

}
