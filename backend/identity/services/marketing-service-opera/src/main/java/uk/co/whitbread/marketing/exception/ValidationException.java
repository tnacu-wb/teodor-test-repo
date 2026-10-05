package uk.co.whitbread.marketing.exception;

import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class ValidationException extends RuntimeException implements MAL400HttpException {

    private final String message;

    public ValidationException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getErrorCode() {
        return "001";
    }
}
