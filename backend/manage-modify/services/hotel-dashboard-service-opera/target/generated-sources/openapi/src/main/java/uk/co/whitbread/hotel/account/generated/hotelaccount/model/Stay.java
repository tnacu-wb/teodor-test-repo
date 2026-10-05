package uk.co.whitbread.hotel.account.generated.hotelaccount.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.Price;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.RoomTypes;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Stay
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:33.863804+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Stay {

  private @Nullable Boolean amendable;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate arrivalDate;

  private @Nullable String bookedBy;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate bookingDate;

  private @Nullable Price bookingFee;

  /**
   * Gets or Sets bookingStatus
   */
  public enum BookingStatusEnum {
    FUTURE("FUTURE"),
    
    PAST("PAST"),
    
    CANCELLED("CANCELLED");

    private String value;

    BookingStatusEnum(String value) {
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
    public static BookingStatusEnum fromValue(String value) {
      for (BookingStatusEnum b : BookingStatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable BookingStatusEnum bookingStatus;

  private @Nullable Boolean cancelable;

  private @Nullable String cancellationId;

  private @Nullable Boolean cancelled;

  private @Nullable Boolean carDataRequired;

  private @Nullable String cellCodeLegend;

  private @Nullable String checkInDate;

  private @Nullable Boolean checkInOnline;

  private @Nullable Boolean checkedIn;

  private @Nullable Price cityTax;

  private @Nullable String confirmationNumber;

  private @Nullable String customerReference;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate departureDate;

  private @Nullable String frequentBooking;

  private @Nullable String guestHistoryNumber;

  private @Nullable Integer historyRecordNumber;

  private String hotelCode;

  private @Nullable String hotelName;

  private @Nullable String leadGuest;

  private @Nullable String leadGuestSurname;

  private @Nullable Boolean mpibooking;

  private @Nullable Integer noOfRooms;

  private @Nullable Price outstandingAmount;

  private @Nullable String paymentStatus;

  private @Nullable Price prePaidAmount;

  private @Nullable String promotionText;

  private @Nullable String purchaseOrder;

  private @Nullable String rateClass;

  private @Nullable String rateName;

  @Valid
  private List<@Valid RoomTypes> roomTypes = new ArrayList<>();

  private @Nullable Price totalCost;

  public Stay() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Stay(LocalDate arrivalDate, LocalDate departureDate, String hotelCode) {
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
    this.hotelCode = hotelCode;
  }

  public Stay amendable(Boolean amendable) {
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

  public Stay arrivalDate(LocalDate arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Get arrivalDate
   * @return arrivalDate
   */
  @NotNull @Valid 
  @Schema(name = "arrivalDate", example = "2015-10-20", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("arrivalDate")
  public LocalDate getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(LocalDate arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public Stay bookedBy(String bookedBy) {
    this.bookedBy = bookedBy;
    return this;
  }

  /**
   * Get bookedBy
   * @return bookedBy
   */
  
  @Schema(name = "bookedBy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookedBy")
  public String getBookedBy() {
    return bookedBy;
  }

  public void setBookedBy(String bookedBy) {
    this.bookedBy = bookedBy;
  }

  public Stay bookingDate(LocalDate bookingDate) {
    this.bookingDate = bookingDate;
    return this;
  }

  /**
   * Get bookingDate
   * @return bookingDate
   */
  @Valid 
  @Schema(name = "bookingDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingDate")
  public LocalDate getBookingDate() {
    return bookingDate;
  }

  public void setBookingDate(LocalDate bookingDate) {
    this.bookingDate = bookingDate;
  }

  public Stay bookingFee(Price bookingFee) {
    this.bookingFee = bookingFee;
    return this;
  }

  /**
   * Get bookingFee
   * @return bookingFee
   */
  @Valid 
  @Schema(name = "bookingFee", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingFee")
  public Price getBookingFee() {
    return bookingFee;
  }

  public void setBookingFee(Price bookingFee) {
    this.bookingFee = bookingFee;
  }

  public Stay bookingStatus(BookingStatusEnum bookingStatus) {
    this.bookingStatus = bookingStatus;
    return this;
  }

  /**
   * Get bookingStatus
   * @return bookingStatus
   */
  
  @Schema(name = "bookingStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingStatus")
  public BookingStatusEnum getBookingStatus() {
    return bookingStatus;
  }

  public void setBookingStatus(BookingStatusEnum bookingStatus) {
    this.bookingStatus = bookingStatus;
  }

  public Stay cancelable(Boolean cancelable) {
    this.cancelable = cancelable;
    return this;
  }

  /**
   * Get cancelable
   * @return cancelable
   */
  
  @Schema(name = "cancelable", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancelable")
  public Boolean getCancelable() {
    return cancelable;
  }

  public void setCancelable(Boolean cancelable) {
    this.cancelable = cancelable;
  }

  public Stay cancellationId(String cancellationId) {
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

  public Stay cancelled(Boolean cancelled) {
    this.cancelled = cancelled;
    return this;
  }

  /**
   * Get cancelled
   * @return cancelled
   */
  
  @Schema(name = "cancelled", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cancelled")
  public Boolean getCancelled() {
    return cancelled;
  }

  public void setCancelled(Boolean cancelled) {
    this.cancelled = cancelled;
  }

  public Stay carDataRequired(Boolean carDataRequired) {
    this.carDataRequired = carDataRequired;
    return this;
  }

  /**
   * Get carDataRequired
   * @return carDataRequired
   */
  
  @Schema(name = "carDataRequired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("carDataRequired")
  public Boolean getCarDataRequired() {
    return carDataRequired;
  }

  public void setCarDataRequired(Boolean carDataRequired) {
    this.carDataRequired = carDataRequired;
  }

  public Stay cellCodeLegend(String cellCodeLegend) {
    this.cellCodeLegend = cellCodeLegend;
    return this;
  }

  /**
   * Get cellCodeLegend
   * @return cellCodeLegend
   */
  
  @Schema(name = "cellCodeLegend", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cellCodeLegend")
  public String getCellCodeLegend() {
    return cellCodeLegend;
  }

  public void setCellCodeLegend(String cellCodeLegend) {
    this.cellCodeLegend = cellCodeLegend;
  }

  public Stay checkInDate(String checkInDate) {
    this.checkInDate = checkInDate;
    return this;
  }

  /**
   * Get checkInDate
   * @return checkInDate
   */
  
  @Schema(name = "checkInDate", example = "2015-10-20", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("checkInDate")
  public String getCheckInDate() {
    return checkInDate;
  }

  public void setCheckInDate(String checkInDate) {
    this.checkInDate = checkInDate;
  }

  public Stay checkInOnline(Boolean checkInOnline) {
    this.checkInOnline = checkInOnline;
    return this;
  }

  /**
   * Get checkInOnline
   * @return checkInOnline
   */
  
  @Schema(name = "checkInOnline", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("checkInOnline")
  public Boolean getCheckInOnline() {
    return checkInOnline;
  }

  public void setCheckInOnline(Boolean checkInOnline) {
    this.checkInOnline = checkInOnline;
  }

  public Stay checkedIn(Boolean checkedIn) {
    this.checkedIn = checkedIn;
    return this;
  }

  /**
   * Get checkedIn
   * @return checkedIn
   */
  
  @Schema(name = "checkedIn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("checkedIn")
  public Boolean getCheckedIn() {
    return checkedIn;
  }

  public void setCheckedIn(Boolean checkedIn) {
    this.checkedIn = checkedIn;
  }

  public Stay cityTax(Price cityTax) {
    this.cityTax = cityTax;
    return this;
  }

  /**
   * Get cityTax
   * @return cityTax
   */
  @Valid 
  @Schema(name = "cityTax", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cityTax")
  public Price getCityTax() {
    return cityTax;
  }

  public void setCityTax(Price cityTax) {
    this.cityTax = cityTax;
  }

  public Stay confirmationNumber(String confirmationNumber) {
    this.confirmationNumber = confirmationNumber;
    return this;
  }

  /**
   * Get confirmationNumber
   * @return confirmationNumber
   */
  
  @Schema(name = "confirmationNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("confirmationNumber")
  public String getConfirmationNumber() {
    return confirmationNumber;
  }

  public void setConfirmationNumber(String confirmationNumber) {
    this.confirmationNumber = confirmationNumber;
  }

  public Stay customerReference(String customerReference) {
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

  public Stay departureDate(LocalDate departureDate) {
    this.departureDate = departureDate;
    return this;
  }

  /**
   * Get departureDate
   * @return departureDate
   */
  @NotNull @Valid 
  @Schema(name = "departureDate", example = "2015-10-20", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("departureDate")
  public LocalDate getDepartureDate() {
    return departureDate;
  }

  public void setDepartureDate(LocalDate departureDate) {
    this.departureDate = departureDate;
  }

  public Stay frequentBooking(String frequentBooking) {
    this.frequentBooking = frequentBooking;
    return this;
  }

  /**
   * Get frequentBooking
   * @return frequentBooking
   */
  
  @Schema(name = "frequentBooking", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("frequentBooking")
  public String getFrequentBooking() {
    return frequentBooking;
  }

  public void setFrequentBooking(String frequentBooking) {
    this.frequentBooking = frequentBooking;
  }

  public Stay guestHistoryNumber(String guestHistoryNumber) {
    this.guestHistoryNumber = guestHistoryNumber;
    return this;
  }

  /**
   * Get guestHistoryNumber
   * @return guestHistoryNumber
   */
  
  @Schema(name = "guestHistoryNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guestHistoryNumber")
  public String getGuestHistoryNumber() {
    return guestHistoryNumber;
  }

  public void setGuestHistoryNumber(String guestHistoryNumber) {
    this.guestHistoryNumber = guestHistoryNumber;
  }

  public Stay historyRecordNumber(Integer historyRecordNumber) {
    this.historyRecordNumber = historyRecordNumber;
    return this;
  }

  /**
   * Get historyRecordNumber
   * @return historyRecordNumber
   */
  
  @Schema(name = "historyRecordNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("historyRecordNumber")
  public Integer getHistoryRecordNumber() {
    return historyRecordNumber;
  }

  public void setHistoryRecordNumber(Integer historyRecordNumber) {
    this.historyRecordNumber = historyRecordNumber;
  }

  public Stay hotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
    return this;
  }

  /**
   * Get hotelCode
   * @return hotelCode
   */
  @NotNull 
  @Schema(name = "hotelCode", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelCode")
  public String getHotelCode() {
    return hotelCode;
  }

  public void setHotelCode(String hotelCode) {
    this.hotelCode = hotelCode;
  }

  public Stay hotelName(String hotelName) {
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

  public Stay leadGuest(String leadGuest) {
    this.leadGuest = leadGuest;
    return this;
  }

  /**
   * Get leadGuest
   * @return leadGuest
   */
  
  @Schema(name = "leadGuest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("leadGuest")
  public String getLeadGuest() {
    return leadGuest;
  }

  public void setLeadGuest(String leadGuest) {
    this.leadGuest = leadGuest;
  }

  public Stay leadGuestSurname(String leadGuestSurname) {
    this.leadGuestSurname = leadGuestSurname;
    return this;
  }

  /**
   * Get leadGuestSurname
   * @return leadGuestSurname
   */
  
  @Schema(name = "leadGuestSurname", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("leadGuestSurname")
  public String getLeadGuestSurname() {
    return leadGuestSurname;
  }

  public void setLeadGuestSurname(String leadGuestSurname) {
    this.leadGuestSurname = leadGuestSurname;
  }

  public Stay mpibooking(Boolean mpibooking) {
    this.mpibooking = mpibooking;
    return this;
  }

  /**
   * Get mpibooking
   * @return mpibooking
   */
  
  @Schema(name = "mpibooking", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mpibooking")
  public Boolean getMpibooking() {
    return mpibooking;
  }

  public void setMpibooking(Boolean mpibooking) {
    this.mpibooking = mpibooking;
  }

  public Stay noOfRooms(Integer noOfRooms) {
    this.noOfRooms = noOfRooms;
    return this;
  }

  /**
   * Get noOfRooms
   * @return noOfRooms
   */
  
  @Schema(name = "noOfRooms", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("noOfRooms")
  public Integer getNoOfRooms() {
    return noOfRooms;
  }

  public void setNoOfRooms(Integer noOfRooms) {
    this.noOfRooms = noOfRooms;
  }

  public Stay outstandingAmount(Price outstandingAmount) {
    this.outstandingAmount = outstandingAmount;
    return this;
  }

  /**
   * Get outstandingAmount
   * @return outstandingAmount
   */
  @Valid 
  @Schema(name = "outstandingAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("outstandingAmount")
  public Price getOutstandingAmount() {
    return outstandingAmount;
  }

  public void setOutstandingAmount(Price outstandingAmount) {
    this.outstandingAmount = outstandingAmount;
  }

  public Stay paymentStatus(String paymentStatus) {
    this.paymentStatus = paymentStatus;
    return this;
  }

  /**
   * Get paymentStatus
   * @return paymentStatus
   */
  
  @Schema(name = "paymentStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentStatus")
  public String getPaymentStatus() {
    return paymentStatus;
  }

  public void setPaymentStatus(String paymentStatus) {
    this.paymentStatus = paymentStatus;
  }

  public Stay prePaidAmount(Price prePaidAmount) {
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
  public Price getPrePaidAmount() {
    return prePaidAmount;
  }

  public void setPrePaidAmount(Price prePaidAmount) {
    this.prePaidAmount = prePaidAmount;
  }

  public Stay promotionText(String promotionText) {
    this.promotionText = promotionText;
    return this;
  }

  /**
   * Get promotionText
   * @return promotionText
   */
  
  @Schema(name = "promotionText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promotionText")
  public String getPromotionText() {
    return promotionText;
  }

  public void setPromotionText(String promotionText) {
    this.promotionText = promotionText;
  }

  public Stay purchaseOrder(String purchaseOrder) {
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

  public Stay rateClass(String rateClass) {
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

  public Stay rateName(String rateName) {
    this.rateName = rateName;
    return this;
  }

  /**
   * Get rateName
   * @return rateName
   */
  
  @Schema(name = "rateName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateName")
  public String getRateName() {
    return rateName;
  }

  public void setRateName(String rateName) {
    this.rateName = rateName;
  }

  public Stay roomTypes(List<@Valid RoomTypes> roomTypes) {
    this.roomTypes = roomTypes;
    return this;
  }

  public Stay addRoomTypesItem(RoomTypes roomTypesItem) {
    if (this.roomTypes == null) {
      this.roomTypes = new ArrayList<>();
    }
    this.roomTypes.add(roomTypesItem);
    return this;
  }

  /**
   * Get roomTypes
   * @return roomTypes
   */
  @Valid 
  @Schema(name = "roomTypes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomTypes")
  public List<@Valid RoomTypes> getRoomTypes() {
    return roomTypes;
  }

  public void setRoomTypes(List<@Valid RoomTypes> roomTypes) {
    this.roomTypes = roomTypes;
  }

  public Stay totalCost(Price totalCost) {
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
  public Price getTotalCost() {
    return totalCost;
  }

  public void setTotalCost(Price totalCost) {
    this.totalCost = totalCost;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Stay stay = (Stay) o;
    return Objects.equals(this.amendable, stay.amendable) &&
        Objects.equals(this.arrivalDate, stay.arrivalDate) &&
        Objects.equals(this.bookedBy, stay.bookedBy) &&
        Objects.equals(this.bookingDate, stay.bookingDate) &&
        Objects.equals(this.bookingFee, stay.bookingFee) &&
        Objects.equals(this.bookingStatus, stay.bookingStatus) &&
        Objects.equals(this.cancelable, stay.cancelable) &&
        Objects.equals(this.cancellationId, stay.cancellationId) &&
        Objects.equals(this.cancelled, stay.cancelled) &&
        Objects.equals(this.carDataRequired, stay.carDataRequired) &&
        Objects.equals(this.cellCodeLegend, stay.cellCodeLegend) &&
        Objects.equals(this.checkInDate, stay.checkInDate) &&
        Objects.equals(this.checkInOnline, stay.checkInOnline) &&
        Objects.equals(this.checkedIn, stay.checkedIn) &&
        Objects.equals(this.cityTax, stay.cityTax) &&
        Objects.equals(this.confirmationNumber, stay.confirmationNumber) &&
        Objects.equals(this.customerReference, stay.customerReference) &&
        Objects.equals(this.departureDate, stay.departureDate) &&
        Objects.equals(this.frequentBooking, stay.frequentBooking) &&
        Objects.equals(this.guestHistoryNumber, stay.guestHistoryNumber) &&
        Objects.equals(this.historyRecordNumber, stay.historyRecordNumber) &&
        Objects.equals(this.hotelCode, stay.hotelCode) &&
        Objects.equals(this.hotelName, stay.hotelName) &&
        Objects.equals(this.leadGuest, stay.leadGuest) &&
        Objects.equals(this.leadGuestSurname, stay.leadGuestSurname) &&
        Objects.equals(this.mpibooking, stay.mpibooking) &&
        Objects.equals(this.noOfRooms, stay.noOfRooms) &&
        Objects.equals(this.outstandingAmount, stay.outstandingAmount) &&
        Objects.equals(this.paymentStatus, stay.paymentStatus) &&
        Objects.equals(this.prePaidAmount, stay.prePaidAmount) &&
        Objects.equals(this.promotionText, stay.promotionText) &&
        Objects.equals(this.purchaseOrder, stay.purchaseOrder) &&
        Objects.equals(this.rateClass, stay.rateClass) &&
        Objects.equals(this.rateName, stay.rateName) &&
        Objects.equals(this.roomTypes, stay.roomTypes) &&
        Objects.equals(this.totalCost, stay.totalCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amendable, arrivalDate, bookedBy, bookingDate, bookingFee, bookingStatus, cancelable, cancellationId, cancelled, carDataRequired, cellCodeLegend, checkInDate, checkInOnline, checkedIn, cityTax, confirmationNumber, customerReference, departureDate, frequentBooking, guestHistoryNumber, historyRecordNumber, hotelCode, hotelName, leadGuest, leadGuestSurname, mpibooking, noOfRooms, outstandingAmount, paymentStatus, prePaidAmount, promotionText, purchaseOrder, rateClass, rateName, roomTypes, totalCost);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Stay {\n");
    sb.append("    amendable: ").append(toIndentedString(amendable)).append("\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    bookedBy: ").append(toIndentedString(bookedBy)).append("\n");
    sb.append("    bookingDate: ").append(toIndentedString(bookingDate)).append("\n");
    sb.append("    bookingFee: ").append(toIndentedString(bookingFee)).append("\n");
    sb.append("    bookingStatus: ").append(toIndentedString(bookingStatus)).append("\n");
    sb.append("    cancelable: ").append(toIndentedString(cancelable)).append("\n");
    sb.append("    cancellationId: ").append(toIndentedString(cancellationId)).append("\n");
    sb.append("    cancelled: ").append(toIndentedString(cancelled)).append("\n");
    sb.append("    carDataRequired: ").append(toIndentedString(carDataRequired)).append("\n");
    sb.append("    cellCodeLegend: ").append(toIndentedString(cellCodeLegend)).append("\n");
    sb.append("    checkInDate: ").append(toIndentedString(checkInDate)).append("\n");
    sb.append("    checkInOnline: ").append(toIndentedString(checkInOnline)).append("\n");
    sb.append("    checkedIn: ").append(toIndentedString(checkedIn)).append("\n");
    sb.append("    cityTax: ").append(toIndentedString(cityTax)).append("\n");
    sb.append("    confirmationNumber: ").append(toIndentedString(confirmationNumber)).append("\n");
    sb.append("    customerReference: ").append(toIndentedString(customerReference)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    frequentBooking: ").append(toIndentedString(frequentBooking)).append("\n");
    sb.append("    guestHistoryNumber: ").append(toIndentedString(guestHistoryNumber)).append("\n");
    sb.append("    historyRecordNumber: ").append(toIndentedString(historyRecordNumber)).append("\n");
    sb.append("    hotelCode: ").append(toIndentedString(hotelCode)).append("\n");
    sb.append("    hotelName: ").append(toIndentedString(hotelName)).append("\n");
    sb.append("    leadGuest: ").append(toIndentedString(leadGuest)).append("\n");
    sb.append("    leadGuestSurname: ").append(toIndentedString(leadGuestSurname)).append("\n");
    sb.append("    mpibooking: ").append(toIndentedString(mpibooking)).append("\n");
    sb.append("    noOfRooms: ").append(toIndentedString(noOfRooms)).append("\n");
    sb.append("    outstandingAmount: ").append(toIndentedString(outstandingAmount)).append("\n");
    sb.append("    paymentStatus: ").append(toIndentedString(paymentStatus)).append("\n");
    sb.append("    prePaidAmount: ").append(toIndentedString(prePaidAmount)).append("\n");
    sb.append("    promotionText: ").append(toIndentedString(promotionText)).append("\n");
    sb.append("    purchaseOrder: ").append(toIndentedString(purchaseOrder)).append("\n");
    sb.append("    rateClass: ").append(toIndentedString(rateClass)).append("\n");
    sb.append("    rateName: ").append(toIndentedString(rateName)).append("\n");
    sb.append("    roomTypes: ").append(toIndentedString(roomTypes)).append("\n");
    sb.append("    totalCost: ").append(toIndentedString(totalCost)).append("\n");
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

