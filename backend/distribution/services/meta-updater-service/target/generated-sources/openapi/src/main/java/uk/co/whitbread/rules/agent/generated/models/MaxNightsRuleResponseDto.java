package uk.co.whitbread.rules.agent.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.rules.agent.generated.models.MaxNightsRequestDetailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * MaxNightsRuleResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:31.460652+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MaxNightsRuleResponseDto {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable Date generatedAt;

  private @Nullable Integer maxNights;

  private @Nullable MaxNightsRequestDetailsDto requestDetails;

  public MaxNightsRuleResponseDto generatedAt(Date generatedAt) {
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

  public MaxNightsRuleResponseDto maxNights(Integer maxNights) {
    this.maxNights = maxNights;
    return this;
  }

  /**
   * Get maxNights
   * @return maxNights
   */
  
  @Schema(name = "maxNights", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxNights")
  public Integer getMaxNights() {
    return maxNights;
  }

  public void setMaxNights(Integer maxNights) {
    this.maxNights = maxNights;
  }

  public MaxNightsRuleResponseDto requestDetails(MaxNightsRequestDetailsDto requestDetails) {
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
  public MaxNightsRequestDetailsDto getRequestDetails() {
    return requestDetails;
  }

  public void setRequestDetails(MaxNightsRequestDetailsDto requestDetails) {
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
    MaxNightsRuleResponseDto maxNightsRuleResponseDto = (MaxNightsRuleResponseDto) o;
    return Objects.equals(this.generatedAt, maxNightsRuleResponseDto.generatedAt) &&
        Objects.equals(this.maxNights, maxNightsRuleResponseDto.maxNights) &&
        Objects.equals(this.requestDetails, maxNightsRuleResponseDto.requestDetails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(generatedAt, maxNights, requestDetails);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MaxNightsRuleResponseDto {\n");
    sb.append("    generatedAt: ").append(toIndentedString(generatedAt)).append("\n");
    sb.append("    maxNights: ").append(toIndentedString(maxNights)).append("\n");
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

