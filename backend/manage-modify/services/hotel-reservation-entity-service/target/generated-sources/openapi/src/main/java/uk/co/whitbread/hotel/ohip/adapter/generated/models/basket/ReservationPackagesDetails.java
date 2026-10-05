package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ReservationPackagesDetails
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationPackagesDetails {

  private @Nullable BigDecimal computedPrice;

  private @Nullable String description;

  private @Nullable String packageCode;

  private @Nullable Integer totalQuantity;

  private @Nullable BigDecimal unitPrice;

  private @Nullable BigDecimal vatTax;

  public ReservationPackagesDetails computedPrice(BigDecimal computedPrice) {
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

  public ReservationPackagesDetails description(String description) {
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

  public ReservationPackagesDetails packageCode(String packageCode) {
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

  public ReservationPackagesDetails totalQuantity(Integer totalQuantity) {
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

  public ReservationPackagesDetails unitPrice(BigDecimal unitPrice) {
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

  public ReservationPackagesDetails vatTax(BigDecimal vatTax) {
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
    ReservationPackagesDetails reservationPackagesDetails = (ReservationPackagesDetails) o;
    return Objects.equals(this.computedPrice, reservationPackagesDetails.computedPrice) &&
        Objects.equals(this.description, reservationPackagesDetails.description) &&
        Objects.equals(this.packageCode, reservationPackagesDetails.packageCode) &&
        Objects.equals(this.totalQuantity, reservationPackagesDetails.totalQuantity) &&
        Objects.equals(this.unitPrice, reservationPackagesDetails.unitPrice) &&
        Objects.equals(this.vatTax, reservationPackagesDetails.vatTax);
  }

  @Override
  public int hashCode() {
    return Objects.hash(computedPrice, description, packageCode, totalQuantity, unitPrice, vatTax);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationPackagesDetails {\n");
    sb.append("    computedPrice: ").append(toIndentedString(computedPrice)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    packageCode: ").append(toIndentedString(packageCode)).append("\n");
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

