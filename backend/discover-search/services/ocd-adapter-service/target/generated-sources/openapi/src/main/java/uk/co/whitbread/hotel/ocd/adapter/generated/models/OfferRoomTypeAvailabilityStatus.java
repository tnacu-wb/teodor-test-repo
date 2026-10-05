package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Indicates the availability status of the room type. <p> AvailableForSale - Room type is available for sale</p> <p> NoAvailability - Room type is not available for sale</p> <p> NotFound - Room type is invalid or can not be located</p> <p> BelowRequestedUnits - The selected room type units below the number of units requested</p>
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public enum OfferRoomTypeAvailabilityStatus {
  
  AVAILABLE_FOR_SALE("AvailableForSale"),
  
  NO_AVAILABILITY("NoAvailability"),
  
  NOT_FOUND("NotFound"),
  
  BELOW_REQUESTED_UNITS("BelowRequestedUnits");

  private String value;

  OfferRoomTypeAvailabilityStatus(String value) {
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
  public static OfferRoomTypeAvailabilityStatus fromValue(String value) {
    for (OfferRoomTypeAvailabilityStatus b : OfferRoomTypeAvailabilityStatus.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

