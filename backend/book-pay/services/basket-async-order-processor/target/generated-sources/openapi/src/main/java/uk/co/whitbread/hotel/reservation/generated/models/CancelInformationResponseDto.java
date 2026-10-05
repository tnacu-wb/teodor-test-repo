package uk.co.whitbread.hotel.reservation.generated.models;

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
 * CancelInformationResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:09:52.163805+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CancelInformationResponseDto {

  private @Nullable Boolean isAmendable;

  private @Nullable Boolean isCancellable;

  private @Nullable Boolean isRuleCompliant;

  public CancelInformationResponseDto isAmendable(Boolean isAmendable) {
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

  public CancelInformationResponseDto isCancellable(Boolean isCancellable) {
    this.isCancellable = isCancellable;
    return this;
  }

  /**
   * Get isCancellable
   * @return isCancellable
   */
  
  @Schema(name = "isCancellable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isCancellable")
  public Boolean getIsCancellable() {
    return isCancellable;
  }

  public void setIsCancellable(Boolean isCancellable) {
    this.isCancellable = isCancellable;
  }

  public CancelInformationResponseDto isRuleCompliant(Boolean isRuleCompliant) {
    this.isRuleCompliant = isRuleCompliant;
    return this;
  }

  /**
   * Get isRuleCompliant
   * @return isRuleCompliant
   */
  
  @Schema(name = "isRuleCompliant", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isRuleCompliant")
  public Boolean getIsRuleCompliant() {
    return isRuleCompliant;
  }

  public void setIsRuleCompliant(Boolean isRuleCompliant) {
    this.isRuleCompliant = isRuleCompliant;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CancelInformationResponseDto cancelInformationResponseDto = (CancelInformationResponseDto) o;
    return Objects.equals(this.isAmendable, cancelInformationResponseDto.isAmendable) &&
        Objects.equals(this.isCancellable, cancelInformationResponseDto.isCancellable) &&
        Objects.equals(this.isRuleCompliant, cancelInformationResponseDto.isRuleCompliant);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isAmendable, isCancellable, isRuleCompliant);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CancelInformationResponseDto {\n");
    sb.append("    isAmendable: ").append(toIndentedString(isAmendable)).append("\n");
    sb.append("    isCancellable: ").append(toIndentedString(isCancellable)).append("\n");
    sb.append("    isRuleCompliant: ").append(toIndentedString(isRuleCompliant)).append("\n");
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

