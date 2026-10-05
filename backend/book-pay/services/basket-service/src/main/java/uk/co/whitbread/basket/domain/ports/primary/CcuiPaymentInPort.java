package uk.co.whitbread.basket.domain.ports.primary;

import uk.co.whitbread.basket.domain.model.ccuieckoh.in.CcuiPaymentRequest;
import uk.co.whitbread.basket.domain.model.ccuieckoh.out.PaymentCcuiResponse;
import uk.co.whitbread.basket.domain.model.payments.in.DiscountRequest;

public interface CcuiPaymentInPort {

  PaymentCcuiResponse initiateCcuiPaymentProcess(String basketReference,
      CcuiPaymentRequest ccuiPaymentRequest);

  void updateDiscount(DiscountRequest discountRequest);
}
