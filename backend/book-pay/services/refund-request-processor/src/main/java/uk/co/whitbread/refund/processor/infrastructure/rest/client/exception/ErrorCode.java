package uk.co.whitbread.refund.processor.infrastructure.rest.client.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Errors map.The Error's message is used by the globalTxtErrTemplate.
 */

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  SEND_FULL_REFUND_INVALID_PAYMENT_EXCEPTION(Constants.BUSINESS_COMPANY_ERROR, 608),
  SEND_FULL_REFUND_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 609),
  INVALID_PAYMENT_RESPONSE_EXCEPTION(Constants.BUSINESS_COMPANY_ERROR, 610),
  PAYMENT_RESPONSE_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 611),
  TOKEN_REFUND_INVALID_EXCEPTION(Constants.BUSINESS_COMPANY_ERROR, 612),
  TOKEN_REFUND_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 613);

  private final String message;
  private final int code;

  public static class Constants {

    public static final String INTERNAL_SERVER_EXCEPTION = "internal.server.exception";
    public static final String BUSINESS_COMPANY_ERROR = "business.company.error";

    private Constants() {
    }
  }
}
