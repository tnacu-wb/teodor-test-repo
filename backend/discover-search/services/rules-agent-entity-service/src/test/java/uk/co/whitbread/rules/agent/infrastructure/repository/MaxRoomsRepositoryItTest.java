package uk.co.whitbread.rules.agent.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.MaxRoomsRuleEntity;

class MaxRoomsRepositoryItTest extends AbstractIntegrationTest {

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

  @Autowired
  public MaxRoomsCacheRepository maxRoomsCacheRepository;

  @BeforeEach
  void setUp() {
    maxRoomsCacheRepository.deleteAll();
  }

  @Test
  void findAllByStatusActive__shouldReturnAListOfRecordsWithStatusActive() {
    //Arrange
    MaxRoomsRuleEntity maxRoomsRuleEntity1 = createMaxRoomsRuleEntity();
    maxRoomsRuleEntity1.setRuleId(1);
    maxRoomsRuleEntity1.setStatus("ACTIVE");
    MaxRoomsRuleEntity maxRoomsRuleEntity2 = createMaxRoomsRuleEntity();
    maxRoomsRuleEntity2.setRuleId(2);
    maxRoomsRuleEntity2.setStatus("ACTIVE");
    MaxRoomsRuleEntity maxRoomsRuleEntity3 = createMaxRoomsRuleEntity();
    maxRoomsRuleEntity3.setRuleId(3);
    maxRoomsRuleEntity3.setStatus("INACTIVE");
    maxRoomsCacheRepository.saveAll(
        List.of(maxRoomsRuleEntity1, maxRoomsRuleEntity2, maxRoomsRuleEntity3));

    //Act
    List<MaxRoomsRuleEntity> foundRules = maxRoomsCacheRepository.findAllByStatusActive();

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(maxRoomsRuleEntity1, maxRoomsRuleEntity2));
  }

  @Test
  void findAllUpdatedAfter_shouldReturnAListOfRecordsWithUpdate() {
    //Arrange
    MaxRoomsRuleEntity maxRoomsRuleEntity1 = createMaxRoomsRuleEntity();
    maxRoomsRuleEntity1.setRuleId(1);
    maxRoomsRuleEntity1.setStatus("ACTIVE");
    maxRoomsRuleEntity1.setLastModifiedAt(LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS));
    MaxRoomsRuleEntity maxRoomsRuleEntity2 = createMaxRoomsRuleEntity();
    maxRoomsRuleEntity2.setRuleId(2);
    maxRoomsRuleEntity2.setStatus("ACTIVE");
    maxRoomsRuleEntity2.setLastModifiedAt(LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MILLIS));
    MaxRoomsRuleEntity maxRoomsRuleEntity3 = createMaxRoomsRuleEntity();
    maxRoomsRuleEntity3.setRuleId(3);
    maxRoomsRuleEntity3.setStatus("ACTIVE");
    maxRoomsCacheRepository.saveAll(
        List.of(maxRoomsRuleEntity1, maxRoomsRuleEntity2, maxRoomsRuleEntity3));

    //Act
    List<MaxRoomsRuleEntity> foundRules = maxRoomsCacheRepository.findAllUpdatedAfter(
        LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(maxRoomsRuleEntity1, maxRoomsRuleEntity2));
  }

  private MaxRoomsRuleEntity createMaxRoomsRuleEntity() {
    MaxRoomsRuleEntity maxRoomsRuleEntity = new MaxRoomsRuleEntity();
    maxRoomsRuleEntity.setRefRuleId(null);
    maxRoomsRuleEntity.setStatus("STATUS");
    maxRoomsRuleEntity.setCreatedAt(NOW);
    maxRoomsRuleEntity.setLastModifiedAt(NOW);
    maxRoomsRuleEntity.setEnableTimestamp(NOW);
    maxRoomsRuleEntity.setDisableTimestamp(NOW);
    maxRoomsRuleEntity.setChannelId("channel id");
    maxRoomsRuleEntity.setMaxRooms(9);
    return maxRoomsRuleEntity;
  }
}
