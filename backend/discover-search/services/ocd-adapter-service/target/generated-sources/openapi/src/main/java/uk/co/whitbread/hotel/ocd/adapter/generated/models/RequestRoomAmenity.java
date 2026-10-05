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
 * Request Amenity item
 */

@Schema(name = "RequestRoomAmenity", description = "Request Amenity item")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RequestRoomAmenity {

  private @Nullable String roomAmenity;

  private @Nullable Integer quantity;

  public RequestRoomAmenity roomAmenity(String roomAmenity) {
    this.roomAmenity = roomAmenity;
    return this;
  }

  /**
   * Descriptive ID of the room amenity
   * @return roomAmenity
   */
  
  @Schema(name = "roomAmenity", example = "Balcony", description = "Descriptive ID of the room amenity", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomAmenity")
  public String getRoomAmenity() {
    return roomAmenity;
  }

  public void setRoomAmenity(String roomAmenity) {
    this.roomAmenity = roomAmenity;
  }

  public RequestRoomAmenity quantity(Integer quantity) {
    this.quantity = quantity;
    return this;
  }

  /**
   * How many amenity item is requested
   * minimum: 1
   * maximum: 10
   * @return quantity
   */
  @Min(1) @Max(10) 
  @Schema(name = "quantity", example = "1", description = "How many amenity item is requested", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("quantity")
  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RequestRoomAmenity requestRoomAmenity = (RequestRoomAmenity) o;
    return Objects.equals(this.roomAmenity, requestRoomAmenity.roomAmenity) &&
        Objects.equals(this.quantity, requestRoomAmenity.quantity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(roomAmenity, quantity);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RequestRoomAmenity {\n");
    sb.append("    roomAmenity: ").append(toIndentedString(roomAmenity)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
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

