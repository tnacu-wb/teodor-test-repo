package uk.co.whitbread.hotel.account.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class EmployeeUpdate400Exception extends AbstractMALException implements MAL400HttpException {
    private static final String DEFAULT_ERROR_CODE = "050";

    public EmployeeUpdate400Exception(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return DEFAULT_ERROR_CODE;
    }
}