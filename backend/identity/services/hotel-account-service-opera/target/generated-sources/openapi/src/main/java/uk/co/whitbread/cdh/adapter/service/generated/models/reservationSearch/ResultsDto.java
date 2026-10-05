package uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.BookerDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.EmployeeAnswersDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.PaymentCardDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.PriceDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.RoomsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ResultsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-09T08:37:59.335673+03:00[Europe/Bucharest]", comments = "Generator version: 7.14.0")
public class ResultsDto {

  private @Nullable String accountType;

  private @Nullable PriceDto activeTotalCost;

  private @Nullable Boolean amendable;

  private @Nullable String arrivalDate;

  private @Nullable Long bartEmployeeId;

  private @Nullable String bartGuestHistoryNumber;

  private @Nullable BookerDto booker;

  private @Nullable String bookingDate;

  private @Nullable String bookingMethod;

  private @Nullable String bookingMethodDescription;

  private @Nullable String bookingReference;

  private @Nullable String bookingType;

  private @Nullable Boolean cancellable;

  private @Nullable String cancellationDate;

  private @Nullable String cancellationId;

  private @Nullable String cellCode;

  private @Nullable Integer cellCodeId;

  private @Nullable String companyAccountId;

  private @Nullable String crmCompanyId;

  private @Nullable String customerAccountId;

  private @Nullable String customerReference;

  private @Nullable String dctmcUserName;

  private @Nullable String departureDate;

  private @Nullable String employeeAccountId;

  private @Nullable EmployeeAnswersDto employeeAnswers;

  private @Nullable Long globalCompanyId;

  private @Nullable String hotelCode;

  private @Nullable String hotelName;

  private @Nullable String iataNumber;

  private @Nullable String id;

  private @Nullable Boolean _package;

  private @Nullable String partitionKey;

  private @Nullable PaymentCardDto paymentCard;

  private @Nullable Boolean prePaid;

  private @Nullable PriceDto prePaidAmount;

  private @Nullable String purchaseOrder;

  private @Nullable String rateCategory;

  private @Nullable String rateCategoryCode;

  private @Nullable String rateClass;

  private @Nullable String ratePlan;

  @Valid
  private List<@Valid RoomsDto> rooms = new ArrayList<>();

  private @Nullable String sourceSystem;

  private @Nullable String status;

  private @Nullable String statusSortOrder;

  private @Nullable String thirdPartyReference;

  private @Nullable PriceDto totalCost;

  private @Nullable PriceDto upsellTotalCost;

  private @Nullable Boolean walkIn;

  public ResultsDto accountType(@Nullable String accountType) {
    this.accountType = accountType;
    return this;
  }

  /**
   * Get accountType
   * @return accountType
   */
  
  @Schema(name = "accountType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accountType")
  public @Nullable String getAccountType() {
    return accountType;
  }

  public void setAccountType(@Nullable String accountType) {
    this.accountType = accountType;
  }

  public ResultsDto activeTotalCost(@Nullable PriceDto activeTotalCost) {
    this.activeTotalCost = activeTotalCost;
    return this;
  }

  /**
   * Get activeTotalCost
   * @return activeTotalCost
   */
  @Valid 
  @Schema(name = "activeTotalCost", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("activeTotalCost")
  public @Nullable PriceDto getActiveTotalCost() {
    return activeTotalCost;
  }

  public void setActiveTotalCost(@Nullable PriceDto activeTotalCost) {
    this.activeTotalCost = activeTotalCost;
  }

  public ResultsDto amendable(@Nullable Boolean amendable) {
    this.amendable = amendable;
    return this;
  }

  /**
   * Get amendable
   * @return amendable
   */
  
  @Schema(name = "amendable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amendable")
  public @Nullable Boolean getAmendable() {
    return amendable;
  }

  public void setAmendable(@Nullable Boolean amendable) {
    this.amendable = amendable;
  }

  public ResultsDto arrivalDate(@Nullable String arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Get arrivalDate
   * @return arrivalDate
   */
  
  @Schema(name = "arrivalDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arrivalDate")
  public @Nullable String getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(@Nullable String arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public ResultsDto bartEmployeeId(@Nullable Long bartEmployeeId) {
    this.bartEmployeeId = bartEmployeeId;
    return this;
  }

  /**
   * Get bartEmployeeId
   * @return bartEmployeeId
   */
  
  @Schema(name = "bartEmployeeId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bartEmployeeId")
  public @Nullable Long getBartEmployeeId() {
    return bartEmployeeId;
  }

  public void setBartEmployeeId(@Nullable Long bartEmployeeId) {
    this.bartEmployeeId = bartEmployeeId;
  }

  public ResultsDto bartGuestHistoryNumber(@Nullable String bartGuestHistoryNumber) {
    this.bartGuestHistoryNumber = bartGuestHistoryNumber;
    return this;
  }

  /**
   * Get bartGuestHistoryNumber
   * @return bartGuestHistoryNumber
   */
  
  @Schema(name = "bartGuestHistoryNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bartGuestHistoryNumber")
  public @Nullable String getBartGuestHistoryNumber() {
    return bartGuestHistoryNumber;
  }

  public void setBartGuestHistoryNumber(@Nullable String bartGuestHistoryNumber) {
    this.bartGuestHistoryNumber = bartGuestHistoryNumber;
  }

  public ResultsDto booker(@Nullable BookerDto booker) {
    this.booker = booker;
    return this;
  }

  /**
   * Get booker
   * @return booker
   */
  @Valid 
  @Schema(name = "booker", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("booker")
  public @Nullable BookerDto getBooker() {
    return booker;
  }

  public void setBooker(@Nullable BookerDto booker) {
    this.booker = booker;
  }

  public ResultsDto bookingDate(@Nullable String bookingDate) {
    this.bookingDate = bookingDate;
    return this;
  }

  /**
   * Get bookingDate
   * @return bookingDate
   */
  
  @Schema(name = "bookingDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingDate")
  public @Nullable String getBookingDate() {
    return bookingDate;
  }

  public void setBookingDate(@Nullable String bookingDate) {
    this.bookingDate = bookingDate;
  }

  public ResultsDto bookingMethod(@Nullable String bookingMethod) {
    this.bookingMethod = bookingMethod;
    return this;
  }

  /**
   * Get bookingMethod
   * @return bookingMethod
   */
  
  @Schema(name = "bookingMethod", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingMethod")
  public @Nullable String getBookingMethod() {
    return bookingMethod;
  }

  public void setBookingMethod(@Nullable String bookingMethod) {
    this.bookingMethod = bookingMethod;
  }

  public ResultsDto bookingMethodDescription(@Nullable String bookingMethodDescription) {
    this.bookingMethodDescription = bookingMethodDescription;
    return this;
  }

  /**
   * Get bookingMethodDescription
   * @return bookingMethodDescription
   */
  
  @Schema(name = "bookingMethodDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingMethodDescription")
  public @Nullable String getBookingMethodDescription() {
    return bookingMethodDescription;
  }

  public void setBookingMethodDescription(@Nullable String bookingMethodDescription) {
    this.bookingMethodDescription = bookingMethodDescription;
  }

  public ResultsDto bookingReference(@Nullable String bookingReference) {
    this.bookingReference = bookingReference;
    return this;
  }

  /**
   * Get bookingReference
   * @return bookingReference
   */
  
  @Schema(name = "bookingReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingReference")
  public @Nullable String getBookingReference() {
    return bookingReference;
  }

  public void setBookingReference(@Nullable String bookingReference) {
    this.bookingReference = bookingReference;
  }

  public ResultsDto bookingType(@Nullable String bookingType) {
    this.bookingType = bookingType;
    return this;
  }

  /**
   * Get bookingType
   * @return bookingType
   */
  
  @Schema(name = "bookingType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingType")
  public @Nullable String getBookingType() {
    return bookingType;
  }

  public void setBookingType(@Nullable String bookingType) {
    this.bookingType = bookingType;
  }

  public ResultsDto cancellable(@Nullable Boolean cancellable) {
    this.cancellable = cancellable;
    return this;
  }

  /**
   * Get cancellable
   * @return cancellable
   */
  
  @Schema(name = "cancellable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancellable")
  public @Nullable Boolean getCancellable() {
    return cancellable;
  }

  public void setCancellable(@Nullable Boolean cancellable) {
    this.cancellable = cancellable;
  }

  public ResultsDto cancellationDate(@Nullable String cancellationDate) {
    this.cancellationDate = cancellationDate;
    return this;
  }

  /**
   * Get cancellationDate
   * @return cancellationDate
   */
  
  @Schema(name = "cancellationDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancellationDate")
  public @Nullable String getCancellationDate() {
    return cancellationDate;
  }

  public void setCancellationDate(@Nullable String cancellationDate) {
    this.cancellationDate = cancellationDate;
  }

  public ResultsDto cancellationId(@Nullable String cancellationId) {
    this.cancellationId = cancellationId;
    return this;
  }

  /**
   * Get cancellationId
   * @return cancellationId
   */
  
  @Schema(name = "cancellationId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancellationId")
  public @Nullable String getCancellationId() {
    return cancellationId;
  }

  public void setCancellationId(@Nullable String cancellationId) {
    this.cancellationId = cancellationId;
  }

  public ResultsDto cellCode(@Nullable String cellCode) {
    this.cellCode = cellCode;
    return this;
  }

  /**
   * Get cellCode
   * @return cellCode
   */
  
  @Schema(name = "cellCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cellCode")
  public @Nullable String getCellCode() {
    return cellCode;
  }

  public void setCellCode(@Nullable String cellCode) {
    this.cellCode = cellCode;
  }

  public ResultsDto cellCodeId(@Nullable Integer cellCodeId) {
    this.cellCodeId = cellCodeId;
    return this;
  }

  /**
   * Get cellCodeId
   * @return cellCodeId
   */
  
  @Schema(name = "cellCodeId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cellCodeId")
  public @Nullable Integer getCellCodeId() {
    return cellCodeId;
  }

  public void setCellCodeId(@Nullable Integer cellCodeId) {
    this.cellCodeId = cellCodeId;
  }

  public ResultsDto companyAccountId(@Nullable String companyAccountId) {
    this.companyAccountId = companyAccountId;
    return this;
  }

  /**
   * Get companyAccountId
   * @return companyAccountId
   */
  
  @Schema(name = "companyAccountId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyAccountId")
  public @Nullable String getCompanyAccountId() {
    return companyAccountId;
  }

  public void setCompanyAccountId(@Nullable String companyAccountId) {
    this.companyAccountId = companyAccountId;
  }

  public ResultsDto crmCompanyId(@Nullable String crmCompanyId) {
    this.crmCompanyId = crmCompanyId;
    return this;
  }

  /**
   * Get crmCompanyId
   * @return crmCompanyId
   */
  
  @Schema(name = "crmCompanyId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("crmCompanyId")
  public @Nullable String getCrmCompanyId() {
    return crmCompanyId;
  }

  public void setCrmCompanyId(@Nullable String crmCompanyId) {
    this.crmCompanyId = crmCompanyId;
  }

  public ResultsDto customerAccountId(@Nullable String customerAccountId) {
    this.customerAccountId = customerAccountId;
    return this;
  }

  /**
   * Get customerAccountId
   * @return customerAccountId
   */
  
  @Schema(name = "customerAccountId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerAccountId")
  public @Nullable String getCustomerAccountId() {
    return customerAccountId;
  }

  public void setCustomerAccountId(@Nullable String customerAccountId) {
    this.customerAccountId = customerAccountId;
  }

  public ResultsDto customerReference(@Nullable String customerReference) {
    this.customerReference = customerReference;
    return this;
  }

  /**
   * Get customerReference
   * @return customerReference
   */
  
  @Schema(name = "customerReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerReference")
  public @Nullable String getCustomerReference() {
    return customerReference;
  }

  public void setCustomerReference(@Nullable String customerReference) {
    this.customerReference = customerReference;
  }

  public ResultsDto dctmcUserName(@Nullable String dctmcUserName) {
    this.dctmcUserName = dctmcUserName;
    return this;
  }

  /**
   * Get dctmcUserName
   * @return dctmcUserName
   */
  
  @Schema(name = "dctmcUserName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dctmcUserName")
  public @Nullable String getDctmcUserName() {
    return dctmcUserName;
  }

  public void setDctmcUserName(@Nullable String dctmcUserName) {
    this.dctmcUserName = dctmcUserName;
  }

  public ResultsDto departureDate(@Nullable String departureDate) {
    this.departureDate = departureDate;
    return this;
  }

  /**
   * Get departureDate
   * @return departureDate
   */
  
  @Schema(name = "departureDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("departureDate")
  public @Nullable String getDepartureDate() {
    return departureDate;
  }

  public void setDepartureDate(@Nullable String departureDate) {
    this.departureDate = departureDate;
  }

  public ResultsDto employeeAccountId(@Nullable String employeeAccountId) {
    this.employeeAccountId = employeeAccountId;
    return this;
  }

  /**
   * Get employeeAccountId
   * @return employeeAccountId
   */
  
  @Schema(name = "employeeAccountId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("employeeAccountId")
  public @Nullable String getEmployeeAccountId() {
    return employeeAccountId;
  }

  public void setEmployeeAccountId(@Nullable String employeeAccountId) {
    this.employeeAccountId = employeeAccountId;
  }

  public ResultsDto employeeAnswers(@Nullable EmployeeAnswersDto employeeAnswers) {
    this.employeeAnswers = employeeAnswers;
    return this;
  }

  /**
   * Get employeeAnswers
   * @return employeeAnswers
   */
  @Valid 
  @Schema(name = "employeeAnswers", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("employeeAnswers")
  public @Nullable EmployeeAnswersDto getEmployeeAnswers() {
    return employeeAnswers;
  }

  public void setEmployeeAnswers(@Nullable EmployeeAnswersDto employeeAnswers) {
    this.employeeAnswers = employeeAnswers;
  }

  public ResultsDto globalCompanyId(@Nullable Long globalCompanyId) {
    this.globalCompanyId = globalCompanyId;
    return this;
  }

  /**
   * Get globalCompanyId
   * @return globalCompanyId
   */
  
  @Schema(name = "globalCompanyId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("globalCompanyId")
  public @Nullable Long getGlobalCompanyId() {
    return globalCompanyId;
  }

  public void setGlobalCompanyId(@Nullable Long globalCompanyId) {
    this.globalCompanyId = globalCompanyId;
  }

  public ResultsDto hotelCode(@Nullable String hotelCode) {
    this.hotelCode = hotelCode;
    return this;
  }

  /**
   * Get hotelCode
   * @return hotelCode
   */
  
  @Schema(name = "hotelCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelCode")
  public @Nullable String getHotelCode() {
    return hotelCode;
  }

  public void setHotelCode(@Nullable String hotelCode) {
    this.hotelCode = hotelCode;
  }

  public ResultsDto hotelName(@Nullable String hotelName) {
    this.hotelName = hotelName;
    return this;
  }

  /**
   * Get hotelName
   * @return hotelName
   */
  
  @Schema(name = "hotelName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelName")
  public @Nullable String getHotelName() {
    return hotelName;
  }

  public void setHotelName(@Nullable String hotelName) {
    this.hotelName = hotelName;
  }

  public ResultsDto iataNumber(@Nullable String iataNumber) {
    this.iataNumber = iataNumber;
    return this;
  }

  /**
   * Get iataNumber
   * @return iataNumber
   */
  
  @Schema(name = "iataNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("iataNumber")
  public @Nullable String getIataNumber() {
    return iataNumber;
  }

  public void setIataNumber(@Nullable String iataNumber) {
    this.iataNumber = iataNumber;
  }

  public ResultsDto id(@Nullable String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public @Nullable String getId() {
    return id;
  }

  public void setId(@Nullable String id) {
    this.id = id;
  }

  public ResultsDto _package(@Nullable Boolean _package) {
    this._package = _package;
    return this;
  }

  /**
   * Get _package
   * @return _package
   */
  
  @Schema(name = "package", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("package")
  public @Nullable Boolean getPackage() {
    return _package;
  }

  public void setPackage(@Nullable Boolean _package) {
    this._package = _package;
  }

  public ResultsDto partitionKey(@Nullable String partitionKey) {
    this.partitionKey = partitionKey;
    return this;
  }

  /**
   * Get partitionKey
   * @return partitionKey
   */
  
  @Schema(name = "partitionKey", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("partitionKey")
  public @Nullable String getPartitionKey() {
    return partitionKey;
  }

  public void setPartitionKey(@Nullable String partitionKey) {
    this.partitionKey = partitionKey;
  }

  public ResultsDto paymentCard(@Nullable PaymentCardDto paymentCard) {
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
  public @Nullable PaymentCardDto getPaymentCard() {
    return paymentCard;
  }

  public void setPaymentCard(@Nullable PaymentCardDto paymentCard) {
    this.paymentCard = paymentCard;
  }

  public ResultsDto prePaid(@Nullable Boolean prePaid) {
    this.prePaid = prePaid;
    return this;
  }

  /**
   * Get prePaid
   * @return prePaid
   */
  
  @Schema(name = "prePaid", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("prePaid")
  public @Nullable Boolean getPrePaid() {
    return prePaid;
  }

  public void setPrePaid(@Nullable Boolean prePaid) {
    this.prePaid = prePaid;
  }

  public ResultsDto prePaidAmount(@Nullable PriceDto prePaidAmount) {
    this.prePaidAmount = prePaidAmount;
    return this;
  }

  /**
   * Get prePaidAmount
   * @return prePaidAmount
   */
  @Valid 
  @Schema(name = "prePaidAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("prePaidAmount")
  public @Nullable PriceDto getPrePaidAmount() {
    return prePaidAmount;
  }

  public void setPrePaidAmount(@Nullable PriceDto prePaidAmount) {
    this.prePaidAmount = prePaidAmount;
  }

  public ResultsDto purchaseOrder(@Nullable String purchaseOrder) {
    this.purchaseOrder = purchaseOrder;
    return this;
  }

  /**
   * Get purchaseOrder
   * @return purchaseOrder
   */
  
  @Schema(name = "purchaseOrder", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("purchaseOrder")
  public @Nullable String getPurchaseOrder() {
    return purchaseOrder;
  }

  public void setPurchaseOrder(@Nullable String purchaseOrder) {
    this.purchaseOrder = purchaseOrder;
  }

  public ResultsDto rateCategory(@Nullable String rateCategory) {
    this.rateCategory = rateCategory;
    return this;
  }

  /**
   * Get rateCategory
   * @return rateCategory
   */
  
  @Schema(name = "rateCategory", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateCategory")
  public @Nullable String getRateCategory() {
    return rateCategory;
  }

  public void setRateCategory(@Nullable String rateCategory) {
    this.rateCategory = rateCategory;
  }

  public ResultsDto rateCategoryCode(@Nullable String rateCategoryCode) {
    this.rateCategoryCode = rateCategoryCode;
    return this;
  }

  /**
   * Get rateCategoryCode
   * @return rateCategoryCode
   */
  
  @Schema(name = "rateCategoryCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateCategoryCode")
  public @Nullable String getRateCategoryCode() {
    return rateCategoryCode;
  }

  public void setRateCategoryCode(@Nullable String rateCategoryCode) {
    this.rateCategoryCode = rateCategoryCode;
  }

  public ResultsDto rateClass(@Nullable String rateClass) {
    this.rateClass = rateClass;
    return this;
  }

  /**
   * Get rateClass
   * @return rateClass
   */
  
  @Schema(name = "rateClass", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateClass")
  public @Nullable String getRateClass() {
    return rateClass;
  }

  public void setRateClass(@Nullable String rateClass) {
    this.rateClass = rateClass;
  }

  public ResultsDto ratePlan(@Nullable String ratePlan) {
    this.ratePlan = ratePlan;
    return this;
  }

  /**
   * Get ratePlan
   * @return ratePlan
   */
  
  @Schema(name = "ratePlan", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlan")
  public @Nullable String getRatePlan() {
    return ratePlan;
  }

  public void setRatePlan(@Nullable String ratePlan) {
    this.ratePlan = ratePlan;
  }

  public ResultsDto rooms(List<@Valid RoomsDto> rooms) {
    this.rooms = rooms;
    return this;
  }

  public ResultsDto addRoomsItem(RoomsDto roomsItem) {
    if (this.rooms == null) {
      this.rooms = new ArrayList<>();
    }
    this.rooms.add(roomsItem);
    return this;
  }

  /**
   * Get rooms
   * @return rooms
   */
  @Valid 
  @Schema(name = "rooms", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rooms")
  public List<@Valid RoomsDto> getRooms() {
    return rooms;
  }

  public void setRooms(List<@Valid RoomsDto> rooms) {
    this.rooms = rooms;
  }

  public ResultsDto sourceSystem(@Nullable String sourceSystem) {
    this.sourceSystem = sourceSystem;
    return this;
  }

  /**
   * Get sourceSystem
   * @return sourceSystem
   */
  
  @Schema(name = "sourceSystem", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sourceSystem")
  public @Nullable String getSourceSystem() {
    return sourceSystem;
  }

  public void setSourceSystem(@Nullable String sourceSystem) {
    this.sourceSystem = sourceSystem;
  }

  public ResultsDto status(@Nullable String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public @Nullable String getStatus() {
    return status;
  }

  public void setStatus(@Nullable String status) {
    this.status = status;
  }

  public ResultsDto statusSortOrder(@Nullable String statusSortOrder) {
    this.statusSortOrder = statusSortOrder;
    return this;
  }

  /**
   * Get statusSortOrder
   * @return statusSortOrder
   */
  
  @Schema(name = "statusSortOrder", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("statusSortOrder")
  public @Nullable String getStatusSortOrder() {
    return statusSortOrder;
  }

  public void setStatusSortOrder(@Nullable String statusSortOrder) {
    this.statusSortOrder = statusSortOrder;
  }

  public ResultsDto thirdPartyReference(@Nullable String thirdPartyReference) {
    this.thirdPartyReference = thirdPartyReference;
    return this;
  }

  /**
   * Get thirdPartyReference
   * @return thirdPartyReference
   */
  
  @Schema(name = "thirdPartyReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("thirdPartyReference")
  public @Nullable String getThirdPartyReference() {
    return thirdPartyReference;
  }

  public void setThirdPartyReference(@Nullable String thirdPartyReference) {
    this.thirdPartyReference = thirdPartyReference;
  }

  public ResultsDto totalCost(@Nullable PriceDto totalCost) {
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
  public @Nullable PriceDto getTotalCost() {
    return totalCost;
  }

  public void setTotalCost(@Nullable PriceDto totalCost) {
    this.totalCost = totalCost;
  }

  public ResultsDto upsellTotalCost(@Nullable PriceDto upsellTotalCost) {
    this.upsellTotalCost = upsellTotalCost;
    return this;
  }

  /**
   * Get upsellTotalCost
   * @return upsellTotalCost
   */
  @Valid 
  @Schema(name = "upsellTotalCost", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("upsellTotalCost")
  public @Nullable PriceDto getUpsellTotalCost() {
    return upsellTotalCost;
  }

  public void setUpsellTotalCost(@Nullable PriceDto upsellTotalCost) {
    this.upsellTotalCost = upsellTotalCost;
  }

  public ResultsDto walkIn(@Nullable Boolean walkIn) {
    this.walkIn = walkIn;
    return this;
  }

  /**
   * Get walkIn
   * @return walkIn
   */
  
  @Schema(name = "walkIn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("walkIn")
  public @Nullable Boolean getWalkIn() {
    return walkIn;
  }

  public void setWalkIn(@Nullable Boolean walkIn) {
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
    ResultsDto resultsDto = (ResultsDto) o;
    return Objects.equals(this.accountType, resultsDto.accountType) &&
        Objects.equals(this.activeTotalCost, resultsDto.activeTotalCost) &&
        Objects.equals(this.amendable, resultsDto.amendable) &&
        Objects.equals(this.arrivalDate, resultsDto.arrivalDate) &&
        Objects.equals(this.bartEmployeeId, resultsDto.bartEmployeeId) &&
        Objects.equals(this.bartGuestHistoryNumber, resultsDto.bartGuestHistoryNumber) &&
        Objects.equals(this.booker, resultsDto.booker) &&
        Objects.equals(this.bookingDate, resultsDto.bookingDate) &&
        Objects.equals(this.bookingMethod, resultsDto.bookingMethod) &&
        Objects.equals(this.bookingMethodDescription, resultsDto.bookingMethodDescription) &&
        Objects.equals(this.bookingReference, resultsDto.bookingReference) &&
        Objects.equals(this.bookingType, resultsDto.bookingType) &&
        Objects.equals(this.cancellable, resultsDto.cancellable) &&
        Objects.equals(this.cancellationDate, resultsDto.cancellationDate) &&
        Objects.equals(this.cancellationId, resultsDto.cancellationId) &&
        Objects.equals(this.cellCode, resultsDto.cellCode) &&
        Objects.equals(this.cellCodeId, resultsDto.cellCodeId) &&
        Objects.equals(this.companyAccountId, resultsDto.companyAccountId) &&
        Objects.equals(this.crmCompanyId, resultsDto.crmCompanyId) &&
        Objects.equals(this.customerAccountId, resultsDto.customerAccountId) &&
        Objects.equals(this.customerReference, resultsDto.customerReference) &&
        Objects.equals(this.dctmcUserName, resultsDto.dctmcUserName) &&
        Objects.equals(this.departureDate, resultsDto.departureDate) &&
        Objects.equals(this.employeeAccountId, resultsDto.employeeAccountId) &&
        Objects.equals(this.employeeAnswers, resultsDto.employeeAnswers) &&
        Objects.equals(this.globalCompanyId, resultsDto.globalCompanyId) &&
        Objects.equals(this.hotelCode, resultsDto.hotelCode) &&
        Objects.equals(this.hotelName, resultsDto.hotelName) &&
        Objects.equals(this.iataNumber, resultsDto.iataNumber) &&
        Objects.equals(this.id, resultsDto.id) &&
        Objects.equals(this._package, resultsDto._package) &&
        Objects.equals(this.partitionKey, resultsDto.partitionKey) &&
        Objects.equals(this.paymentCard, resultsDto.paymentCard) &&
        Objects.equals(this.prePaid, resultsDto.prePaid) &&
        Objects.equals(this.prePaidAmount, resultsDto.prePaidAmount) &&
        Objects.equals(this.purchaseOrder, resultsDto.purchaseOrder) &&
        Objects.equals(this.rateCategory, resultsDto.rateCategory) &&
        Objects.equals(this.rateCategoryCode, resultsDto.rateCategoryCode) &&
        Objects.equals(this.rateClass, resultsDto.rateClass) &&
        Objects.equals(this.ratePlan, resultsDto.ratePlan) &&
        Objects.equals(this.rooms, resultsDto.rooms) &&
        Objects.equals(this.sourceSystem, resultsDto.sourceSystem) &&
        Objects.equals(this.status, resultsDto.status) &&
        Objects.equals(this.statusSortOrder, resultsDto.statusSortOrder) &&
        Objects.equals(this.thirdPartyReference, resultsDto.thirdPartyReference) &&
        Objects.equals(this.totalCost, resultsDto.totalCost) &&
        Objects.equals(this.upsellTotalCost, resultsDto.upsellTotalCost) &&
        Objects.equals(this.walkIn, resultsDto.walkIn);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountType, activeTotalCost, amendable, arrivalDate, bartEmployeeId, bartGuestHistoryNumber, booker, bookingDate, bookingMethod, bookingMethodDescription, bookingReference, bookingType, cancellable, cancellationDate, cancellationId, cellCode, cellCodeId, companyAccountId, crmCompanyId, customerAccountId, customerReference, dctmcUserName, departureDate, employeeAccountId, employeeAnswers, globalCompanyId, hotelCode, hotelName, iataNumber, id, _package, partitionKey, paymentCard, prePaid, prePaidAmount, purchaseOrder, rateCategory, rateCategoryCode, rateClass, ratePlan, rooms, sourceSystem, status, statusSortOrder, thirdPartyReference, totalCost, upsellTotalCost, walkIn);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResultsDto {\n");
    sb.append("    accountType: ").append(toIndentedString(accountType)).append("\n");
    sb.append("    activeTotalCost: ").append(toIndentedString(activeTotalCost)).append("\n");
    sb.append("    amendable: ").append(toIndentedString(amendable)).append("\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    bartEmployeeId: ").append(toIndentedString(bartEmployeeId)).append("\n");
    sb.append("    bartGuestHistoryNumber: ").append(toIndentedString(bartGuestHistoryNumber)).append("\n");
    sb.append("    booker: ").append(toIndentedString(booker)).append("\n");
    sb.append("    bookingDate: ").append(toIndentedString(bookingDate)).append("\n");
    sb.append("    bookingMethod: ").append(toIndentedString(bookingMethod)).append("\n");
    sb.append("    bookingMethodDescription: ").append(toIndentedString(bookingMethodDescription)).append("\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
    sb.append("    bookingType: ").append(toIndentedString(bookingType)).append("\n");
    sb.append("    cancellable: ").append(toIndentedString(cancellable)).append("\n");
    sb.append("    cancellationDate: ").append(toIndentedString(cancellationDate)).append("\n");
    sb.append("    cancellationId: ").append(toIndentedString(cancellationId)).append("\n");
    sb.append("    cellCode: ").append(toIndentedString(cellCode)).append("\n");
    sb.append("    cellCodeId: ").append(toIndentedString(cellCodeId)).append("\n");
    sb.append("    companyAccountId: ").append(toIndentedString(companyAccountId)).append("\n");
    sb.append("    crmCompanyId: ").append(toIndentedString(crmCompanyId)).append("\n");
    sb.append("    customerAccountId: ").append(toIndentedString(customerAccountId)).append("\n");
    sb.append("    customerReference: ").append(toIndentedString(customerReference)).append("\n");
    sb.append("    dctmcUserName: ").append(toIndentedString(dctmcUserName)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    employeeAccountId: ").append(toIndentedString(employeeAccountId)).append("\n");
    sb.append("    employeeAnswers: ").append(toIndentedString(employeeAnswers)).append("\n");
    sb.append("    globalCompanyId: ").append(toIndentedString(globalCompanyId)).append("\n");
    sb.append("    hotelCode: ").append(toIndentedString(hotelCode)).append("\n");
    sb.append("    hotelName: ").append(toIndentedString(hotelName)).append("\n");
    sb.append("    iataNumber: ").append(toIndentedString(iataNumber)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    _package: ").append(toIndentedString(_package)).append("\n");
    sb.append("    partitionKey: ").append(toIndentedString(partitionKey)).append("\n");
    sb.append("    paymentCard: ").append(toIndentedString(paymentCard)).append("\n");
    sb.append("    prePaid: ").append(toIndentedString(prePaid)).append("\n");
    sb.append("    prePaidAmount: ").append(toIndentedString(prePaidAmount)).append("\n");
    sb.append("    purchaseOrder: ").append(toIndentedString(purchaseOrder)).append("\n");
    sb.append("    rateCategory: ").append(toIndentedString(rateCategory)).append("\n");
    sb.append("    rateCategoryCode: ").append(toIndentedString(rateCategoryCode)).append("\n");
    sb.append("    rateClass: ").append(toIndentedString(rateClass)).append("\n");
    sb.append("    ratePlan: ").append(toIndentedString(ratePlan)).append("\n");
    sb.append("    rooms: ").append(toIndentedString(rooms)).append("\n");
    sb.append("    sourceSystem: ").append(toIndentedString(sourceSystem)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    statusSortOrder: ").append(toIndentedString(statusSortOrder)).append("\n");
    sb.append("    thirdPartyReference: ").append(toIndentedString(thirdPartyReference)).append("\n");
    sb.append("    totalCost: ").append(toIndentedString(totalCost)).append("\n");
    sb.append("    upsellTotalCost: ").append(toIndentedString(upsellTotalCost)).append("\n");
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

