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
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePerNightDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomStayByIdDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomStayByIdDto {

  private @Nullable Integer adults;

  private @Nullable String arrivalDate;

  private @Nullable String bookingChannel;

  private @Nullable String cellCode;

  private @Nullable String checkInTime;

  private @Nullable String checkOutTime;

  private @Nullable Integer children;

  private @Nullable Boolean cot;

  private @Nullable String departureDate;

  private @Nullable String ratePlanCode;

  @Valid
  private @Nullable List<@Valid RatePerNightDto> ratesPerNight;

  private @Nullable String roomNumber;

  private @Nullable BigDecimal roomPrice;

  private @Nullable String roomType;

  private @Nullable String sourceCode;

  public RoomStayByIdDto adults(Integer adults) {
    this.adults = adults;
    return this;
  }

  /**
   * Get adults
   * @return adults
   */
  
  @Schema(name = "adults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adults")
  public Integer getAdults() {
    return adults;
  }

  public void setAdults(Integer adults) {
    this.adults = adults;
  }

  public RoomStayByIdDto arrivalDate(String arrivalDate) {
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

  public RoomStayByIdDto bookingChannel(String bookingChannel) {
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

  public RoomStayByIdDto cellCode(String cellCode) {
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

  public RoomStayByIdDto checkInTime(String checkInTime) {
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

  public RoomStayByIdDto checkOutTime(String checkOutTime) {
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

  public RoomStayByIdDto children(Integer children) {
    this.children = children;
    return this;
  }

  /**
   * Get children
   * @return children
   */
  
  @Schema(name = "children", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("children")
  public Integer getChildren() {
    return children;
  }

  public void setChildren(Integer children) {
    this.children = children;
  }

  public RoomStayByIdDto cot(Boolean cot) {
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

  public RoomStayByIdDto departureDate(String departureDate) {
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

  public RoomStayByIdDto ratePlanCode(String ratePlanCode) {
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

  public RoomStayByIdDto ratesPerNight(List<@Valid RatePerNightDto> ratesPerNight) {
    this.ratesPerNight = ratesPerNight;
    return this;
  }

  public RoomStayByIdDto addRatesPerNightItem(RatePerNightDto ratesPerNightItem) {
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
  public List<@Valid RatePerNightDto> getRatesPerNight() {
    return ratesPerNight;
  }

  public void setRatesPerNight(List<@Valid RatePerNightDto> ratesPerNight) {
    this.ratesPerNight = ratesPerNight;
  }

  public RoomStayByIdDto roomNumber(String roomNumber) {
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

  public RoomStayByIdDto roomPrice(BigDecimal roomPrice) {
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

  public RoomStayByIdDto roomType(String roomType) {
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

  public RoomStayByIdDto sourceCode(String sourceCode) {
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
    RoomStayByIdDto roomStayByIdDto = (RoomStayByIdDto) o;
    return Objects.equals(this.adults, roomStayByIdDto.adults) &&
        Objects.equals(this.arrivalDate, roomStayByIdDto.arrivalDate) &&
        Objects.equals(this.bookingChannel, roomStayByIdDto.bookingChannel) &&
        Objects.equals(this.cellCode, roomStayByIdDto.cellCode) &&
        Objects.equals(this.checkInTime, roomStayByIdDto.checkInTime) &&
        Objects.equals(this.checkOutTime, roomStayByIdDto.checkOutTime) &&
        Objects.equals(this.children, roomStayByIdDto.children) &&
        Objects.equals(this.cot, roomStayByIdDto.cot) &&
        Objects.equals(this.departureDate, roomStayByIdDto.departureDate) &&
        Objects.equals(this.ratePlanCode, roomStayByIdDto.ratePlanCode) &&
        Objects.equals(this.ratesPerNight, roomStayByIdDto.ratesPerNight) &&
        Objects.equals(this.roomNumber, roomStayByIdDto.roomNumber) &&
        Objects.equals(this.roomPrice, roomStayByIdDto.roomPrice) &&
        Objects.equals(this.roomType, roomStayByIdDto.roomType) &&
        Objects.equals(this.sourceCode, roomStayByIdDto.sourceCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adults, arrivalDate, bookingChannel, cellCode, checkInTime, checkOutTime, children, cot, departureDate, ratePlanCode, ratesPerNight, roomNumber, roomPrice, roomType, sourceCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomStayByIdDto {\n");
    sb.append("    adults: ").append(toIndentedString(adults)).append("\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    bookingChannel: ").append(toIndentedString(bookingChannel)).append("\n");
    sb.append("    cellCode: ").append(toIndentedString(cellCode)).append("\n");
    sb.append("    checkInTime: ").append(toIndentedString(checkInTime)).append("\n");
    sb.append("    checkOutTime: ").append(toIndentedString(checkOutTime)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
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

