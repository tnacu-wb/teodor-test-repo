package uk.co.whitbread.rules.agent.domain.model.out;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class BusinessAllowanceRule extends Rule {

  String pms;
  String sourceId;
  String sourceType;
  String targetId;
  Boolean isTransactionCode;
  String aemId;
  Boolean isApplicableDaily;
  Boolean isNotesMandatory;

}
