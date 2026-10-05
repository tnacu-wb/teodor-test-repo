package uk.co.whitbread.hotel.cdh.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.cdh.adapter.generated.models.BookerDto;
import uk.co.whitbread.hotel.cdh.adapter.generated.models.EmployeeAnswersDto;
import uk.co.whitbread.hotel.cdh.adapter.generated.models.PaymentCardDto;
import uk.co.whitbread.hotel.cdh.adapter.generated.models.PriceDto;
import uk.co.whitbread.hotel.cdh.adapter.generated.models.RoomsDto;
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

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:35.320601+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
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

  public ResultsDto accountType(String accountType) {
    this.accountType = accountType;
    return this;
  }

  /**
   * Get accountType
   * @return accountType
   */
  
  @Schema(name = "accountType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accountType")
  public String getAccountType() {
    return accountType;
  }

  public void setAccountType(String accountType) {
    this.accountType = accountType;
  }

  public ResultsDto activeTotalCost(PriceDto activeTotalCost) {
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
  public PriceDto getActiveTotalCost() {
    return activeTotalCost;
  }

  public void setActiveTotalCost(PriceDto activeTotalCost) {
    this.activeTotalCost = activeTotalCost;
  }

  public ResultsDto amendable(Boolean amendable) {
    this.amendable = amendable;
    return this;
  }

  /**
   * Get amendable
   * @return amendable
   */
  
  @Schema(name = "amendable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amendable")
  public Boolean getAmendable() {
    return amendable;
  }

  public void setAmendable(Boolean amendable) {
    this.amendable = amendable;
  }

  public ResultsDto arrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Get arrivalDate
   * @return arrivalDate
   */
  
  @Schema(name = "arrivalDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arrivalDate")
  public String getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public ResultsDto bartEmployeeId(Long bartEmployeeId) {
    this.bartEmployeeId = bartEmployeeId;
    return this;
  }

  /**
   * Get bartEmployeeId
   * @return bartEmployeeId
   */
  
  @Schema(name = "bartEmployeeId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bartEmployeeId")
  public Long getBartEmployeeId() {
    return bartEmployeeId;
  }

  public void setBartEmployeeId(Long bartEmployeeId) {
    this.bartEmployeeId = bartEmployeeId;
  }

  public ResultsDto bartGuestHistoryNumber(String bartGuestHistoryNumber) {
    this.bartGuestHistoryNumber = bartGuestHistoryNumber;
    return this;
  }

  /**
   * Get bartGuestHistoryNumber
   * @return bartGuestHistoryNumber
   */
  
  @Schema(name = "bartGuestHistoryNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bartGuestHistoryNumber")
  public String getBartGuestHistoryNumber() {
    return bartGuestHistoryNumber;
  }

  public void setBartGuestHistoryNumber(String bartGuestHistoryNumber) {
    this.bartGuestHistoryNumber = bartGuestHistoryNumber;
  }

  public ResultsDto booker(BookerDto booker) {
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
  public BookerDto getBooker() {
    return booker;
  }

  public void setBooker(BookerDto booker) {
    this.booker = booker;
  }

  public ResultsDto bookingDate(String bookingDate) {
    this.bookingDate = bookingDate;
    return this;
  }

  /**
   * Get bookingDate
   * @return bookingDate
   */
  
  @Schema(name = "bookingDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingDate")
  public String getBookingDate() {
    return bookingDate;
  }

  public void setBookingDate(String bookingDate) {
    this.bookingDate = bookingDate;
  }

  public ResultsDto bookingMethod(String bookingMethod) {
    this.bookingMethod = bookingMethod;
    return this;
  }

  /**
   * Get bookingMethod
   * @return bookingMethod
   */
  
  @Schema(name = "bookingMethod", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingMethod")
  public String getBookingMethod() {
    return bookingMethod;
  }

  public void setBookingMethod(String bookingMethod) {
    this.bookingMethod = bookingMethod;
  }

  public ResultsDto bookingMethodDescription(String bookingMethodDescription) {
    this.bookingMethodDescription = bookingMethodDescription;
    return this;
  }

  /**
   * Get bookingMethodDescription
   * @return bookingMethodDescription
   */
  
  @Schema(name = "bookingMethodDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingMethodDescription")
  public String getBookingMethodDescription() {
    return bookingMethodDescription;
  }

  public void setBookingMethodDescription(String bookingMethodDescription) {
    this.bookingMethodDescription = bookingMethodDescription;
  }

  public ResultsDto bookingReference(String bookingReference) {
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

  public ResultsDto bookingType(String bookingType) {
    this.bookingType = bookingType;
    return this;
  }

  /**
   * Get bookingType
   * @return bookingType
   */
  
  @Schema(name = "bookingType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingType")
  public String getBookingType() {
    return bookingType;
  }

  public void setBookingType(String bookingType) {
    this.bookingType = bookingType;
  }

  public ResultsDto cancellable(Boolean cancellable) {
    this.cancellable = cancellable;
    return this;
  }

  /**
   * Get cancellable
   * @return cancellable
   */
  
  @Schema(name = "cancellable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancellable")
  public Boolean getCancellable() {
    return cancellable;
  }

  public void setCancellable(Boolean cancellable) {
    this.cancellable = cancellable;
  }

  public ResultsDto cancellationDate(String cancellationDate) {
    this.cancellationDate = cancellationDate;
    return this;
  }

  /**
   * Get cancellationDate
   * @return cancellationDate
   */
  
  @Schema(name = "cancellationDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancellationDate")
  public String getCancellationDate() {
    return cancellationDate;
  }

  public void setCancellationDate(String cancellationDate) {
    this.cancellationDate = cancellationDate;
  }

  public ResultsDto cancellationId(String cancellationId) {
    this.cancellationId = cancellationId;
    return this;
  }

  /**
   * Get cancellationId
   * @return cancellationId
   */
  
  @Schema(name = "cancellationId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancellationId")
  public String getCancellationId() {
    return cancellationId;
  }

  public void setCancellationId(String cancellationId) {
    this.cancellationId = cancellationId;
  }

  public ResultsDto cellCode(String cellCode) {
    this.cellCode = cellCode;
    return this;
  }

  /**
   * Get cellCode
   * @return cellCode
   */
  
  @Schema(name = "cellCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cellCode")
  public String getCellCode() {
    return cellCode;
  }

  public void setCellCode(String cellCode) {
    this.cellCode = cellCode;
  }

  public ResultsDto cellCodeId(Integer cellCodeId) {
    this.cellCodeId = cellCodeId;
    return this;
  }

  /**
   * Get cellCodeId
   * @return cellCodeId
   */
  
  @Schema(name = "cellCodeId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cellCodeId")
  public Integer getCellCodeId() {
    return cellCodeId;
  }

  public void setCellCodeId(Integer cellCodeId) {
    this.cellCodeId = cellCodeId;
  }

  public ResultsDto companyAccountId(String companyAccountId) {
    this.companyAccountId = companyAccountId;
    return this;
  }

  /**
   * Get companyAccountId
   * @return companyAccountId
   */
  
  @Schema(name = "companyAccountId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyAccountId")
  public String getCompanyAccountId() {
    return companyAccountId;
  }

  public void setCompanyAccountId(String companyAccountId) {
    this.companyAccountId = companyAccountId;
  }

  public ResultsDto crmCompanyId(String crmCompanyId) {
    this.crmCompanyId = crmCompanyId;
    return this;
  }

  /**
   * Get crmCompanyId
   * @return crmCompanyId
   */
  
  @Schema(name = "crmCompanyId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("crmCompanyId")
  public String getCrmCompanyId() {
    return crmCompanyId;
  }

  public void setCrmCompanyId(String crmCompanyId) {
    this.crmCompanyId = crmCompanyId;
  }

  public ResultsDto customerAccountId(String customerAccountId) {
    this.customerAccountId = customerAccountId;
    return this;
  }

  /**
   * Get customerAccountId
   * @return customerAccountId
   */
  
  @Schema(name = "customerAccountId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerAccountId")
  public String getCustomerAccountId() {
    return customerAccountId;
  }

  public void setCustomerAccountId(String customerAccountId) {
    this.customerAccountId = customerAccountId;
  }

  public ResultsDto customerReference(String customerReference) {
    this.customerReference = customerReference;
    return this;
  }

  /**
   * Get customerReference
   * @return customerReference
   */
  
  @Schema(name = "customerReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerReference")
  public String getCustomerReference() {
    return customerReference;
  }

  public void setCustomerReference(String customerReference) {
    this.customerReference = customerReference;
  }

  public ResultsDto dctmcUserName(String dctmcUserName) {
    this.dctmcUserName = dctmcUserName;
    return this;
  }

  /**
   * Get dctmcUserName
   * @return dctmcUserName
   */
  
  @Schema(name = "dctmcUserName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dctmcUserName")
  public String getDctmcUserName() {
    return dctmcUserName;
  }

  public void setDctmcUserName(String dctmcUserName) {
    this.dctmcUserName = dctmcUserName;
  }

  public ResultsDto departureDate(String departureDate) {
    this.departureDate = departureDate;
    return this;
  }

  /**
   * Get departureDate
   * @return departureDate
   */
  
  @Schema(name = "departureDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("departureDate")
  public String getDepartureDate() {
    return departureDate;
  }

  public void setDepartureDate(String departureDate) {
    this.departureDate = departureDate;
  }

  public ResultsDto employeeAccountId(String employeeAccountId) {
    this.employeeAccountId = employeeAccountId;
    return this;
  }

  /**
   * Get employeeAccountId
   * @return employeeAccountId
   */
  
  @Schema(name = "employeeAccountId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("employeeAccountId")
  public String getEmployeeAccountId() {
    return employeeAccountId;
  }

  public void setEmployeeAccountId(String employeeAccountId) {
    this.employeeAccountId = employeeAccountId;
  }

  public ResultsDto employeeAnswers(EmployeeAnswersDto employeeAnswers) {
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
  public EmployeeAnswersDto getEmployeeAnswers() {
    return employeeAnswers;
  }

  public void setEmployeeAnswers(EmployeeAnswersDto employeeAnswers) {
    this.employeeAnswers = employeeAnswers;
  }

  public ResultsDto globalCompanyId(Long globalCompanyId) {
    this.globalCompanyId = globalCompanyId;
    return this;
  }

  /**
   * Get globalCompanyId
   * @return globalCompanyId
   */
  
  @Schema(name = "globalCompanyId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("globalCompanyId")
  public Long getGlobalCompanyId() {
    return globalCompanyId;
  }

  public void setGlobalCompanyId(Long globalCompanyId) {
    this.globalCompanyId = globalCompanyId;
  }

  public ResultsDto hotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
    return this;
  }

  /**
   * Get hotelCode
   * @return hotelCode
   */
  
  @Schema(name = "hotelCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelCode")
  public String getHotelCode() {
    return hotelCode;
  }

  public void setHotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
  }

  public ResultsDto hotelName(String hotelName) {
    this.hotelName = hotelName;
    return this;
  }

  /**
   * Get hotelName
   * @return hotelName
   */
  
  @Schema(name = "hotelName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelName")
  public String getHotelName() {
    return hotelName;
  }

  public void setHotelName(String hotelName) {
    this.hotelName = hotelName;
  }

  public ResultsDto iataNumber(String iataNumber) {
    this.iataNumber = iataNumber;
    return this;
  }

  /**
   * Get iataNumber
   * @return iataNumber
   */
  
  @Schema(name = "iataNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("iataNumber")
  public String getIataNumber() {
    return iataNumber;
  }

  public void setIataNumber(String iataNumber) {
    this.iataNumber = iataNumber;
  }

  public ResultsDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  
  @Schema(name = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ResultsDto _package(Boolean _package) {
    this._package = _package;
    return this;
  }

  /**
   * Get _package
   * @return _package
   */
  
  @Schema(name = "package", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("package")
  public Boolean getPackage() {
    return _package;
  }

  public void setPackage(Boolean _package) {
    this._package = _package;
  }

  public ResultsDto partitionKey(String partitionKey) {
    this.partitionKey = partitionKey;
    return this;
  }

  /**
   * Get partitionKey
   * @return partitionKey
   */
  
  @Schema(name = "partitionKey", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("partitionKey")
  public String getPartitionKey() {
    return partitionKey;
  }

  public void setPartitionKey(String partitionKey) {
    this.partitionKey = partitionKey;
  }

  public ResultsDto paymentCard(PaymentCardDto paymentCard) {
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
  public PaymentCardDto getPaymentCard() {
    return paymentCard;
  }

  public void setPaymentCard(PaymentCardDto paymentCard) {
    this.paymentCard = paymentCard;
  }

  public ResultsDto prePaid(Boolean prePaid) {
    this.prePaid = prePaid;
    return this;
  }

  /**
   * Get prePaid
   * @return prePaid
   */
  
  @Schema(name = "prePaid", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("prePaid")
  public Boolean getPrePaid() {
    return prePaid;
  }

  public void setPrePaid(Boolean prePaid) {
    this.prePaid = prePaid;
  }

  public ResultsDto prePaidAmount(PriceDto prePaidAmount) {
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
  public PriceDto getPrePaidAmount() {
    return prePaidAmount;
  }

  public void setPrePaidAmount(PriceDto prePaidAmount) {
    this.prePaidAmount = prePaidAmount;
  }

  public ResultsDto purchaseOrder(String purchaseOrder) {
    this.purchaseOrder = purchaseOrder;
    return this;
  }

  /**
   * Get purchaseOrder
   * @return purchaseOrder
   */
  
  @Schema(name = "purchaseOrder", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("purchaseOrder")
  public String getPurchaseOrder() {
    return purchaseOrder;
  }

  public void setPurchaseOrder(String purchaseOrder) {
    this.purchaseOrder = purchaseOrder;
  }

  public ResultsDto rateCategory(String rateCategory) {
    this.rateCategory = rateCategory;
    return this;
  }

  /**
   * Get rateCategory
   * @return rateCategory
   */
  
  @Schema(name = "rateCategory", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateCategory")
  public String getRateCategory() {
    return rateCategory;
  }

  public void setRateCategory(String rateCategory) {
    this.rateCategory = rateCategory;
  }

  public ResultsDto rateCategoryCode(String rateCategoryCode) {
    this.rateCategoryCode = rateCategoryCode;
    return this;
  }

  /**
   * Get rateCategoryCode
   * @return rateCategoryCode
   */
  
  @Schema(name = "rateCategoryCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateCategoryCode")
  public String getRateCategoryCode() {
    return rateCategoryCode;
  }

  public void setRateCategoryCode(String rateCategoryCode) {
    this.rateCategoryCode = rateCategoryCode;
  }

  public ResultsDto rateClass(String rateClass) {
    this.rateClass = rateClass;
    return this;
  }

  /**
   * Get rateClass
   * @return rateClass
   */
  
  @Schema(name = "rateClass", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateClass")
  public String getRateClass() {
    return rateClass;
  }

  public void setRateClass(String rateClass) {
    this.rateClass = rateClass;
  }

  public ResultsDto ratePlan(String ratePlan) {
    this.ratePlan = ratePlan;
    return this;
  }

  /**
   * Get ratePlan
   * @return ratePlan
   */
  
  @Schema(name = "ratePlan", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlan")
  public String getRatePlan() {
    return ratePlan;
  }

  public void setRatePlan(String ratePlan) {
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

  public ResultsDto sourceSystem(String sourceSystem) {
    this.sourceSystem = sourceSystem;
    return this;
  }

  /**
   * Get sourceSystem
   * @return sourceSystem
   */
  
  @Schema(name = "sourceSystem", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sourceSystem")
  public String getSourceSystem() {
    return sourceSystem;
  }

  public void setSourceSystem(String sourceSystem) {
    this.sourceSystem = sourceSystem;
  }

  public ResultsDto status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public ResultsDto statusSortOrder(String statusSortOrder) {
    this.statusSortOrder = statusSortOrder;
    return this;
  }

  /**
   * Get statusSortOrder
   * @return statusSortOrder
   */
  
  @Schema(name = "statusSortOrder", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("statusSortOrder")
  public String getStatusSortOrder() {
    return statusSortOrder;
  }

  public void setStatusSortOrder(String statusSortOrder) {
    this.statusSortOrder = statusSortOrder;
  }

  public ResultsDto thirdPartyReference(String thirdPartyReference) {
    this.thirdPartyReference = thirdPartyReference;
    return this;
  }

  /**
   * Get thirdPartyReference
   * @return thirdPartyReference
   */
  
  @Schema(name = "thirdPartyReference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("thirdPartyReference")
  public String getThirdPartyReference() {
    return thirdPartyReference;
  }

  public void setThirdPartyReference(String thirdPartyReference) {
    this.thirdPartyReference = thirdPartyReference;
  }

  public ResultsDto totalCost(PriceDto totalCost) {
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
  public PriceDto getTotalCost() {
    return totalCost;
  }

  public void setTotalCost(PriceDto totalCost) {
    this.totalCost = totalCost;
  }

  public ResultsDto upsellTotalCost(PriceDto upsellTotalCost) {
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
  public PriceDto getUpsellTotalCost() {
    return upsellTotalCost;
  }

  public void setUpsellTotalCost(PriceDto upsellTotalCost) {
    this.upsellTotalCost = upsellTotalCost;
  }

  public ResultsDto walkIn(Boolean walkIn) {
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

