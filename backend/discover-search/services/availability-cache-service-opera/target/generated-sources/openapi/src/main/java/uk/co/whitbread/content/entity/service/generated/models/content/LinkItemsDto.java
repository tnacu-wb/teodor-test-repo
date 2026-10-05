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
 * LinkItemsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class LinkItemsDto {

  private @Nullable String linkSrc;

  private @Nullable String name;

  private @Nullable Boolean openInNewTab;

  public LinkItemsDto linkSrc(String linkSrc) {
    this.linkSrc = linkSrc;
    return this;
  }

  /**
   * Get linkSrc
   * @return linkSrc
   */
  
  @Schema(name = "linkSrc", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("linkSrc")
  public String getLinkSrc() {
    return linkSrc;
  }

  public void setLinkSrc(String linkSrc) {
    this.linkSrc = linkSrc;
  }

  public LinkItemsDto name(String name) {
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

  public LinkItemsDto openInNewTab(Boolean openInNewTab) {
    this.openInNewTab = openInNewTab;
    return this;
  }

  /**
   * Get openInNewTab
   * @return openInNewTab
   */
  
  @Schema(name = "openInNewTab", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("openInNewTab")
  public Boolean getOpenInNewTab() {
    return openInNewTab;
  }

  public void setOpenInNewTab(Boolean openInNewTab) {
    this.openInNewTab = openInNewTab;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LinkItemsDto linkItemsDto = (LinkItemsDto) o;
    return Objects.equals(this.linkSrc, linkItemsDto.linkSrc) &&
        Objects.equals(this.name, linkItemsDto.name) &&
        Objects.equals(this.openInNewTab, linkItemsDto.openInNewTab);
  }

  @Override
  public int hashCode() {
    return Objects.hash(linkSrc, name, openInNewTab);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LinkItemsDto {\n");
    sb.append("    linkSrc: ").append(toIndentedString(linkSrc)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    openInNewTab: ").append(toIndentedString(openInNewTab)).append("\n");
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

