package uk.co.whitbread.kiosk.domain.logic.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.kiosk.ErrorCode;

public class BalanceNotPaidException extends AbstractInternalException {

  public BalanceNotPaidException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }

}
