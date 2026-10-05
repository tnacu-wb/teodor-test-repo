package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.BannerDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * BusinessAccountCardDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BusinessAccountCardDto {

  private @Nullable BannerDto banner;

  private @Nullable String buttonLabel;

  private @Nullable String tab;

  private @Nullable String tabMobile;

  private @Nullable String textBody;

  private @Nullable String title;

  public BusinessAccountCardDto banner(BannerDto banner) {
    this.banner = banner;
    return this;
  }

  /**
   * Get banner
   * @return banner
   */
  @Valid 
  @Schema(name = "banner", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("banner")
  public BannerDto getBanner() {
    return banner;
  }

  public void setBanner(BannerDto banner) {
    this.banner = banner;
  }

  public BusinessAccountCardDto buttonLabel(String buttonLabel) {
    this.buttonLabel = buttonLabel;
    return this;
  }

  /**
   * Get buttonLabel
   * @return buttonLabel
   */
  
  @Schema(name = "buttonLabel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("buttonLabel")
  public String getButtonLabel() {
    return buttonLabel;
  }

  public void setButtonLabel(String buttonLabel) {
    this.buttonLabel = buttonLabel;
  }

  public BusinessAccountCardDto tab(String tab) {
    this.tab = tab;
    return this;
  }

  /**
   * Get tab
   * @return tab
   */
  
  @Schema(name = "tab", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tab")
  public String getTab() {
    return tab;
  }

  public void setTab(String tab) {
    this.tab = tab;
  }

  public BusinessAccountCardDto tabMobile(String tabMobile) {
    this.tabMobile = tabMobile;
    return this;
  }

  /**
   * Get tabMobile
   * @return tabMobile
   */
  
  @Schema(name = "tabMobile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tabMobile")
  public String getTabMobile() {
    return tabMobile;
  }

  public void setTabMobile(String tabMobile) {
    this.tabMobile = tabMobile;
  }

  public BusinessAccountCardDto textBody(String textBody) {
    this.textBody = textBody;
    return this;
  }

  /**
   * Get textBody
   * @return textBody
   */
  
  @Schema(name = "textBody", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("textBody")
  public String getTextBody() {
    return textBody;
  }

  public void setTextBody(String textBody) {
    this.textBody = textBody;
  }

  public BusinessAccountCardDto title(String title) {
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
    BusinessAccountCardDto businessAccountCardDto = (BusinessAccountCardDto) o;
    return Objects.equals(this.banner, businessAccountCardDto.banner) &&
        Objects.equals(this.buttonLabel, businessAccountCardDto.buttonLabel) &&
        Objects.equals(this.tab, businessAccountCardDto.tab) &&
        Objects.equals(this.tabMobile, businessAccountCardDto.tabMobile) &&
        Objects.equals(this.textBody, businessAccountCardDto.textBody) &&
        Objects.equals(this.title, businessAccountCardDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(banner, buttonLabel, tab, tabMobile, textBody, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BusinessAccountCardDto {\n");
    sb.append("    banner: ").append(toIndentedString(banner)).append("\n");
    sb.append("    buttonLabel: ").append(toIndentedString(buttonLabel)).append("\n");
    sb.append("    tab: ").append(toIndentedString(tab)).append("\n");
    sb.append("    tabMobile: ").append(toIndentedString(tabMobile)).append("\n");
    sb.append("    textBody: ").append(toIndentedString(textBody)).append("\n");
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

