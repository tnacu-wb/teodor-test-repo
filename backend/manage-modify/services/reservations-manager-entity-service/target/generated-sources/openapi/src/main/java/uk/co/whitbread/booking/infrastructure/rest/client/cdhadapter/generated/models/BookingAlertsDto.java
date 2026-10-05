package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.RateCapsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BookingAlertsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookingAlertsDto {

  @Valid
  private List<String> bookingAlertHotels = new ArrayList<>();

  private @Nullable Boolean dayOfArrival;

  private @Nullable String frequency;

  private @Nullable Boolean passThroughWeekend;

  private @Nullable RateCapsDto rateCaps;

  @Valid
  private List<String> recipientEmailAddresses = new ArrayList<>();

  private @Nullable Boolean weekendArrival;

  public BookingAlertsDto bookingAlertHotels(List<String> bookingAlertHotels) {
    this.bookingAlertHotels = bookingAlertHotels;
    return this;
  }

  public BookingAlertsDto addBookingAlertHotelsItem(String bookingAlertHotelsItem) {
    if (this.bookingAlertHotels == null) {
      this.bookingAlertHotels = new ArrayList<>();
    }
    this.bookingAlertHotels.add(bookingAlertHotelsItem);
    return this;
  }

  /**
   * Get bookingAlertHotels
   * @return bookingAlertHotels
   */
  
  @Schema(name = "bookingAlertHotels", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingAlertHotels")
  public List<String> getBookingAlertHotels() {
    return bookingAlertHotels;
  }

  public void setBookingAlertHotels(List<String> bookingAlertHotels) {
    this.bookingAlertHotels = bookingAlertHotels;
  }

  public BookingAlertsDto dayOfArrival(Boolean dayOfArrival) {
    this.dayOfArrival = dayOfArrival;
    return this;
  }

  /**
   * Get dayOfArrival
   * @return dayOfArrival
   */
  
  @Schema(name = "dayOfArrival", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dayOfArrival")
  public Boolean getDayOfArrival() {
    return dayOfArrival;
  }

  public void setDayOfArrival(Boolean dayOfArrival) {
    this.dayOfArrival = dayOfArrival;
  }

  public BookingAlertsDto frequency(String frequency) {
    this.frequency = frequency;
    return this;
  }

  /**
   * Get frequency
   * @return frequency
   */
  
  @Schema(name = "frequency", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("frequency")
  public String getFrequency() {
    return frequency;
  }

  public void setFrequency(String frequency) {
    this.frequency = frequency;
  }

  public BookingAlertsDto passThroughWeekend(Boolean passThroughWeekend) {
    this.passThroughWeekend = passThroughWeekend;
    return this;
  }

  /**
   * Get passThroughWeekend
   * @return passThroughWeekend
   */
  
  @Schema(name = "passThroughWeekend", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("passThroughWeekend")
  public Boolean getPassThroughWeekend() {
    return passThroughWeekend;
  }

  public void setPassThroughWeekend(Boolean passThroughWeekend) {
    this.passThroughWeekend = passThroughWeekend;
  }

  public BookingAlertsDto rateCaps(RateCapsDto rateCaps) {
    this.rateCaps = rateCaps;
    return this;
  }

  /**
   * Get rateCaps
   * @return rateCaps
   */
  @Valid 
  @Schema(name = "rateCaps", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateCaps")
  public RateCapsDto getRateCaps() {
    return rateCaps;
  }

  public void setRateCaps(RateCapsDto rateCaps) {
    this.rateCaps = rateCaps;
  }

  public BookingAlertsDto recipientEmailAddresses(List<String> recipientEmailAddresses) {
    this.recipientEmailAddresses = recipientEmailAddresses;
    return this;
  }

  public BookingAlertsDto addRecipientEmailAddressesItem(String recipientEmailAddressesItem) {
    if (this.recipientEmailAddresses == null) {
      this.recipientEmailAddresses = new ArrayList<>();
    }
    this.recipientEmailAddresses.add(recipientEmailAddressesItem);
    return this;
  }

  /**
   * Get recipientEmailAddresses
   * @return recipientEmailAddresses
   */
  
  @Schema(name = "recipientEmailAddresses", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("recipientEmailAddresses")
  public List<String> getRecipientEmailAddresses() {
    return recipientEmailAddresses;
  }

  public void setRecipientEmailAddresses(List<String> recipientEmailAddresses) {
    this.recipientEmailAddresses = recipientEmailAddresses;
  }

  public BookingAlertsDto weekendArrival(Boolean weekendArrival) {
    this.weekendArrival = weekendArrival;
    return this;
  }

  /**
   * Get weekendArrival
   * @return weekendArrival
   */
  
  @Schema(name = "weekendArrival", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("weekendArrival")
  public Boolean getWeekendArrival() {
    return weekendArrival;
  }

  public void setWeekendArrival(Boolean weekendArrival) {
    this.weekendArrival = weekendArrival;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookingAlertsDto bookingAlertsDto = (BookingAlertsDto) o;
    return Objects.equals(this.bookingAlertHotels, bookingAlertsDto.bookingAlertHotels) &&
        Objects.equals(this.dayOfArrival, bookingAlertsDto.dayOfArrival) &&
        Objects.equals(this.frequency, bookingAlertsDto.frequency) &&
        Objects.equals(this.passThroughWeekend, bookingAlertsDto.passThroughWeekend) &&
        Objects.equals(this.rateCaps, bookingAlertsDto.rateCaps) &&
        Objects.equals(this.recipientEmailAddresses, bookingAlertsDto.recipientEmailAddresses) &&
        Objects.equals(this.weekendArrival, bookingAlertsDto.weekendArrival);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingAlertHotels, dayOfArrival, frequency, passThroughWeekend, rateCaps, recipientEmailAddresses, weekendArrival);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookingAlertsDto {\n");
    sb.append("    bookingAlertHotels: ").append(toIndentedString(bookingAlertHotels)).append("\n");
    sb.append("    dayOfArrival: ").append(toIndentedString(dayOfArrival)).append("\n");
    sb.append("    frequency: ").append(toIndentedString(frequency)).append("\n");
    sb.append("    passThroughWeekend: ").append(toIndentedString(passThroughWeekend)).append("\n");
    sb.append("    rateCaps: ").append(toIndentedString(rateCaps)).append("\n");
    sb.append("    recipientEmailAddresses: ").append(toIndentedString(recipientEmailAddresses)).append("\n");
    sb.append("    weekendArrival: ").append(toIndentedString(weekendArrival)).append("\n");
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

