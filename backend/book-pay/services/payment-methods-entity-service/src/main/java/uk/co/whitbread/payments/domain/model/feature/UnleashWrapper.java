package uk.co.whitbread.payments.domain.model.feature;

import io.getunleash.Unleash;
import io.getunleash.UnleashContext;

public record UnleashWrapper<T>(Unleash unleash, T featureFlag) {

  public boolean isEnabled(FeatureFlag.Feature flag) {
    return unleash.isEnabled(flag.getKey(), flag.isFallback());
  }

  public boolean isEnabled(FeatureFlag.Feature flag, UnleashContext context) {
    return unleash.isEnabled(flag.getKey(), context, flag.isFallback());
  }
}
