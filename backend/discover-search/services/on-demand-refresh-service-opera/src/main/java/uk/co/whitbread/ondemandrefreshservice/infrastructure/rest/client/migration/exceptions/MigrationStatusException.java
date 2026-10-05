package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.migration.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.ondemandrefreshservice.ErrorCode;

public class MigrationStatusException extends AbstractInternalException {

  public MigrationStatusException(ErrorCode error, String debugMessage) {
    super(debugMessage, error.getMessage(), error.getCode());
  }
}