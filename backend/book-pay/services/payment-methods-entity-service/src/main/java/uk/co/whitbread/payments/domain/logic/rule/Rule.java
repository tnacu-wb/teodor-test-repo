package uk.co.whitbread.payments.domain.logic.rule;


import uk.co.whitbread.payments.domain.model.out.RuleData;

public interface Rule {

  RuleData calculate(RuleData request);
}
