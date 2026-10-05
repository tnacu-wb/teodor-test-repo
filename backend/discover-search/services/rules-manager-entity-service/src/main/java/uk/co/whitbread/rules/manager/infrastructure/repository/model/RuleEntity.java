package uk.co.whitbread.rules.manager.infrastructure.repository.model;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@MappedSuperclass
@EntityListeners(RuleEntityListener.class)
public class RuleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer ruleId;

  private Integer refRuleId;
  private String status;
  private LocalDateTime createdAt;
  private LocalDateTime lastModifiedAt;
  private LocalDateTime enableTimestamp;
  private LocalDateTime disableTimestamp;
}
