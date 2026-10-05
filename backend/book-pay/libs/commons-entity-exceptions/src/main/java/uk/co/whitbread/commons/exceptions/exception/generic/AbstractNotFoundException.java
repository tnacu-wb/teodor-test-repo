package uk.co.whitbread.commons.exceptions.exception.generic;

import uk.co.whitbread.commons.exceptions.exception.ExceptionInterface;

/**
 * Generic exception that all microservice exceptions should extend in order to be treated by the
 * common exception handler.
 */
public abstract class AbstractNotFoundException extends RuntimeException implements
    ExceptionInterface {

  private final String debugMessage;
  private final String globalErrTextTemplate;

  private final int errorCode;

  /**
   * Constructs a new AbstractNotFoundException with the specified custom message, debug message and
   * the error code.
   *
   * @param message      the custom message
   * @param debugMessage the original message from the external calls or “debug message” for the
   *                     error response that are returned by boundary services, containing the error
   *                     thrown by the 3rd party application
   * @param errorCode    The error code range: PMS : 900-999; AEM : 800-899; Databases : 700-799;
   *                     Payments : 600-699; BAU & Other external systems : 500-599; Validation
   *                     errors : 400-499; Digital: 00-299
   */
  public AbstractNotFoundException(String message, String debugMessage, int errorCode) {
    super(debugMessage);
    this.globalErrTextTemplate = message;
    this.debugMessage = debugMessage;
    this.errorCode = errorCode;
  }

  /**
   * Constructs a new AbstractNotFoundException with the specified custom message, debug message,
   * cause and the error code.
   *
   * @param message      the custom message
   * @param debugMessage the original message from the external calls or “debug message” for the
   *                     error response that are returned by boundary services, containing the error
   *                     thrown by the 3rd party application
   * @param cause        the cause
   * @param errorCode    The error code range: PMS : 900-999; AEM : 800-899; Databases : 700-799;
   *                     Payments : 600-699; BAU & Other external systems : 500-599; Validation
   *                     errors : 400-499; Digital: 00-299
   */
  public AbstractNotFoundException(String message, String debugMessage, Throwable cause,
      int errorCode) {
    super(debugMessage, cause);
    this.globalErrTextTemplate = message;
    this.debugMessage = debugMessage;
    this.errorCode = errorCode;
  }

  @Override
  public String getDebugMessage() {
    return debugMessage;
  }

  @Override
  public String getGlobalErrTextTemplate() {
    return globalErrTextTemplate;
  }

  @Override
  public int getErrorCode() {
    return errorCode;
  }
}
