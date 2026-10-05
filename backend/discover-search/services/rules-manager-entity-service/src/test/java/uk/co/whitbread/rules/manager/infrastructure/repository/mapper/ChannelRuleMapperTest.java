package uk.co.whitbread.rules.manager.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.manager.domain.model.in.ChannelRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.ChannelRuleEntity;

@ExtendWith(MockitoExtension.class)
class ChannelRuleMapperTest {

  @InjectMocks
  private ChannelRuleMapperImpl channelRuleMapper;

  @Spy
  private ChannelRuleTransformer channelRuleTransformer;

  @Test
  void toDomain_givenEntityObject_shouldTransformToDomainObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var channelRuleDomain = ChannelRule.builder()
        .ruleId(123)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .pms("OP")
        .sourceId("11")
        .ratePlanSets(List.of("PBF"))
        .build();
    var channelRuleEntity = new ChannelRuleEntity();
    channelRuleEntity.setRuleId(123);
    channelRuleEntity.setRefRuleId(0);
    channelRuleEntity.setStatus("new");
    channelRuleEntity.setCreatedAt(now);
    channelRuleEntity.setLastModifiedAt(now);
    channelRuleEntity.setEnableTimestamp(null);
    channelRuleEntity.setDisableTimestamp(now.plusDays(30));
    channelRuleEntity.setChannel("PI");
    channelRuleEntity.setSubchannel("WEB");
    channelRuleEntity.setLanguage("EN");
    channelRuleEntity.setPms("OP");
    channelRuleEntity.setSourceId("11");
    channelRuleEntity.setRatePlanSets(List.of("PBF"));

    //Act
    var result = channelRuleMapper.toDomainModel(channelRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(channelRuleDomain);
  }

  @Test
  void toDomain_givenNull_shouldReturnNull() {
    //Arrange

    //Act
    var result = channelRuleMapper.toDomainModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenDomainObject_shouldTransformToEntityObject() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var channelRuleDomain = ChannelRule.builder()
        .ruleId(123)
        .refRuleId(0)
        .status(RuleStatus.NEW)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .pms("OP")
        .sourceId("11")
        .ratePlanSets(List.of("PBF"))
        .build();
    var channelRuleEntity = new ChannelRuleEntity();
    channelRuleEntity.setRuleId(123);
    channelRuleEntity.setRefRuleId(0);
    channelRuleEntity.setStatus("NEW");
    channelRuleEntity.setCreatedAt(now);
    channelRuleEntity.setLastModifiedAt(now);
    channelRuleEntity.setEnableTimestamp(null);
    channelRuleEntity.setDisableTimestamp(now.plusDays(30));
    channelRuleEntity.setChannel("PI");
    channelRuleEntity.setSubchannel("WEB");
    channelRuleEntity.setLanguage("EN");
    channelRuleEntity.setPms("OP");
    channelRuleEntity.setSourceId("11");
    channelRuleEntity.setRatePlanSets(List.of("PBF"));

    //Act
    var result = channelRuleMapper.toEntityDto(channelRuleDomain);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(channelRuleEntity);
  }

  @Test
  void toEntityDto_givenNull_shouldReturnNull() {
    //Arrange

    //Act
    var result = channelRuleMapper.toEntityDto(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toEntityDto_givenNullStatus_shouldTransformToEntityObjectWithNullStatus() {
    //Arrange
    LocalDateTime now = LocalDateTime.now();
    var channelRuleDomain = ChannelRule.builder()
        .ruleId(123)
        .refRuleId(0)
        .status(null)
        .createdAt(now)
        .lastModifiedAt(now)
        .enableTimestamp(null)
        .disableTimestamp(now.plusDays(30))
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .pms("OP")
        .sourceId("11")
        .ratePlanSets(List.of("PBF"))
        .build();
    var channelRuleEntity = new ChannelRuleEntity();
    channelRuleEntity.setRuleId(123);
    channelRuleEntity.setRefRuleId(0);
    channelRuleEntity.setStatus(null);
    channelRuleEntity.setCreatedAt(now);
    channelRuleEntity.setLastModifiedAt(now);
    channelRuleEntity.setEnableTimestamp(null);
    channelRuleEntity.setDisableTimestamp(now.plusDays(30));
    channelRuleEntity.setChannel("PI");
    channelRuleEntity.setSubchannel("WEB");
    channelRuleEntity.setLanguage("EN");
    channelRuleEntity.setPms("OP");
    channelRuleEntity.setSourceId("11");
    channelRuleEntity.setRatePlanSets(List.of("11"));

    //Act
    var result = channelRuleMapper.toEntityDto(channelRuleDomain);

    //Assert
    assertThat(result.getStatus()).isNull();
  }
}