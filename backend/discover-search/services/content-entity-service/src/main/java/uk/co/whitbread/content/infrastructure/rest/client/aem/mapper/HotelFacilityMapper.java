package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.hotel.out.HotelFacility;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.Facility;

@Mapper(componentModel = "spring")
public interface HotelFacilityMapper {

  @Mapping(target = "name", source = "legend")
  @Mapping(target = "weight", source = "ranking")
  @Mapping(target = "icon", source = "iconSrc")
  @Mapping(target = "isVisible", source = "visibility")
  HotelFacility toDomainModel(Facility facility);
}
