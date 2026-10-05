package uk.co.whitbread.content.infrastructure.rest.client.content.promotion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionItems;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsConfig;

public final class TestPromoConfigFactory {

  private static final DateTimeFormatter DMY =
      DateTimeFormatter.ofPattern("dd/MM/yyyy");

  private TestPromoConfigFactory() {
  }

  public static PromotionsConfig landingPagePromo(String promoCode) {
    return PromotionsConfig.builder()
        .promoItems(List.of(
            PromotionItems.builder()
                .enabled(true)
                .promoCode(promoCode)
                .landingPage("/promo")
                .bookingStartDate(date("01/12/2025"))
                .bookingEndDate(date("31/12/2025"))
                .stayStartDate(date("15/02/2026"))
                .stayEndDate(date("20/02/2026"))
                .promoInvalidMessage("Invalid")
                .promoExpiredMessage("Expired")
                .build()
        ))
        .build();
  }

  public static PromotionsConfig siteWidePromo() {
    return PromotionsConfig.builder()
        .promoItems(List.of(
            PromotionItems.builder()
                .enabled(true)
                .promoCode("SITEWIDE")
                .landingPage("") // site-wide
                .bookingStartDate(date("01/12/2025"))
                .bookingEndDate(date("31/12/2025"))
                .stayStartDate(date("15/02/2026"))
                .stayEndDate(date("20/02/2026"))
                .promoInvalidMessage("Invalid")
                .promoExpiredMessage("Expired")
                .build()
        ))
        .build();
  }

  public static PromotionsConfig promoBoxPromo(String promoCode) {
    return PromotionsConfig.builder()
        .promoItems(List.of(
            PromotionItems.builder()
                .enabled(true)
                .promoCode(promoCode)
                .landingPage("/promo-box")
                .bookingStartDate(date("01/12/2025"))
                .bookingEndDate(date("31/12/2025"))
                .stayStartDate(date("15/02/2026"))
                .stayEndDate(date("20/02/2026"))
                .build()
        ))
        .build();
  }

  public static PromotionsConfig amendPromo(String promoCode) {
    return PromotionsConfig.builder()
        .promoItems(List.of(
            PromotionItems.builder()
                .enabled(true)
                .promoCode(promoCode)
                .landingPage("") // amend can be site-wide
                .bookingStartDate(date("01/12/2025"))
                .bookingEndDate(date("31/12/2025"))
                .stayStartDate(date("15/02/2026"))
                .stayEndDate(date("20/02/2026"))
                .build()
        ))
        .build();
  }

  public static PromotionItems validPromo() {
    return PromotionItems.builder()
        .enabled(true)
        .promoCode("ABCDEF")
        .landingPage("/promo")
        .bookingStartDate(LocalDate.of(2025, 12, 1))
        .bookingEndDate(LocalDate.of(2025, 12, 31))
        .stayStartDate(LocalDate.of(2026, 2, 15))
        .stayEndDate(LocalDate.of(2026, 2, 20))
        .numberOfNights(2)
        .promoInvalidMessage("INVALID")
        .promoExpiredMessage("EXPIRED")
        .promoBannerSubtitle("SUB")
        .build();
  }

  private static LocalDate date(String dmy) {
    return LocalDate.parse(dmy, DMY);
  }
}