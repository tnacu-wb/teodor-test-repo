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
 * Availability status of the property. <p> AvailableForSale - Property has availability based on criteria requested</p> <p> NoAvailability - Property has no availability based on criteria requested </p> <p> NotFound - Property is invalid or can not be located  OtherAvailable - Other available property</p>
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public enum HotelAvailabilityStatus {
  
  AVAILABLE_FOR_SALE("AvailableForSale"),
  
  NO_AVAILABILITY("NoAvailability"),
  
  NOT_FOUND("NotFound"),
  
  OTHER_AVAILABLE("OtherAvailable");

  private String value;

  HotelAvailabilityStatus(String value) {
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
  public static HotelAvailabilityStatus fromValue(String value) {
    for (HotelAvailabilityStatus b : HotelAvailabilityStatus.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

