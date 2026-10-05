package uk.co.whitbread.payments.domain.logic.rule.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.logic.rule.AcceptedCardByHotel;
import uk.co.whitbread.payments.domain.logic.rule.ApplePaymentRule;
import uk.co.whitbread.payments.domain.logic.rule.CardExpiresBeforeDepartureDate;
import uk.co.whitbread.payments.domain.logic.rule.CardNotValidForRate;
import uk.co.whitbread.payments.domain.logic.rule.CnpOptionAllowed;
import uk.co.whitbread.payments.domain.logic.rule.ExpiredCard;
import uk.co.whitbread.payments.domain.logic.rule.GooglePaymentRule;
import uk.co.whitbread.payments.domain.logic.rule.PaymentMethodsForEmployeeRateRule;
import uk.co.whitbread.payments.domain.logic.rule.PaymentMethodsForHubHotelRule;
import uk.co.whitbread.payments.domain.logic.rule.PaypalPaymentMethodRule;
import uk.co.whitbread.payments.domain.logic.rule.PibaAllowedOnlyForPoa;
import uk.co.whitbread.payments.domain.logic.rule.PibaCardDisabilityRule;
import uk.co.whitbread.payments.domain.logic.rule.PibaCardNotAllowedInGermanHotel;
import uk.co.whitbread.payments.domain.logic.rule.PrepaymentAllowed;
import uk.co.whitbread.payments.domain.logic.rule.ReserveWithoutCardBookingAllowed;
import uk.co.whitbread.payments.domain.logic.rule.Rule;
import uk.co.whitbread.payments.domain.logic.rule.UpdateNewPibaCardRule;
import uk.co.whitbread.payments.domain.model.out.PaymentMethods;
import uk.co.whitbread.payments.domain.model.out.RuleData;

@Slf4j
public class LeisureSavedCardRuleExecutor implements RuleExecutor {
  private final List<Rule> rules;
  private final RuleData request;

  public LeisureSavedCardRuleExecutor(final RuleData request) {
    this.request = request;
    this.rules = new ArrayList<>();
    rules.add(new AcceptedCardByHotel());
    rules.add(new ExpiredCard());
    rules.add(new CardExpiresBeforeDepartureDate());
    rules.add(new PibaAllowedOnlyForPoa());
    if (request.isPibaEuroEnabled()) {
      rules.add(new UpdateNewPibaCardRule());
      rules.add(new PibaCardDisabilityRule());
    } else {
      rules.add(new PibaCardNotAllowedInGermanHotel());
    }
    rules.add(new CnpOptionAllowed());
    rules.add(new CardNotValidForRate());
    //GOSH
    //rules.add(new GoshRuleForUKHotels());
    rules.add(new PrepaymentAllowed());
    rules.add(new ReserveWithoutCardBookingAllowed());
    rules.add(new PaymentMethodsForHubHotelRule());
    rules.add(new PaymentMethodsForEmployeeRateRule());
    rules.add(new PaypalPaymentMethodRule());
    rules.add(new ApplePaymentRule());
    rules.add(new GooglePaymentRule());
  }

  @Override
  public Optional<PaymentMethods> execute() {
    log.debug("[Rules] : executing LeisureSavedCardRuleExecutor");
    RuleData data = null;
    for (Rule rule : rules) {
      data = rule.calculate(request);
    }
    return Optional.ofNullable(data)
        .map(RuleData::getPaymentMethods)
        .map(PaymentMethods::new);
  }
}
