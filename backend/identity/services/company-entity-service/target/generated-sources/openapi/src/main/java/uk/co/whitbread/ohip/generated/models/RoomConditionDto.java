package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.RoomConditionValueDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomConditionDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomConditionDto {

  private @Nullable RoomConditionValueDto roomCondition;

  public RoomConditionDto roomCondition(RoomConditionValueDto roomCondition) {
    this.roomCondition = roomCondition;
    return this;
  }

  /**
   * Get roomCondition
   * @return roomCondition
   */
  @Valid 
  @Schema(name = "roomCondition", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomCondition")
  public RoomConditionValueDto getRoomCondition() {
    return roomCondition;
  }

  public void setRoomCondition(RoomConditionValueDto roomCondition) {
    this.roomCondition = roomCondition;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomConditionDto roomConditionDto = (RoomConditionDto) o;
    return Objects.equals(this.roomCondition, roomConditionDto.roomCondition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(roomCondition);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomConditionDto {\n");
    sb.append("    roomCondition: ").append(toIndentedString(roomCondition)).append("\n");
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

