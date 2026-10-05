package uk.co.whitbread.content.infrastructure.rest.client.content.promotion.resolver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoContext;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoFlowType;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.resolver.PromoFlowTypeResolver;

@ExtendWith(MockitoExtension.class)
class PromoFlowTypeResolverTest {

  private final PromoFlowTypeResolver resolver = new PromoFlowTypeResolver();

  @Test
  void promoBoxAndAmend_shouldResolveAmend() {
    PromoContext context = PromoContext.builder()
        .promoBox(true)
        .amendRequest(true)
        .build();

    assertThat(resolver.resolve(context), is(PromoFlowType.AMEND));
  }

  @Test
  void promoBoxOnly_shouldResolvePromoBox() {
    PromoContext context = PromoContext.builder()
        .promoBox(true)
        .build();

    assertThat(resolver.resolve(context), is(PromoFlowType.PROMO_BOX));
  }

  @Test
  void promoCodePresent_shouldResolveLandingPage() {
    PromoContext context = PromoContext.builder()
        .promoCode("ABC")
        .build();

    assertThat(resolver.resolve(context), is(PromoFlowType.LANDING_PAGE));
  }

  @Test
  void default_shouldResolveSiteWide() {
    PromoContext context = PromoContext.builder().build();

    assertThat(resolver.resolve(context), is(PromoFlowType.SITE_WIDE));
  }
}