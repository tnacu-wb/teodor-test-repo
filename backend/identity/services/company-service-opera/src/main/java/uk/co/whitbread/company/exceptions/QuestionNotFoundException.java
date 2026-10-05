package uk.co.whitbread.company.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL404HttpException;

public class QuestionNotFoundException extends AbstractMALException implements MAL404HttpException {
    private static final String ERROR_CODE = "902";

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
