package uk.co.whitbread.rules.agent.infrastructure.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "business_allowance_rule", schema = "rules_engine")
public class BusinessAllowanceRuleEntity extends RuleEntity {

  private String pms;
  private String sourceId;
  private String sourceType;
  private String targetId;
  private Boolean isTransactionCode;
  private String aemId;
  private Boolean isApplicableDaily;
  private Boolean isNotesMandatory;
}
