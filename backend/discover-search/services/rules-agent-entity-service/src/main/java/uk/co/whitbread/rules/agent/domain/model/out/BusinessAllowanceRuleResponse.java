package uk.co.whitbread.rules.agent.domain.model.out;


import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class BusinessAllowanceRuleResponse {

  List<BusinessAllowanceRule> businessAllowances;
}
