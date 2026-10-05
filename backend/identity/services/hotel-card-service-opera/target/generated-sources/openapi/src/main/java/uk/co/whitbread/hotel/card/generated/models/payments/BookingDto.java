package uk.co.whitbread.hotel.card.generated.models.payments;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.card.generated.models.payments.AgentDto;
import uk.co.whitbread.hotel.card.generated.models.payments.BusinessSiteDto;
import uk.co.whitbread.hotel.card.generated.models.payments.GuestDto;
import uk.co.whitbread.hotel.card.generated.models.payments.RoomDto;
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
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:52.919417+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookingDto {

  private @Nullable AgentDto agent;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate arrivalDate;

  private @Nullable String bookingReference;

  private BusinessSiteDto businessSite;

  /**
   * Type of booking channel that the payment is for.
   */
  public enum ChannelEnum {
    APPS_ANDROID("APPS_ANDROID"),
    
    APPS_IOS("APPS_IOS"),
    
    BB("BB"),
    
    CCC("CCC"),
    
    GDS("GDS"),
    
    PI("PI"),
    
    FRONT_DESK("FRONT_DESK"),
    
    WEB("WEB"),
    
    DISTR("DISTR");

    private String value;

    ChannelEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ChannelEnum fromValue(String value) {
      for (ChannelEnum b : ChannelEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private ChannelEnum channel;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate departureDate;

  /**
   * Type of customer journey the payment is for.
   */
  public enum JourneyEnum {
    BOOKING("BOOKING"),
    
    CIOL("CIOL"),
    
    AMEND("AMEND"),
    
    REFUND("REFUND"),
    
    SECURITY_CHECK("SECURITY_CHECK");

    private String value;

    JourneyEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static JourneyEnum fromValue(String value) {
      for (JourneyEnum b : JourneyEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private JourneyEnum journey;

  /**
   * Chosen ISO 639-1 language of the customer making the booking. Only applicable for ECOMM payment type.
   */
  public enum LanguageEnum {
    EN("en"),
    
    DE("de");

    private String value;

    LanguageEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static LanguageEnum fromValue(String value) {
      for (LanguageEnum b : LanguageEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable LanguageEnum language;

  private @Nullable GuestDto leadGuest;

  private @Nullable String reference;

  @Valid
  private List<@Valid RoomDto> rooms = new ArrayList<>();

  /**
   * Type of booking the payment is for.
   */
  public enum TypeEnum {
    PAY_NOW("PAY_NOW"),
    
    PAY_ON_ARRIVAL("PAY_ON_ARRIVAL");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static TypeEnum fromValue(String value) {
      for (TypeEnum b : TypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private TypeEnum type;

  public BookingDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BookingDto(BusinessSiteDto businessSite, ChannelEnum channel, JourneyEnum journey, TypeEnum type) {
    this.businessSite = businessSite;
    this.channel = channel;
    this.journey = journey;
    this.type = type;
  }

  public BookingDto agent(AgentDto agent) {
    this.agent = agent;
    return this;
  }

  /**
   * Get agent
   * @return agent
   */
  @Valid 
  @Schema(name = "agent", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("agent")
  public AgentDto getAgent() {
    return agent;
  }

  public void setAgent(AgentDto agent) {
    this.agent = agent;
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

  public BookingDto bookingReference(String bookingReference) {
    this.bookingReference = bookingReference;
    return this;
  }

  /**
   * Opera Booking reference.
   * @return bookingReference
   */
  
  @Schema(name = "bookingReference", example = "Opera non uuid basket ref GAA12345", description = "Opera Booking reference.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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

  public BookingDto channel(ChannelEnum channel) {
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
  public ChannelEnum getChannel() {
    return channel;
  }

  public void setChannel(ChannelEnum channel) {
    this.channel = channel;
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

  public BookingDto journey(JourneyEnum journey) {
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
  public JourneyEnum getJourney() {
    return journey;
  }

  public void setJourney(JourneyEnum journey) {
    this.journey = journey;
  }

  public BookingDto language(LanguageEnum language) {
    this.language = language;
    return this;
  }

  /**
   * Chosen ISO 639-1 language of the customer making the booking. Only applicable for ECOMM payment type.
   * @return language
   */
  
  @Schema(name = "language", example = "en", description = "Chosen ISO 639-1 language of the customer making the booking. Only applicable for ECOMM payment type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("language")
  public LanguageEnum getLanguage() {
    return language;
  }

  public void setLanguage(LanguageEnum language) {
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
   * Booking reference.
   * @return reference
   */
  
  @Schema(name = "reference", example = "BR260692A or Opera UUID", description = "Booking reference.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reference")
  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
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

  public BookingDto type(TypeEnum type) {
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
  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
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
    BookingDto booking = (BookingDto) o;
    return Objects.equals(this.agent, booking.agent) &&
        Objects.equals(this.arrivalDate, booking.arrivalDate) &&
        Objects.equals(this.bookingReference, booking.bookingReference) &&
        Objects.equals(this.businessSite, booking.businessSite) &&
        Objects.equals(this.channel, booking.channel) &&
        Objects.equals(this.departureDate, booking.departureDate) &&
        Objects.equals(this.journey, booking.journey) &&
        Objects.equals(this.language, booking.language) &&
        Objects.equals(this.leadGuest, booking.leadGuest) &&
        Objects.equals(this.reference, booking.reference) &&
        Objects.equals(this.rooms, booking.rooms) &&
        Objects.equals(this.type, booking.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(agent, arrivalDate, bookingReference, businessSite, channel, departureDate, journey, language, leadGuest, reference, rooms, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookingDto {\n");
    sb.append("    agent: ").append(toIndentedString(agent)).append("\n");
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

