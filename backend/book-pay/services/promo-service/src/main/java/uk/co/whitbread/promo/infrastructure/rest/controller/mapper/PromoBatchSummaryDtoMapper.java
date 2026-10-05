package uk.co.whitbread.promo.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.promo.domain.model.promobatch.out.PromoBatchSummary;
import uk.co.whitbread.promo.infrastructure.rest.controller.model.promobatch.out.PromoBatchSummaryDto;

@Mapper(componentModel = "spring")
public interface PromoBatchSummaryDtoMapper {

  PromoBatchSummaryDto toDto(PromoBatchSummary promoBatchSummary);
}
