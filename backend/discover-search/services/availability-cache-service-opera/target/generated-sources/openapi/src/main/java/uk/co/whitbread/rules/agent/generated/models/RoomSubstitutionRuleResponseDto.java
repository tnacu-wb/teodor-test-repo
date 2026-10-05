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
import uk.co.whitbread.rules.agent.generated.models.RoomSubstitutionDto;
import uk.co.whitbread.rules.agent.generated.models.RoomSubstitutionRequestDetailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomSubstitutionRuleResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:44.384518+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomSubstitutionRuleResponseDto {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable Date generatedAt;

  private @Nullable RoomSubstitutionRequestDetailsDto requestDetails;

  @Valid
  private List<@Valid RoomSubstitutionDto> substitutionList = new ArrayList<>();

  public RoomSubstitutionRuleResponseDto generatedAt(Date generatedAt) {
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

  public RoomSubstitutionRuleResponseDto requestDetails(RoomSubstitutionRequestDetailsDto requestDetails) {
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
  public RoomSubstitutionRequestDetailsDto getRequestDetails() {
    return requestDetails;
  }

  public void setRequestDetails(RoomSubstitutionRequestDetailsDto requestDetails) {
    this.requestDetails = requestDetails;
  }

  public RoomSubstitutionRuleResponseDto substitutionList(List<@Valid RoomSubstitutionDto> substitutionList) {
    this.substitutionList = substitutionList;
    return this;
  }

  public RoomSubstitutionRuleResponseDto addSubstitutionListItem(RoomSubstitutionDto substitutionListItem) {
    if (this.substitutionList == null) {
      this.substitutionList = new ArrayList<>();
    }
    this.substitutionList.add(substitutionListItem);
    return this;
  }

  /**
   * Get substitutionList
   * @return substitutionList
   */
  @Valid 
  @Schema(name = "substitution-list", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("substitution-list")
  public List<@Valid RoomSubstitutionDto> getSubstitutionList() {
    return substitutionList;
  }

  public void setSubstitutionList(List<@Valid RoomSubstitutionDto> substitutionList) {
    this.substitutionList = substitutionList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomSubstitutionRuleResponseDto roomSubstitutionRuleResponseDto = (RoomSubstitutionRuleResponseDto) o;
    return Objects.equals(this.generatedAt, roomSubstitutionRuleResponseDto.generatedAt) &&
        Objects.equals(this.requestDetails, roomSubstitutionRuleResponseDto.requestDetails) &&
        Objects.equals(this.substitutionList, roomSubstitutionRuleResponseDto.substitutionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(generatedAt, requestDetails, substitutionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomSubstitutionRuleResponseDto {\n");
    sb.append("    generatedAt: ").append(toIndentedString(generatedAt)).append("\n");
    sb.append("    requestDetails: ").append(toIndentedString(requestDetails)).append("\n");
    sb.append("    substitutionList: ").append(toIndentedString(substitutionList)).append("\n");
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

