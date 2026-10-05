package uk.co.whitbread.hotel.info.exception;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class ValidationException extends AbstractMALException implements MAL400HttpException {

    private static final String VALIDATION_ERROR_CODE = "001";

    public ValidationException(String message) {
        super(message);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getErrorCode() {
        return VALIDATION_ERROR_CODE;
    }
}
