package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

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
 * ChannelInfoDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ChannelInfoDto {

  private @Nullable String channel;

  private @Nullable String subChannel;

  public ChannelInfoDto channel(String channel) {
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

  public ChannelInfoDto subChannel(String subChannel) {
    this.subChannel = subChannel;
    return this;
  }

  /**
   * Get subChannel
   * @return subChannel
   */
  
  @Schema(name = "subChannel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("subChannel")
  public String getSubChannel() {
    return subChannel;
  }

  public void setSubChannel(String subChannel) {
    this.subChannel = subChannel;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChannelInfoDto channelInfoDto = (ChannelInfoDto) o;
    return Objects.equals(this.channel, channelInfoDto.channel) &&
        Objects.equals(this.subChannel, channelInfoDto.subChannel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channel, subChannel);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChannelInfoDto {\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    subChannel: ").append(toIndentedString(subChannel)).append("\n");
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

