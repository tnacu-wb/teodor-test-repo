package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;

@Value
@Builder
@EqualsAndHashCode(callSuper = false)
public class ChannelRuleResponse extends ModelValidator<ChannelRuleResponse> {

  @NotNull
  String sourceId;
  @NotEmpty
  List<String> ratePlanSets;
  @NotNull
  ChannelRuleRequestDetails requestDetails;
  @NotNull
  LocalDateTime generatedAt;

  public ChannelRuleResponse(String sourceId, List<String> ratePlanSets,
      ChannelRuleRequestDetails requestDetails, LocalDateTime generatedAt) {
    this.sourceId = sourceId;
    this.ratePlanSets = ratePlanSets;
    this.requestDetails = requestDetails;
    this.generatedAt = generatedAt;
    this.validateSelf();
  }
}
