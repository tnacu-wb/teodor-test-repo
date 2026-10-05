package uk.co.whitbread.account.domain.model.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.account.domain.model.feature.FeatureFlag;
import uk.co.whitbread.account.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.shared.commons.validation.CompanyNameSwitch;

@Component
@RequiredArgsConstructor
public class CompanyNameSwitchImpl implements CompanyNameSwitch {

  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  public boolean isEnabled() {
    return unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCompanyNameValidation());
  }
}
