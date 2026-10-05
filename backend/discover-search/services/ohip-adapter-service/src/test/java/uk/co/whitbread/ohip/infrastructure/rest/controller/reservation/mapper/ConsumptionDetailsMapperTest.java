package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConsumptionDetails;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ConsumptionDetailsDto;

@ExtendWith(MockitoExtension.class)
class ConsumptionDetailsMapperTest {

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  private ConsumptionDetailsMapper consumptionDetailsMapper;

  @BeforeEach
  void setUp() {
    consumptionDetailsMapper = new ConsumptionDetailsMapperImpl();
    consumptionDetailsMapper.setUnleashWrapper(unleashWrapper);
  }

  @Test
  void toModel_shouldMapCorrectlyWhenFeatureFlagIsEnabled() {
    // Arrange
    ConsumptionDetailsDto dto = ConsumptionDetailsDto.builder()
        .totalQuantity(5)
        .build();

    FeatureFlag featureFlag = mock(FeatureFlag.class);
    FeatureFlag.Feature feature = mock(FeatureFlag.Feature.class);

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(featureFlag.getConsumptionDetailsDefaultQuantity()).thenReturn(feature);
    when(unleashWrapper.isEnabled(feature)).thenReturn(true);

    // Act
    ConsumptionDetails result = consumptionDetailsMapper.toModel(dto);

    // Assert
    assertNotNull(result);
    assertEquals(5, result.getTotalQuantity());
    assertEquals(5, result.getDefaultQuantity());
  }

  @Test
  void toModel_shouldMapWithNullDefaultQuantityWhenFeatureFlagIsDisabled() {
    // Arrange
    ConsumptionDetailsDto dto = ConsumptionDetailsDto.builder()
        .totalQuantity(10)
        .build();

    FeatureFlag featureFlag = mock(FeatureFlag.class);
    FeatureFlag.Feature feature = mock(FeatureFlag.Feature.class);

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(featureFlag.getConsumptionDetailsDefaultQuantity()).thenReturn(feature);
    when(unleashWrapper.isEnabled(feature)).thenReturn(false);

    // Act
    ConsumptionDetails result = consumptionDetailsMapper.toModel(dto);

    // Assert
    assertNotNull(result);
    assertEquals(10, result.getTotalQuantity());
    assertNull(result.getDefaultQuantity());
  }

  @Test
  void toModel_shouldMapWithNullDefaultQuantityWhenFeatureFlagIsNull() {
    // Arrange
    ConsumptionDetailsDto dto = ConsumptionDetailsDto.builder()
        .totalQuantity(15)
        .build();

    FeatureFlag featureFlag = mock(FeatureFlag.class);

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(featureFlag.getConsumptionDetailsDefaultQuantity()).thenReturn(null);

    // Act
    ConsumptionDetails result = consumptionDetailsMapper.toModel(dto);

    // Assert
    assertNotNull(result);
    assertEquals(15, result.getTotalQuantity());
    assertNull(result.getDefaultQuantity());
  }

  @Test
  void toModel_shouldReturnNullWhenDtoIsNull() {
    // Act
    ConsumptionDetails result = consumptionDetailsMapper.toModel(null);


    // Assert
    assertNull(result);
  }
}

