package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.LinkTabsDto;
import uk.co.whitbread.content.entity.service.generated.models.content.NewsletterSignupDto;
import uk.co.whitbread.content.entity.service.generated.models.content.SocialLinksDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * FooterResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:27.565654+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class FooterResponseDto {

  private @Nullable String copyrightInfo;

  private @Nullable NewsletterSignupDto newsletterSignup;

  @Valid
  private List<@Valid SocialLinksDto> socialMediaIcons = new ArrayList<>();

  @Valid
  private List<@Valid LinkTabsDto> tabs = new ArrayList<>();

  public FooterResponseDto copyrightInfo(String copyrightInfo) {
    this.copyrightInfo = copyrightInfo;
    return this;
  }

  /**
   * Get copyrightInfo
   * @return copyrightInfo
   */
  
  @Schema(name = "copyrightInfo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("copyrightInfo")
  public String getCopyrightInfo() {
    return copyrightInfo;
  }

  public void setCopyrightInfo(String copyrightInfo) {
    this.copyrightInfo = copyrightInfo;
  }

  public FooterResponseDto newsletterSignup(NewsletterSignupDto newsletterSignup) {
    this.newsletterSignup = newsletterSignup;
    return this;
  }

  /**
   * Get newsletterSignup
   * @return newsletterSignup
   */
  @Valid 
  @Schema(name = "newsletterSignup", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("newsletterSignup")
  public NewsletterSignupDto getNewsletterSignup() {
    return newsletterSignup;
  }

  public void setNewsletterSignup(NewsletterSignupDto newsletterSignup) {
    this.newsletterSignup = newsletterSignup;
  }

  public FooterResponseDto socialMediaIcons(List<@Valid SocialLinksDto> socialMediaIcons) {
    this.socialMediaIcons = socialMediaIcons;
    return this;
  }

  public FooterResponseDto addSocialMediaIconsItem(SocialLinksDto socialMediaIconsItem) {
    if (this.socialMediaIcons == null) {
      this.socialMediaIcons = new ArrayList<>();
    }
    this.socialMediaIcons.add(socialMediaIconsItem);
    return this;
  }

  /**
   * Get socialMediaIcons
   * @return socialMediaIcons
   */
  @Valid 
  @Schema(name = "socialMediaIcons", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("socialMediaIcons")
  public List<@Valid SocialLinksDto> getSocialMediaIcons() {
    return socialMediaIcons;
  }

  public void setSocialMediaIcons(List<@Valid SocialLinksDto> socialMediaIcons) {
    this.socialMediaIcons = socialMediaIcons;
  }

  public FooterResponseDto tabs(List<@Valid LinkTabsDto> tabs) {
    this.tabs = tabs;
    return this;
  }

  public FooterResponseDto addTabsItem(LinkTabsDto tabsItem) {
    if (this.tabs == null) {
      this.tabs = new ArrayList<>();
    }
    this.tabs.add(tabsItem);
    return this;
  }

  /**
   * Get tabs
   * @return tabs
   */
  @Valid 
  @Schema(name = "tabs", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tabs")
  public List<@Valid LinkTabsDto> getTabs() {
    return tabs;
  }

  public void setTabs(List<@Valid LinkTabsDto> tabs) {
    this.tabs = tabs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FooterResponseDto footerResponseDto = (FooterResponseDto) o;
    return Objects.equals(this.copyrightInfo, footerResponseDto.copyrightInfo) &&
        Objects.equals(this.newsletterSignup, footerResponseDto.newsletterSignup) &&
        Objects.equals(this.socialMediaIcons, footerResponseDto.socialMediaIcons) &&
        Objects.equals(this.tabs, footerResponseDto.tabs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(copyrightInfo, newsletterSignup, socialMediaIcons, tabs);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FooterResponseDto {\n");
    sb.append("    copyrightInfo: ").append(toIndentedString(copyrightInfo)).append("\n");
    sb.append("    newsletterSignup: ").append(toIndentedString(newsletterSignup)).append("\n");
    sb.append("    socialMediaIcons: ").append(toIndentedString(socialMediaIcons)).append("\n");
    sb.append("    tabs: ").append(toIndentedString(tabs)).append("\n");
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

