package uk.co.whitbread.reservation.domain.exceptions;

import lombok.Getter;

@Getter
public enum ErrorCode {
  GENERIC_EXCEPTION("internal.server.exception"),
  GENERIC_VALIDATION_EXCEPTION("generic.validation.exception");
  private final String code;

  ErrorCode(final String code) {
    this.code = code;
  }

}
