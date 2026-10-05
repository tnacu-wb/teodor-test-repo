package uk.co.whitbread.contentservice.roomtypes.exception;

import lombok.Data;
import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL404HttpException;

@Data
public class NoBookingNotificationsDataFoundException extends AbstractMALException implements MAL404HttpException {

    private String errorCode = "8009";

    public NoBookingNotificationsDataFoundException(String message) {
        super(message);
    }

    public NoBookingNotificationsDataFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
