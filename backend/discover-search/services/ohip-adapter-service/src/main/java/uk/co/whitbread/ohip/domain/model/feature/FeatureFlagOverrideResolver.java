package uk.co.whitbread.ohip.domain.model.feature;

import java.util.Optional;

@FunctionalInterface
public interface FeatureFlagOverrideResolver {

  Optional<Boolean> resolve(String featureKey);
}
