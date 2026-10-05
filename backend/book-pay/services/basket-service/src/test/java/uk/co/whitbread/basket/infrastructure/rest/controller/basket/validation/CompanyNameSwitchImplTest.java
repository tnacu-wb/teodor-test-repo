package uk.co.whitbread.basket.infrastructure.rest.controller.basket.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag.Feature;
import uk.co.whitbread.basket.domain.model.feature.UnleashWrapper;

class CompanyNameSwitchImplTest {

  @Test
  void isEnabled_returnsTrueWhenFeatureEnabled() {
    UnleashWrapper<FeatureFlag> unleashWrapper = mock(UnleashWrapper.class);
    FeatureFlag featureFlag = mock(FeatureFlag.class);
    Feature feature = mock(Feature.class);

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(featureFlag.getCompanyNameFeatureFlag()).thenReturn(feature);
    when(unleashWrapper.isEnabled(feature)).thenReturn(true);

    CompanyNameSwitchImpl companyNameSwitch = new CompanyNameSwitchImpl(unleashWrapper);

    assertTrue(companyNameSwitch.isEnabled());
  }

  @Test
  void isEnabled_returnsFalseWhenFeatureDisabled() {
    UnleashWrapper<FeatureFlag> unleashWrapper = mock(UnleashWrapper.class);
    FeatureFlag featureFlag = mock(FeatureFlag.class);
    Feature feature = mock(Feature.class);

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(featureFlag.getCompanyNameFeatureFlag()).thenReturn(feature);
    when(unleashWrapper.isEnabled(feature)).thenReturn(false);

    CompanyNameSwitchImpl companyNameSwitch = new CompanyNameSwitchImpl(unleashWrapper);

    assertFalse(companyNameSwitch.isEnabled());
  }
}