package uk.co.whitbread.booking.infrastructure.rest.client.booking.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class BookingInvoiceException extends AbstractInternalException {

  public BookingInvoiceException(String message, String debugMessage, int errCode) {
    super(message, debugMessage, errCode);
  }
}
