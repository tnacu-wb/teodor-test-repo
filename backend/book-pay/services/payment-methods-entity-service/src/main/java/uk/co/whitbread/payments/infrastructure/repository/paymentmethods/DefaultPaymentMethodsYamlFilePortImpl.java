package uk.co.whitbread.payments.infrastructure.repository.paymentmethods;

import java.util.List;
import lombok.RequiredArgsConstructor;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.PaymentOption;
import uk.co.whitbread.payments.domain.ports.secondary.DefaultPaymentMethodsPort;
import uk.co.whitbread.payments.infrastructure.repository.paymentmethods.mapper.DefaultPaymentMethodsMapper;
import uk.co.whitbread.payments.infrastructure.repository.paymentmethods.model.out.DefaultPaymentMethodsDto;

@RequiredArgsConstructor
public class DefaultPaymentMethodsYamlFilePortImpl implements DefaultPaymentMethodsPort {

  private final DefaultPaymentMethodsDto defaultPaymentMethods;
  private final DefaultPaymentMethodsMapper paymentMethodsMapper;

  @Override
  public List<PaymentMethod> getDefaultPaymentMethods() {
    var paymentMethods = defaultPaymentMethods.getDefaultPaymentMethods();
    return paymentMethodsMapper.toPaymentMethodsModel(paymentMethods);
  }

  @Override
  public List<PaymentMethod> getDefaultPaymentCcuiMethods() {
    var paymentMethods = defaultPaymentMethods.getDefaultPaymentMethods();
    return paymentMethodsMapper.toPaymentMethodsModel(paymentMethods);
  }

  @Override
  public List<PaymentOption> getSavedCardPaymentOptions() {
    var cardPaymentOptions = defaultPaymentMethods.getSavedCardPaymentOptions();
    return paymentMethodsMapper.toPaymentOptionsModel(cardPaymentOptions);
  }

  @Override
  public List<PaymentMethod> getFailSafePaymentMethods() {
    var paymentMethods = defaultPaymentMethods.getFailSafePaymentMethods();
    return paymentMethodsMapper.toPaymentMethodsModel(paymentMethods);
  }

  @Override
  public List<PaymentMethod> getFailSafePaymentCcuiMethods() {
    var paymentMethods = defaultPaymentMethods.getFailSafePaymentMethods();
    return paymentMethodsMapper.toPaymentMethodsModel(paymentMethods);
  }
}