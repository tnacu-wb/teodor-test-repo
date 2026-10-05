package uk.co.whitbread.hotel.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.reservation.BookingChannelDto;
import uk.co.whitbread.hotel.generated.models.reservation.LeadGuestDto;
import uk.co.whitbread.hotel.generated.models.reservation.RoomOccupancyDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * AddNewRoomRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:18.711997+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AddNewRoomRequestDto {

  private @Nullable BookingChannelDto bookingChannel;

  private LeadGuestDto leadGuest;

  private @Nullable String ratePlanCode;

  private RoomOccupancyDto roomOccupancy;

  private String roomType;

  @Valid
  private List<String> specialRequests = new ArrayList<>();

  private String tempBookingRef;

  private @Nullable String token;

  public AddNewRoomRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AddNewRoomRequestDto(LeadGuestDto leadGuest, RoomOccupancyDto roomOccupancy, String roomType, String tempBookingRef) {
    this.leadGuest = leadGuest;
    this.roomOccupancy = roomOccupancy;
    this.roomType = roomType;
    this.tempBookingRef = tempBookingRef;
  }

  public AddNewRoomRequestDto bookingChannel(BookingChannelDto bookingChannel) {
    this.bookingChannel = bookingChannel;
    return this;
  }

  /**
   * Get bookingChannel
   * @return bookingChannel
   */
  @Valid 
  @Schema(name = "bookingChannel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingChannel")
  public BookingChannelDto getBookingChannel() {
    return bookingChannel;
  }

  public void setBookingChannel(BookingChannelDto bookingChannel) {
    this.bookingChannel = bookingChannel;
  }

  public AddNewRoomRequestDto leadGuest(LeadGuestDto leadGuest) {
    this.leadGuest = leadGuest;
    return this;
  }

  /**
   * Get leadGuest
   * @return leadGuest
   */
  @NotNull @Valid 
  @Schema(name = "leadGuest", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("leadGuest")
  public LeadGuestDto getLeadGuest() {
    return leadGuest;
  }

  public void setLeadGuest(LeadGuestDto leadGuest) {
    this.leadGuest = leadGuest;
  }

  public AddNewRoomRequestDto ratePlanCode(String ratePlanCode) {
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

  public AddNewRoomRequestDto roomOccupancy(RoomOccupancyDto roomOccupancy) {
    this.roomOccupancy = roomOccupancy;
    return this;
  }

  /**
   * Get roomOccupancy
   * @return roomOccupancy
   */
  @NotNull @Valid 
  @Schema(name = "roomOccupancy", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("roomOccupancy")
  public RoomOccupancyDto getRoomOccupancy() {
    return roomOccupancy;
  }

  public void setRoomOccupancy(RoomOccupancyDto roomOccupancy) {
    this.roomOccupancy = roomOccupancy;
  }

  public AddNewRoomRequestDto roomType(String roomType) {
    this.roomType = roomType;
    return this;
  }

  /**
   * Get roomType
   * @return roomType
   */
  @NotNull 
  @Schema(name = "roomType", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("roomType")
  public String getRoomType() {
    return roomType;
  }

  public void setRoomType(String roomType) {
    this.roomType = roomType;
  }

  public AddNewRoomRequestDto specialRequests(List<String> specialRequests) {
    this.specialRequests = specialRequests;
    return this;
  }

  public AddNewRoomRequestDto addSpecialRequestsItem(String specialRequestsItem) {
    if (this.specialRequests == null) {
      this.specialRequests = new ArrayList<>();
    }
    this.specialRequests.add(specialRequestsItem);
    return this;
  }

  /**
   * Get specialRequests
   * @return specialRequests
   */
  
  @Schema(name = "specialRequests", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("specialRequests")
  public List<String> getSpecialRequests() {
    return specialRequests;
  }

  public void setSpecialRequests(List<String> specialRequests) {
    this.specialRequests = specialRequests;
  }

  public AddNewRoomRequestDto tempBookingRef(String tempBookingRef) {
    this.tempBookingRef = tempBookingRef;
    return this;
  }

  /**
   * Get tempBookingRef
   * @return tempBookingRef
   */
  @NotNull 
  @Schema(name = "tempBookingRef", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("tempBookingRef")
  public String getTempBookingRef() {
    return tempBookingRef;
  }

  public void setTempBookingRef(String tempBookingRef) {
    this.tempBookingRef = tempBookingRef;
  }

  public AddNewRoomRequestDto token(String token) {
    this.token = token;
    return this;
  }

  /**
   * Get token
   * @return token
   */
  
  @Schema(name = "token", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("token")
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AddNewRoomRequestDto addNewRoomRequestDto = (AddNewRoomRequestDto) o;
    return Objects.equals(this.bookingChannel, addNewRoomRequestDto.bookingChannel) &&
        Objects.equals(this.leadGuest, addNewRoomRequestDto.leadGuest) &&
        Objects.equals(this.ratePlanCode, addNewRoomRequestDto.ratePlanCode) &&
        Objects.equals(this.roomOccupancy, addNewRoomRequestDto.roomOccupancy) &&
        Objects.equals(this.roomType, addNewRoomRequestDto.roomType) &&
        Objects.equals(this.specialRequests, addNewRoomRequestDto.specialRequests) &&
        Objects.equals(this.tempBookingRef, addNewRoomRequestDto.tempBookingRef) &&
        Objects.equals(this.token, addNewRoomRequestDto.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingChannel, leadGuest, ratePlanCode, roomOccupancy, roomType, specialRequests, tempBookingRef, token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AddNewRoomRequestDto {\n");
    sb.append("    bookingChannel: ").append(toIndentedString(bookingChannel)).append("\n");
    sb.append("    leadGuest: ").append(toIndentedString(leadGuest)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    roomOccupancy: ").append(toIndentedString(roomOccupancy)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
    sb.append("    specialRequests: ").append(toIndentedString(specialRequests)).append("\n");
    sb.append("    tempBookingRef: ").append(toIndentedString(tempBookingRef)).append("\n");
    sb.append("    token: ").append(toIndentedString(token)).append("\n");
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

