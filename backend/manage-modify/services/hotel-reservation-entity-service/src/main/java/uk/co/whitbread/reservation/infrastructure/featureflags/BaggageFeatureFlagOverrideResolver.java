package uk.co.whitbread.reservation.infrastructure.featureflags;

import io.opentelemetry.api.baggage.Baggage;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlagOverrideResolver;

/**
 * Resolves request-scoped feature flag overrides from W3C baggage.
 *
 * <p>The override is visible only while flag resolution runs on the inbound request thread.
 * Resolution on a pooled or {@code @Async} thread has no servlet request context and falls back to
 * Unleash.
 */
public final class BaggageFeatureFlagOverrideResolver implements FeatureFlagOverrideResolver {

  static final String BAGGAGE_FIELD = "wb-feature-overrides";
  private static final String BAGGAGE_HEADER = "baggage";

  private final Supplier<HttpServletRequest> requestSupplier;

  public BaggageFeatureFlagOverrideResolver() {
    this(BaggageFeatureFlagOverrideResolver::currentRequest);
  }

  BaggageFeatureFlagOverrideResolver(Supplier<HttpServletRequest> requestSupplier) {
    this.requestSupplier = requestSupplier;
  }

  @Override
  public Optional<Boolean> resolve(String featureKey) {
    return featureOverridesValue().flatMap(baggage -> findOverride(baggage, featureKey));
  }

  /**
   * The OpenTelemetry baggage is checked first: the attached javaagent carries it to every thread
   * it propagates context to, so overrides survive reactive and asynchronous flag reads where the
   * servlet request's thread-local is absent. The inbound header remains the fallback for
   * deployments running without the agent.
   */
  private Optional<String> featureOverridesValue() {
    return otelBaggageValue().or(this::servletBaggageValue);
  }

  private static Optional<String> otelBaggageValue() {
    return Optional.ofNullable(Baggage.current().getEntryValue(BAGGAGE_FIELD))
        .map(BaggageFeatureFlagOverrideResolver::percentDecode)
        .filter(value -> !value.isBlank());
  }

  private Optional<String> servletBaggageValue() {
    return Optional.ofNullable(requestSupplier.get())
        .map(request -> request.getHeader(BAGGAGE_HEADER))
        .flatMap(this::findFeatureOverrides);
  }

  private static HttpServletRequest currentRequest() {
    var attributes = RequestContextHolder.getRequestAttributes();
    return attributes instanceof ServletRequestAttributes servletAttributes
        ? servletAttributes.getRequest()
        : null;
  }

  private Optional<String> findFeatureOverrides(String baggageHeader) {
    var prefix = BAGGAGE_FIELD + "=";
    return Arrays.stream(baggageHeader.split(","))
        .map(String::trim)
        .filter(member -> member.startsWith(prefix))
        .map(member -> member.substring(prefix.length()).split(";", 2)[0])
        .filter(value -> !value.isBlank())
        .map(BaggageFeatureFlagOverrideResolver::percentDecode)
        .findFirst();
  }

  /**
   * W3C baggage propagation percent-encodes member values on inter-service hops, so the override
   * separator can arrive as {@code %7C}. Decoding is a no-op for values that arrive raw.
   */
  private static String percentDecode(String value) {
    return URLDecoder.decode(value, StandardCharsets.UTF_8);
  }

  /**
   * Overrides use {@code <flag>:on|<flag>:off}. The resolver scans for the requested flag because
   * callers query one feature at a time; there is no need to parse the whole value into a map.
   */
  private Optional<Boolean> findOverride(String baggage, String featureKey) {
    for (var entry : baggage.split("\\|")) {
      var separator = entry.indexOf(':');
      if (separator < 0 || !entry.substring(0, separator).equals(featureKey)) {
        continue;
      }

      return switch (entry.substring(separator + 1)) {
        case "on" -> Optional.of(true);
        case "off" -> Optional.of(false);
        default -> throw invalid("flag '" + featureKey + "' must have state 'on' or 'off'");
      };
    }

    return Optional.empty();
  }

  private IllegalArgumentException invalid(String reason) {
    return new IllegalArgumentException("Invalid " + BAGGAGE_FIELD + " baggage: " + reason);
  }
}
