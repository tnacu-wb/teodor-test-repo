package uk.co.whitbread.rules.agent.infrastructure.repository;

import static java.util.List.of;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.domain.model.in.VatRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.domain.model.out.VatRule;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.VatRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.VatRuleEntity;

@ExtendWith(MockitoExtension.class)
class VatRepositoryCacheOutPortImplTest {

  private static final String VAT_REGION = "UK";
  private static final String PKG_CODE = "MDBEVA";
  private static final String TRAN_CODE = "9028";

  @Mock
  private VatCacheRepository vatCacheRepository;

  @InjectMocks
  private VatRepositoryCacheOutPortImpl vatRepositoryCacheOutPort;

  @Mock
  private VatRuleEntityMapper vatRuleEntityMapper;

  @Test
  void cacheRules__shouldCacheAllActiveRules() throws IllegalAccessException {
    //Arrange
    var vatRuleEntity1 = createVatRuleEntity(1, "ACTIVE", "", "", "", 1);
    var vatRuleEntity2 = createVatRuleEntity(2, "ACTIVE", "", "", "", 2);
    when(vatCacheRepository.findAllByStatusActive()).thenReturn(
        of(vatRuleEntity1, vatRuleEntity2));
    Map<Integer, VatRuleEntity> expectedCachedRules = Map.of(1,
        vatRuleEntity1, 2,
        vatRuleEntity2);

    //Act
    vatRepositoryCacheOutPort.cacheRules();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(vatRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        vatRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(vatCacheRepository).findAllByStatusActive();
    verifyNoMoreInteractions(vatCacheRepository);
  }

  @Test
  void updateCache__shouldUpdateCachedRules() throws IllegalAccessException {
    //Arrange
    var vatRuleEntity1 = createVatRuleEntity(1, "ACTIVE", "", "", "", 1);
    var vatRuleEntity2 = createVatRuleEntity(2, "ACTIVE", "", "", "", 2);
    when(vatCacheRepository.findAllByStatusActive()).thenReturn(
        of(vatRuleEntity1, vatRuleEntity2));
    vatRepositoryCacheOutPort.cacheRules();
    var vatRuleEntity1Inactive = createVatRuleEntity(1, "INACTIVE", "", "", "", 1);
    var vatRuleEntity3 = createVatRuleEntity(3, "ACTIVE", "", "", "", 3);
    when(vatCacheRepository.findAllUpdatedAfter(any())).thenReturn(
        of(vatRuleEntity1Inactive, vatRuleEntity3));
    Map<Integer, VatRuleEntity> expectedCachedRules = Map.of(2,
        vatRuleEntity2, 3, vatRuleEntity3);

    //Act
    vatRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(vatRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        vatRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(vatCacheRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(vatCacheRepository);
  }

  @Test
  void updateCache__shouldNotUpdateForUnknownStatus() throws IllegalAccessException {
    //Arrange
    var vatRuleEntity1 = createVatRuleEntity(1, "NON-EXISTENT STATUS", "", "", "", 1);
    var vatRuleEntity2 = createVatRuleEntity(2, "ACTIVE", "", "", "", 2);
    when(vatCacheRepository.findAllUpdatedAfter(any())).thenReturn(
        of(vatRuleEntity1, vatRuleEntity2));
    var expectedCachedRules = Map.of(2, vatRuleEntity2);

    //Act
    vatRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(vatRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    verify(vatCacheRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(vatCacheRepository);
  }

  @Test
  void findRoomSubstitutionRules__shouldReturnOk() throws IllegalAccessException {
    //Arrange
    var vatRuleEntity = createVatRuleEntity(1, "ACTIVE", VAT_REGION, PKG_CODE, TRAN_CODE, 1);

    var vatRequest = createVatRequest(List.of(PKG_CODE));
    var vatRuleDomain = createVatRuleDomain(vatRuleEntity);
    setCachedRules(
        of(vatRuleEntity));
    when(vatRuleEntityMapper.toModel(vatRuleEntity)).thenReturn(
        vatRuleDomain);

    //Act
    var response = vatRepositoryCacheOutPort.findVatRules(
        vatRequest);

    //Assert
    assertFalse(response.isEmpty());
    assertThat(response.get(0).getVatRegion()).isEqualTo(
        vatRequest.getVatRegion());
    assertThat(response.get(0).getPkgCode()).isEqualTo(
        vatRequest.getPkgCodeArr().get(0));
    verifyNoMoreInteractions(vatRuleEntityMapper);
    verifyNoInteractions(vatCacheRepository);
  }

  @Test
  void findVatRules_ShouldThrowException(){
    String expectedMessage = "VAT rule not found.";
    var vatRuleRequest = new VatRuleRequest("vatRegion", List.of("pkgCodeArr"));

    //Act
    var exception = assertThrows(RuleEngineException.class, () ->
          vatRepositoryCacheOutPort.findVatRules(vatRuleRequest));

    //Assert
    MatcherAssert.assertThat(exception, notNullValue());
    MatcherAssert.assertThat(exception.getMessage(), is(expectedMessage));
  }

  private VatRuleEntity createVatRuleEntity(Integer ruleId,
      String status, String vatRegion, String pkgCode, String tranCode, Integer delayTime) {
    var time = LocalDateTime.now();
    var vatRuleEntity = new VatRuleEntity();
    vatRuleEntity.setRuleId(ruleId);
    vatRuleEntity.setStatus(status);
    vatRuleEntity.setLastModifiedAt(time.minusMinutes(delayTime));
    vatRuleEntity.setVatRegion(vatRegion);
    vatRuleEntity.setPkgCode(pkgCode);
    vatRuleEntity.setTranCode(tranCode);
    return vatRuleEntity;
  }

  private VatRuleRequest createVatRequest(List<String> pkgCodes) {
    return VatRuleRequest.builder()
        .vatRegion(VatRepositoryCacheOutPortImplTest.VAT_REGION)
        .pkgCodeArr(pkgCodes)
        .build();
  }

  private VatRule createVatRuleDomain(
      VatRuleEntity vatRuleEntity) {
    var time = LocalDateTime.now();
    return VatRule.builder()
        .createdAt(time)
        .lastModifiedAt(time)
        .vatRegion(vatRuleEntity.getVatRegion())
        .pkgCode(vatRuleEntity.getPkgCode())
        .tranCode(vatRuleEntity.getTranCode())
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .build();
  }

  private void setCachedRules(List<VatRuleEntity> entities)
      throws IllegalAccessException {
    Map<Integer, VatRuleEntity> cachedRules = (Map<Integer, VatRuleEntity>) FieldUtils.readField(
        vatRepositoryCacheOutPort,
        "cachedRules",
        true);
    entities.forEach(entity -> cachedRules.put(entity.getRuleId(), entity));
  }

}
