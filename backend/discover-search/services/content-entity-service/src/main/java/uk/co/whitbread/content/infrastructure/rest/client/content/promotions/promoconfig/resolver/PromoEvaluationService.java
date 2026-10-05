package uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.resolver;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionItems;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsInformationResponse;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoDateValidationResponse;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoKind;

@Service
public class PromoEvaluationService {

  public PromotionsInformationResponse evaluatePromo(
      PromotionItems promotionItems,
      LocalDate bookingDate,
      LocalDate stayStartDate,
      LocalDate stayEndDate,
      PromoKind promoKind) {

    PromoDateValidationResponse validation =
        validatePromo(promotionItems, bookingDate, stayStartDate, stayEndDate);

    PromoKind resolvedKind = promoKind != null ? promoKind : PromoKind.GENERIC;

    return switch (resolvedKind) {
      case LANDING_PAGE, GENERIC, UNIQUE ->
          evaluateLandingPagePromo(promotionItems, validation, promoKind);

      case SITE_WIDE -> isSiteWideEligible(promotionItems, validation)
          ? buildPromoResponse(promotionItems, true, null, null, promoKind)
          : noPromo();
    };
  }

  private boolean isSiteWideEligible(
      PromotionItems promotionItems,
      PromoDateValidationResponse validation) {

    return promotionItems.isEnabled()
        && validation.isBookingValid()
        && validation.isStayValid()
        && validation.isNightsValid();
  }

  private PromotionsInformationResponse noPromo() {
    return PromotionsInformationResponse.builder()
        .showPromo(false)
        .isWithinPromoWindow(false)
        .promoInvalidMessage("No applicable promotions")
        .build();
  }

  private PromotionsInformationResponse evaluateLandingPagePromo(
      PromotionItems promo, PromoDateValidationResponse validation,
      PromoKind promoKind) {

    if (!promo.isEnabled()) {
      return buildPromoResponse(promo, false,
          promo.getPromoInvalidMessage(), null, promoKind);
    }

    if (validation.isBookingValid()
        && validation.isStayValid()
        && validation.isNightsValid()) {

      return buildPromoResponse(promo, true, null, null, promoKind);
    }

    if (validation.isBookingInvalid()) {
      return buildPromoResponse(
          promo, false, promo.getPromoInvalidMessage(), null, promoKind);
    }

    if (validation.isBookingExpired()) {
      return buildPromoResponse(
          promo, false, null, promo.getPromoExpiredMessage(), promoKind);
    }

    return buildPromoResponse(
        promo, false, promo.getPromoInvalidMessage(), null, promoKind);
  }

  private PromotionsInformationResponse buildPromoResponse(
      PromotionItems promo,
      boolean withinWindow,
      String invalidMessage,
      String expiredMessage,
      PromoKind promoKind) {

    String subTitle =
        (invalidMessage != null || expiredMessage != null)
            ? null
            : promo.getPromoBannerSubtitle();

    return PromotionsInformationResponse.builder()
        .showPromo(true)
        .isWithinPromoWindow(withinWindow)
        .landingPage(promo.getLandingPage())
        .promotionCode(promo.getPromoCode())
        .promoBannerColour(promo.getPromoBannerColour())
        .promoBannerIcon(promo.getPromoBannerIcon())
        .promoBannerTitle(promo.getPromoBannerTitle())
        .promoBannerSubtitle(subTitle)
        .promoInvalidMessage(invalidMessage)
        .promoExpiredMessage(expiredMessage)
        .promoAmendMessage(promo.getPromoAmendMessage())
        .promoKind(promoKind)
        .termsLink(promo.getTermsLink())
        .appPromoBannerTitle(promo.getAppPromoBannerTitle())
        .appPromoBannerSubtitle(promo.getAppPromoBannerSubtitle())
        .appPromoInvalidMessage(promo.getAppPromoInvalidMessage())
        .appPromoExpiredMessage(promo.getAppPromoExpiredMessage())
        .appPromoAmendMessage(promo.getAppPromoAmendMessage())
        .promoBannerVisibility(promo.getPromoBannerVisibility())
        .build();
  }

  private PromoDateValidationResponse validatePromo(
      PromotionItems promoItem,
      LocalDate bookingDate,
      LocalDate stayStart,
      LocalDate stayEnd) {

    LocalDate bookingStart = promoItem.getBookingStartDate();
    LocalDate bookingEnd = promoItem.getBookingEndDate();
    LocalDate stayStartWindow = promoItem.getStayStartDate();
    LocalDate stayEndWindow = promoItem.getStayEndDate();

    long actualNights = ChronoUnit.DAYS.between(stayStart, stayEnd);
    int requiredNights = promoItem.getNumberOfNights();

    boolean bookingValid =
        isWithinRange(bookingDate, bookingStart, bookingEnd);
    boolean bookingInvalid =
        bookingStart != null && bookingDate.isBefore(bookingStart);
    boolean bookingExpired =
        bookingEnd != null && bookingDate.isAfter(bookingEnd);

    boolean stayValid =
        isWithinRange(stayStart, stayStartWindow, stayEndWindow)
            && isWithinRange(stayEnd, stayStartWindow, stayEndWindow);

    boolean nightsValid =
        actualNights >= requiredNights && actualNights > 0;

    return PromoDateValidationResponse.builder()
        .bookingValid(bookingValid)
        .bookingInvalid(bookingInvalid)
        .bookingExpired(bookingExpired)
        .stayValid(stayValid)
        .nightsValid(nightsValid)
        .build();
  }

  private boolean isWithinRange(
      LocalDate date, LocalDate start, LocalDate end) {

    return date != null && start != null && end != null
        && (date.isEqual(start) || date.isAfter(start))
        && (date.isEqual(end) || date.isBefore(end));
  }
}