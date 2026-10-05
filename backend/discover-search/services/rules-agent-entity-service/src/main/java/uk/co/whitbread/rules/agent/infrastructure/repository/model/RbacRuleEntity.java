package uk.co.whitbread.rules.agent.infrastructure.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "rbac_rule", schema = "rules_engine")
public class RbacRuleEntity extends RuleEntity {

  private String resourceId;
  private String roleId;
  private Boolean hasAccess;
}

