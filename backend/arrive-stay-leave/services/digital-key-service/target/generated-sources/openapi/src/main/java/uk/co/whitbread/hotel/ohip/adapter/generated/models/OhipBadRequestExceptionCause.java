package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.OhipBadRequestExceptionCauseStackTraceInner;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.OhipBadRequestExceptionCauseSuppressedInner;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * OhipBadRequestExceptionCause
 */

@JsonTypeName("OhipBadRequestException_cause")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OhipBadRequestExceptionCause {

  private @Nullable String localizedMessage;

  private @Nullable String message;

  @Valid
  private @Nullable List<@Valid OhipBadRequestExceptionCauseStackTraceInner> stackTrace;

  @Valid
  private @Nullable List<@Valid OhipBadRequestExceptionCauseSuppressedInner> suppressed;

  public OhipBadRequestExceptionCause localizedMessage(String localizedMessage) {
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

  public OhipBadRequestExceptionCause message(String message) {
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

  public OhipBadRequestExceptionCause stackTrace(List<@Valid OhipBadRequestExceptionCauseStackTraceInner> stackTrace) {
    this.stackTrace = stackTrace;
    return this;
  }

  public OhipBadRequestExceptionCause addStackTraceItem(OhipBadRequestExceptionCauseStackTraceInner stackTraceItem) {
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
  public List<@Valid OhipBadRequestExceptionCauseStackTraceInner> getStackTrace() {
    return stackTrace;
  }

  public void setStackTrace(List<@Valid OhipBadRequestExceptionCauseStackTraceInner> stackTrace) {
    this.stackTrace = stackTrace;
  }

  public OhipBadRequestExceptionCause suppressed(List<@Valid OhipBadRequestExceptionCauseSuppressedInner> suppressed) {
    this.suppressed = suppressed;
    return this;
  }

  public OhipBadRequestExceptionCause addSuppressedItem(OhipBadRequestExceptionCauseSuppressedInner suppressedItem) {
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
  public List<@Valid OhipBadRequestExceptionCauseSuppressedInner> getSuppressed() {
    return suppressed;
  }

  public void setSuppressed(List<@Valid OhipBadRequestExceptionCauseSuppressedInner> suppressed) {
    this.suppressed = suppressed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OhipBadRequestExceptionCause ohipBadRequestExceptionCause = (OhipBadRequestExceptionCause) o;
    return Objects.equals(this.localizedMessage, ohipBadRequestExceptionCause.localizedMessage) &&
        Objects.equals(this.message, ohipBadRequestExceptionCause.message) &&
        Objects.equals(this.stackTrace, ohipBadRequestExceptionCause.stackTrace) &&
        Objects.equals(this.suppressed, ohipBadRequestExceptionCause.suppressed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(localizedMessage, message, stackTrace, suppressed);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OhipBadRequestExceptionCause {\n");
    sb.append("    localizedMessage: ").append(toIndentedString(localizedMessage)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    stackTrace: ").append(toIndentedString(stackTrace)).append("\n");
    sb.append("    suppressed: ").append(toIndentedString(suppressed)).append("\n");
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

