package uk.co.whitbread.content.domain.model.globalconfig.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoBoxStatus;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoKind;

@Data
@Builder
@AllArgsConstructor
public class PromotionsInformationResponse {

  private Boolean showPromo;
  private Boolean isWithinPromoWindow;
  private String promotionCode;
  private String landingPage;
  private String promoBannerColour;
  private String promoBannerIcon;
  private String promoBannerTitle;
  private String promoBannerSubtitle;
  private String promoInvalidMessage;
  private String promoExpiredMessage;
  private String promoAmendMessage;
  private PromoBox promoBox;
  private PromoKind promoKind;
  private String termsLink;
  private String appPromoBannerTitle;
  private String appPromoBannerSubtitle;
  private String appPromoInvalidMessage;
  private String appPromoExpiredMessage;
  private String appPromoAmendMessage;
  private PromoBoxStatus promoBoxStatus;
  private String promoBoxMessageKey;
  private String errorRateAndRoomMessage;
  private List<String> promoBannerVisibility;
}