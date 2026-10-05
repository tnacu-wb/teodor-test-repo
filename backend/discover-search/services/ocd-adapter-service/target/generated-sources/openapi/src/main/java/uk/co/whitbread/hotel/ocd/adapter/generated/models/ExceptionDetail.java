package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Common Error Response format
 */

@Schema(name = "ExceptionDetail", description = "Common Error Response format")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ExceptionDetail {

  private @Nullable String title;

  private @Nullable Integer status;

  private @Nullable String oErrorCode;

  private @Nullable String type;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable Date timestamp;

  @Valid
  private List<@Valid ExceptionDetail> oErrorDetails = new ArrayList<>();

  private @Nullable Integer logId;

  public ExceptionDetail title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Short, human-readable summary of the problem.  The summary SHOULD NOT change for subsequent occurrences of the problem, except for purposes of localization.
   * @return title
   */
  
  @Schema(name = "title", example = "Error in Application", description = "Short, human-readable summary of the problem.  The summary SHOULD NOT change for subsequent occurrences of the problem, except for purposes of localization.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public ExceptionDetail status(Integer status) {
    this.status = status;
    return this;
  }

  /**
   * HTTP status code for this occurrence of the problem, set by the origin server.
   * @return status
   */
  
  @Schema(name = "status", example = "400", description = "HTTP status code for this occurrence of the problem, set by the origin server.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public ExceptionDetail oErrorCode(String oErrorCode) {
    this.oErrorCode = oErrorCode;
    return this;
  }

  /**
   * Business specific Error code, which is different from HTTP error code.
   * @return oErrorCode
   */
  
  @Schema(name = "o:errorCode", example = "RSV-34534534", description = "Business specific Error code, which is different from HTTP error code.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("o:errorCode")
  public String getoErrorCode() {
    return oErrorCode;
  }

  public void setoErrorCode(String oErrorCode) {
    this.oErrorCode = oErrorCode;
  }

  public ExceptionDetail type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Absolute URI [RFC3986] that identifies the problem type. When dereferenced, it SHOULD provide a human-readable summary of the problem.
   * @return type
   */
  
  @Schema(name = "type", example = "http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4", description = "Absolute URI [RFC3986] that identifies the problem type. When dereferenced, it SHOULD provide a human-readable summary of the problem.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public ExceptionDetail timestamp(Date timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * The UTC Date and Time of the Error happened.
   * @return timestamp
   */
  @Valid 
  @Schema(name = "timestamp", example = "2022-01-02T11:30:22.234Z", description = "The UTC Date and Time of the Error happened.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("timestamp")
  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public ExceptionDetail oErrorDetails(List<@Valid ExceptionDetail> oErrorDetails) {
    this.oErrorDetails = oErrorDetails;
    return this;
  }

  public ExceptionDetail addOErrorDetailsItem(ExceptionDetail oErrorDetailsItem) {
    if (this.oErrorDetails == null) {
      this.oErrorDetails = new ArrayList<>();
    }
    this.oErrorDetails.add(oErrorDetailsItem);
    return this;
  }

  /**
   * Details of the error message, consisting of a hierarchical tree structure
   * @return oErrorDetails
   */
  @Valid 
  @Schema(name = "o:errorDetails", description = "Details of the error message, consisting of a hierarchical tree structure", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("o:errorDetails")
  public List<@Valid ExceptionDetail> getoErrorDetails() {
    return oErrorDetails;
  }

  public void setoErrorDetails(List<@Valid ExceptionDetail> oErrorDetails) {
    this.oErrorDetails = oErrorDetails;
  }

  public ExceptionDetail logId(Integer logId) {
    this.logId = logId;
    return this;
  }

  /**
   * An ID for support reasons to be able identify errors better.
   * @return logId
   */
  
  @Schema(name = "logId", example = "334543532224", description = "An ID for support reasons to be able identify errors better.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("logId")
  public Integer getLogId() {
    return logId;
  }

  public void setLogId(Integer logId) {
    this.logId = logId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExceptionDetail exceptionDetail = (ExceptionDetail) o;
    return Objects.equals(this.title, exceptionDetail.title) &&
        Objects.equals(this.status, exceptionDetail.status) &&
        Objects.equals(this.oErrorCode, exceptionDetail.oErrorCode) &&
        Objects.equals(this.type, exceptionDetail.type) &&
        Objects.equals(this.timestamp, exceptionDetail.timestamp) &&
        Objects.equals(this.oErrorDetails, exceptionDetail.oErrorDetails) &&
        Objects.equals(this.logId, exceptionDetail.logId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, status, oErrorCode, type, timestamp, oErrorDetails, logId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExceptionDetail {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    oErrorCode: ").append(toIndentedString(oErrorCode)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    oErrorDetails: ").append(toIndentedString(oErrorDetails)).append("\n");
    sb.append("    logId: ").append(toIndentedString(logId)).append("\n");
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

