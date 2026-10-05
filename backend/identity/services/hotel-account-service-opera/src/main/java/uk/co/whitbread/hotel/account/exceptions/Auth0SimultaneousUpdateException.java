package uk.co.whitbread.hotel.account.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class Auth0SimultaneousUpdateException extends AbstractMALException implements MAL400HttpException {

    private static final String ERROR_CODE = "7100";

    public Auth0SimultaneousUpdateException(String message) {
        super(message);
    }

    public Auth0SimultaneousUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

}