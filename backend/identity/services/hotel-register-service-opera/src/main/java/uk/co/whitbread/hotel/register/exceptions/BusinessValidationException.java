package uk.co.whitbread.hotel.register.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class BusinessValidationException extends AbstractMALException implements MAL400HttpException {

    private static final String VALIDATION_ERROR_CODE = "053";

    public BusinessValidationException(String message){
        super(message);
    }

    @Override
    public String getErrorCode() {
        return VALIDATION_ERROR_CODE;
    }
}
