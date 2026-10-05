package uk.co.whitbread.ohip.domain.model.feature;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.getunleash.Unleash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnleashWrapperTest {

  private static final String FEATURE_KEY = "test-feature";

  @Mock
  private Unleash unleash;

  private FeatureFlag.Feature feature;
  private UnleashWrapper<FeatureFlag> wrapper;

  @BeforeEach
  void setUp() {
    feature = new FeatureFlag.Feature();
    feature.setKey(FEATURE_KEY);
    feature.setFallback(true);
    wrapper = new UnleashWrapper<>(unleash, new FeatureFlag());
  }

  @Test
  void shouldDelegateDirectlyToUnleash() {
    when(unleash.isEnabled(FEATURE_KEY, true)).thenReturn(false);

    assertThat(wrapper.isEnabled(feature)).isFalse();

    verify(unleash).isEnabled(FEATURE_KEY, true);
  }
}
