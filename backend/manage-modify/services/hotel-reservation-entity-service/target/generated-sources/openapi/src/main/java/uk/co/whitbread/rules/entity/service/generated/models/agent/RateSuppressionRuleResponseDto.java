package uk.co.whitbread.rules.entity.service.generated.models.agent;

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
 * RateSuppressionRuleResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:32.125466+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RateSuppressionRuleResponseDto {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable Date expiryDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable Date generatedAt;

  @Valid
  private List<String> rateSuppressionList = new ArrayList<>();

  public RateSuppressionRuleResponseDto expiryDate(Date expiryDate) {
    this.expiryDate = expiryDate;
    return this;
  }

  /**
   * Get expiryDate
   * @return expiryDate
   */
  @Valid 
  @Schema(name = "expiryDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expiryDate")
  public Date getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(Date expiryDate) {
    this.expiryDate = expiryDate;
  }

  public RateSuppressionRuleResponseDto generatedAt(Date generatedAt) {
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

  public RateSuppressionRuleResponseDto rateSuppressionList(List<String> rateSuppressionList) {
    this.rateSuppressionList = rateSuppressionList;
    return this;
  }

  public RateSuppressionRuleResponseDto addRateSuppressionListItem(String rateSuppressionListItem) {
    if (this.rateSuppressionList == null) {
      this.rateSuppressionList = new ArrayList<>();
    }
    this.rateSuppressionList.add(rateSuppressionListItem);
    return this;
  }

  /**
   * Get rateSuppressionList
   * @return rateSuppressionList
   */
  
  @Schema(name = "rate-suppression-list", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rate-suppression-list")
  public List<String> getRateSuppressionList() {
    return rateSuppressionList;
  }

  public void setRateSuppressionList(List<String> rateSuppressionList) {
    this.rateSuppressionList = rateSuppressionList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RateSuppressionRuleResponseDto rateSuppressionRuleResponseDto = (RateSuppressionRuleResponseDto) o;
    return Objects.equals(this.expiryDate, rateSuppressionRuleResponseDto.expiryDate) &&
        Objects.equals(this.generatedAt, rateSuppressionRuleResponseDto.generatedAt) &&
        Objects.equals(this.rateSuppressionList, rateSuppressionRuleResponseDto.rateSuppressionList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(expiryDate, generatedAt, rateSuppressionList);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateSuppressionRuleResponseDto {\n");
    sb.append("    expiryDate: ").append(toIndentedString(expiryDate)).append("\n");
    sb.append("    generatedAt: ").append(toIndentedString(generatedAt)).append("\n");
    sb.append("    rateSuppressionList: ").append(toIndentedString(rateSuppressionList)).append("\n");
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

