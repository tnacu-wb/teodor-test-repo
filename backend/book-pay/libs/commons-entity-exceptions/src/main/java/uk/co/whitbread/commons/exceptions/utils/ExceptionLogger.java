package uk.co.whitbread.commons.exceptions.utils;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import uk.co.whitbread.commons.exceptions.exception.ExceptionInterface;

@UtilityClass
public class ExceptionLogger {

  private static final String BUSINESS_MESSAGE = "Business exception error code: %s , message: %s , debug message: %s";
  private static final String GENERAL_MESSAGE = "Exception occurred: ";
  private static final String BUSINESS_MESSAGE_WITH_TEXT =
      "Business exception error code: %s , message: %s , debug message : %s, reason: %s";


  public static void log(Logger log, Throwable throwable) {

    if (throwable instanceof ExceptionInterface) {
      var businessException = (ExceptionInterface) throwable;
      var message = String.format(
          BUSINESS_MESSAGE,
          businessException.getErrorCode(),
          businessException.getGlobalErrTextTemplate(),
          businessException.getDebugMessage());
      if (throwable.getCause() == null) {
        log.error(message);
        return;
      }
      log.error(message, throwable.getCause());
    } else {
      log.error(GENERAL_MESSAGE, throwable);
    }
  }

  public static void log(Logger log, Throwable throwable, String errorMessage) {

    if (throwable instanceof ExceptionInterface) {
      var businessException = (ExceptionInterface) throwable;
      var message = String.format(
          BUSINESS_MESSAGE_WITH_TEXT,
          businessException.getErrorCode(),
          businessException.getGlobalErrTextTemplate(),
          businessException.getDebugMessage(),
          errorMessage);
      if (throwable.getCause() == null) {
        log.error(message);
        return;
      }
      log.error(message, throwable.getCause());
    } else {
      log.error(GENERAL_MESSAGE, throwable);
    }
  }
}
