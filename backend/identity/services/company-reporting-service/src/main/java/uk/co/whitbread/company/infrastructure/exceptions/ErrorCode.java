package uk.co.whitbread.company.infrastructure.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

  REPORT_NOT_FOUND("reporting.error.reportNotFound"),
  REPORT_LARGE_CONTENT("reporting.error.reportLargeContent"),
  INTERNAL_SERVER_ERROR("reporting.error.InternalServerError"),
  UNAUTHORIZED_EXCEPTION("reporting.error.unauthorized"),
  DATE_RANGE_EXCEPTION("reporting.error.dateRange");
  private final String code;
}
