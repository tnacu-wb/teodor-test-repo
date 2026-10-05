package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.IconDto;
import uk.co.whitbread.content.entity.service.generated.models.content.MsIconDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * FaviconDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class FaviconDto {

  private @Nullable String faviconUrl;

  @Valid
  private List<@Valid IconDto> icons = new ArrayList<>();

  @Valid
  private List<@Valid MsIconDto> msIcons = new ArrayList<>();

  public FaviconDto faviconUrl(String faviconUrl) {
    this.faviconUrl = faviconUrl;
    return this;
  }

  /**
   * Get faviconUrl
   * @return faviconUrl
   */
  
  @Schema(name = "faviconUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("faviconUrl")
  public String getFaviconUrl() {
    return faviconUrl;
  }

  public void setFaviconUrl(String faviconUrl) {
    this.faviconUrl = faviconUrl;
  }

  public FaviconDto icons(List<@Valid IconDto> icons) {
    this.icons = icons;
    return this;
  }

  public FaviconDto addIconsItem(IconDto iconsItem) {
    if (this.icons == null) {
      this.icons = new ArrayList<>();
    }
    this.icons.add(iconsItem);
    return this;
  }

  /**
   * Get icons
   * @return icons
   */
  @Valid 
  @Schema(name = "icons", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("icons")
  public List<@Valid IconDto> getIcons() {
    return icons;
  }

  public void setIcons(List<@Valid IconDto> icons) {
    this.icons = icons;
  }

  public FaviconDto msIcons(List<@Valid MsIconDto> msIcons) {
    this.msIcons = msIcons;
    return this;
  }

  public FaviconDto addMsIconsItem(MsIconDto msIconsItem) {
    if (this.msIcons == null) {
      this.msIcons = new ArrayList<>();
    }
    this.msIcons.add(msIconsItem);
    return this;
  }

  /**
   * Get msIcons
   * @return msIcons
   */
  @Valid 
  @Schema(name = "msIcons", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("msIcons")
  public List<@Valid MsIconDto> getMsIcons() {
    return msIcons;
  }

  public void setMsIcons(List<@Valid MsIconDto> msIcons) {
    this.msIcons = msIcons;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FaviconDto faviconDto = (FaviconDto) o;
    return Objects.equals(this.faviconUrl, faviconDto.faviconUrl) &&
        Objects.equals(this.icons, faviconDto.icons) &&
        Objects.equals(this.msIcons, faviconDto.msIcons);
  }

  @Override
  public int hashCode() {
    return Objects.hash(faviconUrl, icons, msIcons);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FaviconDto {\n");
    sb.append("    faviconUrl: ").append(toIndentedString(faviconUrl)).append("\n");
    sb.append("    icons: ").append(toIndentedString(icons)).append("\n");
    sb.append("    msIcons: ").append(toIndentedString(msIcons)).append("\n");
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

