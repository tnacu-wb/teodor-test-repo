package uk.co.whitbread.shared.auth.exception;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.MALWarning;
import uk.co.whitbread.common.exceptions.http.MAL401HttpException;
import uk.co.whitbread.shared.auth.constants.ErrorCodes;

public class InvalidLoginException extends AbstractMALException implements MAL401HttpException, MALWarning {

    public InvalidLoginException(String message) {
        super(message);
    }

    public InvalidLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return ErrorCodes.INVALID_LOGIN_ERROR_CODE.getCode();
    }
}
