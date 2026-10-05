package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BusinessSiteDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.GuestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.RoomTypeDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BookingDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookingDto {

  private @Nullable String arrivalDate;

  private @Nullable String bookingReference;

  private BusinessSiteDto businessSite;

  private String channel;

  private @Nullable String departureDate;

  private String journey;

  private @Nullable String language;

  private @Nullable GuestDto leadGuest;

  private @Nullable String reference;

  @Valid
  private List<@Valid RoomTypeDto> rooms = new ArrayList<>();

  private String type;

  public BookingDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BookingDto(BusinessSiteDto businessSite, String channel, String journey, String type) {
    this.businessSite = businessSite;
    this.channel = channel;
    this.journey = journey;
    this.type = type;
  }

  public BookingDto arrivalDate(String arrivalDate) {
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

  public BookingDto bookingReference(String bookingReference) {
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

  public BookingDto businessSite(BusinessSiteDto businessSite) {
    this.businessSite = businessSite;
    return this;
  }

  /**
   * Get businessSite
   * @return businessSite
   */
  @NotNull @Valid 
  @Schema(name = "businessSite", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("businessSite")
  public BusinessSiteDto getBusinessSite() {
    return businessSite;
  }

  public void setBusinessSite(BusinessSiteDto businessSite) {
    this.businessSite = businessSite;
  }

  public BookingDto channel(String channel) {
    this.channel = channel;
    return this;
  }

  /**
   * Get channel
   * @return channel
   */
  @NotNull 
  @Schema(name = "channel", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("channel")
  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public BookingDto departureDate(String departureDate) {
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

  public BookingDto journey(String journey) {
    this.journey = journey;
    return this;
  }

  /**
   * Get journey
   * @return journey
   */
  @NotNull 
  @Schema(name = "journey", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("journey")
  public String getJourney() {
    return journey;
  }

  public void setJourney(String journey) {
    this.journey = journey;
  }

  public BookingDto language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   */
  
  @Schema(name = "language", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public BookingDto leadGuest(GuestDto leadGuest) {
    this.leadGuest = leadGuest;
    return this;
  }

  /**
   * Get leadGuest
   * @return leadGuest
   */
  @Valid 
  @Schema(name = "leadGuest", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("leadGuest")
  public GuestDto getLeadGuest() {
    return leadGuest;
  }

  public void setLeadGuest(GuestDto leadGuest) {
    this.leadGuest = leadGuest;
  }

  public BookingDto reference(String reference) {
    this.reference = reference;
    return this;
  }

  /**
   * Get reference
   * @return reference
   */
  
  @Schema(name = "reference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reference")
  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }

  public BookingDto rooms(List<@Valid RoomTypeDto> rooms) {
    this.rooms = rooms;
    return this;
  }

  public BookingDto addRoomsItem(RoomTypeDto roomsItem) {
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
  public List<@Valid RoomTypeDto> getRooms() {
    return rooms;
  }

  public void setRooms(List<@Valid RoomTypeDto> rooms) {
    this.rooms = rooms;
  }

  public BookingDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   */
  @NotNull 
  @Schema(name = "type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookingDto bookingDto = (BookingDto) o;
    return Objects.equals(this.arrivalDate, bookingDto.arrivalDate) &&
        Objects.equals(this.bookingReference, bookingDto.bookingReference) &&
        Objects.equals(this.businessSite, bookingDto.businessSite) &&
        Objects.equals(this.channel, bookingDto.channel) &&
        Objects.equals(this.departureDate, bookingDto.departureDate) &&
        Objects.equals(this.journey, bookingDto.journey) &&
        Objects.equals(this.language, bookingDto.language) &&
        Objects.equals(this.leadGuest, bookingDto.leadGuest) &&
        Objects.equals(this.reference, bookingDto.reference) &&
        Objects.equals(this.rooms, bookingDto.rooms) &&
        Objects.equals(this.type, bookingDto.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrivalDate, bookingReference, businessSite, channel, departureDate, journey, language, leadGuest, reference, rooms, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookingDto {\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    bookingReference: ").append(toIndentedString(bookingReference)).append("\n");
    sb.append("    businessSite: ").append(toIndentedString(businessSite)).append("\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    journey: ").append(toIndentedString(journey)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    leadGuest: ").append(toIndentedString(leadGuest)).append("\n");
    sb.append("    reference: ").append(toIndentedString(reference)).append("\n");
    sb.append("    rooms: ").append(toIndentedString(rooms)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

