package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Holds restriction information that is restricting offer from being available
 */

@Schema(name = "Restriction", description = "Holds restriction information that is restricting offer from being available")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Restriction {

  /**
   * Type of restriction. <p> Closed: The offer is not restricted to be available.  </p> <p> ClosedForArrival: Offer is not available with arrival date with this restriction. </p> <p> ClosedForDeparture: Offer is not  available with departure date with this restriction. </p> <p> MinimumStayThrough: Offer duration falls in to this restriction date is not available if minimum number of nights less than set restriction nights. Example if minimum stay through is 2, the offer is not available for less than 2 nights stay. </p> <p> MaximumStayThrough: MaximumStayThrough: Offer duration falls in to this restriction date is not available if maximum number of nights are greater than set restriction nights. Example if maximum stay through is 10, the offer is not available for greater than 10 nights stay. </p> <p> MinimumAdvancedBooking: Offer is not available if arrival date and booking date window is less than this restriction setting. </p> <p> MaximumAdvancedBooking: Offer is not available if arrival date and booking date window is greater than this restriction setting. </p> <p> LOSNotAvailable: If the offer arrival date has this restriction, the offer is available for the duration where length of stay is not restricted. Example if length of stay not available set to Open, close, close, open, open, close, open. Offer arrival date with this restriction is available for number of nights, 1, 4,5, 7, and onwards.</p>
   */
  public enum RestrictionTypeEnum {
    CLOSED("Closed"),
    
    CLOSED_FOR_ARRIVAL("ClosedForArrival"),
    
    CLOSED_FOR_DEPARTURE("ClosedForDeparture"),
    
    MINIMUM_STAY_THROUGH("MinimumStayThrough"),
    
    MAXIMUM_STAY_THROUGH("MaximumStayThrough"),
    
    MINIMUM_LENGTH_OF_STAY("MinimumLengthOfStay"),
    
    MAXIMUM_LENGTH_OF_STAY("MaximumLengthOfStay"),
    
    MINIMUM_ADVANCED_BOOKING("MinimumAdvancedBooking"),
    
    MAXIMUM_ADVANCED_BOOKING("MaximumAdvancedBooking"),
    
    LOS_NOT_AVAILABLE("LOSNotAvailable");

    private String value;

    RestrictionTypeEnum(String value) {
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
    public static RestrictionTypeEnum fromValue(String value) {
      for (RestrictionTypeEnum b : RestrictionTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable RestrictionTypeEnum restrictionType;

  private @Nullable Integer restrictionValue;

  public Restriction restrictionType(RestrictionTypeEnum restrictionType) {
    this.restrictionType = restrictionType;
    return this;
  }

  /**
   * Type of restriction. <p> Closed: The offer is not restricted to be available.  </p> <p> ClosedForArrival: Offer is not available with arrival date with this restriction. </p> <p> ClosedForDeparture: Offer is not  available with departure date with this restriction. </p> <p> MinimumStayThrough: Offer duration falls in to this restriction date is not available if minimum number of nights less than set restriction nights. Example if minimum stay through is 2, the offer is not available for less than 2 nights stay. </p> <p> MaximumStayThrough: MaximumStayThrough: Offer duration falls in to this restriction date is not available if maximum number of nights are greater than set restriction nights. Example if maximum stay through is 10, the offer is not available for greater than 10 nights stay. </p> <p> MinimumAdvancedBooking: Offer is not available if arrival date and booking date window is less than this restriction setting. </p> <p> MaximumAdvancedBooking: Offer is not available if arrival date and booking date window is greater than this restriction setting. </p> <p> LOSNotAvailable: If the offer arrival date has this restriction, the offer is available for the duration where length of stay is not restricted. Example if length of stay not available set to Open, close, close, open, open, close, open. Offer arrival date with this restriction is available for number of nights, 1, 4,5, 7, and onwards.</p>
   * @return restrictionType
   */
  
  @Schema(name = "restrictionType", description = "Type of restriction. <p> Closed: The offer is not restricted to be available.  </p> <p> ClosedForArrival: Offer is not available with arrival date with this restriction. </p> <p> ClosedForDeparture: Offer is not  available with departure date with this restriction. </p> <p> MinimumStayThrough: Offer duration falls in to this restriction date is not available if minimum number of nights less than set restriction nights. Example if minimum stay through is 2, the offer is not available for less than 2 nights stay. </p> <p> MaximumStayThrough: MaximumStayThrough: Offer duration falls in to this restriction date is not available if maximum number of nights are greater than set restriction nights. Example if maximum stay through is 10, the offer is not available for greater than 10 nights stay. </p> <p> MinimumAdvancedBooking: Offer is not available if arrival date and booking date window is less than this restriction setting. </p> <p> MaximumAdvancedBooking: Offer is not available if arrival date and booking date window is greater than this restriction setting. </p> <p> LOSNotAvailable: If the offer arrival date has this restriction, the offer is available for the duration where length of stay is not restricted. Example if length of stay not available set to Open, close, close, open, open, close, open. Offer arrival date with this restriction is available for number of nights, 1, 4,5, 7, and onwards.</p>", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("restrictionType")
  public RestrictionTypeEnum getRestrictionType() {
    return restrictionType;
  }

  public void setRestrictionType(RestrictionTypeEnum restrictionType) {
    this.restrictionType = restrictionType;
  }

  public Restriction restrictionValue(Integer restrictionValue) {
    this.restrictionValue = restrictionValue;
    return this;
  }

  /**
   * The specific number of days for the selected restriction. This field applies to restriction type MinimumStayThrough, MaximumStayThrough, MinimumLengthOfStay, MaximumLengthOfStay, MinimumAdvancedBooking, and MaximumAdvancedBooking only.
   * @return restrictionValue
   */
  
  @Schema(name = "restrictionValue", example = "10", description = "The specific number of days for the selected restriction. This field applies to restriction type MinimumStayThrough, MaximumStayThrough, MinimumLengthOfStay, MaximumLengthOfStay, MinimumAdvancedBooking, and MaximumAdvancedBooking only.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("restrictionValue")
  public Integer getRestrictionValue() {
    return restrictionValue;
  }

  public void setRestrictionValue(Integer restrictionValue) {
    this.restrictionValue = restrictionValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Restriction restriction = (Restriction) o;
    return Objects.equals(this.restrictionType, restriction.restrictionType) &&
        Objects.equals(this.restrictionValue, restriction.restrictionValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(restrictionType, restrictionValue);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Restriction {\n");
    sb.append("    restrictionType: ").append(toIndentedString(restrictionType)).append("\n");
    sb.append("    restrictionValue: ").append(toIndentedString(restrictionValue)).append("\n");
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

