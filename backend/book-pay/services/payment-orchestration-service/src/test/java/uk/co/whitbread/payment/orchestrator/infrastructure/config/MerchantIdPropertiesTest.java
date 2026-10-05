package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

class MerchantIdPropertiesTest {

  @Test
  void validateSucceedsWithValidProperties() {
    MerchantIdProperties properties = new MerchantIdProperties();
    properties.setPrefix("deWB-");
    properties.setDefaultMerchantId("deWB-default");
    properties.setProvisionedHotels(List.of("HARHOR", "GRESOU"));

    assertThatCode(properties::validate).doesNotThrowAnyException();
  }

  @Test
  void validateSucceedsWithDefaults() {
    MerchantIdProperties properties = new MerchantIdProperties();

    assertThatCode(properties::validate).doesNotThrowAnyException();
  }

  @ParameterizedTest(name = "prefix \"{0}\" rejected")
  @NullSource
  @MethodSource("blankStrings")
  void validateFailsForNullOrBlankPrefix(String prefix) {
    MerchantIdProperties properties = new MerchantIdProperties();
    properties.setPrefix(prefix);

    assertThatThrownBy(properties::validate)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("prefix must not be null or empty");
  }

  @ParameterizedTest(name = "defaultMerchantId \"{0}\" rejected")
  @NullSource
  @MethodSource("blankStrings")
  void validateFailsForNullOrBlankDefaultMerchantId(String defaultMerchantId) {
    MerchantIdProperties properties = new MerchantIdProperties();
    properties.setDefaultMerchantId(defaultMerchantId);

    assertThatThrownBy(properties::validate)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("default-merchant-id must not be null or empty");
  }

  private static Stream<String> blankStrings() {
    return Stream.of("", "   ", "\t");
  }
}
