package uk.co.whitbread.ohip.infrastructure.rest.controller.amend.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.amend.out.AmendSummaryResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.amend.model.out.AmendSummaryResponseDto;

@Mapper(componentModel = "spring")
public interface AmendSummaryResponseMapper {
  AmendSummaryResponseDto toDto(AmendSummaryResponse amendSummaryResponse);
}
