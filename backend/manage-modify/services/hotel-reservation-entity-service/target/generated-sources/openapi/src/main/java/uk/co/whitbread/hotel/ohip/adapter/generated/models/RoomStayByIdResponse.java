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
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePerNight;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * RoomStayByIdResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomStayByIdResponse {

  private @Nullable Integer adultsNumber;

  private @Nullable String arrivalDate;

  private @Nullable String bookingChannel;

  private @Nullable String cellCode;

  private @Nullable String checkInTime;

  private @Nullable String checkOutTime;

  private @Nullable Integer childrenNumber;

  private @Nullable Boolean cot;

  private @Nullable String departureDate;

  private @Nullable String ratePlanCode;

  @Valid
  private List<@Valid RatePerNight> ratesPerNight = new ArrayList<>();

  private @Nullable String roomNumber;

  private @Nullable BigDecimal roomPrice;

  private @Nullable String roomType;

  private @Nullable String sourceCode;

  public RoomStayByIdResponse adultsNumber(Integer adultsNumber) {
    this.adultsNumber = adultsNumber;
    return this;
  }

  /**
   * Get adultsNumber
   * @return adultsNumber
   */
  
  @Schema(name = "adultsNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adultsNumber")
  public Integer getAdultsNumber() {
    return adultsNumber;
  }

  public void setAdultsNumber(Integer adultsNumber) {
    this.adultsNumber = adultsNumber;
  }

  public RoomStayByIdResponse arrivalDate(String arrivalDate) {
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

  public RoomStayByIdResponse bookingChannel(String bookingChannel) {
    this.bookingChannel = bookingChannel;
    return this;
  }

  /**
   * Get bookingChannel
   * @return bookingChannel
   */
  
  @Schema(name = "bookingChannel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingChannel")
  public String getBookingChannel() {
    return bookingChannel;
  }

  public void setBookingChannel(String bookingChannel) {
    this.bookingChannel = bookingChannel;
  }

  public RoomStayByIdResponse cellCode(String cellCode) {
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

  public RoomStayByIdResponse checkInTime(String checkInTime) {
    this.checkInTime = checkInTime;
    return this;
  }

  /**
   * Get checkInTime
   * @return checkInTime
   */
  
  @Schema(name = "checkInTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("checkInTime")
  public String getCheckInTime() {
    return checkInTime;
  }

  public void setCheckInTime(String checkInTime) {
    this.checkInTime = checkInTime;
  }

  public RoomStayByIdResponse checkOutTime(String checkOutTime) {
    this.checkOutTime = checkOutTime;
    return this;
  }

  /**
   * Get checkOutTime
   * @return checkOutTime
   */
  
  @Schema(name = "checkOutTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("checkOutTime")
  public String getCheckOutTime() {
    return checkOutTime;
  }

  public void setCheckOutTime(String checkOutTime) {
    this.checkOutTime = checkOutTime;
  }

  public RoomStayByIdResponse childrenNumber(Integer childrenNumber) {
    this.childrenNumber = childrenNumber;
    return this;
  }

  /**
   * Get childrenNumber
   * @return childrenNumber
   */
  
  @Schema(name = "childrenNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("childrenNumber")
  public Integer getChildrenNumber() {
    return childrenNumber;
  }

  public void setChildrenNumber(Integer childrenNumber) {
    this.childrenNumber = childrenNumber;
  }

  public RoomStayByIdResponse cot(Boolean cot) {
    this.cot = cot;
    return this;
  }

  /**
   * Get cot
   * @return cot
   */
  
  @Schema(name = "cot", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cot")
  public Boolean getCot() {
    return cot;
  }

  public void setCot(Boolean cot) {
    this.cot = cot;
  }

  public RoomStayByIdResponse departureDate(String departureDate) {
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

  public RoomStayByIdResponse ratePlanCode(String ratePlanCode) {
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

  public RoomStayByIdResponse ratesPerNight(List<@Valid RatePerNight> ratesPerNight) {
    this.ratesPerNight = ratesPerNight;
    return this;
  }

  public RoomStayByIdResponse addRatesPerNightItem(RatePerNight ratesPerNightItem) {
    if (this.ratesPerNight == null) {
      this.ratesPerNight = new ArrayList<>();
    }
    this.ratesPerNight.add(ratesPerNightItem);
    return this;
  }

  /**
   * Get ratesPerNight
   * @return ratesPerNight
   */
  @Valid 
  @Schema(name = "ratesPerNight", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratesPerNight")
  public List<@Valid RatePerNight> getRatesPerNight() {
    return ratesPerNight;
  }

  public void setRatesPerNight(List<@Valid RatePerNight> ratesPerNight) {
    this.ratesPerNight = ratesPerNight;
  }

  public RoomStayByIdResponse roomNumber(String roomNumber) {
    this.roomNumber = roomNumber;
    return this;
  }

  /**
   * Get roomNumber
   * @return roomNumber
   */
  
  @Schema(name = "roomNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomNumber")
  public String getRoomNumber() {
    return roomNumber;
  }

  public void setRoomNumber(String roomNumber) {
    this.roomNumber = roomNumber;
  }

  public RoomStayByIdResponse roomPrice(BigDecimal roomPrice) {
    this.roomPrice = roomPrice;
    return this;
  }

  /**
   * Get roomPrice
   * @return roomPrice
   */
  @Valid 
  @Schema(name = "roomPrice", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomPrice")
  public BigDecimal getRoomPrice() {
    return roomPrice;
  }

  public void setRoomPrice(BigDecimal roomPrice) {
    this.roomPrice = roomPrice;
  }

  public RoomStayByIdResponse roomType(String roomType) {
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

  public RoomStayByIdResponse sourceCode(String sourceCode) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomStayByIdResponse roomStayByIdResponse = (RoomStayByIdResponse) o;
    return Objects.equals(this.adultsNumber, roomStayByIdResponse.adultsNumber) &&
        Objects.equals(this.arrivalDate, roomStayByIdResponse.arrivalDate) &&
        Objects.equals(this.bookingChannel, roomStayByIdResponse.bookingChannel) &&
        Objects.equals(this.cellCode, roomStayByIdResponse.cellCode) &&
        Objects.equals(this.checkInTime, roomStayByIdResponse.checkInTime) &&
        Objects.equals(this.checkOutTime, roomStayByIdResponse.checkOutTime) &&
        Objects.equals(this.childrenNumber, roomStayByIdResponse.childrenNumber) &&
        Objects.equals(this.cot, roomStayByIdResponse.cot) &&
        Objects.equals(this.departureDate, roomStayByIdResponse.departureDate) &&
        Objects.equals(this.ratePlanCode, roomStayByIdResponse.ratePlanCode) &&
        Objects.equals(this.ratesPerNight, roomStayByIdResponse.ratesPerNight) &&
        Objects.equals(this.roomNumber, roomStayByIdResponse.roomNumber) &&
        Objects.equals(this.roomPrice, roomStayByIdResponse.roomPrice) &&
        Objects.equals(this.roomType, roomStayByIdResponse.roomType) &&
        Objects.equals(this.sourceCode, roomStayByIdResponse.sourceCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adultsNumber, arrivalDate, bookingChannel, cellCode, checkInTime, checkOutTime, childrenNumber, cot, departureDate, ratePlanCode, ratesPerNight, roomNumber, roomPrice, roomType, sourceCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomStayByIdResponse {\n");
    sb.append("    adultsNumber: ").append(toIndentedString(adultsNumber)).append("\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    bookingChannel: ").append(toIndentedString(bookingChannel)).append("\n");
    sb.append("    cellCode: ").append(toIndentedString(cellCode)).append("\n");
    sb.append("    checkInTime: ").append(toIndentedString(checkInTime)).append("\n");
    sb.append("    checkOutTime: ").append(toIndentedString(checkOutTime)).append("\n");
    sb.append("    childrenNumber: ").append(toIndentedString(childrenNumber)).append("\n");
    sb.append("    cot: ").append(toIndentedString(cot)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    ratesPerNight: ").append(toIndentedString(ratesPerNight)).append("\n");
    sb.append("    roomNumber: ").append(toIndentedString(roomNumber)).append("\n");
    sb.append("    roomPrice: ").append(toIndentedString(roomPrice)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
    sb.append("    sourceCode: ").append(toIndentedString(sourceCode)).append("\n");
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

