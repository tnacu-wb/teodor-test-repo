package uk.co.whitbread.piba.account.exception;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class InvalidRequestException extends AbstractMALException implements MAL400HttpException {

    public static final String ERROR_CODE = "2604";

    public InvalidRequestException(final String checkErrorMessage) {
        super(checkErrorMessage);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
