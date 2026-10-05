package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.CcuiPaymentRequest;
import uk.co.whitbread.basket.domain.model.ccuieckoh.out.PaymentCcuiResponse;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.in.CcuiPaymentRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.out.PaymentCcuiResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.mapper.AddressMapperRequest;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.PaymentRequestDto;

@Mapper(componentModel = "spring", uses = {
    AddressMapperRequest.class, AddressCcuiMapper.class, CcuiExtraItemsMapper.class },
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CcuiPaymentMapper {
  PaymentRequest toModel(PaymentRequestDto paymentCcuiRequestDto);

  @Mapping(source = "ccuiExtraItems.sendMail", target = "sendMail")
  CcuiPaymentRequest toModel(CcuiPaymentRequestDto ccuiPaymentRequestDto);

  PaymentCcuiResponseDto toDto(PaymentCcuiResponse paymentCcuiResponse);

}
