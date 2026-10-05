package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.content.RoomTypeInformationDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomTypeDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:06.327224+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomTypeDto {

  @Valid
  private List<@Valid RoomTypeInformationDto> roomTypes = new ArrayList<>();

  public RoomTypeDto roomTypes(List<@Valid RoomTypeInformationDto> roomTypes) {
    this.roomTypes = roomTypes;
    return this;
  }

  public RoomTypeDto addRoomTypesItem(RoomTypeInformationDto roomTypesItem) {
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
  public List<@Valid RoomTypeInformationDto> getRoomTypes() {
    return roomTypes;
  }

  public void setRoomTypes(List<@Valid RoomTypeInformationDto> roomTypes) {
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
    RoomTypeDto roomTypeDto = (RoomTypeDto) o;
    return Objects.equals(this.roomTypes, roomTypeDto.roomTypes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(roomTypes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomTypeDto {\n");
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

