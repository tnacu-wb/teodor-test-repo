package uk.co.whitbread.contentservice.roomtypes.exception;

/**
 * Exception thrown by the Feign error decoder for non-5xx responses
 * to prevent the circuit breaker from counting them as failures.
 */
public class FeignNonServerException extends RuntimeException {

    public FeignNonServerException(String message) {
        super(message);
    }
}
