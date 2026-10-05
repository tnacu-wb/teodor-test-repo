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
import uk.co.whitbread.hotel.ocd.adapter.generated.models.Offer;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.PropertyOffersPropertyInfo;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.PropertyOffersRatePlan;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.PropertyOffersRoomType;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.Restriction;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PropertyOffersRoomStay
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PropertyOffersRoomStay {

  private @Nullable PropertyOffersPropertyInfo propertyInfo;

  private @Nullable HotelAvailabilityStatus availability;

  @Valid
  private List<@Valid Restriction> restrictions = new ArrayList<>();

  @Valid
  private List<@Valid PropertyOffersRoomType> roomTypes = new ArrayList<>();

  @Valid
  private List<PropertyOffersRatePlan> ratePlans = new ArrayList<>();

  @Valid
  private List<@Valid Offer> offers = new ArrayList<>();

  public PropertyOffersRoomStay propertyInfo(PropertyOffersPropertyInfo propertyInfo) {
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
  public PropertyOffersPropertyInfo getPropertyInfo() {
    return propertyInfo;
  }

  public void setPropertyInfo(PropertyOffersPropertyInfo propertyInfo) {
    this.propertyInfo = propertyInfo;
  }

  public PropertyOffersRoomStay availability(HotelAvailabilityStatus availability) {
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

  public PropertyOffersRoomStay restrictions(List<@Valid Restriction> restrictions) {
    this.restrictions = restrictions;
    return this;
  }

  public PropertyOffersRoomStay addRestrictionsItem(Restriction restrictionsItem) {
    if (this.restrictions == null) {
      this.restrictions = new ArrayList<>();
    }
    this.restrictions.add(restrictionsItem);
    return this;
  }

  /**
   * List of restrictions for property, only populated when no rate plan codes are given in request
   * @return restrictions
   */
  @Valid 
  @Schema(name = "restrictions", description = "List of restrictions for property, only populated when no rate plan codes are given in request", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("restrictions")
  public List<@Valid Restriction> getRestrictions() {
    return restrictions;
  }

  public void setRestrictions(List<@Valid Restriction> restrictions) {
    this.restrictions = restrictions;
  }

  public PropertyOffersRoomStay roomTypes(List<@Valid PropertyOffersRoomType> roomTypes) {
    this.roomTypes = roomTypes;
    return this;
  }

  public PropertyOffersRoomStay addRoomTypesItem(PropertyOffersRoomType roomTypesItem) {
    if (this.roomTypes == null) {
      this.roomTypes = new ArrayList<>();
    }
    this.roomTypes.add(roomTypesItem);
    return this;
  }

  /**
   * List of the room types selected during the request including room type information, availability status of the room type, and room amenities.
   * @return roomTypes
   */
  @Valid 
  @Schema(name = "roomTypes", description = "List of the room types selected during the request including room type information, availability status of the room type, and room amenities.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomTypes")
  public List<@Valid PropertyOffersRoomType> getRoomTypes() {
    return roomTypes;
  }

  public void setRoomTypes(List<@Valid PropertyOffersRoomType> roomTypes) {
    this.roomTypes = roomTypes;
  }

  public PropertyOffersRoomStay ratePlans(List<PropertyOffersRatePlan> ratePlans) {
    this.ratePlans = ratePlans;
    return this;
  }

  public PropertyOffersRoomStay addRatePlansItem(PropertyOffersRatePlan ratePlansItem) {
    if (this.ratePlans == null) {
      this.ratePlans = new ArrayList<>();
    }
    this.ratePlans.add(ratePlansItem);
    return this;
  }

  /**
   * List of the rate plans selected during the request including rate plan information, availability status of the rate plan, and commission.
   * @return ratePlans
   */
  @Valid 
  @Schema(name = "ratePlans", description = "List of the rate plans selected during the request including rate plan information, availability status of the rate plan, and commission.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlans")
  public List<PropertyOffersRatePlan> getRatePlans() {
    return ratePlans;
  }

  public void setRatePlans(List<PropertyOffersRatePlan> ratePlans) {
    this.ratePlans = ratePlans;
  }

  public PropertyOffersRoomStay offers(List<@Valid Offer> offers) {
    this.offers = offers;
    return this;
  }

  public PropertyOffersRoomStay addOffersItem(Offer offersItem) {
    if (this.offers == null) {
      this.offers = new ArrayList<>();
    }
    this.offers.add(offersItem);
    return this;
  }

  /**
   * List of offers at the property, including availability status of the offer and policies associated with the offer.
   * @return offers
   */
  @Valid 
  @Schema(name = "offers", description = "List of offers at the property, including availability status of the offer and policies associated with the offer.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("offers")
  public List<@Valid Offer> getOffers() {
    return offers;
  }

  public void setOffers(List<@Valid Offer> offers) {
    this.offers = offers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PropertyOffersRoomStay propertyOffersRoomStay = (PropertyOffersRoomStay) o;
    return Objects.equals(this.propertyInfo, propertyOffersRoomStay.propertyInfo) &&
        Objects.equals(this.availability, propertyOffersRoomStay.availability) &&
        Objects.equals(this.restrictions, propertyOffersRoomStay.restrictions) &&
        Objects.equals(this.roomTypes, propertyOffersRoomStay.roomTypes) &&
        Objects.equals(this.ratePlans, propertyOffersRoomStay.ratePlans) &&
        Objects.equals(this.offers, propertyOffersRoomStay.offers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(propertyInfo, availability, restrictions, roomTypes, ratePlans, offers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PropertyOffersRoomStay {\n");
    sb.append("    propertyInfo: ").append(toIndentedString(propertyInfo)).append("\n");
    sb.append("    availability: ").append(toIndentedString(availability)).append("\n");
    sb.append("    restrictions: ").append(toIndentedString(restrictions)).append("\n");
    sb.append("    roomTypes: ").append(toIndentedString(roomTypes)).append("\n");
    sb.append("    ratePlans: ").append(toIndentedString(ratePlans)).append("\n");
    sb.append("    offers: ").append(toIndentedString(offers)).append("\n");
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

