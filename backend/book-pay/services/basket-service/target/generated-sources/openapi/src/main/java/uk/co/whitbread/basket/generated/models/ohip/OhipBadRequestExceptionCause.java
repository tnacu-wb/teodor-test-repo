package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.OhipBadRequestExceptionCauseStackTraceInner;
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
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@JsonTypeName("OhipBadRequestException_cause")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OhipBadRequestExceptionCause {

  private @Nullable String localizedMessage;

  private @Nullable String message;

  @Valid
  private List<@Valid OhipBadRequestExceptionCauseStackTraceInner> stackTrace = new ArrayList<>();

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
        Objects.equals(this.stackTrace, ohipBadRequestExceptionCause.stackTrace);
  }

  @Override
  public int hashCode() {
    return Objects.hash(localizedMessage, message, stackTrace);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OhipBadRequestExceptionCause {\n");
    sb.append("    localizedMessage: ").append(toIndentedString(localizedMessage)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    stackTrace: ").append(toIndentedString(stackTrace)).append("\n");
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

