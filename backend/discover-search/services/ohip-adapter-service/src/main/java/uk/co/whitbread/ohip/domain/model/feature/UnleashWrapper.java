package uk.co.whitbread.ohip.domain.model.feature;

import io.getunleash.Unleash;

public class UnleashWrapper<T> {

  private final Unleash unleash;
  private final T featureFlag;

  public UnleashWrapper(Unleash unleash, T featureFlag) {
    this.unleash = unleash;
    this.featureFlag = featureFlag;
  }

  public boolean isEnabled(FeatureFlag.Feature flag) {
    return unleash.isEnabled(flag.getKey(), flag.isFallback());
  }

  public Unleash unleash() {
    return unleash;
  }

  public T featureFlag() {
    return featureFlag;
  }
}
