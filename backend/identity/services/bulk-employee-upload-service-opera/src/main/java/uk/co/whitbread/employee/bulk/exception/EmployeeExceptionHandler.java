package uk.co.whitbread.employee.bulk.exception;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;
import uk.co.whitbread.common.exceptions.http.MAL401HttpException;
import uk.co.whitbread.common.exceptions.http.MAL404HttpException;
import uk.co.whitbread.common.exceptions.http.MAL500HttpException;

@RestControllerAdvice
public class EmployeeExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = BulkEmployeeUploadException.class)
    public ResponseEntity<List<ValidationError>> handleBulkEmployeeUploadException(BulkEmployeeUploadException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getErrorExceptions());
    }

    @ResponseBody
    @ExceptionHandler(value = AbstractMALException.class)
    public ResponseEntity<ErrorResponse> handleAbstractMALException(AbstractMALException exception) {
        HttpStatus status = getHttpStatus(exception);
        ErrorResponse errorResponse = new ErrorResponse(exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

    private HttpStatus getHttpStatus(AbstractMALException exception) {
        if (exception instanceof MAL400HttpException) {
            return HttpStatus.BAD_REQUEST;
        } else if (exception instanceof MAL401HttpException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (exception instanceof MAL404HttpException) {
            return HttpStatus.NOT_FOUND;
        } else if (exception instanceof MAL500HttpException) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
