package uk.co.whitbread.basket.generated.models.ohip;

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
 * CheckInGuaranteeDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CheckInGuaranteeDto {

  private @Nullable String guaranteeCode;

  private @Nullable String shortDescription;

  public CheckInGuaranteeDto guaranteeCode(String guaranteeCode) {
    this.guaranteeCode = guaranteeCode;
    return this;
  }

  /**
   * Get guaranteeCode
   * @return guaranteeCode
   */
  
  @Schema(name = "guaranteeCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guaranteeCode")
  public String getGuaranteeCode() {
    return guaranteeCode;
  }

  public void setGuaranteeCode(String guaranteeCode) {
    this.guaranteeCode = guaranteeCode;
  }

  public CheckInGuaranteeDto shortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
    return this;
  }

  /**
   * Get shortDescription
   * @return shortDescription
   */
  
  @Schema(name = "shortDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("shortDescription")
  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CheckInGuaranteeDto checkInGuaranteeDto = (CheckInGuaranteeDto) o;
    return Objects.equals(this.guaranteeCode, checkInGuaranteeDto.guaranteeCode) &&
        Objects.equals(this.shortDescription, checkInGuaranteeDto.shortDescription);
  }

  @Override
  public int hashCode() {
    return Objects.hash(guaranteeCode, shortDescription);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CheckInGuaranteeDto {\n");
    sb.append("    guaranteeCode: ").append(toIndentedString(guaranteeCode)).append("\n");
    sb.append("    shortDescription: ").append(toIndentedString(shortDescription)).append("\n");
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

