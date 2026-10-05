package uk.co.whitbread.piba.account.exception;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL500HttpException;

public class TransactionCSVListException extends AbstractMALException implements MAL500HttpException {

    private static final String ERROR_CODE = "2511";

    public TransactionCSVListException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
