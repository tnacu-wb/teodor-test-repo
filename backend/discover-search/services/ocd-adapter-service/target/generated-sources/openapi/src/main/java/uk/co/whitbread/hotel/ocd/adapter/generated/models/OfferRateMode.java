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
 * Type of rate returned, useful on a request for multiple nights stay where multiple rates may apply during the stay. <p> Highest - The highest rate for the stay will be returned </p> <p> Average - The average rate for the stay will be returned </p> <p> Arrival - The first night's rate for the stay will be returned </p> <p> MostFrequent - The rate that occurs the highest number of times for the stay will be returned </p> 
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public enum OfferRateMode {
  
  HIGHEST("Highest"),
  
  AVERAGE("Average"),
  
  ARRIVAL("Arrival"),
  
  MOST_FREQUENT("MostFrequent");

  private String value;

  OfferRateMode(String value) {
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
  public static OfferRateMode fromValue(String value) {
    for (OfferRateMode b : OfferRateMode.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

