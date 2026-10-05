package uk.co.whitbread.ohip.domain.model.feature;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.getunleash.Unleash;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OverrideAwareUnleashWrapperTest {

  private static final String FEATURE_KEY = "test-feature";

  @Mock
  private Unleash unleash;

  @Mock
  private FeatureFlagOverrideResolver featureFlagOverrideResolver;

  private FeatureFlag.Feature feature;
  private OverrideAwareUnleashWrapper<FeatureFlag> wrapper;

  @BeforeEach
  void setUp() {
    feature = new FeatureFlag.Feature();
    feature.setKey(FEATURE_KEY);
    feature.setFallback(true);
    wrapper =
        new OverrideAwareUnleashWrapper<>(
            unleash, new FeatureFlag(), featureFlagOverrideResolver);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void shouldUseRequestOverrideWithoutCallingUnleash(boolean override) {
    when(featureFlagOverrideResolver.resolve(FEATURE_KEY)).thenReturn(Optional.of(override));

    assertThat(wrapper.isEnabled(feature)).isEqualTo(override);

    verifyNoInteractions(unleash);
  }

  @Test
  void shouldDelegateToUnleashWhenRequestHasNoOverride() {
    when(featureFlagOverrideResolver.resolve(FEATURE_KEY)).thenReturn(Optional.empty());
    when(unleash.isEnabled(FEATURE_KEY, true)).thenReturn(false);

    assertThat(wrapper.isEnabled(feature)).isFalse();

    verify(unleash).isEnabled(FEATURE_KEY, true);
  }
}
