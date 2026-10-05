package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.amend.in.AmendConfirmationPricesRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in.AmendConfirmationPricesRequestDto;

@Mapper(componentModel = "spring")
public interface AmendConfirmationPricesRequestMapper {

  AmendConfirmationPricesRequest toModel(AmendConfirmationPricesRequestDto amendConfirmationPricesRequestDto);
}
