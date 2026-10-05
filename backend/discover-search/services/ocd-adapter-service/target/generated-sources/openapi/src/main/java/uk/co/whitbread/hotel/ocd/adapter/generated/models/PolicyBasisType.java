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
 * The basis type used to compute the amount due. Nights -The  amount is based by the number of nights specified Percentage - The  amount is based on a percentage of the entire stay NightPercentage - The  amount is based on a percentage of the first night's rate FlatAmount - The  amount is based on exact amount specified FullAmount - The  amount is the full amount of the entire stay 
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public enum PolicyBasisType {
  
  NIGHTS("Nights"),
  
  PERCENTAGE("Percentage"),
  
  NIGHT_PERCENTAGE("NightPercentage"),
  
  FLAT_AMOUNT("FlatAmount"),
  
  FULL_AMOUNT("FullAmount");

  private String value;

  PolicyBasisType(String value) {
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
  public static PolicyBasisType fromValue(String value) {
    for (PolicyBasisType b : PolicyBasisType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

