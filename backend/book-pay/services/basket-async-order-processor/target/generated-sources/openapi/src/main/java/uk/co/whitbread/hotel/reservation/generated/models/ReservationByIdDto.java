package uk.co.whitbread.hotel.reservation.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.reservation.generated.models.AdditionalGuestInfoDto;
import uk.co.whitbread.hotel.reservation.generated.models.BillingResponseDto;
import uk.co.whitbread.hotel.reservation.generated.models.DepositPoliciesDto;
import uk.co.whitbread.hotel.reservation.generated.models.RateInfoDto;
import uk.co.whitbread.hotel.reservation.generated.models.ReservationBookerDto;
import uk.co.whitbread.hotel.reservation.generated.models.ReservationByIdGuestsDto;
import uk.co.whitbread.hotel.reservation.generated.models.ReservationEmailNotificationsDto;
import uk.co.whitbread.hotel.reservation.generated.models.ReservationOverrideReasonsDto;
import uk.co.whitbread.hotel.reservation.generated.models.ReservationPackagesDetailsResponseDto;
import uk.co.whitbread.hotel.reservation.generated.models.ReservationPaymentCardTypeDto;
import uk.co.whitbread.hotel.reservation.generated.models.RoomStayByIdDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ReservationByIdDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:09:52.163805+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationByIdDto {

  private @Nullable AdditionalGuestInfoDto additionalGuestInfo;

  private @Nullable BigDecimal balanceAmount;

  private @Nullable BillingResponseDto billing;

  @Valid
  private List<@Valid DepositPoliciesDto> depositPolicies = new ArrayList<>();

  private @Nullable String gdsReferenceNumber;

  private @Nullable String guaranteeCode;

  private @Nullable Boolean onHold;

  private @Nullable ReservationPaymentCardTypeDto paymentCard;

  private @Nullable RateInfoDto rateInfo;

  private @Nullable ReservationBookerDto reservationBooker;

  private @Nullable ReservationEmailNotificationsDto reservationEmailNotifications;

  @Valid
  private List<@Valid ReservationByIdGuestsDto> reservationGuestList = new ArrayList<>();

  private @Nullable String reservationId;

  private @Nullable Boolean reservationOverridden;

  private @Nullable ReservationOverrideReasonsDto reservationOverrideReasons;

  @Valid
  private List<@Valid ReservationPackagesDetailsResponseDto> reservationPackageList = new ArrayList<>();

  private @Nullable String reservationStatus;

  private @Nullable RoomStayByIdDto roomStay;

  public ReservationByIdDto additionalGuestInfo(AdditionalGuestInfoDto additionalGuestInfo) {
    this.additionalGuestInfo = additionalGuestInfo;
    return this;
  }

  /**
   * Get additionalGuestInfo
   * @return additionalGuestInfo
   */
  @Valid 
  @Schema(name = "additionalGuestInfo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("additionalGuestInfo")
  public AdditionalGuestInfoDto getAdditionalGuestInfo() {
    return additionalGuestInfo;
  }

  public void setAdditionalGuestInfo(AdditionalGuestInfoDto additionalGuestInfo) {
    this.additionalGuestInfo = additionalGuestInfo;
  }

  public ReservationByIdDto balanceAmount(BigDecimal balanceAmount) {
    this.balanceAmount = balanceAmount;
    return this;
  }

  /**
   * Get balanceAmount
   * @return balanceAmount
   */
  @Valid 
  @Schema(name = "balanceAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("balanceAmount")
  public BigDecimal getBalanceAmount() {
    return balanceAmount;
  }

  public void setBalanceAmount(BigDecimal balanceAmount) {
    this.balanceAmount = balanceAmount;
  }

  public ReservationByIdDto billing(BillingResponseDto billing) {
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

  public ReservationByIdDto depositPolicies(List<@Valid DepositPoliciesDto> depositPolicies) {
    this.depositPolicies = depositPolicies;
    return this;
  }

  public ReservationByIdDto addDepositPoliciesItem(DepositPoliciesDto depositPoliciesItem) {
    if (this.depositPolicies == null) {
      this.depositPolicies = new ArrayList<>();
    }
    this.depositPolicies.add(depositPoliciesItem);
    return this;
  }

  /**
   * Get depositPolicies
   * @return depositPolicies
   */
  @Valid 
  @Schema(name = "depositPolicies", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("depositPolicies")
  public List<@Valid DepositPoliciesDto> getDepositPolicies() {
    return depositPolicies;
  }

  public void setDepositPolicies(List<@Valid DepositPoliciesDto> depositPolicies) {
    this.depositPolicies = depositPolicies;
  }

  public ReservationByIdDto gdsReferenceNumber(String gdsReferenceNumber) {
    this.gdsReferenceNumber = gdsReferenceNumber;
    return this;
  }

  /**
   * Get gdsReferenceNumber
   * @return gdsReferenceNumber
   */
  
  @Schema(name = "gdsReferenceNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("gdsReferenceNumber")
  public String getGdsReferenceNumber() {
    return gdsReferenceNumber;
  }

  public void setGdsReferenceNumber(String gdsReferenceNumber) {
    this.gdsReferenceNumber = gdsReferenceNumber;
  }

  public ReservationByIdDto guaranteeCode(String guaranteeCode) {
    this.guaranteeCode = guaranteeCode;
    return this;
  }

  /**
   * Get guaranteeCode
   * @return guaranteeCode
   */
  
  @Schema(name = "guaranteeCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guaranteeCode")
  public String getGuaranteeCode() {
    return guaranteeCode;
  }

  public void setGuaranteeCode(String guaranteeCode) {
    this.guaranteeCode = guaranteeCode;
  }

  public ReservationByIdDto onHold(Boolean onHold) {
    this.onHold = onHold;
    return this;
  }

  /**
   * Get onHold
   * @return onHold
   */
  
  @Schema(name = "onHold", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("onHold")
  public Boolean getOnHold() {
    return onHold;
  }

  public void setOnHold(Boolean onHold) {
    this.onHold = onHold;
  }

  public ReservationByIdDto paymentCard(ReservationPaymentCardTypeDto paymentCard) {
    this.paymentCard = paymentCard;
    return this;
  }

  /**
   * Get paymentCard
   * @return paymentCard
   */
  @Valid 
  @Schema(name = "paymentCard", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentCard")
  public ReservationPaymentCardTypeDto getPaymentCard() {
    return paymentCard;
  }

  public void setPaymentCard(ReservationPaymentCardTypeDto paymentCard) {
    this.paymentCard = paymentCard;
  }

  public ReservationByIdDto rateInfo(RateInfoDto rateInfo) {
    this.rateInfo = rateInfo;
    return this;
  }

  /**
   * Get rateInfo
   * @return rateInfo
   */
  @Valid 
  @Schema(name = "rateInfo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateInfo")
  public RateInfoDto getRateInfo() {
    return rateInfo;
  }

  public void setRateInfo(RateInfoDto rateInfo) {
    this.rateInfo = rateInfo;
  }

  public ReservationByIdDto reservationBooker(ReservationBookerDto reservationBooker) {
    this.reservationBooker = reservationBooker;
    return this;
  }

  /**
   * Get reservationBooker
   * @return reservationBooker
   */
  @Valid 
  @Schema(name = "reservationBooker", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationBooker")
  public ReservationBookerDto getReservationBooker() {
    return reservationBooker;
  }

  public void setReservationBooker(ReservationBookerDto reservationBooker) {
    this.reservationBooker = reservationBooker;
  }

  public ReservationByIdDto reservationEmailNotifications(ReservationEmailNotificationsDto reservationEmailNotifications) {
    this.reservationEmailNotifications = reservationEmailNotifications;
    return this;
  }

  /**
   * Get reservationEmailNotifications
   * @return reservationEmailNotifications
   */
  @Valid 
  @Schema(name = "reservationEmailNotifications", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationEmailNotifications")
  public ReservationEmailNotificationsDto getReservationEmailNotifications() {
    return reservationEmailNotifications;
  }

  public void setReservationEmailNotifications(ReservationEmailNotificationsDto reservationEmailNotifications) {
    this.reservationEmailNotifications = reservationEmailNotifications;
  }

  public ReservationByIdDto reservationGuestList(List<@Valid ReservationByIdGuestsDto> reservationGuestList) {
    this.reservationGuestList = reservationGuestList;
    return this;
  }

  public ReservationByIdDto addReservationGuestListItem(ReservationByIdGuestsDto reservationGuestListItem) {
    if (this.reservationGuestList == null) {
      this.reservationGuestList = new ArrayList<>();
    }
    this.reservationGuestList.add(reservationGuestListItem);
    return this;
  }

  /**
   * Get reservationGuestList
   * @return reservationGuestList
   */
  @Valid 
  @Schema(name = "reservationGuestList", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationGuestList")
  public List<@Valid ReservationByIdGuestsDto> getReservationGuestList() {
    return reservationGuestList;
  }

  public void setReservationGuestList(List<@Valid ReservationByIdGuestsDto> reservationGuestList) {
    this.reservationGuestList = reservationGuestList;
  }

  public ReservationByIdDto reservationId(String reservationId) {
    this.reservationId = reservationId;
    return this;
  }

  /**
   * Get reservationId
   * @return reservationId
   */
  
  @Schema(name = "reservationId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationId")
  public String getReservationId() {
    return reservationId;
  }

  public void setReservationId(String reservationId) {
    this.reservationId = reservationId;
  }

  public ReservationByIdDto reservationOverridden(Boolean reservationOverridden) {
    this.reservationOverridden = reservationOverridden;
    return this;
  }

  /**
   * Get reservationOverridden
   * @return reservationOverridden
   */
  
  @Schema(name = "reservationOverridden", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationOverridden")
  public Boolean getReservationOverridden() {
    return reservationOverridden;
  }

  public void setReservationOverridden(Boolean reservationOverridden) {
    this.reservationOverridden = reservationOverridden;
  }

  public ReservationByIdDto reservationOverrideReasons(ReservationOverrideReasonsDto reservationOverrideReasons) {
    this.reservationOverrideReasons = reservationOverrideReasons;
    return this;
  }

  /**
   * Get reservationOverrideReasons
   * @return reservationOverrideReasons
   */
  @Valid 
  @Schema(name = "reservationOverrideReasons", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationOverrideReasons")
  public ReservationOverrideReasonsDto getReservationOverrideReasons() {
    return reservationOverrideReasons;
  }

  public void setReservationOverrideReasons(ReservationOverrideReasonsDto reservationOverrideReasons) {
    this.reservationOverrideReasons = reservationOverrideReasons;
  }

  public ReservationByIdDto reservationPackageList(List<@Valid ReservationPackagesDetailsResponseDto> reservationPackageList) {
    this.reservationPackageList = reservationPackageList;
    return this;
  }

  public ReservationByIdDto addReservationPackageListItem(ReservationPackagesDetailsResponseDto reservationPackageListItem) {
    if (this.reservationPackageList == null) {
      this.reservationPackageList = new ArrayList<>();
    }
    this.reservationPackageList.add(reservationPackageListItem);
    return this;
  }

  /**
   * Get reservationPackageList
   * @return reservationPackageList
   */
  @Valid 
  @Schema(name = "reservationPackageList", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationPackageList")
  public List<@Valid ReservationPackagesDetailsResponseDto> getReservationPackageList() {
    return reservationPackageList;
  }

  public void setReservationPackageList(List<@Valid ReservationPackagesDetailsResponseDto> reservationPackageList) {
    this.reservationPackageList = reservationPackageList;
  }

  public ReservationByIdDto reservationStatus(String reservationStatus) {
    this.reservationStatus = reservationStatus;
    return this;
  }

  /**
   * Get reservationStatus
   * @return reservationStatus
   */
  
  @Schema(name = "reservationStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationStatus")
  public String getReservationStatus() {
    return reservationStatus;
  }

  public void setReservationStatus(String reservationStatus) {
    this.reservationStatus = reservationStatus;
  }

  public ReservationByIdDto roomStay(RoomStayByIdDto roomStay) {
    this.roomStay = roomStay;
    return this;
  }

  /**
   * Get roomStay
   * @return roomStay
   */
  @Valid 
  @Schema(name = "roomStay", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomStay")
  public RoomStayByIdDto getRoomStay() {
    return roomStay;
  }

  public void setRoomStay(RoomStayByIdDto roomStay) {
    this.roomStay = roomStay;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReservationByIdDto reservationByIdDto = (ReservationByIdDto) o;
    return Objects.equals(this.additionalGuestInfo, reservationByIdDto.additionalGuestInfo) &&
        Objects.equals(this.balanceAmount, reservationByIdDto.balanceAmount) &&
        Objects.equals(this.billing, reservationByIdDto.billing) &&
        Objects.equals(this.depositPolicies, reservationByIdDto.depositPolicies) &&
        Objects.equals(this.gdsReferenceNumber, reservationByIdDto.gdsReferenceNumber) &&
        Objects.equals(this.guaranteeCode, reservationByIdDto.guaranteeCode) &&
        Objects.equals(this.onHold, reservationByIdDto.onHold) &&
        Objects.equals(this.paymentCard, reservationByIdDto.paymentCard) &&
        Objects.equals(this.rateInfo, reservationByIdDto.rateInfo) &&
        Objects.equals(this.reservationBooker, reservationByIdDto.reservationBooker) &&
        Objects.equals(this.reservationEmailNotifications, reservationByIdDto.reservationEmailNotifications) &&
        Objects.equals(this.reservationGuestList, reservationByIdDto.reservationGuestList) &&
        Objects.equals(this.reservationId, reservationByIdDto.reservationId) &&
        Objects.equals(this.reservationOverridden, reservationByIdDto.reservationOverridden) &&
        Objects.equals(this.reservationOverrideReasons, reservationByIdDto.reservationOverrideReasons) &&
        Objects.equals(this.reservationPackageList, reservationByIdDto.reservationPackageList) &&
        Objects.equals(this.reservationStatus, reservationByIdDto.reservationStatus) &&
        Objects.equals(this.roomStay, reservationByIdDto.roomStay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(additionalGuestInfo, balanceAmount, billing, depositPolicies, gdsReferenceNumber, guaranteeCode, onHold, paymentCard, rateInfo, reservationBooker, reservationEmailNotifications, reservationGuestList, reservationId, reservationOverridden, reservationOverrideReasons, reservationPackageList, reservationStatus, roomStay);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationByIdDto {\n");
    sb.append("    additionalGuestInfo: ").append(toIndentedString(additionalGuestInfo)).append("\n");
    sb.append("    balanceAmount: ").append(toIndentedString(balanceAmount)).append("\n");
    sb.append("    billing: ").append(toIndentedString(billing)).append("\n");
    sb.append("    depositPolicies: ").append(toIndentedString(depositPolicies)).append("\n");
    sb.append("    gdsReferenceNumber: ").append(toIndentedString(gdsReferenceNumber)).append("\n");
    sb.append("    guaranteeCode: ").append(toIndentedString(guaranteeCode)).append("\n");
    sb.append("    onHold: ").append(toIndentedString(onHold)).append("\n");
    sb.append("    paymentCard: ").append(toIndentedString(paymentCard)).append("\n");
    sb.append("    rateInfo: ").append(toIndentedString(rateInfo)).append("\n");
    sb.append("    reservationBooker: ").append(toIndentedString(reservationBooker)).append("\n");
    sb.append("    reservationEmailNotifications: ").append(toIndentedString(reservationEmailNotifications)).append("\n");
    sb.append("    reservationGuestList: ").append(toIndentedString(reservationGuestList)).append("\n");
    sb.append("    reservationId: ").append(toIndentedString(reservationId)).append("\n");
    sb.append("    reservationOverridden: ").append(toIndentedString(reservationOverridden)).append("\n");
    sb.append("    reservationOverrideReasons: ").append(toIndentedString(reservationOverrideReasons)).append("\n");
    sb.append("    reservationPackageList: ").append(toIndentedString(reservationPackageList)).append("\n");
    sb.append("    reservationStatus: ").append(toIndentedString(reservationStatus)).append("\n");
    sb.append("    roomStay: ").append(toIndentedString(roomStay)).append("\n");
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

