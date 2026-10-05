package uk.co.whitbread.piba.account.exception;

import uk.co.whitbread.piba.api.exception.PibaException;

public class PibaAccountException extends PibaException {
    public static final String ERROR_CODE = "2603";
    
    
    public PibaAccountException(String message) {
        super(message);
        withErrorCode(ERROR_CODE);
    }
    
    
    @Override
    public PibaAccountException withErrorCode(String errorCode) {
        super.withErrorCode(errorCode);
        return this;
    }
}
