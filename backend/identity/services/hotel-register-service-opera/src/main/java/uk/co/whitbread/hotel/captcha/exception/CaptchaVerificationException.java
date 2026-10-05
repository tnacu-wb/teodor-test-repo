package uk.co.whitbread.hotel.captcha.exception;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL401HttpException;

public class CaptchaVerificationException extends AbstractMALException implements MAL401HttpException {

    private static final String ERROR_CODE = "042";

    public CaptchaVerificationException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}