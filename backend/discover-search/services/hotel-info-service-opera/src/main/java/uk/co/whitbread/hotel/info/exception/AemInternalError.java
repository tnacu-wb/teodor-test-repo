package uk.co.whitbread.hotel.info.exception;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL500HttpException;

public class AemInternalError extends AbstractMALException implements MAL500HttpException {

    public static final String REMOTE_CALL_ERROR_CODE = "041";

    public AemInternalError(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getErrorCode() {
        return REMOTE_CALL_ERROR_CODE;
    }
}