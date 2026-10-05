package uk.co.whitbread.payments.domain.logic.rule.logic;


import java.util.Optional;
import uk.co.whitbread.payments.domain.model.out.PaymentMethods;

public interface RuleExecutor {

  Optional<PaymentMethods> execute();
}
