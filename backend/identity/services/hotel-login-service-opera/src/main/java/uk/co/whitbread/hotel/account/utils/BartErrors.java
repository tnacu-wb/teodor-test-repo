package uk.co.whitbread.hotel.account.utils;

/**
 * Created by KrakenDevTeam on 20/01/2017.
 */
public interface BartErrors {
    String BART_INVOCATION_ERROR_CODE = "011";
    String BART_RETURNED_ERROR_CODE = "012";
    String BART_CREDENTIALS_ERROR_CODE = "000";

    String BART_INVALID_LOGIN_FAULTCODE = "94";
    String BART_INVALID_SESSION_KEY = "SESSION_ID_NOT_FOUND";
}
