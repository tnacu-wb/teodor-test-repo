package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.amend.out.AmendPaymentPageResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.AmendPaymentPageResponseDto;

@Mapper(componentModel = "spring")
public interface AmendPaymentPageResponseMapper {

  AmendPaymentPageResponseDto toDto(AmendPaymentPageResponse amendPaymentPageResponse);
}
