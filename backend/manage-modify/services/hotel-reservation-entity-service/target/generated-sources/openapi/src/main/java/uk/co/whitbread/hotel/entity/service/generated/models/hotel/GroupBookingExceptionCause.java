package uk.co.whitbread.hotel.entity.service.generated.models.hotel;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
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
 * GroupBookingExceptionCause
 */

@JsonTypeName("GroupBookingException_cause")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:33.749132+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class GroupBookingExceptionCause {

  private @Nullable String localizedMessage;

  private @Nullable String message;

  @Valid
  private List<@Valid GroupBookingExceptionCauseStackTraceInner> stackTrace = new ArrayList<>();

  @Valid
  private List<@Valid GroupBookingExceptionCauseSuppressedInner> suppressed = new ArrayList<>();

  public GroupBookingExceptionCause localizedMessage(String localizedMessage) {
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

  public GroupBookingExceptionCause message(String message) {
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

  public GroupBookingExceptionCause stackTrace(List<@Valid GroupBookingExceptionCauseStackTraceInner> stackTrace) {
    this.stackTrace = stackTrace;
    return this;
  }

  public GroupBookingExceptionCause addStackTraceItem(GroupBookingExceptionCauseStackTraceInner stackTraceItem) {
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

  public GroupBookingExceptionCause suppressed(List<@Valid GroupBookingExceptionCauseSuppressedInner> suppressed) {
    this.suppressed = suppressed;
    return this;
  }

  public GroupBookingExceptionCause addSuppressedItem(GroupBookingExceptionCauseSuppressedInner suppressedItem) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GroupBookingExceptionCause groupBookingExceptionCause = (GroupBookingExceptionCause) o;
    return Objects.equals(this.localizedMessage, groupBookingExceptionCause.localizedMessage) &&
        Objects.equals(this.message, groupBookingExceptionCause.message) &&
        Objects.equals(this.stackTrace, groupBookingExceptionCause.stackTrace) &&
        Objects.equals(this.suppressed, groupBookingExceptionCause.suppressed);
  }

  @Override
  public int hashCode() {
    return Objects.hash(localizedMessage, message, stackTrace, suppressed);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GroupBookingExceptionCause {\n");
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

