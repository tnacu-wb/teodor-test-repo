package uk.co.whitbread.hotel.account.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;

public class StaysFilterException extends AbstractMALException {
    public static final String ERROR_CODE = "039";

    public StaysFilterException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
