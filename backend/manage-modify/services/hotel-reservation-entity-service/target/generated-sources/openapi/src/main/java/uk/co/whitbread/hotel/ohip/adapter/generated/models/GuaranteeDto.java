package uk.co.whitbread.hotel.ohip.adapter.generated.models;

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
 * GuaranteeDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class GuaranteeDto {

  private @Nullable String guaranteeCode;

  private @Nullable Boolean onHold;

  private @Nullable String shortDescription;

  public GuaranteeDto guaranteeCode(String guaranteeCode) {
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

  public GuaranteeDto onHold(Boolean onHold) {
    this.onHold = onHold;
    return this;
  }

  /**
   * Get onHold
   * @return onHold
   */
  
  @Schema(name = "onHold", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("onHold")
  public Boolean getOnHold() {
    return onHold;
  }

  public void setOnHold(Boolean onHold) {
    this.onHold = onHold;
  }

  public GuaranteeDto shortDescription(String shortDescription) {
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
    GuaranteeDto guaranteeDto = (GuaranteeDto) o;
    return Objects.equals(this.guaranteeCode, guaranteeDto.guaranteeCode) &&
        Objects.equals(this.onHold, guaranteeDto.onHold) &&
        Objects.equals(this.shortDescription, guaranteeDto.shortDescription);
  }

  @Override
  public int hashCode() {
    return Objects.hash(guaranteeCode, onHold, shortDescription);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GuaranteeDto {\n");
    sb.append("    guaranteeCode: ").append(toIndentedString(guaranteeCode)).append("\n");
    sb.append("    onHold: ").append(toIndentedString(onHold)).append("\n");
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

