package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.rates.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.ondemandrefreshservice.ErrorCode;

public class DailyRatesException extends AbstractInternalException {

  public DailyRatesException(ErrorCode error, String debugMessage) {
    super(debugMessage, error.getMessage(), error.getCode());
  }
}