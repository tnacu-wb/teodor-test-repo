package uk.co.whitbread.employee.bulk.exception;

import java.util.List;
import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class BulkEmployeeUploadException extends AbstractMALException implements MAL400HttpException {
    private static final String ERROR_CODE = "14003";
    private final List<ValidationError> errorExceptions;

    public BulkEmployeeUploadException(List<ValidationError> errorExceptions) {
        super("Bulk employee upload validation failed with " + errorExceptions.size() + " error(s)");
        this.errorExceptions = errorExceptions;
    }

    public List<ValidationError> getErrorExceptions() {
        return errorExceptions;
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
