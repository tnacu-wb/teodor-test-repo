package uk.co.whitbread.payments.infrastructure.rest.controller.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.payments.domain.model.out.PaymentAction;
import uk.co.whitbread.payments.domain.model.out.PaymentActionsResponse;
import uk.co.whitbread.payments.domain.model.out.Price;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out.PaymentActionDto;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out.PaymentActionResponseDto;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out.PriceDto;

@Mapper(componentModel = "spring")
public interface PaymentActionsMapper {

  PaymentActionResponseDto toDto(PaymentActionsResponse response);

  @Mapping(target = "chargeType", expression = "java(paymentAction.getChargeType().name())")
  PaymentActionDto toPaymentActionDto(PaymentAction paymentAction);

  PriceDto toPriceDto(Price price);

}
