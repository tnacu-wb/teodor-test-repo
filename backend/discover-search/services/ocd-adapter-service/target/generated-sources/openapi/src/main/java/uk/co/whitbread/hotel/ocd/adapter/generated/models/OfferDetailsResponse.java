package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.HotelAvailabilityStatus;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.Offer;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferDetailsPropertyInfo;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferDetailsRatePlan;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferDetailsRoomType;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * OfferDetailsResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class OfferDetailsResponse {

  private @Nullable OfferDetailsPropertyInfo propertyInfo;

  private @Nullable HotelAvailabilityStatus availability;

  private @Nullable OfferDetailsRoomType roomType;

  private @Nullable OfferDetailsRatePlan ratePlan;

  private @Nullable Offer offer;

  public OfferDetailsResponse propertyInfo(OfferDetailsPropertyInfo propertyInfo) {
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
  public OfferDetailsPropertyInfo getPropertyInfo() {
    return propertyInfo;
  }

  public void setPropertyInfo(OfferDetailsPropertyInfo propertyInfo) {
    this.propertyInfo = propertyInfo;
  }

  public OfferDetailsResponse availability(HotelAvailabilityStatus availability) {
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

  public OfferDetailsResponse roomType(OfferDetailsRoomType roomType) {
    this.roomType = roomType;
    return this;
  }

  /**
   * Get roomType
   * @return roomType
   */
  @Valid 
  @Schema(name = "roomType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomType")
  public OfferDetailsRoomType getRoomType() {
    return roomType;
  }

  public void setRoomType(OfferDetailsRoomType roomType) {
    this.roomType = roomType;
  }

  public OfferDetailsResponse ratePlan(OfferDetailsRatePlan ratePlan) {
    this.ratePlan = ratePlan;
    return this;
  }

  /**
   * Get ratePlan
   * @return ratePlan
   */
  @Valid 
  @Schema(name = "ratePlan", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlan")
  public OfferDetailsRatePlan getRatePlan() {
    return ratePlan;
  }

  public void setRatePlan(OfferDetailsRatePlan ratePlan) {
    this.ratePlan = ratePlan;
  }

  public OfferDetailsResponse offer(Offer offer) {
    this.offer = offer;
    return this;
  }

  /**
   * Get offer
   * @return offer
   */
  @Valid 
  @Schema(name = "offer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("offer")
  public Offer getOffer() {
    return offer;
  }

  public void setOffer(Offer offer) {
    this.offer = offer;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OfferDetailsResponse offerDetailsResponse = (OfferDetailsResponse) o;
    return Objects.equals(this.propertyInfo, offerDetailsResponse.propertyInfo) &&
        Objects.equals(this.availability, offerDetailsResponse.availability) &&
        Objects.equals(this.roomType, offerDetailsResponse.roomType) &&
        Objects.equals(this.ratePlan, offerDetailsResponse.ratePlan) &&
        Objects.equals(this.offer, offerDetailsResponse.offer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(propertyInfo, availability, roomType, ratePlan, offer);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OfferDetailsResponse {\n");
    sb.append("    propertyInfo: ").append(toIndentedString(propertyInfo)).append("\n");
    sb.append("    availability: ").append(toIndentedString(availability)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
    sb.append("    ratePlan: ").append(toIndentedString(ratePlan)).append("\n");
    sb.append("    offer: ").append(toIndentedString(offer)).append("\n");
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

