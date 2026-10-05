package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.manager.domain.model.in.PaypalRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.PaypalRuleEntity;

@ExtendWith(MockitoExtension.class)
class PaypalRuleMapperTest {

  @InjectMocks
  private PaypalRuleMapperImpl paypalRuleMapper;
  @Spy
  private PaypalRuleTransformer maxRoomsRuleTransformer;

  @Test
  void toDomain_givenEntityObject_shouldTransformToDomainObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var paypalRuleDomain = PaypalRule.builder()
        .ruleId(1234)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .channelId("some channel")
        .country("GB")
        .hotelIdList("hotelId")
        .build();
    var paypalRuleEntity = new PaypalRuleEntity();
    paypalRuleEntity.setRuleId(1234);
    paypalRuleEntity.setRefRuleId(0);
    paypalRuleEntity.setStatus("NEW");
    paypalRuleEntity.setCreatedAt(now);
    paypalRuleEntity.setLastModifiedAt(now);
    paypalRuleEntity.setEnableTimestamp(null);
    paypalRuleEntity.setDisableTimestamp(now.plusDays(30));
    paypalRuleEntity.setChannelId("some channel");
    paypalRuleEntity.setCountry("GB");
    paypalRuleEntity.setHotelIdList("hotelId");

    //Act
    var result = paypalRuleMapper.toDomainModel(paypalRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(paypalRuleDomain);
  }

  @Test
  void toDomain_givenNull_shouldReturnNull() {
    //Arrange

    //Act
    var result = paypalRuleMapper.toDomainModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenDomainObject_shouldTransformToEntityObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var paypalRuleDomain = PaypalRule.builder()
        .ruleId(1234)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .channelId("some channel")
        .country("GB")
        .hotelIdList("hotelId")
        .build();
    var paypalRuleEntity = new PaypalRuleEntity();
    paypalRuleEntity.setRuleId(1234);
    paypalRuleEntity.setRefRuleId(0);
    paypalRuleEntity.setStatus("NEW");
    paypalRuleEntity.setCreatedAt(now);
    paypalRuleEntity.setLastModifiedAt(now);
    paypalRuleEntity.setEnableTimestamp(null);
    paypalRuleEntity.setDisableTimestamp(now.plusDays(30));
    paypalRuleEntity.setChannelId("some channel");
    paypalRuleEntity.setCountry("GB");
    paypalRuleEntity.setHotelIdList("hotelId");

    //Act
    var result = paypalRuleMapper.toEntityDto(paypalRuleDomain);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(paypalRuleEntity);
  }

  @Test
  void toEntityDto_givenNull_shouldReturnNull() {
    //Arrange

    //Act
    var result = paypalRuleMapper.toEntityDto(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenNullStatus_shouldTransformToEntityObjectWithNullStatus() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var paypalRuleDomain = PaypalRule.builder()
        .ruleId(1234)
        .refRuleId(0)
        .status(null)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .channelId("some channel")
        .country("GB")
        .hotelIdList("hotelID")
        .build();
    var paypalRuleEntity = new PaypalRuleEntity();
    paypalRuleEntity.setRuleId(1234);
    paypalRuleEntity.setRefRuleId(0);
    paypalRuleEntity.setStatus(null);
    paypalRuleEntity.setCreatedAt(now);
    paypalRuleEntity.setLastModifiedAt(now);
    paypalRuleEntity.setEnableTimestamp(null);
    paypalRuleEntity.setDisableTimestamp(now.plusDays(30));
    paypalRuleEntity.setChannelId("some channel");
    paypalRuleEntity.setCountry("GB");
    paypalRuleEntity.setHotelIdList("hotelID");

    //Act
    var result = paypalRuleMapper.toEntityDto(paypalRuleDomain);

    //Assert
    assertThat(result.getStatus()).isNull();
  }
}
