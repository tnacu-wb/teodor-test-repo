package uk.co.whitbread.basket.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.reservation.BookingChannelDto;
import uk.co.whitbread.basket.generated.models.reservation.LeadGuestDto;
import uk.co.whitbread.basket.generated.models.reservation.RoomOccupancyDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * EditRoomRequestDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class EditRoomRequestDto {

  private @Nullable BookingChannelDto bookingChannel;

  private LeadGuestDto leadGuest;

  private @Nullable String ratePlanCode;

  private String reservationId;

  private RoomOccupancyDto roomOccupancy;

  private String roomType;

  @Valid
  private List<String> specialRequests = new ArrayList<>();

  private String tempBookingRef;

  private @Nullable String token;

  public EditRoomRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EditRoomRequestDto(LeadGuestDto leadGuest, String reservationId, RoomOccupancyDto roomOccupancy, String roomType, String tempBookingRef) {
    this.leadGuest = leadGuest;
    this.reservationId = reservationId;
    this.roomOccupancy = roomOccupancy;
    this.roomType = roomType;
    this.tempBookingRef = tempBookingRef;
  }

  public EditRoomRequestDto bookingChannel(BookingChannelDto bookingChannel) {
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

  public EditRoomRequestDto leadGuest(LeadGuestDto leadGuest) {
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

  public EditRoomRequestDto ratePlanCode(String ratePlanCode) {
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

  public EditRoomRequestDto reservationId(String reservationId) {
    this.reservationId = reservationId;
    return this;
  }

  /**
   * Get reservationId
   * @return reservationId
   */
  @NotNull 
  @Schema(name = "reservationId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reservationId")
  public String getReservationId() {
    return reservationId;
  }

  public void setReservationId(String reservationId) {
    this.reservationId = reservationId;
  }

  public EditRoomRequestDto roomOccupancy(RoomOccupancyDto roomOccupancy) {
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

  public EditRoomRequestDto roomType(String roomType) {
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

  public EditRoomRequestDto specialRequests(List<String> specialRequests) {
    this.specialRequests = specialRequests;
    return this;
  }

  public EditRoomRequestDto addSpecialRequestsItem(String specialRequestsItem) {
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

  public EditRoomRequestDto tempBookingRef(String tempBookingRef) {
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

  public EditRoomRequestDto token(String token) {
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
    EditRoomRequestDto editRoomRequestDto = (EditRoomRequestDto) o;
    return Objects.equals(this.bookingChannel, editRoomRequestDto.bookingChannel) &&
        Objects.equals(this.leadGuest, editRoomRequestDto.leadGuest) &&
        Objects.equals(this.ratePlanCode, editRoomRequestDto.ratePlanCode) &&
        Objects.equals(this.reservationId, editRoomRequestDto.reservationId) &&
        Objects.equals(this.roomOccupancy, editRoomRequestDto.roomOccupancy) &&
        Objects.equals(this.roomType, editRoomRequestDto.roomType) &&
        Objects.equals(this.specialRequests, editRoomRequestDto.specialRequests) &&
        Objects.equals(this.tempBookingRef, editRoomRequestDto.tempBookingRef) &&
        Objects.equals(this.token, editRoomRequestDto.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingChannel, leadGuest, ratePlanCode, reservationId, roomOccupancy, roomType, specialRequests, tempBookingRef, token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EditRoomRequestDto {\n");
    sb.append("    bookingChannel: ").append(toIndentedString(bookingChannel)).append("\n");
    sb.append("    leadGuest: ").append(toIndentedString(leadGuest)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    reservationId: ").append(toIndentedString(reservationId)).append("\n");
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

