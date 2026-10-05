package uk.co.whitbread.rules.agent.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.rules.agent.generated.models.MaxArrivalDateRequestDetailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * MaxArrivalDateRuleResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:44.384518+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MaxArrivalDateRuleResponseDto {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable Date generatedAt;

  private @Nullable Integer maxArrivalDate;

  private @Nullable MaxArrivalDateRequestDetailsDto requestDetails;

  public MaxArrivalDateRuleResponseDto generatedAt(Date generatedAt) {
    this.generatedAt = generatedAt;
    return this;
  }

  /**
   * Get generatedAt
   * @return generatedAt
   */
  @Valid 
  @Schema(name = "generatedAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("generatedAt")
  public Date getGeneratedAt() {
    return generatedAt;
  }

  public void setGeneratedAt(Date generatedAt) {
    this.generatedAt = generatedAt;
  }

  public MaxArrivalDateRuleResponseDto maxArrivalDate(Integer maxArrivalDate) {
    this.maxArrivalDate = maxArrivalDate;
    return this;
  }

  /**
   * Get maxArrivalDate
   * @return maxArrivalDate
   */
  
  @Schema(name = "maxArrivalDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxArrivalDate")
  public Integer getMaxArrivalDate() {
    return maxArrivalDate;
  }

  public void setMaxArrivalDate(Integer maxArrivalDate) {
    this.maxArrivalDate = maxArrivalDate;
  }

  public MaxArrivalDateRuleResponseDto requestDetails(MaxArrivalDateRequestDetailsDto requestDetails) {
    this.requestDetails = requestDetails;
    return this;
  }

  /**
   * Get requestDetails
   * @return requestDetails
   */
  @Valid 
  @Schema(name = "requestDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("requestDetails")
  public MaxArrivalDateRequestDetailsDto getRequestDetails() {
    return requestDetails;
  }

  public void setRequestDetails(MaxArrivalDateRequestDetailsDto requestDetails) {
    this.requestDetails = requestDetails;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MaxArrivalDateRuleResponseDto maxArrivalDateRuleResponseDto = (MaxArrivalDateRuleResponseDto) o;
    return Objects.equals(this.generatedAt, maxArrivalDateRuleResponseDto.generatedAt) &&
        Objects.equals(this.maxArrivalDate, maxArrivalDateRuleResponseDto.maxArrivalDate) &&
        Objects.equals(this.requestDetails, maxArrivalDateRuleResponseDto.requestDetails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(generatedAt, maxArrivalDate, requestDetails);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MaxArrivalDateRuleResponseDto {\n");
    sb.append("    generatedAt: ").append(toIndentedString(generatedAt)).append("\n");
    sb.append("    maxArrivalDate: ").append(toIndentedString(maxArrivalDate)).append("\n");
    sb.append("    requestDetails: ").append(toIndentedString(requestDetails)).append("\n");
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

