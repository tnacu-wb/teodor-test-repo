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
 * Availability status of the rate plan.  AvailableForSale - Rate plan is available for sale  NoAvailability - Rate plan is not available for sale  NotFound - Rate plan is invalid or can't be located  Restricted - Rate plan is restricted 
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public enum OfferRatePlanAvailabilityStatus {
  
  AVAILABLE_FOR_SALE("AvailableForSale"),
  
  NO_AVAILABILITY("NoAvailability"),
  
  NOT_FOUND("NotFound"),
  
  RESTRICTED("Restricted");

  private String value;

  OfferRatePlanAvailabilityStatus(String value) {
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
  public static OfferRatePlanAvailabilityStatus fromValue(String value) {
    for (OfferRatePlanAvailabilityStatus b : OfferRatePlanAvailabilityStatus.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

