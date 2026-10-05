package uk.co.whitbread.hotel.card.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL500HttpException;

public class CardHolderInvalidMobileException extends AbstractMALException implements
    MAL500HttpException {
    public static final String ERROR_CODE = "616";

    public CardHolderInvalidMobileException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
