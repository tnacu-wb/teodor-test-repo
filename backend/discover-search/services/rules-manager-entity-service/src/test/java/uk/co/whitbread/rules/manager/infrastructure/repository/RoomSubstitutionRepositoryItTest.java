package uk.co.whitbread.rules.manager.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.RoomSubstitutionRuleEntity;

class RoomSubstitutionRepositoryItTest extends AbstractIntegrationTest {

  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

  @Autowired
  private RoomSubstitutionRepository roomSubstitutionRepository;

  @BeforeEach
  void setUp() {
    roomSubstitutionRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    roomSubstitutionRepository.deleteAll();
  }

  @Test
  void save_shouldSaveInDb() {
    //Arrange
    RoomSubstitutionRuleEntity roomSubstitutionRuleEntity = createRoomSubstitutionRuleEntity();

    //Act
    RoomSubstitutionRuleEntity savedRoomSubstitutionRuleEntity = roomSubstitutionRepository.save(
        roomSubstitutionRuleEntity);

    //Assert
    assertThat(savedRoomSubstitutionRuleEntity).usingRecursiveComparison()
        .isEqualTo(roomSubstitutionRuleEntity);
  }

  @Test
  void findAllByStatusEqualsIgnoreCase_shouldReturnAListOfRecordsWithSameStatus() {
    //Arrange
    RoomSubstitutionRuleEntity roomSubstitutionRuleEntity1 = createRoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity1.setStatus("Searched Status");
    RoomSubstitutionRuleEntity roomSubstitutionRuleEntity2 = createRoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity2.setStatus("searched status");
    RoomSubstitutionRuleEntity roomSubstitutionRuleEntity3 = createRoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity3.setStatus("another status");
    roomSubstitutionRepository.saveAll(
        List.of(roomSubstitutionRuleEntity1, roomSubstitutionRuleEntity2,
            roomSubstitutionRuleEntity3));

    //Act
    List<RoomSubstitutionRuleEntity> foundRules = roomSubstitutionRepository.findAllByStatusEqualsIgnoreCase(
        "SEARCHED STATUS");

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(roomSubstitutionRuleEntity1, roomSubstitutionRuleEntity2));
  }

  @Test
  void findAllByStatusAndDisableTimestampIsBefore_shouldReturnRulesThatNeedsToBeDisable() {
    //Arrange
    RoomSubstitutionRuleEntity roomSubstitutionRuleEntity1 = createRoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity1.setStatus("Searched Status");
    roomSubstitutionRuleEntity1.setDisableTimestamp(NOW.plusDays(1));
    RoomSubstitutionRuleEntity roomSubstitutionRuleEntity2 = createRoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity2.setStatus("searched status");
    roomSubstitutionRuleEntity2.setDisableTimestamp(NOW.minusDays(1));
    RoomSubstitutionRuleEntity roomSubstitutionRuleEntity3 = createRoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity3.setStatus("another status");
    roomSubstitutionRuleEntity3.setDisableTimestamp(NOW.plusDays(1));
    roomSubstitutionRepository.saveAll(
        List.of(roomSubstitutionRuleEntity1, roomSubstitutionRuleEntity2,
            roomSubstitutionRuleEntity3));

    //Act
    List<RoomSubstitutionRuleEntity> foundRules = roomSubstitutionRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
        "SEARCHED STATUS", LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(roomSubstitutionRuleEntity2));
  }

  @Test
  void findAllByStatusAndEnableTimestampIsBefore_shouldReturnRulesThatNeedsToBeEnable() {
    //Arrange
    RoomSubstitutionRuleEntity roomSubstitutionRuleEntity1 = createRoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity1.setStatus("Searched Status");
    roomSubstitutionRuleEntity1.setEnableTimestamp(NOW.minusDays(1));
    RoomSubstitutionRuleEntity roomSubstitutionRuleEntity2 = createRoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity2.setStatus("searched status");
    roomSubstitutionRuleEntity2.setEnableTimestamp(NOW.plusDays(1));
    RoomSubstitutionRuleEntity roomSubstitutionRuleEntity3 = createRoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity3.setStatus("another status");
    roomSubstitutionRuleEntity3.setEnableTimestamp(NOW.minusDays(1));
    roomSubstitutionRepository.saveAll(
        List.of(roomSubstitutionRuleEntity1, roomSubstitutionRuleEntity2,
            roomSubstitutionRuleEntity3));

    //Act
    List<RoomSubstitutionRuleEntity> foundRules = roomSubstitutionRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
        "SEARCHED STATUS", LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(roomSubstitutionRuleEntity1));
  }

  private RoomSubstitutionRuleEntity createRoomSubstitutionRuleEntity() {
    RoomSubstitutionRuleEntity roomSubstitutionRuleEntity = new RoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity.setRefRuleId(null);
    roomSubstitutionRuleEntity.setStatus("STATUS");
    roomSubstitutionRuleEntity.setCreatedAt(NOW);
    roomSubstitutionRuleEntity.setLastModifiedAt(NOW);
    roomSubstitutionRuleEntity.setEnableTimestamp(NOW);
    roomSubstitutionRuleEntity.setDisableTimestamp(NOW);
    roomSubstitutionRuleEntity.setPms("OP");
    roomSubstitutionRuleEntity.setAdults(2);
    roomSubstitutionRuleEntity.setChildren(0);
    roomSubstitutionRuleEntity.setRoomType("DB");
    roomSubstitutionRuleEntity.setPmsRoomType("DBLWIN");
    roomSubstitutionRuleEntity.setOfferOrder(1);
    roomSubstitutionRuleEntity.setSpecialRequest("DBLE");
    roomSubstitutionRuleEntity.setChannel("PI");

    return roomSubstitutionRuleEntity;
  }


}
