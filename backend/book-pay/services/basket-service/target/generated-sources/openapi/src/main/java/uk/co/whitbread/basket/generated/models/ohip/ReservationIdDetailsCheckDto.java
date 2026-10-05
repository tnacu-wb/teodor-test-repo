package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.AdditionalGuestInfo;
import uk.co.whitbread.basket.generated.models.ohip.Cashiering;
import uk.co.whitbread.basket.generated.models.ohip.Housekeeping;
import uk.co.whitbread.basket.generated.models.ohip.ReservationGuest;
import uk.co.whitbread.basket.generated.models.ohip.ReservationPackages;
import uk.co.whitbread.basket.generated.models.ohip.ReservationPaymentMethodDto;
import uk.co.whitbread.basket.generated.models.ohip.ReservationPoliciesDto;
import uk.co.whitbread.basket.generated.models.ohip.RoomStayDto;
import uk.co.whitbread.basket.generated.models.ohip.SourceOfSale;
import uk.co.whitbread.basket.generated.models.ohip.UniqueIdTypeDto;
import uk.co.whitbread.basket.generated.models.ohip.UserDefinedFieldsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ReservationIdDetailsCheckDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReservationIdDetailsCheckDto {

  private @Nullable AdditionalGuestInfo additionalGuestInfo;

  private @Nullable Boolean allowAutoCheckin;

  private @Nullable Boolean allowMobileCheckout;

  private @Nullable Boolean allowMobileViewFolio;

  private @Nullable Boolean allowPreRegistration;

  private @Nullable Cashiering cashiering;

  private @Nullable String computedReservationStatus;

  private @Nullable String createBusinessDate;

  private @Nullable String createDateTime;

  private @Nullable String creatorId;

  private @Nullable Boolean extSystemSync;

  private @Nullable Boolean hasOpenFolio;

  private @Nullable String hotelId;

  private @Nullable Housekeeping housekeeping;

  private @Nullable String lastModifierId;

  private @Nullable String lastModifyDateTime;

  private @Nullable Boolean optedForCommunication;

  private @Nullable Boolean preRegistered;

  private @Nullable Boolean printRate;

  @Valid
  private List<@Valid ReservationGuest> reservationGuests = new ArrayList<>();

  @Valid
  private List<@Valid UniqueIdTypeDto> reservationIdList = new ArrayList<>();

  @Valid
  private List<@Valid ReservationPackages> reservationPackages = new ArrayList<>();

  @Valid
  private List<@Valid ReservationPaymentMethodDto> reservationPaymentMethods = new ArrayList<>();

  private @Nullable ReservationPoliciesDto reservationPolicies;

  private @Nullable String reservationStatus;

  private @Nullable RoomStayDto roomStay;

  private @Nullable Boolean roomStayReservation;

  private @Nullable SourceOfSale sourceOfSale;

  private @Nullable Boolean upgradeEligible;

  private @Nullable UserDefinedFieldsDto userDefinedFields;

  private @Nullable Boolean walkIn;

  public ReservationIdDetailsCheckDto additionalGuestInfo(AdditionalGuestInfo additionalGuestInfo) {
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

  public ReservationIdDetailsCheckDto allowAutoCheckin(Boolean allowAutoCheckin) {
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

  public ReservationIdDetailsCheckDto allowMobileCheckout(Boolean allowMobileCheckout) {
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

  public ReservationIdDetailsCheckDto allowMobileViewFolio(Boolean allowMobileViewFolio) {
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

  public ReservationIdDetailsCheckDto allowPreRegistration(Boolean allowPreRegistration) {
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

  public ReservationIdDetailsCheckDto cashiering(Cashiering cashiering) {
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
  public Cashiering getCashiering() {
    return cashiering;
  }

  public void setCashiering(Cashiering cashiering) {
    this.cashiering = cashiering;
  }

  public ReservationIdDetailsCheckDto computedReservationStatus(String computedReservationStatus) {
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

  public ReservationIdDetailsCheckDto createBusinessDate(String createBusinessDate) {
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

  public ReservationIdDetailsCheckDto createDateTime(String createDateTime) {
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

  public ReservationIdDetailsCheckDto creatorId(String creatorId) {
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

  public ReservationIdDetailsCheckDto extSystemSync(Boolean extSystemSync) {
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

  public ReservationIdDetailsCheckDto hasOpenFolio(Boolean hasOpenFolio) {
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

  public ReservationIdDetailsCheckDto hotelId(String hotelId) {
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

  public ReservationIdDetailsCheckDto housekeeping(Housekeeping housekeeping) {
    this.housekeeping = housekeeping;
    return this;
  }

  /**
   * Get housekeeping
   * @return housekeeping
   */
  @Valid 
  @Schema(name = "housekeeping", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("housekeeping")
  public Housekeeping getHousekeeping() {
    return housekeeping;
  }

  public void setHousekeeping(Housekeeping housekeeping) {
    this.housekeeping = housekeeping;
  }

  public ReservationIdDetailsCheckDto lastModifierId(String lastModifierId) {
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

  public ReservationIdDetailsCheckDto lastModifyDateTime(String lastModifyDateTime) {
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

  public ReservationIdDetailsCheckDto optedForCommunication(Boolean optedForCommunication) {
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

  public ReservationIdDetailsCheckDto preRegistered(Boolean preRegistered) {
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

  public ReservationIdDetailsCheckDto printRate(Boolean printRate) {
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

  public ReservationIdDetailsCheckDto reservationGuests(List<@Valid ReservationGuest> reservationGuests) {
    this.reservationGuests = reservationGuests;
    return this;
  }

  public ReservationIdDetailsCheckDto addReservationGuestsItem(ReservationGuest reservationGuestsItem) {
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
  public List<@Valid ReservationGuest> getReservationGuests() {
    return reservationGuests;
  }

  public void setReservationGuests(List<@Valid ReservationGuest> reservationGuests) {
    this.reservationGuests = reservationGuests;
  }

  public ReservationIdDetailsCheckDto reservationIdList(List<@Valid UniqueIdTypeDto> reservationIdList) {
    this.reservationIdList = reservationIdList;
    return this;
  }

  public ReservationIdDetailsCheckDto addReservationIdListItem(UniqueIdTypeDto reservationIdListItem) {
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
  public List<@Valid UniqueIdTypeDto> getReservationIdList() {
    return reservationIdList;
  }

  public void setReservationIdList(List<@Valid UniqueIdTypeDto> reservationIdList) {
    this.reservationIdList = reservationIdList;
  }

  public ReservationIdDetailsCheckDto reservationPackages(List<@Valid ReservationPackages> reservationPackages) {
    this.reservationPackages = reservationPackages;
    return this;
  }

  public ReservationIdDetailsCheckDto addReservationPackagesItem(ReservationPackages reservationPackagesItem) {
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
  public List<@Valid ReservationPackages> getReservationPackages() {
    return reservationPackages;
  }

  public void setReservationPackages(List<@Valid ReservationPackages> reservationPackages) {
    this.reservationPackages = reservationPackages;
  }

  public ReservationIdDetailsCheckDto reservationPaymentMethods(List<@Valid ReservationPaymentMethodDto> reservationPaymentMethods) {
    this.reservationPaymentMethods = reservationPaymentMethods;
    return this;
  }

  public ReservationIdDetailsCheckDto addReservationPaymentMethodsItem(ReservationPaymentMethodDto reservationPaymentMethodsItem) {
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
  public List<@Valid ReservationPaymentMethodDto> getReservationPaymentMethods() {
    return reservationPaymentMethods;
  }

  public void setReservationPaymentMethods(List<@Valid ReservationPaymentMethodDto> reservationPaymentMethods) {
    this.reservationPaymentMethods = reservationPaymentMethods;
  }

  public ReservationIdDetailsCheckDto reservationPolicies(ReservationPoliciesDto reservationPolicies) {
    this.reservationPolicies = reservationPolicies;
    return this;
  }

  /**
   * Get reservationPolicies
   * @return reservationPolicies
   */
  @Valid 
  @Schema(name = "reservationPolicies", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservationPolicies")
  public ReservationPoliciesDto getReservationPolicies() {
    return reservationPolicies;
  }

  public void setReservationPolicies(ReservationPoliciesDto reservationPolicies) {
    this.reservationPolicies = reservationPolicies;
  }

  public ReservationIdDetailsCheckDto reservationStatus(String reservationStatus) {
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

  public ReservationIdDetailsCheckDto roomStay(RoomStayDto roomStay) {
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
  public RoomStayDto getRoomStay() {
    return roomStay;
  }

  public void setRoomStay(RoomStayDto roomStay) {
    this.roomStay = roomStay;
  }

  public ReservationIdDetailsCheckDto roomStayReservation(Boolean roomStayReservation) {
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

  public ReservationIdDetailsCheckDto sourceOfSale(SourceOfSale sourceOfSale) {
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
  public SourceOfSale getSourceOfSale() {
    return sourceOfSale;
  }

  public void setSourceOfSale(SourceOfSale sourceOfSale) {
    this.sourceOfSale = sourceOfSale;
  }

  public ReservationIdDetailsCheckDto upgradeEligible(Boolean upgradeEligible) {
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

  public ReservationIdDetailsCheckDto userDefinedFields(UserDefinedFieldsDto userDefinedFields) {
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

  public ReservationIdDetailsCheckDto walkIn(Boolean walkIn) {
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
    ReservationIdDetailsCheckDto reservationIdDetailsCheckDto = (ReservationIdDetailsCheckDto) o;
    return Objects.equals(this.additionalGuestInfo, reservationIdDetailsCheckDto.additionalGuestInfo) &&
        Objects.equals(this.allowAutoCheckin, reservationIdDetailsCheckDto.allowAutoCheckin) &&
        Objects.equals(this.allowMobileCheckout, reservationIdDetailsCheckDto.allowMobileCheckout) &&
        Objects.equals(this.allowMobileViewFolio, reservationIdDetailsCheckDto.allowMobileViewFolio) &&
        Objects.equals(this.allowPreRegistration, reservationIdDetailsCheckDto.allowPreRegistration) &&
        Objects.equals(this.cashiering, reservationIdDetailsCheckDto.cashiering) &&
        Objects.equals(this.computedReservationStatus, reservationIdDetailsCheckDto.computedReservationStatus) &&
        Objects.equals(this.createBusinessDate, reservationIdDetailsCheckDto.createBusinessDate) &&
        Objects.equals(this.createDateTime, reservationIdDetailsCheckDto.createDateTime) &&
        Objects.equals(this.creatorId, reservationIdDetailsCheckDto.creatorId) &&
        Objects.equals(this.extSystemSync, reservationIdDetailsCheckDto.extSystemSync) &&
        Objects.equals(this.hasOpenFolio, reservationIdDetailsCheckDto.hasOpenFolio) &&
        Objects.equals(this.hotelId, reservationIdDetailsCheckDto.hotelId) &&
        Objects.equals(this.housekeeping, reservationIdDetailsCheckDto.housekeeping) &&
        Objects.equals(this.lastModifierId, reservationIdDetailsCheckDto.lastModifierId) &&
        Objects.equals(this.lastModifyDateTime, reservationIdDetailsCheckDto.lastModifyDateTime) &&
        Objects.equals(this.optedForCommunication, reservationIdDetailsCheckDto.optedForCommunication) &&
        Objects.equals(this.preRegistered, reservationIdDetailsCheckDto.preRegistered) &&
        Objects.equals(this.printRate, reservationIdDetailsCheckDto.printRate) &&
        Objects.equals(this.reservationGuests, reservationIdDetailsCheckDto.reservationGuests) &&
        Objects.equals(this.reservationIdList, reservationIdDetailsCheckDto.reservationIdList) &&
        Objects.equals(this.reservationPackages, reservationIdDetailsCheckDto.reservationPackages) &&
        Objects.equals(this.reservationPaymentMethods, reservationIdDetailsCheckDto.reservationPaymentMethods) &&
        Objects.equals(this.reservationPolicies, reservationIdDetailsCheckDto.reservationPolicies) &&
        Objects.equals(this.reservationStatus, reservationIdDetailsCheckDto.reservationStatus) &&
        Objects.equals(this.roomStay, reservationIdDetailsCheckDto.roomStay) &&
        Objects.equals(this.roomStayReservation, reservationIdDetailsCheckDto.roomStayReservation) &&
        Objects.equals(this.sourceOfSale, reservationIdDetailsCheckDto.sourceOfSale) &&
        Objects.equals(this.upgradeEligible, reservationIdDetailsCheckDto.upgradeEligible) &&
        Objects.equals(this.userDefinedFields, reservationIdDetailsCheckDto.userDefinedFields) &&
        Objects.equals(this.walkIn, reservationIdDetailsCheckDto.walkIn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(additionalGuestInfo, allowAutoCheckin, allowMobileCheckout, allowMobileViewFolio, allowPreRegistration, cashiering, computedReservationStatus, createBusinessDate, createDateTime, creatorId, extSystemSync, hasOpenFolio, hotelId, housekeeping, lastModifierId, lastModifyDateTime, optedForCommunication, preRegistered, printRate, reservationGuests, reservationIdList, reservationPackages, reservationPaymentMethods, reservationPolicies, reservationStatus, roomStay, roomStayReservation, sourceOfSale, upgradeEligible, userDefinedFields, walkIn);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReservationIdDetailsCheckDto {\n");
    sb.append("    additionalGuestInfo: ").append(toIndentedString(additionalGuestInfo)).append("\n");
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
    sb.append("    housekeeping: ").append(toIndentedString(housekeeping)).append("\n");
    sb.append("    lastModifierId: ").append(toIndentedString(lastModifierId)).append("\n");
    sb.append("    lastModifyDateTime: ").append(toIndentedString(lastModifyDateTime)).append("\n");
    sb.append("    optedForCommunication: ").append(toIndentedString(optedForCommunication)).append("\n");
    sb.append("    preRegistered: ").append(toIndentedString(preRegistered)).append("\n");
    sb.append("    printRate: ").append(toIndentedString(printRate)).append("\n");
    sb.append("    reservationGuests: ").append(toIndentedString(reservationGuests)).append("\n");
    sb.append("    reservationIdList: ").append(toIndentedString(reservationIdList)).append("\n");
    sb.append("    reservationPackages: ").append(toIndentedString(reservationPackages)).append("\n");
    sb.append("    reservationPaymentMethods: ").append(toIndentedString(reservationPaymentMethods)).append("\n");
    sb.append("    reservationPolicies: ").append(toIndentedString(reservationPolicies)).append("\n");
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

