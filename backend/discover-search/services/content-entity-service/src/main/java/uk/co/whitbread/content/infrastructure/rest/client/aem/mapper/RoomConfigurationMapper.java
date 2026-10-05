package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.hotel.out.RoomConfiguration;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.HotelRoomConfiguration;

@Mapper(componentModel = "spring", uses = {
    RoomConfigurationGroupMapper.class,
    RoomConfigurationItemMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RoomConfigurationMapper {

  RoomConfiguration toDomainModel(HotelRoomConfiguration hotelRoomConfiguration);
}
