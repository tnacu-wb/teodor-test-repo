package uk.co.whitbread.payments.domain.logic.rule.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.logic.rule.AcceptedCardByHotel;
import uk.co.whitbread.payments.domain.logic.rule.ArrivalDateRule;
import uk.co.whitbread.payments.domain.logic.rule.CardNotValidForRate;
import uk.co.whitbread.payments.domain.logic.rule.ChangePaymentDisablePayNowRule;
import uk.co.whitbread.payments.domain.logic.rule.CnpOptionAllowed;
import uk.co.whitbread.payments.domain.logic.rule.EnablePibaAsPaymentOption;
import uk.co.whitbread.payments.domain.logic.rule.NonGuaranteedBookingAllowed;
import uk.co.whitbread.payments.domain.logic.rule.PaymentMethodsForHubHotelRule;
import uk.co.whitbread.payments.domain.logic.rule.PibaAllowedOnlyForPoa;
import uk.co.whitbread.payments.domain.logic.rule.PibaCardDisabilityRule;
import uk.co.whitbread.payments.domain.logic.rule.PibaCardNotAllowedInGermanHotel;
import uk.co.whitbread.payments.domain.logic.rule.PibaEuDisabledForUKCountry;
import uk.co.whitbread.payments.domain.logic.rule.PrepaymentAllowed;
import uk.co.whitbread.payments.domain.logic.rule.Rule;
import uk.co.whitbread.payments.domain.model.out.PaymentMethods;
import uk.co.whitbread.payments.domain.model.out.RuleData;


@Slf4j
public class AgentNewCardRuleExecutor implements RuleExecutor {

  private final List<Rule> rules;
  private final RuleData request;

  public AgentNewCardRuleExecutor(final RuleData request) {
    this.request = request;
    this.rules = new ArrayList<>();
    rules.add(new AcceptedCardByHotel());
    rules.add(new PibaAllowedOnlyForPoa());
    if (request.isPibaEuroEnabled()) {
      rules.add(new PibaCardDisabilityRule());
    } else {
      rules.add(new PibaEuDisabledForUKCountry());
      rules.add(new PibaCardNotAllowedInGermanHotel());
    }
    rules.add(new CnpOptionAllowed());
    rules.add(new CardNotValidForRate());
    rules.add(new PrepaymentAllowed());
    rules.add(new ArrivalDateRule());
    rules.add(new NonGuaranteedBookingAllowed());
    rules.add(new EnablePibaAsPaymentOption());
    rules.add(new PaymentMethodsForHubHotelRule());
    rules.add(new ChangePaymentDisablePayNowRule());
  }

  @Override
  public Optional<PaymentMethods> execute() {
    log.debug("[Rules] : executing AgentNewCardRuleExecutor");
    RuleData data = null;
    for (Rule rule : rules) {
      data = rule.calculate(request);
    }
    return Optional.ofNullable(data)
        .map(RuleData::getPaymentMethods)
        .map(PaymentMethods::new);
  }
}
