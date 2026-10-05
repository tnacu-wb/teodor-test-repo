package uk.co.whitbread.hotel.card.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL404HttpException;

public class WorldlineNotFoundException extends AbstractMALException implements
    MAL404HttpException {

    private static final String ERROR_CODE = "609";

    public WorldlineNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
