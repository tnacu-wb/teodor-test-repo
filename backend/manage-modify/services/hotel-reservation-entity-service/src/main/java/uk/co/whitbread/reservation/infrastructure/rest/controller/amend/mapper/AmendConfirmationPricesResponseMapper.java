package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.amend.out.AmendConfirmationPricesResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.AmendConfirmationPricesResponseDto;

@Mapper(componentModel = "spring")
public interface AmendConfirmationPricesResponseMapper {

  AmendConfirmationPricesResponseDto toDto(AmendConfirmationPricesResponse amendConfirmationPricesResponse);
}
