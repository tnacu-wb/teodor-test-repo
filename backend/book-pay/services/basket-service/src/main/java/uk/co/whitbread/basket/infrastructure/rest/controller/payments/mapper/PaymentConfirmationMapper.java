package uk.co.whitbread.basket.infrastructure.rest.controller.payments.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.out.PaymentConfirmationDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentConfirmationMapper {

  @Mapping(source = "booking.reference", target = "reference")
  PaymentConfirmationDto toDto(PaymentResponse clientResponse);

  PaymentConfirmationDto toDto(PaymentsConfirmation clientResponse);

}
