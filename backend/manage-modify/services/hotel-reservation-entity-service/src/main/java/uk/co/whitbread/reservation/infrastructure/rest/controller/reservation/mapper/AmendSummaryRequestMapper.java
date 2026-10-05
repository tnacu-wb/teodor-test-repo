package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.AmendSummaryRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AmendSummaryRequestDto;

@Mapper(componentModel = "spring")
public interface AmendSummaryRequestMapper {
  AmendSummaryRequest toModel(AmendSummaryRequestDto amendSummaryRequestDtoDto);
}
