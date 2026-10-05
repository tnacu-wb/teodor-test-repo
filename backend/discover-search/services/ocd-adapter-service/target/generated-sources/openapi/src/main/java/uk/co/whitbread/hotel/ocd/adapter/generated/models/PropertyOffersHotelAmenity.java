package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PropertyOffersHotelAmenity
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PropertyOffersHotelAmenity {

  private @Nullable String hotelAmenity;

  private @Nullable String description;

  private @Nullable Integer quantity;

  private @Nullable Boolean includeInRate;

  private @Nullable Boolean confirmable;

  public PropertyOffersHotelAmenity hotelAmenity(String hotelAmenity) {
    this.hotelAmenity = hotelAmenity;
    return this;
  }

  /**
   * The code for an amenity offered at the property.
   * @return hotelAmenity
   */
  
  @Schema(name = "hotelAmenity", example = "12", description = "The code for an amenity offered at the property.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelAmenity")
  public String getHotelAmenity() {
    return hotelAmenity;
  }

  public void setHotelAmenity(String hotelAmenity) {
    this.hotelAmenity = hotelAmenity;
  }

  public PropertyOffersHotelAmenity description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Description of the amenity offered at the property.
   * @return description
   */
  
  @Schema(name = "description", example = "Swimming Pool", description = "Description of the amenity offered at the property.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public PropertyOffersHotelAmenity quantity(Integer quantity) {
    this.quantity = quantity;
    return this;
  }

  /**
   * The number of amenities within the property amenity code.
   * @return quantity
   */
  
  @Schema(name = "quantity", example = "1", description = "The number of amenities within the property amenity code.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("quantity")
  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public PropertyOffersHotelAmenity includeInRate(Boolean includeInRate) {
    this.includeInRate = includeInRate;
    return this;
  }

  /**
   * When true indicates the property amenity is included with the room rate.
   * @return includeInRate
   */
  
  @Schema(name = "includeInRate", example = "true", description = "When true indicates the property amenity is included with the room rate.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("includeInRate")
  public Boolean getIncludeInRate() {
    return includeInRate;
  }

  public void setIncludeInRate(Boolean includeInRate) {
    this.includeInRate = includeInRate;
  }

  public PropertyOffersHotelAmenity confirmable(Boolean confirmable) {
    this.confirmable = confirmable;
    return this;
  }

  /**
   * When true indicates there is a need to contact the property to confirm the availability of the property amenity.
   * @return confirmable
   */
  
  @Schema(name = "confirmable", example = "false", description = "When true indicates there is a need to contact the property to confirm the availability of the property amenity.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    PropertyOffersHotelAmenity propertyOffersHotelAmenity = (PropertyOffersHotelAmenity) o;
    return Objects.equals(this.hotelAmenity, propertyOffersHotelAmenity.hotelAmenity) &&
        Objects.equals(this.description, propertyOffersHotelAmenity.description) &&
        Objects.equals(this.quantity, propertyOffersHotelAmenity.quantity) &&
        Objects.equals(this.includeInRate, propertyOffersHotelAmenity.includeInRate) &&
        Objects.equals(this.confirmable, propertyOffersHotelAmenity.confirmable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(hotelAmenity, description, quantity, includeInRate, confirmable);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PropertyOffersHotelAmenity {\n");
    sb.append("    hotelAmenity: ").append(toIndentedString(hotelAmenity)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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

