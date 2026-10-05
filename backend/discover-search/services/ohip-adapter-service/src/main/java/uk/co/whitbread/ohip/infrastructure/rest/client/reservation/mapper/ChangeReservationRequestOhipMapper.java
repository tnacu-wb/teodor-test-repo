package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomRateReservation;

@Mapper(componentModel = "spring")
public abstract class ChangeReservationRequestOhipMapper {
  
  private ChangeReservationOhipMapper changeReservationOhipMapper;
  
  @Autowired
  public void toChangeReservationOhipMapperForModel(final ChangeReservationOhipMapper changeReservationOhipMapper) {
    this.changeReservationOhipMapper = changeReservationOhipMapper;
  }
  
  @Mapping(expression = "java(injectHotelReservations(reservationId, roomRates, sourceCode))",
      target = "reservations")
  public abstract ChangeReservation toChangeReservationDto(String reservationId,
                                                           RoomRateReservation roomRates, String sourceCode);
  
  protected List<HotelReservationInstructionType> injectHotelReservations(String reservationId,
                                                                          RoomRateReservation roomRates,
                                                                          String sourceCode) {
    return Collections.singletonList(changeReservationOhipMapper.fromDto(reservationId, roomRates, sourceCode));
  }
}

