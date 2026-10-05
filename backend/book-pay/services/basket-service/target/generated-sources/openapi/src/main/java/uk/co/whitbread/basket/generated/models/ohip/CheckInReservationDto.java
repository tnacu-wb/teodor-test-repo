package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.CashieringDto;
import uk.co.whitbread.basket.generated.models.ohip.CheckInReservationGuestsDto;
import uk.co.whitbread.basket.generated.models.ohip.CheckInReservationIdListDto;
import uk.co.whitbread.basket.generated.models.ohip.CheckInReservationPaymentMethodsDto;
import uk.co.whitbread.basket.generated.models.ohip.CheckInRoomStayDto;
import uk.co.whitbread.basket.generated.models.ohip.ReservationIndicatorsDto;
import uk.co.whitbread.basket.generated.models.ohip.ReservationPackagesDto;
import uk.co.whitbread.basket.generated.models.ohip.SourceOfSaleDto;
import uk.co.whitbread.basket.generated.models.ohip.UserDefinedFieldsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CheckInReservationDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CheckInReservationDto {

  @Valid
  private List<String> alerts = new ArrayList<>();

  private @Nullable Boolean allowAutoCheckin;

  private @Nullable Boolean allowMobileCheckout;

  private @Nullable Boolean allowMobileViewFolio;

  private @Nullable Boolean allowPreRegistration;

  private @Nullable CashieringDto cashiering;

  private @Nullable String computedReservationStatus;

  private @Nullable String createBusinessDate;

  private @Nullable String createDateTime;

  private @Nullable String creatorId;

  private @Nullable Boolean extSystemSync;

  private @Nullable Boolean hasOpenFolio;

  private @Nullable String hotelId;

  private @Nullable String lastModifierId;

  private @Nullable String lastModifyDateTime;

  private @Nullable Boolean optedForCommunication;

  private @Nullable Boolean preRegistered;

  private @Nullable Boolean printRate;

  @Valid
  private List<@Valid CheckInReservationGuestsDto> reservationGuests = new ArrayList<>();

  @Valid
  private List<@Valid CheckInReservationIdListDto> reservationIdList = new ArrayList<>();

  @Valid
  private List<@Valid ReservationIndicatorsDto> reservationIndicators = new ArrayList<>();

  @Valid
  private List<String> reservationMemberships = new ArrayList<>();

  @Valid
  private List<@Valid ReservationPackagesDto> reservationPackages = new ArrayList<>();

  @Valid
  private List<@Valid CheckInReservationPaymentMethodsDto> reservationPaymentMethods = new ArrayList<>();

  private @Nullable String reservationStatus;

  private @Nullable CheckInRoomStayDto roomStay;

  private @Nullable Boolean roomStayReservation;

  private @Nullable SourceOfSaleDto sourceOfSale;

  private @Nullable Boolean upgradeEligible;

  private @Nullable UserDefinedFieldsDto userDefinedFields;

  private @Nullable Boolean walkIn;

  public CheckInReservationDto alerts(List<String> alerts) {
    this.alerts = alerts;
    return this;
  }

  public CheckInReservationDto addAlertsItem(String alertsItem) {
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
  
  @Schema(name = "alerts", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("alerts")
  public List<String> getAlerts() {
    return alerts;
  }

  public void setAlerts(List<String> alerts) {
    this.alerts = alerts;
  }

  public CheckInReservationDto allowAutoCheckin(Boolean allowAutoCheckin) {
    this.allowAutoCheckin = allowAutoCheckin;
    return this;
  }

  /**
   * Get allowAutoCheckin
   * @return allowAutoCheckin
   */
  
  @Schema(name = "allowAutoCheckin", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allowAutoCheckin")
  public Boolean getAllowAutoCheckin() {
    return allowAutoCheckin;
  }

  public void setAllowAutoCheckin(Boolean allowAutoCheckin) {
    this.allowAutoCheckin = allowAutoCheckin;
  }

  public CheckInReservationDto allowMobileCheckout(Boolean allowMobileCheckout) {
    this.allowMobileCheckout = allowMobileCheckout;
    return this;
  }

  /**
   * Get allowMobileCheckout
   * @return allowMobileCheckout
   */
  
  @Schema(name = "allowMobileCheckout", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allowMobileCheckout")
  public Boolean getAllowMobileCheckout() {
    return allowMobileCheckout;
  }

  public void setAllowMobileCheckout(Boolean allowMobileCheckout) {
    this.allowMobileCheckout = allowMobileCheckout;
  }

  public CheckInReservationDto allowMobileViewFolio(Boolean allowMobileViewFolio) {
    this.allowMobileViewFolio = allowMobileViewFolio;
    return this;
  }

  /**
   * Get allowMobileViewFolio
   * @return allowMobileViewFolio
   */
  
  @Schema(name = "allowMobileViewFolio", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allowMobileViewFolio")
  public Boolean getAllowMobileViewFolio() {
    return allowMobileViewFolio;
  }

  public void setAllowMobileViewFolio(Boolean allowMobileViewFolio) {
    this.allowMobileViewFolio = allowMobileViewFolio;
  }

  public CheckInReservationDto allowPreRegistration(Boolean allowPreRegistration) {
    this.allowPreRegistration = allowPreRegistration;
    return this;
  }

  /**
   * Get allowPreRegistration
   * @return allowPreRegistration
   */
  
  @Schema(name = "allowPreRegistration", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("allowPreRegistration")
  public Boolean getAllowPreRegistration() {
    return allowPreRegistration;
  }

  public void setAllowPreRegistration(Boolean allowPreRegistration) {
    this.allowPreRegistration = allowPreRegistration;
  }

  public CheckInReservationDto cashiering(CashieringDto cashiering) {
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
  public CashieringDto getCashiering() {
    return cashiering;
  }

  public void setCashiering(CashieringDto cashiering) {
    this.cashiering = cashiering;
  }

  public CheckInReservationDto computedReservationStatus(String computedReservationStatus) {
    this.computedReservationStatus = computedReservationStatus;
    return this;
  }

  /**
   * Get computedReservationStatus
   * @return computedReservationStatus
   */
  
  @Schema(name = "computedReservationStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("computedReservationStatus")
  public String getComputedReservationStatus() {
    return computedReservationStatus;
  }

  public void setComputedReservationStatus(String computedReservationStatus) {
    this.computedReservationStatus = computedReservationStatus;
  }

  public CheckInReservationDto createBusinessDate(String createBusinessDate) {
    this.createBusinessDate = createBusinessDate;
    return this;
  }

  /**
   * Get createBusinessDate
   * @return createBusinessDate
   */
  
  @Schema(name = "createBusinessDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("createBusinessDate")
  public String getCreateBusinessDate() {
    return createBusinessDate;
  }

  public void setCreateBusinessDate(String createBusinessDate) {
    this.createBusinessDate = createBusinessDate;
  }

  public CheckInReservationDto createDateTime(String createDateTime) {
    this.createDateTime = createDateTime;
    return this;
  }

  /**
   * Get createDateTime
   * @return createDateTime
   */
  
  @Schema(name = "createDateTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("createDateTime")
  public String getCreateDateTime() {
    return createDateTime;
  }

  public void setCreateDateTime(String createDateTime) {
    this.createDateTime = createDateTime;
  }

  public CheckInReservationDto creatorId(String creatorId) {
    this.creatorId = creatorId;
    return this;
  }

  /**
   * Get creatorId
   * @return creatorId
   */
  
  @Schema(name = "creatorId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("creatorId")
  public String getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(String creatorId) {
    this.creatorId = creatorId;
  }

  public CheckInReservationDto extSystemSync(Boolean extSystemSync) {
    this.extSystemSync = extSystemSync;
    return this;
  }

  /**
   * Get extSystemSync
   * @return extSystemSync
   */
  
  @Schema(name = "extSystemSync", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("extSystemSync")
  public Boolean getExtSystemSync() {
    return extSystemSync;
  }

  public void setExtSystemSync(Boolean extSystemSync) {
    this.extSystemSync = extSystemSync;
  }

  public CheckInReservationDto hasOpenFolio(Boolean hasOpenFolio) {
    this.hasOpenFolio = hasOpenFolio;
    return this;
  }

  /**
   * Get hasOpenFolio
   * @return hasOpenFolio
   */
  
  @Schema(name = "hasOpenFolio", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hasOpenFolio")
  public Boolean getHasOpenFolio() {
    return hasOpenFolio;
  }

  public void setHasOpenFolio(Boolean hasOpenFolio) {
    this.hasOpenFolio = hasOpenFolio;
  }

  public CheckInReservationDto hotelId(String hotelId) {
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

  public CheckInReservationDto lastModifierId(String lastModifierId) {
    this.lastModifierId = lastModifierId;
    return this;
  }

  /**
   * Get lastModifierId
   * @return lastModifierId
   */
  
  @Schema(name = "lastModifierId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastModifierId")
  public String getLastModifierId() {
    return lastModifierId;
  }

  public void setLastModifierId(String lastModifierId) {
    this.lastModifierId = lastModifierId;
  }

  public CheckInReservationDto lastModifyDateTime(String lastModifyDateTime) {
    this.lastModifyDateTime = lastModifyDateTime;
    return this;
  }

  /**
   * Get lastModifyDateTime
   * @return lastModifyDateTime
   */
  
  @Schema(name = "lastModifyDateTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastModifyDateTime")
  public String getLastModifyDateTime() {
    return lastModifyDateTime;
  }

  public void setLastModifyDateTime(String lastModifyDateTime) {
    this.lastModifyDateTime = lastModifyDateTime;
  }

  public CheckInReservationDto optedForCommunication(Boolean optedForCommunication) {
    this.optedForCommunication = optedForCommunication;
    return this;
  }

  /**
   * Get optedForCommunication
   * @return optedForCommunication
   */
  
  @Schema(name = "optedForCommunication", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("optedForCommunication")
  public Boolean getOptedForCommunication() {
    return optedForCommunication;
  }

  public void setOptedForCommunication(Boolean optedForCommunication) {
    this.optedForCommunication = optedForCommunication;
  }

  public CheckInReservationDto preRegistered(Boolean preRegistered) {
    this.preRegistered = preRegistered;
    return this;
  }

  /**
   * Get preRegistered
   * @return preRegistered
   */
  
  @Schema(name = "preRegistered", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("preRegistered")
  public Boolean getPreRegistered() {
    return preRegistered;
  }

  public void setPreRegistered(Boolean preRegistered) {
    this.preRegistered = preRegistered;
  }

  public CheckInReservationDto printRate(Boolean printRate) {
    this.printRate = printRate;
    return this;
  }

  /**
   * Get printRate
   * @return printRate
   */
  
  @Schema(name = "printRate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("printRate")
  public Boolean getPrintRate() {
    return printRate;
  }

  public void setPrintRate(Boolean printRate) {
    this.printRate = printRate;
  }

  public CheckInReservationDto reservationGuests(List<@Valid CheckInReservationGuestsDto> reservationGuests) {
    this.reservationGuests = reservationGuests;
    return this;
  }

  public CheckInReservationDto addReservationGuestsItem(CheckInReservationGuestsDto reservationGuestsItem) {
    if (this.reservationGuests == null) {
      this.reservationGuests = new ArrayList<>();
    }
    this.reservationGuests.add(reservationGuestsItem);
    return this;
  }

  /**
   * Get reservationGuests
   * @return reservationGuests
   */
  @Valid 
  @Schema(name = "reservationGuests", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationGuests")
  public List<@Valid CheckInReservationGuestsDto> getReservationGuests() {
    return reservationGuests;
  }

  public void setReservationGuests(List<@Valid CheckInReservationGuestsDto> reservationGuests) {
    this.reservationGuests = reservationGuests;
  }

  public CheckInReservationDto reservationIdList(List<@Valid CheckInReservationIdListDto> reservationIdList) {
    this.reservationIdList = reservationIdList;
    return this;
  }

  public CheckInReservationDto addReservationIdListItem(CheckInReservationIdListDto reservationIdListItem) {
    if (this.reservationIdList == null) {
      this.reservationIdList = new ArrayList<>();
    }
    this.reservationIdList.add(reservationIdListItem);
    return this;
  }

  /**
   * Get reservationIdList
   * @return reservationIdList
   */
  @Valid 
  @Schema(name = "reservationIdList", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationIdList")
  public List<@Valid CheckInReservationIdListDto> getReservationIdList() {
    return reservationIdList;
  }

  public void setReservationIdList(List<@Valid CheckInReservationIdListDto> reservationIdList) {
    this.reservationIdList = reservationIdList;
  }

  public CheckInReservationDto reservationIndicators(List<@Valid ReservationIndicatorsDto> reservationIndicators) {
    this.reservationIndicators = reservationIndicators;
    return this;
  }

  public CheckInReservationDto addReservationIndicatorsItem(ReservationIndicatorsDto reservationIndicatorsItem) {
    if (this.reservationIndicators == null) {
      this.reservationIndicators = new ArrayList<>();
    }
    this.reservationIndicators.add(reservationIndicatorsItem);
    return this;
  }

  /**
   * Get reservationIndicators
   * @return reservationIndicators
   */
  @Valid 
  @Schema(name = "reservationIndicators", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationIndicators")
  public List<@Valid ReservationIndicatorsDto> getReservationIndicators() {
    return reservationIndicators;
  }

  public void setReservationIndicators(List<@Valid ReservationIndicatorsDto> reservationIndicators) {
    this.reservationIndicators = reservationIndicators;
  }

  public CheckInReservationDto reservationMemberships(List<String> reservationMemberships) {
    this.reservationMemberships = reservationMemberships;
    return this;
  }

  public CheckInReservationDto addReservationMembershipsItem(String reservationMembershipsItem) {
    if (this.reservationMemberships == null) {
      this.reservationMemberships = new ArrayList<>();
    }
    this.reservationMemberships.add(reservationMembershipsItem);
    return this;
  }

  /**
   * Get reservationMemberships
   * @return reservationMemberships
   */
  
  @Schema(name = "reservationMemberships", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationMemberships")
  public List<String> getReservationMemberships() {
    return reservationMemberships;
  }

  public void setReservationMemberships(List<String> reservationMemberships) {
    this.reservationMemberships = reservationMemberships;
  }

  public CheckInReservationDto reservationPackages(List<@Valid ReservationPackagesDto> reservationPackages) {
    this.reservationPackages = reservationPackages;
    return this;
  }

  public CheckInReservationDto addReservationPackagesItem(ReservationPackagesDto reservationPackagesItem) {
    if (this.reservationPackages == null) {
      this.reservationPackages = new ArrayList<>();
    }
    this.reservationPackages.add(reservationPackagesItem);
    return this;
  }

  /**
   * Get reservationPackages
   * @return reservationPackages
   */
  @Valid 
  @Schema(name = "reservationPackages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationPackages")
  public List<@Valid ReservationPackagesDto> getReservationPackages() {
    return reservationPackages;
  }

  public void setReservationPackages(List<@Valid ReservationPackagesDto> reservationPackages) {
    this.reservationPackages = reservationPackages;
  }

  public CheckInReservationDto reservationPaymentMethods(List<@Valid CheckInReservationPaymentMethodsDto> reservationPaymentMethods) {
    this.reservationPaymentMethods = reservationPaymentMethods;
    return this;
  }

  public CheckInReservationDto addReservationPaymentMethodsItem(CheckInReservationPaymentMethodsDto reservationPaymentMethodsItem) {
    if (this.reservationPaymentMethods == null) {
      this.reservationPaymentMethods = new ArrayList<>();
    }
    this.reservationPaymentMethods.add(reservationPaymentMethodsItem);
    return this;
  }

  /**
   * Get reservationPaymentMethods
   * @return reservationPaymentMethods
   */
  @Valid 
  @Schema(name = "reservationPaymentMethods", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationPaymentMethods")
  public List<@Valid CheckInReservationPaymentMethodsDto> getReservationPaymentMethods() {
    return reservationPaymentMethods;
  }

  public void setReservationPaymentMethods(List<@Valid CheckInReservationPaymentMethodsDto> reservationPaymentMethods) {
    this.reservationPaymentMethods = reservationPaymentMethods;
  }

  public CheckInReservationDto reservationStatus(String reservationStatus) {
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

  public CheckInReservationDto roomStay(CheckInRoomStayDto roomStay) {
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
  public CheckInRoomStayDto getRoomStay() {
    return roomStay;
  }

  public void setRoomStay(CheckInRoomStayDto roomStay) {
    this.roomStay = roomStay;
  }

  public CheckInReservationDto roomStayReservation(Boolean roomStayReservation) {
    this.roomStayReservation = roomStayReservation;
    return this;
  }

  /**
   * Get roomStayReservation
   * @return roomStayReservation
   */
  
  @Schema(name = "roomStayReservation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomStayReservation")
  public Boolean getRoomStayReservation() {
    return roomStayReservation;
  }

  public void setRoomStayReservation(Boolean roomStayReservation) {
    this.roomStayReservation = roomStayReservation;
  }

  public CheckInReservationDto sourceOfSale(SourceOfSaleDto sourceOfSale) {
    this.sourceOfSale = sourceOfSale;
    return this;
  }

  /**
   * Get sourceOfSale
   * @return sourceOfSale
   */
  @Valid 
  @Schema(name = "sourceOfSale", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sourceOfSale")
  public SourceOfSaleDto getSourceOfSale() {
    return sourceOfSale;
  }

  public void setSourceOfSale(SourceOfSaleDto sourceOfSale) {
    this.sourceOfSale = sourceOfSale;
  }

  public CheckInReservationDto upgradeEligible(Boolean upgradeEligible) {
    this.upgradeEligible = upgradeEligible;
    return this;
  }

  /**
   * Get upgradeEligible
   * @return upgradeEligible
   */
  
  @Schema(name = "upgradeEligible", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("upgradeEligible")
  public Boolean getUpgradeEligible() {
    return upgradeEligible;
  }

  public void setUpgradeEligible(Boolean upgradeEligible) {
    this.upgradeEligible = upgradeEligible;
  }

  public CheckInReservationDto userDefinedFields(UserDefinedFieldsDto userDefinedFields) {
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

  public CheckInReservationDto walkIn(Boolean walkIn) {
    this.walkIn = walkIn;
    return this;
  }

  /**
   * Get walkIn
   * @return walkIn
   */
  
  @Schema(name = "walkIn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("walkIn")
  public Boolean getWalkIn() {
    return walkIn;
  }

  public void setWalkIn(Boolean walkIn) {
    this.walkIn = walkIn;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CheckInReservationDto checkInReservationDto = (CheckInReservationDto) o;
    return Objects.equals(this.alerts, checkInReservationDto.alerts) &&
        Objects.equals(this.allowAutoCheckin, checkInReservationDto.allowAutoCheckin) &&
        Objects.equals(this.allowMobileCheckout, checkInReservationDto.allowMobileCheckout) &&
        Objects.equals(this.allowMobileViewFolio, checkInReservationDto.allowMobileViewFolio) &&
        Objects.equals(this.allowPreRegistration, checkInReservationDto.allowPreRegistration) &&
        Objects.equals(this.cashiering, checkInReservationDto.cashiering) &&
        Objects.equals(this.computedReservationStatus, checkInReservationDto.computedReservationStatus) &&
        Objects.equals(this.createBusinessDate, checkInReservationDto.createBusinessDate) &&
        Objects.equals(this.createDateTime, checkInReservationDto.createDateTime) &&
        Objects.equals(this.creatorId, checkInReservationDto.creatorId) &&
        Objects.equals(this.extSystemSync, checkInReservationDto.extSystemSync) &&
        Objects.equals(this.hasOpenFolio, checkInReservationDto.hasOpenFolio) &&
        Objects.equals(this.hotelId, checkInReservationDto.hotelId) &&
        Objects.equals(this.lastModifierId, checkInReservationDto.lastModifierId) &&
        Objects.equals(this.lastModifyDateTime, checkInReservationDto.lastModifyDateTime) &&
        Objects.equals(this.optedForCommunication, checkInReservationDto.optedForCommunication) &&
        Objects.equals(this.preRegistered, checkInReservationDto.preRegistered) &&
        Objects.equals(this.printRate, checkInReservationDto.printRate) &&
        Objects.equals(this.reservationGuests, checkInReservationDto.reservationGuests) &&
        Objects.equals(this.reservationIdList, checkInReservationDto.reservationIdList) &&
        Objects.equals(this.reservationIndicators, checkInReservationDto.reservationIndicators) &&
        Objects.equals(this.reservationMemberships, checkInReservationDto.reservationMemberships) &&
        Objects.equals(this.reservationPackages, checkInReservationDto.reservationPackages) &&
        Objects.equals(this.reservationPaymentMethods, checkInReservationDto.reservationPaymentMethods) &&
        Objects.equals(this.reservationStatus, checkInReservationDto.reservationStatus) &&
        Objects.equals(this.roomStay, checkInReservationDto.roomStay) &&
        Objects.equals(this.roomStayReservation, checkInReservationDto.roomStayReservation) &&
        Objects.equals(this.sourceOfSale, checkInReservationDto.sourceOfSale) &&
        Objects.equals(this.upgradeEligible, checkInReservationDto.upgradeEligible) &&
        Objects.equals(this.userDefinedFields, checkInReservationDto.userDefinedFields) &&
        Objects.equals(this.walkIn, checkInReservationDto.walkIn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(alerts, allowAutoCheckin, allowMobileCheckout, allowMobileViewFolio, allowPreRegistration, cashiering, computedReservationStatus, createBusinessDate, createDateTime, creatorId, extSystemSync, hasOpenFolio, hotelId, lastModifierId, lastModifyDateTime, optedForCommunication, preRegistered, printRate, reservationGuests, reservationIdList, reservationIndicators, reservationMemberships, reservationPackages, reservationPaymentMethods, reservationStatus, roomStay, roomStayReservation, sourceOfSale, upgradeEligible, userDefinedFields, walkIn);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CheckInReservationDto {\n");
    sb.append("    alerts: ").append(toIndentedString(alerts)).append("\n");
    sb.append("    allowAutoCheckin: ").append(toIndentedString(allowAutoCheckin)).append("\n");
    sb.append("    allowMobileCheckout: ").append(toIndentedString(allowMobileCheckout)).append("\n");
    sb.append("    allowMobileViewFolio: ").append(toIndentedString(allowMobileViewFolio)).append("\n");
    sb.append("    allowPreRegistration: ").append(toIndentedString(allowPreRegistration)).append("\n");
    sb.append("    cashiering: ").append(toIndentedString(cashiering)).append("\n");
    sb.append("    computedReservationStatus: ").append(toIndentedString(computedReservationStatus)).append("\n");
    sb.append("    createBusinessDate: ").append(toIndentedString(createBusinessDate)).append("\n");
    sb.append("    createDateTime: ").append(toIndentedString(createDateTime)).append("\n");
    sb.append("    creatorId: ").append(toIndentedString(creatorId)).append("\n");
    sb.append("    extSystemSync: ").append(toIndentedString(extSystemSync)).append("\n");
    sb.append("    hasOpenFolio: ").append(toIndentedString(hasOpenFolio)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    lastModifierId: ").append(toIndentedString(lastModifierId)).append("\n");
    sb.append("    lastModifyDateTime: ").append(toIndentedString(lastModifyDateTime)).append("\n");
    sb.append("    optedForCommunication: ").append(toIndentedString(optedForCommunication)).append("\n");
    sb.append("    preRegistered: ").append(toIndentedString(preRegistered)).append("\n");
    sb.append("    printRate: ").append(toIndentedString(printRate)).append("\n");
    sb.append("    reservationGuests: ").append(toIndentedString(reservationGuests)).append("\n");
    sb.append("    reservationIdList: ").append(toIndentedString(reservationIdList)).append("\n");
    sb.append("    reservationIndicators: ").append(toIndentedString(reservationIndicators)).append("\n");
    sb.append("    reservationMemberships: ").append(toIndentedString(reservationMemberships)).append("\n");
    sb.append("    reservationPackages: ").append(toIndentedString(reservationPackages)).append("\n");
    sb.append("    reservationPaymentMethods: ").append(toIndentedString(reservationPaymentMethods)).append("\n");
    sb.append("    reservationStatus: ").append(toIndentedString(reservationStatus)).append("\n");
    sb.append("    roomStay: ").append(toIndentedString(roomStay)).append("\n");
    sb.append("    roomStayReservation: ").append(toIndentedString(roomStayReservation)).append("\n");
    sb.append("    sourceOfSale: ").append(toIndentedString(sourceOfSale)).append("\n");
    sb.append("    upgradeEligible: ").append(toIndentedString(upgradeEligible)).append("\n");
    sb.append("    userDefinedFields: ").append(toIndentedString(userDefinedFields)).append("\n");
    sb.append("    walkIn: ").append(toIndentedString(walkIn)).append("\n");
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

