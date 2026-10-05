package uk.co.whitbread.shared.auth.exception;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL401HttpException;

public class TokenVerificationException extends AbstractMALException implements MAL401HttpException {

    private static final String ERROR_CODE = "035";

    public TokenVerificationException(String message) {
        super(message);
    }

    public TokenVerificationException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
