package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ReservationPackagesDetailsResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationPackagesDetailsResponseDto {

  private @Nullable BigDecimal computedPrice;

  private @Nullable String description;

  private @Nullable String endDate;

  private @Nullable BigDecimal grossPrice;

  private @Nullable String packageCode;

  private @Nullable String startDate;

  private @Nullable Integer totalQuantity;

  private @Nullable BigDecimal unitPrice;

  private @Nullable BigDecimal vatTax;

  public ReservationPackagesDetailsResponseDto computedPrice(BigDecimal computedPrice) {
    this.computedPrice = computedPrice;
    return this;
  }

  /**
   * Get computedPrice
   * @return computedPrice
   */
  @Valid 
  @Schema(name = "computedPrice", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("computedPrice")
  public BigDecimal getComputedPrice() {
    return computedPrice;
  }

  public void setComputedPrice(BigDecimal computedPrice) {
    this.computedPrice = computedPrice;
  }

  public ReservationPackagesDetailsResponseDto description(String description) {
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

  public ReservationPackagesDetailsResponseDto endDate(String endDate) {
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

  public ReservationPackagesDetailsResponseDto grossPrice(BigDecimal grossPrice) {
    this.grossPrice = grossPrice;
    return this;
  }

  /**
   * Get grossPrice
   * @return grossPrice
   */
  @Valid 
  @Schema(name = "grossPrice", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("grossPrice")
  public BigDecimal getGrossPrice() {
    return grossPrice;
  }

  public void setGrossPrice(BigDecimal grossPrice) {
    this.grossPrice = grossPrice;
  }

  public ReservationPackagesDetailsResponseDto packageCode(String packageCode) {
    this.packageCode = packageCode;
    return this;
  }

  /**
   * Get packageCode
   * @return packageCode
   */
  
  @Schema(name = "packageCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packageCode")
  public String getPackageCode() {
    return packageCode;
  }

  public void setPackageCode(String packageCode) {
    this.packageCode = packageCode;
  }

  public ReservationPackagesDetailsResponseDto startDate(String startDate) {
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

  public ReservationPackagesDetailsResponseDto totalQuantity(Integer totalQuantity) {
    this.totalQuantity = totalQuantity;
    return this;
  }

  /**
   * Get totalQuantity
   * @return totalQuantity
   */
  
  @Schema(name = "totalQuantity", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalQuantity")
  public Integer getTotalQuantity() {
    return totalQuantity;
  }

  public void setTotalQuantity(Integer totalQuantity) {
    this.totalQuantity = totalQuantity;
  }

  public ReservationPackagesDetailsResponseDto unitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
    return this;
  }

  /**
   * Get unitPrice
   * @return unitPrice
   */
  @Valid 
  @Schema(name = "unitPrice", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("unitPrice")
  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public ReservationPackagesDetailsResponseDto vatTax(BigDecimal vatTax) {
    this.vatTax = vatTax;
    return this;
  }

  /**
   * Get vatTax
   * @return vatTax
   */
  @Valid 
  @Schema(name = "vatTax", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("vatTax")
  public BigDecimal getVatTax() {
    return vatTax;
  }

  public void setVatTax(BigDecimal vatTax) {
    this.vatTax = vatTax;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationPackagesDetailsResponseDto reservationPackagesDetailsResponseDto = (ReservationPackagesDetailsResponseDto) o;
    return Objects.equals(this.computedPrice, reservationPackagesDetailsResponseDto.computedPrice) &&
        Objects.equals(this.description, reservationPackagesDetailsResponseDto.description) &&
        Objects.equals(this.endDate, reservationPackagesDetailsResponseDto.endDate) &&
        Objects.equals(this.grossPrice, reservationPackagesDetailsResponseDto.grossPrice) &&
        Objects.equals(this.packageCode, reservationPackagesDetailsResponseDto.packageCode) &&
        Objects.equals(this.startDate, reservationPackagesDetailsResponseDto.startDate) &&
        Objects.equals(this.totalQuantity, reservationPackagesDetailsResponseDto.totalQuantity) &&
        Objects.equals(this.unitPrice, reservationPackagesDetailsResponseDto.unitPrice) &&
        Objects.equals(this.vatTax, reservationPackagesDetailsResponseDto.vatTax);
  }

  @Override
  public int hashCode() {
    return Objects.hash(computedPrice, description, endDate, grossPrice, packageCode, startDate, totalQuantity, unitPrice, vatTax);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationPackagesDetailsResponseDto {\n");
    sb.append("    computedPrice: ").append(toIndentedString(computedPrice)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
    sb.append("    grossPrice: ").append(toIndentedString(grossPrice)).append("\n");
    sb.append("    packageCode: ").append(toIndentedString(packageCode)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    totalQuantity: ").append(toIndentedString(totalQuantity)).append("\n");
    sb.append("    unitPrice: ").append(toIndentedString(unitPrice)).append("\n");
    sb.append("    vatTax: ").append(toIndentedString(vatTax)).append("\n");
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

