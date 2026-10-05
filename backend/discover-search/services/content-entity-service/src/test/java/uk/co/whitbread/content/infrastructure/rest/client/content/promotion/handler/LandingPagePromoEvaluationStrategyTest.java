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
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.handler.LandingPagePromoHandler;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.resolver.PromoEvaluationService;

@ExtendWith(MockitoExtension.class)
class LandingPagePromoEvaluationStrategyTest {

  private static final String PROMOTION_CODE = "ABCDEF";

  private LandingPagePromoHandler strategy;

  @Mock
  private PromoEvaluationService promoEvaluationService;

  @BeforeEach
  void setUp() {
    strategy = new LandingPagePromoHandler(promoEvaluationService);
  }

  @Test
  void flowType_shouldReturnLandingPage() {
    assertThat(strategy.flowType(), is(PromoFlowType.LANDING_PAGE));
  }

  @Test
  void evaluate_validLandingPagePromo_shouldReturnSuccess() {

    PromoContext context = PromoContext.builder()
        .bookingDate(LocalDate.of(2025, 12, 12))
        .stayStartDate(LocalDate.of(2026, 2, 16))
        .stayEndDate(LocalDate.of(2026, 2, 18))
        .promoCode(PROMOTION_CODE)
        .build();

    PromotionsConfig config =
        TestPromoConfigFactory.landingPagePromo(PROMOTION_CODE);

    when(promoEvaluationService.evaluatePromo(any(), any(), any(),
        any(), eq(PromoKind.LANDING_PAGE)
    )).thenReturn(
        PromotionsInformationResponse.builder()
            .showPromo(true)
            .isWithinPromoWindow(true)
            .promoKind(PromoKind.LANDING_PAGE)
            .promotionCode(PROMOTION_CODE)
            .build()
    );

    PromotionsInformationResponse resp =
        strategy.evaluate(context, config);

    assertThat(resp.getShowPromo(), is(true));
    assertThat(resp.getIsWithinPromoWindow(), is(true));
    assertThat(resp.getPromoKind(), is(PromoKind.LANDING_PAGE));
  }
}