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
 * SocialLinksDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:06.327224+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class SocialLinksDto {

  private @Nullable String iconSrc;

  private @Nullable String label;

  private @Nullable String linkSrc;

  public SocialLinksDto iconSrc(String iconSrc) {
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

  public SocialLinksDto label(String label) {
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

  public SocialLinksDto linkSrc(String linkSrc) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SocialLinksDto socialLinksDto = (SocialLinksDto) o;
    return Objects.equals(this.iconSrc, socialLinksDto.iconSrc) &&
        Objects.equals(this.label, socialLinksDto.label) &&
        Objects.equals(this.linkSrc, socialLinksDto.linkSrc);
  }

  @Override
  public int hashCode() {
    return Objects.hash(iconSrc, label, linkSrc);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SocialLinksDto {\n");
    sb.append("    iconSrc: ").append(toIndentedString(iconSrc)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    linkSrc: ").append(toIndentedString(linkSrc)).append("\n");
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

