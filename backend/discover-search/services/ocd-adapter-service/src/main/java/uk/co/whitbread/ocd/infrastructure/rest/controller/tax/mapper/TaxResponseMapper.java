package uk.co.whitbread.ocd.infrastructure.rest.controller.tax.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ocd.domain.model.tax.out.TaxResponse;
import uk.co.whitbread.ocd.infrastructure.rest.controller.tax.model.out.TaxResponseDto;

@Mapper(componentModel = "spring")
public interface TaxResponseMapper {

  TaxResponseDto toDto(TaxResponse taxResponse);
}
