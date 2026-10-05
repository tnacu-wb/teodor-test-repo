package uk.co.whitbread.hotel.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.reservation.OhipBadRequestExceptionCause;
import uk.co.whitbread.hotel.generated.models.reservation.OhipBadRequestExceptionCauseStackTrace;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * OhipInternalException
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OhipInternalException {

  private @Nullable OhipBadRequestExceptionCause cause;

  private @Nullable String debugMessage;

  private @Nullable Integer errorCode;

  private @Nullable String globalErrTextTemplate;

  private @Nullable String localizedMessage;

  private @Nullable String message;

  @Valid
  private List<@Valid OhipBadRequestExceptionCauseStackTrace> stackTrace = new ArrayList<>();

  @Valid
  private List<@Valid OhipBadRequestExceptionCause> suppressed = new ArrayList<>();

  @Valid
  private Map<String, String> validationErrors = new HashMap<>();

  public OhipInternalException cause(OhipBadRequestExceptionCause cause) {
    this.cause = cause;
    return this;
  }

  /**
   * Get cause
   * @return cause
   */
  @Valid 
  @Schema(name = "cause", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cause")
  public OhipBadRequestExceptionCause getCause() {
    return cause;
  }

  public void setCause(OhipBadRequestExceptionCause cause) {
    this.cause = cause;
  }

  public OhipInternalException debugMessage(String debugMessage) {
    this.debugMessage = debugMessage;
    return this;
  }

  /**
   * Get debugMessage
   * @return debugMessage
   */
  
  @Schema(name = "debugMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("debugMessage")
  public String getDebugMessage() {
    return debugMessage;
  }

  public void setDebugMessage(String debugMessage) {
    this.debugMessage = debugMessage;
  }

  public OhipInternalException errorCode(Integer errorCode) {
    this.errorCode = errorCode;
    return this;
  }

  /**
   * Get errorCode
   * @return errorCode
   */
  
  @Schema(name = "errorCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("errorCode")
  public Integer getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(Integer errorCode) {
    this.errorCode = errorCode;
  }

  public OhipInternalException globalErrTextTemplate(String globalErrTextTemplate) {
    this.globalErrTextTemplate = globalErrTextTemplate;
    return this;
  }

  /**
   * Get globalErrTextTemplate
   * @return globalErrTextTemplate
   */
  
  @Schema(name = "globalErrTextTemplate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("globalErrTextTemplate")
  public String getGlobalErrTextTemplate() {
    return globalErrTextTemplate;
  }

  public void setGlobalErrTextTemplate(String globalErrTextTemplate) {
    this.globalErrTextTemplate = globalErrTextTemplate;
  }

  public OhipInternalException localizedMessage(String localizedMessage) {
    this.localizedMessage = localizedMessage;
    return this;
  }

  /**
   * Get localizedMessage
   * @return localizedMessage
   */
  
  @Schema(name = "localizedMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("localizedMessage")
  public String getLocalizedMessage() {
    return localizedMessage;
  }

  public void setLocalizedMessage(String localizedMessage) {
    this.localizedMessage = localizedMessage;
  }

  public OhipInternalException message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
   */
  
  @Schema(name = "message", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public OhipInternalException stackTrace(List<@Valid OhipBadRequestExceptionCauseStackTrace> stackTrace) {
    this.stackTrace = stackTrace;
    return this;
  }

  public OhipInternalException addStackTraceItem(OhipBadRequestExceptionCauseStackTrace stackTraceItem) {
    if (this.stackTrace == null) {
      this.stackTrace = new ArrayList<>();
    }
    this.stackTrace.add(stackTraceItem);
    return this;
  }

  /**
   * Get stackTrace
   * @return stackTrace
   */
  @Valid 
  @Schema(name = "stackTrace", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("stackTrace")
  public List<@Valid OhipBadRequestExceptionCauseStackTrace> getStackTrace() {
    return stackTrace;
  }

  public void setStackTrace(List<@Valid OhipBadRequestExceptionCauseStackTrace> stackTrace) {
    this.stackTrace = stackTrace;
  }

  public OhipInternalException suppressed(List<@Valid OhipBadRequestExceptionCause> suppressed) {
    this.suppressed = suppressed;
    return this;
  }

  public OhipInternalException addSuppressedItem(OhipBadRequestExceptionCause suppressedItem) {
    if (this.suppressed == null) {
      this.suppressed = new ArrayList<>();
    }
    this.suppressed.add(suppressedItem);
    return this;
  }

  /**
   * Get suppressed
   * @return suppressed
   */
  @Valid 
  @Schema(name = "suppressed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("suppressed")
  public List<@Valid OhipBadRequestExceptionCause> getSuppressed() {
    return suppressed;
  }

  public void setSuppressed(List<@Valid OhipBadRequestExceptionCause> suppressed) {
    this.suppressed = suppressed;
  }

  public OhipInternalException validationErrors(Map<String, String> validationErrors) {
    this.validationErrors = validationErrors;
    return this;
  }

  public OhipInternalException putValidationErrorsItem(String key, String validationErrorsItem) {
    if (this.validationErrors == null) {
      this.validationErrors = new HashMap<>();
    }
    this.validationErrors.put(key, validationErrorsItem);
    return this;
  }

  /**
   * Get validationErrors
   * @return validationErrors
   */
  
  @Schema(name = "validationErrors", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("validationErrors")
  public Map<String, String> getValidationErrors() {
    return validationErrors;
  }

  public void setValidationErrors(Map<String, String> validationErrors) {
    this.validationErrors = validationErrors;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OhipInternalException ohipInternalException = (OhipInternalException) o;
    return Objects.equals(this.cause, ohipInternalException.cause) &&
        Objects.equals(this.debugMessage, ohipInternalException.debugMessage) &&
        Objects.equals(this.errorCode, ohipInternalException.errorCode) &&
        Objects.equals(this.globalErrTextTemplate, ohipInternalException.globalErrTextTemplate) &&
        Objects.equals(this.localizedMessage, ohipInternalException.localizedMessage) &&
        Objects.equals(this.message, ohipInternalException.message) &&
        Objects.equals(this.stackTrace, ohipInternalException.stackTrace) &&
        Objects.equals(this.suppressed, ohipInternalException.suppressed) &&
        Objects.equals(this.validationErrors, ohipInternalException.validationErrors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cause, debugMessage, errorCode, globalErrTextTemplate, localizedMessage, message, stackTrace, suppressed, validationErrors);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OhipInternalException {\n");
    sb.append("    cause: ").append(toIndentedString(cause)).append("\n");
    sb.append("    debugMessage: ").append(toIndentedString(debugMessage)).append("\n");
    sb.append("    errorCode: ").append(toIndentedString(errorCode)).append("\n");
    sb.append("    globalErrTextTemplate: ").append(toIndentedString(globalErrTextTemplate)).append("\n");
    sb.append("    localizedMessage: ").append(toIndentedString(localizedMessage)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    stackTrace: ").append(toIndentedString(stackTrace)).append("\n");
    sb.append("    suppressed: ").append(toIndentedString(suppressed)).append("\n");
    sb.append("    validationErrors: ").append(toIndentedString(validationErrors)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

