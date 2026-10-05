package uk.co.whitbread.rules.agent.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.rules.agent.generated.models.AmendmentRequestDetailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AmendmentRuleResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:44.384518+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AmendmentRuleResponseDto {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable Date generatedAt;

  private @Nullable Boolean isAmendable;

  private @Nullable AmendmentRequestDetailsDto requestDetails;

  public AmendmentRuleResponseDto generatedAt(Date generatedAt) {
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

  public AmendmentRuleResponseDto isAmendable(Boolean isAmendable) {
    this.isAmendable = isAmendable;
    return this;
  }

  /**
   * Get isAmendable
   * @return isAmendable
   */
  
  @Schema(name = "isAmendable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isAmendable")
  public Boolean getIsAmendable() {
    return isAmendable;
  }

  public void setIsAmendable(Boolean isAmendable) {
    this.isAmendable = isAmendable;
  }

  public AmendmentRuleResponseDto requestDetails(AmendmentRequestDetailsDto requestDetails) {
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
  public AmendmentRequestDetailsDto getRequestDetails() {
    return requestDetails;
  }

  public void setRequestDetails(AmendmentRequestDetailsDto requestDetails) {
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
    AmendmentRuleResponseDto amendmentRuleResponseDto = (AmendmentRuleResponseDto) o;
    return Objects.equals(this.generatedAt, amendmentRuleResponseDto.generatedAt) &&
        Objects.equals(this.isAmendable, amendmentRuleResponseDto.isAmendable) &&
        Objects.equals(this.requestDetails, amendmentRuleResponseDto.requestDetails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(generatedAt, isAmendable, requestDetails);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AmendmentRuleResponseDto {\n");
    sb.append("    generatedAt: ").append(toIndentedString(generatedAt)).append("\n");
    sb.append("    isAmendable: ").append(toIndentedString(isAmendable)).append("\n");
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

