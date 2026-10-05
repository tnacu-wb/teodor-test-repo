package uk.co.whitbread.company.employee.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class HotelAccountClientException extends AbstractMALException implements MAL400HttpException {
    private static final String ERROR_CODE = "001";

    public HotelAccountClientException(String message, Throwable throwable) {
        super(message, throwable);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}
