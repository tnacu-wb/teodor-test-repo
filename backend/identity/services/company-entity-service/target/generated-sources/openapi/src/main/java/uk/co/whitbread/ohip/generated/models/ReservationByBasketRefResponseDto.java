package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.ReservationByIdDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ReservationByBasketRefResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationByBasketRefResponseDto {

  private @Nullable BigDecimal amountPaid;

  private @Nullable BigDecimal balanceOutstanding;

  private @Nullable String currencyCode;

  private @Nullable BigDecimal discount;

  private @Nullable String hotelId;

  private @Nullable Boolean isCnp;

  private @Nullable BigDecimal newTotal;

  private @Nullable String policyCode;

  private @Nullable BigDecimal previousTotal;

  @Valid
  private List<@Valid ReservationByIdDto> reservationByIdList = new ArrayList<>();

  private @Nullable BigDecimal totalCost;

  private @Nullable BigDecimal totalCostWoDiscount;

  public ReservationByBasketRefResponseDto amountPaid(BigDecimal amountPaid) {
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

  public ReservationByBasketRefResponseDto balanceOutstanding(BigDecimal balanceOutstanding) {
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

  public ReservationByBasketRefResponseDto currencyCode(String currencyCode) {
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

  public ReservationByBasketRefResponseDto discount(BigDecimal discount) {
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

  public ReservationByBasketRefResponseDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public ReservationByBasketRefResponseDto isCnp(Boolean isCnp) {
    this.isCnp = isCnp;
    return this;
  }

  /**
   * Get isCnp
   * @return isCnp
   */
  
  @Schema(name = "isCnp", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isCnp")
  public Boolean getIsCnp() {
    return isCnp;
  }

  public void setIsCnp(Boolean isCnp) {
    this.isCnp = isCnp;
  }

  public ReservationByBasketRefResponseDto newTotal(BigDecimal newTotal) {
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

  public ReservationByBasketRefResponseDto policyCode(String policyCode) {
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

  public ReservationByBasketRefResponseDto previousTotal(BigDecimal previousTotal) {
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

  public ReservationByBasketRefResponseDto reservationByIdList(List<@Valid ReservationByIdDto> reservationByIdList) {
    this.reservationByIdList = reservationByIdList;
    return this;
  }

  public ReservationByBasketRefResponseDto addReservationByIdListItem(ReservationByIdDto reservationByIdListItem) {
    if (this.reservationByIdList == null) {
      this.reservationByIdList = new ArrayList<>();
    }
    this.reservationByIdList.add(reservationByIdListItem);
    return this;
  }

  /**
   * Get reservationByIdList
   * @return reservationByIdList
   */
  @Valid 
  @Schema(name = "reservationByIdList", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationByIdList")
  public List<@Valid ReservationByIdDto> getReservationByIdList() {
    return reservationByIdList;
  }

  public void setReservationByIdList(List<@Valid ReservationByIdDto> reservationByIdList) {
    this.reservationByIdList = reservationByIdList;
  }

  public ReservationByBasketRefResponseDto totalCost(BigDecimal totalCost) {
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

  public ReservationByBasketRefResponseDto totalCostWoDiscount(BigDecimal totalCostWoDiscount) {
    this.totalCostWoDiscount = totalCostWoDiscount;
    return this;
  }

  /**
   * Get totalCostWoDiscount
   * @return totalCostWoDiscount
   */
  @Valid 
  @Schema(name = "totalCostWoDiscount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalCostWoDiscount")
  public BigDecimal getTotalCostWoDiscount() {
    return totalCostWoDiscount;
  }

  public void setTotalCostWoDiscount(BigDecimal totalCostWoDiscount) {
    this.totalCostWoDiscount = totalCostWoDiscount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationByBasketRefResponseDto reservationByBasketRefResponseDto = (ReservationByBasketRefResponseDto) o;
    return Objects.equals(this.amountPaid, reservationByBasketRefResponseDto.amountPaid) &&
        Objects.equals(this.balanceOutstanding, reservationByBasketRefResponseDto.balanceOutstanding) &&
        Objects.equals(this.currencyCode, reservationByBasketRefResponseDto.currencyCode) &&
        Objects.equals(this.discount, reservationByBasketRefResponseDto.discount) &&
        Objects.equals(this.hotelId, reservationByBasketRefResponseDto.hotelId) &&
        Objects.equals(this.isCnp, reservationByBasketRefResponseDto.isCnp) &&
        Objects.equals(this.newTotal, reservationByBasketRefResponseDto.newTotal) &&
        Objects.equals(this.policyCode, reservationByBasketRefResponseDto.policyCode) &&
        Objects.equals(this.previousTotal, reservationByBasketRefResponseDto.previousTotal) &&
        Objects.equals(this.reservationByIdList, reservationByBasketRefResponseDto.reservationByIdList) &&
        Objects.equals(this.totalCost, reservationByBasketRefResponseDto.totalCost) &&
        Objects.equals(this.totalCostWoDiscount, reservationByBasketRefResponseDto.totalCostWoDiscount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amountPaid, balanceOutstanding, currencyCode, discount, hotelId, isCnp, newTotal, policyCode, previousTotal, reservationByIdList, totalCost, totalCostWoDiscount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationByBasketRefResponseDto {\n");
    sb.append("    amountPaid: ").append(toIndentedString(amountPaid)).append("\n");
    sb.append("    balanceOutstanding: ").append(toIndentedString(balanceOutstanding)).append("\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    discount: ").append(toIndentedString(discount)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    isCnp: ").append(toIndentedString(isCnp)).append("\n");
    sb.append("    newTotal: ").append(toIndentedString(newTotal)).append("\n");
    sb.append("    policyCode: ").append(toIndentedString(policyCode)).append("\n");
    sb.append("    previousTotal: ").append(toIndentedString(previousTotal)).append("\n");
    sb.append("    reservationByIdList: ").append(toIndentedString(reservationByIdList)).append("\n");
    sb.append("    totalCost: ").append(toIndentedString(totalCost)).append("\n");
    sb.append("    totalCostWoDiscount: ").append(toIndentedString(totalCostWoDiscount)).append("\n");
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

