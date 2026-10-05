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
 * CookieGroupDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:27.565654+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CookieGroupDto {

  private @Nullable String cookieName;

  private @Nullable String description;

  private @Nullable Boolean isAlwaysActive;

  private @Nullable String title;

  private @Nullable String toggleLabel;

  public CookieGroupDto cookieName(String cookieName) {
    this.cookieName = cookieName;
    return this;
  }

  /**
   * Get cookieName
   * @return cookieName
   */
  
  @Schema(name = "cookieName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cookieName")
  public String getCookieName() {
    return cookieName;
  }

  public void setCookieName(String cookieName) {
    this.cookieName = cookieName;
  }

  public CookieGroupDto description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public CookieGroupDto isAlwaysActive(Boolean isAlwaysActive) {
    this.isAlwaysActive = isAlwaysActive;
    return this;
  }

  /**
   * Get isAlwaysActive
   * @return isAlwaysActive
   */
  
  @Schema(name = "isAlwaysActive", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isAlwaysActive")
  public Boolean getIsAlwaysActive() {
    return isAlwaysActive;
  }

  public void setIsAlwaysActive(Boolean isAlwaysActive) {
    this.isAlwaysActive = isAlwaysActive;
  }

  public CookieGroupDto title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   */
  
  @Schema(name = "title", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public CookieGroupDto toggleLabel(String toggleLabel) {
    this.toggleLabel = toggleLabel;
    return this;
  }

  /**
   * Get toggleLabel
   * @return toggleLabel
   */
  
  @Schema(name = "toggleLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("toggleLabel")
  public String getToggleLabel() {
    return toggleLabel;
  }

  public void setToggleLabel(String toggleLabel) {
    this.toggleLabel = toggleLabel;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CookieGroupDto cookieGroupDto = (CookieGroupDto) o;
    return Objects.equals(this.cookieName, cookieGroupDto.cookieName) &&
        Objects.equals(this.description, cookieGroupDto.description) &&
        Objects.equals(this.isAlwaysActive, cookieGroupDto.isAlwaysActive) &&
        Objects.equals(this.title, cookieGroupDto.title) &&
        Objects.equals(this.toggleLabel, cookieGroupDto.toggleLabel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cookieName, description, isAlwaysActive, title, toggleLabel);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CookieGroupDto {\n");
    sb.append("    cookieName: ").append(toIndentedString(cookieName)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    isAlwaysActive: ").append(toIndentedString(isAlwaysActive)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    toggleLabel: ").append(toIndentedString(toggleLabel)).append("\n");
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

