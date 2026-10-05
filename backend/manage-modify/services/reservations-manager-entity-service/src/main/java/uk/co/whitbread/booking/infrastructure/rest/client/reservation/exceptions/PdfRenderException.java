package uk.co.whitbread.booking.infrastructure.rest.client.reservation.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class PdfRenderException extends AbstractInternalException {

  public PdfRenderException(String message, String debugMessage, int errorCode) {
    super(message, debugMessage, errorCode);
  }
}