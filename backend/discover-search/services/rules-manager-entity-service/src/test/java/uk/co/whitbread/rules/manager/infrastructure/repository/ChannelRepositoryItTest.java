package uk.co.whitbread.rules.manager.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.ChannelRuleEntity;

class ChannelRepositoryItTest extends AbstractIntegrationTest {

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

  @Autowired
  private ChannelRepository channelRepository;

  @BeforeEach
  void setUp() {
    channelRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    channelRepository.deleteAll();
  }

  @Test
  void save__shouldSaveInDb() {
    //Arrange
    ChannelRuleEntity channelRuleEntity = createChannelRuleEntity();

    //Act
    ChannelRuleEntity savedChannelRuleEntity = channelRepository.save(channelRuleEntity);

    //Assert
    assertThat(savedChannelRuleEntity).usingRecursiveComparison()
        .isEqualTo(channelRuleEntity);
  }

  @Test
  void findAllByStatusEqualsIgnoreCase__shouldReturnAListOfRecordsWithSameStatus() {
    //Arrange
    ChannelRuleEntity channelRuleEntity1 = createChannelRuleEntity();
    channelRuleEntity1.setStatus("Searched Status");
    channelRuleEntity1.setChannel("PI");
    channelRuleEntity1.setSubchannel("WEB");
    channelRuleEntity1.setLanguage("EN");
    channelRuleEntity1.setSourceId("11");
    ChannelRuleEntity channelRuleEntity2 = createChannelRuleEntity();
    channelRuleEntity2.setStatus("searched status");
    channelRuleEntity2.setChannel("PI");
    channelRuleEntity2.setSubchannel("WEB");
    channelRuleEntity2.setLanguage("DE");
    channelRuleEntity2.setSourceId("22");
    ChannelRuleEntity channelRuleEntity3 = createChannelRuleEntity();
    channelRuleEntity3.setStatus("another status");
    channelRuleEntity3.setChannel("PI");
    channelRuleEntity3.setSubchannel("WEB");
    channelRuleEntity3.setLanguage("FR");
    channelRuleEntity3.setSourceId("33");

    channelRepository.saveAll(
        List.of(channelRuleEntity1, channelRuleEntity2, channelRuleEntity3));

    //Act
    List<ChannelRuleEntity> foundRules = channelRepository.findAllByStatusEqualsIgnoreCase(
        "SEARCHED STATUS");

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(channelRuleEntity1, channelRuleEntity2));
  }

  @Test
  void findAllByStatusAndDisableTimestampIsBefore__shouldReturnRulesThatNeedsToBeDisable() {
    //Arrange
    ChannelRuleEntity channelRuleEntity1 = createChannelRuleEntity();
    channelRuleEntity1.setStatus("Searched Status");
    channelRuleEntity1.setDisableTimestamp(NOW.plusDays(1));
    channelRuleEntity1.setChannel("PI");
    channelRuleEntity1.setSubchannel("WEB");
    channelRuleEntity1.setLanguage("EN");
    channelRuleEntity1.setSourceId("11");
    ChannelRuleEntity channelRuleEntity2 = createChannelRuleEntity();
    channelRuleEntity2.setStatus("searched status");
    channelRuleEntity2.setDisableTimestamp(NOW.minusDays(1));
    channelRuleEntity2.setChannel("PI");
    channelRuleEntity2.setSubchannel("WEB");
    channelRuleEntity2.setLanguage("DE");
    channelRuleEntity2.setSourceId("22");
    ChannelRuleEntity channelRuleEntity3 = createChannelRuleEntity();
    channelRuleEntity3.setStatus("another status");
    channelRuleEntity3.setDisableTimestamp(NOW.plusDays(1));
    channelRuleEntity3.setChannel("PI");
    channelRuleEntity3.setSubchannel("WEB");
    channelRuleEntity3.setLanguage("FR");
    channelRuleEntity3.setSourceId("33");

    channelRepository.saveAll(
        List.of(channelRuleEntity1, channelRuleEntity2, channelRuleEntity3));

    //Act
    List<ChannelRuleEntity> foundRules = channelRepository
        .findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore("SEARCHED STATUS",
            LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(channelRuleEntity2));
  }

  @Test
  void findAllByStatusAndEnableTimestampIsBefore__shouldReturnRulesThatNeedsToBeEnable() {
    //Arrange
    ChannelRuleEntity channelRuleEntity1 = createChannelRuleEntity();
    channelRuleEntity1.setStatus("Searched Status");
    channelRuleEntity1.setEnableTimestamp(NOW.minusDays(1));
    channelRuleEntity1.setChannel("PI");
    channelRuleEntity1.setSubchannel("WEB");
    channelRuleEntity1.setLanguage("EN");
    channelRuleEntity1.setSourceId("11");
    ChannelRuleEntity channelRuleEntity2 = createChannelRuleEntity();
    channelRuleEntity2.setStatus("searched status");
    channelRuleEntity2.setEnableTimestamp(NOW.plusDays(1));
    channelRuleEntity2.setChannel("PI");
    channelRuleEntity2.setSubchannel("WEB");
    channelRuleEntity2.setLanguage("DE");
    channelRuleEntity2.setSourceId("22");
    ChannelRuleEntity channelRuleEntity3 = createChannelRuleEntity();
    channelRuleEntity3.setStatus("another status");
    channelRuleEntity3.setEnableTimestamp(NOW.minusDays(1));
    channelRuleEntity3.setChannel("PI");
    channelRuleEntity3.setSubchannel("WEB");
    channelRuleEntity3.setLanguage("FR");
    channelRuleEntity3.setSourceId("33");

    channelRepository.saveAll(
        List.of(channelRuleEntity1, channelRuleEntity2, channelRuleEntity3));

    //Act
    List<ChannelRuleEntity> foundRules = channelRepository
        .findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore("SEARCHED STATUS",
            LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(channelRuleEntity1));
  }

  private ChannelRuleEntity createChannelRuleEntity() {
    ChannelRuleEntity channelRuleEntity = new ChannelRuleEntity();
    channelRuleEntity.setRefRuleId(null);
    channelRuleEntity.setStatus("STATUS");
    channelRuleEntity.setCreatedAt(NOW);
    channelRuleEntity.setLastModifiedAt(NOW);
    channelRuleEntity.setEnableTimestamp(NOW);
    channelRuleEntity.setDisableTimestamp(NOW);
    channelRuleEntity.setChannel("PI");
    channelRuleEntity.setSubchannel("WEB");
    channelRuleEntity.setLanguage("EN");
    channelRuleEntity.setPms("OP");
    channelRuleEntity.setSourceId("11");
    channelRuleEntity.setRatePlanSets(List.of("PBN"));
    return channelRuleEntity;
  }
}
