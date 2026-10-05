package uk.co.whitbread.hotel.ohip.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CurrentRoomInfoDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CurrentRoomInfoDto {

  private @Nullable String roomId;

  private @Nullable String roomType;

  @Valid
  private @Nullable List<String> suggestedRoomNumbers;

  public CurrentRoomInfoDto roomId(String roomId) {
    this.roomId = roomId;
    return this;
  }

  /**
   * Get roomId
   * @return roomId
   */
  
  @Schema(name = "roomId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomId")
  public String getRoomId() {
    return roomId;
  }

  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }

  public CurrentRoomInfoDto roomType(String roomType) {
    this.roomType = roomType;
    return this;
  }

  /**
   * Get roomType
   * @return roomType
   */
  
  @Schema(name = "roomType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomType")
  public String getRoomType() {
    return roomType;
  }

  public void setRoomType(String roomType) {
    this.roomType = roomType;
  }

  public CurrentRoomInfoDto suggestedRoomNumbers(List<String> suggestedRoomNumbers) {
    this.suggestedRoomNumbers = suggestedRoomNumbers;
    return this;
  }

  public CurrentRoomInfoDto addSuggestedRoomNumbersItem(String suggestedRoomNumbersItem) {
    if (this.suggestedRoomNumbers == null) {
      this.suggestedRoomNumbers = new ArrayList<>();
    }
    this.suggestedRoomNumbers.add(suggestedRoomNumbersItem);
    return this;
  }

  /**
   * Get suggestedRoomNumbers
   * @return suggestedRoomNumbers
   */
  
  @Schema(name = "suggestedRoomNumbers", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("suggestedRoomNumbers")
  public List<String> getSuggestedRoomNumbers() {
    return suggestedRoomNumbers;
  }

  public void setSuggestedRoomNumbers(List<String> suggestedRoomNumbers) {
    this.suggestedRoomNumbers = suggestedRoomNumbers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CurrentRoomInfoDto currentRoomInfoDto = (CurrentRoomInfoDto) o;
    return Objects.equals(this.roomId, currentRoomInfoDto.roomId) &&
        Objects.equals(this.roomType, currentRoomInfoDto.roomType) &&
        Objects.equals(this.suggestedRoomNumbers, currentRoomInfoDto.suggestedRoomNumbers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(roomId, roomType, suggestedRoomNumbers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CurrentRoomInfoDto {\n");
    sb.append("    roomId: ").append(toIndentedString(roomId)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
    sb.append("    suggestedRoomNumbers: ").append(toIndentedString(suggestedRoomNumbers)).append("\n");
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

