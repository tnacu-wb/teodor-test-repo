package uk.co.whitbread.basket.generated.models.marketing;

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
 * SourceDetails
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:09.701357+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class SourceDetails {

  private @Nullable String channel;

  private @Nullable String journey;

  private @Nullable String locale;

  public SourceDetails channel(String channel) {
    this.channel = channel;
    return this;
  }

  /**
   * Channel is the source from where Opt-In/Opt-Out requested
   * @return channel
   */
  
  @Schema(name = "channel", example = "WEB/BB/APPS_IOS/APPS_ANDROID", description = "Channel is the source from where Opt-In/Opt-Out requested", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("channel")
  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public SourceDetails journey(String journey) {
    this.journey = journey;
    return this;
  }

  /**
   * Journey is User flow from where Opt-In/Opt-Out requested
   * @return journey
   */
  
  @Schema(name = "journey", example = "PERMISSIONCENTRE/SIGNUP/NEWSLETTERSIGNUP/NEWSLETTERSIGNUP", description = "Journey is User flow from where Opt-In/Opt-Out requested", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("journey")
  public String getJourney() {
    return journey;
  }

  public void setJourney(String journey) {
    this.journey = journey;
  }

  public SourceDetails locale(String locale) {
    this.locale = locale;
    return this;
  }

  /**
   * Locale is the logical location from Opt-In/Opt-Out requested
   * @return locale
   */
  
  @Schema(name = "locale", example = "UK/DE", description = "Locale is the logical location from Opt-In/Opt-Out requested", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("locale")
  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SourceDetails sourceDetails = (SourceDetails) o;
    return Objects.equals(this.channel, sourceDetails.channel) &&
        Objects.equals(this.journey, sourceDetails.journey) &&
        Objects.equals(this.locale, sourceDetails.locale);
  }

  @Override
  public int hashCode() {
    return Objects.hash(channel, journey, locale);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SourceDetails {\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    journey: ").append(toIndentedString(journey)).append("\n");
    sb.append("    locale: ").append(toIndentedString(locale)).append("\n");
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

