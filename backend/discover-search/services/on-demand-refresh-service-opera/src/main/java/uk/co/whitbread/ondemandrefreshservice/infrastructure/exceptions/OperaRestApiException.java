package uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions;

public class OperaRestApiException extends RuntimeException{

  public OperaRestApiException(String message) {
    super(message);
  }
}
