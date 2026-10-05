package uk.co.whitbread.hotel.ohip.adapter.generated.models;

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
 * CheckInTaxTypeDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CheckInTaxTypeDto {

  private @Nullable String code;

  private @Nullable Boolean collectingAgentTax;

  private @Nullable String description;

  private @Nullable Boolean printAutoAdjust;

  public CheckInTaxTypeDto code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
   */
  
  @Schema(name = "code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("code")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public CheckInTaxTypeDto collectingAgentTax(Boolean collectingAgentTax) {
    this.collectingAgentTax = collectingAgentTax;
    return this;
  }

  /**
   * Get collectingAgentTax
   * @return collectingAgentTax
   */
  
  @Schema(name = "collectingAgentTax", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("collectingAgentTax")
  public Boolean getCollectingAgentTax() {
    return collectingAgentTax;
  }

  public void setCollectingAgentTax(Boolean collectingAgentTax) {
    this.collectingAgentTax = collectingAgentTax;
  }

  public CheckInTaxTypeDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public CheckInTaxTypeDto printAutoAdjust(Boolean printAutoAdjust) {
    this.printAutoAdjust = printAutoAdjust;
    return this;
  }

  /**
   * Get printAutoAdjust
   * @return printAutoAdjust
   */
  
  @Schema(name = "printAutoAdjust", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("printAutoAdjust")
  public Boolean getPrintAutoAdjust() {
    return printAutoAdjust;
  }

  public void setPrintAutoAdjust(Boolean printAutoAdjust) {
    this.printAutoAdjust = printAutoAdjust;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CheckInTaxTypeDto checkInTaxTypeDto = (CheckInTaxTypeDto) o;
    return Objects.equals(this.code, checkInTaxTypeDto.code) &&
        Objects.equals(this.collectingAgentTax, checkInTaxTypeDto.collectingAgentTax) &&
        Objects.equals(this.description, checkInTaxTypeDto.description) &&
        Objects.equals(this.printAutoAdjust, checkInTaxTypeDto.printAutoAdjust);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, collectingAgentTax, description, printAutoAdjust);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CheckInTaxTypeDto {\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    collectingAgentTax: ").append(toIndentedString(collectingAgentTax)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    printAutoAdjust: ").append(toIndentedString(printAutoAdjust)).append("\n");
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

