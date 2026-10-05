package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.AdditionalGuestInfoDto;
import uk.co.whitbread.basket.generated.models.ohip.BillingResponseDto;
import uk.co.whitbread.basket.generated.models.ohip.BookingAllowancesResponseDto;
import uk.co.whitbread.basket.generated.models.ohip.DepositFoliosResponseDto;
import uk.co.whitbread.basket.generated.models.ohip.DepositPoliciesDto;
import uk.co.whitbread.basket.generated.models.ohip.GuaranteeDto;
import uk.co.whitbread.basket.generated.models.ohip.RateInfoDto;
import uk.co.whitbread.basket.generated.models.ohip.ResCashieringTypeDto;
import uk.co.whitbread.basket.generated.models.ohip.ReservationAlertsDto;
import uk.co.whitbread.basket.generated.models.ohip.ReservationBookerDto;
import uk.co.whitbread.basket.generated.models.ohip.ReservationByIdGuestsDto;
import uk.co.whitbread.basket.generated.models.ohip.ReservationCompanyDto;
import uk.co.whitbread.basket.generated.models.ohip.ReservationEmailNotificationsDto;
import uk.co.whitbread.basket.generated.models.ohip.ReservationEventPreferenceDto;
import uk.co.whitbread.basket.generated.models.ohip.ReservationOverrideReasonsDto;
import uk.co.whitbread.basket.generated.models.ohip.ReservationPackagesDetailsResponseDto;
import uk.co.whitbread.basket.generated.models.ohip.ReservationPaymentCardTypeDto;
import uk.co.whitbread.basket.generated.models.ohip.RoomStayByIdDto;
import uk.co.whitbread.basket.generated.models.ohip.UserDefinedFieldsDto;
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
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationByIdDto {

  private @Nullable AdditionalGuestInfoDto additionalGuestInfo;

  @Valid
  private List<@Valid ReservationAlertsDto> alerts = new ArrayList<>();

  private @Nullable BigDecimal balanceAmount;

  private @Nullable BillingResponseDto billing;

  private @Nullable BookingAllowancesResponseDto bookingAllowancesResponse;

  private @Nullable ResCashieringTypeDto cashiering;

  private @Nullable DepositFoliosResponseDto depositFoliosResponse;

  @Valid
  private List<@Valid DepositPoliciesDto> depositPolicies = new ArrayList<>();

  private @Nullable String gdsReferenceNumber;

  private @Nullable GuaranteeDto guarantee;

  private @Nullable Boolean operaLinkedReservation;

  private @Nullable ReservationPaymentCardTypeDto paymentCard;

  private @Nullable Boolean preCheckInStatus;

  @Valid
  private List<@Valid ReservationEventPreferenceDto> preferences = new ArrayList<>();

  private @Nullable RateInfoDto rateInfo;

  private @Nullable ReservationBookerDto reservationBooker;

  private @Nullable ReservationCompanyDto reservationCompany;

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

  private @Nullable UserDefinedFieldsDto userDefinedFields;

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

  public ReservationByIdDto alerts(List<@Valid ReservationAlertsDto> alerts) {
    this.alerts = alerts;
    return this;
  }

  public ReservationByIdDto addAlertsItem(ReservationAlertsDto alertsItem) {
    if (this.alerts == null) {
      this.alerts = new ArrayList<>();
    }
    this.alerts.add(alertsItem);
    return this;
  }

  /**
   * Get alerts
   * @return alerts
   */
  @Valid 
  @Schema(name = "alerts", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("alerts")
  public List<@Valid ReservationAlertsDto> getAlerts() {
    return alerts;
  }

  public void setAlerts(List<@Valid ReservationAlertsDto> alerts) {
    this.alerts = alerts;
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

  public ReservationByIdDto bookingAllowancesResponse(BookingAllowancesResponseDto bookingAllowancesResponse) {
    this.bookingAllowancesResponse = bookingAllowancesResponse;
    return this;
  }

  /**
   * Get bookingAllowancesResponse
   * @return bookingAllowancesResponse
   */
  @Valid 
  @Schema(name = "bookingAllowancesResponse", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingAllowancesResponse")
  public BookingAllowancesResponseDto getBookingAllowancesResponse() {
    return bookingAllowancesResponse;
  }

  public void setBookingAllowancesResponse(BookingAllowancesResponseDto bookingAllowancesResponse) {
    this.bookingAllowancesResponse = bookingAllowancesResponse;
  }

  public ReservationByIdDto cashiering(ResCashieringTypeDto cashiering) {
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
  public ResCashieringTypeDto getCashiering() {
    return cashiering;
  }

  public void setCashiering(ResCashieringTypeDto cashiering) {
    this.cashiering = cashiering;
  }

  public ReservationByIdDto depositFoliosResponse(DepositFoliosResponseDto depositFoliosResponse) {
    this.depositFoliosResponse = depositFoliosResponse;
    return this;
  }

  /**
   * Get depositFoliosResponse
   * @return depositFoliosResponse
   */
  @Valid 
  @Schema(name = "depositFoliosResponse", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("depositFoliosResponse")
  public DepositFoliosResponseDto getDepositFoliosResponse() {
    return depositFoliosResponse;
  }

  public void setDepositFoliosResponse(DepositFoliosResponseDto depositFoliosResponse) {
    this.depositFoliosResponse = depositFoliosResponse;
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

  public ReservationByIdDto guarantee(GuaranteeDto guarantee) {
    this.guarantee = guarantee;
    return this;
  }

  /**
   * Get guarantee
   * @return guarantee
   */
  @Valid 
  @Schema(name = "guarantee", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guarantee")
  public GuaranteeDto getGuarantee() {
    return guarantee;
  }

  public void setGuarantee(GuaranteeDto guarantee) {
    this.guarantee = guarantee;
  }

  public ReservationByIdDto operaLinkedReservation(Boolean operaLinkedReservation) {
    this.operaLinkedReservation = operaLinkedReservation;
    return this;
  }

  /**
   * Get operaLinkedReservation
   * @return operaLinkedReservation
   */
  
  @Schema(name = "operaLinkedReservation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("operaLinkedReservation")
  public Boolean getOperaLinkedReservation() {
    return operaLinkedReservation;
  }

  public void setOperaLinkedReservation(Boolean operaLinkedReservation) {
    this.operaLinkedReservation = operaLinkedReservation;
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

  public ReservationByIdDto preCheckInStatus(Boolean preCheckInStatus) {
    this.preCheckInStatus = preCheckInStatus;
    return this;
  }

  /**
   * Get preCheckInStatus
   * @return preCheckInStatus
   */
  
  @Schema(name = "preCheckInStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("preCheckInStatus")
  public Boolean getPreCheckInStatus() {
    return preCheckInStatus;
  }

  public void setPreCheckInStatus(Boolean preCheckInStatus) {
    this.preCheckInStatus = preCheckInStatus;
  }

  public ReservationByIdDto preferences(List<@Valid ReservationEventPreferenceDto> preferences) {
    this.preferences = preferences;
    return this;
  }

  public ReservationByIdDto addPreferencesItem(ReservationEventPreferenceDto preferencesItem) {
    if (this.preferences == null) {
      this.preferences = new ArrayList<>();
    }
    this.preferences.add(preferencesItem);
    return this;
  }

  /**
   * Get preferences
   * @return preferences
   */
  @Valid 
  @Schema(name = "preferences", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("preferences")
  public List<@Valid ReservationEventPreferenceDto> getPreferences() {
    return preferences;
  }

  public void setPreferences(List<@Valid ReservationEventPreferenceDto> preferences) {
    this.preferences = preferences;
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

  public ReservationByIdDto reservationCompany(ReservationCompanyDto reservationCompany) {
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
  public ReservationCompanyDto getReservationCompany() {
    return reservationCompany;
  }

  public void setReservationCompany(ReservationCompanyDto reservationCompany) {
    this.reservationCompany = reservationCompany;
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

  public ReservationByIdDto userDefinedFields(UserDefinedFieldsDto userDefinedFields) {
    this.userDefinedFields = userDefinedFields;
    return this;
  }

  /**
   * Get userDefinedFields
   * @return userDefinedFields
   */
  @Valid 
  @Schema(name = "userDefinedFields", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userDefinedFields")
  public UserDefinedFieldsDto getUserDefinedFields() {
    return userDefinedFields;
  }

  public void setUserDefinedFields(UserDefinedFieldsDto userDefinedFields) {
    this.userDefinedFields = userDefinedFields;
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
        Objects.equals(this.alerts, reservationByIdDto.alerts) &&
        Objects.equals(this.balanceAmount, reservationByIdDto.balanceAmount) &&
        Objects.equals(this.billing, reservationByIdDto.billing) &&
        Objects.equals(this.bookingAllowancesResponse, reservationByIdDto.bookingAllowancesResponse) &&
        Objects.equals(this.cashiering, reservationByIdDto.cashiering) &&
        Objects.equals(this.depositFoliosResponse, reservationByIdDto.depositFoliosResponse) &&
        Objects.equals(this.depositPolicies, reservationByIdDto.depositPolicies) &&
        Objects.equals(this.gdsReferenceNumber, reservationByIdDto.gdsReferenceNumber) &&
        Objects.equals(this.guarantee, reservationByIdDto.guarantee) &&
        Objects.equals(this.operaLinkedReservation, reservationByIdDto.operaLinkedReservation) &&
        Objects.equals(this.paymentCard, reservationByIdDto.paymentCard) &&
        Objects.equals(this.preCheckInStatus, reservationByIdDto.preCheckInStatus) &&
        Objects.equals(this.preferences, reservationByIdDto.preferences) &&
        Objects.equals(this.rateInfo, reservationByIdDto.rateInfo) &&
        Objects.equals(this.reservationBooker, reservationByIdDto.reservationBooker) &&
        Objects.equals(this.reservationCompany, reservationByIdDto.reservationCompany) &&
        Objects.equals(this.reservationEmailNotifications, reservationByIdDto.reservationEmailNotifications) &&
        Objects.equals(this.reservationGuestList, reservationByIdDto.reservationGuestList) &&
        Objects.equals(this.reservationId, reservationByIdDto.reservationId) &&
        Objects.equals(this.reservationOverridden, reservationByIdDto.reservationOverridden) &&
        Objects.equals(this.reservationOverrideReasons, reservationByIdDto.reservationOverrideReasons) &&
        Objects.equals(this.reservationPackageList, reservationByIdDto.reservationPackageList) &&
        Objects.equals(this.reservationStatus, reservationByIdDto.reservationStatus) &&
        Objects.equals(this.roomStay, reservationByIdDto.roomStay) &&
        Objects.equals(this.userDefinedFields, reservationByIdDto.userDefinedFields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(additionalGuestInfo, alerts, balanceAmount, billing, bookingAllowancesResponse, cashiering, depositFoliosResponse, depositPolicies, gdsReferenceNumber, guarantee, operaLinkedReservation, paymentCard, preCheckInStatus, preferences, rateInfo, reservationBooker, reservationCompany, reservationEmailNotifications, reservationGuestList, reservationId, reservationOverridden, reservationOverrideReasons, reservationPackageList, reservationStatus, roomStay, userDefinedFields);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationByIdDto {\n");
    sb.append("    additionalGuestInfo: ").append(toIndentedString(additionalGuestInfo)).append("\n");
    sb.append("    alerts: ").append(toIndentedString(alerts)).append("\n");
    sb.append("    balanceAmount: ").append(toIndentedString(balanceAmount)).append("\n");
    sb.append("    billing: ").append(toIndentedString(billing)).append("\n");
    sb.append("    bookingAllowancesResponse: ").append(toIndentedString(bookingAllowancesResponse)).append("\n");
    sb.append("    cashiering: ").append(toIndentedString(cashiering)).append("\n");
    sb.append("    depositFoliosResponse: ").append(toIndentedString(depositFoliosResponse)).append("\n");
    sb.append("    depositPolicies: ").append(toIndentedString(depositPolicies)).append("\n");
    sb.append("    gdsReferenceNumber: ").append(toIndentedString(gdsReferenceNumber)).append("\n");
    sb.append("    guarantee: ").append(toIndentedString(guarantee)).append("\n");
    sb.append("    operaLinkedReservation: ").append(toIndentedString(operaLinkedReservation)).append("\n");
    sb.append("    paymentCard: ").append(toIndentedString(paymentCard)).append("\n");
    sb.append("    preCheckInStatus: ").append(toIndentedString(preCheckInStatus)).append("\n");
    sb.append("    preferences: ").append(toIndentedString(preferences)).append("\n");
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
    sb.append("    userDefinedFields: ").append(toIndentedString(userDefinedFields)).append("\n");
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

