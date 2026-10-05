package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class CdhBookingInvoiceException extends AbstractInternalException {

  public CdhBookingInvoiceException(String globalErrTextTemplate, String debugMessage,
      int errorCode) {
    super(globalErrTextTemplate, debugMessage, errorCode);
  }
}