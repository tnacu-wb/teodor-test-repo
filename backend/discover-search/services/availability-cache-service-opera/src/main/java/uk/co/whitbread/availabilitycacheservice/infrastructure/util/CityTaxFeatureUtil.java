package uk.co.whitbread.availabilitycacheservice.infrastructure.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.availabilitycacheservice.domain.model.feature.FeatureFlag;
import uk.co.whitbread.availabilitycacheservice.domain.model.feature.UnleashWrapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class CityTaxFeatureUtil {

  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  public boolean isFeatureEnabled() {
    return unleashWrapper.isEnabled(unleashWrapper.featureFlag().getReleasePiCcuiCityTaxUk());
  }

  public boolean isFallbackEnabled() {
    return unleashWrapper.isEnabled(unleashWrapper.featureFlag().getReleasePiCcuiCityTaxUkFallback());
  }
}
