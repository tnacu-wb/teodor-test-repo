package uk.co.whitbread.payments.infrastructure.rest.client.customers.mapper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out.CardType;

@Component
public class AccountMapperTransformer {

  @Named("toExpiryMonth")
  String toExpiryMonth(String date) {
    return date.substring(0, Math.min(date.length(), 2));
  }

  @Named("toExpiryYear")
  String toExpiryYear(String date) {
    return date.substring(Math.max(date.length() - 2, 0));
  }

  @Named("toCardName")
  String toCardName(String cardType) {
    return CardType.getCardName(cardType);
  }

}
