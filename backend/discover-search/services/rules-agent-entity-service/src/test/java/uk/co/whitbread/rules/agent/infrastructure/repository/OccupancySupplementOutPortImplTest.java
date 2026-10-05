package uk.co.whitbread.rules.agent.infrastructure.repository;

import static java.util.List.of;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.OccupancySupplementEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.OccupancySupplementEntityMapperImpl;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.OccupancySupplementEntity;

@ExtendWith(MockitoExtension.class)
class OccupancySupplementOutPortImplTest {

  @Mock
  private OccupancySupplementRepository occupancySupplementRepository;

  @Spy
  private OccupancySupplementEntityMapper occupancySupplementEntityMapper = new OccupancySupplementEntityMapperImpl();

  @InjectMocks
  private OccupancySupplementOutPortImpl occupancySupplementOutPort;

  @Test
  void cacheRules__shouldCacheActiveRules() throws IllegalAccessException {
    // Arrange
    var occupancySupplementEntity1 = createOccupancySupplementEntity("FRAMTI", BigDecimal.valueOf(2), "ACTIVE");
    var occupancySupplementEntity2 = createOccupancySupplementEntity("MANOLD", BigDecimal.valueOf(3), "ACTIVE");
    var occupancySupplementEntity3 = createOccupancySupplementEntity("GATGAT", BigDecimal.valueOf(3), "ACTIVE");
    var listOfRules = of(occupancySupplementEntity1, occupancySupplementEntity2, occupancySupplementEntity3);
    Map<String, OccupancySupplementEntity> expectedCachedRules = listOfRules
        .stream()
        .collect(Collectors.toMap(OccupancySupplementEntity::getHotelId, Function.identity()));
    when(occupancySupplementRepository.findAllByStatusActive()).thenReturn(listOfRules);

    //Act
    occupancySupplementOutPort.cacheRules();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(occupancySupplementOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        occupancySupplementOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verifyNoMoreInteractions(occupancySupplementRepository);
  }

  @Test
  void updateCache__shouldUpdateCache() throws IllegalAccessException {
    //Arrange
    var occupancySupplementEntity1 = createOccupancySupplementEntity("FRAMTI", BigDecimal.valueOf(2), "ACTIVE");
    var occupancySupplementEntity2 = createOccupancySupplementEntity("MANOLD", BigDecimal.valueOf(3), "INACTIVE");
    var occupancySupplementEntity3 = createOccupancySupplementEntity("GATGAT", BigDecimal.valueOf(3), "UNKNOWN");
    var listOfRules = of(occupancySupplementEntity1, occupancySupplementEntity2, occupancySupplementEntity3);
    Map<String, OccupancySupplementEntity> expectedCachedRules = listOfRules
        .stream()
        .filter(rule -> "ACTIVE".equals(rule.getStatus()))
        .collect(Collectors.toMap(OccupancySupplementEntity::getHotelId, Function.identity()));
    when(occupancySupplementRepository.findAllUpdatedAfter(any())).thenReturn(listOfRules);


    //Act
    occupancySupplementOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(occupancySupplementOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    verifyNoMoreInteractions(occupancySupplementRepository);
  }


  @Test
  void findRule__shouldReturnOk() throws IllegalAccessException {
    // Arrange
    var occupancySupplementEntity = createOccupancySupplementEntity("FRAMTI", BigDecimal.valueOf(2), "ACTIVE");

    setCachedRules(List.of(occupancySupplementEntity));

    //Act
    var response = occupancySupplementOutPort.findRule("FRAMTI");

    //Assert
    assertFalse(response.isEmpty());
    var actualResponse = response.get();
    assertThat(actualResponse.getHotelId()).isEqualTo(occupancySupplementEntity.getHotelId());
    assertThat(actualResponse.getPricing()).isEqualTo(occupancySupplementEntity.getPricing());
    verifyNoInteractions(occupancySupplementRepository);
  }

  private OccupancySupplementEntity createOccupancySupplementEntity(String hotelId,
                                                                    BigDecimal pricing,
                                                                    String status) {
    var time = LocalDateTime.now();
    var rateSuppressionRuleEntity = new OccupancySupplementEntity();
    rateSuppressionRuleEntity.setRuleId(ThreadLocalRandom.current().nextInt());
    rateSuppressionRuleEntity.setStatus(status);
    rateSuppressionRuleEntity.setCreatedAt(time);
    rateSuppressionRuleEntity.setLastModifiedAt(time);
    rateSuppressionRuleEntity.setHotelId(hotelId);
    rateSuppressionRuleEntity.setPricing(pricing);
    return rateSuppressionRuleEntity;
  }

  private void setCachedRules(List<OccupancySupplementEntity> entities) throws IllegalAccessException {
    Map<String, OccupancySupplementEntity> cachedRules = (Map<String, OccupancySupplementEntity>) FieldUtils.readField(
        occupancySupplementOutPort,
        "cachedRules",
        true);
    entities.forEach(entity -> cachedRules.put(entity.getHotelId(), entity));
  }
}