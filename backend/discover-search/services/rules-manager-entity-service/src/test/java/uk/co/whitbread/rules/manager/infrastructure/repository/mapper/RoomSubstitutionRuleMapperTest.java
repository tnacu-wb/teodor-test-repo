package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.manager.domain.model.in.RoomSubstitutionRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.RoomSubstitutionRuleEntity;

@ExtendWith(MockitoExtension.class)
class RoomSubstitutionRuleMapperTest {

  @InjectMocks
  private RoomSubstitutionRuleMapperImpl roomSubstitutionRuleMapper;
  @Spy
  private RoomSubstitutionRuleTransformer roomSubstitutionRuleTransformer;

  @Test
  void toDomain_givenEntityObject_shouldTransformToDomainObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var roomSubstitutionRuleDomain = RoomSubstitutionRule.builder()
        .ruleId(1)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .pms("OP")
        .adults(2)
        .children(0)
        .roomType("DB")
        .pmsRoomType("DBLWIN")
        .offerOrder(1)
        .specialRequest("DBLE")
        .build();

    var roomSubstitutionRuleEntity = new RoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity.setRuleId(1);
    roomSubstitutionRuleEntity.setRefRuleId(0);
    roomSubstitutionRuleEntity.setStatus("NEW");
    roomSubstitutionRuleEntity.setCreatedAt(now);
    roomSubstitutionRuleEntity.setLastModifiedAt(now);
    roomSubstitutionRuleEntity.setEnableTimestamp(null);
    roomSubstitutionRuleEntity.setDisableTimestamp(now.plusDays(30));
    roomSubstitutionRuleEntity.setPms("OP");
    roomSubstitutionRuleEntity.setAdults(2);
    roomSubstitutionRuleEntity.setChildren(0);
    roomSubstitutionRuleEntity.setRoomType("DB");
    roomSubstitutionRuleEntity.setPmsRoomType("DBLWIN");
    roomSubstitutionRuleEntity.setOfferOrder(1);
    roomSubstitutionRuleEntity.setSpecialRequest("DBLE");

    //Act
    var result = roomSubstitutionRuleMapper.toDomainModel(roomSubstitutionRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison().withStrictTypeChecking()
        .isEqualTo(roomSubstitutionRuleDomain);

  }

  @Test
  void toDomain_givenNull_shouldReturnNull() {
    //Act
    var result = roomSubstitutionRuleMapper.toDomainModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenDomainObject_shouldTransformToEntityObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var roomSubstitutionRuleDomain = RoomSubstitutionRule.builder()
        .ruleId(1)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .pms("OP")
        .adults(2)
        .children(0)
        .roomType("DB")
        .pmsRoomType("DBLWIN")
        .offerOrder(1)
        .specialRequest("DBLE")
        .build();

    var roomSubstitutionRuleEntity = new RoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity.setRuleId(1);
    roomSubstitutionRuleEntity.setRefRuleId(0);
    roomSubstitutionRuleEntity.setStatus("NEW");
    roomSubstitutionRuleEntity.setCreatedAt(now);
    roomSubstitutionRuleEntity.setLastModifiedAt(now);
    roomSubstitutionRuleEntity.setEnableTimestamp(null);
    roomSubstitutionRuleEntity.setDisableTimestamp(now.plusDays(30));
    roomSubstitutionRuleEntity.setPms("OP");
    roomSubstitutionRuleEntity.setAdults(2);
    roomSubstitutionRuleEntity.setChildren(0);
    roomSubstitutionRuleEntity.setRoomType("DB");
    roomSubstitutionRuleEntity.setPmsRoomType("DBLWIN");
    roomSubstitutionRuleEntity.setOfferOrder(1);
    roomSubstitutionRuleEntity.setSpecialRequest("DBLE");

    //Act
    var result = roomSubstitutionRuleMapper.toEntityDto(roomSubstitutionRuleDomain);

    //Assert
    assertThat(result).usingRecursiveComparison().withStrictTypeChecking()
        .isEqualTo(roomSubstitutionRuleEntity);
  }

  @Test
  void toEntityDto_givenNull_shouldReturnNull() {
    //Act
    var result = roomSubstitutionRuleMapper.toEntityDto(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenNullStatus_shouldTransformToEntityObjectWithNullStatus() {
    LocalDateTime now = LocalDateTime.now();
    var roomSubstitutionRuleDomain = RoomSubstitutionRule.builder()
        .ruleId(1)
        .refRuleId(0)
        .status(null)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .pms("OP")
        .adults(2)
        .children(0)
        .roomType("DB")
        .pmsRoomType("DBLWIN")
        .offerOrder(1)
        .specialRequest("DBLE")
        .build();

    var roomSubstitutionRuleEntity = new RoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity.setRuleId(1);
    roomSubstitutionRuleEntity.setRefRuleId(0);
    roomSubstitutionRuleEntity.setStatus(null);
    roomSubstitutionRuleEntity.setCreatedAt(now);
    roomSubstitutionRuleEntity.setLastModifiedAt(now);
    roomSubstitutionRuleEntity.setEnableTimestamp(null);
    roomSubstitutionRuleEntity.setDisableTimestamp(now.plusDays(30));
    roomSubstitutionRuleEntity.setPms("OP");
    roomSubstitutionRuleEntity.setAdults(2);
    roomSubstitutionRuleEntity.setChildren(0);
    roomSubstitutionRuleEntity.setRoomType("DB");
    roomSubstitutionRuleEntity.setPmsRoomType("DBLWIN");
    roomSubstitutionRuleEntity.setOfferOrder(1);
    roomSubstitutionRuleEntity.setSpecialRequest("DBLE");

    //Act
    var result = roomSubstitutionRuleMapper.toEntityDto(roomSubstitutionRuleDomain);

    //Assert
    assertThat(result.getStatus()).isNull();
  }


}
