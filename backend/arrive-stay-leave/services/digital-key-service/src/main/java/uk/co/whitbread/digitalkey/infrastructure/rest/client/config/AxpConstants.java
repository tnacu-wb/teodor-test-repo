package uk.co.whitbread.digitalkey.infrastructure.rest.client.config;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AxpConstants {
  public static final String BEARER_PREFIX = "Bearer ";
  public static final String INVALID_OR_EXPIRED_OTP = "INVALID_OR_EXPIRED_OTP";
  public static final String LOCATION_NOT_FOUND = "LOCATION_NOT_FOUND";
  public static final String BOOKING_NOT_FOUND = "BOOKING_NOT_FOUND";
  public static final String PROFILE_NOT_FOUND = "PROFILE_NOT_FOUND";
  public static final String UNHANDLED_AXP_ERROR = "Unhandled AXP error: ";
}

