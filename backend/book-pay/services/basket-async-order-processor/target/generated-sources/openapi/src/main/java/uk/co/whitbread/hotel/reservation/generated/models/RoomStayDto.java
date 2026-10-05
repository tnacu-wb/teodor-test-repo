package uk.co.whitbread.hotel.reservation.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.reservation.generated.models.CurrencyAmountTypeDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomStayDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:09:52.163805+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomStayDto {

  private @Nullable Integer adultCount;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate arrivalDate;

  private @Nullable String bookingChannelCode;

  private @Nullable Integer childCount;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate departureDate;

  private @Nullable Boolean fixedRate;

  private @Nullable String marketCode;

  private @Nullable Integer numberOfRooms;

  private @Nullable Boolean pseudoRoom;

  private @Nullable CurrencyAmountTypeDto rateAmount;

  private @Nullable String ratePlanCode;

  private @Nullable Boolean rateSuppressed;

  private @Nullable String roomClass;

  private @Nullable Boolean roomNumberLocked;

  private @Nullable String roomType;

  private @Nullable String roomTypeCharged;

  private @Nullable String sourceCode;

  private @Nullable CurrencyAmountTypeDto totalAmount;

  public RoomStayDto adultCount(Integer adultCount) {
    this.adultCount = adultCount;
    return this;
  }

  /**
   * Get adultCount
   * @return adultCount
   */
  
  @Schema(name = "adultCount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adultCount")
  public Integer getAdultCount() {
    return adultCount;
  }

  public void setAdultCount(Integer adultCount) {
    this.adultCount = adultCount;
  }

  public RoomStayDto arrivalDate(LocalDate arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Get arrivalDate
   * @return arrivalDate
   */
  @Valid 
  @Schema(name = "arrivalDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arrivalDate")
  public LocalDate getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(LocalDate arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public RoomStayDto bookingChannelCode(String bookingChannelCode) {
    this.bookingChannelCode = bookingChannelCode;
    return this;
  }

  /**
   * Get bookingChannelCode
   * @return bookingChannelCode
   */
  
  @Schema(name = "bookingChannelCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingChannelCode")
  public String getBookingChannelCode() {
    return bookingChannelCode;
  }

  public void setBookingChannelCode(String bookingChannelCode) {
    this.bookingChannelCode = bookingChannelCode;
  }

  public RoomStayDto childCount(Integer childCount) {
    this.childCount = childCount;
    return this;
  }

  /**
   * Get childCount
   * @return childCount
   */
  
  @Schema(name = "childCount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("childCount")
  public Integer getChildCount() {
    return childCount;
  }

  public void setChildCount(Integer childCount) {
    this.childCount = childCount;
  }

  public RoomStayDto departureDate(LocalDate departureDate) {
    this.departureDate = departureDate;
    return this;
  }

  /**
   * Get departureDate
   * @return departureDate
   */
  @Valid 
  @Schema(name = "departureDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("departureDate")
  public LocalDate getDepartureDate() {
    return departureDate;
  }

  public void setDepartureDate(LocalDate departureDate) {
    this.departureDate = departureDate;
  }

  public RoomStayDto fixedRate(Boolean fixedRate) {
    this.fixedRate = fixedRate;
    return this;
  }

  /**
   * Get fixedRate
   * @return fixedRate
   */
  
  @Schema(name = "fixedRate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("fixedRate")
  public Boolean getFixedRate() {
    return fixedRate;
  }

  public void setFixedRate(Boolean fixedRate) {
    this.fixedRate = fixedRate;
  }

  public RoomStayDto marketCode(String marketCode) {
    this.marketCode = marketCode;
    return this;
  }

  /**
   * Get marketCode
   * @return marketCode
   */
  
  @Schema(name = "marketCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("marketCode")
  public String getMarketCode() {
    return marketCode;
  }

  public void setMarketCode(String marketCode) {
    this.marketCode = marketCode;
  }

  public RoomStayDto numberOfRooms(Integer numberOfRooms) {
    this.numberOfRooms = numberOfRooms;
    return this;
  }

  /**
   * Get numberOfRooms
   * @return numberOfRooms
   */
  
  @Schema(name = "numberOfRooms", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("numberOfRooms")
  public Integer getNumberOfRooms() {
    return numberOfRooms;
  }

  public void setNumberOfRooms(Integer numberOfRooms) {
    this.numberOfRooms = numberOfRooms;
  }

  public RoomStayDto pseudoRoom(Boolean pseudoRoom) {
    this.pseudoRoom = pseudoRoom;
    return this;
  }

  /**
   * Get pseudoRoom
   * @return pseudoRoom
   */
  
  @Schema(name = "pseudoRoom", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pseudoRoom")
  public Boolean getPseudoRoom() {
    return pseudoRoom;
  }

  public void setPseudoRoom(Boolean pseudoRoom) {
    this.pseudoRoom = pseudoRoom;
  }

  public RoomStayDto rateAmount(CurrencyAmountTypeDto rateAmount) {
    this.rateAmount = rateAmount;
    return this;
  }

  /**
   * Get rateAmount
   * @return rateAmount
   */
  @Valid 
  @Schema(name = "rateAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateAmount")
  public CurrencyAmountTypeDto getRateAmount() {
    return rateAmount;
  }

  public void setRateAmount(CurrencyAmountTypeDto rateAmount) {
    this.rateAmount = rateAmount;
  }

  public RoomStayDto ratePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
    return this;
  }

  /**
   * Get ratePlanCode
   * @return ratePlanCode
   */
  
  @Schema(name = "ratePlanCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanCode")
  public String getRatePlanCode() {
    return ratePlanCode;
  }

  public void setRatePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
  }

  public RoomStayDto rateSuppressed(Boolean rateSuppressed) {
    this.rateSuppressed = rateSuppressed;
    return this;
  }

  /**
   * Get rateSuppressed
   * @return rateSuppressed
   */
  
  @Schema(name = "rateSuppressed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateSuppressed")
  public Boolean getRateSuppressed() {
    return rateSuppressed;
  }

  public void setRateSuppressed(Boolean rateSuppressed) {
    this.rateSuppressed = rateSuppressed;
  }

  public RoomStayDto roomClass(String roomClass) {
    this.roomClass = roomClass;
    return this;
  }

  /**
   * Get roomClass
   * @return roomClass
   */
  
  @Schema(name = "roomClass", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomClass")
  public String getRoomClass() {
    return roomClass;
  }

  public void setRoomClass(String roomClass) {
    this.roomClass = roomClass;
  }

  public RoomStayDto roomNumberLocked(Boolean roomNumberLocked) {
    this.roomNumberLocked = roomNumberLocked;
    return this;
  }

  /**
   * Get roomNumberLocked
   * @return roomNumberLocked
   */
  
  @Schema(name = "roomNumberLocked", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomNumberLocked")
  public Boolean getRoomNumberLocked() {
    return roomNumberLocked;
  }

  public void setRoomNumberLocked(Boolean roomNumberLocked) {
    this.roomNumberLocked = roomNumberLocked;
  }

  public RoomStayDto roomType(String roomType) {
    this.roomType = roomType;
    return this;
  }

  /**
   * Get roomType
   * @return roomType
   */
  
  @Schema(name = "roomType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomType")
  public String getRoomType() {
    return roomType;
  }

  public void setRoomType(String roomType) {
    this.roomType = roomType;
  }

  public RoomStayDto roomTypeCharged(String roomTypeCharged) {
    this.roomTypeCharged = roomTypeCharged;
    return this;
  }

  /**
   * Get roomTypeCharged
   * @return roomTypeCharged
   */
  
  @Schema(name = "roomTypeCharged", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomTypeCharged")
  public String getRoomTypeCharged() {
    return roomTypeCharged;
  }

  public void setRoomTypeCharged(String roomTypeCharged) {
    this.roomTypeCharged = roomTypeCharged;
  }

  public RoomStayDto sourceCode(String sourceCode) {
    this.sourceCode = sourceCode;
    return this;
  }

  /**
   * Get sourceCode
   * @return sourceCode
   */
  
  @Schema(name = "sourceCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sourceCode")
  public String getSourceCode() {
    return sourceCode;
  }

  public void setSourceCode(String sourceCode) {
    this.sourceCode = sourceCode;
  }

  public RoomStayDto totalAmount(CurrencyAmountTypeDto totalAmount) {
    this.totalAmount = totalAmount;
    return this;
  }

  /**
   * Get totalAmount
   * @return totalAmount
   */
  @Valid 
  @Schema(name = "totalAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalAmount")
  public CurrencyAmountTypeDto getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(CurrencyAmountTypeDto totalAmount) {
    this.totalAmount = totalAmount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomStayDto roomStayDto = (RoomStayDto) o;
    return Objects.equals(this.adultCount, roomStayDto.adultCount) &&
        Objects.equals(this.arrivalDate, roomStayDto.arrivalDate) &&
        Objects.equals(this.bookingChannelCode, roomStayDto.bookingChannelCode) &&
        Objects.equals(this.childCount, roomStayDto.childCount) &&
        Objects.equals(this.departureDate, roomStayDto.departureDate) &&
        Objects.equals(this.fixedRate, roomStayDto.fixedRate) &&
        Objects.equals(this.marketCode, roomStayDto.marketCode) &&
        Objects.equals(this.numberOfRooms, roomStayDto.numberOfRooms) &&
        Objects.equals(this.pseudoRoom, roomStayDto.pseudoRoom) &&
        Objects.equals(this.rateAmount, roomStayDto.rateAmount) &&
        Objects.equals(this.ratePlanCode, roomStayDto.ratePlanCode) &&
        Objects.equals(this.rateSuppressed, roomStayDto.rateSuppressed) &&
        Objects.equals(this.roomClass, roomStayDto.roomClass) &&
        Objects.equals(this.roomNumberLocked, roomStayDto.roomNumberLocked) &&
        Objects.equals(this.roomType, roomStayDto.roomType) &&
        Objects.equals(this.roomTypeCharged, roomStayDto.roomTypeCharged) &&
        Objects.equals(this.sourceCode, roomStayDto.sourceCode) &&
        Objects.equals(this.totalAmount, roomStayDto.totalAmount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adultCount, arrivalDate, bookingChannelCode, childCount, departureDate, fixedRate, marketCode, numberOfRooms, pseudoRoom, rateAmount, ratePlanCode, rateSuppressed, roomClass, roomNumberLocked, roomType, roomTypeCharged, sourceCode, totalAmount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomStayDto {\n");
    sb.append("    adultCount: ").append(toIndentedString(adultCount)).append("\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    bookingChannelCode: ").append(toIndentedString(bookingChannelCode)).append("\n");
    sb.append("    childCount: ").append(toIndentedString(childCount)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    fixedRate: ").append(toIndentedString(fixedRate)).append("\n");
    sb.append("    marketCode: ").append(toIndentedString(marketCode)).append("\n");
    sb.append("    numberOfRooms: ").append(toIndentedString(numberOfRooms)).append("\n");
    sb.append("    pseudoRoom: ").append(toIndentedString(pseudoRoom)).append("\n");
    sb.append("    rateAmount: ").append(toIndentedString(rateAmount)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    rateSuppressed: ").append(toIndentedString(rateSuppressed)).append("\n");
    sb.append("    roomClass: ").append(toIndentedString(roomClass)).append("\n");
    sb.append("    roomNumberLocked: ").append(toIndentedString(roomNumberLocked)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
    sb.append("    roomTypeCharged: ").append(toIndentedString(roomTypeCharged)).append("\n");
    sb.append("    sourceCode: ").append(toIndentedString(sourceCode)).append("\n");
    sb.append("    totalAmount: ").append(toIndentedString(totalAmount)).append("\n");
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

