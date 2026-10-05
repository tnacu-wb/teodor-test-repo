package uk.co.whitbread.hotel.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.reservation.ReservationByIdDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ReservationByBasketRefResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationByBasketRefResponseDto {

  private @Nullable BigDecimal balanceOutstanding;

  private @Nullable String basketReference;

  private @Nullable String basketStatus;

  private @Nullable String bookingReference;

  private @Nullable String channel;

  private @Nullable String companyId;

  private @Nullable String currencyCode;

  private @Nullable String customReferenceNumber;

  private @Nullable BigDecimal discount;

  private @Nullable Boolean hasCityTax;

  private @Nullable String hotelId;

  private @Nullable String idContext;

  private @Nullable Boolean isCnp;

  private @Nullable BigDecimal newTotal;

  private @Nullable String paymentOption;

  private @Nullable String policyCode;

  private @Nullable BigDecimal previousTotal;

  /**
   * Gets or Sets promoKind
   */
  public enum PromoKindEnum {
    LANDING_PAGE("LANDING_PAGE"),
    
    SITE_WIDE("SITE_WIDE"),
    
    GENERIC("GENERIC"),
    
    UNIQUE("UNIQUE");

    private String value;

    PromoKindEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static PromoKindEnum fromValue(String value) {
      for (PromoKindEnum b : PromoKindEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable PromoKindEnum promoKind;

  private @Nullable String promotionCode;

  private @Nullable String purchaseOrderNumber;

  @Valid
  private List<@Valid ReservationByIdDto> reservationByIdList = new ArrayList<>();

  private @Nullable BigDecimal totalCost;

  private @Nullable BigDecimal totalCostWoDiscount;

  private @Nullable Boolean upsellsAddonsEnabled;

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

  public ReservationByBasketRefResponseDto basketStatus(String basketStatus) {
    this.basketStatus = basketStatus;
    return this;
  }

  /**
   * Get basketStatus
   * @return basketStatus
   */
  
  @Schema(name = "basketStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("basketStatus")
  public String getBasketStatus() {
    return basketStatus;
  }

  public void setBasketStatus(String basketStatus) {
    this.basketStatus = basketStatus;
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

  public ReservationByBasketRefResponseDto idContext(String idContext) {
    this.idContext = idContext;
    return this;
  }

  /**
   * Get idContext
   * @return idContext
   */
  
  @Schema(name = "idContext", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("idContext")
  public String getIdContext() {
    return idContext;
  }

  public void setIdContext(String idContext) {
    this.idContext = idContext;
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

  public ReservationByBasketRefResponseDto paymentOption(String paymentOption) {
    this.paymentOption = paymentOption;
    return this;
  }

  /**
   * Get paymentOption
   * @return paymentOption
   */
  
  @Schema(name = "paymentOption", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentOption")
  public String getPaymentOption() {
    return paymentOption;
  }

  public void setPaymentOption(String paymentOption) {
    this.paymentOption = paymentOption;
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

  public ReservationByBasketRefResponseDto promoKind(PromoKindEnum promoKind) {
    this.promoKind = promoKind;
    return this;
  }

  /**
   * Get promoKind
   * @return promoKind
   */
  
  @Schema(name = "promoKind", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoKind")
  public PromoKindEnum getPromoKind() {
    return promoKind;
  }

  public void setPromoKind(PromoKindEnum promoKind) {
    this.promoKind = promoKind;
  }

  public ReservationByBasketRefResponseDto promotionCode(String promotionCode) {
    this.promotionCode = promotionCode;
    return this;
  }

  /**
   * Get promotionCode
   * @return promotionCode
   */
  
  @Schema(name = "promotionCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promotionCode")
  public String getPromotionCode() {
    return promotionCode;
  }

  public void setPromotionCode(String promotionCode) {
    this.promotionCode = promotionCode;
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

  public ReservationByBasketRefResponseDto upsellsAddonsEnabled(Boolean upsellsAddonsEnabled) {
    this.upsellsAddonsEnabled = upsellsAddonsEnabled;
    return this;
  }

  /**
   * Get upsellsAddonsEnabled
   * @return upsellsAddonsEnabled
   */
  
  @Schema(name = "upsellsAddonsEnabled", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("upsellsAddonsEnabled")
  public Boolean getUpsellsAddonsEnabled() {
    return upsellsAddonsEnabled;
  }

  public void setUpsellsAddonsEnabled(Boolean upsellsAddonsEnabled) {
    this.upsellsAddonsEnabled = upsellsAddonsEnabled;
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
        Objects.equals(this.basketStatus, reservationByBasketRefResponseDto.basketStatus) &&
        Objects.equals(this.bookingReference, reservationByBasketRefResponseDto.bookingReference) &&
        Objects.equals(this.channel, reservationByBasketRefResponseDto.channel) &&
        Objects.equals(this.companyId, reservationByBasketRefResponseDto.companyId) &&
        Objects.equals(this.currencyCode, reservationByBasketRefResponseDto.currencyCode) &&
        Objects.equals(this.customReferenceNumber, reservationByBasketRefResponseDto.customReferenceNumber) &&
        Objects.equals(this.discount, reservationByBasketRefResponseDto.discount) &&
        Objects.equals(this.hasCityTax, reservationByBasketRefResponseDto.hasCityTax) &&
        Objects.equals(this.hotelId, reservationByBasketRefResponseDto.hotelId) &&
        Objects.equals(this.idContext, reservationByBasketRefResponseDto.idContext) &&
        Objects.equals(this.isCnp, reservationByBasketRefResponseDto.isCnp) &&
        Objects.equals(this.newTotal, reservationByBasketRefResponseDto.newTotal) &&
        Objects.equals(this.paymentOption, reservationByBasketRefResponseDto.paymentOption) &&
        Objects.equals(this.policyCode, reservationByBasketRefResponseDto.policyCode) &&
        Objects.equals(this.previousTotal, reservationByBasketRefResponseDto.previousTotal) &&
        Objects.equals(this.promoKind, reservationByBasketRefResponseDto.promoKind) &&
        Objects.equals(this.promotionCode, reservationByBasketRefResponseDto.promotionCode) &&
        Objects.equals(this.purchaseOrderNumber, reservationByBasketRefResponseDto.purchaseOrderNumber) &&
        Objects.equals(this.reservationByIdList, reservationByBasketRefResponseDto.reservationByIdList) &&
        Objects.equals(this.totalCost, reservationByBasketRefResponseDto.totalCost) &&
        Objects.equals(this.totalCostWoDiscount, reservationByBasketRefResponseDto.totalCostWoDiscount) &&
        Objects.equals(this.upsellsAddonsEnabled, reservationByBasketRefResponseDto.upsellsAddonsEnabled);
  }

  @Override
  public int hashCode() {
    return Objects.hash(balanceOutstanding, basketReference, basketStatus, bookingReference, channel, companyId, currencyCode, customReferenceNumber, discount, hasCityTax, hotelId, idContext, isCnp, newTotal, paymentOption, policyCode, previousTotal, promoKind, promotionCode, purchaseOrderNumber, reservationByIdList, totalCost, totalCostWoDiscount, upsellsAddonsEnabled);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationByBasketRefResponseDto {\n");
    sb.append("    balanceOutstanding: ").append(toIndentedString(balanceOutstanding)).append("\n");
    sb.append("    basketReference: ").append(toIndentedString(basketReference)).append("\n");
    sb.append("    basketStatus: ").append(toIndentedString(basketStatus)).append("\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    companyId: ").append(toIndentedString(companyId)).append("\n");
    sb.append("    currencyCode: ").append(toIndentedString(currencyCode)).append("\n");
    sb.append("    customReferenceNumber: ").append(toIndentedString(customReferenceNumber)).append("\n");
    sb.append("    discount: ").append(toIndentedString(discount)).append("\n");
    sb.append("    hasCityTax: ").append(toIndentedString(hasCityTax)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    idContext: ").append(toIndentedString(idContext)).append("\n");
    sb.append("    isCnp: ").append(toIndentedString(isCnp)).append("\n");
    sb.append("    newTotal: ").append(toIndentedString(newTotal)).append("\n");
    sb.append("    paymentOption: ").append(toIndentedString(paymentOption)).append("\n");
    sb.append("    policyCode: ").append(toIndentedString(policyCode)).append("\n");
    sb.append("    previousTotal: ").append(toIndentedString(previousTotal)).append("\n");
    sb.append("    promoKind: ").append(toIndentedString(promoKind)).append("\n");
    sb.append("    promotionCode: ").append(toIndentedString(promotionCode)).append("\n");
    sb.append("    purchaseOrderNumber: ").append(toIndentedString(purchaseOrderNumber)).append("\n");
    sb.append("    reservationByIdList: ").append(toIndentedString(reservationByIdList)).append("\n");
    sb.append("    totalCost: ").append(toIndentedString(totalCost)).append("\n");
    sb.append("    totalCostWoDiscount: ").append(toIndentedString(totalCostWoDiscount)).append("\n");
    sb.append("    upsellsAddonsEnabled: ").append(toIndentedString(upsellsAddonsEnabled)).append("\n");
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

