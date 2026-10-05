package uk.co.whitbread.hotel.account.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.MALWarning;
import uk.co.whitbread.common.exceptions.http.MAL401HttpException;

import static uk.co.whitbread.hotel.account.exceptions.ErrorCodes.INVALID_LOGIN_ERROR_CODE;

/**
 * Created by Abu-Taleb on 11/05/2017.
 */
public class InvalidLoginException extends AbstractMALException implements MAL401HttpException, MALWarning{

    public InvalidLoginException(String message) {
        super(message);
    }

    public InvalidLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return INVALID_LOGIN_ERROR_CODE.getCode();
    }
}
