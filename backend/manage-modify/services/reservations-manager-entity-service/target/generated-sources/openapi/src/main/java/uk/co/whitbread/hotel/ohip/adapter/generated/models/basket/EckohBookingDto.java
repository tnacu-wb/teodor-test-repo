package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.EckohAgentDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.EckohBusinessSiteDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * EckohBookingDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:44.119190+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class EckohBookingDto {

  private EckohAgentDto agent;

  private EckohBusinessSiteDto businessSite;

  private String channel;

  private String journey;

  private String language;

  private String type;

  public EckohBookingDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EckohBookingDto(EckohAgentDto agent, EckohBusinessSiteDto businessSite, String channel, String journey, String language, String type) {
    this.agent = agent;
    this.businessSite = businessSite;
    this.channel = channel;
    this.journey = journey;
    this.language = language;
    this.type = type;
  }

  public EckohBookingDto agent(EckohAgentDto agent) {
    this.agent = agent;
    return this;
  }

  /**
   * Get agent
   * @return agent
   */
  @NotNull @Valid 
  @Schema(name = "agent", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("agent")
  public EckohAgentDto getAgent() {
    return agent;
  }

  public void setAgent(EckohAgentDto agent) {
    this.agent = agent;
  }

  public EckohBookingDto businessSite(EckohBusinessSiteDto businessSite) {
    this.businessSite = businessSite;
    return this;
  }

  /**
   * Get businessSite
   * @return businessSite
   */
  @NotNull @Valid 
  @Schema(name = "businessSite", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("businessSite")
  public EckohBusinessSiteDto getBusinessSite() {
    return businessSite;
  }

  public void setBusinessSite(EckohBusinessSiteDto businessSite) {
    this.businessSite = businessSite;
  }

  public EckohBookingDto channel(String channel) {
    this.channel = channel;
    return this;
  }

  /**
   * Get channel
   * @return channel
   */
  @NotNull 
  @Schema(name = "channel", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("channel")
  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public EckohBookingDto journey(String journey) {
    this.journey = journey;
    return this;
  }

  /**
   * Get journey
   * @return journey
   */
  @NotNull 
  @Schema(name = "journey", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("journey")
  public String getJourney() {
    return journey;
  }

  public void setJourney(String journey) {
    this.journey = journey;
  }

  public EckohBookingDto language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   */
  @NotNull 
  @Schema(name = "language", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public EckohBookingDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   */
  @NotNull 
  @Schema(name = "type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EckohBookingDto eckohBookingDto = (EckohBookingDto) o;
    return Objects.equals(this.agent, eckohBookingDto.agent) &&
        Objects.equals(this.businessSite, eckohBookingDto.businessSite) &&
        Objects.equals(this.channel, eckohBookingDto.channel) &&
        Objects.equals(this.journey, eckohBookingDto.journey) &&
        Objects.equals(this.language, eckohBookingDto.language) &&
        Objects.equals(this.type, eckohBookingDto.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(agent, businessSite, channel, journey, language, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EckohBookingDto {\n");
    sb.append("    agent: ").append(toIndentedString(agent)).append("\n");
    sb.append("    businessSite: ").append(toIndentedString(businessSite)).append("\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    journey: ").append(toIndentedString(journey)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

