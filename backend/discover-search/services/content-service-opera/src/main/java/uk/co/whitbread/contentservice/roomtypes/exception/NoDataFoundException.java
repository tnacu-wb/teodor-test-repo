package uk.co.whitbread.contentservice.roomtypes.exception;

import lombok.Data;
import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL404HttpException;

@Data
public class NoDataFoundException extends AbstractMALException implements MAL404HttpException {

    private String errorCode = "8008";

    public NoDataFoundException(String message) {
        super(message);
    }

    public NoDataFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
