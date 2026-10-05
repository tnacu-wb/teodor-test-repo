package uk.co.whitbread.hotelcountries.exception;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL500HttpException;

public class AEMServiceException extends AbstractMALException implements MAL500HttpException {
    public static final String ERROR_CODE = "003";

    public AEMServiceException(String message) {
        super(message);
    }

    public AEMServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}