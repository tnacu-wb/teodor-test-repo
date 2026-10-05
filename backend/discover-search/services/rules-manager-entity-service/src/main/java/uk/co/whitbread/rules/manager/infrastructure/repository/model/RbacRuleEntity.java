package uk.co.whitbread.rules.manager.infrastructure.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "rbac_rule", schema = "rules_engine")
public class RbacRuleEntity extends RuleEntity {

  private String resourceId;
  private String roleId;
  private Boolean hasAccess;
}

