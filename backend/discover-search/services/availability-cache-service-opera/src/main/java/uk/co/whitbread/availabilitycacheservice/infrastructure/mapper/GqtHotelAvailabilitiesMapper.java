package uk.co.whitbread.availabilitycacheservice.infrastructure.mapper;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Room;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;

@Slf4j
public final class GqtHotelAvailabilitiesMapper {

  private GqtHotelAvailabilitiesMapper() {
  }

  //ABC - A, C ,S, F - today - room Sb,DB, TWIN
  public static List<Hotel> mapHotelAvailabilitiesResultSetToHotel(
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSets) {

    final List<Hotel> hotelList = new ArrayList<>();

    for (HotelAvailabilitiesResultSet availabilitiesResultSet : hotelAvailabilitiesResultSets) {

      final Hotel hotel = buildHotel(availabilitiesResultSet);
      if (!hotelList.contains(hotel)) {
        hotelList.add(hotel);
      }
    }

    for (final Hotel hotel : hotelList) {
      final List<RatePlan> ratePlans =
          buildRatePlansFromAvailabilitiesResultSet(hotel, hotelAvailabilitiesResultSets);
      //add rooms to each ratePlan
      for (final RatePlan ratePlan : ratePlans) {
        if (ratePlan != null && ratePlan.getClassification() != null) {
          List<Room> rooms = getRoomsForRatePlan(hotel, ratePlan, hotelAvailabilitiesResultSets);
          ratePlan.setRooms(rooms);
        }
      }
      hotel.setRates(ratePlans);
    }

    return hotelList;
  }

  private static List<Room> getRoomsForRatePlan(
      final Hotel hotel,
      final RatePlan ratePlan,
      final List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSets) {

    final List<Room> rooms = new ArrayList<>();
    for (HotelAvailabilitiesResultSet resultSet : hotelAvailabilitiesResultSets) {

      if (resultSet.getHotelCode().equalsIgnoreCase(hotel.getHotelCode())
          && resultSet.getAvailableDate().toString().equals(hotel.getDate())
          && resultSet.getRateClassification().equalsIgnoreCase(ratePlan.getClassification())
      ) {
        Room room = Room.builder()
            .type(resultSet.getRoomType())
            .qtyRequested((long) resultSet.getQuantity())
            .build();
        rooms.add(room);
      }
    }
    return rooms;
  }

  private static List<RatePlan> buildRatePlansFromAvailabilitiesResultSet(
      final Hotel hotel,
      List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSets) {

    final List<RatePlan> ratePlans = new ArrayList<>();
    final List<String> processedClassifications = new ArrayList<>();

    for (final HotelAvailabilitiesResultSet resultSet : hotelAvailabilitiesResultSets) {

      if (resultSet.getHotelCode().equalsIgnoreCase(hotel.getHotelCode())
          && resultSet.getAvailableDate().toString().equals(hotel.getDate())
          && !processedClassifications.contains(resultSet.getRateClassification())) {

        final Price price = new Price(resultSet.getAmount(), resultSet.getCurrency());

        final RatePlan ratePlan = RatePlan.builder()
            .code(resultSet.getRateCode())
            .classification(resultSet.getRateClassification())
            .totalPrice(price)
            .build();
        if (resultSet.getMinNights() != null) {
          ratePlan.setMinNights(resultSet.getMinNights());
        }
        if (resultSet.getMaxNights() != null) {
          ratePlan.setMaxNights(resultSet.getMaxNights());
        }
        ratePlans.add(ratePlan);
        processedClassifications.add(resultSet.getRateClassification());
      }
    }
    return ratePlans;
  }

  private static Hotel buildHotel(final HotelAvailabilitiesResultSet availabilitiesResultSet) {

    return Hotel.builder()
        .hotelCode(availabilitiesResultSet.getHotelCode())
        .available(availabilitiesResultSet.isAvailability())
        .date(availabilitiesResultSet.getAvailableDate().toString())
        .build();
  }

}
