package uk.co.whitbread.booking.infrastructure.rest.client.aws.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class S3UploadException extends AbstractInternalException {

  public S3UploadException(String message, String debugMessage, int errorCode) {
    super(message, debugMessage, errorCode);
  }
}
