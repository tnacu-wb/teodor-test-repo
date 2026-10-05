package uk.co.whitbread.payment.orchestrator.infrastructure.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.MerchantIdProperties;

class SandboxMerchantIdResolverTest {

  private MerchantIdProperties properties;
  private SandboxMerchantIdResolver underTest;

  @BeforeEach
  void setUp() {
    properties = new MerchantIdProperties();
    properties.setPrefix("deWB-");
    properties.setDefaultMerchantId("deWB-default");
    properties.setProvisionedHotels(List.of("HARHOR", "GRESOU"));
    underTest = new SandboxMerchantIdResolver(properties);
  }

  @Nested
  class ProvisionedHotels {

    @Test
    void returnsSpecificMerchantIdForHarhor() {
      String result = underTest.resolveMerchantId("HARHOR");

      assertThat(result).isEqualTo("deWB-HARHOR");
    }

    @Test
    void returnsSpecificMerchantIdForGresou() {
      String result = underTest.resolveMerchantId("GRESOU");

      assertThat(result).isEqualTo("deWB-GRESOU");
    }

    @Test
    void prefixIsPresentInProvisionedResult() {
      String result = underTest.resolveMerchantId("HARHOR");

      assertThat(result).startsWith("deWB-");
    }
  }

  @Nested
  class NonProvisionedHotels {

    @Test
    void returnsDefaultMerchantIdForNonProvisionedHotel() {
      String result = underTest.resolveMerchantId("TESTHT");

      assertThat(result).isEqualTo("deWB-default");
    }
  }

  @Nested
  class NullOrEmptyHotelCode {

    @Test
    void returnsDefaultMerchantIdForNullHotelCode() {
      String result = underTest.resolveMerchantId(null);

      assertThat(result).isEqualTo("deWB-default");
    }

    @Test
    void returnsDefaultMerchantIdForEmptyHotelCode() {
      String result = underTest.resolveMerchantId("");

      assertThat(result).isEqualTo("deWB-default");
    }

    @Test
    void returnsDefaultMerchantIdForBlankHotelCode() {
      String result = underTest.resolveMerchantId("   ");

      assertThat(result).isEqualTo("deWB-default");
    }
  }

  @Nested
  class EmptyProvisionedHotelsList {

    @BeforeEach
    void setUp() {
      properties.setProvisionedHotels(List.of());
      underTest = new SandboxMerchantIdResolver(properties);
    }

    @Test
    void returnsDefaultMerchantIdWhenNoHotelsProvisioned() {
      String result = underTest.resolveMerchantId("HARHOR");

      assertThat(result).isEqualTo("deWB-default");
    }
  }
}
