package uk.co.whitbread.rules.entity.service.generated.models.agent;

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
 * ChannelRuleRequestDetailsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:32.125466+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ChannelRuleRequestDetailsDto {

  private @Nullable String channel;

  private @Nullable String language;

  private @Nullable String pms;

  private @Nullable String subchannel;

  public ChannelRuleRequestDetailsDto channel(String channel) {
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

  public ChannelRuleRequestDetailsDto language(String language) {
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

  public ChannelRuleRequestDetailsDto pms(String pms) {
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

  public ChannelRuleRequestDetailsDto subchannel(String subchannel) {
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
    ChannelRuleRequestDetailsDto channelRuleRequestDetailsDto = (ChannelRuleRequestDetailsDto) o;
    return Objects.equals(this.channel, channelRuleRequestDetailsDto.channel) &&
        Objects.equals(this.language, channelRuleRequestDetailsDto.language) &&
        Objects.equals(this.pms, channelRuleRequestDetailsDto.pms) &&
        Objects.equals(this.subchannel, channelRuleRequestDetailsDto.subchannel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channel, language, pms, subchannel);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChannelRuleRequestDetailsDto {\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    pms: ").append(toIndentedString(pms)).append("\n");
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

