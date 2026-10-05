package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * RateClassificationDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:24.587145+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RateClassificationDto {

  private @Nullable String additionalDescription;

  private @Nullable String rateClassification;

  private @Nullable String rateDescription;

  private @Nullable String rateLongDescription;

  private @Nullable String rateName;

  private @Nullable String rateNotes;

  private @Nullable String rateOrder;

  private @Nullable String ratePlanCode;

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
        Objects.equals(this.rateClassification, rateClassificationDto.rateClassification) &&
        Objects.equals(this.rateDescription, rateClassificationDto.rateDescription) &&
        Objects.equals(this.rateLongDescription, rateClassificationDto.rateLongDescription) &&
        Objects.equals(this.rateName, rateClassificationDto.rateName) &&
        Objects.equals(this.rateNotes, rateClassificationDto.rateNotes) &&
        Objects.equals(this.rateOrder, rateClassificationDto.rateOrder) &&
        Objects.equals(this.ratePlanCode, rateClassificationDto.ratePlanCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(additionalDescription, rateClassification, rateDescription, rateLongDescription, rateName, rateNotes, rateOrder, ratePlanCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RateClassificationDto {\n");
    sb.append("    additionalDescription: ").append(toIndentedString(additionalDescription)).append("\n");
    sb.append("    rateClassification: ").append(toIndentedString(rateClassification)).append("\n");
    sb.append("    rateDescription: ").append(toIndentedString(rateDescription)).append("\n");
    sb.append("    rateLongDescription: ").append(toIndentedString(rateLongDescription)).append("\n");
    sb.append("    rateName: ").append(toIndentedString(rateName)).append("\n");
    sb.append("    rateNotes: ").append(toIndentedString(rateNotes)).append("\n");
    sb.append("    rateOrder: ").append(toIndentedString(rateOrder)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
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

