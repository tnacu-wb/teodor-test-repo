package uk.co.whitbread.rules.manager.infrastructure.repository.model;

import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class RuleEntityListener {

  @PreUpdate
  public void updateLastModifiedAt(RuleEntity ruleEntity) {
    ruleEntity.setLastModifiedAt(LocalDateTime.now(ZoneOffset.UTC));
  }

}