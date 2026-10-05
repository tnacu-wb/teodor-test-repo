package uk.co.whitbread.avail.business.events.infrastructure.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.avail.business.events.infrastructure.entity.HotelEntity;
import uk.co.whitbread.avail.business.events.infrastructure.entity.RoomEntity;
import uk.co.whitbread.avail.business.events.infrastructure.model.BusinessEventType;
import uk.co.whitbread.avail.business.events.infrastructure.model.RoomDataElement;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.*;

@RequiredArgsConstructor
public class SummaryTotalMapperTest {

  private static final String PMS_SOURCE = "OPERA";
  final int year = LocalDate.now().getYear() + 1;
  final String hotelCode = "LONSUT";

  final LocalDate localDate = LocalDate.of(year, 05, 18);
  final String availDate = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
  final String hotelId = hotelCode + "_OPERA_" + availDate;
  final String roomType = "DB";

  final int physicalRoom = 79;
  final int overBooking = 0;
  final int soldRooms = 1;

  EventHeader eventHeader;
  HotelEntity hotelEntityExpected;
  List<RoomEntity> rooms;

  @BeforeEach
  public void setup() {
    eventHeader = buildBusinessEventHeader(hotelCode);
    rooms = buildRoomEntities(hotelId, roomType);
    hotelEntityExpected = buildHotelEntity(hotelId, hotelCode, localDate, eventHeader);
  }

  @Test
  public void convertSummaryTotalEventToHotelEntityTest() {

    final int quantity = (physicalRoom - soldRooms - (overBooking));
    rooms.get(0).setQuantity(quantity);

    hotelEntityExpected.setRooms(rooms);

    final EventDetail roomTypeDe =
        buildBusinessEventDataElement(RoomDataElement.ROOM_TYPE, roomType);

    final EventDetail dateDe =
        buildBusinessEventDataElement(RoomDataElement.DATE, availDate);

    final EventDetail physicalRoomDe =
        buildBusinessEventDataElement(RoomDataElement.PHYSICAL_ROOMS, String.valueOf(physicalRoom));

    final EventDetail overBookingDe =
        buildBusinessEventDataElement(RoomDataElement.OVERBOOKING, String.valueOf(overBooking));

    final EventDetail soldRoomsDe =
        buildBusinessEventDataElement(RoomDataElement.SOLD_ROOMS, String.valueOf(soldRooms));

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(roomTypeDe);
    eventDetailSet.add(dateDe);
    eventDetailSet.add(physicalRoomDe);
    eventDetailSet.add(overBookingDe);
    eventDetailSet.add(soldRoomsDe);

    eventHeader.setDetail(eventDetailSet);

    final HotelEntity hotelEntityActual
        = SummaryTotalMapper.convertSummaryTotalEventToHotelEntity(eventHeader);

    assertEquals(hotelEntityExpected, hotelEntityActual);
  }

  @Test
  public void negativeQuantityConvertSummaryTotalEventToHotelEntityTest() {

    final int physicalRoom = 79;
    final int overBooking = 32;
    final int soldRooms = 50;
    //-3
    final int quantity = (physicalRoom - soldRooms - (overBooking));
    rooms.get(0).setQuantity(quantity);

    hotelEntityExpected.setRooms(rooms);

    final EventDetail roomTypeDe =
        buildBusinessEventDataElement(RoomDataElement.ROOM_TYPE, roomType);

    final EventDetail dateDe =
        buildBusinessEventDataElement(RoomDataElement.DATE, availDate);

    final EventDetail physicalRoomDe =
        buildBusinessEventDataElement(RoomDataElement.PHYSICAL_ROOMS, String.valueOf(physicalRoom));

    final EventDetail overBookingDe =
        buildBusinessEventDataElement(RoomDataElement.OVERBOOKING, String.valueOf(overBooking));

    final EventDetail soldRoomsDe =
        buildBusinessEventDataElement(RoomDataElement.SOLD_ROOMS, String.valueOf(soldRooms));

    final Set<EventDetail> eventDetailSet = new HashSet<>();
    eventDetailSet.add(roomTypeDe);
    eventDetailSet.add(dateDe);
    eventDetailSet.add(physicalRoomDe);
    eventDetailSet.add(overBookingDe);
    eventDetailSet.add(soldRoomsDe);

    eventHeader.setDetail(eventDetailSet);

    final HotelEntity hotelEntityActual
        = SummaryTotalMapper.convertSummaryTotalEventToHotelEntity(eventHeader);

    assertEquals(hotelEntityExpected, hotelEntityActual);
    assertEquals(-3, hotelEntityActual.getRooms().get(0).getQuantity());
  }

  private EventDetail buildBusinessEventDataElement(
      final String elementName,
      final String newValue
  ) {

    return EventDetail.builder()
        .elementName(elementName)
        .newValue(newValue)
        .build();
  }

  private BusinessEvent buildBusinessEvent(
      final EventHeader businessEventHeader,
      final BusinessEventDetails businessEventDetails) {

    return BusinessEvent.builder()
        .headerData(businessEventHeader)
        .details(businessEventDetails)
        .build();
  }

  private EventHeader buildBusinessEventHeader(final String hotelId) {
    return EventHeader.builder()
            .eventName(BusinessEventType.SUMMARY_TOTAL)
            .hotelId(hotelId)
            .build();
  }

  private List<RoomEntity> buildRoomEntities(
      final String hotelId,
      final String roomType) {

    final List<RoomEntity> roomEntityList = new ArrayList<>();
    final RoomEntity roomEntity1 = buildRoomEntity(hotelId, roomType);
    roomEntityList.add(roomEntity1);

    return roomEntityList;
  }

  private RoomEntity buildRoomEntity(
      final String hotelId,
      final String roomType
  ) {
    return RoomEntity.builder()
        .id(roomType + "_" + hotelId)
        .roomType(roomType)
        .build();
  }

  private HotelEntity buildHotelEntity(
      final String hotelId,
      final String hotelCode,
      final LocalDate date,
      final EventHeader eventHeader) {

    return HotelEntity.builder()
        .id(hotelId)
        .hotelCode(hotelCode)
        .date(date)
        .pmsSource(PMS_SOURCE)
        .rooms(rooms)
        .eventHeader(eventHeader)
        .build();

  }

}