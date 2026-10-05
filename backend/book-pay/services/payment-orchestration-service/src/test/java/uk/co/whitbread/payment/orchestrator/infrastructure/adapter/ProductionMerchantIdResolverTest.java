package uk.co.whitbread.payment.orchestrator.infrastructure.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.MerchantIdProperties;

class ProductionMerchantIdResolverTest {

  private MerchantIdProperties properties;
  private ProductionMerchantIdResolver underTest;

  @BeforeEach
  void setUp() {
    properties = new MerchantIdProperties();
    underTest = new ProductionMerchantIdResolver(properties);
  }

  @Test
  void resolvesHotelCodeToMerchantIdWithPrefix() {
    String result = underTest.resolveMerchantId("HARHOR");

    assertThat(result).isEqualTo("deWB-HARHOR");
  }

  @Test
  void resolvesAlternativeHotelCodeToMerchantIdWithPrefix() {
    String result = underTest.resolveMerchantId("GRESOU");

    assertThat(result).isEqualTo("deWB-GRESOU");
  }

  @Test
  void throwsForNullHotelCode() {
    assertThatThrownBy(() -> underTest.resolveMerchantId(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("must not be null or blank");
  }

  @Test
  void throwsForEmptyHotelCode() {
    assertThatThrownBy(() -> underTest.resolveMerchantId(""))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("must not be null or blank");
  }

  @Test
  void throwsForBlankHotelCode() {
    assertThatThrownBy(() -> underTest.resolveMerchantId("   "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("must not be null or blank");
  }

  @Test
  void alwaysIncludesConfiguredPrefix() {
    assertThat(underTest.resolveMerchantId("HARHOR")).startsWith("deWB-");
    assertThat(underTest.resolveMerchantId("GRESOU")).startsWith("deWB-");
    assertThat(underTest.resolveMerchantId("LONWAT")).startsWith("deWB-");
  }
}
