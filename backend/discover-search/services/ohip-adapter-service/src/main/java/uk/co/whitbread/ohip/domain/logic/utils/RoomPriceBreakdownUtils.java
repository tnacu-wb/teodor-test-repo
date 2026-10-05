package uk.co.whitbread.ohip.domain.logic.utils;

import java.util.ArrayList;
import java.util.List;
import uk.co.whitbread.ohip.domain.model.availability.in.RoomPriceBreakdownWrapper;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityResult;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoom;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoomRate;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoomType;

public final class RoomPriceBreakdownUtils {

  private RoomPriceBreakdownUtils() {
  }

  public static List<RoomPriceBreakdownWrapper> wrapAllRoomPriceBreakdownRequests(String arrivalDate,
      String departureDate,
      AvailabilityResult hotelAvailabilityResult) {
    List<RoomPriceBreakdownWrapper> allWrappers = new ArrayList<>();
    for (AvailabilityRoomRate roomRate : hotelAvailabilityResult.getRoomRates()) {
      for (AvailabilityRoomType roomType : roomRate.getRoomTypes()) {
        allWrappers.addAll(wrapRoomPriceBreakdownForRoomType(
            arrivalDate, departureDate, hotelAvailabilityResult, roomRate, roomType));
      }
    }
    return allWrappers;
  }

  private static List<RoomPriceBreakdownWrapper> wrapRoomPriceBreakdownForRoomType(String arrivalDate,
      String departureDate,
      AvailabilityResult hotelAvailabilityResult, AvailabilityRoomRate roomRate,
      AvailabilityRoomType roomType) {
    List<RoomPriceBreakdownWrapper> wrappers = new ArrayList<>();
    for (AvailabilityRoom room : roomType.getRooms()) {
      wrappers.add(RoomPriceBreakdownWrapper.builder()
          .hotelId(hotelAvailabilityResult.getHotelId())
          .arrivalDate(arrivalDate)
          .departureDate(departureDate)
          .ratePlanCode(roomRate.getRatePlanCode())
          .adults(roomType.getAdults())
          .children(roomType.getChildren())
          .room(room)
          .build());
    }
    return wrappers;
  }
}
