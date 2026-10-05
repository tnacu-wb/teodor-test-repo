package uk.co.whitbread.refund.processor.infrastructure.rest.client.threec.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.refund.processor.domain.model.out.PaymentResponse;
import uk.co.whitbread.refund.processor.generated.models.payments.PaymentResponseDto;

@Mapper(componentModel = "spring", uses = {PaymentResponseMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentResponseMapper {

  PaymentResponse toModel(PaymentResponseDto clientResponse);

}
