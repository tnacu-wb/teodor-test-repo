package uk.co.whitbread.refund.processor.infrastructure.rest.client.threec.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.refund.processor.domain.model.out.RefundResponse;
import uk.co.whitbread.refund.processor.generated.models.payments.PaymentResponseDto;
import uk.co.whitbread.refund.processor.generated.models.payments.RefundResponseDto;

@Mapper(componentModel = "spring", uses = {RefundResponseMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RefundResponseMapper {

  RefundResponse toModelFromPaymentResponse(PaymentResponseDto clientResponse);

  RefundResponse toModelFromRefundResponse(RefundResponseDto clientResponse);


}
