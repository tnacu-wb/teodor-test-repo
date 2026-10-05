package uk.co.whitbread.refund.processor.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.refund.processor.generated.models.payments.BusinessSiteDto;
import uk.co.whitbread.refund.processor.generated.models.payments.GuestDto;
import uk.co.whitbread.refund.processor.generated.models.payments.RoomDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * BookingDto
 */

@JsonTypeName("Booking")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:12:20.597747+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookingDto {

  private String channel;

  private String journey;

  private String type;

  private @Nullable String reference;

  private BusinessSiteDto businessSite;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate arrivalDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate departureDate;

  private @Nullable String language;

  private @Nullable GuestDto leadGuest;

  @Valid
  private List<@Valid RoomDto> rooms = new ArrayList<>();

  public BookingDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BookingDto(String channel, String journey, String type, BusinessSiteDto businessSite) {
    this.channel = channel;
    this.journey = journey;
    this.type = type;
    this.businessSite = businessSite;
  }

  public BookingDto channel(String channel) {
    this.channel = channel;
    return this;
  }

  /**
   * Type of booking channel that the payment is for.
   * @return channel
   */
  @NotNull 
  @Schema(name = "channel", example = "PI", description = "Type of booking channel that the payment is for.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("channel")
  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public BookingDto journey(String journey) {
    this.journey = journey;
    return this;
  }

  /**
   * Type of customer journey the payment is for.
   * @return journey
   */
  @NotNull 
  @Schema(name = "journey", example = "BOOKING", description = "Type of customer journey the payment is for.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("journey")
  public String getJourney() {
    return journey;
  }

  public void setJourney(String journey) {
    this.journey = journey;
  }

  public BookingDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Type of booking the payment is for.
   * @return type
   */
  @NotNull 
  @Schema(name = "type", example = "PAY_NOW", description = "Type of booking the payment is for.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public BookingDto reference(String reference) {
    this.reference = reference;
    return this;
  }

  /**
   * Booking reference.
   * @return reference
   */
  
  @Schema(name = "reference", example = "BR260692A", description = "Booking reference.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reference")
  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
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

  public BookingDto arrivalDate(LocalDate arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Arrival date of the booking.
   * @return arrivalDate
   */
  @Valid 
  @Schema(name = "arrivalDate", example = "2021-11-25", description = "Arrival date of the booking.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arrivalDate")
  public LocalDate getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(LocalDate arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public BookingDto departureDate(LocalDate departureDate) {
    this.departureDate = departureDate;
    return this;
  }

  /**
   * Departure date of the booking.
   * @return departureDate
   */
  @Valid 
  @Schema(name = "departureDate", example = "2021-11-26", description = "Departure date of the booking.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("departureDate")
  public LocalDate getDepartureDate() {
    return departureDate;
  }

  public void setDepartureDate(LocalDate departureDate) {
    this.departureDate = departureDate;
  }

  public BookingDto language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Chosen ISO 639-1 language of the customer making the booking. Only applicable for ECOMM payment type.
   * @return language
   */
  
  @Schema(name = "language", example = "en", description = "Chosen ISO 639-1 language of the customer making the booking. Only applicable for ECOMM payment type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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

  public BookingDto rooms(List<@Valid RoomDto> rooms) {
    this.rooms = rooms;
    return this;
  }

  public BookingDto addRoomsItem(RoomDto roomsItem) {
    if (this.rooms == null) {
      this.rooms = new ArrayList<>();
    }
    this.rooms.add(roomsItem);
    return this;
  }

  /**
   * Information on the rooms being booked.
   * @return rooms
   */
  @Valid 
  @Schema(name = "rooms", description = "Information on the rooms being booked.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rooms")
  public List<@Valid RoomDto> getRooms() {
    return rooms;
  }

  public void setRooms(List<@Valid RoomDto> rooms) {
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
    BookingDto booking = (BookingDto) o;
    return Objects.equals(this.channel, booking.channel) &&
        Objects.equals(this.journey, booking.journey) &&
        Objects.equals(this.type, booking.type) &&
        Objects.equals(this.reference, booking.reference) &&
        Objects.equals(this.businessSite, booking.businessSite) &&
        Objects.equals(this.arrivalDate, booking.arrivalDate) &&
        Objects.equals(this.departureDate, booking.departureDate) &&
        Objects.equals(this.language, booking.language) &&
        Objects.equals(this.leadGuest, booking.leadGuest) &&
        Objects.equals(this.rooms, booking.rooms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channel, journey, type, reference, businessSite, arrivalDate, departureDate, language, leadGuest, rooms);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookingDto {\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    journey: ").append(toIndentedString(journey)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    reference: ").append(toIndentedString(reference)).append("\n");
    sb.append("    businessSite: ").append(toIndentedString(businessSite)).append("\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    leadGuest: ").append(toIndentedString(leadGuest)).append("\n");
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

