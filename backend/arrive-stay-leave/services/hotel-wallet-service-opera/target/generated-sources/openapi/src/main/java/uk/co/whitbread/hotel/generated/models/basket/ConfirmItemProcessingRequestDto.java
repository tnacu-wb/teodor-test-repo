package uk.co.whitbread.hotel.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ConfirmItemProcessingRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ConfirmItemProcessingRequestDto {

  private @Nullable String description;

  @Valid
  private List<String> errors = new ArrayList<>();

  private @Nullable String reportedAt;

  private @Nullable String reqAction;

  private @Nullable Integer status;

  public ConfirmItemProcessingRequestDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ConfirmItemProcessingRequestDto errors(List<String> errors) {
    this.errors = errors;
    return this;
  }

  public ConfirmItemProcessingRequestDto addErrorsItem(String errorsItem) {
    if (this.errors == null) {
      this.errors = new ArrayList<>();
    }
    this.errors.add(errorsItem);
    return this;
  }

  /**
   * Get errors
   * @return errors
   */
  
  @Schema(name = "errors", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("errors")
  public List<String> getErrors() {
    return errors;
  }

  public void setErrors(List<String> errors) {
    this.errors = errors;
  }

  public ConfirmItemProcessingRequestDto reportedAt(String reportedAt) {
    this.reportedAt = reportedAt;
    return this;
  }

  /**
   * Get reportedAt
   * @return reportedAt
   */
  
  @Schema(name = "reported_at", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reported_at")
  public String getReportedAt() {
    return reportedAt;
  }

  public void setReportedAt(String reportedAt) {
    this.reportedAt = reportedAt;
  }

  public ConfirmItemProcessingRequestDto reqAction(String reqAction) {
    this.reqAction = reqAction;
    return this;
  }

  /**
   * Get reqAction
   * @return reqAction
   */
  
  @Schema(name = "reqAction", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reqAction")
  public String getReqAction() {
    return reqAction;
  }

  public void setReqAction(String reqAction) {
    this.reqAction = reqAction;
  }

  public ConfirmItemProcessingRequestDto status(Integer status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConfirmItemProcessingRequestDto confirmItemProcessingRequestDto = (ConfirmItemProcessingRequestDto) o;
    return Objects.equals(this.description, confirmItemProcessingRequestDto.description) &&
        Objects.equals(this.errors, confirmItemProcessingRequestDto.errors) &&
        Objects.equals(this.reportedAt, confirmItemProcessingRequestDto.reportedAt) &&
        Objects.equals(this.reqAction, confirmItemProcessingRequestDto.reqAction) &&
        Objects.equals(this.status, confirmItemProcessingRequestDto.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, errors, reportedAt, reqAction, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConfirmItemProcessingRequestDto {\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    errors: ").append(toIndentedString(errors)).append("\n");
    sb.append("    reportedAt: ").append(toIndentedString(reportedAt)).append("\n");
    sb.append("    reqAction: ").append(toIndentedString(reqAction)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

