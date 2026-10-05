package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.amend.in.AmendPaymentPageRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in.AmendPaymentPageRequestDto;

@Mapper(componentModel = "spring")
public interface AmendPaymentPageRequestMapper {

  AmendPaymentPageRequest toModel(AmendPaymentPageRequestDto amendPaymentPageRequestDto);
}
