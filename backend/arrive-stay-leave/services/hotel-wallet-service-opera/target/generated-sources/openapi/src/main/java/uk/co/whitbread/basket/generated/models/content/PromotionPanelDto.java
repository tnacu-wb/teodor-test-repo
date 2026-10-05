package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PromotionPanelDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:24.587145+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PromotionPanelDto {

  private @Nullable String description;

  @Valid
  private List<String> displayHotels = new ArrayList<>();

  private @Nullable String image;

  private @Nullable String linkLabel;

  private @Nullable String linkPath;

  private @Nullable String name;

  public PromotionPanelDto description(String description) {
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

  public PromotionPanelDto displayHotels(List<String> displayHotels) {
    this.displayHotels = displayHotels;
    return this;
  }

  public PromotionPanelDto addDisplayHotelsItem(String displayHotelsItem) {
    if (this.displayHotels == null) {
      this.displayHotels = new ArrayList<>();
    }
    this.displayHotels.add(displayHotelsItem);
    return this;
  }

  /**
   * Get displayHotels
   * @return displayHotels
   */
  
  @Schema(name = "displayHotels", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("displayHotels")
  public List<String> getDisplayHotels() {
    return displayHotels;
  }

  public void setDisplayHotels(List<String> displayHotels) {
    this.displayHotels = displayHotels;
  }

  public PromotionPanelDto image(String image) {
    this.image = image;
    return this;
  }

  /**
   * Get image
   * @return image
   */
  
  @Schema(name = "image", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("image")
  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public PromotionPanelDto linkLabel(String linkLabel) {
    this.linkLabel = linkLabel;
    return this;
  }

  /**
   * Get linkLabel
   * @return linkLabel
   */
  
  @Schema(name = "linkLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("linkLabel")
  public String getLinkLabel() {
    return linkLabel;
  }

  public void setLinkLabel(String linkLabel) {
    this.linkLabel = linkLabel;
  }

  public PromotionPanelDto linkPath(String linkPath) {
    this.linkPath = linkPath;
    return this;
  }

  /**
   * Get linkPath
   * @return linkPath
   */
  
  @Schema(name = "linkPath", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("linkPath")
  public String getLinkPath() {
    return linkPath;
  }

  public void setLinkPath(String linkPath) {
    this.linkPath = linkPath;
  }

  public PromotionPanelDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PromotionPanelDto promotionPanelDto = (PromotionPanelDto) o;
    return Objects.equals(this.description, promotionPanelDto.description) &&
        Objects.equals(this.displayHotels, promotionPanelDto.displayHotels) &&
        Objects.equals(this.image, promotionPanelDto.image) &&
        Objects.equals(this.linkLabel, promotionPanelDto.linkLabel) &&
        Objects.equals(this.linkPath, promotionPanelDto.linkPath) &&
        Objects.equals(this.name, promotionPanelDto.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, displayHotels, image, linkLabel, linkPath, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PromotionPanelDto {\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    displayHotels: ").append(toIndentedString(displayHotels)).append("\n");
    sb.append("    image: ").append(toIndentedString(image)).append("\n");
    sb.append("    linkLabel: ").append(toIndentedString(linkLabel)).append("\n");
    sb.append("    linkPath: ").append(toIndentedString(linkPath)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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

