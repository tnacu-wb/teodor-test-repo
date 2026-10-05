package uk.co.whitbread.common.exceptions;

/**
 * Abstract exception that has an error code which can be mapped to the specific exception.
 * <p>
 * See the 'Microservice Error Codes' confluence page for the list of error codes, and to add
 * any new ones when extending this exception.
 *
 * @see <a href="https://whitbreadis.atlassian.net/wiki/display/MID/Microservice+Error+Codes">Microservice Error Codes</a>
 * <p>
 * Created by Oleksandr Murha on 04/11/2016.
 */
public abstract class AbstractMALException extends RuntimeException implements MALException {

    public AbstractMALException() {
    }

    public AbstractMALException(String message) {
        super(message);
    }

    public AbstractMALException(String message, Throwable cause) {
        super(message, cause);
    }

    public AbstractMALException(Throwable cause) {
        super(cause);
    }

    public AbstractMALException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
