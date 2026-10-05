package uk.co.whitbread.content.entity.service.generated.models.content;

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
 * AncillaryCloseoutItemDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AncillaryCloseoutItemDto {

  private @Nullable String endDate;

  private @Nullable String serviceCode;

  private @Nullable String startDate;

  private @Nullable String text;

  private @Nullable String upsellCodes;

  public AncillaryCloseoutItemDto endDate(String endDate) {
    this.endDate = endDate;
    return this;
  }

  /**
   * Get endDate
   * @return endDate
   */
  
  @Schema(name = "endDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("endDate")
  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public AncillaryCloseoutItemDto serviceCode(String serviceCode) {
    this.serviceCode = serviceCode;
    return this;
  }

  /**
   * Get serviceCode
   * @return serviceCode
   */
  
  @Schema(name = "serviceCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("serviceCode")
  public String getServiceCode() {
    return serviceCode;
  }

  public void setServiceCode(String serviceCode) {
    this.serviceCode = serviceCode;
  }

  public AncillaryCloseoutItemDto startDate(String startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Get startDate
   * @return startDate
   */
  
  @Schema(name = "startDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("startDate")
  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public AncillaryCloseoutItemDto text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Get text
   * @return text
   */
  
  @Schema(name = "text", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("text")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public AncillaryCloseoutItemDto upsellCodes(String upsellCodes) {
    this.upsellCodes = upsellCodes;
    return this;
  }

  /**
   * Get upsellCodes
   * @return upsellCodes
   */
  
  @Schema(name = "upsellCodes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("upsellCodes")
  public String getUpsellCodes() {
    return upsellCodes;
  }

  public void setUpsellCodes(String upsellCodes) {
    this.upsellCodes = upsellCodes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AncillaryCloseoutItemDto ancillaryCloseoutItemDto = (AncillaryCloseoutItemDto) o;
    return Objects.equals(this.endDate, ancillaryCloseoutItemDto.endDate) &&
        Objects.equals(this.serviceCode, ancillaryCloseoutItemDto.serviceCode) &&
        Objects.equals(this.startDate, ancillaryCloseoutItemDto.startDate) &&
        Objects.equals(this.text, ancillaryCloseoutItemDto.text) &&
        Objects.equals(this.upsellCodes, ancillaryCloseoutItemDto.upsellCodes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(endDate, serviceCode, startDate, text, upsellCodes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AncillaryCloseoutItemDto {\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    serviceCode: ").append(toIndentedString(serviceCode)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("    upsellCodes: ").append(toIndentedString(upsellCodes)).append("\n");
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

