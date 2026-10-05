package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.TabItem;

@Mapper(componentModel = "spring", uses = {
    HotelFacilityMapper.class,
    HotelGalleryImageMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RoomConfigurationItemMapper {

  @Mapping(source = "room", target = "roomType")
  @Mapping(source = "facilityList", target = "facilities")
  @Mapping(source = "roomTitle", target = "roomName")
  @Mapping(source = "roomDescriptionText", target = "roomDescription")
  uk.co.whitbread.content.domain.model.hotel.out.TabItem toDomainModel(TabItem roomItem);
}
