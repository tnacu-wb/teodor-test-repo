package uk.co.whitbread.content.entity.service.generated.models.content;

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
 * OfferDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferDto {

  private @Nullable String cellCode;

  private @Nullable String corporateId;

  private @Nullable Integer maxRooms;

  private @Nullable Integer numberOfNights;

  private @Nullable String page;

  private @Nullable String ratePlanCode;

  public OfferDto cellCode(String cellCode) {
    this.cellCode = cellCode;
    return this;
  }

  /**
   * Get cellCode
   * @return cellCode
   */
  
  @Schema(name = "cellCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cellCode")
  public String getCellCode() {
    return cellCode;
  }

  public void setCellCode(String cellCode) {
    this.cellCode = cellCode;
  }

  public OfferDto corporateId(String corporateId) {
    this.corporateId = corporateId;
    return this;
  }

  /**
   * Get corporateId
   * @return corporateId
   */
  
  @Schema(name = "corporateId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("corporateId")
  public String getCorporateId() {
    return corporateId;
  }

  public void setCorporateId(String corporateId) {
    this.corporateId = corporateId;
  }

  public OfferDto maxRooms(Integer maxRooms) {
    this.maxRooms = maxRooms;
    return this;
  }

  /**
   * Get maxRooms
   * @return maxRooms
   */
  
  @Schema(name = "maxRooms", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxRooms")
  public Integer getMaxRooms() {
    return maxRooms;
  }

  public void setMaxRooms(Integer maxRooms) {
    this.maxRooms = maxRooms;
  }

  public OfferDto numberOfNights(Integer numberOfNights) {
    this.numberOfNights = numberOfNights;
    return this;
  }

  /**
   * Get numberOfNights
   * @return numberOfNights
   */
  
  @Schema(name = "numberOfNights", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("numberOfNights")
  public Integer getNumberOfNights() {
    return numberOfNights;
  }

  public void setNumberOfNights(Integer numberOfNights) {
    this.numberOfNights = numberOfNights;
  }

  public OfferDto page(String page) {
    this.page = page;
    return this;
  }

  /**
   * Get page
   * @return page
   */
  
  @Schema(name = "page", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("page")
  public String getPage() {
    return page;
  }

  public void setPage(String page) {
    this.page = page;
  }

  public OfferDto ratePlanCode(String ratePlanCode) {
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
    OfferDto offerDto = (OfferDto) o;
    return Objects.equals(this.cellCode, offerDto.cellCode) &&
        Objects.equals(this.corporateId, offerDto.corporateId) &&
        Objects.equals(this.maxRooms, offerDto.maxRooms) &&
        Objects.equals(this.numberOfNights, offerDto.numberOfNights) &&
        Objects.equals(this.page, offerDto.page) &&
        Objects.equals(this.ratePlanCode, offerDto.ratePlanCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cellCode, corporateId, maxRooms, numberOfNights, page, ratePlanCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferDto {\n");
    sb.append("    cellCode: ").append(toIndentedString(cellCode)).append("\n");
    sb.append("    corporateId: ").append(toIndentedString(corporateId)).append("\n");
    sb.append("    maxRooms: ").append(toIndentedString(maxRooms)).append("\n");
    sb.append("    numberOfNights: ").append(toIndentedString(numberOfNights)).append("\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
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

