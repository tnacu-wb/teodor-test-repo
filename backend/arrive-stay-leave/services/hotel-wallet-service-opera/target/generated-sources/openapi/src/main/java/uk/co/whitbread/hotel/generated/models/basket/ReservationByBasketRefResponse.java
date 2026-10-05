package uk.co.whitbread.hotel.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.basket.Reservation;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ReservationByBasketRefResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationByBasketRefResponse {

  private @Nullable BigDecimal balanceOutstanding;

  private @Nullable String bookingReference;

  private @Nullable String companyId;

  private @Nullable String currencyCode;

  private @Nullable BigDecimal discount;

  private @Nullable String hotelId;

  private @Nullable BigDecimal newTotal;

  private @Nullable String policyCode;

  private @Nullable BigDecimal previousTotal;

  @Valid
  private List<@Valid Reservation> reservationByIdList = new ArrayList<>();

  private @Nullable BigDecimal totalCost;

  public ReservationByBasketRefResponse balanceOutstanding(BigDecimal balanceOutstanding) {
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

  public ReservationByBasketRefResponse bookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
    return this;
  }

  /**
   * Get bookingReference
   * @return bookingReference
   */
  
  @Schema(name = "bookingReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingReference")
  public String getBookingReference() {
    return bookingReference;
  }

  public void setBookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
  }

  public ReservationByBasketRefResponse companyId(String companyId) {
    this.companyId = companyId;
    return this;
  }

  /**
   * Get companyId
   * @return companyId
   */
  
  @Schema(name = "companyId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyId")
  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public ReservationByBasketRefResponse currencyCode(String currencyCode) {
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

  public ReservationByBasketRefResponse discount(BigDecimal discount) {
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

  public ReservationByBasketRefResponse hotelId(String hotelId) {
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

  public ReservationByBasketRefResponse newTotal(BigDecimal newTotal) {
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

  public ReservationByBasketRefResponse policyCode(String policyCode) {
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

  public ReservationByBasketRefResponse previousTotal(BigDecimal previousTotal) {
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

  public ReservationByBasketRefResponse reservationByIdList(List<@Valid Reservation> reservationByIdList) {
    this.reservationByIdList = reservationByIdList;
    return this;
  }

  public ReservationByBasketRefResponse addReservationByIdListItem(Reservation reservationByIdListItem) {
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
  public List<@Valid Reservation> getReservationByIdList() {
    return reservationByIdList;
  }

  public void setReservationByIdList(List<@Valid Reservation> reservationByIdList) {
    this.reservationByIdList = reservationByIdList;
  }

  public ReservationByBasketRefResponse totalCost(BigDecimal totalCost) {
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
    ReservationByBasketRefResponse reservationByBasketRefResponse = (ReservationByBasketRefResponse) o;
    return Objects.equals(this.balanceOutstanding, reservationByBasketRefResponse.balanceOutstanding) &&
        Objects.equals(this.bookingReference, reservationByBasketRefResponse.bookingReference) &&
        Objects.equals(this.companyId, reservationByBasketRefResponse.companyId) &&
        Objects.equals(this.currencyCode, reservationByBasketRefResponse.currencyCode) &&
        Objects.equals(this.discount, reservationByBasketRefResponse.discount) &&
        Objects.equals(this.hotelId, reservationByBasketRefResponse.hotelId) &&
        Objects.equals(this.newTotal, reservationByBasketRefResponse.newTotal) &&
        Objects.equals(this.policyCode, reservationByBasketRefResponse.policyCode) &&
        Objects.equals(this.previousTotal, reservationByBasketRefResponse.previousTotal) &&
        Objects.equals(this.reservationByIdList, reservationByBasketRefResponse.reservationByIdList) &&
        Objects.equals(this.totalCost, reservationByBasketRefResponse.totalCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(balanceOutstanding, bookingReference, companyId, currencyCode, discount, hotelId, newTotal, policyCode, previousTotal, reservationByIdList, totalCost);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationByBasketRefResponse {\n");
    sb.append("    balanceOutstanding: ").append(toIndentedString(balanceOutstanding)).append("\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
    sb.append("    companyId: ").append(toIndentedString(companyId)).append("\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    discount: ").append(toIndentedString(discount)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    newTotal: ").append(toIndentedString(newTotal)).append("\n");
    sb.append("    policyCode: ").append(toIndentedString(policyCode)).append("\n");
    sb.append("    previousTotal: ").append(toIndentedString(previousTotal)).append("\n");
    sb.append("    reservationByIdList: ").append(toIndentedString(reservationByIdList)).append("\n");
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

