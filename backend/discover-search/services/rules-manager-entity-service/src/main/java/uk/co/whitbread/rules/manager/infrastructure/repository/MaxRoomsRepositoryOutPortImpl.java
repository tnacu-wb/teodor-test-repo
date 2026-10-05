package uk.co.whitbread.rules.manager.infrastructure.repository;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.manager.domain.model.in.MaxRoomsRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.domain.ports.secondary.RuleEngineRepositoryOutPort;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.MaxRoomsRuleMapper;

@Slf4j
@RequiredArgsConstructor
public class MaxRoomsRepositoryOutPortImpl implements RuleEngineRepositoryOutPort<MaxRoomsRule> {

  private static final String TABLE_NAME = "max_Rooms_rule";
  private final MaxRoomsRepository maxRoomsRepository;
  private final MaxRoomsRuleMapper maxRoomsRuleMapper;

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  @Override
  public List<MaxRoomsRule> findNewRecords() {
    log.debug("Find new records for max rooms");
    return maxRoomsRepository
        .findAllByStatusEqualsIgnoreCase(RuleStatus.NEW.toString())
        .stream()
        .map(maxRoomsRuleMapper::toDomainModel)
        .toList();
  }

  @Override
  public List<MaxRoomsRule> findExpiredRecords() {
    log.debug("Find expired records for max rooms");
    return maxRoomsRepository
        .findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(RuleStatus.ACTIVE.toString(),
            LocalDateTime.now(ZoneOffset.UTC))
        .stream()
        .map(maxRoomsRuleMapper::toDomainModel)
        .toList();
  }

  @Override
  public List<MaxRoomsRule> findReadyRecords() {
    log.debug("Find ready records for max rooms");
    return maxRoomsRepository
        .findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(RuleStatus.FUTURE.toString(),
            LocalDateTime.now(ZoneOffset.UTC))
        .stream()
        .map(maxRoomsRuleMapper::toDomainModel)
        .toList();
  }

  @Override
  @Transactional
  public void persistRecords(RuleProcessorComposite<MaxRoomsRule> ruleProcessorComposite) {
    log.debug("Save records for max rooms");
    maxRoomsRepository.save(
        maxRoomsRuleMapper.toEntityDto(ruleProcessorComposite.getRecordToUpdate()));
    var recordIdToDelete = ruleProcessorComposite.getRecordIdToDelete();
    if (recordIdToDelete != null) {
      maxRoomsRepository.deleteById(recordIdToDelete);
    }
  }
}
