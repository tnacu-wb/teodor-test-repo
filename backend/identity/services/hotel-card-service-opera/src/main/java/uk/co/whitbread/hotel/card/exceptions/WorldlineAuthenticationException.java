package uk.co.whitbread.hotel.card.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL401HttpException;

/**
 * Exception thrown when the Worldline API returns a 401 Unauthorized response,
 * indicating that the credentials used to authenticate with Worldline were rejected.
 */
public class WorldlineAuthenticationException extends AbstractMALException implements MAL401HttpException {

    private static final String DEFAULT_ERROR_CODE = "612";

    public WorldlineAuthenticationException(String message) {
        super(message);
    }

    public WorldlineAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return DEFAULT_ERROR_CODE;
    }
}
