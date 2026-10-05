package uk.co.whitbread.hotel.card.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL500HttpException;

public class CardHolderNotCreatedException extends AbstractMALException implements
    MAL500HttpException {
    public static final String ERROR_CODE = "612";

    public CardHolderNotCreatedException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
