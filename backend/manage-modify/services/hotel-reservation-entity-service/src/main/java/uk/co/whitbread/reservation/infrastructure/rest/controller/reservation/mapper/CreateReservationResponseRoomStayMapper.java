package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.out.RoomStay;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.MealsIncludedDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ReservationCreationRoomStayDto;

@Mapper(componentModel = "spring")
public interface CreateReservationResponseRoomStayMapper {

  @Mapping(source = "adultCount", target = "adultsNumber")
  @Mapping(source = "childCount", target = "childrenNumber")
  @Mapping(source = "roomType", target = "pmsRoomType")
  @Mapping(target = "mealsIncluded", expression = "java(toMealsIncludedDto())")
  ReservationCreationRoomStayDto toDto(RoomStay roomStay);

  default MealsIncludedDto toMealsIncludedDto() {

    //this will be removed once OB-714 it's clarified
    var mealsIncludedDto = new MealsIncludedDto();

    mealsIncludedDto.setBreakfast(Boolean.FALSE);
    mealsIncludedDto.setDinner(Boolean.FALSE);

    return mealsIncludedDto;
  }
}
