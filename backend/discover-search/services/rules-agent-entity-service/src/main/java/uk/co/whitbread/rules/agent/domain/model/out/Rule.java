package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.rules.agent.domain.model.validation.SelfValidation;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public abstract class Rule implements SelfValidation<Rule> {

  @NotNull
  private Integer ruleId;
  private Integer refRuleId;
  @NotNull
  private RuleStatus status;
  @NotNull
  private LocalDateTime createdAt;
  @NotNull
  private LocalDateTime lastModifiedAt;
  private LocalDateTime enableTimestamp;
  private LocalDateTime disableTimestamp;

}