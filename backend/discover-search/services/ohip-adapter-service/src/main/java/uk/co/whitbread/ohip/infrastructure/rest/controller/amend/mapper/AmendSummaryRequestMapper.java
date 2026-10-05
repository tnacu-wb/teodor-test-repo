package uk.co.whitbread.ohip.infrastructure.rest.controller.amend.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.amend.in.AmendSummaryRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.amend.model.in.AmendSummaryRequestDto;

@Mapper(componentModel = "spring")
public interface AmendSummaryRequestMapper {
  AmendSummaryRequest toModel(AmendSummaryRequestDto amendSummaryRequestDto);
}
