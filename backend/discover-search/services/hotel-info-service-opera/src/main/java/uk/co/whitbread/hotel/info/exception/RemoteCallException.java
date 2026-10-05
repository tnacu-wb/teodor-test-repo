package uk.co.whitbread.hotel.info.exception;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL404HttpException;

public class RemoteCallException extends AbstractMALException implements MAL404HttpException {

    public static final String REMOTE_CALL_ERROR_CODE = "100";

    public RemoteCallException(String message, Throwable cause) {
        super(message, cause);
    }
    public RemoteCallException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getErrorCode() {
        return REMOTE_CALL_ERROR_CODE;
    }
}
