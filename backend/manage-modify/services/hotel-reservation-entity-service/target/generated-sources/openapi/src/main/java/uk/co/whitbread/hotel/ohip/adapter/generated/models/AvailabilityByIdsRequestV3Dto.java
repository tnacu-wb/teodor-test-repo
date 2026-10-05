package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BookingChannelDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RateV3Dto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomV2Dto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * AvailabilityByIdsRequestV3Dto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AvailabilityByIdsRequestV3Dto {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate arrivalDate;

  private @Nullable BookingChannelDto bookingChannel;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate departureDate;

  @Valid
  private List<String> hotelIds = new ArrayList<>();

  private RateV3Dto rates;

  @Valid
  private List<@Valid RoomV2Dto> rooms = new ArrayList<>();

  public AvailabilityByIdsRequestV3Dto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AvailabilityByIdsRequestV3Dto(LocalDate arrivalDate, LocalDate departureDate, List<String> hotelIds, RateV3Dto rates, List<@Valid RoomV2Dto> rooms) {
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
    this.hotelIds = hotelIds;
    this.rates = rates;
    this.rooms = rooms;
  }

  public AvailabilityByIdsRequestV3Dto arrivalDate(LocalDate arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Get arrivalDate
   * @return arrivalDate
   */
  @NotNull @Valid 
  @Schema(name = "arrivalDate", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("arrivalDate")
  public LocalDate getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(LocalDate arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public AvailabilityByIdsRequestV3Dto bookingChannel(BookingChannelDto bookingChannel) {
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

  public AvailabilityByIdsRequestV3Dto departureDate(LocalDate departureDate) {
    this.departureDate = departureDate;
    return this;
  }

  /**
   * Get departureDate
   * @return departureDate
   */
  @NotNull @Valid 
  @Schema(name = "departureDate", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("departureDate")
  public LocalDate getDepartureDate() {
    return departureDate;
  }

  public void setDepartureDate(LocalDate departureDate) {
    this.departureDate = departureDate;
  }

  public AvailabilityByIdsRequestV3Dto hotelIds(List<String> hotelIds) {
    this.hotelIds = hotelIds;
    return this;
  }

  public AvailabilityByIdsRequestV3Dto addHotelIdsItem(String hotelIdsItem) {
    if (this.hotelIds == null) {
      this.hotelIds = new ArrayList<>();
    }
    this.hotelIds.add(hotelIdsItem);
    return this;
  }

  /**
   * Get hotelIds
   * @return hotelIds
   */
  @NotNull 
  @Schema(name = "hotelIds", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hotelIds")
  public List<String> getHotelIds() {
    return hotelIds;
  }

  public void setHotelIds(List<String> hotelIds) {
    this.hotelIds = hotelIds;
  }

  public AvailabilityByIdsRequestV3Dto rates(RateV3Dto rates) {
    this.rates = rates;
    return this;
  }

  /**
   * Get rates
   * @return rates
   */
  @NotNull @Valid 
  @Schema(name = "rates", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("rates")
  public RateV3Dto getRates() {
    return rates;
  }

  public void setRates(RateV3Dto rates) {
    this.rates = rates;
  }

  public AvailabilityByIdsRequestV3Dto rooms(List<@Valid RoomV2Dto> rooms) {
    this.rooms = rooms;
    return this;
  }

  public AvailabilityByIdsRequestV3Dto addRoomsItem(RoomV2Dto roomsItem) {
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
  @NotNull @Valid 
  @Schema(name = "rooms", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("rooms")
  public List<@Valid RoomV2Dto> getRooms() {
    return rooms;
  }

  public void setRooms(List<@Valid RoomV2Dto> rooms) {
    this.rooms = rooms;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AvailabilityByIdsRequestV3Dto availabilityByIdsRequestV3Dto = (AvailabilityByIdsRequestV3Dto) o;
    return Objects.equals(this.arrivalDate, availabilityByIdsRequestV3Dto.arrivalDate) &&
        Objects.equals(this.bookingChannel, availabilityByIdsRequestV3Dto.bookingChannel) &&
        Objects.equals(this.departureDate, availabilityByIdsRequestV3Dto.departureDate) &&
        Objects.equals(this.hotelIds, availabilityByIdsRequestV3Dto.hotelIds) &&
        Objects.equals(this.rates, availabilityByIdsRequestV3Dto.rates) &&
        Objects.equals(this.rooms, availabilityByIdsRequestV3Dto.rooms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrivalDate, bookingChannel, departureDate, hotelIds, rates, rooms);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AvailabilityByIdsRequestV3Dto {\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    bookingChannel: ").append(toIndentedString(bookingChannel)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    hotelIds: ").append(toIndentedString(hotelIds)).append("\n");
    sb.append("    rates: ").append(toIndentedString(rates)).append("\n");
    sb.append("    rooms: ").append(toIndentedString(rooms)).append("\n");
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

