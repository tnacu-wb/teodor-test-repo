package uk.co.whitbread.commons.exceptions.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Validation errors map.The Error's message is used by the globalTxtErrTemplate.
 */

@RequiredArgsConstructor
@Getter
public enum ValidationError {
  GENERIC_EXCEPTION("generic.server.exception", 400),
  GENERIC_VALIDATION_EXCEPTION("validation.error.form", 401),
  GENERIC_BINDING_EXCEPTION("validation.error.form", 402),
  GENERIC_UNKNOWN_EXCEPTION("unknown.exception", 403),
  GENERIC_CONSTRAINT_VIOLATION_EXCEPTION("constraint.violation.error", 404),
  HTTP_MEDIA_TYPE_NOT_SUPPORTED_EXCEPTION("unsupported.media.error.exception", 405),
  HTTP_MESSAGE_NOT_READABLE_EXCEPTION("message.not.readable.exception", 406),
  TYPE_MISMATCH_EXCEPTION("type.mismatch.exception", 407),
  HTTP_REQUEST_NOT_SUPPORTED_EXCEPTION("http.request.method.not.supported", 408),
  MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION("missing.servlet.request.parameter", 409),
  MISSING_PATH_VARIABLE_EXCEPTION("missing.path.variable.exception", 410),
  HTTP_MESSAGE_NOT_WRITABLE_EXCEPTION("http.meesage.not.writable.exception", 411),
  HTTP_MEDIA_TYPE_NOT_ACCEPTABLE_EXCEPTION("http.media.type.not.acceptable.exception", 412),
  NO_RESOURCE_FOUND_EXCEPTION("resource.not.found.exception", 413),
  NO_HANDLER_FOUND_EXCEPTION("handler.not.found.exception", 414);

  private final String message;
  private final int errorCode;
}
