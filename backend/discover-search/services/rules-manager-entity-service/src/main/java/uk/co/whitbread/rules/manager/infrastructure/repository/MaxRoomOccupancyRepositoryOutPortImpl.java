package uk.co.whitbread.rules.manager.infrastructure.repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.manager.domain.model.in.MaxRoomOccupancyRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.domain.ports.secondary.RuleEngineRepositoryOutPort;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.MaxRoomOccupancyRuleMapper;

@Slf4j
@RequiredArgsConstructor
public class MaxRoomOccupancyRepositoryOutPortImpl implements
    RuleEngineRepositoryOutPort<MaxRoomOccupancyRule> {

  private static final String TABLE_NAME = "max_room_occupancy_rule";
  private final MaxRoomOccupancyRepository roomOccupancyRepository;
  private final MaxRoomOccupancyRuleMapper roomOccupancyRuleMapper;

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  @Override
  public List<MaxRoomOccupancyRule> findNewRecords() {
    log.debug("Find new records for max room occupancy");
    return roomOccupancyRepository
        .findAllByStatusEqualsIgnoreCase(RuleStatus.NEW.toString())
        .stream()
        .map(roomOccupancyRuleMapper::toDomainModel)
        .toList();
  }

  @Override
  public List<MaxRoomOccupancyRule> findExpiredRecords() {
    log.debug("Find expired records for max room occupancy");
    return roomOccupancyRepository
        .findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(RuleStatus.ACTIVE.toString(),
            LocalDateTime.now(ZoneOffset.UTC))
        .stream()
        .map(roomOccupancyRuleMapper::toDomainModel)
        .toList();
  }

  @Override
  public List<MaxRoomOccupancyRule> findReadyRecords() {
    log.debug("Find ready records for max room occupancy");
    return roomOccupancyRepository
        .findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(RuleStatus.FUTURE.toString(),
            LocalDateTime.now(ZoneOffset.UTC))
        .stream()
        .map(roomOccupancyRuleMapper::toDomainModel)
        .toList();
  }

  @Override
  public void persistRecords(RuleProcessorComposite<MaxRoomOccupancyRule> ruleProcessorComposite) {
    log.debug("Save records for max room occupancy");
    roomOccupancyRepository.save(
        roomOccupancyRuleMapper.toEntityDto(ruleProcessorComposite.getRecordToUpdate()));
    var recordIdToDelete = ruleProcessorComposite.getRecordIdToDelete();
    if (recordIdToDelete != null) {
      roomOccupancyRepository.deleteById(recordIdToDelete);
    }
  }
}
