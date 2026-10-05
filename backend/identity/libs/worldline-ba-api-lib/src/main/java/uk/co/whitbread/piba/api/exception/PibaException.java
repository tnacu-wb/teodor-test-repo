package uk.co.whitbread.piba.api.exception;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL500HttpException;

public class PibaException extends AbstractMALException implements MAL500HttpException {
    public static final String ERROR_CODE = "2602";
    private String errorCode = ERROR_CODE;

    public PibaException(String message) {
        super(message);
    }
    @Override
    public String getErrorCode() {
        return errorCode;
    }

    public PibaException withErrorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }
}
