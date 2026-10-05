package uk.co.whitbread.basket.generated.models.rules;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * BusinessAllowanceRuleDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.380951+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BusinessAllowanceRuleDto {

  private @Nullable String pms;

  private @Nullable String sourceId;

  private @Nullable String sourceType;

  private @Nullable String targetId;

  private @Nullable String aemId;

  private @Nullable Boolean isTransactionCode;

  private @Nullable Boolean isApplicableDaily;

  private @Nullable Boolean isNotesMandatory;

  public BusinessAllowanceRuleDto pms(String pms) {
    this.pms = pms;
    return this;
  }

  /**
   * Get pms
   * @return pms
   */
  
  @Schema(name = "pms", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pms")
  public String getPms() {
    return pms;
  }

  public void setPms(String pms) {
    this.pms = pms;
  }

  public BusinessAllowanceRuleDto sourceId(String sourceId) {
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

  public BusinessAllowanceRuleDto sourceType(String sourceType) {
    this.sourceType = sourceType;
    return this;
  }

  /**
   * Get sourceType
   * @return sourceType
   */
  
  @Schema(name = "sourceType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sourceType")
  public String getSourceType() {
    return sourceType;
  }

  public void setSourceType(String sourceType) {
    this.sourceType = sourceType;
  }

  public BusinessAllowanceRuleDto targetId(String targetId) {
    this.targetId = targetId;
    return this;
  }

  /**
   * Get targetId
   * @return targetId
   */
  
  @Schema(name = "targetId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("targetId")
  public String getTargetId() {
    return targetId;
  }

  public void setTargetId(String targetId) {
    this.targetId = targetId;
  }

  public BusinessAllowanceRuleDto aemId(String aemId) {
    this.aemId = aemId;
    return this;
  }

  /**
   * Get aemId
   * @return aemId
   */
  
  @Schema(name = "aemId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("aemId")
  public String getAemId() {
    return aemId;
  }

  public void setAemId(String aemId) {
    this.aemId = aemId;
  }

  public BusinessAllowanceRuleDto isTransactionCode(Boolean isTransactionCode) {
    this.isTransactionCode = isTransactionCode;
    return this;
  }

  /**
   * Get isTransactionCode
   * @return isTransactionCode
   */
  
  @Schema(name = "isTransactionCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isTransactionCode")
  public Boolean getIsTransactionCode() {
    return isTransactionCode;
  }

  public void setIsTransactionCode(Boolean isTransactionCode) {
    this.isTransactionCode = isTransactionCode;
  }

  public BusinessAllowanceRuleDto isApplicableDaily(Boolean isApplicableDaily) {
    this.isApplicableDaily = isApplicableDaily;
    return this;
  }

  /**
   * Get isApplicableDaily
   * @return isApplicableDaily
   */
  
  @Schema(name = "isApplicableDaily", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isApplicableDaily")
  public Boolean getIsApplicableDaily() {
    return isApplicableDaily;
  }

  public void setIsApplicableDaily(Boolean isApplicableDaily) {
    this.isApplicableDaily = isApplicableDaily;
  }

  public BusinessAllowanceRuleDto isNotesMandatory(Boolean isNotesMandatory) {
    this.isNotesMandatory = isNotesMandatory;
    return this;
  }

  /**
   * Get isNotesMandatory
   * @return isNotesMandatory
   */
  
  @Schema(name = "isNotesMandatory", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isNotesMandatory")
  public Boolean getIsNotesMandatory() {
    return isNotesMandatory;
  }

  public void setIsNotesMandatory(Boolean isNotesMandatory) {
    this.isNotesMandatory = isNotesMandatory;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BusinessAllowanceRuleDto businessAllowanceRuleDto = (BusinessAllowanceRuleDto) o;
    return Objects.equals(this.pms, businessAllowanceRuleDto.pms) &&
        Objects.equals(this.sourceId, businessAllowanceRuleDto.sourceId) &&
        Objects.equals(this.sourceType, businessAllowanceRuleDto.sourceType) &&
        Objects.equals(this.targetId, businessAllowanceRuleDto.targetId) &&
        Objects.equals(this.aemId, businessAllowanceRuleDto.aemId) &&
        Objects.equals(this.isTransactionCode, businessAllowanceRuleDto.isTransactionCode) &&
        Objects.equals(this.isApplicableDaily, businessAllowanceRuleDto.isApplicableDaily) &&
        Objects.equals(this.isNotesMandatory, businessAllowanceRuleDto.isNotesMandatory);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pms, sourceId, sourceType, targetId, aemId, isTransactionCode, isApplicableDaily, isNotesMandatory);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BusinessAllowanceRuleDto {\n");
    sb.append("    pms: ").append(toIndentedString(pms)).append("\n");
    sb.append("    sourceId: ").append(toIndentedString(sourceId)).append("\n");
    sb.append("    sourceType: ").append(toIndentedString(sourceType)).append("\n");
    sb.append("    targetId: ").append(toIndentedString(targetId)).append("\n");
    sb.append("    aemId: ").append(toIndentedString(aemId)).append("\n");
    sb.append("    isTransactionCode: ").append(toIndentedString(isTransactionCode)).append("\n");
    sb.append("    isApplicableDaily: ").append(toIndentedString(isApplicableDaily)).append("\n");
    sb.append("    isNotesMandatory: ").append(toIndentedString(isNotesMandatory)).append("\n");
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

