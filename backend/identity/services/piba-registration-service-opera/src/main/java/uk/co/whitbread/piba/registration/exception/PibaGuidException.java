package uk.co.whitbread.piba.registration.exception;

import uk.co.whitbread.piba.api.exception.PibaException;
@SuppressWarnings("squid:S110") //Suppressing "Inheritance tree of classes should not be too deep" as this is a valid use case
public class PibaGuidException extends PibaException {
    
    private static final long serialVersionUID = -4363559236806279712L;
    
    private static final String DEFAULT_ERROR_MESSAGE = "Error saving tether guid information";
    
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
