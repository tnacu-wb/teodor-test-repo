package uk.co.whitbread.review.infrastructure.rest.client.review.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Errors map.The Error's message is used by the globalTxtErrTemplate.
 */

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

  TRIPADVISOR_DATA_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 536),
  TRIPADVISOR_REVIEWS_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 537);


  private final String message;
  private final int code;

  private static class Constants {

    public static final String INTERNAL_SERVER_EXCEPTION = "internal.server.exception";
  }
}