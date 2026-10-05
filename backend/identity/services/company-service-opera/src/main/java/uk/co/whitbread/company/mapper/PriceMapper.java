package uk.co.whitbread.company.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.company.model.PriceCapLocations;
import uk.co.whitbread.shared.cdh.model.company.RateCaps;

@Mapper(componentModel = "spring")
public interface PriceMapper {

  @Mapping(target = "uKWide", source = "ukWide")
  PriceCapLocations rateCapsToPriceCapLocations(RateCaps rateCaps);

}
