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
 * LinkAccountBannerDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class LinkAccountBannerDto {

  private @Nullable String linkAccountButton;

  private @Nullable String subtitle;

  private @Nullable String title;

  public LinkAccountBannerDto linkAccountButton(String linkAccountButton) {
    this.linkAccountButton = linkAccountButton;
    return this;
  }

  /**
   * Get linkAccountButton
   * @return linkAccountButton
   */
  
  @Schema(name = "linkAccountButton", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("linkAccountButton")
  public String getLinkAccountButton() {
    return linkAccountButton;
  }

  public void setLinkAccountButton(String linkAccountButton) {
    this.linkAccountButton = linkAccountButton;
  }

  public LinkAccountBannerDto subtitle(String subtitle) {
    this.subtitle = subtitle;
    return this;
  }

  /**
   * Get subtitle
   * @return subtitle
   */
  
  @Schema(name = "subtitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("subtitle")
  public String getSubtitle() {
    return subtitle;
  }

  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
  }

  public LinkAccountBannerDto title(String title) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LinkAccountBannerDto linkAccountBannerDto = (LinkAccountBannerDto) o;
    return Objects.equals(this.linkAccountButton, linkAccountBannerDto.linkAccountButton) &&
        Objects.equals(this.subtitle, linkAccountBannerDto.subtitle) &&
        Objects.equals(this.title, linkAccountBannerDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(linkAccountButton, subtitle, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LinkAccountBannerDto {\n");
    sb.append("    linkAccountButton: ").append(toIndentedString(linkAccountButton)).append("\n");
    sb.append("    subtitle: ").append(toIndentedString(subtitle)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
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

