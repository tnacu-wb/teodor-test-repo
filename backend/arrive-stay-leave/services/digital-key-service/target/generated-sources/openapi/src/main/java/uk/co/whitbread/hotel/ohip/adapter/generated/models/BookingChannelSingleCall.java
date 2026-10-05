package uk.co.whitbread.hotel.ohip.adapter.generated.models;

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
 * BookingChannelSingleCall
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:50:34.031950+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookingChannelSingleCall {

  private @Nullable String channel;

  private @Nullable String language;

  private @Nullable String subchannel;

  public BookingChannelSingleCall channel(String channel) {
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

  public BookingChannelSingleCall language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   */
  
  @Schema(name = "language", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public BookingChannelSingleCall subchannel(String subchannel) {
    this.subchannel = subchannel;
    return this;
  }

  /**
   * Get subchannel
   * @return subchannel
   */
  
  @Schema(name = "subchannel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    BookingChannelSingleCall bookingChannelSingleCall = (BookingChannelSingleCall) o;
    return Objects.equals(this.channel, bookingChannelSingleCall.channel) &&
        Objects.equals(this.language, bookingChannelSingleCall.language) &&
        Objects.equals(this.subchannel, bookingChannelSingleCall.subchannel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channel, language, subchannel);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookingChannelSingleCall {\n");
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

