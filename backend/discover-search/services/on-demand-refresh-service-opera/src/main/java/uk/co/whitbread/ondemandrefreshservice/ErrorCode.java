package uk.co.whitbread.ondemandrefreshservice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  OPERA_DAILY_RATES_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 965),
  OPERA_HOTEL_INVENTORY_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 966),
  OPERA_HOTEL_MIGRATION_STATUS_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 967),
  OPERA_HOTEL_RATE_RESTRICTION_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 968);

  private final String message;
  private final int code;

  private static class Constants {

    public static final String INTERNAL_SERVER_EXCEPTION = "internal.server.exception";
  }
}