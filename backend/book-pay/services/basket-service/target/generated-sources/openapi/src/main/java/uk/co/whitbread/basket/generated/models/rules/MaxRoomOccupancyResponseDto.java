package uk.co.whitbread.basket.generated.models.rules;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.rules.MaxRoomOccupancyDataDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * MaxRoomOccupancyResponseDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.380951+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MaxRoomOccupancyResponseDto {

  private @Nullable String channelId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private @Nullable Date generatedAt;

  @Valid
  private List<@Valid MaxRoomOccupancyDataDto> roomOccupancies = new ArrayList<>();

  public MaxRoomOccupancyResponseDto channelId(String channelId) {
    this.channelId = channelId;
    return this;
  }

  /**
   * Get channelId
   * @return channelId
   */
  
  @Schema(name = "channelId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("channelId")
  public String getChannelId() {
    return channelId;
  }

  public void setChannelId(String channelId) {
    this.channelId = channelId;
  }

  public MaxRoomOccupancyResponseDto generatedAt(Date generatedAt) {
    this.generatedAt = generatedAt;
    return this;
  }

  /**
   * Get generatedAt
   * @return generatedAt
   */
  @Valid 
  @Schema(name = "generatedAt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("generatedAt")
  public Date getGeneratedAt() {
    return generatedAt;
  }

  public void setGeneratedAt(Date generatedAt) {
    this.generatedAt = generatedAt;
  }

  public MaxRoomOccupancyResponseDto roomOccupancies(List<@Valid MaxRoomOccupancyDataDto> roomOccupancies) {
    this.roomOccupancies = roomOccupancies;
    return this;
  }

  public MaxRoomOccupancyResponseDto addRoomOccupanciesItem(MaxRoomOccupancyDataDto roomOccupanciesItem) {
    if (this.roomOccupancies == null) {
      this.roomOccupancies = new ArrayList<>();
    }
    this.roomOccupancies.add(roomOccupanciesItem);
    return this;
  }

  /**
   * Get roomOccupancies
   * @return roomOccupancies
   */
  @Valid 
  @Schema(name = "roomOccupancies", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomOccupancies")
  public List<@Valid MaxRoomOccupancyDataDto> getRoomOccupancies() {
    return roomOccupancies;
  }

  public void setRoomOccupancies(List<@Valid MaxRoomOccupancyDataDto> roomOccupancies) {
    this.roomOccupancies = roomOccupancies;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MaxRoomOccupancyResponseDto maxRoomOccupancyResponseDto = (MaxRoomOccupancyResponseDto) o;
    return Objects.equals(this.channelId, maxRoomOccupancyResponseDto.channelId) &&
        Objects.equals(this.generatedAt, maxRoomOccupancyResponseDto.generatedAt) &&
        Objects.equals(this.roomOccupancies, maxRoomOccupancyResponseDto.roomOccupancies);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channelId, generatedAt, roomOccupancies);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MaxRoomOccupancyResponseDto {\n");
    sb.append("    channelId: ").append(toIndentedString(channelId)).append("\n");
    sb.append("    generatedAt: ").append(toIndentedString(generatedAt)).append("\n");
    sb.append("    roomOccupancies: ").append(toIndentedString(roomOccupancies)).append("\n");
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

