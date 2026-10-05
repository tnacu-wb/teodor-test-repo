package uk.co.whitbread.rules.manager.infrastructure.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "business_allowance_rule", schema = "rules_engine")
public class BusinessAllowanceRuleEntity extends RuleEntity {

  private String pms;
  private String sourceId;
  private String targetId;
  private String aemId;
  private Boolean isApplicableDaily;

}
