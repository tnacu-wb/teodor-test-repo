package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.RoomClassOrderDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * RoomClassConfigDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:46.057591+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomClassConfigDto {

  @Valid
  private List<@Valid RoomClassOrderDto> roomClassConfig = new ArrayList<>();

  public RoomClassConfigDto roomClassConfig(List<@Valid RoomClassOrderDto> roomClassConfig) {
    this.roomClassConfig = roomClassConfig;
    return this;
  }

  public RoomClassConfigDto addRoomClassConfigItem(RoomClassOrderDto roomClassConfigItem) {
    if (this.roomClassConfig == null) {
      this.roomClassConfig = new ArrayList<>();
    }
    this.roomClassConfig.add(roomClassConfigItem);
    return this;
  }

  /**
   * Get roomClassConfig
   * @return roomClassConfig
   */
  @Valid 
  @Schema(name = "roomClassConfig", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomClassConfig")
  public List<@Valid RoomClassOrderDto> getRoomClassConfig() {
    return roomClassConfig;
  }

  public void setRoomClassConfig(List<@Valid RoomClassOrderDto> roomClassConfig) {
    this.roomClassConfig = roomClassConfig;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomClassConfigDto roomClassConfigDto = (RoomClassConfigDto) o;
    return Objects.equals(this.roomClassConfig, roomClassConfigDto.roomClassConfig);
  }

  @Override
  public int hashCode() {
    return Objects.hash(roomClassConfig);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomClassConfigDto {\n");
    sb.append("    roomClassConfig: ").append(toIndentedString(roomClassConfig)).append("\n");
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

