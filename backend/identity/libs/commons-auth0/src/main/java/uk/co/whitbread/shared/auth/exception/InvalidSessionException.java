package uk.co.whitbread.shared.auth.exception;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class InvalidSessionException extends AbstractMALException implements MAL400HttpException {

    private static final String ERROR_CODE = "013";

    public InvalidSessionException() {
        super();
    }

    public InvalidSessionException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
