package uk.co.whitbread.availabilitycacheservice.infrastructure.mapper;

import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Room;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelEntity;

@Slf4j
@Component
public final class HotelMapper {

  private HotelMapper() {
    //adding a private constructor to hide the implicit public constructor
  }

  public static List<Hotel> mapHotelEntityToHotel(final List<HotelEntity> hotelEntities) {
    log.trace("Mapping hotelEntity to hotel");
    if (CollectionUtils.isEmpty(hotelEntities)) {
      return Collections.emptyList();
    }

    final List<Hotel> hotelsWithRates = new ArrayList<>();
    ofNullable(hotelEntities)
        .orElse(Collections.emptyList())
        .forEach(hotelEntity -> {
          Hotel hotel = buildHotelFromSearchCriteria(hotelEntity.getHotelCode());
          final List<Room> rooms = mapRoomEntityToRoom(hotelEntity);
          final List<RatePlan> ratePlans = mapRateEntityToRatePlan(hotelEntity, rooms);
          hotel.setRates(ratePlans);
          hotelsWithRates.add(hotel);
        });
    return hotelsWithRates;
  }

  private static List<RatePlan> mapRateEntityToRatePlan(final HotelEntity hotelEntity, final List<Room> rooms) {
    log.trace("Mapping rateEntity to ratePlan");
    final List<RatePlan> ratePlans = new ArrayList<>();
    hotelEntity.getRates().forEach(rate -> {
      final var ratePlan = new RatePlan();
      ratePlan.setClassification(rate.getRateClassification());
      ratePlan.setTotalPrice(new Price(rate.getAmount(), rate.getCurrency()));
      ratePlan.setRooms(rooms);
      ratePlans.add(ratePlan);
    });
    return ratePlans;
  }

  private static List<Room> mapRoomEntityToRoom(final HotelEntity hotelEntity) {
    log.trace("Mapping roomEntity to room");
    final List<Room> roomList = new ArrayList<>();
    hotelEntity.getRooms().forEach(r -> {
      final var room = new Room();
      room.setType(r.getRoomType());
      roomList.add(room);
    });
    return roomList;
  }

  private static Hotel buildHotelFromSearchCriteria(String hotelCode) {
    return Hotel.builder()
        .hotelCode(hotelCode)
        .hotelBrand("pi")
        .limitedAvailability(false)
        .available(false)
        .build();
  }

}


