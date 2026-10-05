package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayByIdDto;
import uk.co.whitbread.reservation.domain.model.out.RoomStay;

@Mapper(componentModel = "spring")
public interface RoomStayOhipMapper {


  @Mapping(source = "adults", target = "adultCount")
  @Mapping(source = "children", target = "childCount")
  RoomStay toModel(RoomStayByIdDto roomStay);
}
