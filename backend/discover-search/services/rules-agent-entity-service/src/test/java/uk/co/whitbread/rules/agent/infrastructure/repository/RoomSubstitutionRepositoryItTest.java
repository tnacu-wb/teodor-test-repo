package uk.co.whitbread.rules.agent.infrastructure.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.co.whitbread.rules.agent.domain.model.out.RuleStatus.ACTIVE;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.rules.agent.infrastructure.repository.model.RoomSubstitutionRuleEntity;

class RoomSubstitutionRepositoryItTest extends AbstractIntegrationTest {

  private static final String DOUBLE = "Double";
  private static final String OP = "OP";
  private static final LocalDateTime NOW = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

  @Autowired
  public RoomSubstitutionCacheRepository roomSubstitutionCacheRepository;

  @BeforeEach
  void setUp() {
    roomSubstitutionCacheRepository.deleteAll();
  }

  @Test
  void findAllByStatusActive__shouldReturnAListOfRecordsWithStatusActive() {
    //Arrange
    RoomSubstitutionRuleEntity roomSubstitutionRuleEntity1 = createRoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity1.setRuleId(1);
    roomSubstitutionRuleEntity1.setStatus("ACTIVE");
    RoomSubstitutionRuleEntity roomSubstitutionRuleEntity2 = createRoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity2.setRuleId(2);
    roomSubstitutionRuleEntity2.setStatus("ACTIVE");
    RoomSubstitutionRuleEntity roomSubstitutionRuleEntity3 = createRoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity3.setRuleId(3);
    roomSubstitutionRuleEntity3.setStatus("INACTIVE");
    roomSubstitutionCacheRepository.saveAll(
        List.of(roomSubstitutionRuleEntity1, roomSubstitutionRuleEntity2, roomSubstitutionRuleEntity3));

    //Act
    List<RoomSubstitutionRuleEntity> foundRules = roomSubstitutionCacheRepository.findAllByStatusActive();

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(roomSubstitutionRuleEntity1, roomSubstitutionRuleEntity2));
  }

  @Test
  void findAllUpdatedAfter_shouldReturnAListOfRecordsWithUpdate() {
    //Arrange
    RoomSubstitutionRuleEntity roomSubstitutionRuleEntity1 = createRoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity1.setRuleId(1);
    roomSubstitutionRuleEntity1.setStatus("ACTIVE");
    roomSubstitutionRuleEntity1.setLastModifiedAt(LocalDateTime.now().plusMinutes(1)
        .truncatedTo(ChronoUnit.MILLIS));
    RoomSubstitutionRuleEntity roomSubstitutionRuleEntity2 = createRoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity2.setRuleId(2);
    roomSubstitutionRuleEntity2.setStatus("ACTIVE");
    roomSubstitutionRuleEntity2.setLastModifiedAt(LocalDateTime.now().plusMinutes(1)
        .truncatedTo(ChronoUnit.MILLIS));
    RoomSubstitutionRuleEntity roomSubstitutionRuleEntity3 = createRoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity3.setRuleId(3);
    roomSubstitutionRuleEntity3.setStatus("ACTIVE");
    roomSubstitutionCacheRepository.saveAll(
        List.of(roomSubstitutionRuleEntity1, roomSubstitutionRuleEntity2, roomSubstitutionRuleEntity3));

    //Act
    List<RoomSubstitutionRuleEntity> foundRules = roomSubstitutionCacheRepository.findAllUpdatedAfter(
        LocalDateTime.now());

    //Assert
    assertThat(foundRules).usingRecursiveComparison()
        .isEqualTo(List.of(roomSubstitutionRuleEntity1, roomSubstitutionRuleEntity2));
  }

  private RoomSubstitutionRuleEntity createRoomSubstitutionRuleEntity() {
    RoomSubstitutionRuleEntity roomSubstitutionRuleEntity = new RoomSubstitutionRuleEntity();
    roomSubstitutionRuleEntity.setAccessibleSpecialRequest("");
    roomSubstitutionRuleEntity.setAdults(2);
    roomSubstitutionRuleEntity.setChildren(0);
    roomSubstitutionRuleEntity.setCreatedAt(NOW);
    roomSubstitutionRuleEntity.setDisableTimestamp(NOW);
    roomSubstitutionRuleEntity.setEnableTimestamp(NOW);
    roomSubstitutionRuleEntity.setLastModifiedAt(NOW);
    roomSubstitutionRuleEntity.setOfferOrder(1);
    roomSubstitutionRuleEntity.setPkgCode("");
    roomSubstitutionRuleEntity.setPms(OP);
    roomSubstitutionRuleEntity.setPmsRoomType("DBLWIN");
    roomSubstitutionRuleEntity.setRefRuleId(null);
    roomSubstitutionRuleEntity.setRoomType(DOUBLE);
    roomSubstitutionRuleEntity.setSpecialRequest("DBLE");
    roomSubstitutionRuleEntity.setStatus(ACTIVE.name());
    return roomSubstitutionRuleEntity;
  }
}
