package uk.co.whitbread.hotel.generated.models.basket;

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
 * RatePerNight
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RatePerNight {

  private @Nullable BigDecimal cityTaxAmountBeforeTax;

  private @Nullable BigDecimal cityTaxPerNight;

  private @Nullable BigDecimal cityTaxVat;

  private @Nullable BigDecimal grossPricePerNight;

  private @Nullable BigDecimal pricePerNight;

  private @Nullable String startDate;

  private @Nullable BigDecimal vatRate;

  public RatePerNight cityTaxAmountBeforeTax(BigDecimal cityTaxAmountBeforeTax) {
    this.cityTaxAmountBeforeTax = cityTaxAmountBeforeTax;
    return this;
  }

  /**
   * Get cityTaxAmountBeforeTax
   * @return cityTaxAmountBeforeTax
   */
  @Valid 
  @Schema(name = "cityTaxAmountBeforeTax", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cityTaxAmountBeforeTax")
  public BigDecimal getCityTaxAmountBeforeTax() {
    return cityTaxAmountBeforeTax;
  }

  public void setCityTaxAmountBeforeTax(BigDecimal cityTaxAmountBeforeTax) {
    this.cityTaxAmountBeforeTax = cityTaxAmountBeforeTax;
  }

  public RatePerNight cityTaxPerNight(BigDecimal cityTaxPerNight) {
    this.cityTaxPerNight = cityTaxPerNight;
    return this;
  }

  /**
   * Get cityTaxPerNight
   * @return cityTaxPerNight
   */
  @Valid 
  @Schema(name = "cityTaxPerNight", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cityTaxPerNight")
  public BigDecimal getCityTaxPerNight() {
    return cityTaxPerNight;
  }

  public void setCityTaxPerNight(BigDecimal cityTaxPerNight) {
    this.cityTaxPerNight = cityTaxPerNight;
  }

  public RatePerNight cityTaxVat(BigDecimal cityTaxVat) {
    this.cityTaxVat = cityTaxVat;
    return this;
  }

  /**
   * Get cityTaxVat
   * @return cityTaxVat
   */
  @Valid 
  @Schema(name = "cityTaxVat", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cityTaxVat")
  public BigDecimal getCityTaxVat() {
    return cityTaxVat;
  }

  public void setCityTaxVat(BigDecimal cityTaxVat) {
    this.cityTaxVat = cityTaxVat;
  }

  public RatePerNight grossPricePerNight(BigDecimal grossPricePerNight) {
    this.grossPricePerNight = grossPricePerNight;
    return this;
  }

  /**
   * Get grossPricePerNight
   * @return grossPricePerNight
   */
  @Valid 
  @Schema(name = "grossPricePerNight", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("grossPricePerNight")
  public BigDecimal getGrossPricePerNight() {
    return grossPricePerNight;
  }

  public void setGrossPricePerNight(BigDecimal grossPricePerNight) {
    this.grossPricePerNight = grossPricePerNight;
  }

  public RatePerNight pricePerNight(BigDecimal pricePerNight) {
    this.pricePerNight = pricePerNight;
    return this;
  }

  /**
   * Get pricePerNight
   * @return pricePerNight
   */
  @Valid 
  @Schema(name = "pricePerNight", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pricePerNight")
  public BigDecimal getPricePerNight() {
    return pricePerNight;
  }

  public void setPricePerNight(BigDecimal pricePerNight) {
    this.pricePerNight = pricePerNight;
  }

  public RatePerNight startDate(String startDate) {
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

  public RatePerNight vatRate(BigDecimal vatRate) {
    this.vatRate = vatRate;
    return this;
  }

  /**
   * Get vatRate
   * @return vatRate
   */
  @Valid 
  @Schema(name = "vatRate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("vatRate")
  public BigDecimal getVatRate() {
    return vatRate;
  }

  public void setVatRate(BigDecimal vatRate) {
    this.vatRate = vatRate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RatePerNight ratePerNight = (RatePerNight) o;
    return Objects.equals(this.cityTaxAmountBeforeTax, ratePerNight.cityTaxAmountBeforeTax) &&
        Objects.equals(this.cityTaxPerNight, ratePerNight.cityTaxPerNight) &&
        Objects.equals(this.cityTaxVat, ratePerNight.cityTaxVat) &&
        Objects.equals(this.grossPricePerNight, ratePerNight.grossPricePerNight) &&
        Objects.equals(this.pricePerNight, ratePerNight.pricePerNight) &&
        Objects.equals(this.startDate, ratePerNight.startDate) &&
        Objects.equals(this.vatRate, ratePerNight.vatRate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cityTaxAmountBeforeTax, cityTaxPerNight, cityTaxVat, grossPricePerNight, pricePerNight, startDate, vatRate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RatePerNight {\n");
    sb.append("    cityTaxAmountBeforeTax: ").append(toIndentedString(cityTaxAmountBeforeTax)).append("\n");
    sb.append("    cityTaxPerNight: ").append(toIndentedString(cityTaxPerNight)).append("\n");
    sb.append("    cityTaxVat: ").append(toIndentedString(cityTaxVat)).append("\n");
    sb.append("    grossPricePerNight: ").append(toIndentedString(grossPricePerNight)).append("\n");
    sb.append("    pricePerNight: ").append(toIndentedString(pricePerNight)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    vatRate: ").append(toIndentedString(vatRate)).append("\n");
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

