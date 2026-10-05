package uk.co.whitbread.hotel.account.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class InvalidSessionException extends AbstractMALException implements MAL400HttpException {

    private static final String ERROR_CODE = "013";

    public InvalidSessionException() {
        super("Invalid/Missing request header 'Authorization'");
    }

    public InvalidSessionException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
