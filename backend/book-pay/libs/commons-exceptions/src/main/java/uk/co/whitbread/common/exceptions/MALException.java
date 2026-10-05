package uk.co.whitbread.common.exceptions;

/**
 * Created by KrakenDevTeam on 02/12/2016.
 */
public interface MALException {

    /**
     * This method mirrors the * {@link Throwable#getMessage()} method
     * @return
     */
    String getMessage();
    /**
     * All concrete subclasses must implement this, returning an error code unique to them.
     * The returned error could should be found on the 'Microservice Error Codes' confluence page,
     * with all appropriate information, including the microservice, exception class name and a description.
     *
     * @return The error code unique to the exception.
     */
    String getErrorCode();
}
