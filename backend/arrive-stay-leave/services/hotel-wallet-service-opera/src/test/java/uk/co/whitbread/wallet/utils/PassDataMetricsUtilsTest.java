package uk.co.whitbread.wallet.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.micrometer.core.instrument.Metrics;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.wallet.domain.utils.PassDataMetricsUtils;

class PassDataMetricsUtilsTest {

  @Test
  void testOneCounterValue() {
    Metrics.globalRegistry.clear();

    // Call the method under test
    PassDataMetricsUtils.incrementMandatoryPassDataMissing();

    assertNotNull(PassDataMetricsUtils.mandatoryPassDataMissing);
  }

}
