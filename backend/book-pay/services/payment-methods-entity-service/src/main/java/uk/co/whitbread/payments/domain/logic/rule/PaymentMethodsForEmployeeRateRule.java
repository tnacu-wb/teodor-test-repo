package uk.co.whitbread.payments.domain.logic.rule;

import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_NOW;

import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.payments.domain.logic.common.PaymentMethodsConstant;
import uk.co.whitbread.payments.domain.model.in.HotelBrand;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.RuleData;

public class PaymentMethodsForEmployeeRateRule implements Rule {

  @Override
  public RuleData calculate(RuleData request) {
    var paymentMethods = request.getPaymentMethods();
    if (StringUtils.equalsIgnoreCase(HotelBrand.HUB.getValue(), request.getHotelBrand())
            && StringUtils.equalsIgnoreCase(PaymentMethodsConstant.EMPLOYEE_RATE_PLAN_CODE,
                    request.getReservation().getRatePlanCode())) {
      paymentMethods.stream()
              .filter(paymentMethod -> !StringUtils.equalsIgnoreCase(PaymentMethodsConstant.PIBA,
                      paymentMethod.getName()))
              .map(PaymentMethod::getPaymentOptions)
              .flatMap(Collection::stream)
              .filter(paymentOption -> paymentOption.getType().equals(PAY_NOW.name()))
              .forEach(paymentOption -> paymentOption.setEnabled(true));
    }
    return request;
  }
}
