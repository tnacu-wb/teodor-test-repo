package uk.co.whitbread.content.infrastructure.rest.controller.hotel.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.hotel.out.TabItem;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.TabItemDto;

@Mapper(componentModel = "spring", uses = {
    HotelFacilityDtoMapper.class,
    HotelGalleryImageDtoMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RoomConfigurationItemDtoMapper {

  TabItemDto toDto(TabItem tabItem);
}
