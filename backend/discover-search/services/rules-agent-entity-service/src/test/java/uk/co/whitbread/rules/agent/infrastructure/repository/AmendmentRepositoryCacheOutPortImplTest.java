package uk.co.whitbread.rules.agent.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.model.out.AmendmentRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.AmendmentRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.AmendmentRuleEntity;

@ExtendWith(MockitoExtension.class)
class AmendmentRepositoryCacheOutPortImplTest {

  @Mock
  private AmendmentRepository amendmentRepository;

  @Mock
  private AmendmentRuleEntityMapper amendmentRuleEntityMapper;

  @InjectMocks
  private AmendmentRepositoryCacheOutPortImpl amendmentRepositoryCacheOutPort;

  @Test
  void cacheRules__shouldCacheAllActiveRules() throws IllegalAccessException {
    //Arrange
    var are1 = createAmendmentRuleEntity(1, "ACTIVE");
    var are2 = createAmendmentRuleEntity(2, "ACTIVE");
    when(amendmentRepository.findAllByStatusActive()).thenReturn(List.of(are1, are2));
    Map<Integer, AmendmentRuleEntity> expectedCachedRules = Map.of(1, are1, 2, are2);

    //Act
    amendmentRepositoryCacheOutPort.cacheRules();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(amendmentRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        amendmentRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(amendmentRepository).findAllByStatusActive();
    verifyNoMoreInteractions(amendmentRepository);
  }

  @Test
  void updateCache__shouldUpdateCachedRules() throws IllegalAccessException {
    //Arrange
    var are1 = createAmendmentRuleEntity(1, "ACTIVE");
    var are2 = createAmendmentRuleEntity(2, "ACTIVE");
    when(amendmentRepository.findAllByStatusActive()).thenReturn(List.of(are1, are2));
    amendmentRepositoryCacheOutPort.cacheRules();
    var are1inactive = createAmendmentRuleEntity(1, "INACTIVE");
    var are3 = createAmendmentRuleEntity(3, "ACTIVE");
    when(amendmentRepository.findAllUpdatedAfter(any())).thenReturn(List.of(are1inactive, are3));
    Map<Integer, AmendmentRuleEntity> expectedCachedRules = Map.of(2, are2, 3, are3);

    //Act
    amendmentRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(amendmentRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        amendmentRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(amendmentRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(amendmentRepository);
  }

  @Test
  void updateCache__shouldNotUpdateForUnknownStatus() throws IllegalAccessException {
    //Arrange
    var are1 = createAmendmentRuleEntity(1, "THIS WOULD BE SOMETHING RANDOM");
    var are2 = createAmendmentRuleEntity(2, "ACTIVE");
    when(amendmentRepository.findAllUpdatedAfter(any())).thenReturn(List.of(are1, are2));
    var expectedCachedRules = Map.of(2, are2);

    //Act
    amendmentRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(amendmentRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    verify(amendmentRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(amendmentRepository);
  }

  @Test
  void findRule__shouldFilterByRateType() throws IllegalAccessException {
    //Arrange
    var dummyEntity1 = createDummyEntity(1, "Flex", 1);
    var dummyEntity2 = createDummyEntity(2, "Semi-Flex", 2);
    var dummyEntity3 = createDummyEntity(3, "Non-Flex", 3);
    setCachedRules(List.of(dummyEntity1, dummyEntity2, dummyEntity3));
    var dummyRule1 = createDummyRule();
    when(amendmentRuleEntityMapper.toModel(dummyEntity1)).thenReturn(dummyRule1);

    //Act
    var actualRule = amendmentRepositoryCacheOutPort.findRule("Flex", "GB");

    //Assert
    assertThat(actualRule).isNotEmpty();
    assertThat(actualRule).contains(dummyRule1);
    verifyNoMoreInteractions(amendmentRuleEntityMapper);
    verifyNoInteractions(amendmentRepository);
  }

  @Test
  void findRule__shouldReturnNothingIfRateTypeNotFoundWhenFilterByRateType()
      throws IllegalAccessException {
    //Arrange
    var dummyEntity1 = createDummyEntity(1, "Flex", 1);
    var dummyEntity2 = createDummyEntity(2, "Semi-Flex", 2);
    var dummyEntity3 = createDummyEntity(3, "Non-Flex", 3);
    setCachedRules(List.of(dummyEntity1, dummyEntity2, dummyEntity3));

    //Act
    var actualRule = amendmentRepositoryCacheOutPort.findRule("Standard", "GB");

    //Assert
    assertThat(actualRule).isEmpty();
    verifyNoInteractions(amendmentRuleEntityMapper);
    verifyNoInteractions(amendmentRepository);
  }

  @Test
  void findRule__shouldReturnTheLastUpdatedRuleWhenDuplicateRulesFound()
      throws IllegalAccessException {
    //Arrange
    var dummyEntity1 = createDummyEntity(1, "Flex", 1);
    var dummyEntity2 = createDummyEntity(2, "Flex", 2);
    var now = LocalDateTime.now();
    dummyEntity1.setLastModifiedAt(now.minusMinutes(1));
    dummyEntity2.setLastModifiedAt(now.minusMinutes(2));
    setCachedRules(List.of(dummyEntity1, dummyEntity2));
    var dummyRule1 = createDummyRule();
    dummyRule1.setLastModifiedAt(now.minusMinutes(1));
    when(amendmentRuleEntityMapper.toModel(dummyEntity1)).thenReturn(dummyRule1);

    //Act
    var actualRule = amendmentRepositoryCacheOutPort.findRule("Flex", "GB");

    //Assert
    assertThat(actualRule).isNotEmpty();
    assertThat(actualRule).contains(dummyRule1);
    verifyNoMoreInteractions(amendmentRuleEntityMapper);
    verifyNoInteractions(amendmentRepository);
  }

  private AmendmentRuleEntity createAmendmentRuleEntity(Integer ruleId, String status) {
    var amendmentRuleEntity = new AmendmentRuleEntity();
    amendmentRuleEntity.setRuleId(ruleId);
    amendmentRuleEntity.setStatus(status);
    return amendmentRuleEntity;
  }

  private AmendmentRuleEntity createDummyEntity(Integer ruleId, String rateType,
      Integer delayTime) {
    var dummyEntity = new AmendmentRuleEntity();
    var time = LocalDateTime.now();
    dummyEntity.setRuleId(ruleId);
    dummyEntity.setRateType(rateType);
    dummyEntity.setCountryCode("GB");
    dummyEntity.setLastModifiedAt(time.minusMinutes(delayTime));
    return dummyEntity;
  }

  private AmendmentRule createDummyRule() {
    var time = LocalDateTime.now();
    return AmendmentRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(time)
        .lastModifiedAt(time)
        .rateType("Flex")
        .countryCode("GB")
        .build();
  }

  private void setCachedRules(List<AmendmentRuleEntity> entities) throws IllegalAccessException {
    Map<Integer, AmendmentRuleEntity> cachedRules = (Map<Integer, AmendmentRuleEntity>) FieldUtils.readField(
        amendmentRepositoryCacheOutPort,
        "cachedRules",
        true);
    entities.forEach(entity -> cachedRules.put(entity.getRuleId(), entity));
  }
}
