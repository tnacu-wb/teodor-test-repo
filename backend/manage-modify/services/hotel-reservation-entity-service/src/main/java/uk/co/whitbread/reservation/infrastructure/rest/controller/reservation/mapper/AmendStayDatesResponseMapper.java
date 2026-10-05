package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.AmendStayDatesResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.AmendStayDatesResponseDto;

@Mapper(componentModel = "spring")
public interface AmendStayDatesResponseMapper {

  AmendStayDatesResponseDto toDto(AmendStayDatesResponse amendStayDatesResponse);
}
