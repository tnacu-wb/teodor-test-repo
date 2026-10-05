package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationByBasketRefResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationByIdDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationEventPreferenceDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayByIdDto;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationPreferencesResponse;
import uk.co.whitbread.reservation.domain.model.out.RoomStayByIdResponse;


@Mapper(componentModel = "spring")
public interface ReservationByBasketRefOhipResponseMapper {

  ReservationByBasketRefResponse toModel(ReservationByBasketRefResponseDto responseDto);

  @Mapping(source = "guarantee.onHold", target = "onHold")
  @Mapping(source = "guarantee.guaranteeCode", target = "guaranteeCode")
  @Mapping(
      target = "deRegCardCompleted",
      expression = "java("
          + "uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service."
          + "OhipAlertUtils.isDeRegCardCompleted(reservationByIdDto.getAlerts())"
          + ")"
  )
  ReservationByIdResponse toModel(ReservationByIdDto reservationByIdDto);

  @Mapping(source = "adults", target = "adultsNumber")
  @Mapping(source = "children", target = "childrenNumber")
  RoomStayByIdResponse toModel(RoomStayByIdDto roomStayByIdDto);

  ReservationPreferencesResponse toModel(ReservationEventPreferenceDto reservationEventPreferenceDto);

}
