package uk.co.whitbread.shared.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.MALAuth0Exception;

public class AuthServiceException extends AbstractMALException implements MALAuth0Exception {

    @Getter
    private String errorCode = "7000";

    @Getter
    private HttpStatus httpStatus;

    public AuthServiceException() {
        super();
    }

    public AuthServiceException(String message) {
        super(message);
    }

    public AuthServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthServiceException(Throwable cause) {
        super(cause);
    }

    protected AuthServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AuthServiceException withErrorCode(String errorCode){
        this.errorCode = errorCode;
        return this;
    }

    public AuthServiceException withHttpStatus(HttpStatus httpStatus){
        this.httpStatus = httpStatus;
        return this;
    }
}
