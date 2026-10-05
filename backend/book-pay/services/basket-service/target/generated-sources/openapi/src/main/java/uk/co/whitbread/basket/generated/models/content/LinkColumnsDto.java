package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.content.LinkItemsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * LinkColumnsDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:06.327224+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class LinkColumnsDto {

  @Valid
  private List<@Valid LinkItemsDto> linkItems = new ArrayList<>();

  private @Nullable String name;

  public LinkColumnsDto linkItems(List<@Valid LinkItemsDto> linkItems) {
    this.linkItems = linkItems;
    return this;
  }

  public LinkColumnsDto addLinkItemsItem(LinkItemsDto linkItemsItem) {
    if (this.linkItems == null) {
      this.linkItems = new ArrayList<>();
    }
    this.linkItems.add(linkItemsItem);
    return this;
  }

  /**
   * Get linkItems
   * @return linkItems
   */
  @Valid 
  @Schema(name = "linkItems", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("linkItems")
  public List<@Valid LinkItemsDto> getLinkItems() {
    return linkItems;
  }

  public void setLinkItems(List<@Valid LinkItemsDto> linkItems) {
    this.linkItems = linkItems;
  }

  public LinkColumnsDto name(String name) {
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
    LinkColumnsDto linkColumnsDto = (LinkColumnsDto) o;
    return Objects.equals(this.linkItems, linkColumnsDto.linkItems) &&
        Objects.equals(this.name, linkColumnsDto.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(linkItems, name);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LinkColumnsDto {\n");
    sb.append("    linkItems: ").append(toIndentedString(linkItems)).append("\n");
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

