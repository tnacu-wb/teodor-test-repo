package uk.co.whitbread.hotel.register.exceptions;

import static uk.co.whitbread.hotel.register.exceptions.ErrorCodes.UL_UNAUTHORIZED_ERROR_CODE;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class UnauthorizedULException extends AbstractMALException implements MAL400HttpException {

    public UnauthorizedULException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return UL_UNAUTHORIZED_ERROR_CODE.getCode();
    }
}
