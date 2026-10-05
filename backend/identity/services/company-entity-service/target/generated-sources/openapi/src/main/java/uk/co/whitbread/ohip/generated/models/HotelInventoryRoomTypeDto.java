package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.RoomLevelInventoryDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * HotelInventoryRoomTypeDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelInventoryRoomTypeDto {

  @Valid
  private List<@Valid RoomLevelInventoryDto> roomTypeInventories = new ArrayList<>();

  public HotelInventoryRoomTypeDto roomTypeInventories(List<@Valid RoomLevelInventoryDto> roomTypeInventories) {
    this.roomTypeInventories = roomTypeInventories;
    return this;
  }

  public HotelInventoryRoomTypeDto addRoomTypeInventoriesItem(RoomLevelInventoryDto roomTypeInventoriesItem) {
    if (this.roomTypeInventories == null) {
      this.roomTypeInventories = new ArrayList<>();
    }
    this.roomTypeInventories.add(roomTypeInventoriesItem);
    return this;
  }

  /**
   * Get roomTypeInventories
   * @return roomTypeInventories
   */
  @Valid 
  @Schema(name = "roomTypeInventories", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomTypeInventories")
  public List<@Valid RoomLevelInventoryDto> getRoomTypeInventories() {
    return roomTypeInventories;
  }

  public void setRoomTypeInventories(List<@Valid RoomLevelInventoryDto> roomTypeInventories) {
    this.roomTypeInventories = roomTypeInventories;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HotelInventoryRoomTypeDto hotelInventoryRoomTypeDto = (HotelInventoryRoomTypeDto) o;
    return Objects.equals(this.roomTypeInventories, hotelInventoryRoomTypeDto.roomTypeInventories);
  }

  @Override
  public int hashCode() {
    return Objects.hash(roomTypeInventories);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelInventoryRoomTypeDto {\n");
    sb.append("    roomTypeInventories: ").append(toIndentedString(roomTypeInventories)).append("\n");
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

