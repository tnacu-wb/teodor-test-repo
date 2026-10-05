package uk.co.whitbread.content.entity.service.generated.models.content;

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
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RateClassificationDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RateClassificationDto {

  private @Nullable String additionalDescription;

  private @Nullable String rateCategory;

  private @Nullable String rateClassification;

  private @Nullable String rateDescription;

  private @Nullable String rateDisplaySet;

  private @Nullable String rateLongDescription;

  private @Nullable String rateName;

  private @Nullable String rateNotes;

  private @Nullable String rateOrder;

  private @Nullable String ratePlanCode;

  @Valid
  private List<String> rateTags = new ArrayList<>();

  public RateClassificationDto additionalDescription(String additionalDescription) {
    this.additionalDescription = additionalDescription;
    return this;
  }

  /**
   * Get additionalDescription
   * @return additionalDescription
   */
  
  @Schema(name = "additionalDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("additionalDescription")
  public String getAdditionalDescription() {
    return additionalDescription;
  }

  public void setAdditionalDescription(String additionalDescription) {
    this.additionalDescription = additionalDescription;
  }

  public RateClassificationDto rateCategory(String rateCategory) {
    this.rateCategory = rateCategory;
    return this;
  }

  /**
   * Get rateCategory
   * @return rateCategory
   */
  
  @Schema(name = "rateCategory", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateCategory")
  public String getRateCategory() {
    return rateCategory;
  }

  public void setRateCategory(String rateCategory) {
    this.rateCategory = rateCategory;
  }

  public RateClassificationDto rateClassification(String rateClassification) {
    this.rateClassification = rateClassification;
    return this;
  }

  /**
   * Get rateClassification
   * @return rateClassification
   */
  
  @Schema(name = "rateClassification", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateClassification")
  public String getRateClassification() {
    return rateClassification;
  }

  public void setRateClassification(String rateClassification) {
    this.rateClassification = rateClassification;
  }

  public RateClassificationDto rateDescription(String rateDescription) {
    this.rateDescription = rateDescription;
    return this;
  }

  /**
   * Get rateDescription
   * @return rateDescription
   */
  
  @Schema(name = "rateDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateDescription")
  public String getRateDescription() {
    return rateDescription;
  }

  public void setRateDescription(String rateDescription) {
    this.rateDescription = rateDescription;
  }

  public RateClassificationDto rateDisplaySet(String rateDisplaySet) {
    this.rateDisplaySet = rateDisplaySet;
    return this;
  }

  /**
   * Get rateDisplaySet
   * @return rateDisplaySet
   */
  
  @Schema(name = "rateDisplaySet", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateDisplaySet")
  public String getRateDisplaySet() {
    return rateDisplaySet;
  }

  public void setRateDisplaySet(String rateDisplaySet) {
    this.rateDisplaySet = rateDisplaySet;
  }

  public RateClassificationDto rateLongDescription(String rateLongDescription) {
    this.rateLongDescription = rateLongDescription;
    return this;
  }

  /**
   * Get rateLongDescription
   * @return rateLongDescription
   */
  
  @Schema(name = "rateLongDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateLongDescription")
  public String getRateLongDescription() {
    return rateLongDescription;
  }

  public void setRateLongDescription(String rateLongDescription) {
    this.rateLongDescription = rateLongDescription;
  }

  public RateClassificationDto rateName(String rateName) {
    this.rateName = rateName;
    return this;
  }

  /**
   * Get rateName
   * @return rateName
   */
  
  @Schema(name = "rateName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateName")
  public String getRateName() {
    return rateName;
  }

  public void setRateName(String rateName) {
    this.rateName = rateName;
  }

  public RateClassificationDto rateNotes(String rateNotes) {
    this.rateNotes = rateNotes;
    return this;
  }

  /**
   * Get rateNotes
   * @return rateNotes
   */
  
  @Schema(name = "rateNotes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateNotes")
  public String getRateNotes() {
    return rateNotes;
  }

  public void setRateNotes(String rateNotes) {
    this.rateNotes = rateNotes;
  }

  public RateClassificationDto rateOrder(String rateOrder) {
    this.rateOrder = rateOrder;
    return this;
  }

  /**
   * Get rateOrder
   * @return rateOrder
   */
  
  @Schema(name = "rateOrder", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateOrder")
  public String getRateOrder() {
    return rateOrder;
  }

  public void setRateOrder(String rateOrder) {
    this.rateOrder = rateOrder;
  }

  public RateClassificationDto ratePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
    return this;
  }

  /**
   * Get ratePlanCode
   * @return ratePlanCode
   */
  
  @Schema(name = "ratePlanCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanCode")
  public String getRatePlanCode() {
    return ratePlanCode;
  }

  public void setRatePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
  }

  public RateClassificationDto rateTags(List<String> rateTags) {
    this.rateTags = rateTags;
    return this;
  }

  public RateClassificationDto addRateTagsItem(String rateTagsItem) {
    if (this.rateTags == null) {
      this.rateTags = new ArrayList<>();
    }
    this.rateTags.add(rateTagsItem);
    return this;
  }

  /**
   * Get rateTags
   * @return rateTags
   */
  
  @Schema(name = "rateTags", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateTags")
  public List<String> getRateTags() {
    return rateTags;
  }

  public void setRateTags(List<String> rateTags) {
    this.rateTags = rateTags;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RateClassificationDto rateClassificationDto = (RateClassificationDto) o;
    return Objects.equals(this.additionalDescription, rateClassificationDto.additionalDescription) &&
        Objects.equals(this.rateCategory, rateClassificationDto.rateCategory) &&
        Objects.equals(this.rateClassification, rateClassificationDto.rateClassification) &&
        Objects.equals(this.rateDescription, rateClassificationDto.rateDescription) &&
        Objects.equals(this.rateDisplaySet, rateClassificationDto.rateDisplaySet) &&
        Objects.equals(this.rateLongDescription, rateClassificationDto.rateLongDescription) &&
        Objects.equals(this.rateName, rateClassificationDto.rateName) &&
        Objects.equals(this.rateNotes, rateClassificationDto.rateNotes) &&
        Objects.equals(this.rateOrder, rateClassificationDto.rateOrder) &&
        Objects.equals(this.ratePlanCode, rateClassificationDto.ratePlanCode) &&
        Objects.equals(this.rateTags, rateClassificationDto.rateTags);
  }

  @Override
  public int hashCode() {
    return Objects.hash(additionalDescription, rateCategory, rateClassification, rateDescription, rateDisplaySet, rateLongDescription, rateName, rateNotes, rateOrder, ratePlanCode, rateTags);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateClassificationDto {\n");
    sb.append("    additionalDescription: ").append(toIndentedString(additionalDescription)).append("\n");
    sb.append("    rateCategory: ").append(toIndentedString(rateCategory)).append("\n");
    sb.append("    rateClassification: ").append(toIndentedString(rateClassification)).append("\n");
    sb.append("    rateDescription: ").append(toIndentedString(rateDescription)).append("\n");
    sb.append("    rateDisplaySet: ").append(toIndentedString(rateDisplaySet)).append("\n");
    sb.append("    rateLongDescription: ").append(toIndentedString(rateLongDescription)).append("\n");
    sb.append("    rateName: ").append(toIndentedString(rateName)).append("\n");
    sb.append("    rateNotes: ").append(toIndentedString(rateNotes)).append("\n");
    sb.append("    rateOrder: ").append(toIndentedString(rateOrder)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    rateTags: ").append(toIndentedString(rateTags)).append("\n");
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

