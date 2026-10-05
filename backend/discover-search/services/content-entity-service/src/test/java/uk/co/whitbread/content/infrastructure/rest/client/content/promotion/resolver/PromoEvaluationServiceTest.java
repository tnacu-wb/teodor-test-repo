package uk.co.whitbread.content.infrastructure.rest.client.content.promotion.resolver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static uk.co.whitbread.content.infrastructure.rest.client.content.promotion.TestPromoConfigFactory.validPromo;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsInformationResponse;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoKind;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.resolver.PromoEvaluationService;

class PromoEvaluationServiceTest {

  private PromoEvaluationService service;

  @BeforeEach
  void setUp() {
    service = new PromoEvaluationService();
  }

  @Test
  void siteWide_allValid_shouldReturnSuccess() {
    PromotionsInformationResponse resp =
        service.evaluatePromo(
            validPromo(),
            LocalDate.of(2025, 12, 12),
            LocalDate.of(2026, 2, 16),
            LocalDate.of(2026, 2, 18),
            PromoKind.SITE_WIDE
        );

    assertThat(resp.getShowPromo(), is(true));
    assertThat(resp.getIsWithinPromoWindow(), is(true));
    assertThat(resp.getPromoInvalidMessage(), nullValue());
  }

  @Test
  void landingPage_allValid_shouldReturnSuccess() {
    PromotionsInformationResponse resp =
        service.evaluatePromo(
            validPromo(),
            LocalDate.of(2025, 12, 12),
            LocalDate.of(2026, 2, 16),
            LocalDate.of(2026, 2, 18),
            PromoKind.LANDING_PAGE
        );

    assertThat(resp.getIsWithinPromoWindow(), is(true));
  }

  @Test
  void landingPage_bookingBeforeStart_shouldReturnInvalid() {
    PromotionsInformationResponse resp =
        service.evaluatePromo(
            validPromo(),
            LocalDate.of(2025, 11, 1),
            LocalDate.of(2026, 2, 16),
            LocalDate.of(2026, 2, 18),
            PromoKind.LANDING_PAGE
        );

    assertThat(resp.getIsWithinPromoWindow(), is(false));
    assertThat(resp.getPromoInvalidMessage(), is("INVALID"));
  }

  @Test
  void landingPage_bookingAfterEnd_shouldReturnExpired() {
    PromotionsInformationResponse resp =
        service.evaluatePromo(
            validPromo(),
            LocalDate.of(2026, 1, 10),
            LocalDate.of(2026, 2, 16),
            LocalDate.of(2026, 2, 18),
            PromoKind.LANDING_PAGE
        );

    assertThat(resp.getIsWithinPromoWindow(), is(false));
    assertThat(resp.getPromoExpiredMessage(), is("EXPIRED"));
  }

  @Test
  void landingPage_stayOutsideWindow_shouldReturnInvalid() {
    PromotionsInformationResponse resp =
        service.evaluatePromo(
            validPromo(),
            LocalDate.of(2025, 12, 12),
            LocalDate.of(2026, 3, 1),
            LocalDate.of(2026, 3, 3),
            PromoKind.LANDING_PAGE
        );

    assertThat(resp.getIsWithinPromoWindow(), is(false));
    assertThat(resp.getPromoInvalidMessage(), is("INVALID"));
  }

  @Test
  void landingPage_insufficientNights_shouldReturnInvalid() {
    PromotionsInformationResponse resp =
        service.evaluatePromo(
            validPromo(),
            LocalDate.of(2025, 12, 12),
            LocalDate.of(2026, 2, 16),
            LocalDate.of(2026, 2, 17),
            PromoKind.LANDING_PAGE
        );

    assertThat(resp.getIsWithinPromoWindow(), is(false));
    assertThat(resp.getPromoInvalidMessage(), is("INVALID"));
  }
}