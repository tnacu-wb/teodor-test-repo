package uk.co.whitbread.hotel.card.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL500HttpException;

public class ThreeCPClientException extends AbstractMALException implements MAL500HttpException {
    private static final String DEFAULT_ERROR_CODE = "040";

    public ThreeCPClientException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() { return DEFAULT_ERROR_CODE; }
}
