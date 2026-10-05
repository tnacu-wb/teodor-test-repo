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
import uk.co.whitbread.rules.agent.domain.model.in.PaypalRuleRequest;
import uk.co.whitbread.rules.agent.infrastructure.repository.mapper.PaypalRuleEntityMapper;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.PaypalRuleEntity;

@ExtendWith(MockitoExtension.class)
class PaypalRepositoryCacheOutPortImplTest {

  @Mock
  private PaypalCacheRepository paypalCacheRepository;

  @InjectMocks
  private PaypalRepositoryCacheOutPortImpl paypalRepositoryCacheOutPort;

  @Mock
  private PaypalRuleEntityMapper paypalRuleEntityMapper;

  @Test
  void cacheRules__shouldCacheAllActiveRules() throws IllegalAccessException {
    //Arrange
    var rre1 = createPaypalRuleEntity(1, "ACTIVE");
    var rre2 = createPaypalRuleEntity(2, "ACTIVE");
    when(paypalCacheRepository.findAllByStatusActive()).thenReturn(List.of(rre1, rre2));
    Map<Integer, PaypalRuleEntity> expectedCachedRules = Map.of(1, rre1, 2, rre2);

    //Act
    paypalRepositoryCacheOutPort.cacheRules();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(paypalRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        paypalRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(paypalCacheRepository).findAllByStatusActive();
    verifyNoMoreInteractions(paypalCacheRepository);
  }

  @Test
  void updateCache__shouldUpdateCachedRules() throws IllegalAccessException {
    //Arrange
    var paypalRuleEntity1 = createPaypalRuleEntity(1, "ACTIVE");
    var paypalRuleEntity2 = createPaypalRuleEntity(2, "ACTIVE");
    when(paypalCacheRepository.findAllByStatusActive()).thenReturn(
        List.of(paypalRuleEntity1, paypalRuleEntity2));
    paypalRepositoryCacheOutPort.cacheRules();
    var paypalRuleEntityInactive = createPaypalRuleEntity(1, "INACTIVE");
    var paypalRuleEntity3 = createPaypalRuleEntity(3, "ACTIVE");
    when(paypalCacheRepository.findAllUpdatedAfter(any())).thenReturn(
        List.of(paypalRuleEntityInactive, paypalRuleEntity3));
    Map<Integer, PaypalRuleEntity> expectedCachedRules = Map.of(2, paypalRuleEntity2, 3,
        paypalRuleEntity3);

    //Act
    paypalRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(paypalRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    var actualLastCacheUpdate = (LocalDateTime) FieldUtils.readField(
        paypalRepositoryCacheOutPort, "lastCacheUpdate",
        true);
    assertThat(actualLastCacheUpdate).isBeforeOrEqualTo(LocalDateTime.now(ZoneOffset.UTC));
    verify(paypalCacheRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(paypalCacheRepository);
  }

  @Test
  void updateCache__shouldNotUpdateForUnknownStatus() throws IllegalAccessException {
    //Arrange
    var paypalRuleEntity1 = createPaypalRuleEntity(1, "THIS WOULD BE SOMETHING RANDOM");
    var paypalRuleEntity2 = createPaypalRuleEntity(2, "ACTIVE");
    when(paypalCacheRepository.findAllUpdatedAfter(any())).thenReturn(
        List.of(paypalRuleEntity1, paypalRuleEntity2));
    var expectedCachedRules = Map.of(2, paypalRuleEntity2);

    //Act
    paypalRepositoryCacheOutPort.updateCache();

    //Assert
    var actualCachedRules = (Map<?, ?>) FieldUtils.readField(paypalRepositoryCacheOutPort,
        "cachedRules",
        true);
    assertThat(actualCachedRules).usingRecursiveComparison()
        .isEqualTo(expectedCachedRules);
    verify(paypalCacheRepository).findAllUpdatedAfter(any());
    verifyNoMoreInteractions(paypalCacheRepository);
  }

  @Test
  void findPaypalRule__shouldReturnOk() throws IllegalAccessException {
    //Arrange
    var paypalEntity1 = createPaypalRuleEntity(1, "GB", "PI", "hotelID", 1);
    var paypalEntity2 = createPaypalRuleEntity(2, "DE", "BB", "hotelID", 2);
    setCachedRules(List.of(paypalEntity1, paypalEntity2));

    //Act
    var response = paypalRepositoryCacheOutPort.getPaypalHasAccess("PI", "GB", "hotelID");

    //Assert
    assertThat(response).isTrue();
    verifyNoMoreInteractions(paypalRuleEntityMapper);
    verifyNoInteractions(paypalCacheRepository);
  }

  @Test
  void findPaypalRule_withResourceId_shouldReturnOptionalEmtpy() throws IllegalAccessException {
    //Arrange
    var paypalEntity1 = createPaypalRuleEntity(1, "GB", "PI", "hotelID", 1);
    var paypalEntity2 = createPaypalRuleEntity(2, "DE", "BB", "hotelID", 2);
    var paypalRequest = createPaypalRequest("PI");
    setCachedRules(List.of(paypalEntity1, paypalEntity2));

    //Act
    var response = paypalRepositoryCacheOutPort.getPaypalHasAccess(paypalRequest.getChannelId(),
        paypalRequest.getCountry(), paypalRequest.getHotelId());

    //Assert
    assertThat(response).isTrue();
    verifyNoMoreInteractions(paypalRuleEntityMapper);
    verifyNoInteractions(paypalCacheRepository);
  }

  @Test
  void findPaypalRule_withRoleID_shouldReturnOptionalEmtpy() throws IllegalAccessException {
    //Arrange
    var paypalEntity1 = createPaypalRuleEntity(1, "GB", "PI", "hotelID", 1);
    var paypalEntity2 = createPaypalRuleEntity(2, "DE", "BB", "hotelID", 2);
    var paypalRequest = createPaypalRequest("PI");
    setCachedRules(List.of(paypalEntity1, paypalEntity2));

    //Act
    var response = paypalRepositoryCacheOutPort.getPaypalHasAccess(paypalRequest.getChannelId(),
        paypalRequest.getCountry(), paypalRequest.getHotelId());

    //Assert
    assertThat(response).isNotNull();
    verifyNoMoreInteractions(paypalRuleEntityMapper);
    verifyNoInteractions(paypalCacheRepository);
  }

  private PaypalRuleEntity createPaypalRuleEntity(Integer ruleId, String status) {
    var PaypalRuleEntity = new PaypalRuleEntity();
    PaypalRuleEntity.setRuleId(ruleId);
    PaypalRuleEntity.setStatus(status);
    return PaypalRuleEntity;
  }

  private PaypalRuleRequest createPaypalRequest(String channelId) {
    return PaypalRuleRequest.builder()
        .channelId(channelId)
        .country("GB")
        .hotelId("hotelID")
        .build();
  }

  private PaypalRuleEntity createPaypalRuleEntity(Integer ruleId, String country,
      String channelId, String hotelID, Integer delayTime) {
    var paypalRuleEntity = new PaypalRuleEntity();
    var time = LocalDateTime.now();
    paypalRuleEntity.setRuleId(ruleId);
    paypalRuleEntity.setCountryCode(country);
    paypalRuleEntity.setHotelId(hotelID);
    paypalRuleEntity.setChannelId(channelId);
    paypalRuleEntity.setLastModifiedAt(time.minusMinutes(delayTime));
    return paypalRuleEntity;
  }

  private void setCachedRules(List<PaypalRuleEntity> entities) throws IllegalAccessException {
    Map<Integer, PaypalRuleEntity> cachedRules = (Map<Integer, PaypalRuleEntity>) FieldUtils.readField(
        paypalRepositoryCacheOutPort,
        "cachedRules",
        true);
    entities.forEach(entity -> cachedRules.put(entity.getRuleId(), entity));
  }
}
