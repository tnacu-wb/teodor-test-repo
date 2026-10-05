package uk.co.whitbread.employee.bulk.exception;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL500HttpException;

public class GetEmployeeCSVListException extends AbstractMALException implements MAL500HttpException {

    private static final String ERROR_CODE = "2511";

    public GetEmployeeCSVListException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
