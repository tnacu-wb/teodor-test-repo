package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomRateType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomRateReservation;

@Mapper(componentModel = "spring")
public abstract class ChangeReservationOhipMapper {
  
  public static final String RESERVATION_VALUE = "Reservation";
  
  @Mapping(expression = "java(injectReservationIds(reservationId))", target = "reservationIdList")
  @Mapping(expression = "java(injectRoomStay(roomRates, sourceCode))", target = "roomStay")
  abstract HotelReservationInstructionType fromDto(String reservationId,
                                                   RoomRateReservation roomRates, String sourceCode);
  
  @Named("injectReservationIds")
  protected List<UniqueIDType> injectReservationIds(
      String reservationId) {
    return Collections.singletonList(buildUniqueIdType(reservationId));
  }
  
  private UniqueIDType buildUniqueIdType(String reservationId) {
    var uniqueIdType = new UniqueIDType();
    uniqueIdType.setType(RESERVATION_VALUE);
    uniqueIdType.setId(reservationId);
    return uniqueIdType;
  }
  
  @Named("injectRoomStay")
  protected RoomStayType injectRoomStay(RoomRateReservation roomRates, String sourceCode) {
    var roomStayType = new RoomStayType();
    roomStayType.setRoomRates(buildRoomStay(roomRates, sourceCode));
    return roomStayType;
  }
  
  private List<RoomRateType> buildRoomStay(RoomRateReservation roomRates, String sourceCode) {
    return Collections.singletonList(buildRoomRate(roomRates, sourceCode));
  }
  
  private RoomRateType buildRoomRate(RoomRateReservation roomRates, String sourceCode) {
    var roomRateType = new RoomRateType();
    roomRateType.setRoomType(roomRates.getRoomType());
    roomRateType.setRatePlanCode(roomRates.getRatePlanCode());
    roomRateType.setSourceCode(sourceCode);
    roomRateType.setFixedRate(true);
    return roomRateType;
  }
}
