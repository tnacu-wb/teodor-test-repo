package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.WhyItemDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * WhyDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class WhyDto {

  private @Nullable String description;

  private @Nullable String picture;

  private @Nullable String title;

  @Valid
  private List<@Valid WhyItemDto> whyItems = new ArrayList<>();

  public WhyDto description(String description) {
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

  public WhyDto picture(String picture) {
    this.picture = picture;
    return this;
  }

  /**
   * Get picture
   * @return picture
   */
  
  @Schema(name = "picture", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("picture")
  public String getPicture() {
    return picture;
  }

  public void setPicture(String picture) {
    this.picture = picture;
  }

  public WhyDto title(String title) {
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

  public WhyDto whyItems(List<@Valid WhyItemDto> whyItems) {
    this.whyItems = whyItems;
    return this;
  }

  public WhyDto addWhyItemsItem(WhyItemDto whyItemsItem) {
    if (this.whyItems == null) {
      this.whyItems = new ArrayList<>();
    }
    this.whyItems.add(whyItemsItem);
    return this;
  }

  /**
   * Get whyItems
   * @return whyItems
   */
  @Valid 
  @Schema(name = "whyItems", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("whyItems")
  public List<@Valid WhyItemDto> getWhyItems() {
    return whyItems;
  }

  public void setWhyItems(List<@Valid WhyItemDto> whyItems) {
    this.whyItems = whyItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WhyDto whyDto = (WhyDto) o;
    return Objects.equals(this.description, whyDto.description) &&
        Objects.equals(this.picture, whyDto.picture) &&
        Objects.equals(this.title, whyDto.title) &&
        Objects.equals(this.whyItems, whyDto.whyItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, picture, title, whyItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WhyDto {\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    picture: ").append(toIndentedString(picture)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    whyItems: ").append(toIndentedString(whyItems)).append("\n");
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

