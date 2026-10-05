package uk.co.whitbread.content.infrastructure.rest.client.content.promotion.handler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsConfig;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsInformationResponse;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoContext;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoFlowType;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoKind;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotion.TestPromoConfigFactory;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.handler.AmendPromoHandler;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.resolver.PromoEvaluationService;

@ExtendWith(MockitoExtension.class)
class AmendPromoEvaluationStrategyTest {

  private static final String PROMO_CODE = "ABCDEF";

  private AmendPromoHandler strategy;

  @Mock
  private PromoEvaluationService promoEvaluationService;

  @BeforeEach
  void setUp() {
    strategy = new AmendPromoHandler(promoEvaluationService);
  }

  @Test
  void flowType_shouldReturnAmend() {
    assertThat(strategy.flowType(), is(PromoFlowType.AMEND));
  }

  @Test
  void evaluate_amendWithPromoKind_shouldUseProvidedPromoKind() {

    PromoContext context = PromoContext.builder()
        .bookingDate(LocalDate.of(2025, 12, 12))
        .stayStartDate(LocalDate.of(2026, 2, 16))
        .stayEndDate(LocalDate.of(2026, 2, 18))
        .promoCode(PROMO_CODE)
        .promoKind(PromoKind.SITE_WIDE)
        .build();

    PromotionsConfig config =
        TestPromoConfigFactory.amendPromo(PROMO_CODE);

    when(promoEvaluationService.evaluatePromo(
        any(),
        any(),
        any(),
        any(),
        eq(PromoKind.SITE_WIDE)
    )).thenReturn(
        PromotionsInformationResponse.builder()
            .showPromo(true)
            .isWithinPromoWindow(true)
            .promoKind(PromoKind.SITE_WIDE)
            .promotionCode(PROMO_CODE)
            .build()
    );

    PromotionsInformationResponse resp =
        strategy.evaluate(context, config);

    assertThat(resp.getShowPromo(), is(true));
    assertThat(resp.getPromoKind(), is(PromoKind.SITE_WIDE));
  }

  @Test
  void evaluate_amendWithNullPromoKind_shouldDefaultToLandingPage() {

    PromoContext context = PromoContext.builder()
        .bookingDate(LocalDate.of(2025, 12, 12))
        .stayStartDate(LocalDate.of(2026, 2, 16))
        .stayEndDate(LocalDate.of(2026, 2, 18))
        .promoCode(PROMO_CODE)
        .promoKind(null)
        .build();

    PromotionsConfig config =
        TestPromoConfigFactory.amendPromo(PROMO_CODE);

    when(promoEvaluationService.evaluatePromo(
        any(),
        any(),
        any(),
        any(),
        eq(PromoKind.LANDING_PAGE)
    )).thenReturn(
        PromotionsInformationResponse.builder()
            .showPromo(true)
            .isWithinPromoWindow(true)
            .promoKind(PromoKind.LANDING_PAGE)
            .promotionCode(PROMO_CODE)
            .build()
    );

    PromotionsInformationResponse resp =
        strategy.evaluate(context, config);

    assertThat(resp.getShowPromo(), is(true));
    assertThat(resp.getPromoKind(), is(PromoKind.LANDING_PAGE));
  }

  @Test
  void evaluate_amendWithNoMatchingPromoCode_shouldReturnNoPromo() {

    PromoContext context = PromoContext.builder()
        .bookingDate(LocalDate.of(2025, 12, 12))
        .stayStartDate(LocalDate.of(2026, 2, 16))
        .stayEndDate(LocalDate.of(2026, 2, 18))
        .promoCode("DIFFERENT")
        .promoKind(PromoKind.SITE_WIDE)
        .build();

    PromotionsConfig config =
        TestPromoConfigFactory.amendPromo(PROMO_CODE);

    PromotionsInformationResponse resp =
        strategy.evaluate(context, config);

    assertThat(resp.getShowPromo(), is(false));
    assertThat(resp.getIsWithinPromoWindow(), is(false));
  }
}