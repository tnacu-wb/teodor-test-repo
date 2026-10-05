package uk.co.whitbread.basket.infrastructure.rest.client.ccuieckoh.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.generated.models.payments.PaymentResponseDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CcuiEckohPaymentResponseMapper {

  PaymentResponse toModel(PaymentResponseDto clientResponse);
}
