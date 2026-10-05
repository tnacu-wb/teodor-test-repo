package uk.co.whitbread.avail.business.events.infrastructure.mapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.avail.business.events.infrastructure.entity.HotelEntity;
import uk.co.whitbread.avail.business.events.infrastructure.entity.RoomEntity;
import uk.co.whitbread.avail.business.events.infrastructure.model.RoomDataElement;
import uk.co.whitbread.avail.business.events.infrastructure.model.SummaryTotalEventValues;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.EventDetail;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.EventHeader;

@Slf4j
public final class SummaryTotalMapper {

  private static final String PMS_SOURCE = "OPERA";

  private SummaryTotalMapper() {
  }

  public static HotelEntity convertSummaryTotalEventToHotelEntity(
      final EventHeader eventHeader) {

    final String hotelCode = eventHeader.getHotelId();

    final SummaryTotalEventValues summaryTotalEventValues =
        getDataElementValues(eventHeader);
    log.debug("summaryTotalEventValues: {}", summaryTotalEventValues);

    final int quantity = summaryTotalEventValues.getQuantity();
    log.debug("calculated quantity: {}", quantity);
    final String roomType = summaryTotalEventValues.getRoomType();
    final String availabilityDate = summaryTotalEventValues.getDate();

    final String hotelId = hotelCode + "_OPERA_" + availabilityDate;

    final HotelEntity hotelEntity = buildHotelEntity(hotelCode, hotelId, availabilityDate);

    final List<RoomEntity> roomEntityList = new ArrayList<>();
    final String roomId = new StringBuilder(roomType)
        .append("_").append(hotelId).toString();

    final RoomEntity roomEntity = buildRoomEntity(roomId, quantity, roomType, hotelEntity);
    roomEntityList.add(roomEntity);
    hotelEntity.setRooms(roomEntityList);
    hotelEntity.setEventHeader(eventHeader);
    return hotelEntity;
  }

  private static RoomEntity buildRoomEntity(
      final String roomId,
      final int quantity,
      final String roomType,
      final HotelEntity hotelEntity) {

    return RoomEntity.builder()
        .id(roomId)
        .quantity(quantity)
        .roomType(roomType)
        .hotel(hotelEntity)
        .build();
  }

  private static SummaryTotalEventValues getDataElementValues(
      final EventHeader eventHeader) {

    int physicalRooms = 0;
    int soldRooms = 0;
    int outOfOrder = 0;
    int overBooking = 0;
    String roomType = "";
    String date = "";

    final Set<EventDetail> eventDetails =
            eventHeader.getDetail();

    for (EventDetail eventDetailElementName : eventDetails) {
      final String dateElement = eventDetailElementName.getElementName();
      switch (dateElement) {
        case RoomDataElement.PHYSICAL_ROOMS:
          physicalRooms = getNewValue(eventDetailElementName);
          log.debug("physicalRooms: {}", physicalRooms);
          break;
        case RoomDataElement.SOLD_ROOMS:
          soldRooms = getNewValue(eventDetailElementName);
          log.debug("soldRooms: {}", soldRooms);
          break;
        case RoomDataElement.OUT_OF_ORDER:
          outOfOrder = getNewValue(eventDetailElementName);
          log.debug("outOfOrder: {}", outOfOrder);
          break;
        case RoomDataElement.OVERBOOKING:
          overBooking = getNewValue(eventDetailElementName);
          log.debug("overBooking: {}", overBooking);
          break;
        case RoomDataElement.ROOM_TYPE:
          roomType = eventDetailElementName.getNewValue();
          log.debug("roomType: {}", roomType);
          break;
        case RoomDataElement.DATE:
          date = eventDetailElementName.getNewValue();
          log.debug("date: {}", date);
          break;
        default:
          //do nothing
      }
    }
    //quantity = PHYSYCAL ROOMS - SOLD ROOMS - OUT OF ORDER +OVERBOOKING
    final int quantity = ((physicalRooms - soldRooms) - (overBooking));
    return buildSummaryTotalEventValues(roomType, date, quantity);
  }

  private static SummaryTotalEventValues buildSummaryTotalEventValues(
      final String roomType,
      final String date,
      final int quantity) {

    return SummaryTotalEventValues.builder()
        .date(date)
        .roomType(roomType)
        .quantity(quantity)
        .build();
  }

  private static int getNewValue(final EventDetail businessEventDataElement) {
    return Integer.parseInt(businessEventDataElement.getNewValue());
  }

  private static HotelEntity buildHotelEntity(
      final String hotelCode,
      final String hotelId,
      final String date) {

    final LocalDate availDate = LocalDate.parse(date);
    return HotelEntity.builder()
        .id(hotelId)
        .hotelCode(hotelCode)
        .pmsSource(PMS_SOURCE)
        .date(availDate)
        .build();
  }

}
