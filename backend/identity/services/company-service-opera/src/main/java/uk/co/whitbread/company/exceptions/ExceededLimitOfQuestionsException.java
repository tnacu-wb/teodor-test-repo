package uk.co.whitbread.company.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL403HttpException;

public class ExceededLimitOfQuestionsException extends AbstractMALException implements MAL403HttpException {
    private static final String ERROR_CODE = "900";

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

    public ExceededLimitOfQuestionsException() {
        super("Limit of questions has been reached!");
    }
}
