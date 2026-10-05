package uk.co.whitbread.payments.domain.logic.rule;

import static uk.co.whitbread.payments.domain.model.out.CardOption.NEW_PIBA;
import static uk.co.whitbread.payments.domain.model.out.PibaCard.resolveApplicablePibaCard;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payments.domain.model.out.RuleData;

@Slf4j
public class UpdateNewPibaCardRule implements Rule {
  @Override
  public RuleData calculate(RuleData request) {
    log.debug("[Rule] : executing rule UpdateNewPibaCardRule");
    request.getPaymentMethods()
            .stream()
            .filter(paymentMethod -> paymentMethod.getType().equals(NEW_PIBA.name()))
            .forEach(paymentMethod -> resolveApplicablePibaCard(request.getCountry()).ifPresent(card ->
              paymentMethod.setSubType(card.getSubType())
            ));
    return request;
  }
}