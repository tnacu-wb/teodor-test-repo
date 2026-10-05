package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.RoomConditionValue;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomCondition
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomCondition {

  private @Nullable RoomConditionValue roomCondition;

  public RoomCondition roomCondition(RoomConditionValue roomCondition) {
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
  public RoomConditionValue getRoomCondition() {
    return roomCondition;
  }

  public void setRoomCondition(RoomConditionValue roomCondition) {
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
    RoomCondition roomCondition = (RoomCondition) o;
    return Objects.equals(this.roomCondition, roomCondition.roomCondition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(roomCondition);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomCondition {\n");
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

