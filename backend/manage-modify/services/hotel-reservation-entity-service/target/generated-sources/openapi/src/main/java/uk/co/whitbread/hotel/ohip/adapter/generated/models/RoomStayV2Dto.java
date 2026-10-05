package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomTypeV2Dto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * RoomStayV2Dto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:15.274731+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomStayV2Dto {

  private @Nullable String roomClass;

  @Valid
  private List<@Valid RoomTypeV2Dto> roomTypes = new ArrayList<>();

  public RoomStayV2Dto roomClass(String roomClass) {
    this.roomClass = roomClass;
    return this;
  }

  /**
   * Get roomClass
   * @return roomClass
   */
  
  @Schema(name = "roomClass", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomClass")
  public String getRoomClass() {
    return roomClass;
  }

  public void setRoomClass(String roomClass) {
    this.roomClass = roomClass;
  }

  public RoomStayV2Dto roomTypes(List<@Valid RoomTypeV2Dto> roomTypes) {
    this.roomTypes = roomTypes;
    return this;
  }

  public RoomStayV2Dto addRoomTypesItem(RoomTypeV2Dto roomTypesItem) {
    if (this.roomTypes == null) {
      this.roomTypes = new ArrayList<>();
    }
    this.roomTypes.add(roomTypesItem);
    return this;
  }

  /**
   * Get roomTypes
   * @return roomTypes
   */
  @Valid 
  @Schema(name = "roomTypes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomTypes")
  public List<@Valid RoomTypeV2Dto> getRoomTypes() {
    return roomTypes;
  }

  public void setRoomTypes(List<@Valid RoomTypeV2Dto> roomTypes) {
    this.roomTypes = roomTypes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomStayV2Dto roomStayV2Dto = (RoomStayV2Dto) o;
    return Objects.equals(this.roomClass, roomStayV2Dto.roomClass) &&
        Objects.equals(this.roomTypes, roomStayV2Dto.roomTypes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(roomClass, roomTypes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomStayV2Dto {\n");
    sb.append("    roomClass: ").append(toIndentedString(roomClass)).append("\n");
    sb.append("    roomTypes: ").append(toIndentedString(roomTypes)).append("\n");
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

