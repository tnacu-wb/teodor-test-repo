package uk.co.whitbread.payments.domain.model.out;

public class PaymentOptionTest {

  private static final String ANY_TYPE = "ANY_TYPE";
  public static final String PAY_ON_ARRIVAL = "PAY_ON_ARRIVAL";

  public static PaymentOption createPaymentOption() {
    var paymentOption = new PaymentOption();
    paymentOption.setEnabled(Boolean.FALSE);
    paymentOption.setOrder(2);
    paymentOption.setType(ANY_TYPE);

    return paymentOption;
  }

  public static PaymentOption createPaymentOption(String type, int order, boolean enabled) {
    var paymentOption = new PaymentOption();
    paymentOption.setEnabled(enabled);
    paymentOption.setOrder(order);
    paymentOption.setType(type);

    return paymentOption;
  }

}
