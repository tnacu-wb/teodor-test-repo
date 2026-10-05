package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AlertType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.reservation.in.Alert;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;

@Mapper(componentModel = "spring")
public interface ReservationAlertMapper {

  default ChangeReservation toChangeReservationAlertDto(String reservationId, String hotelId,
      List<Alert> alertRequest) {
    final var reservationUniqueIdType = new UniqueIDType();
    reservationUniqueIdType.setType(UniqueIdTypeEnumDto.RESERVATION_TYPE.value());
    reservationUniqueIdType.setId(reservationId);
    var alerts = toAlertTypesDto(alertRequest);
    var hotelReservation = new HotelReservationInstructionType();
    hotelReservation.setReservationIdList(List.of(reservationUniqueIdType));
    hotelReservation.setAlerts(alerts);
    hotelReservation.setHotelId(hotelId);

    ChangeReservation changeReservation = new ChangeReservation();
    changeReservation.setReservations(List.of(hotelReservation));

    return changeReservation;
  }

  List<AlertType> toAlertTypesDto(List<Alert> alerts);

  AlertType toAlertTypeDto(Alert alert);

}
