package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.Arrays;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CreateReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationsType;
import uk.co.whitbread.ohip.domain.model.reservation.in.Reservation;

@Mapper(componentModel = "spring", uses = {
    ReservationOhipMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {Arrays.class})
public abstract class ReservationRequestOhipMapper {

  private ReservationOhipMapper reservationOhipMapper;

  @Autowired
  public void toReservationOhipMapperForModel(final ReservationOhipMapper reservationOhipMapper) {
    this.reservationOhipMapper = reservationOhipMapper;
  }

  @Mapping(expression = "java(mapReservationTypes(reservation))", target = "reservations")
  public abstract CreateReservation toCreateReservationModel(Reservation reservation);

  protected HotelReservationsType mapReservationTypes(Reservation reservation) {
    var reservationType = reservationOhipMapper.fromDto(reservation);
    var reservationsType = new HotelReservationsType();
    reservationsType.addReservationItem(reservationType);

    return reservationsType;
  }
}