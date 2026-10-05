package uk.co.whitbread.hotel.account.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class PibaGuidException extends AbstractMALException implements MAL400HttpException {

    public static final String ERROR_CODE = "2604";
    private static final long serialVersionUID = -4363559236806279712L;
    public static String DEFAULT_ERROR_MESSAGE = "Error retrieving tetheredGuids";

    public PibaGuidException() {
        super(DEFAULT_ERROR_MESSAGE);
    }


    public PibaGuidException(String message) {
        super(message);
    }


    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

}
