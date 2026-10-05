package uk.co.whitbread.payments.infrastructure.rest.controller.payment.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.payments.domain.model.in.PaymentMethodsRequest;
import uk.co.whitbread.payments.domain.model.in.SelectedPaymentMethods;
import uk.co.whitbread.payments.domain.model.out.PaymentMethod;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.in.BookingChannel;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.in.PaymentMethodsCriteriaDto;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.in.SelectedPaymentMethodsDto;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out.PaymentMethodDto;

@Mapper(componentModel = "spring")
public interface PaymentMethodsMapper {

  List<PaymentMethodDto> toDto(List<PaymentMethod> paymentMethods);

  PaymentMethodsRequest toModel(PaymentMethodsCriteriaDto criteria, String authorization,
                                BookingChannel bookingChannel);

  SelectedPaymentMethods toModel(SelectedPaymentMethodsDto request);
}
