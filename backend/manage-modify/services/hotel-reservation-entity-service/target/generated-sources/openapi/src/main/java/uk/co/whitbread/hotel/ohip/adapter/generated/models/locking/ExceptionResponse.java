package uk.co.whitbread.hotel.ohip.adapter.generated.models.locking;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import java.util.Date;
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
 * ExceptionResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:31.981751+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ExceptionResponse {

  private @Nullable String debugMessage;

  private @Nullable String errorCode;

  private @Nullable String message;

  /**
   * Gets or Sets status
   */
  public enum StatusEnum {
    _100_CONTINUE("100 CONTINUE"),
    
    _101_SWITCHING_PROTOCOLS("101 SWITCHING_PROTOCOLS"),
    
    _102_PROCESSING("102 PROCESSING"),
    
    _103_CHECKPOINT("103 CHECKPOINT"),
    
    _200_OK("200 OK"),
    
    _201_CREATED("201 CREATED"),
    
    _202_ACCEPTED("202 ACCEPTED"),
    
    _203_NON_AUTHORITATIVE_INFORMATION("203 NON_AUTHORITATIVE_INFORMATION"),
    
    _204_NO_CONTENT("204 NO_CONTENT"),
    
    _205_RESET_CONTENT("205 RESET_CONTENT"),
    
    _206_PARTIAL_CONTENT("206 PARTIAL_CONTENT"),
    
    _207_MULTI_STATUS("207 MULTI_STATUS"),
    
    _208_ALREADY_REPORTED("208 ALREADY_REPORTED"),
    
    _226_IM_USED("226 IM_USED"),
    
    _300_MULTIPLE_CHOICES("300 MULTIPLE_CHOICES"),
    
    _301_MOVED_PERMANENTLY("301 MOVED_PERMANENTLY"),
    
    _302_FOUND("302 FOUND"),
    
    _302_MOVED_TEMPORARILY("302 MOVED_TEMPORARILY"),
    
    _303_SEE_OTHER("303 SEE_OTHER"),
    
    _304_NOT_MODIFIED("304 NOT_MODIFIED"),
    
    _305_USE_PROXY("305 USE_PROXY"),
    
    _307_TEMPORARY_REDIRECT("307 TEMPORARY_REDIRECT"),
    
    _308_PERMANENT_REDIRECT("308 PERMANENT_REDIRECT"),
    
    _400_BAD_REQUEST("400 BAD_REQUEST"),
    
    _401_UNAUTHORIZED("401 UNAUTHORIZED"),
    
    _402_PAYMENT_REQUIRED("402 PAYMENT_REQUIRED"),
    
    _403_FORBIDDEN("403 FORBIDDEN"),
    
    _404_NOT_FOUND("404 NOT_FOUND"),
    
    _405_METHOD_NOT_ALLOWED("405 METHOD_NOT_ALLOWED"),
    
    _406_NOT_ACCEPTABLE("406 NOT_ACCEPTABLE"),
    
    _407_PROXY_AUTHENTICATION_REQUIRED("407 PROXY_AUTHENTICATION_REQUIRED"),
    
    _408_REQUEST_TIMEOUT("408 REQUEST_TIMEOUT"),
    
    _409_CONFLICT("409 CONFLICT"),
    
    _410_GONE("410 GONE"),
    
    _411_LENGTH_REQUIRED("411 LENGTH_REQUIRED"),
    
    _412_PRECONDITION_FAILED("412 PRECONDITION_FAILED"),
    
    _413_PAYLOAD_TOO_LARGE("413 PAYLOAD_TOO_LARGE"),
    
    _413_REQUEST_ENTITY_TOO_LARGE("413 REQUEST_ENTITY_TOO_LARGE"),
    
    _414_URI_TOO_LONG("414 URI_TOO_LONG"),
    
    _414_REQUEST_URI_TOO_LONG("414 REQUEST_URI_TOO_LONG"),
    
    _415_UNSUPPORTED_MEDIA_TYPE("415 UNSUPPORTED_MEDIA_TYPE"),
    
    _416_REQUESTED_RANGE_NOT_SATISFIABLE("416 REQUESTED_RANGE_NOT_SATISFIABLE"),
    
    _417_EXPECTATION_FAILED("417 EXPECTATION_FAILED"),
    
    _418_I_AM_A_TEAPOT("418 I_AM_A_TEAPOT"),
    
    _419_INSUFFICIENT_SPACE_ON_RESOURCE("419 INSUFFICIENT_SPACE_ON_RESOURCE"),
    
    _420_METHOD_FAILURE("420 METHOD_FAILURE"),
    
    _421_DESTINATION_LOCKED("421 DESTINATION_LOCKED"),
    
    _422_UNPROCESSABLE_ENTITY("422 UNPROCESSABLE_ENTITY"),
    
    _423_LOCKED("423 LOCKED"),
    
    _424_FAILED_DEPENDENCY("424 FAILED_DEPENDENCY"),
    
    _425_TOO_EARLY("425 TOO_EARLY"),
    
    _426_UPGRADE_REQUIRED("426 UPGRADE_REQUIRED"),
    
    _428_PRECONDITION_REQUIRED("428 PRECONDITION_REQUIRED"),
    
    _429_TOO_MANY_REQUESTS("429 TOO_MANY_REQUESTS"),
    
    _431_REQUEST_HEADER_FIELDS_TOO_LARGE("431 REQUEST_HEADER_FIELDS_TOO_LARGE"),
    
    _451_UNAVAILABLE_FOR_LEGAL_REASONS("451 UNAVAILABLE_FOR_LEGAL_REASONS"),
    
    _500_INTERNAL_SERVER_ERROR("500 INTERNAL_SERVER_ERROR"),
    
    _501_NOT_IMPLEMENTED("501 NOT_IMPLEMENTED"),
    
    _502_BAD_GATEWAY("502 BAD_GATEWAY"),
    
    _503_SERVICE_UNAVAILABLE("503 SERVICE_UNAVAILABLE"),
    
    _504_GATEWAY_TIMEOUT("504 GATEWAY_TIMEOUT"),
    
    _505_HTTP_VERSION_NOT_SUPPORTED("505 HTTP_VERSION_NOT_SUPPORTED"),
    
    _506_VARIANT_ALSO_NEGOTIATES("506 VARIANT_ALSO_NEGOTIATES"),
    
    _507_INSUFFICIENT_STORAGE("507 INSUFFICIENT_STORAGE"),
    
    _508_LOOP_DETECTED("508 LOOP_DETECTED"),
    
    _509_BANDWIDTH_LIMIT_EXCEEDED("509 BANDWIDTH_LIMIT_EXCEEDED"),
    
    _510_NOT_EXTENDED("510 NOT_EXTENDED"),
    
    _511_NETWORK_AUTHENTICATION_REQUIRED("511 NETWORK_AUTHENTICATION_REQUIRED");

    private String value;

    StatusEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String value) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable StatusEnum status;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable Date timestamp;

  public ExceptionResponse debugMessage(String debugMessage) {
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

  public ExceptionResponse errorCode(String errorCode) {
    this.errorCode = errorCode;
    return this;
  }

  /**
   * Get errorCode
   * @return errorCode
   */
  
  @Schema(name = "errorCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("errorCode")
  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public ExceptionResponse message(String message) {
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

  public ExceptionResponse status(StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public ExceptionResponse timestamp(Date timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * Get timestamp
   * @return timestamp
   */
  @Valid 
  @Schema(name = "timestamp", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("timestamp")
  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExceptionResponse exceptionResponse = (ExceptionResponse) o;
    return Objects.equals(this.debugMessage, exceptionResponse.debugMessage) &&
        Objects.equals(this.errorCode, exceptionResponse.errorCode) &&
        Objects.equals(this.message, exceptionResponse.message) &&
        Objects.equals(this.status, exceptionResponse.status) &&
        Objects.equals(this.timestamp, exceptionResponse.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(debugMessage, errorCode, message, status, timestamp);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExceptionResponse {\n");
    sb.append("    debugMessage: ").append(toIndentedString(debugMessage)).append("\n");
    sb.append("    errorCode: ").append(toIndentedString(errorCode)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
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

