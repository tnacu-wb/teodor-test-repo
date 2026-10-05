package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils;

import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.FOLIO_WINDOW_1;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.FOLIO_WINDOW_2;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.CreditCardInfo;

@Slf4j
public class PaymentUtils {

  private PaymentUtils() {
  }

  public static int resolveDistrFolioWindow(Boolean pibaCardPresent) {
    log.debug("Resolving folio window for DISTR/BB booking with pibaCardPresent={}", pibaCardPresent);
    return Boolean.TRUE.equals(pibaCardPresent) ? FOLIO_WINDOW_1 : FOLIO_WINDOW_2;
  }

  public static void setCardType(CreditCardInfo creditCardInfo,
      uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType card) {

    var userDefinedCardType = creditCardInfo.getCreditCard().getUserDefinedCardType();
    if (Objects.nonNull(userDefinedCardType) && !userDefinedCardType.isEmpty()) {
      card.setUserDefinedCardType(userDefinedCardType);
    } else {
      card.setCardType(creditCardInfo.getCreditCard().getCardType() != null
          ? CardTypeType.valueOf(creditCardInfo.getCreditCard().getCardType().name())
          : null);
    }
  }
}
