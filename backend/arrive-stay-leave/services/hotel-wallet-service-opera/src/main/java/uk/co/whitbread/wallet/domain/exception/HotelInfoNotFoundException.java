package uk.co.whitbread.wallet.domain.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.wallet.ErrorCode;

public class HotelInfoNotFoundException extends AbstractInternalException {

  public HotelInfoNotFoundException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
