package uk.co.whitbread.hotel.card.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL500HttpException;

public class PibaAccountClientException extends AbstractMALException implements MAL500HttpException {
    private static final String DEFAULT_ERROR_CODE = "613";

    public PibaAccountClientException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() { return DEFAULT_ERROR_CODE; }
}
