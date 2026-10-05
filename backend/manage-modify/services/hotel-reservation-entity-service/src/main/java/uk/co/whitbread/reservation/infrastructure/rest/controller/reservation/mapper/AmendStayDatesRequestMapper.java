package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.AmendStayDatesRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AmendStayDatesRequestDto;

@Mapper(componentModel = "spring")
public interface AmendStayDatesRequestMapper {

  AmendStayDatesRequest toModel(AmendStayDatesRequestDto amendStayDatesRequestDto);
}
