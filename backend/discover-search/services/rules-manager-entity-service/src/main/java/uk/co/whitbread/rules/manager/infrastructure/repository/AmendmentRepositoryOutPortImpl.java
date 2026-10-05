package uk.co.whitbread.rules.manager.infrastructure.repository;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.rules.manager.domain.model.in.AmendmentRule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;
import uk.co.whitbread.rules.manager.domain.model.in.RuleStatus;
import uk.co.whitbread.rules.manager.domain.ports.secondary.RuleEngineRepositoryOutPort;
import uk.co.whitbread.rules.manager.infrastructure.repository.mapper.AmendmentRuleMapper;
import uk.co.whitbread.rules.manager.infrastructure.repository.model.AmendmentRuleEntity;

@Slf4j
@RequiredArgsConstructor
public class AmendmentRepositoryOutPortImpl implements RuleEngineRepositoryOutPort<AmendmentRule> {

  private static final String TABLE_NAME = "amendment_rule";
  private final AmendmentRepository amendmentRepository;
  private final AmendmentRuleMapper amendmentMapper;

  @Override
  public String getTableName() {
    return TABLE_NAME;
  }

  @Override
  public List<AmendmentRule> findNewRecords() {
    log.debug("Find new records for amendment");
    return amendmentRepository
        .findAllByStatusEqualsIgnoreCase(RuleStatus.NEW.toString())
        .stream()
        .map(this::toDomainModel)
        .filter(Objects::nonNull)
        .toList();
  }

  @Override
  public List<AmendmentRule> findExpiredRecords() {
    log.debug("Find expired records for amendment");
    return amendmentRepository
        .findAllByStatusEqualsIgnoreCaseAndDisableTimestampIsBefore(RuleStatus.ACTIVE.toString(),
            LocalDateTime.now(ZoneOffset.UTC))
        .stream()
        .map(amendmentMapper::toDomainModel)
        .toList();
  }

  @Override
  public List<AmendmentRule> findReadyRecords() {
    log.debug("Find ready records for amendment");
    return amendmentRepository
        .findAllByStatusEqualsIgnoreCaseAndEnableTimestampIsBefore(RuleStatus.FUTURE.toString(),
            LocalDateTime.now(ZoneOffset.UTC))
        .stream()
        .map(amendmentMapper::toDomainModel)
        .toList();
  }

  @Override
  @Transactional
  public void persistRecords(RuleProcessorComposite<AmendmentRule> ruleProcessorComposite) {
    log.debug("Save records for amendment");
    amendmentRepository.save(
        amendmentMapper.toEntityDto(ruleProcessorComposite.getRecordToUpdate()));
    var recordIdToDelete = ruleProcessorComposite.getRecordIdToDelete();
    if (recordIdToDelete != null) {
      amendmentRepository.deleteById(recordIdToDelete);
    }
  }

  private AmendmentRule toDomainModel(AmendmentRuleEntity amendmentRuleEntity) {
    try {
      return amendmentMapper.toDomainModel(amendmentRuleEntity);
    } catch (Exception ex) {
      log.error("Error while converting new amendment rule {}", amendmentRuleEntity, ex);
      amendmentRuleEntity.setStatus(RuleStatus.FAILED.toString());
      amendmentRepository.save(amendmentRuleEntity);
      return null;
    }
  }
}
