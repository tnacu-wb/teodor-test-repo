package uk.co.whitbread.hotel.cdh.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.cdh.adapter.generated.models.BookerDto;
import uk.co.whitbread.hotel.cdh.adapter.generated.models.EmployeeAnswersDto;
import uk.co.whitbread.hotel.cdh.adapter.generated.models.PriceDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ReportsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:35.320601+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ReportsDto {

  private @Nullable String arrivalDate;

  private @Nullable BookerDto booker;

  private @Nullable String bookingDate;

  private @Nullable String bookingReference;

  private @Nullable String bookingStatus;

  private @Nullable String cardNumber;

  private @Nullable String cardType;

  private @Nullable String customerReference;

  private @Nullable String departureDate;

  private @Nullable EmployeeAnswersDto employeeAnswers;

  private @Nullable BookerDto guest;

  private @Nullable String hotelName;

  private @Nullable Integer leadTime;

  private @Nullable Integer nights;

  private @Nullable Integer noOfAdults;

  private @Nullable Integer noOfChildren;

  private @Nullable String purchaseOrder;

  private @Nullable String rateCategory;

  private @Nullable PriceDto roomCost;

  private @Nullable Integer rooms;

  private @Nullable PriceDto totalCost;

  private @Nullable PriceDto upsellTotalCost;

  public ReportsDto arrivalDate(String arrivalDate) {
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

  public ReportsDto booker(BookerDto booker) {
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

  public ReportsDto bookingDate(String bookingDate) {
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

  public ReportsDto bookingReference(String bookingReference) {
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

  public ReportsDto bookingStatus(String bookingStatus) {
    this.bookingStatus = bookingStatus;
    return this;
  }

  /**
   * Get bookingStatus
   * @return bookingStatus
   */
  
  @Schema(name = "bookingStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingStatus")
  public String getBookingStatus() {
    return bookingStatus;
  }

  public void setBookingStatus(String bookingStatus) {
    this.bookingStatus = bookingStatus;
  }

  public ReportsDto cardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
    return this;
  }

  /**
   * Get cardNumber
   * @return cardNumber
   */
  
  @Schema(name = "cardNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardNumber")
  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public ReportsDto cardType(String cardType) {
    this.cardType = cardType;
    return this;
  }

  /**
   * Get cardType
   * @return cardType
   */
  
  @Schema(name = "cardType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardType")
  public String getCardType() {
    return cardType;
  }

  public void setCardType(String cardType) {
    this.cardType = cardType;
  }

  public ReportsDto customerReference(String customerReference) {
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

  public ReportsDto departureDate(String departureDate) {
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

  public ReportsDto employeeAnswers(EmployeeAnswersDto employeeAnswers) {
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

  public ReportsDto guest(BookerDto guest) {
    this.guest = guest;
    return this;
  }

  /**
   * Get guest
   * @return guest
   */
  @Valid 
  @Schema(name = "guest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guest")
  public BookerDto getGuest() {
    return guest;
  }

  public void setGuest(BookerDto guest) {
    this.guest = guest;
  }

  public ReportsDto hotelName(String hotelName) {
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

  public ReportsDto leadTime(Integer leadTime) {
    this.leadTime = leadTime;
    return this;
  }

  /**
   * Get leadTime
   * @return leadTime
   */
  
  @Schema(name = "leadTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("leadTime")
  public Integer getLeadTime() {
    return leadTime;
  }

  public void setLeadTime(Integer leadTime) {
    this.leadTime = leadTime;
  }

  public ReportsDto nights(Integer nights) {
    this.nights = nights;
    return this;
  }

  /**
   * Get nights
   * @return nights
   */
  
  @Schema(name = "nights", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nights")
  public Integer getNights() {
    return nights;
  }

  public void setNights(Integer nights) {
    this.nights = nights;
  }

  public ReportsDto noOfAdults(Integer noOfAdults) {
    this.noOfAdults = noOfAdults;
    return this;
  }

  /**
   * Get noOfAdults
   * @return noOfAdults
   */
  
  @Schema(name = "noOfAdults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("noOfAdults")
  public Integer getNoOfAdults() {
    return noOfAdults;
  }

  public void setNoOfAdults(Integer noOfAdults) {
    this.noOfAdults = noOfAdults;
  }

  public ReportsDto noOfChildren(Integer noOfChildren) {
    this.noOfChildren = noOfChildren;
    return this;
  }

  /**
   * Get noOfChildren
   * @return noOfChildren
   */
  
  @Schema(name = "noOfChildren", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("noOfChildren")
  public Integer getNoOfChildren() {
    return noOfChildren;
  }

  public void setNoOfChildren(Integer noOfChildren) {
    this.noOfChildren = noOfChildren;
  }

  public ReportsDto purchaseOrder(String purchaseOrder) {
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

  public ReportsDto rateCategory(String rateCategory) {
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

  public ReportsDto roomCost(PriceDto roomCost) {
    this.roomCost = roomCost;
    return this;
  }

  /**
   * Get roomCost
   * @return roomCost
   */
  @Valid 
  @Schema(name = "roomCost", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomCost")
  public PriceDto getRoomCost() {
    return roomCost;
  }

  public void setRoomCost(PriceDto roomCost) {
    this.roomCost = roomCost;
  }

  public ReportsDto rooms(Integer rooms) {
    this.rooms = rooms;
    return this;
  }

  /**
   * Get rooms
   * @return rooms
   */
  
  @Schema(name = "rooms", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rooms")
  public Integer getRooms() {
    return rooms;
  }

  public void setRooms(Integer rooms) {
    this.rooms = rooms;
  }

  public ReportsDto totalCost(PriceDto totalCost) {
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

  public ReportsDto upsellTotalCost(PriceDto upsellTotalCost) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReportsDto reportsDto = (ReportsDto) o;
    return Objects.equals(this.arrivalDate, reportsDto.arrivalDate) &&
        Objects.equals(this.booker, reportsDto.booker) &&
        Objects.equals(this.bookingDate, reportsDto.bookingDate) &&
        Objects.equals(this.bookingReference, reportsDto.bookingReference) &&
        Objects.equals(this.bookingStatus, reportsDto.bookingStatus) &&
        Objects.equals(this.cardNumber, reportsDto.cardNumber) &&
        Objects.equals(this.cardType, reportsDto.cardType) &&
        Objects.equals(this.customerReference, reportsDto.customerReference) &&
        Objects.equals(this.departureDate, reportsDto.departureDate) &&
        Objects.equals(this.employeeAnswers, reportsDto.employeeAnswers) &&
        Objects.equals(this.guest, reportsDto.guest) &&
        Objects.equals(this.hotelName, reportsDto.hotelName) &&
        Objects.equals(this.leadTime, reportsDto.leadTime) &&
        Objects.equals(this.nights, reportsDto.nights) &&
        Objects.equals(this.noOfAdults, reportsDto.noOfAdults) &&
        Objects.equals(this.noOfChildren, reportsDto.noOfChildren) &&
        Objects.equals(this.purchaseOrder, reportsDto.purchaseOrder) &&
        Objects.equals(this.rateCategory, reportsDto.rateCategory) &&
        Objects.equals(this.roomCost, reportsDto.roomCost) &&
        Objects.equals(this.rooms, reportsDto.rooms) &&
        Objects.equals(this.totalCost, reportsDto.totalCost) &&
        Objects.equals(this.upsellTotalCost, reportsDto.upsellTotalCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrivalDate, booker, bookingDate, bookingReference, bookingStatus, cardNumber, cardType, customerReference, departureDate, employeeAnswers, guest, hotelName, leadTime, nights, noOfAdults, noOfChildren, purchaseOrder, rateCategory, roomCost, rooms, totalCost, upsellTotalCost);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReportsDto {\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    booker: ").append(toIndentedString(booker)).append("\n");
    sb.append("    bookingDate: ").append(toIndentedString(bookingDate)).append("\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
    sb.append("    bookingStatus: ").append(toIndentedString(bookingStatus)).append("\n");
    sb.append("    cardNumber: ").append(toIndentedString(cardNumber)).append("\n");
    sb.append("    cardType: ").append(toIndentedString(cardType)).append("\n");
    sb.append("    customerReference: ").append(toIndentedString(customerReference)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    employeeAnswers: ").append(toIndentedString(employeeAnswers)).append("\n");
    sb.append("    guest: ").append(toIndentedString(guest)).append("\n");
    sb.append("    hotelName: ").append(toIndentedString(hotelName)).append("\n");
    sb.append("    leadTime: ").append(toIndentedString(leadTime)).append("\n");
    sb.append("    nights: ").append(toIndentedString(nights)).append("\n");
    sb.append("    noOfAdults: ").append(toIndentedString(noOfAdults)).append("\n");
    sb.append("    noOfChildren: ").append(toIndentedString(noOfChildren)).append("\n");
    sb.append("    purchaseOrder: ").append(toIndentedString(purchaseOrder)).append("\n");
    sb.append("    rateCategory: ").append(toIndentedString(rateCategory)).append("\n");
    sb.append("    roomCost: ").append(toIndentedString(roomCost)).append("\n");
    sb.append("    rooms: ").append(toIndentedString(rooms)).append("\n");
    sb.append("    totalCost: ").append(toIndentedString(totalCost)).append("\n");
    sb.append("    upsellTotalCost: ").append(toIndentedString(upsellTotalCost)).append("\n");
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

