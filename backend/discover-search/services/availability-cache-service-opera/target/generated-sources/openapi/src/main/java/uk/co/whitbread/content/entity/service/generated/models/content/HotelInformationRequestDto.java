package uk.co.whitbread.content.entity.service.generated.models.content;

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
 * HotelInformationRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelInformationRequestDto {

  private @Nullable String channel;

  private String country;

  private String language;

  private @Nullable String subchannel;

  public HotelInformationRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public HotelInformationRequestDto(String country, String language) {
    this.country = country;
    this.language = language;
  }

  public HotelInformationRequestDto channel(String channel) {
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

  public HotelInformationRequestDto country(String country) {
    this.country = country;
    return this;
  }

  /**
   * Get country
   * @return country
   */
  @NotNull 
  @Schema(name = "country", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("country")
  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public HotelInformationRequestDto language(String language) {
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

  public HotelInformationRequestDto subchannel(String subchannel) {
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
    HotelInformationRequestDto hotelInformationRequestDto = (HotelInformationRequestDto) o;
    return Objects.equals(this.channel, hotelInformationRequestDto.channel) &&
        Objects.equals(this.country, hotelInformationRequestDto.country) &&
        Objects.equals(this.language, hotelInformationRequestDto.language) &&
        Objects.equals(this.subchannel, hotelInformationRequestDto.subchannel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channel, country, language, subchannel);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelInformationRequestDto {\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    country: ").append(toIndentedString(country)).append("\n");
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

