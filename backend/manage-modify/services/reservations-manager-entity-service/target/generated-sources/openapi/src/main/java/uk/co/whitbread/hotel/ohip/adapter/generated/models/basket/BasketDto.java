package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketError;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BookingAllowanceDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CcuiExtraItems;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BasketDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:44.119190+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BasketDto {

  private @Nullable BasketError basketError;

  @Valid
  private List<@Valid BookingAllowanceDto> bookingAllowances = new ArrayList<>();

  private String bookingReference;

  private @Nullable CcuiExtraItems ccuiExtraItems;

  private @Nullable String channel;

  private String createdAt;

  private String hotelId;

  private @Nullable Boolean isCheckInOnlinePay;

  private @Nullable Boolean isErroredBooking;

  @Valid
  private Set<String> itemTypes = new LinkedHashSet<>();

  @Valid
  private List<@Valid BasketItemDto> items = new ArrayList<>();

  private @Nullable String lastModifiedAt;

  @Valid
  private Map<String, String> linkAmendReservations = new HashMap<>();

  private @Nullable String lockingTime;

  private @Nullable String originalBasketId;

  private @Nullable String paymentID;

  private @Nullable String paymentOption;

  /**
   * Gets or Sets paymentStatus
   */
  public enum PaymentStatusEnum {
    COMPLETED("COMPLETED"),
    
    REFUNDING("REFUNDING"),
    
    REFUNDED("REFUNDED"),
    
    FAILED_REFUND("FAILED_REFUND");

    private String value;

    PaymentStatusEnum(String value) {
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
    public static PaymentStatusEnum fromValue(String value) {
      for (PaymentStatusEnum b : PaymentStatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable PaymentStatusEnum paymentStatus;

  private String reference;

  private @Nullable Boolean sendMail;

  /**
   * Gets or Sets status
   */
  public enum StatusEnum {
    OPEN("OPEN"),
    
    PROCESSING("PROCESSING"),
    
    AMENDING("AMENDING"),
    
    COMPLETED("COMPLETED"),
    
    AMENDED("AMENDED"),
    
    PRE_CHECKED_IN("PRE_CHECKED_IN"),
    
    CANCELLED("CANCELLED"),
    
    PAY_PENDING("PAY_PENDING"),
    
    AMEND_FAILED("AMEND_FAILED"),
    
    FAILED("FAILED"),
    
    CIOL_FAILED("CIOL_FAILED"),
    
    PRE_CHECKED_OUT("PRE_CHECKED_OUT"),
    
    CIOL_RC_FAILED("CIOL_RC_FAILED");

    private String value;

    StatusEnum(String value) {
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
    public static StatusEnum fromValue(String value) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private StatusEnum status;

  private @Nullable String userId;

  public BasketDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BasketDto(String bookingReference, String createdAt, String hotelId, Set<String> itemTypes, List<@Valid BasketItemDto> items, String reference, StatusEnum status) {
    this.bookingReference = bookingReference;
    this.createdAt = createdAt;
    this.hotelId = hotelId;
    this.itemTypes = itemTypes;
    this.items = items;
    this.reference = reference;
    this.status = status;
  }

  public BasketDto basketError(BasketError basketError) {
    this.basketError = basketError;
    return this;
  }

  /**
   * Get basketError
   * @return basketError
   */
  @Valid 
  @Schema(name = "basketError", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("basketError")
  public BasketError getBasketError() {
    return basketError;
  }

  public void setBasketError(BasketError basketError) {
    this.basketError = basketError;
  }

  public BasketDto bookingAllowances(List<@Valid BookingAllowanceDto> bookingAllowances) {
    this.bookingAllowances = bookingAllowances;
    return this;
  }

  public BasketDto addBookingAllowancesItem(BookingAllowanceDto bookingAllowancesItem) {
    if (this.bookingAllowances == null) {
      this.bookingAllowances = new ArrayList<>();
    }
    this.bookingAllowances.add(bookingAllowancesItem);
    return this;
  }

  /**
   * Get bookingAllowances
   * @return bookingAllowances
   */
  @Valid 
  @Schema(name = "bookingAllowances", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingAllowances")
  public List<@Valid BookingAllowanceDto> getBookingAllowances() {
    return bookingAllowances;
  }

  public void setBookingAllowances(List<@Valid BookingAllowanceDto> bookingAllowances) {
    this.bookingAllowances = bookingAllowances;
  }

  public BasketDto bookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
    return this;
  }

  /**
   * Get bookingReference
   * @return bookingReference
   */
  @NotNull 
  @Schema(name = "bookingReference", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bookingReference")
  public String getBookingReference() {
    return bookingReference;
  }

  public void setBookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
  }

  public BasketDto ccuiExtraItems(CcuiExtraItems ccuiExtraItems) {
    this.ccuiExtraItems = ccuiExtraItems;
    return this;
  }

  /**
   * Get ccuiExtraItems
   * @return ccuiExtraItems
   */
  @Valid 
  @Schema(name = "ccuiExtraItems", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ccuiExtraItems")
  public CcuiExtraItems getCcuiExtraItems() {
    return ccuiExtraItems;
  }

  public void setCcuiExtraItems(CcuiExtraItems ccuiExtraItems) {
    this.ccuiExtraItems = ccuiExtraItems;
  }

  public BasketDto channel(String channel) {
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

  public BasketDto createdAt(String createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * Get createdAt
   * @return createdAt
   */
  @NotNull 
  @Schema(name = "createdAt", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("createdAt")
  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public BasketDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  @NotNull 
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public BasketDto isCheckInOnlinePay(Boolean isCheckInOnlinePay) {
    this.isCheckInOnlinePay = isCheckInOnlinePay;
    return this;
  }

  /**
   * Get isCheckInOnlinePay
   * @return isCheckInOnlinePay
   */
  
  @Schema(name = "isCheckInOnlinePay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isCheckInOnlinePay")
  public Boolean getIsCheckInOnlinePay() {
    return isCheckInOnlinePay;
  }

  public void setIsCheckInOnlinePay(Boolean isCheckInOnlinePay) {
    this.isCheckInOnlinePay = isCheckInOnlinePay;
  }

  public BasketDto isErroredBooking(Boolean isErroredBooking) {
    this.isErroredBooking = isErroredBooking;
    return this;
  }

  /**
   * Get isErroredBooking
   * @return isErroredBooking
   */
  
  @Schema(name = "isErroredBooking", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isErroredBooking")
  public Boolean getIsErroredBooking() {
    return isErroredBooking;
  }

  public void setIsErroredBooking(Boolean isErroredBooking) {
    this.isErroredBooking = isErroredBooking;
  }

  public BasketDto itemTypes(Set<String> itemTypes) {
    this.itemTypes = itemTypes;
    return this;
  }

  public BasketDto addItemTypesItem(String itemTypesItem) {
    if (this.itemTypes == null) {
      this.itemTypes = new LinkedHashSet<>();
    }
    this.itemTypes.add(itemTypesItem);
    return this;
  }

  /**
   * Get itemTypes
   * @return itemTypes
   */
  @NotNull 
  @Schema(name = "itemTypes", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("itemTypes")
  public Set<String> getItemTypes() {
    return itemTypes;
  }

  @JsonDeserialize(as = LinkedHashSet.class)
  public void setItemTypes(Set<String> itemTypes) {
    this.itemTypes = itemTypes;
  }

  public BasketDto items(List<@Valid BasketItemDto> items) {
    this.items = items;
    return this;
  }

  public BasketDto addItemsItem(BasketItemDto itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * Get items
   * @return items
   */
  @NotNull @Valid 
  @Schema(name = "items", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("items")
  public List<@Valid BasketItemDto> getItems() {
    return items;
  }

  public void setItems(List<@Valid BasketItemDto> items) {
    this.items = items;
  }

  public BasketDto lastModifiedAt(String lastModifiedAt) {
    this.lastModifiedAt = lastModifiedAt;
    return this;
  }

  /**
   * Get lastModifiedAt
   * @return lastModifiedAt
   */
  
  @Schema(name = "lastModifiedAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastModifiedAt")
  public String getLastModifiedAt() {
    return lastModifiedAt;
  }

  public void setLastModifiedAt(String lastModifiedAt) {
    this.lastModifiedAt = lastModifiedAt;
  }

  public BasketDto linkAmendReservations(Map<String, String> linkAmendReservations) {
    this.linkAmendReservations = linkAmendReservations;
    return this;
  }

  public BasketDto putLinkAmendReservationsItem(String key, String linkAmendReservationsItem) {
    if (this.linkAmendReservations == null) {
      this.linkAmendReservations = new HashMap<>();
    }
    this.linkAmendReservations.put(key, linkAmendReservationsItem);
    return this;
  }

  /**
   * Get linkAmendReservations
   * @return linkAmendReservations
   */
  
  @Schema(name = "linkAmendReservations", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("linkAmendReservations")
  public Map<String, String> getLinkAmendReservations() {
    return linkAmendReservations;
  }

  public void setLinkAmendReservations(Map<String, String> linkAmendReservations) {
    this.linkAmendReservations = linkAmendReservations;
  }

  public BasketDto lockingTime(String lockingTime) {
    this.lockingTime = lockingTime;
    return this;
  }

  /**
   * Get lockingTime
   * @return lockingTime
   */
  
  @Schema(name = "lockingTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lockingTime")
  public String getLockingTime() {
    return lockingTime;
  }

  public void setLockingTime(String lockingTime) {
    this.lockingTime = lockingTime;
  }

  public BasketDto originalBasketId(String originalBasketId) {
    this.originalBasketId = originalBasketId;
    return this;
  }

  /**
   * Get originalBasketId
   * @return originalBasketId
   */
  
  @Schema(name = "originalBasketId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("originalBasketId")
  public String getOriginalBasketId() {
    return originalBasketId;
  }

  public void setOriginalBasketId(String originalBasketId) {
    this.originalBasketId = originalBasketId;
  }

  public BasketDto paymentID(String paymentID) {
    this.paymentID = paymentID;
    return this;
  }

  /**
   * Get paymentID
   * @return paymentID
   */
  
  @Schema(name = "paymentID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentID")
  public String getPaymentID() {
    return paymentID;
  }

  public void setPaymentID(String paymentID) {
    this.paymentID = paymentID;
  }

  public BasketDto paymentOption(String paymentOption) {
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

  public BasketDto paymentStatus(PaymentStatusEnum paymentStatus) {
    this.paymentStatus = paymentStatus;
    return this;
  }

  /**
   * Get paymentStatus
   * @return paymentStatus
   */
  
  @Schema(name = "paymentStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentStatus")
  public PaymentStatusEnum getPaymentStatus() {
    return paymentStatus;
  }

  public void setPaymentStatus(PaymentStatusEnum paymentStatus) {
    this.paymentStatus = paymentStatus;
  }

  public BasketDto reference(String reference) {
    this.reference = reference;
    return this;
  }

  /**
   * Get reference
   * @return reference
   */
  @NotNull 
  @Schema(name = "reference", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reference")
  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public BasketDto sendMail(Boolean sendMail) {
    this.sendMail = sendMail;
    return this;
  }

  /**
   * Get sendMail
   * @return sendMail
   */
  
  @Schema(name = "sendMail", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sendMail")
  public Boolean getSendMail() {
    return sendMail;
  }

  public void setSendMail(Boolean sendMail) {
    this.sendMail = sendMail;
  }

  public BasketDto status(StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  @NotNull 
  @Schema(name = "status", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("status")
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public BasketDto userId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
   */
  
  @Schema(name = "userId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userId")
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BasketDto basketDto = (BasketDto) o;
    return Objects.equals(this.basketError, basketDto.basketError) &&
        Objects.equals(this.bookingAllowances, basketDto.bookingAllowances) &&
        Objects.equals(this.bookingReference, basketDto.bookingReference) &&
        Objects.equals(this.ccuiExtraItems, basketDto.ccuiExtraItems) &&
        Objects.equals(this.channel, basketDto.channel) &&
        Objects.equals(this.createdAt, basketDto.createdAt) &&
        Objects.equals(this.hotelId, basketDto.hotelId) &&
        Objects.equals(this.isCheckInOnlinePay, basketDto.isCheckInOnlinePay) &&
        Objects.equals(this.isErroredBooking, basketDto.isErroredBooking) &&
        Objects.equals(this.itemTypes, basketDto.itemTypes) &&
        Objects.equals(this.items, basketDto.items) &&
        Objects.equals(this.lastModifiedAt, basketDto.lastModifiedAt) &&
        Objects.equals(this.linkAmendReservations, basketDto.linkAmendReservations) &&
        Objects.equals(this.lockingTime, basketDto.lockingTime) &&
        Objects.equals(this.originalBasketId, basketDto.originalBasketId) &&
        Objects.equals(this.paymentID, basketDto.paymentID) &&
        Objects.equals(this.paymentOption, basketDto.paymentOption) &&
        Objects.equals(this.paymentStatus, basketDto.paymentStatus) &&
        Objects.equals(this.reference, basketDto.reference) &&
        Objects.equals(this.sendMail, basketDto.sendMail) &&
        Objects.equals(this.status, basketDto.status) &&
        Objects.equals(this.userId, basketDto.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(basketError, bookingAllowances, bookingReference, ccuiExtraItems, channel, createdAt, hotelId, isCheckInOnlinePay, isErroredBooking, itemTypes, items, lastModifiedAt, linkAmendReservations, lockingTime, originalBasketId, paymentID, paymentOption, paymentStatus, reference, sendMail, status, userId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BasketDto {\n");
    sb.append("    basketError: ").append(toIndentedString(basketError)).append("\n");
    sb.append("    bookingAllowances: ").append(toIndentedString(bookingAllowances)).append("\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
    sb.append("    ccuiExtraItems: ").append(toIndentedString(ccuiExtraItems)).append("\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    isCheckInOnlinePay: ").append(toIndentedString(isCheckInOnlinePay)).append("\n");
    sb.append("    isErroredBooking: ").append(toIndentedString(isErroredBooking)).append("\n");
    sb.append("    itemTypes: ").append(toIndentedString(itemTypes)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    lastModifiedAt: ").append(toIndentedString(lastModifiedAt)).append("\n");
    sb.append("    linkAmendReservations: ").append(toIndentedString(linkAmendReservations)).append("\n");
    sb.append("    lockingTime: ").append(toIndentedString(lockingTime)).append("\n");
    sb.append("    originalBasketId: ").append(toIndentedString(originalBasketId)).append("\n");
    sb.append("    paymentID: ").append(toIndentedString(paymentID)).append("\n");
    sb.append("    paymentOption: ").append(toIndentedString(paymentOption)).append("\n");
    sb.append("    paymentStatus: ").append(toIndentedString(paymentStatus)).append("\n");
    sb.append("    reference: ").append(toIndentedString(reference)).append("\n");
    sb.append("    sendMail: ").append(toIndentedString(sendMail)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
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

