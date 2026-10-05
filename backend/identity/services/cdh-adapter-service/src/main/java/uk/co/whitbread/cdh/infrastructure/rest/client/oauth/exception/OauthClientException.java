package uk.co.whitbread.cdh.infrastructure.rest.client.oauth.exception;


import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.ErrorCode;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class OauthClientException extends AbstractInternalException {

  public OauthClientException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
