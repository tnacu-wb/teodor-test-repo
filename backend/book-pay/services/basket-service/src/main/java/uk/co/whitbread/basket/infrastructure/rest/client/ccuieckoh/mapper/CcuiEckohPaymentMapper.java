package uk.co.whitbread.basket.infrastructure.rest.client.ccuieckoh.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohPaymentRequest;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.PaymentCcuiRequest;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.mapper.RoomTypeRequestMapper;

@Mapper(componentModel = "spring", uses = {RoomTypeRequestMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CcuiEckohPaymentMapper {

  PaymentRequest toPaymentRequestModel(PaymentResponse paymentResponse);

  PaymentRequest toPaymentRequestModel(EckohPaymentRequest eckohPaymentRequest);

  PaymentRequest toPaymentRequestModel(PaymentCcuiRequest paymentCcuiRequest);
}