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
 * ContactDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ContactDto {

  private @Nullable String icon;

  private @Nullable String iconActive;

  private @Nullable String label;

  public ContactDto icon(String icon) {
    this.icon = icon;
    return this;
  }

  /**
   * Get icon
   * @return icon
   */
  
  @Schema(name = "icon", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("icon")
  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public ContactDto iconActive(String iconActive) {
    this.iconActive = iconActive;
    return this;
  }

  /**
   * Get iconActive
   * @return iconActive
   */
  
  @Schema(name = "iconActive", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("iconActive")
  public String getIconActive() {
    return iconActive;
  }

  public void setIconActive(String iconActive) {
    this.iconActive = iconActive;
  }

  public ContactDto label(String label) {
    this.label = label;
    return this;
  }

  /**
   * Get label
   * @return label
   */
  
  @Schema(name = "label", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("label")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContactDto contactDto = (ContactDto) o;
    return Objects.equals(this.icon, contactDto.icon) &&
        Objects.equals(this.iconActive, contactDto.iconActive) &&
        Objects.equals(this.label, contactDto.label);
  }

  @Override
  public int hashCode() {
    return Objects.hash(icon, iconActive, label);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContactDto {\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    iconActive: ").append(toIndentedString(iconActive)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
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

