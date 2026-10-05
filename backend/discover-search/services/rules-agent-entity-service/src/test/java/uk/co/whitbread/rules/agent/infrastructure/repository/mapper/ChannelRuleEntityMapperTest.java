package uk.co.whitbread.rules.agent.infrastructure.repository.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.rules.agent.domain.model.out.ChannelRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.ChannelRuleEntity;

class ChannelRuleEntityMapperTest {

  private final ChannelRuleEntityMapper channelRuleEntityMapper = new ChannelRuleEntityMapperImpl();
  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void toModel_givenEntityObject_shouldTransformToModelObject() {
    //Arrange
    var time = LocalDateTime.now();
    var channelRuleEntity = new ChannelRuleEntity();
    channelRuleEntity.setRuleId(1);
    channelRuleEntity.setCreatedAt(time);
    channelRuleEntity.setLastModifiedAt(time);
    channelRuleEntity.setStatus("ACTIVE");
    channelRuleEntity.setChannel("PI");
    channelRuleEntity.setSubchannel("WEB");
    channelRuleEntity.setLanguage("EN");
    channelRuleEntity.setPms("OP");
    channelRuleEntity.setSourceId("11");
    channelRuleEntity.setRatePlanSets(List.of("PBF"));
    var channelRule = ChannelRule.builder()
        .ruleId(1)
        .createdAt(time)
        .lastModifiedAt(time)
        .status(RuleStatus.ACTIVE)
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .pms("OP")
        .sourceId("11")
        .ratePlanSets(List.of("PBF"))
        .build();

    //Act
    var result = channelRuleEntityMapper.toModel(channelRuleEntity);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(channelRule);
  }

  @Test
  void toModel_givenNullStatus_shouldTransformToModelObjectWithNullStatusAndThrows() {
    //Arrange
    var time = LocalDateTime.now();
    var channelRuleEntity = new ChannelRuleEntity();
    channelRuleEntity.setRuleId(1);
    channelRuleEntity.setCreatedAt(time);
    channelRuleEntity.setLastModifiedAt(time);
    channelRuleEntity.setStatus(null);
    channelRuleEntity.setChannel("PI");
    channelRuleEntity.setSubchannel("WEB");
    channelRuleEntity.setLanguage("EN");
    channelRuleEntity.setPms("OP");
    channelRuleEntity.setSourceId("11");
    channelRuleEntity.setRatePlanSets(List.of("PBF"));
    var expectedMessage = "status: must not be null";

    //Act
    //Assert
    checkErrorThrown(() -> channelRuleEntityMapper.toModel(channelRuleEntity), expectedMessage);
  }

  @Test
  void toModel_givenNullChannel_shouldTransformToModelObjectWithNullChannelAndThrows() {
    //Arrange
    var time = LocalDateTime.now();
    var channelRuleEntity = new ChannelRuleEntity();
    channelRuleEntity.setRuleId(1);
    channelRuleEntity.setCreatedAt(time);
    channelRuleEntity.setLastModifiedAt(time);
    channelRuleEntity.setStatus("ACTIVE");
    channelRuleEntity.setRuleId(1);
    channelRuleEntity.setCreatedAt(time);
    channelRuleEntity.setLastModifiedAt(time);
    channelRuleEntity.setStatus("ACTIVE");
    channelRuleEntity.setChannel(null);
    channelRuleEntity.setSubchannel("WEB");
    channelRuleEntity.setLanguage("EN");
    channelRuleEntity.setPms("OP");
    channelRuleEntity.setSourceId("11");
    channelRuleEntity.setRatePlanSets(List.of("PBF"));
    var expectedMessage = "channel: must not be empty";

    //Act
    //Assert
    checkErrorThrown(() -> channelRuleEntityMapper.toModel(channelRuleEntity), expectedMessage);
  }

  @Test
  void toModel_givenNullSubChannel_shouldTransformToModelObjectWithNullSubChannelAndThrows() {
    //Arrange
    var time = LocalDateTime.now();
    var channelRuleEntity = new ChannelRuleEntity();
    channelRuleEntity.setRuleId(1);
    channelRuleEntity.setCreatedAt(time);
    channelRuleEntity.setLastModifiedAt(time);
    channelRuleEntity.setStatus("ACTIVE");
    channelRuleEntity.setRuleId(1);
    channelRuleEntity.setCreatedAt(time);
    channelRuleEntity.setLastModifiedAt(time);
    channelRuleEntity.setStatus("ACTIVE");
    channelRuleEntity.setChannel("PI");
    channelRuleEntity.setSubchannel(null);
    channelRuleEntity.setLanguage("EN");
    channelRuleEntity.setPms("OP");
    channelRuleEntity.setSourceId("11");
    channelRuleEntity.setRatePlanSets(List.of("PBF"));
    var expectedMessage = "subchannel: must not be empty";

    //Act
    //Assert
    checkErrorThrown(() -> channelRuleEntityMapper.toModel(channelRuleEntity), expectedMessage);
  }

  @Test
  void toModel_givenNullLanguage_shouldTransformToModelObjectWithNullLanguageAndThrows() {
    //Arrange
    var time = LocalDateTime.now();
    var channelRuleEntity = new ChannelRuleEntity();
    channelRuleEntity.setRuleId(1);
    channelRuleEntity.setCreatedAt(time);
    channelRuleEntity.setLastModifiedAt(time);
    channelRuleEntity.setStatus("ACTIVE");
    channelRuleEntity.setRuleId(1);
    channelRuleEntity.setCreatedAt(time);
    channelRuleEntity.setLastModifiedAt(time);
    channelRuleEntity.setStatus("ACTIVE");
    channelRuleEntity.setChannel("PI");
    channelRuleEntity.setSubchannel("WEB");
    channelRuleEntity.setLanguage(null);
    channelRuleEntity.setPms("OP");
    channelRuleEntity.setSourceId("11");
    channelRuleEntity.setRatePlanSets(List.of("PBF"));
    var expectedMessage = "language: must not be empty";

    //Act
    //Assert
    checkErrorThrown(() -> channelRuleEntityMapper.toModel(channelRuleEntity), expectedMessage);
  }

  @Test
  void toModel_givenNullPms_shouldTransformToModelObjectWithNullPmsAndThrows() {
    //Arrange
    var time = LocalDateTime.now();
    var channelRuleEntity = new ChannelRuleEntity();
    channelRuleEntity.setRuleId(1);
    channelRuleEntity.setCreatedAt(time);
    channelRuleEntity.setLastModifiedAt(time);
    channelRuleEntity.setStatus("ACTIVE");
    channelRuleEntity.setRuleId(1);
    channelRuleEntity.setCreatedAt(time);
    channelRuleEntity.setLastModifiedAt(time);
    channelRuleEntity.setStatus("ACTIVE");
    channelRuleEntity.setChannel("PI");
    channelRuleEntity.setSubchannel("WEB");
    channelRuleEntity.setLanguage("EN");
    channelRuleEntity.setPms(null);
    channelRuleEntity.setSourceId("11");
    channelRuleEntity.setRatePlanSets(List.of("PBF"));
    var expectedMessage = "pms: must not be empty";

    //Act
    //Assert
    checkErrorThrown(() -> channelRuleEntityMapper.toModel(channelRuleEntity), expectedMessage);
  }

  @Test
  void toModel_givenNullSourceId_shouldTransformToModelObjectWithNullSourceIdAndThrows() {
    //Arrange
    var time = LocalDateTime.now();
    var channelRuleEntity = new ChannelRuleEntity();
    channelRuleEntity.setRuleId(1);
    channelRuleEntity.setCreatedAt(time);
    channelRuleEntity.setLastModifiedAt(time);
    channelRuleEntity.setStatus("ACTIVE");
    channelRuleEntity.setRuleId(1);
    channelRuleEntity.setCreatedAt(time);
    channelRuleEntity.setLastModifiedAt(time);
    channelRuleEntity.setStatus("ACTIVE");
    channelRuleEntity.setChannel("PI");
    channelRuleEntity.setSubchannel("WEB");
    channelRuleEntity.setLanguage("EN");
    channelRuleEntity.setPms("OP");
    channelRuleEntity.setSourceId(null);
    channelRuleEntity.setRatePlanSets(List.of("PBF"));
    var expectedMessage = "sourceId: must not be empty";

    //Act
    //Assert
    checkErrorThrown(() -> channelRuleEntityMapper.toModel(channelRuleEntity), expectedMessage);
  }

  @Test
  void toModel_givenNullRatePlanSets_shouldTransformToModelObjectWithNullRatePlanSetsAndThrows() {
    //Arrange
    var time = LocalDateTime.now();
    var channelRuleEntity = new ChannelRuleEntity();
    channelRuleEntity.setRuleId(1);
    channelRuleEntity.setCreatedAt(time);
    channelRuleEntity.setLastModifiedAt(time);
    channelRuleEntity.setStatus("ACTIVE");
    channelRuleEntity.setRuleId(1);
    channelRuleEntity.setCreatedAt(time);
    channelRuleEntity.setLastModifiedAt(time);
    channelRuleEntity.setStatus("ACTIVE");
    channelRuleEntity.setChannel("PI");
    channelRuleEntity.setSubchannel("WEB");
    channelRuleEntity.setLanguage("EN");
    channelRuleEntity.setPms("OP");
    channelRuleEntity.setSourceId("11");
    channelRuleEntity.setRatePlanSets(null);
    var expectedMessage = "ratePlanSets: must not be empty";

    //Act
    //Assert
    checkErrorThrown(() -> channelRuleEntityMapper.toModel(channelRuleEntity), expectedMessage);
  }

  @Test
  void toModel_givenNullEntityObject_shouldReturnNull() {
    //Arrange

    //Act
    var result = channelRuleEntityMapper.toModel(null);

    //Assert
    assertThat(result).isNull();
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
