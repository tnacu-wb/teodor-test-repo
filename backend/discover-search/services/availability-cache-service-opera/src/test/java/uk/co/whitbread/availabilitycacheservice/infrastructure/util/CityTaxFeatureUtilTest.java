package uk.co.whitbread.availabilitycacheservice.infrastructure.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.co.whitbread.availabilitycacheservice.domain.model.feature.FeatureFlag;
import uk.co.whitbread.availabilitycacheservice.domain.model.feature.FeatureFlag.Feature;
import uk.co.whitbread.availabilitycacheservice.domain.model.feature.UnleashWrapper;

class CityTaxFeatureUtilTest {

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @Mock
  private FeatureFlag featureFlag;
  @Mock
  private Feature releasePiCcuiCityTaxUk;
  @Mock
  private Feature releasePiCcuiCityTaxUkFallback;

  private CityTaxFeatureUtil util;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    util = new CityTaxFeatureUtil(unleashWrapper);
  }

  @Test
  void testIsFeatureEnabled_returnsFalse_whenMainFlagDisabled() {
    when(featureFlag.getReleasePiCcuiCityTaxUk()).thenReturn(releasePiCcuiCityTaxUk);
    when(unleashWrapper.isEnabled(releasePiCcuiCityTaxUk)).thenReturn(false);

    boolean result = util.isFeatureEnabled();

    assertThat(result).isFalse();
  }

  @Test
  void testIsFallbackEnabled() {
    when(featureFlag.getReleasePiCcuiCityTaxUkFallback()).thenReturn(releasePiCcuiCityTaxUkFallback);
    when(unleashWrapper.isEnabled(releasePiCcuiCityTaxUkFallback)).thenReturn(true);

    boolean result = util.isFallbackEnabled();

    assertThat(result).isTrue();
  }
}