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
import uk.co.whitbread.hotel.ocd.adapter.generated.models.Occupancy;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferRoomTypeAvailabilityStatus;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.PropertyOffersRoomAmenity;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Room Type information
 */

@Schema(name = "PropertyOffersRoomType", description = "Room Type information")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PropertyOffersRoomType {

  private @Nullable OfferRoomTypeAvailabilityStatus availabilityStatus;

  private @Nullable String roomType;

  @Valid
  private List<String> description = new ArrayList<>();

  private @Nullable String roomName;

  private @Nullable String roomCategory;

  @Valid
  private List<@Valid PropertyOffersRoomAmenity> roomAmenities = new ArrayList<>();

  private @Nullable String roomViewType;

  private @Nullable String roomPrimaryBedType;

  private @Nullable Boolean adultOccupancyMatch;

  private @Nullable Boolean childOccupancyMatch;

  private @Nullable Integer numberOfUnits;

  private @Nullable Boolean nonSmokingInd;

  private @Nullable Occupancy occupancy;

  public PropertyOffersRoomType availabilityStatus(OfferRoomTypeAvailabilityStatus availabilityStatus) {
    this.availabilityStatus = availabilityStatus;
    return this;
  }

  /**
   * Get availabilityStatus
   * @return availabilityStatus
   */
  @Valid 
  @Schema(name = "availabilityStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("availabilityStatus")
  public OfferRoomTypeAvailabilityStatus getAvailabilityStatus() {
    return availabilityStatus;
  }

  public void setAvailabilityStatus(OfferRoomTypeAvailabilityStatus availabilityStatus) {
    this.availabilityStatus = availabilityStatus;
  }

  public PropertyOffersRoomType roomType(String roomType) {
    this.roomType = roomType;
    return this;
  }

  /**
   * The code for the room type.
   * @return roomType
   */
  
  @Schema(name = "roomType", example = "XA1K", description = "The code for the room type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomType")
  public String getRoomType() {
    return roomType;
  }

  public void setRoomType(String roomType) {
    this.roomType = roomType;
  }

  public PropertyOffersRoomType description(List<String> description) {
    this.description = description;
    return this;
  }

  public PropertyOffersRoomType addDescriptionItem(String descriptionItem) {
    if (this.description == null) {
      this.description = new ArrayList<>();
    }
    this.description.add(descriptionItem);
    return this;
  }

  /**
   * Description of the room type.
   * @return description
   */
  
  @Schema(name = "description", description = "Description of the room type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public List<String> getDescription() {
    return description;
  }

  public void setDescription(List<String> description) {
    this.description = description;
  }

  public PropertyOffersRoomType roomName(String roomName) {
    this.roomName = roomName;
    return this;
  }

  /**
   * Name of the room type.
   * @return roomName
   */
  
  @Schema(name = "roomName", example = "102", description = "Name of the room type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomName")
  public String getRoomName() {
    return roomName;
  }

  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }

  public PropertyOffersRoomType roomCategory(String roomCategory) {
    this.roomCategory = roomCategory;
    return this;
  }

  /**
   * Room category with which the room type is associated.
   * @return roomCategory
   */
  
  @Schema(name = "roomCategory", example = "SUITE", description = "Room category with which the room type is associated.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomCategory")
  public String getRoomCategory() {
    return roomCategory;
  }

  public void setRoomCategory(String roomCategory) {
    this.roomCategory = roomCategory;
  }

  public PropertyOffersRoomType roomAmenities(List<@Valid PropertyOffersRoomAmenity> roomAmenities) {
    this.roomAmenities = roomAmenities;
    return this;
  }

  public PropertyOffersRoomType addRoomAmenitiesItem(PropertyOffersRoomAmenity roomAmenitiesItem) {
    if (this.roomAmenities == null) {
      this.roomAmenities = new ArrayList<>();
    }
    this.roomAmenities.add(roomAmenitiesItem);
    return this;
  }

  /**
   * List of amenities for the room type.
   * @return roomAmenities
   */
  @Valid 
  @Schema(name = "roomAmenities", description = "List of amenities for the room type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomAmenities")
  public List<@Valid PropertyOffersRoomAmenity> getRoomAmenities() {
    return roomAmenities;
  }

  public void setRoomAmenities(List<@Valid PropertyOffersRoomAmenity> roomAmenities) {
    this.roomAmenities = roomAmenities;
  }

  public PropertyOffersRoomType roomViewType(String roomViewType) {
    this.roomViewType = roomViewType;
    return this;
  }

  /**
   * The type of view the room type offers.
   * @return roomViewType
   */
  
  @Schema(name = "roomViewType", example = "Ocean view", description = "The type of view the room type offers.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomViewType")
  public String getRoomViewType() {
    return roomViewType;
  }

  public void setRoomViewType(String roomViewType) {
    this.roomViewType = roomViewType;
  }

  public PropertyOffersRoomType roomPrimaryBedType(String roomPrimaryBedType) {
    this.roomPrimaryBedType = roomPrimaryBedType;
    return this;
  }

  /**
   * The primary bed type for the room type.
   * @return roomPrimaryBedType
   */
  
  @Schema(name = "roomPrimaryBedType", description = "The primary bed type for the room type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomPrimaryBedType")
  public String getRoomPrimaryBedType() {
    return roomPrimaryBedType;
  }

  public void setRoomPrimaryBedType(String roomPrimaryBedType) {
    this.roomPrimaryBedType = roomPrimaryBedType;
  }

  public PropertyOffersRoomType adultOccupancyMatch(Boolean adultOccupancyMatch) {
    this.adultOccupancyMatch = adultOccupancyMatch;
    return this;
  }

  /**
   * When true the room type matches the desired number of adult occupants.
   * @return adultOccupancyMatch
   */
  
  @Schema(name = "adultOccupancyMatch", example = "false", description = "When true the room type matches the desired number of adult occupants.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adultOccupancyMatch")
  public Boolean getAdultOccupancyMatch() {
    return adultOccupancyMatch;
  }

  public void setAdultOccupancyMatch(Boolean adultOccupancyMatch) {
    this.adultOccupancyMatch = adultOccupancyMatch;
  }

  public PropertyOffersRoomType childOccupancyMatch(Boolean childOccupancyMatch) {
    this.childOccupancyMatch = childOccupancyMatch;
    return this;
  }

  /**
   * When true the room type matches the desired number of child occupants.
   * @return childOccupancyMatch
   */
  
  @Schema(name = "childOccupancyMatch", example = "false", description = "When true the room type matches the desired number of child occupants.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("childOccupancyMatch")
  public Boolean getChildOccupancyMatch() {
    return childOccupancyMatch;
  }

  public void setChildOccupancyMatch(Boolean childOccupancyMatch) {
    this.childOccupancyMatch = childOccupancyMatch;
  }

  public PropertyOffersRoomType numberOfUnits(Integer numberOfUnits) {
    this.numberOfUnits = numberOfUnits;
    return this;
  }

  /**
   * The number of units for the room type.
   * @return numberOfUnits
   */
  
  @Schema(name = "numberOfUnits", example = "1", description = "The number of units for the room type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("numberOfUnits")
  public Integer getNumberOfUnits() {
    return numberOfUnits;
  }

  public void setNumberOfUnits(Integer numberOfUnits) {
    this.numberOfUnits = numberOfUnits;
  }

  public PropertyOffersRoomType nonSmokingInd(Boolean nonSmokingInd) {
    this.nonSmokingInd = nonSmokingInd;
    return this;
  }

  /**
   * When true the room type is non-smoking.
   * @return nonSmokingInd
   */
  
  @Schema(name = "nonSmokingInd", example = "true", description = "When true the room type is non-smoking.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nonSmokingInd")
  public Boolean getNonSmokingInd() {
    return nonSmokingInd;
  }

  public void setNonSmokingInd(Boolean nonSmokingInd) {
    this.nonSmokingInd = nonSmokingInd;
  }

  public PropertyOffersRoomType occupancy(Occupancy occupancy) {
    this.occupancy = occupancy;
    return this;
  }

  /**
   * Get occupancy
   * @return occupancy
   */
  @Valid 
  @Schema(name = "occupancy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("occupancy")
  public Occupancy getOccupancy() {
    return occupancy;
  }

  public void setOccupancy(Occupancy occupancy) {
    this.occupancy = occupancy;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PropertyOffersRoomType propertyOffersRoomType = (PropertyOffersRoomType) o;
    return Objects.equals(this.availabilityStatus, propertyOffersRoomType.availabilityStatus) &&
        Objects.equals(this.roomType, propertyOffersRoomType.roomType) &&
        Objects.equals(this.description, propertyOffersRoomType.description) &&
        Objects.equals(this.roomName, propertyOffersRoomType.roomName) &&
        Objects.equals(this.roomCategory, propertyOffersRoomType.roomCategory) &&
        Objects.equals(this.roomAmenities, propertyOffersRoomType.roomAmenities) &&
        Objects.equals(this.roomViewType, propertyOffersRoomType.roomViewType) &&
        Objects.equals(this.roomPrimaryBedType, propertyOffersRoomType.roomPrimaryBedType) &&
        Objects.equals(this.adultOccupancyMatch, propertyOffersRoomType.adultOccupancyMatch) &&
        Objects.equals(this.childOccupancyMatch, propertyOffersRoomType.childOccupancyMatch) &&
        Objects.equals(this.numberOfUnits, propertyOffersRoomType.numberOfUnits) &&
        Objects.equals(this.nonSmokingInd, propertyOffersRoomType.nonSmokingInd) &&
        Objects.equals(this.occupancy, propertyOffersRoomType.occupancy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(availabilityStatus, roomType, description, roomName, roomCategory, roomAmenities, roomViewType, roomPrimaryBedType, adultOccupancyMatch, childOccupancyMatch, numberOfUnits, nonSmokingInd, occupancy);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PropertyOffersRoomType {\n");
    sb.append("    availabilityStatus: ").append(toIndentedString(availabilityStatus)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    roomName: ").append(toIndentedString(roomName)).append("\n");
    sb.append("    roomCategory: ").append(toIndentedString(roomCategory)).append("\n");
    sb.append("    roomAmenities: ").append(toIndentedString(roomAmenities)).append("\n");
    sb.append("    roomViewType: ").append(toIndentedString(roomViewType)).append("\n");
    sb.append("    roomPrimaryBedType: ").append(toIndentedString(roomPrimaryBedType)).append("\n");
    sb.append("    adultOccupancyMatch: ").append(toIndentedString(adultOccupancyMatch)).append("\n");
    sb.append("    childOccupancyMatch: ").append(toIndentedString(childOccupancyMatch)).append("\n");
    sb.append("    numberOfUnits: ").append(toIndentedString(numberOfUnits)).append("\n");
    sb.append("    nonSmokingInd: ").append(toIndentedString(nonSmokingInd)).append("\n");
    sb.append("    occupancy: ").append(toIndentedString(occupancy)).append("\n");
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

