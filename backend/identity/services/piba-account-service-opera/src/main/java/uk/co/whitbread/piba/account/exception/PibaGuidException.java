package uk.co.whitbread.piba.account.exception;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class PibaGuidException extends AbstractMALException implements MAL400HttpException {
    
    private static final long serialVersionUID = -4363559236806279712L;
    
    public static final String DEFAULT_ERROR_MESSAGE = "Error retrieving tetheredGuids";
    
    public static final String ERROR_CODE = "2604";
    
    public PibaGuidException() {
        super(DEFAULT_ERROR_MESSAGE);
    }
    
    
    public PibaGuidException(String message) {
        super(message);
    }
    
    
    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

}
