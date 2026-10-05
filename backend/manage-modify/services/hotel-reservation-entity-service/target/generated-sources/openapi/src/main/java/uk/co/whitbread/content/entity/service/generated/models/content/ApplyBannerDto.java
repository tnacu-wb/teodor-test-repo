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
 * ApplyBannerDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ApplyBannerDto {

  private @Nullable String applyNowButton;

  private @Nullable String image;

  private @Nullable String linkAccountButton;

  private @Nullable String subtitle;

  private @Nullable String title;

  public ApplyBannerDto applyNowButton(String applyNowButton) {
    this.applyNowButton = applyNowButton;
    return this;
  }

  /**
   * Get applyNowButton
   * @return applyNowButton
   */
  
  @Schema(name = "applyNowButton", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("applyNowButton")
  public String getApplyNowButton() {
    return applyNowButton;
  }

  public void setApplyNowButton(String applyNowButton) {
    this.applyNowButton = applyNowButton;
  }

  public ApplyBannerDto image(String image) {
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

  public ApplyBannerDto linkAccountButton(String linkAccountButton) {
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

  public ApplyBannerDto subtitle(String subtitle) {
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

  public ApplyBannerDto title(String title) {
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
    ApplyBannerDto applyBannerDto = (ApplyBannerDto) o;
    return Objects.equals(this.applyNowButton, applyBannerDto.applyNowButton) &&
        Objects.equals(this.image, applyBannerDto.image) &&
        Objects.equals(this.linkAccountButton, applyBannerDto.linkAccountButton) &&
        Objects.equals(this.subtitle, applyBannerDto.subtitle) &&
        Objects.equals(this.title, applyBannerDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(applyNowButton, image, linkAccountButton, subtitle, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApplyBannerDto {\n");
    sb.append("    applyNowButton: ").append(toIndentedString(applyNowButton)).append("\n");
    sb.append("    image: ").append(toIndentedString(image)).append("\n");
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

