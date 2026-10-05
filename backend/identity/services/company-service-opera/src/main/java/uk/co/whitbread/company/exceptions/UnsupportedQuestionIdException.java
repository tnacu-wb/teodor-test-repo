package uk.co.whitbread.company.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class UnsupportedQuestionIdException extends AbstractMALException implements MAL400HttpException {
    private static final String ERROR_CODE = "901";

    public UnsupportedQuestionIdException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
