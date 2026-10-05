package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.BillingResponseDto;
import uk.co.whitbread.basket.generated.models.ohip.ReservationsIdDetailsResponseDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ReservationIdDetailsDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationIdDetailsDto {

  private @Nullable BigDecimal amountPaid;

  private @Nullable BigDecimal balanceOutstanding;

  private @Nullable BillingResponseDto billing;

  private @Nullable String currencyCode;

  private @Nullable BigDecimal newTotal;

  private @Nullable String policyCode;

  private @Nullable BigDecimal previousTotal;

  private @Nullable ReservationsIdDetailsResponseDto reservationsDetailsResponse;

  private @Nullable BigDecimal totalCost;

  public ReservationIdDetailsDto amountPaid(BigDecimal amountPaid) {
    this.amountPaid = amountPaid;
    return this;
  }

  /**
   * Get amountPaid
   * @return amountPaid
   */
  @Valid 
  @Schema(name = "amountPaid", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amountPaid")
  public BigDecimal getAmountPaid() {
    return amountPaid;
  }

  public void setAmountPaid(BigDecimal amountPaid) {
    this.amountPaid = amountPaid;
  }

  public ReservationIdDetailsDto balanceOutstanding(BigDecimal balanceOutstanding) {
    this.balanceOutstanding = balanceOutstanding;
    return this;
  }

  /**
   * Get balanceOutstanding
   * @return balanceOutstanding
   */
  @Valid 
  @Schema(name = "balanceOutstanding", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("balanceOutstanding")
  public BigDecimal getBalanceOutstanding() {
    return balanceOutstanding;
  }

  public void setBalanceOutstanding(BigDecimal balanceOutstanding) {
    this.balanceOutstanding = balanceOutstanding;
  }

  public ReservationIdDetailsDto billing(BillingResponseDto billing) {
    this.billing = billing;
    return this;
  }

  /**
   * Get billing
   * @return billing
   */
  @Valid 
  @Schema(name = "billing", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("billing")
  public BillingResponseDto getBilling() {
    return billing;
  }

  public void setBilling(BillingResponseDto billing) {
    this.billing = billing;
  }

  public ReservationIdDetailsDto currencyCode(String currencyCode) {
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

  public ReservationIdDetailsDto newTotal(BigDecimal newTotal) {
    this.newTotal = newTotal;
    return this;
  }

  /**
   * Get newTotal
   * @return newTotal
   */
  @Valid 
  @Schema(name = "newTotal", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("newTotal")
  public BigDecimal getNewTotal() {
    return newTotal;
  }

  public void setNewTotal(BigDecimal newTotal) {
    this.newTotal = newTotal;
  }

  public ReservationIdDetailsDto policyCode(String policyCode) {
    this.policyCode = policyCode;
    return this;
  }

  /**
   * Get policyCode
   * @return policyCode
   */
  
  @Schema(name = "policyCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("policyCode")
  public String getPolicyCode() {
    return policyCode;
  }

  public void setPolicyCode(String policyCode) {
    this.policyCode = policyCode;
  }

  public ReservationIdDetailsDto previousTotal(BigDecimal previousTotal) {
    this.previousTotal = previousTotal;
    return this;
  }

  /**
   * Get previousTotal
   * @return previousTotal
   */
  @Valid 
  @Schema(name = "previousTotal", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("previousTotal")
  public BigDecimal getPreviousTotal() {
    return previousTotal;
  }

  public void setPreviousTotal(BigDecimal previousTotal) {
    this.previousTotal = previousTotal;
  }

  public ReservationIdDetailsDto reservationsDetailsResponse(ReservationsIdDetailsResponseDto reservationsDetailsResponse) {
    this.reservationsDetailsResponse = reservationsDetailsResponse;
    return this;
  }

  /**
   * Get reservationsDetailsResponse
   * @return reservationsDetailsResponse
   */
  @Valid 
  @Schema(name = "reservationsDetailsResponse", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationsDetailsResponse")
  public ReservationsIdDetailsResponseDto getReservationsDetailsResponse() {
    return reservationsDetailsResponse;
  }

  public void setReservationsDetailsResponse(ReservationsIdDetailsResponseDto reservationsDetailsResponse) {
    this.reservationsDetailsResponse = reservationsDetailsResponse;
  }

  public ReservationIdDetailsDto totalCost(BigDecimal totalCost) {
    this.totalCost = totalCost;
    return this;
  }

  /**
   * Get totalCost
   * @return totalCost
   */
  @Valid 
  @Schema(name = "totalCost", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalCost")
  public BigDecimal getTotalCost() {
    return totalCost;
  }

  public void setTotalCost(BigDecimal totalCost) {
    this.totalCost = totalCost;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationIdDetailsDto reservationIdDetailsDto = (ReservationIdDetailsDto) o;
    return Objects.equals(this.amountPaid, reservationIdDetailsDto.amountPaid) &&
        Objects.equals(this.balanceOutstanding, reservationIdDetailsDto.balanceOutstanding) &&
        Objects.equals(this.billing, reservationIdDetailsDto.billing) &&
        Objects.equals(this.currencyCode, reservationIdDetailsDto.currencyCode) &&
        Objects.equals(this.newTotal, reservationIdDetailsDto.newTotal) &&
        Objects.equals(this.policyCode, reservationIdDetailsDto.policyCode) &&
        Objects.equals(this.previousTotal, reservationIdDetailsDto.previousTotal) &&
        Objects.equals(this.reservationsDetailsResponse, reservationIdDetailsDto.reservationsDetailsResponse) &&
        Objects.equals(this.totalCost, reservationIdDetailsDto.totalCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amountPaid, balanceOutstanding, billing, currencyCode, newTotal, policyCode, previousTotal, reservationsDetailsResponse, totalCost);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationIdDetailsDto {\n");
    sb.append("    amountPaid: ").append(toIndentedString(amountPaid)).append("\n");
    sb.append("    balanceOutstanding: ").append(toIndentedString(balanceOutstanding)).append("\n");
    sb.append("    billing: ").append(toIndentedString(billing)).append("\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    newTotal: ").append(toIndentedString(newTotal)).append("\n");
    sb.append("    policyCode: ").append(toIndentedString(policyCode)).append("\n");
    sb.append("    previousTotal: ").append(toIndentedString(previousTotal)).append("\n");
    sb.append("    reservationsDetailsResponse: ").append(toIndentedString(reservationsDetailsResponse)).append("\n");
    sb.append("    totalCost: ").append(toIndentedString(totalCost)).append("\n");
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

