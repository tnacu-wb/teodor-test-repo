package uk.co.whitbread.hotel.captcha.exception;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class CypherVerificationException extends AbstractMALException implements MAL400HttpException {

    private static final String ERROR_CODE = "7006";

    public CypherVerificationException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}