package uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Errors map.The Error's message is used by the globalTxtErrTemplate.
 */

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

  CDH_MANAGEMENT_INFO_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 528),
  CDH_EMERGENCY_REPORT_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 529),
  CDH_GET_COMPANY_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 530),
  CDH_SEARCH_COMPANY_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 531),
  FEIGN_DECODER_CONTENT_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 532),
  FEIGN_DECODER_PARSE_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 533),
  FEIGN_DECODER_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 534),
  FEIGN_DECODER_UNKNOWN_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 535),
  CDH_GET_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 536),
  CDH_GET_EMPLOYEE_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 542),
  CDH_GET_COMPANY_EMPLOYEES_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 543),
  CDH_POST_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 544);

  private final String message;
  private final int code;

  private static class Constants {

    public static final String INTERNAL_SERVER_EXCEPTION = "internal.server.exception";
  }
}