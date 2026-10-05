package uk.co.whitbread.rules.manager.infrastructure.repository;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.manager.domain.model.in.MaxArrivalDateRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.domain.ports.secondary.RuleEngineRepositoryOutPort;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.MaxArrivalDateMapper;

@Slf4j
@RequiredArgsConstructor
public class MaxArrivalDateRepositoryOutPortImpl
    implements RuleEngineRepositoryOutPort<MaxArrivalDateRule> {

  private static final String TABLE_NAME = "max_arrival_date_rule";
  private final MaxArrivalDateRepository maxArrivalDateRepository;
  private final MaxArrivalDateMapper maxArrivalDateMapper;

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  @Override
  public List<MaxArrivalDateRule> findNewRecords() {
    log.debug("Find new records for max arrival date");
    return maxArrivalDateRepository
        .findAllByStatusEqualsIgnoreCase(RuleStatus.NEW.toString())
        .stream()
        .map(maxArrivalDateMapper::toDomainModel)
        .toList();
  }

  @Override
  public List<MaxArrivalDateRule> findExpiredRecords() {
    log.debug("Find expired records for max arrival date");
    return maxArrivalDateRepository
        .findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(RuleStatus.ACTIVE.toString(),
            LocalDateTime.now(ZoneOffset.UTC))
        .stream()
        .map(maxArrivalDateMapper::toDomainModel)
        .toList();
  }

  @Override
  public List<MaxArrivalDateRule> findReadyRecords() {
    log.debug("Find ready records for max arrival date");
    return maxArrivalDateRepository
        .findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(RuleStatus.FUTURE.toString(),
            LocalDateTime.now(ZoneOffset.UTC))
        .stream()
        .map(maxArrivalDateMapper::toDomainModel)
        .toList();
  }

  @Override
  @Transactional
  public void persistRecords(RuleProcessorComposite<MaxArrivalDateRule> ruleProcessorComposite) {
    log.debug("Save records for max arrival date");
    maxArrivalDateRepository.save(
        maxArrivalDateMapper.toEntityDto(ruleProcessorComposite.getRecordToUpdate()));
    var recordIdToDelete = ruleProcessorComposite.getRecordIdToDelete();
    if (recordIdToDelete != null) {
      maxArrivalDateRepository.deleteById(recordIdToDelete);
    }
  }
}
