package uk.co.whitbread.rules.agent.generated.models;

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
import uk.co.whitbread.rules.agent.generated.models.ChannelRuleRequestDetailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ChannelRuleResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:44.384518+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ChannelRuleResponseDto {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable Date generatedAt;

  @Valid
  private List<String> ratePlanSets = new ArrayList<>();

  private @Nullable ChannelRuleRequestDetailsDto requestDetails;

  private @Nullable String sourceId;

  public ChannelRuleResponseDto generatedAt(Date generatedAt) {
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

  public ChannelRuleResponseDto ratePlanSets(List<String> ratePlanSets) {
    this.ratePlanSets = ratePlanSets;
    return this;
  }

  public ChannelRuleResponseDto addRatePlanSetsItem(String ratePlanSetsItem) {
    if (this.ratePlanSets == null) {
      this.ratePlanSets = new ArrayList<>();
    }
    this.ratePlanSets.add(ratePlanSetsItem);
    return this;
  }

  /**
   * Get ratePlanSets
   * @return ratePlanSets
   */
  
  @Schema(name = "ratePlanSets", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanSets")
  public List<String> getRatePlanSets() {
    return ratePlanSets;
  }

  public void setRatePlanSets(List<String> ratePlanSets) {
    this.ratePlanSets = ratePlanSets;
  }

  public ChannelRuleResponseDto requestDetails(ChannelRuleRequestDetailsDto requestDetails) {
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
  public ChannelRuleRequestDetailsDto getRequestDetails() {
    return requestDetails;
  }

  public void setRequestDetails(ChannelRuleRequestDetailsDto requestDetails) {
    this.requestDetails = requestDetails;
  }

  public ChannelRuleResponseDto sourceId(String sourceId) {
    this.sourceId = sourceId;
    return this;
  }

  /**
   * Get sourceId
   * @return sourceId
   */
  
  @Schema(name = "sourceId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sourceId")
  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChannelRuleResponseDto channelRuleResponseDto = (ChannelRuleResponseDto) o;
    return Objects.equals(this.generatedAt, channelRuleResponseDto.generatedAt) &&
        Objects.equals(this.ratePlanSets, channelRuleResponseDto.ratePlanSets) &&
        Objects.equals(this.requestDetails, channelRuleResponseDto.requestDetails) &&
        Objects.equals(this.sourceId, channelRuleResponseDto.sourceId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(generatedAt, ratePlanSets, requestDetails, sourceId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChannelRuleResponseDto {\n");
    sb.append("    generatedAt: ").append(toIndentedString(generatedAt)).append("\n");
    sb.append("    ratePlanSets: ").append(toIndentedString(ratePlanSets)).append("\n");
    sb.append("    requestDetails: ").append(toIndentedString(requestDetails)).append("\n");
    sb.append("    sourceId: ").append(toIndentedString(sourceId)).append("\n");
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

