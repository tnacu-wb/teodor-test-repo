package uk.co.whitbread.rules.manager.domain.logic.scheduler;

import java.util.List;

public class RecordProcessorPipeline {

  private static final List<RecordProcessor> PIPELINE = List.of(
      new NewRecordProcessor(),
      new ExpiredRecordProcessor(),
      new ActivationRecordProcessor());

  public List<RecordProcessor> getSteps() {
    return PIPELINE;
  }
}
