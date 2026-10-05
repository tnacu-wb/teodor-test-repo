package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.FOLIO_WINDOW_1;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.FOLIO_WINDOW_2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.CardTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.CreditCardInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.ResPaymentCardType;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PaymentUtils.class)
class PaymentUtilsTest {

  @Test
  void resolveDistrFolioWindow_ShouldReturnWindow1_WhenPibaCardPresentIsTrue() {
    assertEquals(FOLIO_WINDOW_1, PaymentUtils.resolveDistrFolioWindow(Boolean.TRUE));
  }

  @Test
  void resolveDistrFolioWindow_ShouldReturnWindow2_WhenPibaCardPresentIsFalse() {
    assertEquals(FOLIO_WINDOW_2, PaymentUtils.resolveDistrFolioWindow(Boolean.FALSE));
  }

  @Test
  void resolveDistrFolioWindow_ShouldReturnWindow2_WhenPibaCardPresentIsNull() {
    assertEquals(FOLIO_WINDOW_2, PaymentUtils.resolveDistrFolioWindow(null));
  }

  @Test
  void setUserDefinedCardType_OK() {
    ResPaymentCardType resPaymentCardType = new ResPaymentCardType();
    resPaymentCardType.userDefinedCardType("BU");
    resPaymentCardType.setCardType(CardTypeType.ZZ);

    CreditCardInfo creditCardInfo = new CreditCardInfo();
    creditCardInfo.setCreditCard(resPaymentCardType);

    var resultCard = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
    PaymentUtils.setCardType(creditCardInfo, resultCard);

    assertEquals("BU", resultCard.getUserDefinedCardType());
    assertNull(resultCard.getCardType());
  }

  @Test
  void setUserDefinedCardType_Empty() {
    ResPaymentCardType resPaymentCardType = new ResPaymentCardType();
    resPaymentCardType.userDefinedCardType("");
    resPaymentCardType.setCardType(CardTypeType.ZZ);

    CreditCardInfo creditCardInfo = new CreditCardInfo();
    creditCardInfo.setCreditCard(resPaymentCardType);

    var resultCard = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
    PaymentUtils.setCardType(creditCardInfo, resultCard);

    assertNull(resultCard.getUserDefinedCardType());
    assertEquals(uk.co.whitbread.hotel.ohip.adapter.generated.models.CardTypeType.ZZ,
        resultCard.getCardType());
  }

  @Test
  void setUserDefinedCardType_Null() {
    ResPaymentCardType resPaymentCardType = new ResPaymentCardType();
    resPaymentCardType.userDefinedCardType(null);
    resPaymentCardType.setCardType(CardTypeType.ZZ);

    CreditCardInfo creditCardInfo = new CreditCardInfo();
    creditCardInfo.setCreditCard(resPaymentCardType);

    var resultCard = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
    PaymentUtils.setCardType(creditCardInfo, resultCard);

    assertNull(resultCard.getUserDefinedCardType());
    assertEquals(uk.co.whitbread.hotel.ohip.adapter.generated.models.CardTypeType.ZZ,
        resultCard.getCardType());
  }
}
