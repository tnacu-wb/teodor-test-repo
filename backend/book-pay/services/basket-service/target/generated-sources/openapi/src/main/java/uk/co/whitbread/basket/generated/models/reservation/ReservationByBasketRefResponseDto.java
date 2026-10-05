package uk.co.whitbread.basket.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.reservation.ReservationByIdDto;
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
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationByBasketRefResponseDto {

  private @Nullable BigDecimal balanceOutstanding;

  private @Nullable String basketReference;

  private @Nullable String bookingReference;

  private @Nullable String channel;

  private @Nullable String companyId;

  private @Nullable String currencyCode;

  private @Nullable String customReferenceNumber;

  private @Nullable BigDecimal discount;

  private @Nullable Boolean hasCityTax;

  private @Nullable String hotelId;

  private @Nullable Boolean isCnp;

  private @Nullable BigDecimal newTotal;

  private @Nullable String policyCode;

  private @Nullable BigDecimal previousTotal;

  private @Nullable String purchaseOrderNumber;

  @Valid
  private List<@Valid ReservationByIdDto> reservationByIdList = new ArrayList<>();

  private @Nullable BigDecimal totalCost;

  private @Nullable BigDecimal totalCostWoDiscount;

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

  public ReservationByBasketRefResponseDto basketReference(String basketReference) {
    this.basketReference = basketReference;
    return this;
  }

  /**
   * Get basketReference
   * @return basketReference
   */
  
  @Schema(name = "basketReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("basketReference")
  public String getBasketReference() {
    return basketReference;
  }

  public void setBasketReference(String basketReference) {
    this.basketReference = basketReference;
  }

  public ReservationByBasketRefResponseDto bookingReference(String bookingReference) {
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

  public ReservationByBasketRefResponseDto channel(String channel) {
    this.channel = channel;
    return this;
  }

  /**
   * Get channel
   * @return channel
   */
  
  @Schema(name = "channel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("channel")
  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public ReservationByBasketRefResponseDto companyId(String companyId) {
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

  public ReservationByBasketRefResponseDto customReferenceNumber(String customReferenceNumber) {
    this.customReferenceNumber = customReferenceNumber;
    return this;
  }

  /**
   * Get customReferenceNumber
   * @return customReferenceNumber
   */
  
  @Schema(name = "customReferenceNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customReferenceNumber")
  public String getCustomReferenceNumber() {
    return customReferenceNumber;
  }

  public void setCustomReferenceNumber(String customReferenceNumber) {
    this.customReferenceNumber = customReferenceNumber;
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

  public ReservationByBasketRefResponseDto hasCityTax(Boolean hasCityTax) {
    this.hasCityTax = hasCityTax;
    return this;
  }

  /**
   * Get hasCityTax
   * @return hasCityTax
   */
  
  @Schema(name = "hasCityTax", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hasCityTax")
  public Boolean getHasCityTax() {
    return hasCityTax;
  }

  public void setHasCityTax(Boolean hasCityTax) {
    this.hasCityTax = hasCityTax;
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

  public ReservationByBasketRefResponseDto purchaseOrderNumber(String purchaseOrderNumber) {
    this.purchaseOrderNumber = purchaseOrderNumber;
    return this;
  }

  /**
   * Get purchaseOrderNumber
   * @return purchaseOrderNumber
   */
  
  @Schema(name = "purchaseOrderNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("purchaseOrderNumber")
  public String getPurchaseOrderNumber() {
    return purchaseOrderNumber;
  }

  public void setPurchaseOrderNumber(String purchaseOrderNumber) {
    this.purchaseOrderNumber = purchaseOrderNumber;
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
    return Objects.equals(this.balanceOutstanding, reservationByBasketRefResponseDto.balanceOutstanding) &&
        Objects.equals(this.basketReference, reservationByBasketRefResponseDto.basketReference) &&
        Objects.equals(this.bookingReference, reservationByBasketRefResponseDto.bookingReference) &&
        Objects.equals(this.channel, reservationByBasketRefResponseDto.channel) &&
        Objects.equals(this.companyId, reservationByBasketRefResponseDto.companyId) &&
        Objects.equals(this.currencyCode, reservationByBasketRefResponseDto.currencyCode) &&
        Objects.equals(this.customReferenceNumber, reservationByBasketRefResponseDto.customReferenceNumber) &&
        Objects.equals(this.discount, reservationByBasketRefResponseDto.discount) &&
        Objects.equals(this.hasCityTax, reservationByBasketRefResponseDto.hasCityTax) &&
        Objects.equals(this.hotelId, reservationByBasketRefResponseDto.hotelId) &&
        Objects.equals(this.isCnp, reservationByBasketRefResponseDto.isCnp) &&
        Objects.equals(this.newTotal, reservationByBasketRefResponseDto.newTotal) &&
        Objects.equals(this.policyCode, reservationByBasketRefResponseDto.policyCode) &&
        Objects.equals(this.previousTotal, reservationByBasketRefResponseDto.previousTotal) &&
        Objects.equals(this.purchaseOrderNumber, reservationByBasketRefResponseDto.purchaseOrderNumber) &&
        Objects.equals(this.reservationByIdList, reservationByBasketRefResponseDto.reservationByIdList) &&
        Objects.equals(this.totalCost, reservationByBasketRefResponseDto.totalCost) &&
        Objects.equals(this.totalCostWoDiscount, reservationByBasketRefResponseDto.totalCostWoDiscount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(balanceOutstanding, basketReference, bookingReference, channel, companyId, currencyCode, customReferenceNumber, discount, hasCityTax, hotelId, isCnp, newTotal, policyCode, previousTotal, purchaseOrderNumber, reservationByIdList, totalCost, totalCostWoDiscount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationByBasketRefResponseDto {\n");
    sb.append("    balanceOutstanding: ").append(toIndentedString(balanceOutstanding)).append("\n");
    sb.append("    basketReference: ").append(toIndentedString(basketReference)).append("\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    companyId: ").append(toIndentedString(companyId)).append("\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    customReferenceNumber: ").append(toIndentedString(customReferenceNumber)).append("\n");
    sb.append("    discount: ").append(toIndentedString(discount)).append("\n");
    sb.append("    hasCityTax: ").append(toIndentedString(hasCityTax)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    isCnp: ").append(toIndentedString(isCnp)).append("\n");
    sb.append("    newTotal: ").append(toIndentedString(newTotal)).append("\n");
    sb.append("    policyCode: ").append(toIndentedString(policyCode)).append("\n");
    sb.append("    previousTotal: ").append(toIndentedString(previousTotal)).append("\n");
    sb.append("    purchaseOrderNumber: ").append(toIndentedString(purchaseOrderNumber)).append("\n");
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

