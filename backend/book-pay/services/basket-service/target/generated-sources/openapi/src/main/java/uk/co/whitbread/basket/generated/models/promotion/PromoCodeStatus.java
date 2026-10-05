package uk.co.whitbread.basket.generated.models.promotion;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets PromoCodeStatus
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:16.998949+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public enum PromoCodeStatus {
  
  ISSUED("ISSUED"),
  
  REDEEMED("REDEEMED"),
  
  EXPIRED("EXPIRED"),
  
  VOID("VOID");

  private String value;

  PromoCodeStatus(String value) {
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
  public static PromoCodeStatus fromValue(String value) {
    for (PromoCodeStatus b : PromoCodeStatus.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

