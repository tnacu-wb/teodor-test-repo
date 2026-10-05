package uk.co.whitbread.rules.agent.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.ChannelRuleEntity;

class ChannelRepositoryItTest extends AbstractIntegrationTest {

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
  @Autowired
  private ChannelRepository channelRepository;

  @BeforeEach
  void setUp() {
    channelRepository.deleteAll();
  }

  @Test
  void findAllByStatusActive__shouldReturnAListOfRecordsWithStatusActive() {
    //Arrange
    ChannelRuleEntity channeRuleEntity1 = createChannelRuleEntity();
    channeRuleEntity1.setRuleId(1);
    channeRuleEntity1.setStatus("ACTIVE");
    ChannelRuleEntity channelRuleEntity2 = createChannelRuleEntity();
    channelRuleEntity2.setRuleId(2);
    channelRuleEntity2.setStatus("ACTIVE");
    ChannelRuleEntity channelRuleEntity3 = createChannelRuleEntity();
    channelRuleEntity3.setRuleId(3);
    channelRuleEntity3.setStatus("INACTIVE");
    channelRepository.saveAll(
        List.of(channeRuleEntity1, channelRuleEntity2, channelRuleEntity3));

    //Act
    List<ChannelRuleEntity> foundRules = channelRepository.findAllByStatusActive();

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(channeRuleEntity1, channelRuleEntity2));
  }

  @Test
  void findAllUpdatedAfter_shouldReturnAListOfRecordsWithUpdate() {
    //Arrange
    ChannelRuleEntity channelRuleEntity1 = createChannelRuleEntity();
    channelRuleEntity1.setRuleId(1);
    channelRuleEntity1.setStatus("ACTIVE");
    channelRuleEntity1.setLastModifiedAt(
        LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS));
    ChannelRuleEntity channelRuleEntity2 = createChannelRuleEntity();
    channelRuleEntity2.setRuleId(2);
    channelRuleEntity2.setStatus("ACTIVE");
    channelRuleEntity2.setLastModifiedAt(
        LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS));
    ChannelRuleEntity channelRuleEntity3 = createChannelRuleEntity();
    channelRuleEntity3.setRuleId(3);
    channelRuleEntity3.setStatus("ACTIVE");
    channelRepository.saveAll(
        List.of(channelRuleEntity1, channelRuleEntity2, channelRuleEntity3));

    //Act
    List<ChannelRuleEntity> foundRules = channelRepository.findAllUpdatedAfter(
        LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(channelRuleEntity1, channelRuleEntity2));
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
    channelRuleEntity.setRatePlanSets(Collections.emptyList());
    return channelRuleEntity;
  }
}
