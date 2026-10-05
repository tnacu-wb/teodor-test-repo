package uk.co.whitbread.company.employee.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class RemoveMainContactRestrictedOperationException extends AbstractMALException implements MAL400HttpException {
    public static final String ERROR_CODE = "2519";

    public RemoveMainContactRestrictedOperationException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
