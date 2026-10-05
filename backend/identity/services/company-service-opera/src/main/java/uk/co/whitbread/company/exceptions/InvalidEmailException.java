package uk.co.whitbread.company.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class InvalidEmailException extends AbstractMALException implements MAL400HttpException {
    private static final String ERROR_CODE = "905";

    public InvalidEmailException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
