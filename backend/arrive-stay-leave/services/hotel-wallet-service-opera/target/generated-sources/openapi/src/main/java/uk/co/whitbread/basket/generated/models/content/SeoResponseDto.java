package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.content.IconDto;
import uk.co.whitbread.basket.generated.models.content.MsIconDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * SeoResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:24.587145+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class SeoResponseDto {

  private @Nullable String cardImageUrl;

  private @Nullable String faviconUrl;

  @Valid
  private List<@Valid IconDto> icons = new ArrayList<>();

  @Valid
  private List<@Valid MsIconDto> msIcons = new ArrayList<>();

  private @Nullable String pageDescription;

  private @Nullable String pageTitle;

  public SeoResponseDto cardImageUrl(String cardImageUrl) {
    this.cardImageUrl = cardImageUrl;
    return this;
  }

  /**
   * Get cardImageUrl
   * @return cardImageUrl
   */
  
  @Schema(name = "cardImageUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardImageUrl")
  public String getCardImageUrl() {
    return cardImageUrl;
  }

  public void setCardImageUrl(String cardImageUrl) {
    this.cardImageUrl = cardImageUrl;
  }

  public SeoResponseDto faviconUrl(String faviconUrl) {
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

  public SeoResponseDto icons(List<@Valid IconDto> icons) {
    this.icons = icons;
    return this;
  }

  public SeoResponseDto addIconsItem(IconDto iconsItem) {
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

  public SeoResponseDto msIcons(List<@Valid MsIconDto> msIcons) {
    this.msIcons = msIcons;
    return this;
  }

  public SeoResponseDto addMsIconsItem(MsIconDto msIconsItem) {
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

  public SeoResponseDto pageDescription(String pageDescription) {
    this.pageDescription = pageDescription;
    return this;
  }

  /**
   * Get pageDescription
   * @return pageDescription
   */
  
  @Schema(name = "pageDescription", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageDescription")
  public String getPageDescription() {
    return pageDescription;
  }

  public void setPageDescription(String pageDescription) {
    this.pageDescription = pageDescription;
  }

  public SeoResponseDto pageTitle(String pageTitle) {
    this.pageTitle = pageTitle;
    return this;
  }

  /**
   * Get pageTitle
   * @return pageTitle
   */
  
  @Schema(name = "pageTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pageTitle")
  public String getPageTitle() {
    return pageTitle;
  }

  public void setPageTitle(String pageTitle) {
    this.pageTitle = pageTitle;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SeoResponseDto seoResponseDto = (SeoResponseDto) o;
    return Objects.equals(this.cardImageUrl, seoResponseDto.cardImageUrl) &&
        Objects.equals(this.faviconUrl, seoResponseDto.faviconUrl) &&
        Objects.equals(this.icons, seoResponseDto.icons) &&
        Objects.equals(this.msIcons, seoResponseDto.msIcons) &&
        Objects.equals(this.pageDescription, seoResponseDto.pageDescription) &&
        Objects.equals(this.pageTitle, seoResponseDto.pageTitle);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cardImageUrl, faviconUrl, icons, msIcons, pageDescription, pageTitle);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SeoResponseDto {\n");
    sb.append("    cardImageUrl: ").append(toIndentedString(cardImageUrl)).append("\n");
    sb.append("    faviconUrl: ").append(toIndentedString(faviconUrl)).append("\n");
    sb.append("    icons: ").append(toIndentedString(icons)).append("\n");
    sb.append("    msIcons: ").append(toIndentedString(msIcons)).append("\n");
    sb.append("    pageDescription: ").append(toIndentedString(pageDescription)).append("\n");
    sb.append("    pageTitle: ").append(toIndentedString(pageTitle)).append("\n");
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

