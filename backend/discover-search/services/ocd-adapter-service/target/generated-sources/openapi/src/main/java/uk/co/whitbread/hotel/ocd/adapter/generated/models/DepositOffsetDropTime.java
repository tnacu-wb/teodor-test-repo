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
 * Defines as to when the deposit rule will take effect, works together with offsetTimeUnit and offsetMultiplier. <p>  <strong>BeforeArrival</strong> - The rule will take effect before arrival of what is specified in offsetTimeUnit and offsetMultiplier </p>  <strong>AfterBooking</strong> - The rule will take effect after booking of what is specified in offsetTimeUnit and offsetMultiplier</p>
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public enum DepositOffsetDropTime {
  
  BEFORE_ARRIVAL("BeforeArrival"),
  
  AFTER_BOOKING("AfterBooking");

  private String value;

  DepositOffsetDropTime(String value) {
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
  public static DepositOffsetDropTime fromValue(String value) {
    for (DepositOffsetDropTime b : DepositOffsetDropTime.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

