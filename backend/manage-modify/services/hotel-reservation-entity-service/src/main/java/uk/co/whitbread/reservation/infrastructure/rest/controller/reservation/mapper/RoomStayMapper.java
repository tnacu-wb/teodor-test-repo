package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.out.RoomStay;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.RoomStayByIdDto;

@Mapper(componentModel = "spring")
public interface RoomStayMapper {

  @Mapping(source = "adultCount", target = "adultsNumber")
  @Mapping(source = "childCount", target = "childrenNumber")
  RoomStayByIdDto toDto(RoomStay roomStay);
}
