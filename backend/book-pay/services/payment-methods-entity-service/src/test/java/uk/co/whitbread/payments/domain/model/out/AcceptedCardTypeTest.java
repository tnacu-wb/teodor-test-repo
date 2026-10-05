package uk.co.whitbread.payments.domain.model.out;

public class AcceptedCardTypeTest {

  public static AcceptedCardType createAcceptedCardType() {
    var acceptedCardType = new AcceptedCardType();
    acceptedCardType.setType("VI");
    acceptedCardType.setName("Visa Credit");
    acceptedCardType.setType("MC");
    acceptedCardType.setName("Mastercard Credit");
    acceptedCardType.setType("AX");
    acceptedCardType.setName("Mastercard Credit");
    acceptedCardType.setType("MD");
    acceptedCardType.setName("Mastercard Debit");
    acceptedCardType.setType("AM");
    acceptedCardType.setName("American Express");
    acceptedCardType.setType("DI");
    acceptedCardType.setName("Diners Club");
    acceptedCardType.setType("DN");
    acceptedCardType.setName("Diners Club");
    acceptedCardType.setType("VS");
    acceptedCardType.setName("Visa Debit");
    acceptedCardType.setType("DL");
    acceptedCardType.setName("Visa Debit");
    acceptedCardType.setType("MA");
    acceptedCardType.setName("Maestro");
    acceptedCardType.setType("EL");
    acceptedCardType.setName("Electron");

    return acceptedCardType;
  }

}
