package uk.co.whitbread.digitalkey;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Errors map.The Error's message is used by the globalTxtErrTemplate.
 */

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

  ALLIANTS_INTERNAL_ERROR(Constants.ALLIANTS_INTERNAL_ERROR, 500),
  LOCATION_NOT_FOUND(Constants.LOCATION_NOT_FOUND, 404),
  PROFILE_NOT_FOUND(Constants.PROFILE_NOT_FOUND, 404),
  BOOKING_NOT_FOUND(Constants.BOOKING_NOT_FOUND, 404),
  INVALID_CODE(Constants.INVALID_CODE, 400),
  RESERVATION_NOT_FOUND(Constants.RESERVATION_NOT_FOUND, 500),
  NO_ROOMS_AVAILABLE(Constants.INVALID_ROOM_NUMBER, 500),
  CHECKIN_FAILED(Constants.CHECK_IN_EXCEPTION, 500),
  ALLIANTS_BAD_REQUEST(Constants.BAD_REQUEST, 546);

  private final String message;
  private final int code;

  public static class Constants {

    public static final String RESERVATION_NOT_FOUND = "No reservation found for the provided details.";
    public static final String INVALID_ROOM_NUMBER = "The room number provided is invalid or does not exist.";
    public static final String ALLIANTS_INTERNAL_ERROR = "ALLIANTS_INTERNAL_ERROR";
    public static final String LOCATION_NOT_FOUND = "LOCATION_NOT_FOUND";
    public static final String PROFILE_NOT_FOUND = "PROFILE_NOT_FOUND";
    public static final String BOOKING_NOT_FOUND = "BOOKING_NOT_FOUND";
    public static final String INVALID_CODE = "INVALID_CODE_OR_EXPIRED_CODE";
    public static final String CHECK_IN_EXCEPTION = "Something went wrong. Please try again.";
    public static final String BAD_REQUEST = "BAD_REQUEST";

    private Constants() {
      throw new IllegalStateException("Constants class");
    }
  }

}
