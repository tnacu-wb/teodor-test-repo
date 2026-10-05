package uk.co.whitbread.basket.generated.models.rules;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RoomSubstitutionRequestDetailsDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.380951+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RoomSubstitutionRequestDetailsDto {

  private @Nullable Integer adults;

  private @Nullable String channel;

  private @Nullable Integer children;

  private @Nullable String pms;

  private @Nullable String roomType;

  public RoomSubstitutionRequestDetailsDto adults(Integer adults) {
    this.adults = adults;
    return this;
  }

  /**
   * Get adults
   * @return adults
   */
  
  @Schema(name = "adults", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("adults")
  public Integer getAdults() {
    return adults;
  }

  public void setAdults(Integer adults) {
    this.adults = adults;
  }

  public RoomSubstitutionRequestDetailsDto channel(String channel) {
    this.channel = channel;
    return this;
  }

  /**
   * Get channel
   * @return channel
   */
  
  @Schema(name = "channel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("channel")
  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public RoomSubstitutionRequestDetailsDto children(Integer children) {
    this.children = children;
    return this;
  }

  /**
   * Get children
   * @return children
   */
  
  @Schema(name = "children", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("children")
  public Integer getChildren() {
    return children;
  }

  public void setChildren(Integer children) {
    this.children = children;
  }

  public RoomSubstitutionRequestDetailsDto pms(String pms) {
    this.pms = pms;
    return this;
  }

  /**
   * Get pms
   * @return pms
   */
  
  @Schema(name = "pms", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pms")
  public String getPms() {
    return pms;
  }

  public void setPms(String pms) {
    this.pms = pms;
  }

  public RoomSubstitutionRequestDetailsDto roomType(String roomType) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoomSubstitutionRequestDetailsDto roomSubstitutionRequestDetailsDto = (RoomSubstitutionRequestDetailsDto) o;
    return Objects.equals(this.adults, roomSubstitutionRequestDetailsDto.adults) &&
        Objects.equals(this.channel, roomSubstitutionRequestDetailsDto.channel) &&
        Objects.equals(this.children, roomSubstitutionRequestDetailsDto.children) &&
        Objects.equals(this.pms, roomSubstitutionRequestDetailsDto.pms) &&
        Objects.equals(this.roomType, roomSubstitutionRequestDetailsDto.roomType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(adults, channel, children, pms, roomType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoomSubstitutionRequestDetailsDto {\n");
    sb.append("    adults: ").append(toIndentedString(adults)).append("\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    children: ").append(toIndentedString(children)).append("\n");
    sb.append("    pms: ").append(toIndentedString(pms)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
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

