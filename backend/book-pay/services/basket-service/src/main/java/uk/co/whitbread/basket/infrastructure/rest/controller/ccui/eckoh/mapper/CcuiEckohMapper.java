package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohPaymentRequest;
import uk.co.whitbread.basket.domain.model.ccuieckoh.out.CcuiPaymentStatusResponse;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.in.EckohPaymentRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.out.EckohPaymentResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.out.PaymentStatusResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.mapper.AddressMapperRequest;

@Mapper(componentModel = "spring", uses = {
    AddressMapperRequest.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CcuiEckohMapper {

  EckohPaymentResponseDto toEckohPaymentResponseDto(PaymentResponse paymentResponse);

  EckohPaymentRequest toEckohPaymentRequestDto(EckohPaymentRequestDto eckohPaymentRequestDto);

  PaymentStatusResponseDto toDto(CcuiPaymentStatusResponse paymentStatusResponse);
}
