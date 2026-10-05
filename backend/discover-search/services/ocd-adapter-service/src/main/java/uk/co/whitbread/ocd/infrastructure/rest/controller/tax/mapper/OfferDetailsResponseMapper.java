package uk.co.whitbread.ocd.infrastructure.rest.controller.tax.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.Offer;
import uk.co.whitbread.ocd.domain.model.tax.out.TaxResponse;

@Mapper(componentModel = "spring")
public interface OfferDetailsResponseMapper {

  @Mapping(source = "total", target = "total")
  TaxResponse toModel(Offer offer);
}
