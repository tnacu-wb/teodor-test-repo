package uk.co.whitbread.common.exceptions.http;

import uk.co.whitbread.common.exceptions.MALException;

/**
 * Created by KrakenDevTeam on 02/12/2016.
 */
public interface MALHttpException extends MALException {
    int getStatus();
}
