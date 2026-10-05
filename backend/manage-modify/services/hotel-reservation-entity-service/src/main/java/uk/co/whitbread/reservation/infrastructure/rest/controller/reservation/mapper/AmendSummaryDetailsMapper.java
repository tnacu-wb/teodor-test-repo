package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.AmendSummaryDetails;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.AmendSummaryDetailsDto;

@Mapper(componentModel = "spring")

public interface AmendSummaryDetailsMapper {
  AmendSummaryDetailsDto toDto(AmendSummaryDetails amendSummaryDetails);

}
