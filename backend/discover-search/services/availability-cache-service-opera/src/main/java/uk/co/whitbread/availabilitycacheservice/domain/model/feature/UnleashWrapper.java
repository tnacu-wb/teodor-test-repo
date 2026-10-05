package uk.co.whitbread.availabilitycacheservice.domain.model.feature;

import io.getunleash.Unleash;

public record UnleashWrapper<T>(Unleash unleash, T featureFlag) {

  public boolean isEnabled(FeatureFlag.Feature flag) {
    return unleash.isEnabled(flag.getKey(), flag.isFallback());
  }
}