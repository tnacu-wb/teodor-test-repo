package uk.co.whitbread.reservation.infrastructure.featureflags;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.context.Context;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BaggageFeatureFlagOverrideResolverTest {

  @Mock
  private HttpServletRequest request;

  @Test
  void shouldResolveEnabledAndDisabledOverrides() {
    givenBaggage("wb-test-id=scenario-1,wb-feature-overrides=first-flag:on|second.flag:off");
    var resolver = new BaggageFeatureFlagOverrideResolver(() -> request);

    assertThat(resolver.resolve("first-flag")).contains(true);
    assertThat(resolver.resolve("second.flag")).contains(false);
  }


  @Test
  void shouldResolvePercentEncodedOverridesFromPropagatedBaggage() {
    givenBaggage("wb-feature-overrides=first-flag:on%7Csecond.flag:off,wb-test-id=scenario-1");
    var resolver = new BaggageFeatureFlagOverrideResolver(() -> request);

    assertThat(resolver.resolve("first-flag")).contains(true);
    assertThat(resolver.resolve("second.flag")).contains(false);
  }


  @Test
  void shouldResolveOverridesFromOtelBaggageWithoutARequest() {
    var baggage = Baggage.builder()
        .put("wb-feature-overrides", "first-flag:on|second.flag:off")
        .build();
    try (var ignored = Context.current().with(baggage).makeCurrent()) {
      var resolver = new BaggageFeatureFlagOverrideResolver(() -> null);

      assertThat(resolver.resolve("first-flag")).contains(true);
      assertThat(resolver.resolve("second.flag")).contains(false);
    }
  }

  @Test
  void shouldReturnEmptyWhenBaggageIsMissing() {
    var resolver = new BaggageFeatureFlagOverrideResolver(() -> request);

    assertThat(resolver.resolve("first-flag")).isEmpty();
  }

  @Test
  void shouldReturnEmptyOutsideARequest() {
    var resolver = new BaggageFeatureFlagOverrideResolver();

    assertThat(resolver.resolve("first-flag")).isEmpty();
  }

  @Test
  void shouldReturnEmptyWhenOverrideBaggageIsMissing() {
    givenBaggage("wb-test-id=scenario-1");
    var resolver = new BaggageFeatureFlagOverrideResolver(() -> request);

    assertThat(resolver.resolve("first-flag")).isEmpty();
  }

  @Test
  void shouldReturnEmptyWhenBaggageIsBlank() {
    givenBaggage("wb-feature-overrides=   ");
    var resolver = new BaggageFeatureFlagOverrideResolver(() -> request);

    assertThat(resolver.resolve("first-flag")).isEmpty();
  }

  @Test
  void shouldReturnEmptyWhenFlagIsNotOverridden() {
    givenBaggage("wb-feature-overrides=other-flag:on");
    var resolver = new BaggageFeatureFlagOverrideResolver(() -> request);

    assertThat(resolver.resolve("first-flag")).isEmpty();
  }

  @Test
  void shouldIgnoreEntriesThatDoNotOverrideTheRequestedFlag() {
    givenBaggage(
        "wb-test-id=scenario-1, wb-feature-overrides=malformed|other-flag:unknown|first-flag:on");
    var resolver = new BaggageFeatureFlagOverrideResolver(() -> request);

    assertThat(resolver.resolve("first-flag")).contains(true);
  }

  @Test
  void shouldRejectAnInvalidStateForTheRequestedFlag() {
    givenBaggage("wb-feature-overrides=first-flag:true;source=journey");
    var resolver = new BaggageFeatureFlagOverrideResolver(() -> request);

    assertThatThrownBy(() -> resolver.resolve("first-flag"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(
            "Invalid wb-feature-overrides baggage: flag 'first-flag' must have state 'on' or 'off'");
  }

  private void givenBaggage(String baggage) {
    when(request.getHeader("baggage")).thenReturn(baggage);
  }
}
