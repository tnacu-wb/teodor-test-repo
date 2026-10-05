package uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions;

public class OnDemandRefreshDbException extends RuntimeException{
  public OnDemandRefreshDbException(String message, Throwable cause) {
    super(message, cause);
  }

  public OnDemandRefreshDbException(String message) {
    super(message);
  }
}
