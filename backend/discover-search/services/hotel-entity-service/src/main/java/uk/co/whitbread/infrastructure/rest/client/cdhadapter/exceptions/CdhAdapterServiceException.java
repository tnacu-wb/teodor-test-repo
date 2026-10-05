package uk.co.whitbread.infrastructure.rest.client.cdhadapter.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.domain.exceptions.ErrorCode;

public class CdhAdapterServiceException extends AbstractInternalException {

  public CdhAdapterServiceException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }

  public CdhAdapterServiceException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}