package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.PromoBoxResponseDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PromotionsInformationResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PromotionsInformationResponseDto {

  private @Nullable String appPromoAmendMessage;

  private @Nullable String appPromoBannerSubtitle;

  private @Nullable String appPromoBannerTitle;

  private @Nullable String appPromoExpiredMessage;

  private @Nullable String appPromoInvalidMessage;

  private @Nullable Boolean isWithinPromoWindow;

  private @Nullable String landingPage;

  private @Nullable String promoAmendMessage;

  private @Nullable String promoBannerColour;

  private @Nullable String promoBannerIcon;

  private @Nullable String promoBannerSubtitle;

  private @Nullable String promoBannerTitle;

  private @Nullable PromoBoxResponseDto promoBox;

  private @Nullable String promoExpiredMessage;

  private @Nullable String promoInvalidMessage;

  /**
   * Gets or Sets promoKind
   */
  public enum PromoKindEnum {
    LANDING_PAGE("LANDING_PAGE"),
    
    SITE_WIDE("SITE_WIDE"),
    
    GENERIC("GENERIC"),
    
    UNIQUE("UNIQUE");

    private String value;

    PromoKindEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static PromoKindEnum fromValue(String value) {
      for (PromoKindEnum b : PromoKindEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private @Nullable PromoKindEnum promoKind;

  private @Nullable String promotionCode;

  private @Nullable Boolean showPromo;

  private @Nullable String termsLink;

  public PromotionsInformationResponseDto appPromoAmendMessage(String appPromoAmendMessage) {
    this.appPromoAmendMessage = appPromoAmendMessage;
    return this;
  }

  /**
   * Get appPromoAmendMessage
   * @return appPromoAmendMessage
   */
  
  @Schema(name = "appPromoAmendMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("appPromoAmendMessage")
  public String getAppPromoAmendMessage() {
    return appPromoAmendMessage;
  }

  public void setAppPromoAmendMessage(String appPromoAmendMessage) {
    this.appPromoAmendMessage = appPromoAmendMessage;
  }

  public PromotionsInformationResponseDto appPromoBannerSubtitle(String appPromoBannerSubtitle) {
    this.appPromoBannerSubtitle = appPromoBannerSubtitle;
    return this;
  }

  /**
   * Get appPromoBannerSubtitle
   * @return appPromoBannerSubtitle
   */
  
  @Schema(name = "appPromoBannerSubtitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("appPromoBannerSubtitle")
  public String getAppPromoBannerSubtitle() {
    return appPromoBannerSubtitle;
  }

  public void setAppPromoBannerSubtitle(String appPromoBannerSubtitle) {
    this.appPromoBannerSubtitle = appPromoBannerSubtitle;
  }

  public PromotionsInformationResponseDto appPromoBannerTitle(String appPromoBannerTitle) {
    this.appPromoBannerTitle = appPromoBannerTitle;
    return this;
  }

  /**
   * Get appPromoBannerTitle
   * @return appPromoBannerTitle
   */
  
  @Schema(name = "appPromoBannerTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("appPromoBannerTitle")
  public String getAppPromoBannerTitle() {
    return appPromoBannerTitle;
  }

  public void setAppPromoBannerTitle(String appPromoBannerTitle) {
    this.appPromoBannerTitle = appPromoBannerTitle;
  }

  public PromotionsInformationResponseDto appPromoExpiredMessage(String appPromoExpiredMessage) {
    this.appPromoExpiredMessage = appPromoExpiredMessage;
    return this;
  }

  /**
   * Get appPromoExpiredMessage
   * @return appPromoExpiredMessage
   */
  
  @Schema(name = "appPromoExpiredMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("appPromoExpiredMessage")
  public String getAppPromoExpiredMessage() {
    return appPromoExpiredMessage;
  }

  public void setAppPromoExpiredMessage(String appPromoExpiredMessage) {
    this.appPromoExpiredMessage = appPromoExpiredMessage;
  }

  public PromotionsInformationResponseDto appPromoInvalidMessage(String appPromoInvalidMessage) {
    this.appPromoInvalidMessage = appPromoInvalidMessage;
    return this;
  }

  /**
   * Get appPromoInvalidMessage
   * @return appPromoInvalidMessage
   */
  
  @Schema(name = "appPromoInvalidMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("appPromoInvalidMessage")
  public String getAppPromoInvalidMessage() {
    return appPromoInvalidMessage;
  }

  public void setAppPromoInvalidMessage(String appPromoInvalidMessage) {
    this.appPromoInvalidMessage = appPromoInvalidMessage;
  }

  public PromotionsInformationResponseDto isWithinPromoWindow(Boolean isWithinPromoWindow) {
    this.isWithinPromoWindow = isWithinPromoWindow;
    return this;
  }

  /**
   * Get isWithinPromoWindow
   * @return isWithinPromoWindow
   */
  
  @Schema(name = "isWithinPromoWindow", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isWithinPromoWindow")
  public Boolean getIsWithinPromoWindow() {
    return isWithinPromoWindow;
  }

  public void setIsWithinPromoWindow(Boolean isWithinPromoWindow) {
    this.isWithinPromoWindow = isWithinPromoWindow;
  }

  public PromotionsInformationResponseDto landingPage(String landingPage) {
    this.landingPage = landingPage;
    return this;
  }

  /**
   * Get landingPage
   * @return landingPage
   */
  
  @Schema(name = "landingPage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("landingPage")
  public String getLandingPage() {
    return landingPage;
  }

  public void setLandingPage(String landingPage) {
    this.landingPage = landingPage;
  }

  public PromotionsInformationResponseDto promoAmendMessage(String promoAmendMessage) {
    this.promoAmendMessage = promoAmendMessage;
    return this;
  }

  /**
   * Get promoAmendMessage
   * @return promoAmendMessage
   */
  
  @Schema(name = "promoAmendMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoAmendMessage")
  public String getPromoAmendMessage() {
    return promoAmendMessage;
  }

  public void setPromoAmendMessage(String promoAmendMessage) {
    this.promoAmendMessage = promoAmendMessage;
  }

  public PromotionsInformationResponseDto promoBannerColour(String promoBannerColour) {
    this.promoBannerColour = promoBannerColour;
    return this;
  }

  /**
   * Get promoBannerColour
   * @return promoBannerColour
   */
  
  @Schema(name = "promoBannerColour", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoBannerColour")
  public String getPromoBannerColour() {
    return promoBannerColour;
  }

  public void setPromoBannerColour(String promoBannerColour) {
    this.promoBannerColour = promoBannerColour;
  }

  public PromotionsInformationResponseDto promoBannerIcon(String promoBannerIcon) {
    this.promoBannerIcon = promoBannerIcon;
    return this;
  }

  /**
   * Get promoBannerIcon
   * @return promoBannerIcon
   */
  
  @Schema(name = "promoBannerIcon", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoBannerIcon")
  public String getPromoBannerIcon() {
    return promoBannerIcon;
  }

  public void setPromoBannerIcon(String promoBannerIcon) {
    this.promoBannerIcon = promoBannerIcon;
  }

  public PromotionsInformationResponseDto promoBannerSubtitle(String promoBannerSubtitle) {
    this.promoBannerSubtitle = promoBannerSubtitle;
    return this;
  }

  /**
   * Get promoBannerSubtitle
   * @return promoBannerSubtitle
   */
  
  @Schema(name = "promoBannerSubtitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoBannerSubtitle")
  public String getPromoBannerSubtitle() {
    return promoBannerSubtitle;
  }

  public void setPromoBannerSubtitle(String promoBannerSubtitle) {
    this.promoBannerSubtitle = promoBannerSubtitle;
  }

  public PromotionsInformationResponseDto promoBannerTitle(String promoBannerTitle) {
    this.promoBannerTitle = promoBannerTitle;
    return this;
  }

  /**
   * Get promoBannerTitle
   * @return promoBannerTitle
   */
  
  @Schema(name = "promoBannerTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoBannerTitle")
  public String getPromoBannerTitle() {
    return promoBannerTitle;
  }

  public void setPromoBannerTitle(String promoBannerTitle) {
    this.promoBannerTitle = promoBannerTitle;
  }

  public PromotionsInformationResponseDto promoBox(PromoBoxResponseDto promoBox) {
    this.promoBox = promoBox;
    return this;
  }

  /**
   * Get promoBox
   * @return promoBox
   */
  @Valid 
  @Schema(name = "promoBox", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoBox")
  public PromoBoxResponseDto getPromoBox() {
    return promoBox;
  }

  public void setPromoBox(PromoBoxResponseDto promoBox) {
    this.promoBox = promoBox;
  }

  public PromotionsInformationResponseDto promoExpiredMessage(String promoExpiredMessage) {
    this.promoExpiredMessage = promoExpiredMessage;
    return this;
  }

  /**
   * Get promoExpiredMessage
   * @return promoExpiredMessage
   */
  
  @Schema(name = "promoExpiredMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoExpiredMessage")
  public String getPromoExpiredMessage() {
    return promoExpiredMessage;
  }

  public void setPromoExpiredMessage(String promoExpiredMessage) {
    this.promoExpiredMessage = promoExpiredMessage;
  }

  public PromotionsInformationResponseDto promoInvalidMessage(String promoInvalidMessage) {
    this.promoInvalidMessage = promoInvalidMessage;
    return this;
  }

  /**
   * Get promoInvalidMessage
   * @return promoInvalidMessage
   */
  
  @Schema(name = "promoInvalidMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoInvalidMessage")
  public String getPromoInvalidMessage() {
    return promoInvalidMessage;
  }

  public void setPromoInvalidMessage(String promoInvalidMessage) {
    this.promoInvalidMessage = promoInvalidMessage;
  }

  public PromotionsInformationResponseDto promoKind(PromoKindEnum promoKind) {
    this.promoKind = promoKind;
    return this;
  }

  /**
   * Get promoKind
   * @return promoKind
   */
  
  @Schema(name = "promoKind", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promoKind")
  public PromoKindEnum getPromoKind() {
    return promoKind;
  }

  public void setPromoKind(PromoKindEnum promoKind) {
    this.promoKind = promoKind;
  }

  public PromotionsInformationResponseDto promotionCode(String promotionCode) {
    this.promotionCode = promotionCode;
    return this;
  }

  /**
   * Get promotionCode
   * @return promotionCode
   */
  
  @Schema(name = "promotionCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promotionCode")
  public String getPromotionCode() {
    return promotionCode;
  }

  public void setPromotionCode(String promotionCode) {
    this.promotionCode = promotionCode;
  }

  public PromotionsInformationResponseDto showPromo(Boolean showPromo) {
    this.showPromo = showPromo;
    return this;
  }

  /**
   * Get showPromo
   * @return showPromo
   */
  
  @Schema(name = "showPromo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("showPromo")
  public Boolean getShowPromo() {
    return showPromo;
  }

  public void setShowPromo(Boolean showPromo) {
    this.showPromo = showPromo;
  }

  public PromotionsInformationResponseDto termsLink(String termsLink) {
    this.termsLink = termsLink;
    return this;
  }

  /**
   * Get termsLink
   * @return termsLink
   */
  
  @Schema(name = "termsLink", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("termsLink")
  public String getTermsLink() {
    return termsLink;
  }

  public void setTermsLink(String termsLink) {
    this.termsLink = termsLink;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PromotionsInformationResponseDto promotionsInformationResponseDto = (PromotionsInformationResponseDto) o;
    return Objects.equals(this.appPromoAmendMessage, promotionsInformationResponseDto.appPromoAmendMessage) &&
        Objects.equals(this.appPromoBannerSubtitle, promotionsInformationResponseDto.appPromoBannerSubtitle) &&
        Objects.equals(this.appPromoBannerTitle, promotionsInformationResponseDto.appPromoBannerTitle) &&
        Objects.equals(this.appPromoExpiredMessage, promotionsInformationResponseDto.appPromoExpiredMessage) &&
        Objects.equals(this.appPromoInvalidMessage, promotionsInformationResponseDto.appPromoInvalidMessage) &&
        Objects.equals(this.isWithinPromoWindow, promotionsInformationResponseDto.isWithinPromoWindow) &&
        Objects.equals(this.landingPage, promotionsInformationResponseDto.landingPage) &&
        Objects.equals(this.promoAmendMessage, promotionsInformationResponseDto.promoAmendMessage) &&
        Objects.equals(this.promoBannerColour, promotionsInformationResponseDto.promoBannerColour) &&
        Objects.equals(this.promoBannerIcon, promotionsInformationResponseDto.promoBannerIcon) &&
        Objects.equals(this.promoBannerSubtitle, promotionsInformationResponseDto.promoBannerSubtitle) &&
        Objects.equals(this.promoBannerTitle, promotionsInformationResponseDto.promoBannerTitle) &&
        Objects.equals(this.promoBox, promotionsInformationResponseDto.promoBox) &&
        Objects.equals(this.promoExpiredMessage, promotionsInformationResponseDto.promoExpiredMessage) &&
        Objects.equals(this.promoInvalidMessage, promotionsInformationResponseDto.promoInvalidMessage) &&
        Objects.equals(this.promoKind, promotionsInformationResponseDto.promoKind) &&
        Objects.equals(this.promotionCode, promotionsInformationResponseDto.promotionCode) &&
        Objects.equals(this.showPromo, promotionsInformationResponseDto.showPromo) &&
        Objects.equals(this.termsLink, promotionsInformationResponseDto.termsLink);
  }

  @Override
  public int hashCode() {
    return Objects.hash(appPromoAmendMessage, appPromoBannerSubtitle, appPromoBannerTitle, appPromoExpiredMessage, appPromoInvalidMessage, isWithinPromoWindow, landingPage, promoAmendMessage, promoBannerColour, promoBannerIcon, promoBannerSubtitle, promoBannerTitle, promoBox, promoExpiredMessage, promoInvalidMessage, promoKind, promotionCode, showPromo, termsLink);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PromotionsInformationResponseDto {\n");
    sb.append("    appPromoAmendMessage: ").append(toIndentedString(appPromoAmendMessage)).append("\n");
    sb.append("    appPromoBannerSubtitle: ").append(toIndentedString(appPromoBannerSubtitle)).append("\n");
    sb.append("    appPromoBannerTitle: ").append(toIndentedString(appPromoBannerTitle)).append("\n");
    sb.append("    appPromoExpiredMessage: ").append(toIndentedString(appPromoExpiredMessage)).append("\n");
    sb.append("    appPromoInvalidMessage: ").append(toIndentedString(appPromoInvalidMessage)).append("\n");
    sb.append("    isWithinPromoWindow: ").append(toIndentedString(isWithinPromoWindow)).append("\n");
    sb.append("    landingPage: ").append(toIndentedString(landingPage)).append("\n");
    sb.append("    promoAmendMessage: ").append(toIndentedString(promoAmendMessage)).append("\n");
    sb.append("    promoBannerColour: ").append(toIndentedString(promoBannerColour)).append("\n");
    sb.append("    promoBannerIcon: ").append(toIndentedString(promoBannerIcon)).append("\n");
    sb.append("    promoBannerSubtitle: ").append(toIndentedString(promoBannerSubtitle)).append("\n");
    sb.append("    promoBannerTitle: ").append(toIndentedString(promoBannerTitle)).append("\n");
    sb.append("    promoBox: ").append(toIndentedString(promoBox)).append("\n");
    sb.append("    promoExpiredMessage: ").append(toIndentedString(promoExpiredMessage)).append("\n");
    sb.append("    promoInvalidMessage: ").append(toIndentedString(promoInvalidMessage)).append("\n");
    sb.append("    promoKind: ").append(toIndentedString(promoKind)).append("\n");
    sb.append("    promotionCode: ").append(toIndentedString(promotionCode)).append("\n");
    sb.append("    showPromo: ").append(toIndentedString(showPromo)).append("\n");
    sb.append("    termsLink: ").append(toIndentedString(termsLink)).append("\n");
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

