package uk.co.whitbread.content.entity.service.generated.models.content;

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
 * WhyItemDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:46.057591+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class WhyItemDto {

  private @Nullable String itemDescription;

  private @Nullable String itemIcon;

  private @Nullable String itemTitle;

  public WhyItemDto itemDescription(String itemDescription) {
    this.itemDescription = itemDescription;
    return this;
  }

  /**
   * Get itemDescription
   * @return itemDescription
   */
  
  @Schema(name = "itemDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("itemDescription")
  public String getItemDescription() {
    return itemDescription;
  }

  public void setItemDescription(String itemDescription) {
    this.itemDescription = itemDescription;
  }

  public WhyItemDto itemIcon(String itemIcon) {
    this.itemIcon = itemIcon;
    return this;
  }

  /**
   * Get itemIcon
   * @return itemIcon
   */
  
  @Schema(name = "itemIcon", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("itemIcon")
  public String getItemIcon() {
    return itemIcon;
  }

  public void setItemIcon(String itemIcon) {
    this.itemIcon = itemIcon;
  }

  public WhyItemDto itemTitle(String itemTitle) {
    this.itemTitle = itemTitle;
    return this;
  }

  /**
   * Get itemTitle
   * @return itemTitle
   */
  
  @Schema(name = "itemTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("itemTitle")
  public String getItemTitle() {
    return itemTitle;
  }

  public void setItemTitle(String itemTitle) {
    this.itemTitle = itemTitle;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WhyItemDto whyItemDto = (WhyItemDto) o;
    return Objects.equals(this.itemDescription, whyItemDto.itemDescription) &&
        Objects.equals(this.itemIcon, whyItemDto.itemIcon) &&
        Objects.equals(this.itemTitle, whyItemDto.itemTitle);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemDescription, itemIcon, itemTitle);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WhyItemDto {\n");
    sb.append("    itemDescription: ").append(toIndentedString(itemDescription)).append("\n");
    sb.append("    itemIcon: ").append(toIndentedString(itemIcon)).append("\n");
    sb.append("    itemTitle: ").append(toIndentedString(itemTitle)).append("\n");
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

