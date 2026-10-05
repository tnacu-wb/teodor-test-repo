package uk.co.whitbread.commons.exceptions.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Base Property Management System Exception.
 */
public interface ExceptionInterface {

  String getDebugMessage();

  String getGlobalErrTextTemplate();

  default Map<String, String> getValidationErrors() {
    return new HashMap<>();
  }

  /**
   * The error code range:
   * PMS : 900-999
   * AEM : 800-899
   * Databases : 700-799
   * Payments : 600-699
   * BAU & Other external systems : 500-599
   * Validation errors : 400-499
   * Digital: 00-299
   */
  int getErrorCode();
}
