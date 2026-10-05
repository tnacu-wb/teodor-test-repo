package uk.co.whitbread.ocd.infrastructure.rest.controller.tax.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ocd.domain.model.tax.in.TaxRequest;
import uk.co.whitbread.ocd.infrastructure.rest.controller.tax.model.in.TaxRequestDto;

@Mapper(componentModel = "spring")
public interface TaxRequestMapper {

  TaxRequest toModel(String hotelId, TaxRequestDto taxRequestDto);

}
