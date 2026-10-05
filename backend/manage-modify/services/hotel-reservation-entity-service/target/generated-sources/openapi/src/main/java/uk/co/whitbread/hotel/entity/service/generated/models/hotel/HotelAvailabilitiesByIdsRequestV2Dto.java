package uk.co.whitbread.hotel.entity.service.generated.models.hotel;

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
import uk.co.whitbread.hotel.entity.service.generated.models.hotel.BookingChannelDto;
import uk.co.whitbread.hotel.entity.service.generated.models.hotel.RateDto;
import uk.co.whitbread.hotel.entity.service.generated.models.hotel.RoomDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * HotelAvailabilitiesByIdsRequestV2Dto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:33.749132+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelAvailabilitiesByIdsRequestV2Dto {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate arrivalDate;

  private BookingChannelDto bookingChannel;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate departureDate;

  @Valid
  private List<String> hotelIds = new ArrayList<>();

  private @Nullable Boolean isOTA;

  private RateDto rates;

  @Valid
  private List<@Valid RoomDto> rooms = new ArrayList<>();

  private @Nullable Boolean vatNotRequired;

  public HotelAvailabilitiesByIdsRequestV2Dto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public HotelAvailabilitiesByIdsRequestV2Dto(LocalDate arrivalDate, BookingChannelDto bookingChannel, LocalDate departureDate, List<String> hotelIds, RateDto rates, List<@Valid RoomDto> rooms) {
    this.arrivalDate = arrivalDate;
    this.bookingChannel = bookingChannel;
    this.departureDate = departureDate;
    this.hotelIds = hotelIds;
    this.rates = rates;
    this.rooms = rooms;
  }

  public HotelAvailabilitiesByIdsRequestV2Dto arrivalDate(LocalDate arrivalDate) {
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

  public HotelAvailabilitiesByIdsRequestV2Dto bookingChannel(BookingChannelDto bookingChannel) {
    this.bookingChannel = bookingChannel;
    return this;
  }

  /**
   * Get bookingChannel
   * @return bookingChannel
   */
  @NotNull @Valid 
  @Schema(name = "bookingChannel", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bookingChannel")
  public BookingChannelDto getBookingChannel() {
    return bookingChannel;
  }

  public void setBookingChannel(BookingChannelDto bookingChannel) {
    this.bookingChannel = bookingChannel;
  }

  public HotelAvailabilitiesByIdsRequestV2Dto departureDate(LocalDate departureDate) {
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

  public HotelAvailabilitiesByIdsRequestV2Dto hotelIds(List<String> hotelIds) {
    this.hotelIds = hotelIds;
    return this;
  }

  public HotelAvailabilitiesByIdsRequestV2Dto addHotelIdsItem(String hotelIdsItem) {
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

  public HotelAvailabilitiesByIdsRequestV2Dto isOTA(Boolean isOTA) {
    this.isOTA = isOTA;
    return this;
  }

  /**
   * Get isOTA
   * @return isOTA
   */
  
  @Schema(name = "isOTA", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isOTA")
  public Boolean getIsOTA() {
    return isOTA;
  }

  public void setIsOTA(Boolean isOTA) {
    this.isOTA = isOTA;
  }

  public HotelAvailabilitiesByIdsRequestV2Dto rates(RateDto rates) {
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
  public RateDto getRates() {
    return rates;
  }

  public void setRates(RateDto rates) {
    this.rates = rates;
  }

  public HotelAvailabilitiesByIdsRequestV2Dto rooms(List<@Valid RoomDto> rooms) {
    this.rooms = rooms;
    return this;
  }

  public HotelAvailabilitiesByIdsRequestV2Dto addRoomsItem(RoomDto roomsItem) {
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
  public List<@Valid RoomDto> getRooms() {
    return rooms;
  }

  public void setRooms(List<@Valid RoomDto> rooms) {
    this.rooms = rooms;
  }

  public HotelAvailabilitiesByIdsRequestV2Dto vatNotRequired(Boolean vatNotRequired) {
    this.vatNotRequired = vatNotRequired;
    return this;
  }

  /**
   * Get vatNotRequired
   * @return vatNotRequired
   */
  
  @Schema(name = "vatNotRequired", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("vatNotRequired")
  public Boolean getVatNotRequired() {
    return vatNotRequired;
  }

  public void setVatNotRequired(Boolean vatNotRequired) {
    this.vatNotRequired = vatNotRequired;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HotelAvailabilitiesByIdsRequestV2Dto hotelAvailabilitiesByIdsRequestV2Dto = (HotelAvailabilitiesByIdsRequestV2Dto) o;
    return Objects.equals(this.arrivalDate, hotelAvailabilitiesByIdsRequestV2Dto.arrivalDate) &&
        Objects.equals(this.bookingChannel, hotelAvailabilitiesByIdsRequestV2Dto.bookingChannel) &&
        Objects.equals(this.departureDate, hotelAvailabilitiesByIdsRequestV2Dto.departureDate) &&
        Objects.equals(this.hotelIds, hotelAvailabilitiesByIdsRequestV2Dto.hotelIds) &&
        Objects.equals(this.isOTA, hotelAvailabilitiesByIdsRequestV2Dto.isOTA) &&
        Objects.equals(this.rates, hotelAvailabilitiesByIdsRequestV2Dto.rates) &&
        Objects.equals(this.rooms, hotelAvailabilitiesByIdsRequestV2Dto.rooms) &&
        Objects.equals(this.vatNotRequired, hotelAvailabilitiesByIdsRequestV2Dto.vatNotRequired);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrivalDate, bookingChannel, departureDate, hotelIds, isOTA, rates, rooms, vatNotRequired);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelAvailabilitiesByIdsRequestV2Dto {\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    bookingChannel: ").append(toIndentedString(bookingChannel)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    hotelIds: ").append(toIndentedString(hotelIds)).append("\n");
    sb.append("    isOTA: ").append(toIndentedString(isOTA)).append("\n");
    sb.append("    rates: ").append(toIndentedString(rates)).append("\n");
    sb.append("    rooms: ").append(toIndentedString(rooms)).append("\n");
    sb.append("    vatNotRequired: ").append(toIndentedString(vatNotRequired)).append("\n");
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

