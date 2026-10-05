package uk.co.whitbread.hotel.account.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL401HttpException;

public class InvalidTokenException extends AbstractMALException implements MAL401HttpException {

    private static final String ERROR_CODE = "7101";

    public InvalidTokenException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
