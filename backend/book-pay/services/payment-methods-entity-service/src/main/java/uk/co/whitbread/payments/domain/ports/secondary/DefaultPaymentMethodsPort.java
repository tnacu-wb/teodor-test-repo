package uk.co.whitbread.payments.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.PaymentOption;

public interface DefaultPaymentMethodsPort {

  List<PaymentMethod> getDefaultPaymentMethods();

  List<PaymentMethod> getDefaultPaymentCcuiMethods();

  List<PaymentOption> getSavedCardPaymentOptions();

  List<PaymentMethod> getFailSafePaymentMethods();

  List<PaymentMethod> getFailSafePaymentCcuiMethods();
}
