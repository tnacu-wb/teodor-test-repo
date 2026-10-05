package uk.co.whitbread.availabilitycacheservice.infrastructure.util;

import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Room;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionHotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionRoom;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.OperaHotelsSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.OperaSearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums.SortType;

@Slf4j
public final class HotelAvailabilitiesUtil {

  private static final String LANGUAGE = "EN";
  private static final String COUNTRY = "GB";

  private HotelAvailabilitiesUtil() {

  }

  public static final Room createRoom(String roomType, int children, int adults) {
    return Room.builder()
        .type(roomType).children(children)
        .adults(adults).cotRequired(false).build();
  }

  public static final Hotel buildHotelFromSearchCriteria(final String hotelCode) {
    return Hotel.builder()
        .hotelCode(hotelCode)
        .hotelBrand("pi")
        .limitedAvailability(false)
        .available(true)
        .euroCurrencyHotel(false)
        .build();
  }

  public static final SearchCriteria buildSearchCriteriaFromOperaCriteria(
      final OperaHotelsSearchCriteria operaHotelsSearchCriteria,
      final String[] types) {
    return SearchCriteria.builder()
        .hotelCodes(Collections.emptyList())
        .adults(operaHotelsSearchCriteria.getAdults())
        .children(operaHotelsSearchCriteria.getChildren())
        .cot(false)
        .arrival(operaHotelsSearchCriteria.getArrival())
        .departure(operaHotelsSearchCriteria.getDeparture())
        .country(COUNTRY)
        .language(LANGUAGE)
        .sort(SortType.DISTANCE)
        .rooms(operaHotelsSearchCriteria.getRooms())
        .type(types)
        .flagMlos(operaHotelsSearchCriteria.isFlagMlos())
        .build();
  }

  public static final OperaHotelsSearchCriteria buildOperaHotelsSearchCriteria(
      OperaSearchCriteria operaSearchCriteria,
      String[][] roomTypes, Boolean flagMlos) {
    return OperaHotelsSearchCriteria.builder()
        .hotelCodes(operaSearchCriteria.getHotelCodes())
        .arrival(operaSearchCriteria.getArrival())
        .departure(operaSearchCriteria.getDeparture())
        .cot(operaSearchCriteria.getCot())
        .language(operaSearchCriteria.getLanguage())
        .country(operaSearchCriteria.getCountry())
        .adults(operaSearchCriteria.getAdults())
        .children(operaSearchCriteria.getChildren())
        .rooms(operaSearchCriteria.getRooms())
        .roomQty(operaSearchCriteria.getRoomQty())
        .roomTypes(roomTypes)
        .flagMlos(Boolean.TRUE.equals(flagMlos))
        .build();
  }

  public static final DistributionHotel buildDistributedHotelFromHotelCode(final String hotelCode) {
    return DistributionHotel.builder()
        .hotelCode(hotelCode)
        .hotelName(hotelCode)
        .hotelBrand("pi")
        .hotelName(hotelCode)
        .available(false)
        .pmsSource("OPERA")
        .build();
  }

  public static final DistributionRoom createDistributionRoom(final String roomType, final int roomQty,
      final boolean cotAvailable) {
    return DistributionRoom.builder()
        .roomType(roomType)
        .qtyRequested(roomQty)
        .cotRequired(cotAvailable)
        .build();
  }


}
