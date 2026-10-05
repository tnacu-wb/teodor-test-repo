package uk.co.whitbread.hotel.register.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class CdhServiceException extends AbstractMALException implements MAL400HttpException {

    private static final String GENERIC_CDH_ERROR_CODE = "7008";

    public CdhServiceException(String message){
        super(message);
    }

    @Override
    public String getErrorCode() {
        return GENERIC_CDH_ERROR_CODE;
    }
}
