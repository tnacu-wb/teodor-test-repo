package uk.co.whitbread.payapp.generated.models.company;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.payapp.generated.models.company.PriceCapLocationsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * BookingAlertsDto
 */

@JsonTypeName("BookingAlerts")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:38.523560+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookingAlertsDto {

  @Valid
  private List<String> bookingAlertHotels = new ArrayList<>();

  private @Nullable Boolean dayOfArrival;

  /**
   * Gets or Sets frequency
   */
  public enum FrequencyEnum {
    N("N"),
    
    A("A"),
    
    D("D"),
    
    W("W"),
    
    M("M");

    private String value;

    FrequencyEnum(String value) {
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
    public static FrequencyEnum fromValue(String value) {
      for (FrequencyEnum b : FrequencyEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable FrequencyEnum frequency;

  private @Nullable Boolean passThroughWeekend;

  private @Nullable PriceCapLocationsDto rateCaps;

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

  public BookingAlertsDto frequency(FrequencyEnum frequency) {
    this.frequency = frequency;
    return this;
  }

  /**
   * Get frequency
   * @return frequency
   */
  
  @Schema(name = "frequency", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("frequency")
  public FrequencyEnum getFrequency() {
    return frequency;
  }

  public void setFrequency(FrequencyEnum frequency) {
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

  public BookingAlertsDto rateCaps(PriceCapLocationsDto rateCaps) {
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
  public PriceCapLocationsDto getRateCaps() {
    return rateCaps;
  }

  public void setRateCaps(PriceCapLocationsDto rateCaps) {
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
    BookingAlertsDto bookingAlerts = (BookingAlertsDto) o;
    return Objects.equals(this.bookingAlertHotels, bookingAlerts.bookingAlertHotels) &&
        Objects.equals(this.dayOfArrival, bookingAlerts.dayOfArrival) &&
        Objects.equals(this.frequency, bookingAlerts.frequency) &&
        Objects.equals(this.passThroughWeekend, bookingAlerts.passThroughWeekend) &&
        Objects.equals(this.rateCaps, bookingAlerts.rateCaps) &&
        Objects.equals(this.recipientEmailAddresses, bookingAlerts.recipientEmailAddresses) &&
        Objects.equals(this.weekendArrival, bookingAlerts.weekendArrival);
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

