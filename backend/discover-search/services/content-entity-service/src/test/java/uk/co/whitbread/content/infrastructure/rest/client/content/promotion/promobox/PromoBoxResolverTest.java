package uk.co.whitbread.content.infrastructure.rest.client.content.promotion.promobox;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import uk.co.whitbread.content.domain.model.globalconfig.in.PromoConfigRequest;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsInformationResponse;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoBoxStatus;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoCodeStatus;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoKind;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promobox.PromoBoxResolutionResult;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promobox.resolver.PromoBoxResolver;

class PromoBoxResolverTest {

  private PromoBoxResolver resolver;

  @BeforeEach
  void setUp() {
    resolver = new PromoBoxResolver();
  }

  @Test
  void emptyCode_shouldReturnEmpty() {
    PromoConfigRequest req = PromoConfigRequest.builder()
        .isPromoBox(true)
        .promotionCode("")
        .build();

    PromoBoxResolutionResult result =
        resolver.resolvePreEvaluation(req);

    assertThat(result.isTerminal(), is(true));
    assertThat(result.getStatus(), is(PromoBoxStatus.EMPTY));
  }

  @ParameterizedTest(name = "unique {0} → {1}")
  @CsvSource({
      "REDEEMED, CODE_ALREADY_APPLIED",
      "EXPIRED,  CODE_EXPIRED"
  })
  void preEval_uniqueTerminalStates_shouldReturnExpectedStatus(
      PromoCodeStatus promoCodeStatus,
      PromoBoxStatus expectedStatus
  ) {
    PromoConfigRequest req = PromoConfigRequest.builder()
        .isPromoBox(true)
        .promoKind(PromoKind.UNIQUE)
        .promotionCode("CODE")
        .uniquePromoCodeStatus(promoCodeStatus)
        .build();

    PromoBoxResolutionResult result =
        resolver.resolvePreEvaluation(req);

    assertThat(result.isTerminal(), is(true));
    assertThat(result.getStatus(), is(expectedStatus));
  }

  @ParameterizedTest(name = "{index} → non-terminal pre-eval")
  @MethodSource("nonTerminalPreEvalRequests")
  void preEval_nonTerminalCases_shouldReturnNonTerminal(
      PromoConfigRequest request
  ) {
    PromoBoxResolutionResult result =
        resolver.resolvePreEvaluation(request);

    assertThat(result.isTerminal(), is(false));
  }

  private static Stream<PromoConfigRequest> nonTerminalPreEvalRequests() {
    return Stream.of(
        PromoConfigRequest.builder()
            .isPromoBox(false)
            .build(),

        PromoConfigRequest.builder()
            .isPromoBox(true)
            .isAmendRequest(true)
            .build(),

        PromoConfigRequest.builder()
            .isPromoBox(true)
            .promoKind(PromoKind.GENERIC)
            .promotionCode("ABC")
            .build(),

        PromoConfigRequest.builder()
            .isPromoBox(true)
            .promoKind(PromoKind.UNIQUE)
            .promotionCode("ABC")
            .uniquePromoCodeStatus(PromoCodeStatus.ISSUED)
            .build()
    );
  }

  @Test
  void postEval_notPromoBox_shouldReturnNonTerminal() {
    PromoConfigRequest req = PromoConfigRequest.builder()
        .isPromoBox(false)
        .build();

    PromotionsInformationResponse resp =
        PromotionsInformationResponse.builder().build();

    PromoBoxResolutionResult result =
        resolver.resolvePostEvaluation(req, resp);

    assertThat(result.isTerminal(), is(false));
  }

  @ParameterizedTest(name = "showPromo={0}, withinWindow={1} → {2}")
  @CsvSource({
      "true,  true,  SUCCESS",
      "true,  false, UNAVAILABLE",
      "false, false, INVALID"
  })
  void postEval_shouldResolveExpectedStatus(
      boolean showPromo,
      boolean withinWindow,
      PromoBoxStatus expectedStatus
  ) {
    PromoConfigRequest req = PromoConfigRequest.builder()
        .isPromoBox(true)
        .build();

    PromotionsInformationResponse resp =
        PromotionsInformationResponse.builder()
            .showPromo(showPromo)
            .isWithinPromoWindow(withinWindow)
            .build();

    PromoBoxResolutionResult result =
        resolver.resolvePostEvaluation(req, resp);

    assertThat(result.isTerminal(), is(true));
    assertThat(result.getStatus(), is(expectedStatus));
  }
}