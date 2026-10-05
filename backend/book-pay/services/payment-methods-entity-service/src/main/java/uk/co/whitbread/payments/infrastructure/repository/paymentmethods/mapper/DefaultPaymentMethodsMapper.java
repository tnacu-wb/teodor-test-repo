package uk.co.whitbread.payments.infrastructure.repository.paymentmethods.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.domain.model.out.PaymentOption;
import uk.co.whitbread.payments.infrastructure.repository.paymentmethods.model.out.PaymentMethodDto;
import uk.co.whitbread.payments.infrastructure.repository.paymentmethods.model.out.PaymentOptionDto;

@Mapper(componentModel = "spring")
public interface DefaultPaymentMethodsMapper {
  List<PaymentMethod> toPaymentMethodsModel(List<PaymentMethodDto> defaultPaymentMethods);

  List<PaymentOption> toPaymentOptionsModel(List<PaymentOptionDto> paymentOptions);
}