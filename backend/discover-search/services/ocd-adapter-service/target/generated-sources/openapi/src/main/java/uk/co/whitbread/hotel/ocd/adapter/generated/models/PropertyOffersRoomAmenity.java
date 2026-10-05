package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferRoomAmenityAvailabilityStatus;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PropertyOffersRoomAmenity
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PropertyOffersRoomAmenity {

  private @Nullable String roomAmenity;

  private @Nullable String description;

  private @Nullable OfferRoomAmenityAvailabilityStatus availabilityStatus;

  private @Nullable Integer quantity;

  private @Nullable Boolean includeInRate;

  private @Nullable Boolean confirmable;

  public PropertyOffersRoomAmenity roomAmenity(String roomAmenity) {
    this.roomAmenity = roomAmenity;
    return this;
  }

  /**
   * The code for an amenity offered with the room type.
   * @return roomAmenity
   */
  
  @Schema(name = "roomAmenity", description = "The code for an amenity offered with the room type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomAmenity")
  public String getRoomAmenity() {
    return roomAmenity;
  }

  public void setRoomAmenity(String roomAmenity) {
    this.roomAmenity = roomAmenity;
  }

  public PropertyOffersRoomAmenity description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Description of the amenity for the room type.
   * @return description
   */
  
  @Schema(name = "description", description = "Description of the amenity for the room type.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public PropertyOffersRoomAmenity availabilityStatus(OfferRoomAmenityAvailabilityStatus availabilityStatus) {
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
  public OfferRoomAmenityAvailabilityStatus getAvailabilityStatus() {
    return availabilityStatus;
  }

  public void setAvailabilityStatus(OfferRoomAmenityAvailabilityStatus availabilityStatus) {
    this.availabilityStatus = availabilityStatus;
  }

  public PropertyOffersRoomAmenity quantity(Integer quantity) {
    this.quantity = quantity;
    return this;
  }

  /**
   * The number of amenities within the room amenity code.
   * @return quantity
   */
  
  @Schema(name = "quantity", description = "The number of amenities within the room amenity code.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("quantity")
  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public PropertyOffersRoomAmenity includeInRate(Boolean includeInRate) {
    this.includeInRate = includeInRate;
    return this;
  }

  /**
   * When true indicates if the room amenity is included with the room rate.
   * @return includeInRate
   */
  
  @Schema(name = "includeInRate", description = "When true indicates if the room amenity is included with the room rate.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("includeInRate")
  public Boolean getIncludeInRate() {
    return includeInRate;
  }

  public void setIncludeInRate(Boolean includeInRate) {
    this.includeInRate = includeInRate;
  }

  public PropertyOffersRoomAmenity confirmable(Boolean confirmable) {
    this.confirmable = confirmable;
    return this;
  }

  /**
   * When true indicates there is a need to contact the property to confirm the availability of the room amenity.
   * @return confirmable
   */
  
  @Schema(name = "confirmable", description = "When true indicates there is a need to contact the property to confirm the availability of the room amenity.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("confirmable")
  public Boolean getConfirmable() {
    return confirmable;
  }

  public void setConfirmable(Boolean confirmable) {
    this.confirmable = confirmable;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PropertyOffersRoomAmenity propertyOffersRoomAmenity = (PropertyOffersRoomAmenity) o;
    return Objects.equals(this.roomAmenity, propertyOffersRoomAmenity.roomAmenity) &&
        Objects.equals(this.description, propertyOffersRoomAmenity.description) &&
        Objects.equals(this.availabilityStatus, propertyOffersRoomAmenity.availabilityStatus) &&
        Objects.equals(this.quantity, propertyOffersRoomAmenity.quantity) &&
        Objects.equals(this.includeInRate, propertyOffersRoomAmenity.includeInRate) &&
        Objects.equals(this.confirmable, propertyOffersRoomAmenity.confirmable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(roomAmenity, description, availabilityStatus, quantity, includeInRate, confirmable);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PropertyOffersRoomAmenity {\n");
    sb.append("    roomAmenity: ").append(toIndentedString(roomAmenity)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    availabilityStatus: ").append(toIndentedString(availabilityStatus)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
    sb.append("    includeInRate: ").append(toIndentedString(includeInRate)).append("\n");
    sb.append("    confirmable: ").append(toIndentedString(confirmable)).append("\n");
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

