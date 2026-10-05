package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag;
import uk.co.whitbread.reservation.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.shared.commons.validation.CompanyNameSwitch;

@Component
@RequiredArgsConstructor
public class CompanyNameSwitchImpl implements CompanyNameSwitch {

  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  public boolean isEnabled() {
    return unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCompanyNameFeatureFlag());
  }
}
