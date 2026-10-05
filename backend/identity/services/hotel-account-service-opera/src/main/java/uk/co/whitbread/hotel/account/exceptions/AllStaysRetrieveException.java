package uk.co.whitbread.hotel.account.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;

public class AllStaysRetrieveException extends AbstractMALException {

    private static final String DEFAULT_ERROR_CODE = "033";

    public AllStaysRetrieveException(String message) {
        super(message);
    }


    public String getErrorCode() {
        return DEFAULT_ERROR_CODE;
    }


}
