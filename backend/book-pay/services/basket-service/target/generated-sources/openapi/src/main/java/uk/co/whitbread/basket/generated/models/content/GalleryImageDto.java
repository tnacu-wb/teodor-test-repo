package uk.co.whitbread.basket.generated.models.content;

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
 * GalleryImageDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:06.327224+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class GalleryImageDto {

  private @Nullable String alt;

  private @Nullable String caption;

  private @Nullable String iconSrc;

  private @Nullable String imageSrc;

  private @Nullable String thumbnailSrc;

  public GalleryImageDto alt(String alt) {
    this.alt = alt;
    return this;
  }

  /**
   * Get alt
   * @return alt
   */
  
  @Schema(name = "alt", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("alt")
  public String getAlt() {
    return alt;
  }

  public void setAlt(String alt) {
    this.alt = alt;
  }

  public GalleryImageDto caption(String caption) {
    this.caption = caption;
    return this;
  }

  /**
   * Get caption
   * @return caption
   */
  
  @Schema(name = "caption", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("caption")
  public String getCaption() {
    return caption;
  }

  public void setCaption(String caption) {
    this.caption = caption;
  }

  public GalleryImageDto iconSrc(String iconSrc) {
    this.iconSrc = iconSrc;
    return this;
  }

  /**
   * Get iconSrc
   * @return iconSrc
   */
  
  @Schema(name = "iconSrc", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("iconSrc")
  public String getIconSrc() {
    return iconSrc;
  }

  public void setIconSrc(String iconSrc) {
    this.iconSrc = iconSrc;
  }

  public GalleryImageDto imageSrc(String imageSrc) {
    this.imageSrc = imageSrc;
    return this;
  }

  /**
   * Get imageSrc
   * @return imageSrc
   */
  
  @Schema(name = "imageSrc", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("imageSrc")
  public String getImageSrc() {
    return imageSrc;
  }

  public void setImageSrc(String imageSrc) {
    this.imageSrc = imageSrc;
  }

  public GalleryImageDto thumbnailSrc(String thumbnailSrc) {
    this.thumbnailSrc = thumbnailSrc;
    return this;
  }

  /**
   * Get thumbnailSrc
   * @return thumbnailSrc
   */
  
  @Schema(name = "thumbnailSrc", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("thumbnailSrc")
  public String getThumbnailSrc() {
    return thumbnailSrc;
  }

  public void setThumbnailSrc(String thumbnailSrc) {
    this.thumbnailSrc = thumbnailSrc;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GalleryImageDto galleryImageDto = (GalleryImageDto) o;
    return Objects.equals(this.alt, galleryImageDto.alt) &&
        Objects.equals(this.caption, galleryImageDto.caption) &&
        Objects.equals(this.iconSrc, galleryImageDto.iconSrc) &&
        Objects.equals(this.imageSrc, galleryImageDto.imageSrc) &&
        Objects.equals(this.thumbnailSrc, galleryImageDto.thumbnailSrc);
  }

  @Override
  public int hashCode() {
    return Objects.hash(alt, caption, iconSrc, imageSrc, thumbnailSrc);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GalleryImageDto {\n");
    sb.append("    alt: ").append(toIndentedString(alt)).append("\n");
    sb.append("    caption: ").append(toIndentedString(caption)).append("\n");
    sb.append("    iconSrc: ").append(toIndentedString(iconSrc)).append("\n");
    sb.append("    imageSrc: ").append(toIndentedString(imageSrc)).append("\n");
    sb.append("    thumbnailSrc: ").append(toIndentedString(thumbnailSrc)).append("\n");
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

