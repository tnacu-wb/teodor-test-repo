package uk.co.whitbread.basket.infrastructure.rest.controller.payments.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.ProcessAmendRequestDto;

@Mapper(componentModel = "spring")
public interface AmendMapper {

  PaymentsConfirmation toModel(ProcessAmendRequestDto processAmendRequestDto);
}
