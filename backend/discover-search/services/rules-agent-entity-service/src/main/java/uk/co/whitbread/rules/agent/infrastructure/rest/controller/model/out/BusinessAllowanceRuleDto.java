package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BusinessAllowanceRuleDto {

  String pms;
  String sourceId;
  String sourceType;
  String targetId;
  Boolean isTransactionCode;
  String aemId;
  Boolean isApplicableDaily;
  Boolean isNotesMandatory;

}
