package uk.co.whitbread.rules.agent.infrastructure.repository.model;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@MappedSuperclass
public class RuleEntity {

  @Id
  private Integer ruleId;

  private Integer refRuleId;
  private String status;
  private LocalDateTime createdAt;
  private LocalDateTime lastModifiedAt;
  private LocalDateTime enableTimestamp;
  private LocalDateTime disableTimestamp;

}
