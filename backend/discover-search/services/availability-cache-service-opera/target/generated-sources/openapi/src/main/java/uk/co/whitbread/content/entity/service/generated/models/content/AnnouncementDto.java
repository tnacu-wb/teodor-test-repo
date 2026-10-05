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
 * AnnouncementDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AnnouncementDto {

  private @Nullable String browserCompatibilityMessage;

  private @Nullable String text;

  private @Nullable String type;

  public AnnouncementDto browserCompatibilityMessage(String browserCompatibilityMessage) {
    this.browserCompatibilityMessage = browserCompatibilityMessage;
    return this;
  }

  /**
   * Get browserCompatibilityMessage
   * @return browserCompatibilityMessage
   */
  
  @Schema(name = "browserCompatibilityMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("browserCompatibilityMessage")
  public String getBrowserCompatibilityMessage() {
    return browserCompatibilityMessage;
  }

  public void setBrowserCompatibilityMessage(String browserCompatibilityMessage) {
    this.browserCompatibilityMessage = browserCompatibilityMessage;
  }

  public AnnouncementDto text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Get text
   * @return text
   */
  
  @Schema(name = "text", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("text")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public AnnouncementDto type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   */
  
  @Schema(name = "type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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
    AnnouncementDto announcementDto = (AnnouncementDto) o;
    return Objects.equals(this.browserCompatibilityMessage, announcementDto.browserCompatibilityMessage) &&
        Objects.equals(this.text, announcementDto.text) &&
        Objects.equals(this.type, announcementDto.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(browserCompatibilityMessage, text, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AnnouncementDto {\n");
    sb.append("    browserCompatibilityMessage: ").append(toIndentedString(browserCompatibilityMessage)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
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

