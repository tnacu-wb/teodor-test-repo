package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag;
import uk.co.whitbread.reservation.domain.model.feature.UnleashWrapper;

@ExtendWith(MockitoExtension.class)
class CompanyNameSwitchImplTest {

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @Mock
  private FeatureFlag featureFlag;

  @Mock
  private FeatureFlag.Feature feature;

  @Test
  void isEnabled_delegatesToUnleashWrapperAndReturnsResult() {
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(featureFlag.getCompanyNameFeatureFlag()).thenReturn(feature);
    when(unleashWrapper.isEnabled(feature)).thenReturn(true);

    CompanyNameSwitchImpl companyNameSwitch = new CompanyNameSwitchImpl(unleashWrapper);

    assertTrue(companyNameSwitch.isEnabled());

    when(unleashWrapper.isEnabled(feature)).thenReturn(false);

    assertFalse(companyNameSwitch.isEnabled());
  }
}
