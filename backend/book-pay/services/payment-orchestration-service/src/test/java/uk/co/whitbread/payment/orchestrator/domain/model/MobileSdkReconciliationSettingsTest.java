package uk.co.whitbread.payment.orchestrator.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MobileSdkReconciliationSettingsTest {

  @Test
  void recordConstruction_storesAllFields() {
    MobileSdkReconciliationSettings settings = new MobileSdkReconciliationSettings(
        true,
        120_000L,
        30_000L,
        1_800_000L
    );

    assertThat(settings.enabled()).isTrue();
    assertThat(settings.initialDelayMillis()).isEqualTo(120_000L);
    assertThat(settings.pollIntervalMillis()).isEqualTo(30_000L);
    assertThat(settings.maxDurationMillis()).isEqualTo(1_800_000L);
  }
}
