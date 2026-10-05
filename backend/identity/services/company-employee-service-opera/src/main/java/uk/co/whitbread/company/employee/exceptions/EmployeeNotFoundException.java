package uk.co.whitbread.company.employee.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL404HttpException;

public class EmployeeNotFoundException extends AbstractMALException implements MAL404HttpException {
    public static final String ERROR_CODE = "2515";

    public EmployeeNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
