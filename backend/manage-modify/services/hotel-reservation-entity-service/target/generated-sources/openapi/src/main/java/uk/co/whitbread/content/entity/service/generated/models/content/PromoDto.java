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
 * PromoDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PromoDto {

  private @Nullable String description;

  private @Nullable String linkTarget;

  private @Nullable String linkText;

  private @Nullable String linkUrl;

  private @Nullable Integer order;

  private @Nullable String picture;

  private @Nullable String title;

  public PromoDto description(String description) {
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

  public PromoDto linkTarget(String linkTarget) {
    this.linkTarget = linkTarget;
    return this;
  }

  /**
   * Get linkTarget
   * @return linkTarget
   */
  
  @Schema(name = "linkTarget", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("linkTarget")
  public String getLinkTarget() {
    return linkTarget;
  }

  public void setLinkTarget(String linkTarget) {
    this.linkTarget = linkTarget;
  }

  public PromoDto linkText(String linkText) {
    this.linkText = linkText;
    return this;
  }

  /**
   * Get linkText
   * @return linkText
   */
  
  @Schema(name = "linkText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("linkText")
  public String getLinkText() {
    return linkText;
  }

  public void setLinkText(String linkText) {
    this.linkText = linkText;
  }

  public PromoDto linkUrl(String linkUrl) {
    this.linkUrl = linkUrl;
    return this;
  }

  /**
   * Get linkUrl
   * @return linkUrl
   */
  
  @Schema(name = "linkUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("linkUrl")
  public String getLinkUrl() {
    return linkUrl;
  }

  public void setLinkUrl(String linkUrl) {
    this.linkUrl = linkUrl;
  }

  public PromoDto order(Integer order) {
    this.order = order;
    return this;
  }

  /**
   * Get order
   * @return order
   */
  
  @Schema(name = "order", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("order")
  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public PromoDto picture(String picture) {
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

  public PromoDto title(String title) {
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
    PromoDto promoDto = (PromoDto) o;
    return Objects.equals(this.description, promoDto.description) &&
        Objects.equals(this.linkTarget, promoDto.linkTarget) &&
        Objects.equals(this.linkText, promoDto.linkText) &&
        Objects.equals(this.linkUrl, promoDto.linkUrl) &&
        Objects.equals(this.order, promoDto.order) &&
        Objects.equals(this.picture, promoDto.picture) &&
        Objects.equals(this.title, promoDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, linkTarget, linkText, linkUrl, order, picture, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PromoDto {\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    linkTarget: ").append(toIndentedString(linkTarget)).append("\n");
    sb.append("    linkText: ").append(toIndentedString(linkText)).append("\n");
    sb.append("    linkUrl: ").append(toIndentedString(linkUrl)).append("\n");
    sb.append("    order: ").append(toIndentedString(order)).append("\n");
    sb.append("    picture: ").append(toIndentedString(picture)).append("\n");
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

