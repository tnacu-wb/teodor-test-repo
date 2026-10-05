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
 * PromotionBannerDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PromotionBannerDto {

  private @Nullable String description;

  private @Nullable Boolean enabled;

  private @Nullable String icon;

  private @Nullable String srpNotificationText;

  private @Nullable String srpNotificationTitle;

  private @Nullable String terms;

  private @Nullable String title;

  public PromotionBannerDto description(String description) {
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

  public PromotionBannerDto enabled(Boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  /**
   * Get enabled
   * @return enabled
   */
  
  @Schema(name = "enabled", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("enabled")
  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public PromotionBannerDto icon(String icon) {
    this.icon = icon;
    return this;
  }

  /**
   * Get icon
   * @return icon
   */
  
  @Schema(name = "icon", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("icon")
  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public PromotionBannerDto srpNotificationText(String srpNotificationText) {
    this.srpNotificationText = srpNotificationText;
    return this;
  }

  /**
   * Get srpNotificationText
   * @return srpNotificationText
   */
  
  @Schema(name = "srpNotificationText", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("srpNotificationText")
  public String getSrpNotificationText() {
    return srpNotificationText;
  }

  public void setSrpNotificationText(String srpNotificationText) {
    this.srpNotificationText = srpNotificationText;
  }

  public PromotionBannerDto srpNotificationTitle(String srpNotificationTitle) {
    this.srpNotificationTitle = srpNotificationTitle;
    return this;
  }

  /**
   * Get srpNotificationTitle
   * @return srpNotificationTitle
   */
  
  @Schema(name = "srpNotificationTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("srpNotificationTitle")
  public String getSrpNotificationTitle() {
    return srpNotificationTitle;
  }

  public void setSrpNotificationTitle(String srpNotificationTitle) {
    this.srpNotificationTitle = srpNotificationTitle;
  }

  public PromotionBannerDto terms(String terms) {
    this.terms = terms;
    return this;
  }

  /**
   * Get terms
   * @return terms
   */
  
  @Schema(name = "terms", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("terms")
  public String getTerms() {
    return terms;
  }

  public void setTerms(String terms) {
    this.terms = terms;
  }

  public PromotionBannerDto title(String title) {
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
    PromotionBannerDto promotionBannerDto = (PromotionBannerDto) o;
    return Objects.equals(this.description, promotionBannerDto.description) &&
        Objects.equals(this.enabled, promotionBannerDto.enabled) &&
        Objects.equals(this.icon, promotionBannerDto.icon) &&
        Objects.equals(this.srpNotificationText, promotionBannerDto.srpNotificationText) &&
        Objects.equals(this.srpNotificationTitle, promotionBannerDto.srpNotificationTitle) &&
        Objects.equals(this.terms, promotionBannerDto.terms) &&
        Objects.equals(this.title, promotionBannerDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, enabled, icon, srpNotificationText, srpNotificationTitle, terms, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PromotionBannerDto {\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    enabled: ").append(toIndentedString(enabled)).append("\n");
    sb.append("    icon: ").append(toIndentedString(icon)).append("\n");
    sb.append("    srpNotificationText: ").append(toIndentedString(srpNotificationText)).append("\n");
    sb.append("    srpNotificationTitle: ").append(toIndentedString(srpNotificationTitle)).append("\n");
    sb.append("    terms: ").append(toIndentedString(terms)).append("\n");
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

