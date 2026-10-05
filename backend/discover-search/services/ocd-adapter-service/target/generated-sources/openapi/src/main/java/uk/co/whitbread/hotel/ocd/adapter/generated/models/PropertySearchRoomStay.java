package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.HotelAvailabilityStatus;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferMinMaxTotalType;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.PropertySearchPropertyInfo;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.PropertySearchRatePlan;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PropertySearchRoomStay
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PropertySearchRoomStay {

  private @Nullable PropertySearchPropertyInfo propertyInfo;

  private @Nullable HotelAvailabilityStatus availability;

  @Valid
  private List<@Valid PropertySearchRatePlan> ratePlans = new ArrayList<>();

  private @Nullable OfferMinMaxTotalType minRate;

  private @Nullable OfferMinMaxTotalType maxRate;

  public PropertySearchRoomStay propertyInfo(PropertySearchPropertyInfo propertyInfo) {
    this.propertyInfo = propertyInfo;
    return this;
  }

  /**
   * Get propertyInfo
   * @return propertyInfo
   */
  @Valid 
  @Schema(name = "propertyInfo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("propertyInfo")
  public PropertySearchPropertyInfo getPropertyInfo() {
    return propertyInfo;
  }

  public void setPropertyInfo(PropertySearchPropertyInfo propertyInfo) {
    this.propertyInfo = propertyInfo;
  }

  public PropertySearchRoomStay availability(HotelAvailabilityStatus availability) {
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
  public HotelAvailabilityStatus getAvailability() {
    return availability;
  }

  public void setAvailability(HotelAvailabilityStatus availability) {
    this.availability = availability;
  }

  public PropertySearchRoomStay ratePlans(List<@Valid PropertySearchRatePlan> ratePlans) {
    this.ratePlans = ratePlans;
    return this;
  }

  public PropertySearchRoomStay addRatePlansItem(PropertySearchRatePlan ratePlansItem) {
    if (this.ratePlans == null) {
      this.ratePlans = new ArrayList<>();
    }
    this.ratePlans.add(ratePlansItem);
    return this;
  }

  /**
   * List of rate plans selected during the request including rate plan information, availability status of the rate plan, and commission.
   * @return ratePlans
   */
  @Valid 
  @Schema(name = "ratePlans", description = "List of rate plans selected during the request including rate plan information, availability status of the rate plan, and commission.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlans")
  public List<@Valid PropertySearchRatePlan> getRatePlans() {
    return ratePlans;
  }

  public void setRatePlans(List<@Valid PropertySearchRatePlan> ratePlans) {
    this.ratePlans = ratePlans;
  }

  public PropertySearchRoomStay minRate(OfferMinMaxTotalType minRate) {
    this.minRate = minRate;
    return this;
  }

  /**
   * Get minRate
   * @return minRate
   */
  @Valid 
  @Schema(name = "minRate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("minRate")
  public OfferMinMaxTotalType getMinRate() {
    return minRate;
  }

  public void setMinRate(OfferMinMaxTotalType minRate) {
    this.minRate = minRate;
  }

  public PropertySearchRoomStay maxRate(OfferMinMaxTotalType maxRate) {
    this.maxRate = maxRate;
    return this;
  }

  /**
   * Get maxRate
   * @return maxRate
   */
  @Valid 
  @Schema(name = "maxRate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("maxRate")
  public OfferMinMaxTotalType getMaxRate() {
    return maxRate;
  }

  public void setMaxRate(OfferMinMaxTotalType maxRate) {
    this.maxRate = maxRate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PropertySearchRoomStay propertySearchRoomStay = (PropertySearchRoomStay) o;
    return Objects.equals(this.propertyInfo, propertySearchRoomStay.propertyInfo) &&
        Objects.equals(this.availability, propertySearchRoomStay.availability) &&
        Objects.equals(this.ratePlans, propertySearchRoomStay.ratePlans) &&
        Objects.equals(this.minRate, propertySearchRoomStay.minRate) &&
        Objects.equals(this.maxRate, propertySearchRoomStay.maxRate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(propertyInfo, availability, ratePlans, minRate, maxRate);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PropertySearchRoomStay {\n");
    sb.append("    propertyInfo: ").append(toIndentedString(propertyInfo)).append("\n");
    sb.append("    availability: ").append(toIndentedString(availability)).append("\n");
    sb.append("    ratePlans: ").append(toIndentedString(ratePlans)).append("\n");
    sb.append("    minRate: ").append(toIndentedString(minRate)).append("\n");
    sb.append("    maxRate: ").append(toIndentedString(maxRate)).append("\n");
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

