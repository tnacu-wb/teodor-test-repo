package uk.co.whitbread.basket.generated.models.ohip;

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
 * ReservationAmountsDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationAmountsDto {

  private @Nullable String currencyCode;

  private @Nullable BigDecimal deposit;

  private @Nullable BigDecimal discount;

  private @Nullable BigDecimal gross;

  private @Nullable BigDecimal net;

  private @Nullable BigDecimal outStandingCostOfStay;

  private @Nullable BigDecimal totalCostOfStay;

  public ReservationAmountsDto currencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
    return this;
  }

  /**
   * Get currencyCode
   * @return currencyCode
   */
  
  @Schema(name = "currencyCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currencyCode")
  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  public ReservationAmountsDto deposit(BigDecimal deposit) {
    this.deposit = deposit;
    return this;
  }

  /**
   * Get deposit
   * @return deposit
   */
  @Valid 
  @Schema(name = "deposit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("deposit")
  public BigDecimal getDeposit() {
    return deposit;
  }

  public void setDeposit(BigDecimal deposit) {
    this.deposit = deposit;
  }

  public ReservationAmountsDto discount(BigDecimal discount) {
    this.discount = discount;
    return this;
  }

  /**
   * Get discount
   * @return discount
   */
  @Valid 
  @Schema(name = "discount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("discount")
  public BigDecimal getDiscount() {
    return discount;
  }

  public void setDiscount(BigDecimal discount) {
    this.discount = discount;
  }

  public ReservationAmountsDto gross(BigDecimal gross) {
    this.gross = gross;
    return this;
  }

  /**
   * Get gross
   * @return gross
   */
  @Valid 
  @Schema(name = "gross", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("gross")
  public BigDecimal getGross() {
    return gross;
  }

  public void setGross(BigDecimal gross) {
    this.gross = gross;
  }

  public ReservationAmountsDto net(BigDecimal net) {
    this.net = net;
    return this;
  }

  /**
   * Get net
   * @return net
   */
  @Valid 
  @Schema(name = "net", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("net")
  public BigDecimal getNet() {
    return net;
  }

  public void setNet(BigDecimal net) {
    this.net = net;
  }

  public ReservationAmountsDto outStandingCostOfStay(BigDecimal outStandingCostOfStay) {
    this.outStandingCostOfStay = outStandingCostOfStay;
    return this;
  }

  /**
   * Get outStandingCostOfStay
   * @return outStandingCostOfStay
   */
  @Valid 
  @Schema(name = "outStandingCostOfStay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("outStandingCostOfStay")
  public BigDecimal getOutStandingCostOfStay() {
    return outStandingCostOfStay;
  }

  public void setOutStandingCostOfStay(BigDecimal outStandingCostOfStay) {
    this.outStandingCostOfStay = outStandingCostOfStay;
  }

  public ReservationAmountsDto totalCostOfStay(BigDecimal totalCostOfStay) {
    this.totalCostOfStay = totalCostOfStay;
    return this;
  }

  /**
   * Get totalCostOfStay
   * @return totalCostOfStay
   */
  @Valid 
  @Schema(name = "totalCostOfStay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalCostOfStay")
  public BigDecimal getTotalCostOfStay() {
    return totalCostOfStay;
  }

  public void setTotalCostOfStay(BigDecimal totalCostOfStay) {
    this.totalCostOfStay = totalCostOfStay;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationAmountsDto reservationAmountsDto = (ReservationAmountsDto) o;
    return Objects.equals(this.currencyCode, reservationAmountsDto.currencyCode) &&
        Objects.equals(this.deposit, reservationAmountsDto.deposit) &&
        Objects.equals(this.discount, reservationAmountsDto.discount) &&
        Objects.equals(this.gross, reservationAmountsDto.gross) &&
        Objects.equals(this.net, reservationAmountsDto.net) &&
        Objects.equals(this.outStandingCostOfStay, reservationAmountsDto.outStandingCostOfStay) &&
        Objects.equals(this.totalCostOfStay, reservationAmountsDto.totalCostOfStay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currencyCode, deposit, discount, gross, net, outStandingCostOfStay, totalCostOfStay);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationAmountsDto {\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    deposit: ").append(toIndentedString(deposit)).append("\n");
    sb.append("    discount: ").append(toIndentedString(discount)).append("\n");
    sb.append("    gross: ").append(toIndentedString(gross)).append("\n");
    sb.append("    net: ").append(toIndentedString(net)).append("\n");
    sb.append("    outStandingCostOfStay: ").append(toIndentedString(outStandingCostOfStay)).append("\n");
    sb.append("    totalCostOfStay: ").append(toIndentedString(totalCostOfStay)).append("\n");
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

