package uk.co.whitbread.company.config;

import io.getunleash.UnleashContext;
import io.micrometer.tracing.Baggage;
import io.micrometer.tracing.Tracer;
import java.time.ZonedDateTime;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.unleash.features.autoconfigure.UnleashProperties;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UnleashConfigTest {

  CustomUnleashConfig underTest = new CustomUnleashConfig();

  @Spy
  Tracer tracer = Tracer.NOOP;

  @Spy
  Baggage baggage = Baggage.NOOP;

  @Test
  void shouldGenerateContext_whenBaggageIsGiven() {
    var properties = new UnleashProperties();
    properties.setAppName("My Cool App");
    properties.setEnvironment("Production");

    // Mock our distributed baggage instead of setting up the full tracing system
    Mockito.when(tracer.getBaggage("wb-session-id")).thenReturn(baggage);
    Mockito.when(baggage.get()).thenReturn("my-unique-session-id");

    var actual = underTest.unleashContextProvider(tracer, properties).getContext();

    var expected = UnleashContext.builder()
        .environment("Production")
        .appName("My Cool App")
        .sessionId("my-unique-session-id")
        .currentTime(actual.getCurrentTime().orElse(ZonedDateTime.now()))
        .build();

    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }
}

