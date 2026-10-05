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
 * PromotionsInformationResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PromotionsInformationResponseDto {

  private @Nullable Boolean isWithinPromoWindow;

  private @Nullable String landingPage;

  private @Nullable String promoBannerColour;

  private @Nullable String promoBannerIcon;

  private @Nullable String promoBannerSubtitle;

  private @Nullable String promoBannerTitle;

  private @Nullable String promoExpiredMessage;

  private @Nullable String promoInvalidMessage;

  private @Nullable String promotionCode;

  private @Nullable Boolean showPromo;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PromotionsInformationResponseDto promotionsInformationResponseDto = (PromotionsInformationResponseDto) o;
    return Objects.equals(this.isWithinPromoWindow, promotionsInformationResponseDto.isWithinPromoWindow) &&
        Objects.equals(this.landingPage, promotionsInformationResponseDto.landingPage) &&
        Objects.equals(this.promoBannerColour, promotionsInformationResponseDto.promoBannerColour) &&
        Objects.equals(this.promoBannerIcon, promotionsInformationResponseDto.promoBannerIcon) &&
        Objects.equals(this.promoBannerSubtitle, promotionsInformationResponseDto.promoBannerSubtitle) &&
        Objects.equals(this.promoBannerTitle, promotionsInformationResponseDto.promoBannerTitle) &&
        Objects.equals(this.promoExpiredMessage, promotionsInformationResponseDto.promoExpiredMessage) &&
        Objects.equals(this.promoInvalidMessage, promotionsInformationResponseDto.promoInvalidMessage) &&
        Objects.equals(this.promotionCode, promotionsInformationResponseDto.promotionCode) &&
        Objects.equals(this.showPromo, promotionsInformationResponseDto.showPromo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isWithinPromoWindow, landingPage, promoBannerColour, promoBannerIcon, promoBannerSubtitle, promoBannerTitle, promoExpiredMessage, promoInvalidMessage, promotionCode, showPromo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PromotionsInformationResponseDto {\n");
    sb.append("    isWithinPromoWindow: ").append(toIndentedString(isWithinPromoWindow)).append("\n");
    sb.append("    landingPage: ").append(toIndentedString(landingPage)).append("\n");
    sb.append("    promoBannerColour: ").append(toIndentedString(promoBannerColour)).append("\n");
    sb.append("    promoBannerIcon: ").append(toIndentedString(promoBannerIcon)).append("\n");
    sb.append("    promoBannerSubtitle: ").append(toIndentedString(promoBannerSubtitle)).append("\n");
    sb.append("    promoBannerTitle: ").append(toIndentedString(promoBannerTitle)).append("\n");
    sb.append("    promoExpiredMessage: ").append(toIndentedString(promoExpiredMessage)).append("\n");
    sb.append("    promoInvalidMessage: ").append(toIndentedString(promoInvalidMessage)).append("\n");
    sb.append("    promotionCode: ").append(toIndentedString(promotionCode)).append("\n");
    sb.append("    showPromo: ").append(toIndentedString(showPromo)).append("\n");
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

