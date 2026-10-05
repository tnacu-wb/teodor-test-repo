package uk.co.whitbread.common.exceptions.http;

/**
 * Created by KrakenDevTeam on 02/12/2016.
 */
public interface MAL401HttpException extends MALHttpException {
    default int getStatus(){
        return 401;
    }
}
