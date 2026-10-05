package uk.co.whitbread.rules.manager.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.rules.manager.domain.model.in.Rule;
import uk.co.whitbread.rules.manager.domain.model.in.RuleProcessorComposite;

public interface RuleEngineRepositoryOutPort<T extends Rule> {

  String getTableName();

  List<T> findNewRecords();

  List<T> findExpiredRecords();

  List<T> findReadyRecords();

  void persistRecords(RuleProcessorComposite<T> ruleProcessorComposite);
}
