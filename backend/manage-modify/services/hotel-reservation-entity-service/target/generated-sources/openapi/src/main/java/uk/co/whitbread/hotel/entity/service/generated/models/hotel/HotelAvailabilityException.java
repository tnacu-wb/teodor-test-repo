package uk.co.whitbread.hotel.entity.service.generated.models.hotel;

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
import uk.co.whitbread.hotel.entity.service.generated.models.hotel.GroupBookingExceptionCause;
import uk.co.whitbread.hotel.entity.service.generated.models.hotel.GroupBookingExceptionCauseStackTraceInner;
import uk.co.whitbread.hotel.entity.service.generated.models.hotel.GroupBookingExceptionCauseSuppressedInner;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * HotelAvailabilityException
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:33.749132+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelAvailabilityException {

  private @Nullable GroupBookingExceptionCause cause;

  private @Nullable String debugMessage;

  private @Nullable Integer errorCode;

  private @Nullable String globalErrTextTemplate;

  private @Nullable String localizedMessage;

  private @Nullable String message;

  @Valid
  private List<@Valid GroupBookingExceptionCauseStackTraceInner> stackTrace = new ArrayList<>();

  @Valid
  private List<@Valid GroupBookingExceptionCauseSuppressedInner> suppressed = new ArrayList<>();

  @Valid
  private Map<String, String> validationErrors = new HashMap<>();

  public HotelAvailabilityException cause(GroupBookingExceptionCause cause) {
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
  public GroupBookingExceptionCause getCause() {
    return cause;
  }

  public void setCause(GroupBookingExceptionCause cause) {
    this.cause = cause;
  }

  public HotelAvailabilityException debugMessage(String debugMessage) {
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

  public HotelAvailabilityException errorCode(Integer errorCode) {
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

  public HotelAvailabilityException globalErrTextTemplate(String globalErrTextTemplate) {
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

  public HotelAvailabilityException localizedMessage(String localizedMessage) {
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

  public HotelAvailabilityException message(String message) {
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

  public HotelAvailabilityException stackTrace(List<@Valid GroupBookingExceptionCauseStackTraceInner> stackTrace) {
    this.stackTrace = stackTrace;
    return this;
  }

  public HotelAvailabilityException addStackTraceItem(GroupBookingExceptionCauseStackTraceInner stackTraceItem) {
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
  public List<@Valid GroupBookingExceptionCauseStackTraceInner> getStackTrace() {
    return stackTrace;
  }

  public void setStackTrace(List<@Valid GroupBookingExceptionCauseStackTraceInner> stackTrace) {
    this.stackTrace = stackTrace;
  }

  public HotelAvailabilityException suppressed(List<@Valid GroupBookingExceptionCauseSuppressedInner> suppressed) {
    this.suppressed = suppressed;
    return this;
  }

  public HotelAvailabilityException addSuppressedItem(GroupBookingExceptionCauseSuppressedInner suppressedItem) {
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
  public List<@Valid GroupBookingExceptionCauseSuppressedInner> getSuppressed() {
    return suppressed;
  }

  public void setSuppressed(List<@Valid GroupBookingExceptionCauseSuppressedInner> suppressed) {
    this.suppressed = suppressed;
  }

  public HotelAvailabilityException validationErrors(Map<String, String> validationErrors) {
    this.validationErrors = validationErrors;
    return this;
  }

  public HotelAvailabilityException putValidationErrorsItem(String key, String validationErrorsItem) {
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
    HotelAvailabilityException hotelAvailabilityException = (HotelAvailabilityException) o;
    return Objects.equals(this.cause, hotelAvailabilityException.cause) &&
        Objects.equals(this.debugMessage, hotelAvailabilityException.debugMessage) &&
        Objects.equals(this.errorCode, hotelAvailabilityException.errorCode) &&
        Objects.equals(this.globalErrTextTemplate, hotelAvailabilityException.globalErrTextTemplate) &&
        Objects.equals(this.localizedMessage, hotelAvailabilityException.localizedMessage) &&
        Objects.equals(this.message, hotelAvailabilityException.message) &&
        Objects.equals(this.stackTrace, hotelAvailabilityException.stackTrace) &&
        Objects.equals(this.suppressed, hotelAvailabilityException.suppressed) &&
        Objects.equals(this.validationErrors, hotelAvailabilityException.validationErrors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cause, debugMessage, errorCode, globalErrTextTemplate, localizedMessage, message, stackTrace, suppressed, validationErrors);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelAvailabilityException {\n");
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

