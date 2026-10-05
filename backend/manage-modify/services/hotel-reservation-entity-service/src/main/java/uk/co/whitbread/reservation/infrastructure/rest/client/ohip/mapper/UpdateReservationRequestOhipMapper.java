package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationByBasketRefResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationByIdDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayByIdDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReservationsRequestDto;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationsRequest;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.RoomStayByIdResponse;


@Mapper(componentModel = "spring")
public interface UpdateReservationRequestOhipMapper {
  @Mapping(source = "updateReservationsRequest.reservations", target = "updateReservationsRequest")
  UpdateReservationsRequestDto toDto(UpdateReservationsRequest updateReservationsRequest);

  ReservationByBasketRefResponseDto toResByBasketRefResponseDto(
      ReservationByBasketRefResponse tempReservations);

  List<ReservationByIdDto> toResByIdModel(List<ReservationByIdResponse> reservationById);

  @Mapping(target = "adults", source = "adultsNumber")
  @Mapping(target = "children", source = "childrenNumber")
  RoomStayByIdDto toRoomStayDto(RoomStayByIdResponse roomStay);

}
