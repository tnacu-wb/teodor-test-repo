package uk.co.whitbread.shared.auth.exception;

import lombok.Getter;

/**
 * Replaces {@code com.auth0.exception.Auth0Exception}.
 * Thrown when an Auth0 Management API call returns an error response.
 */
@Getter
public class Auth0ApiException extends RuntimeException {

    private final int statusCode;
    private final String errorCode;
    private final String description;

    public Auth0ApiException(String message, int statusCode, String errorCode, String description) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.description = description;
    }

    public Auth0ApiException(String message, int statusCode, String errorCode, String description,
                             Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.description = description;
    }

    public Auth0ApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
        this.errorCode = null;
        this.description = message;
    }
}
