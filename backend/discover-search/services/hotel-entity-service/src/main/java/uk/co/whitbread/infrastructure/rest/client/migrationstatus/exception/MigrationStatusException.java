package uk.co.whitbread.infrastructure.rest.client.migrationstatus.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.domain.exceptions.ErrorCode;

public class MigrationStatusException extends AbstractInternalException {
  public MigrationStatusException(String message, String debugMessage, Throwable clause,
                                 int errCode) {
    super(message, debugMessage, clause, errCode);
  }

  public MigrationStatusException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}
