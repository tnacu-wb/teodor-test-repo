package uk.co.whitbread.reservation.domain.model.feature;

import io.getunleash.Unleash;

public final class OverrideAwareUnleashWrapper<T> extends UnleashWrapper<T> {

  private final FeatureFlagOverrideResolver featureFlagOverrideResolver;

  public OverrideAwareUnleashWrapper(
      Unleash unleash,
      T featureFlag,
      FeatureFlagOverrideResolver featureFlagOverrideResolver) {
    super(unleash, featureFlag);
    this.featureFlagOverrideResolver = featureFlagOverrideResolver;
  }

  @Override
  public boolean isEnabled(FeatureFlag.Feature flag) {
    return featureFlagOverrideResolver.resolve(flag.getKey())
        .orElseGet(() -> super.isEnabled(flag));
  }
}
