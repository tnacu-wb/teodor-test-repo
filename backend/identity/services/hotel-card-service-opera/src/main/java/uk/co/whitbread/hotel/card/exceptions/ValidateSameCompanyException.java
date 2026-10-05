package uk.co.whitbread.hotel.card.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class ValidateSameCompanyException extends AbstractMALException implements
    MAL400HttpException {

    public static final String ERROR_CODE = "610";
    public static final String ERROR_MESSAGE = "The company is not the same as the one in the request";

    public ValidateSameCompanyException() {
        super(ERROR_MESSAGE);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
