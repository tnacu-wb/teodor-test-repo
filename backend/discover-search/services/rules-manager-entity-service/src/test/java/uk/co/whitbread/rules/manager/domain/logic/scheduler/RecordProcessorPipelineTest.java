package uk.co.whitbread.rules.manager.domain.logic.scheduler;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class RecordProcessorPipelineTest {

  private final RecordProcessorPipeline recordProcessorPipeline = new RecordProcessorPipeline();

  @Test
  void getSteps__shouldActivateRule() {
    //Arrange
    var expectedPipelineSphases = List.of(
        new NewRecordProcessor(),
        new ExpiredRecordProcessor(),
        new ActivationRecordProcessor());

    //Act
    var actualPipelinePhases = recordProcessorPipeline.getSteps();

    //Assert
    assertThat(actualPipelinePhases).usingRecursiveComparison()
        .isEqualTo(expectedPipelineSphases);
  }
}