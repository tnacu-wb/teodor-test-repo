package uk.co.whitbread.basket.generated.models.reservation;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.reservation.AmendDistributionReservationDto;
import uk.co.whitbread.basket.generated.models.reservation.BookerDetailsCnpDto;
import uk.co.whitbread.basket.generated.models.reservation.BookingChannelDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AmendDistributionRequestDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:03.993735+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AmendDistributionRequestDto {

  private @Nullable BookerDetailsCnpDto bookerDetails;

  private BookingChannelDto bookingChannel;

  @Valid
  private List<String> bookingNotes = new ArrayList<>();

  @Valid
  private List<@Valid AmendDistributionReservationDto> reservations = new ArrayList<>();

  private @Nullable Boolean sendEmailConfirmation;

  private @Nullable Boolean sendEmailInvoice;

  private String token;

  public AmendDistributionRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AmendDistributionRequestDto(BookingChannelDto bookingChannel, String token) {
    this.bookingChannel = bookingChannel;
    this.token = token;
  }

  public AmendDistributionRequestDto bookerDetails(BookerDetailsCnpDto bookerDetails) {
    this.bookerDetails = bookerDetails;
    return this;
  }

  /**
   * Get bookerDetails
   * @return bookerDetails
   */
  @Valid 
  @Schema(name = "bookerDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookerDetails")
  public BookerDetailsCnpDto getBookerDetails() {
    return bookerDetails;
  }

  public void setBookerDetails(BookerDetailsCnpDto bookerDetails) {
    this.bookerDetails = bookerDetails;
  }

  public AmendDistributionRequestDto bookingChannel(BookingChannelDto bookingChannel) {
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

  public AmendDistributionRequestDto bookingNotes(List<String> bookingNotes) {
    this.bookingNotes = bookingNotes;
    return this;
  }

  public AmendDistributionRequestDto addBookingNotesItem(String bookingNotesItem) {
    if (this.bookingNotes == null) {
      this.bookingNotes = new ArrayList<>();
    }
    this.bookingNotes.add(bookingNotesItem);
    return this;
  }

  /**
   * Get bookingNotes
   * @return bookingNotes
   */
  
  @Schema(name = "bookingNotes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingNotes")
  public List<String> getBookingNotes() {
    return bookingNotes;
  }

  public void setBookingNotes(List<String> bookingNotes) {
    this.bookingNotes = bookingNotes;
  }

  public AmendDistributionRequestDto reservations(List<@Valid AmendDistributionReservationDto> reservations) {
    this.reservations = reservations;
    return this;
  }

  public AmendDistributionRequestDto addReservationsItem(AmendDistributionReservationDto reservationsItem) {
    if (this.reservations == null) {
      this.reservations = new ArrayList<>();
    }
    this.reservations.add(reservationsItem);
    return this;
  }

  /**
   * Get reservations
   * @return reservations
   */
  @Valid 
  @Schema(name = "reservations", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reservations")
  public List<@Valid AmendDistributionReservationDto> getReservations() {
    return reservations;
  }

  public void setReservations(List<@Valid AmendDistributionReservationDto> reservations) {
    this.reservations = reservations;
  }

  public AmendDistributionRequestDto sendEmailConfirmation(Boolean sendEmailConfirmation) {
    this.sendEmailConfirmation = sendEmailConfirmation;
    return this;
  }

  /**
   * Get sendEmailConfirmation
   * @return sendEmailConfirmation
   */
  
  @Schema(name = "sendEmailConfirmation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sendEmailConfirmation")
  public Boolean getSendEmailConfirmation() {
    return sendEmailConfirmation;
  }

  public void setSendEmailConfirmation(Boolean sendEmailConfirmation) {
    this.sendEmailConfirmation = sendEmailConfirmation;
  }

  public AmendDistributionRequestDto sendEmailInvoice(Boolean sendEmailInvoice) {
    this.sendEmailInvoice = sendEmailInvoice;
    return this;
  }

  /**
   * Get sendEmailInvoice
   * @return sendEmailInvoice
   */
  
  @Schema(name = "sendEmailInvoice", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sendEmailInvoice")
  public Boolean getSendEmailInvoice() {
    return sendEmailInvoice;
  }

  public void setSendEmailInvoice(Boolean sendEmailInvoice) {
    this.sendEmailInvoice = sendEmailInvoice;
  }

  public AmendDistributionRequestDto token(String token) {
    this.token = token;
    return this;
  }

  /**
   * Get token
   * @return token
   */
  @NotNull 
  @Schema(name = "token", requiredMode = Schema.RequiredMode.REQUIRED)
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
    AmendDistributionRequestDto amendDistributionRequestDto = (AmendDistributionRequestDto) o;
    return Objects.equals(this.bookerDetails, amendDistributionRequestDto.bookerDetails) &&
        Objects.equals(this.bookingChannel, amendDistributionRequestDto.bookingChannel) &&
        Objects.equals(this.bookingNotes, amendDistributionRequestDto.bookingNotes) &&
        Objects.equals(this.reservations, amendDistributionRequestDto.reservations) &&
        Objects.equals(this.sendEmailConfirmation, amendDistributionRequestDto.sendEmailConfirmation) &&
        Objects.equals(this.sendEmailInvoice, amendDistributionRequestDto.sendEmailInvoice) &&
        Objects.equals(this.token, amendDistributionRequestDto.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookerDetails, bookingChannel, bookingNotes, reservations, sendEmailConfirmation, sendEmailInvoice, token);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AmendDistributionRequestDto {\n");
    sb.append("    bookerDetails: ").append(toIndentedString(bookerDetails)).append("\n");
    sb.append("    bookingChannel: ").append(toIndentedString(bookingChannel)).append("\n");
    sb.append("    bookingNotes: ").append(toIndentedString(bookingNotes)).append("\n");
    sb.append("    reservations: ").append(toIndentedString(reservations)).append("\n");
    sb.append("    sendEmailConfirmation: ").append(toIndentedString(sendEmailConfirmation)).append("\n");
    sb.append("    sendEmailInvoice: ").append(toIndentedString(sendEmailInvoice)).append("\n");
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

