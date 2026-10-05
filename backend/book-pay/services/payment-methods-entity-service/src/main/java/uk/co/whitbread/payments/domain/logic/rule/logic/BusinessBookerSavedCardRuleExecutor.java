package uk.co.whitbread.payments.domain.logic.rule.logic;

import static uk.co.whitbread.payments.domain.model.out.StoredCardType.BUSINESS_CENTRALLY_STORED_CARD;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.logic.rule.AcceptedCardByHotel;
import uk.co.whitbread.payments.domain.logic.rule.ApplePaymentRule;
import uk.co.whitbread.payments.domain.logic.rule.BookingAllowancesForCentrallyStoredNonPibaCards;
import uk.co.whitbread.payments.domain.logic.rule.BookingAllowancesForPibaCards;
import uk.co.whitbread.payments.domain.logic.rule.BusinessCentralCardsOnlyForPoa;
import uk.co.whitbread.payments.domain.logic.rule.CardExpiresBeforeDepartureDate;
import uk.co.whitbread.payments.domain.logic.rule.CardNotValidForRate;
import uk.co.whitbread.payments.domain.logic.rule.CnpOptionAllowed;
import uk.co.whitbread.payments.domain.logic.rule.CompanyDisableNewCard;
import uk.co.whitbread.payments.domain.logic.rule.CompanyDisabledPersonalStoredCard;
import uk.co.whitbread.payments.domain.logic.rule.ExpiredCard;
import uk.co.whitbread.payments.domain.logic.rule.GooglePaymentRule;
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
public class BusinessBookerSavedCardRuleExecutor implements RuleExecutor {
  private final List<Rule> rules;
  private final RuleData request;

  public BusinessBookerSavedCardRuleExecutor(final RuleData request) {
    this.request = request;
    this.rules = new ArrayList<>();
    rules.add(new AcceptedCardByHotel());
    rules.add(new CardNotValidForRate());
    rules.add(new CardExpiresBeforeDepartureDate());
    rules.add(new ExpiredCard());
    rules.add(new PibaAllowedOnlyForPoa());
    if (request.isPibaEuroEnabled()) {
      rules.add(new UpdateNewPibaCardRule());
      rules.add(new PibaCardDisabilityRule());
    } else {
      rules.add(new PibaCardNotAllowedInGermanHotel());
    }
    rules.add(new CompanyDisableNewCard());
    rules.add(new CnpOptionAllowed());
    rules.add(new CompanyDisabledPersonalStoredCard());
    rules.add(new BusinessCentralCardsOnlyForPoa());
    rules.add(new PrepaymentAllowed());
    rules.add(new ReserveWithoutCardBookingAllowed());
    rules.add(new BookingAllowancesForCentrallyStoredNonPibaCards());
    rules.add(new BookingAllowancesForPibaCards());
    rules.add(new PaymentMethodsForHubHotelRule());
    rules.add(new ApplePaymentRule());
    rules.add(new GooglePaymentRule());
    rules.add(new PaypalPaymentMethodRule());
  }

  @Override
  public Optional<PaymentMethods> execute() {
    log.debug("[Rules]: executing BusinessBookerSavedCardRuleExecutor");
    RuleData data = null;
    for (Rule rule : rules) {
      data = rule.calculate(request);
    }
    return Optional.ofNullable(data)
        .map(RuleData::getPaymentMethods)
        .map(PaymentMethods::new)
        .map(this::sortedPaymentMethods);
  }

  /*
   * Sorts payment methods to ensure business centrally stored card is first in list
   *
   * @param paymentMethods the available payment methods
   * @return PaymentMethods with a sorted list of payment methods
   */
  private PaymentMethods sortedPaymentMethods(PaymentMethods paymentMethods) {
    paymentMethods.getPaymentMethods().sort((o1, o2) -> {
      if (Optional.ofNullable(o1.getCard()).isPresent()
          && BUSINESS_CENTRALLY_STORED_CARD.name().equals(o1.getCard().getCardType())) {
        return -1;
      }
      return 0;
    });
    return paymentMethods;
  }
}
