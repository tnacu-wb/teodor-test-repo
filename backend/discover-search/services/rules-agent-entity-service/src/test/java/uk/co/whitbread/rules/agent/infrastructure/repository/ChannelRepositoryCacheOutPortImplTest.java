package uk.co.whitbread.rules.agent.infrastructure.repository;

import static java.util.List.of;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.model.in.ChannelRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.ChannelRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.ChannelRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.ChannelRuleEntity;

@ExtendWith(MockitoExtension.class)
class ChannelRepositoryCacheOutPortImplTest {

  private static final String SOURCE_ID ="11";

  @Mock
  private ChannelRepository channelRepository;

  @InjectMocks
  private ChannelRepositoryCacheOutPortImpl channelRepositoryCacheOutPort;

  @Mock
  private ChannelRuleEntityMapper channelRuleEntityMapper;

  @Test
  void cacheRules__shouldCacheAllActiveRules() throws IllegalAccessException {
    // Arrange
    var channelRuleEntity1 = createChannelRuleEntity(1, "ACTIVE");
    var channelRuleEntity2 = createChannelRuleEntity(2, "ACTIVE");
    when(channelRepository.findAllByStatusActive()).thenReturn(
        List.of(channelRuleEntity1, channelRuleEntity2));

    Map<Integer, ChannelRuleEntity> expectedCachedRules = Map.of(1,
        channelRuleEntity1, 2,
        channelRuleEntity2);

    //Act
    channelRepositoryCacheOutPort.cacheRules();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(channelRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        channelRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(channelRepository).findAllByStatusActive();
    verifyNoMoreInteractions(channelRepository);
  }

  @Test
  void updateCache__shouldUpdateCachedRules() throws IllegalAccessException {
    // Arrange
    var channelRuleEntity1 = createChannelRuleEntity(1, "ACTIVE");
    var channelRuleEntity2 = createChannelRuleEntity(2, "ACTIVE");
    when(channelRepository.findAllByStatusActive()).thenReturn(
        List.of(channelRuleEntity1, channelRuleEntity2));
    channelRepositoryCacheOutPort.cacheRules();

    var channelRuleEntityInactive1 = createChannelRuleEntity(1, "INACTIVE");
    var channelRuleEntity3 = createChannelRuleEntity(3, "ACTIVE");

    when(channelRepository.findAllUpdatedAfter(any())).thenReturn(
        List.of(channelRuleEntityInactive1, channelRuleEntity3));

    Map<Integer, ChannelRuleEntity> expectedCachedRules = Map.of(2,
        channelRuleEntity2, 3, channelRuleEntity3);

    //Act
    channelRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(channelRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        channelRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(channelRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(channelRepository);

  }

  @Test
  void updateCache__shouldNotUpdateForUnknownStatus() throws IllegalAccessException {
    // Arrange
    var channelRuleEntity1 = createChannelRuleEntity(1, "NON-EXISTENT STATUS");
    var channelRuleEntity2 = createChannelRuleEntity(2, "ACTIVE");

    when(channelRepository.findAllUpdatedAfter(any())).thenReturn(
        List.of(channelRuleEntity1, channelRuleEntity2));
    var expectedCachedRules = Map.of(2, channelRuleEntity2);

    //Act
    channelRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(channelRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    verify(channelRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(channelRepository);
  }

  @Test
  void findChannelRule__shouldReturnOk() throws IllegalAccessException {
    // Arrange
    var channelRuleEntity1 = createChannelRuleEntity(1, "ACTIVE");

    var channelRuleDomain1 = createChannelRuleDomain(channelRuleEntity1);

    setCachedRules(
        of(channelRuleEntity1));

    when(channelRuleEntityMapper.toModel(channelRuleEntity1)).thenReturn(
        channelRuleDomain1);

    //Act
    var response = channelRepositoryCacheOutPort.findRule(ChannelRuleRequest.builder()
            .channel("PI")
            .subchannel("WEB")
            .language("EN")
            .pms("OP")
        .build());

    //Assert
    assertFalse(response.isEmpty());
    assertThat(response.get().getChannel()).isEqualTo(
        channelRuleEntity1.getChannel());
    assertThat(response.get().getSubchannel()).isEqualTo(
        channelRuleEntity1.getSubchannel());
    assertThat(response.get().getLanguage()).isEqualTo(
        channelRuleEntity1.getLanguage());
    assertThat(response.get().getPms()).isEqualTo(
        channelRuleEntity1.getPms());

    verifyNoMoreInteractions(channelRuleEntityMapper);
    verifyNoInteractions(channelRepository);
  }

  @Test
  void givenValidSourceId_whenFindChannelRuleBasedOnSourceId__shouldReturnOk() throws IllegalAccessException {
    var channelRuleEntity = createChannelRuleEntity(1, "ACTIVE");
    var channelRuleDomain = createChannelRuleDomain(channelRuleEntity);
    setCachedRules(of(channelRuleEntity));
    when(channelRuleEntityMapper.toModel(channelRuleEntity)).thenReturn(channelRuleDomain);

    var response = channelRepositoryCacheOutPort.findRule(SOURCE_ID);

    assertFalse(response.isEmpty());
    assertEquals(channelRuleEntity.getChannel(), response.get().getChannel());
    assertEquals(channelRuleEntity.getSubchannel(), response.get().getSubchannel());
    assertEquals(channelRuleEntity.getLanguage(), response.get().getLanguage());
    assertEquals(channelRuleEntity.getPms(), response.get().getPms());
    verifyNoMoreInteractions(channelRuleEntityMapper);
    verifyNoInteractions(channelRepository);
  }

  @Test
  void givenInvalidSourceId_whenFindChannelRuleBasedOnSourceId__shouldReturnEmpty() throws IllegalAccessException {
    var channelRuleEntity = createChannelRuleEntity(1, "ACTIVE");
    setCachedRules(of(channelRuleEntity));

    assertEquals(Optional.empty(), channelRepositoryCacheOutPort.findRule("100"));
  }

  private ChannelRuleEntity createChannelRuleEntity(Integer ruleId,
      String status) {
    var time = LocalDateTime.now();
    var channelRuleEntity = new ChannelRuleEntity();
    channelRuleEntity.setRuleId(ruleId);
    channelRuleEntity.setStatus(status);
    channelRuleEntity.setCreatedAt(time);
    channelRuleEntity.setLastModifiedAt(time);
    channelRuleEntity.setChannel("PI");
    channelRuleEntity.setSubchannel("WEB");
    channelRuleEntity.setLanguage("EN");
    channelRuleEntity.setPms("OP");
    channelRuleEntity.setSourceId(SOURCE_ID);
    channelRuleEntity.setRatePlanSets(List.of("PBF"));

    return channelRuleEntity;
  }

  private ChannelRule createChannelRuleDomain(
      ChannelRuleEntity channelRuleEntity) {
    var time = LocalDateTime.now();
    return ChannelRule.builder()
        .ruleId(channelRuleEntity.getRuleId())
        .createdAt(time)
        .lastModifiedAt(time)
        .status(RuleStatus.ACTIVE)
        .channel(channelRuleEntity.getChannel())
        .subchannel(channelRuleEntity.getSubchannel())
        .language(channelRuleEntity.getLanguage())
        .pms(channelRuleEntity.getPms())
        .sourceId(SOURCE_ID)
        .ratePlanSets(List.of("PBN"))
        .build();

  }

  private void setCachedRules(List<ChannelRuleEntity> entities)
      throws IllegalAccessException {
    Map<Integer, ChannelRuleEntity> cachedRules = (Map<Integer, ChannelRuleEntity>) FieldUtils.readField(
        channelRepositoryCacheOutPort,
        "cachedRules",
        true);
    entities.forEach(entity -> cachedRules.put(entity.getRuleId(), entity));
  }
}
