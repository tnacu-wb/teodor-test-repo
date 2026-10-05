package uk.co.whitbread.wallet.domain.utils;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class PassDataMetricsUtils {

  public static final String MANDATORY_PASS_DATA_MISSING
      = "mandatory_pass_data_missing";

  public static final String COUNTS_HOW_MANY_TIMES_MANDATORY_PASS_DATA_IS_MISSING
      = "Counts how many times mandatory pass data is missing";

  public static final Counter mandatoryPassDataMissing = Counter
      .builder(MANDATORY_PASS_DATA_MISSING)
      .description(COUNTS_HOW_MANY_TIMES_MANDATORY_PASS_DATA_IS_MISSING)
      .register(Metrics.globalRegistry);

  public static void incrementMandatoryPassDataMissing() {
    log.error("Mandatory pass data is missing, metric incremented");
    mandatoryPassDataMissing.increment();
  }

}
