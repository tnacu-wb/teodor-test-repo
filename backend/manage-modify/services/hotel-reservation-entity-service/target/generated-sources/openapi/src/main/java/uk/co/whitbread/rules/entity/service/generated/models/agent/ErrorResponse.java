package uk.co.whitbread.rules.entity.service.generated.models.agent;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.rules.entity.service.generated.models.agent.ValidationError;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ErrorResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:32.125466+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ErrorResponse {

  private @Nullable String debugMessage;

  @Valid
  private List<@Valid ValidationError> details = new ArrayList<>();

  private @Nullable Integer errCode;

  private @Nullable String globalErrTextTemplate;

  public ErrorResponse debugMessage(String debugMessage) {
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

  public ErrorResponse details(List<@Valid ValidationError> details) {
    this.details = details;
    return this;
  }

  public ErrorResponse addDetailsItem(ValidationError detailsItem) {
    if (this.details == null) {
      this.details = new ArrayList<>();
    }
    this.details.add(detailsItem);
    return this;
  }

  /**
   * Get details
   * @return details
   */
  @Valid 
  @Schema(name = "details", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("details")
  public List<@Valid ValidationError> getDetails() {
    return details;
  }

  public void setDetails(List<@Valid ValidationError> details) {
    this.details = details;
  }

  public ErrorResponse errCode(Integer errCode) {
    this.errCode = errCode;
    return this;
  }

  /**
   * Get errCode
   * @return errCode
   */
  
  @Schema(name = "errCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("errCode")
  public Integer getErrCode() {
    return errCode;
  }

  public void setErrCode(Integer errCode) {
    this.errCode = errCode;
  }

  public ErrorResponse globalErrTextTemplate(String globalErrTextTemplate) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorResponse errorResponse = (ErrorResponse) o;
    return Objects.equals(this.debugMessage, errorResponse.debugMessage) &&
        Objects.equals(this.details, errorResponse.details) &&
        Objects.equals(this.errCode, errorResponse.errCode) &&
        Objects.equals(this.globalErrTextTemplate, errorResponse.globalErrTextTemplate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(debugMessage, details, errCode, globalErrTextTemplate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErrorResponse {\n");
    sb.append("    debugMessage: ").append(toIndentedString(debugMessage)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
    sb.append("    errCode: ").append(toIndentedString(errCode)).append("\n");
    sb.append("    globalErrTextTemplate: ").append(toIndentedString(globalErrTextTemplate)).append("\n");
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

