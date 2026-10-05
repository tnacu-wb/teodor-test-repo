package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AdditionalGuestInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BillingResponse;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositPoliciesResponseSingleCall;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RateInfoSingleCall;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResCashieringTypeSingleCall;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationBookerSingleCall;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationByIdGuestsResponse;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationCompany;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationEmailNotificationsSingleCall;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationOverrideReasons;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackagesDetailsResponse;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPaymentCardTypeSingleCall;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayByIdResponse;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ReservationByIdResponseSingleCall
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationByIdResponseSingleCall {

  private @Nullable AdditionalGuestInfo additionalGuestInfo;

  private @Nullable BigDecimal balanceAmount;

  private @Nullable BillingResponse billing;

  private @Nullable ResCashieringTypeSingleCall cashiering;

  @Valid
  private List<@Valid DepositPoliciesResponseSingleCall> depositPolicies = new ArrayList<>();

  private @Nullable String gdsReferenceNumber;

  private @Nullable String guaranteeCode;

  private @Nullable Boolean onHold;

  private @Nullable ReservationPaymentCardTypeSingleCall paymentCard;

  private @Nullable RateInfoSingleCall rateInfo;

  private @Nullable ReservationBookerSingleCall reservationBooker;

  private @Nullable ReservationCompany reservationCompany;

  private @Nullable ReservationEmailNotificationsSingleCall reservationEmailNotifications;

  @Valid
  private List<@Valid ReservationByIdGuestsResponse> reservationGuestList = new ArrayList<>();

  private @Nullable String reservationId;

  private @Nullable Boolean reservationOverridden;

  private @Nullable ReservationOverrideReasons reservationOverrideReasons;

  @Valid
  private List<@Valid ReservationPackagesDetailsResponse> reservationPackageList = new ArrayList<>();

  private @Nullable String reservationStatus;

  private @Nullable RoomStayByIdResponse roomStay;

  public ReservationByIdResponseSingleCall additionalGuestInfo(AdditionalGuestInfo additionalGuestInfo) {
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
  public AdditionalGuestInfo getAdditionalGuestInfo() {
    return additionalGuestInfo;
  }

  public void setAdditionalGuestInfo(AdditionalGuestInfo additionalGuestInfo) {
    this.additionalGuestInfo = additionalGuestInfo;
  }

  public ReservationByIdResponseSingleCall balanceAmount(BigDecimal balanceAmount) {
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

  public ReservationByIdResponseSingleCall billing(BillingResponse billing) {
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
  public BillingResponse getBilling() {
    return billing;
  }

  public void setBilling(BillingResponse billing) {
    this.billing = billing;
  }

  public ReservationByIdResponseSingleCall cashiering(ResCashieringTypeSingleCall cashiering) {
    this.cashiering = cashiering;
    return this;
  }

  /**
   * Get cashiering
   * @return cashiering
   */
  @Valid 
  @Schema(name = "cashiering", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cashiering")
  public ResCashieringTypeSingleCall getCashiering() {
    return cashiering;
  }

  public void setCashiering(ResCashieringTypeSingleCall cashiering) {
    this.cashiering = cashiering;
  }

  public ReservationByIdResponseSingleCall depositPolicies(List<@Valid DepositPoliciesResponseSingleCall> depositPolicies) {
    this.depositPolicies = depositPolicies;
    return this;
  }

  public ReservationByIdResponseSingleCall addDepositPoliciesItem(DepositPoliciesResponseSingleCall depositPoliciesItem) {
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
  public List<@Valid DepositPoliciesResponseSingleCall> getDepositPolicies() {
    return depositPolicies;
  }

  public void setDepositPolicies(List<@Valid DepositPoliciesResponseSingleCall> depositPolicies) {
    this.depositPolicies = depositPolicies;
  }

  public ReservationByIdResponseSingleCall gdsReferenceNumber(String gdsReferenceNumber) {
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

  public ReservationByIdResponseSingleCall guaranteeCode(String guaranteeCode) {
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

  public ReservationByIdResponseSingleCall onHold(Boolean onHold) {
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

  public ReservationByIdResponseSingleCall paymentCard(ReservationPaymentCardTypeSingleCall paymentCard) {
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
  public ReservationPaymentCardTypeSingleCall getPaymentCard() {
    return paymentCard;
  }

  public void setPaymentCard(ReservationPaymentCardTypeSingleCall paymentCard) {
    this.paymentCard = paymentCard;
  }

  public ReservationByIdResponseSingleCall rateInfo(RateInfoSingleCall rateInfo) {
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
  public RateInfoSingleCall getRateInfo() {
    return rateInfo;
  }

  public void setRateInfo(RateInfoSingleCall rateInfo) {
    this.rateInfo = rateInfo;
  }

  public ReservationByIdResponseSingleCall reservationBooker(ReservationBookerSingleCall reservationBooker) {
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
  public ReservationBookerSingleCall getReservationBooker() {
    return reservationBooker;
  }

  public void setReservationBooker(ReservationBookerSingleCall reservationBooker) {
    this.reservationBooker = reservationBooker;
  }

  public ReservationByIdResponseSingleCall reservationCompany(ReservationCompany reservationCompany) {
    this.reservationCompany = reservationCompany;
    return this;
  }

  /**
   * Get reservationCompany
   * @return reservationCompany
   */
  @Valid 
  @Schema(name = "reservationCompany", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationCompany")
  public ReservationCompany getReservationCompany() {
    return reservationCompany;
  }

  public void setReservationCompany(ReservationCompany reservationCompany) {
    this.reservationCompany = reservationCompany;
  }

  public ReservationByIdResponseSingleCall reservationEmailNotifications(ReservationEmailNotificationsSingleCall reservationEmailNotifications) {
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
  public ReservationEmailNotificationsSingleCall getReservationEmailNotifications() {
    return reservationEmailNotifications;
  }

  public void setReservationEmailNotifications(ReservationEmailNotificationsSingleCall reservationEmailNotifications) {
    this.reservationEmailNotifications = reservationEmailNotifications;
  }

  public ReservationByIdResponseSingleCall reservationGuestList(List<@Valid ReservationByIdGuestsResponse> reservationGuestList) {
    this.reservationGuestList = reservationGuestList;
    return this;
  }

  public ReservationByIdResponseSingleCall addReservationGuestListItem(ReservationByIdGuestsResponse reservationGuestListItem) {
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
  public List<@Valid ReservationByIdGuestsResponse> getReservationGuestList() {
    return reservationGuestList;
  }

  public void setReservationGuestList(List<@Valid ReservationByIdGuestsResponse> reservationGuestList) {
    this.reservationGuestList = reservationGuestList;
  }

  public ReservationByIdResponseSingleCall reservationId(String reservationId) {
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

  public ReservationByIdResponseSingleCall reservationOverridden(Boolean reservationOverridden) {
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

  public ReservationByIdResponseSingleCall reservationOverrideReasons(ReservationOverrideReasons reservationOverrideReasons) {
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
  public ReservationOverrideReasons getReservationOverrideReasons() {
    return reservationOverrideReasons;
  }

  public void setReservationOverrideReasons(ReservationOverrideReasons reservationOverrideReasons) {
    this.reservationOverrideReasons = reservationOverrideReasons;
  }

  public ReservationByIdResponseSingleCall reservationPackageList(List<@Valid ReservationPackagesDetailsResponse> reservationPackageList) {
    this.reservationPackageList = reservationPackageList;
    return this;
  }

  public ReservationByIdResponseSingleCall addReservationPackageListItem(ReservationPackagesDetailsResponse reservationPackageListItem) {
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
  public List<@Valid ReservationPackagesDetailsResponse> getReservationPackageList() {
    return reservationPackageList;
  }

  public void setReservationPackageList(List<@Valid ReservationPackagesDetailsResponse> reservationPackageList) {
    this.reservationPackageList = reservationPackageList;
  }

  public ReservationByIdResponseSingleCall reservationStatus(String reservationStatus) {
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

  public ReservationByIdResponseSingleCall roomStay(RoomStayByIdResponse roomStay) {
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
  public RoomStayByIdResponse getRoomStay() {
    return roomStay;
  }

  public void setRoomStay(RoomStayByIdResponse roomStay) {
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
    ReservationByIdResponseSingleCall reservationByIdResponseSingleCall = (ReservationByIdResponseSingleCall) o;
    return Objects.equals(this.additionalGuestInfo, reservationByIdResponseSingleCall.additionalGuestInfo) &&
        Objects.equals(this.balanceAmount, reservationByIdResponseSingleCall.balanceAmount) &&
        Objects.equals(this.billing, reservationByIdResponseSingleCall.billing) &&
        Objects.equals(this.cashiering, reservationByIdResponseSingleCall.cashiering) &&
        Objects.equals(this.depositPolicies, reservationByIdResponseSingleCall.depositPolicies) &&
        Objects.equals(this.gdsReferenceNumber, reservationByIdResponseSingleCall.gdsReferenceNumber) &&
        Objects.equals(this.guaranteeCode, reservationByIdResponseSingleCall.guaranteeCode) &&
        Objects.equals(this.onHold, reservationByIdResponseSingleCall.onHold) &&
        Objects.equals(this.paymentCard, reservationByIdResponseSingleCall.paymentCard) &&
        Objects.equals(this.rateInfo, reservationByIdResponseSingleCall.rateInfo) &&
        Objects.equals(this.reservationBooker, reservationByIdResponseSingleCall.reservationBooker) &&
        Objects.equals(this.reservationCompany, reservationByIdResponseSingleCall.reservationCompany) &&
        Objects.equals(this.reservationEmailNotifications, reservationByIdResponseSingleCall.reservationEmailNotifications) &&
        Objects.equals(this.reservationGuestList, reservationByIdResponseSingleCall.reservationGuestList) &&
        Objects.equals(this.reservationId, reservationByIdResponseSingleCall.reservationId) &&
        Objects.equals(this.reservationOverridden, reservationByIdResponseSingleCall.reservationOverridden) &&
        Objects.equals(this.reservationOverrideReasons, reservationByIdResponseSingleCall.reservationOverrideReasons) &&
        Objects.equals(this.reservationPackageList, reservationByIdResponseSingleCall.reservationPackageList) &&
        Objects.equals(this.reservationStatus, reservationByIdResponseSingleCall.reservationStatus) &&
        Objects.equals(this.roomStay, reservationByIdResponseSingleCall.roomStay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(additionalGuestInfo, balanceAmount, billing, cashiering, depositPolicies, gdsReferenceNumber, guaranteeCode, onHold, paymentCard, rateInfo, reservationBooker, reservationCompany, reservationEmailNotifications, reservationGuestList, reservationId, reservationOverridden, reservationOverrideReasons, reservationPackageList, reservationStatus, roomStay);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationByIdResponseSingleCall {\n");
    sb.append("    additionalGuestInfo: ").append(toIndentedString(additionalGuestInfo)).append("\n");
    sb.append("    balanceAmount: ").append(toIndentedString(balanceAmount)).append("\n");
    sb.append("    billing: ").append(toIndentedString(billing)).append("\n");
    sb.append("    cashiering: ").append(toIndentedString(cashiering)).append("\n");
    sb.append("    depositPolicies: ").append(toIndentedString(depositPolicies)).append("\n");
    sb.append("    gdsReferenceNumber: ").append(toIndentedString(gdsReferenceNumber)).append("\n");
    sb.append("    guaranteeCode: ").append(toIndentedString(guaranteeCode)).append("\n");
    sb.append("    onHold: ").append(toIndentedString(onHold)).append("\n");
    sb.append("    paymentCard: ").append(toIndentedString(paymentCard)).append("\n");
    sb.append("    rateInfo: ").append(toIndentedString(rateInfo)).append("\n");
    sb.append("    reservationBooker: ").append(toIndentedString(reservationBooker)).append("\n");
    sb.append("    reservationCompany: ").append(toIndentedString(reservationCompany)).append("\n");
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

