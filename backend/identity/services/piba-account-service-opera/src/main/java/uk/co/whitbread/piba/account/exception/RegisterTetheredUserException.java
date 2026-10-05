package uk.co.whitbread.piba.account.exception;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class RegisterTetheredUserException extends AbstractMALException implements MAL400HttpException {


    public static final String DEFAULT_ERROR_MESSAGE = "Error registering tethered user";

    public static final String ERROR_CODE = "2605";

    public RegisterTetheredUserException() {
        super(DEFAULT_ERROR_MESSAGE);
    }
    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

}
