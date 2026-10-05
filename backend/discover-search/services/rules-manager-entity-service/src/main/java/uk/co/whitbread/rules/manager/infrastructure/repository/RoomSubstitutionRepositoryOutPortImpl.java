package uk.co.whitbread.rules.manager.infrastructure.repository;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.manager.domain.model.in.RoomSubstitutionRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.domain.ports.secondary.RuleEngineRepositoryOutPort;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.RoomSubstitutionRuleMapper;

@Slf4j
@RequiredArgsConstructor
public class RoomSubstitutionRepositoryOutPortImpl implements
    RuleEngineRepositoryOutPort<RoomSubstitutionRule> {

  private static final String TABLE_NAME = "room_substitution_rule";
  private final RoomSubstitutionRepository roomSubstitutionRepository;
  private final RoomSubstitutionRuleMapper roomSubstitutionRuleMapper;

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  @Override
  public List<RoomSubstitutionRule> findNewRecords() {
    log.debug("Find new records for room substitution");
    return roomSubstitutionRepository.findAllByStatusEqualsIgnoreCase(RuleStatus.NEW.toString())
        .stream().map(roomSubstitutionRuleMapper::toDomainModel).toList();
  }

  @Override
  public List<RoomSubstitutionRule> findExpiredRecords() {
    log.debug("Find expired records for room substitution");
    return roomSubstitutionRepository.findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(
        RuleStatus.ACTIVE.toString(), LocalDateTime.now(
            ZoneOffset.UTC)).stream().map(roomSubstitutionRuleMapper::toDomainModel).toList();
  }

  @Override
  public List<RoomSubstitutionRule> findReadyRecords() {
    log.debug("Find ready records for room substitution");
    return roomSubstitutionRepository.findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(
            RuleStatus.FUTURE.toString(), LocalDateTime.now(ZoneOffset.UTC)).stream()
        .map(roomSubstitutionRuleMapper::toDomainModel).toList();
  }

  @Override
  @Transactional
  public void persistRecords(RuleProcessorComposite<RoomSubstitutionRule> ruleProcessorComposite) {
    log.debug("Save records for room substitution");
    roomSubstitutionRepository.save(
        roomSubstitutionRuleMapper.toEntityDto(ruleProcessorComposite.getRecordToUpdate()));
    var recordIdToDelete = ruleProcessorComposite.getRecordIdToDelete();
    if (recordIdToDelete != null) {
      roomSubstitutionRepository.deleteById(recordIdToDelete);
    }
  }
}
