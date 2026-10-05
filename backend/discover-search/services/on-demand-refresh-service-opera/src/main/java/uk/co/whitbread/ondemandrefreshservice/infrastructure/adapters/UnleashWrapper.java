package uk.co.whitbread.ondemandrefreshservice.infrastructure.adapters;

import io.getunleash.Unleash;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.FeatureFlag;

public record UnleashWrapper<T>(Unleash unleash, T featureFlag) {

  public boolean isEnabled(FeatureFlag.Feature flag) {
    return unleash.isEnabled(flag.getKey(), flag.isFallback());
  }
}
