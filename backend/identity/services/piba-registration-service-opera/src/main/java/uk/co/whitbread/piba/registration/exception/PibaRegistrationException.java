package uk.co.whitbread.piba.registration.exception;

import uk.co.whitbread.piba.api.exception.PibaException;
@SuppressWarnings("squid:S110") //Suppressing "Inheritance tree of classes should not be too deep" as this is a valid use case
public class PibaRegistrationException extends PibaException {
    public static final String ERROR_CODE = "2602";

    public PibaRegistrationException(String message) {
        super(message);
        withErrorCode(ERROR_CODE);
    }

    @Override
    public PibaRegistrationException withErrorCode(String errorCode) {
        super.withErrorCode(errorCode);
        return this;
    }


}
