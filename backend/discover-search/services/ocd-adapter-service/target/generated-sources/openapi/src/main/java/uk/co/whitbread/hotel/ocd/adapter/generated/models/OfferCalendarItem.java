package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferAvailabilityStatus;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferMinMaxTotalType;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * OfferCalendarItem
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferCalendarItem {

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate arrivalDate;

  private @Nullable OfferAvailabilityStatus availability;

  private @Nullable OfferMinMaxTotalType rate;

  public OfferCalendarItem arrivalDate(LocalDate arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Arrival date provided on the request, this will be used as the starting date to search for rates.
   * @return arrivalDate
   */
  @Valid 
  @Schema(name = "arrivalDate", example = "2021-06-01", description = "Arrival date provided on the request, this will be used as the starting date to search for rates.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arrivalDate")
  public LocalDate getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(LocalDate arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public OfferCalendarItem availability(OfferAvailabilityStatus availability) {
    this.availability = availability;
    return this;
  }

  /**
   * Get availability
   * @return availability
   */
  @Valid 
  @Schema(name = "availability", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("availability")
  public OfferAvailabilityStatus getAvailability() {
    return availability;
  }

  public void setAvailability(OfferAvailabilityStatus availability) {
    this.availability = availability;
  }

  public OfferCalendarItem rate(OfferMinMaxTotalType rate) {
    this.rate = rate;
    return this;
  }

  /**
   * Get rate
   * @return rate
   */
  @Valid 
  @Schema(name = "rate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rate")
  public OfferMinMaxTotalType getRate() {
    return rate;
  }

  public void setRate(OfferMinMaxTotalType rate) {
    this.rate = rate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferCalendarItem offerCalendarItem = (OfferCalendarItem) o;
    return Objects.equals(this.arrivalDate, offerCalendarItem.arrivalDate) &&
        Objects.equals(this.availability, offerCalendarItem.availability) &&
        Objects.equals(this.rate, offerCalendarItem.rate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrivalDate, availability, rate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferCalendarItem {\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    availability: ").append(toIndentedString(availability)).append("\n");
    sb.append("    rate: ").append(toIndentedString(rate)).append("\n");
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

