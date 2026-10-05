package uk.co.whitbread.employee.bulk.exception;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class BulkEmployeeUploadValidationException extends AbstractMALException implements MAL400HttpException {

    private static final String ERROR_CODE = "2509";

    public BulkEmployeeUploadValidationException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
