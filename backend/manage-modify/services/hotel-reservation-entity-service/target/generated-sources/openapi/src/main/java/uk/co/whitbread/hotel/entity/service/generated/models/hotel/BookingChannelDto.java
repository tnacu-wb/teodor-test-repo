package uk.co.whitbread.hotel.entity.service.generated.models.hotel;

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
 * BookingChannelDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:33.749132+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookingChannelDto {

  private String channel;

  private String language;

  private String subchannel;

  public BookingChannelDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BookingChannelDto(String channel, String language, String subchannel) {
    this.channel = channel;
    this.language = language;
    this.subchannel = subchannel;
  }

  public BookingChannelDto channel(String channel) {
    this.channel = channel;
    return this;
  }

  /**
   * Get channel
   * @return channel
   */
  @NotNull 
  @Schema(name = "channel", example = "PI", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("channel")
  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public BookingChannelDto language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   */
  @NotNull 
  @Schema(name = "language", example = "EN", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public BookingChannelDto subchannel(String subchannel) {
    this.subchannel = subchannel;
    return this;
  }

  /**
   * Get subchannel
   * @return subchannel
   */
  @NotNull 
  @Schema(name = "subchannel", example = "WEB", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("subchannel")
  public String getSubchannel() {
    return subchannel;
  }

  public void setSubchannel(String subchannel) {
    this.subchannel = subchannel;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookingChannelDto bookingChannelDto = (BookingChannelDto) o;
    return Objects.equals(this.channel, bookingChannelDto.channel) &&
        Objects.equals(this.language, bookingChannelDto.language) &&
        Objects.equals(this.subchannel, bookingChannelDto.subchannel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channel, language, subchannel);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookingChannelDto {\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    subchannel: ").append(toIndentedString(subchannel)).append("\n");
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

