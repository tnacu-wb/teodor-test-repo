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
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.handler.SiteWidePromoHandler;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.resolver.PromoEvaluationService;

@ExtendWith(MockitoExtension.class)
class SiteWidePromoEvaluationStrategyTest {

  private SiteWidePromoHandler strategy;

  @Mock
  private PromoEvaluationService promoEvaluationService;

  @BeforeEach
  void setUp() {
    strategy = new SiteWidePromoHandler(promoEvaluationService);
  }

  @Test
  void flowType_shouldReturnSiteWide() {
    assertThat(strategy.flowType(), is(PromoFlowType.SITE_WIDE));
  }

  @Test
  void evaluate_validSiteWidePromo_shouldReturnSuccess() {
    PromoContext context = PromoContext.builder()
        .bookingDate(LocalDate.of(2025, 12, 12))
        .stayStartDate(LocalDate.of(2026, 2, 16))
        .stayEndDate(LocalDate.of(2026, 2, 18))
        .build();

    PromotionsConfig config =
        TestPromoConfigFactory.siteWidePromo();

    when(promoEvaluationService.evaluatePromo(any(), any(), any(),
        any(), eq(PromoKind.SITE_WIDE)
    )).thenReturn(
        PromotionsInformationResponse.builder()
            .showPromo(true)
            .isWithinPromoWindow(true)
            .promoKind(PromoKind.SITE_WIDE)
            .promotionCode("SITEWIDE")
            .build()
    );

    PromotionsInformationResponse resp =
        strategy.evaluate(context, config);

    assertThat(resp.getShowPromo(), is(true));
    assertThat(resp.getIsWithinPromoWindow(), is(true));
    assertThat(resp.getPromoKind(), is(PromoKind.SITE_WIDE));
  }

  @Test
  void evaluate_siteWidePromo_notApplicable_shouldReturnNoPromo() {

    PromoContext context = PromoContext.builder()
        .bookingDate(LocalDate.of(2025, 12, 12))
        .stayStartDate(LocalDate.of(2026, 2, 16))
        .stayEndDate(LocalDate.of(2026, 2, 18))
        .build();

    PromotionsConfig config =
        TestPromoConfigFactory.siteWidePromo();

    when(promoEvaluationService.evaluatePromo(any(), any(), any(),
        any(), eq(PromoKind.SITE_WIDE)
    )).thenReturn(
        PromotionsInformationResponse.builder()
            .showPromo(false)
            .isWithinPromoWindow(false)
            .build()
    );

    PromotionsInformationResponse resp =
        strategy.evaluate(context, config);

    assertThat(resp.getShowPromo(), is(false));
    assertThat(resp.getIsWithinPromoWindow(), is(false));
  }
}