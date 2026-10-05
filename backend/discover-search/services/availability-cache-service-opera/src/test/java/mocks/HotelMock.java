package mocks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Room;

public class HotelMock {

  private static final String PMS_SOURCE_OPERA = "OPERA";

  public static List<Hotel> buildAllHotelsOfOpera() {
    List<Hotel> hotelListForOpera = buildAllHotels();
    Hotel hotelWithCodePlypti = hotelListForOpera.get(0);
    hotelWithCodePlypti.setPmsSource(PMS_SOURCE_OPERA);
    List<RatePlan> ratePlans = hotelWithCodePlypti.getRates();
    List<RatePlan> ratePlansWithtotalCostEmpty = ratePlans.stream().map(ratePlan -> {
      ratePlan.setTotalPrice(null);
      return ratePlan;
    }).collect(Collectors.toList());
    hotelWithCodePlypti.setRates(ratePlansWithtotalCostEmpty);
    hotelListForOpera.set(0, hotelWithCodePlypti);
    return hotelListForOpera;
  }

  public static List<Hotel> buildAllHotels() {
    List<Hotel> hotelList = new ArrayList<>();
    List<Room> roomList = new ArrayList<>();
    List<RatePlan> ratePlans = new ArrayList<>();
    List<RatePlan> emptyRatePlans = Collections.emptyList();

    roomList.add(mockRoom("DB", 2, 1, true, new Price(new BigDecimal(100.00), "GBP"), true));
    roomList.add(mockRoom("S", 1, 0, false, new Price(new BigDecimal(100.00), "GBP"), false));

    ratePlans.add(mockRatePlan("A", "A", roomList, new Price(new BigDecimal(100.00), "GBP")));
    ratePlans.add(mockRatePlan("F", "F", roomList, new Price(new BigDecimal(40.00), "GBP")));
    ratePlans.add(mockRatePlan("S", "S", roomList, new Price(new BigDecimal(78.00), "GBP")));
    ratePlans.add(mockRatePlan("A", "A", roomList, new Price(new BigDecimal(450.00), "GBP")));
    ratePlans.add(mockRatePlan("A", "A", roomList, new Price(new BigDecimal(80.00), "GBP")));

    //PLYPTI, PLYLOC, PLYMAR, LISBAR, PAIWHI mocked hotel Codes
    hotelList.add(mockHotel("PLYPTI", ratePlans));
    hotelList.add(mockHotel("PLYLOC", ratePlans));
    hotelList.add(mockHotel("PLYMAR", emptyRatePlans));
    hotelList.add(mockHotel("LISBAR", emptyRatePlans));
    hotelList.add(mockHotel("PAIWHI", emptyRatePlans));
    return hotelList;
  }

  public static List<Hotel> buildAllHotelsWithoutRates() {
    List<Hotel> hotelList = new ArrayList<>();
    //PLYPTI, PLYLOC, PLYMAR, LISBAR, PAIWHI mocked hotel Codes
    hotelList.add(mockHotel("PLYPTI", null));
    hotelList.add(mockHotel("PLYLOC", null));
    hotelList.add(mockHotel("PLYMAR", null));
    hotelList.add(mockHotel("LISBAR", null));
    hotelList.add(mockHotel("PAIWHI", null));
    return hotelList;
  }

  public static List<Hotel> buildHotels(final List<String> hotelCodes) {
    final List<Hotel> hotels = new ArrayList<Hotel>();
    for (String hotelCode : hotelCodes) {
      hotels.add(Hotel.builder()
          .hotelCode(hotelCode)
          .hotelBrand("pi")
          .limitedAvailability(false)
          .available(false)
          .euroCurrencyHotel(false)
          .build());
    }
    return hotels;
  }

  public static List<Hotel> buildHotelsWithRates(final List<String> hotelCodes) {
    final List<Hotel> hotels = buildHotels(hotelCodes);
    List<Room> roomList = new ArrayList<>();
    roomList.add(mockRoom("DB", 2, 1, true, new Price(new BigDecimal(100.00), "GBP"), true));
    roomList.add(mockRoom("S", 1, 0, false, new Price(new BigDecimal(100.00), "GBP"), false));

    hotels.forEach(hotel -> {
      hotel.setRates(Arrays.asList(mockRatePlan("R", "R", roomList, new Price(new BigDecimal(100.00), "GBP"))));
    });
    return hotels;
  }

  public static List<Hotel> buildHotelsWithMultiRates(final List<String> hotelCodes) {
    final List<Hotel> hotels = buildHotels(hotelCodes);

    List<Room> roomList = new ArrayList<>();
    roomList.add(mockRoom("DB", 2, 1, true, new Price(new BigDecimal(100.00), "GBP"), true));
    roomList.add(mockRoom("S", 1, 0, false, new Price(new BigDecimal(100.00), "GBP"), false));

    for (int i = 0; i < hotels.size(); i++) {
      hotels.get(i).setRates(Arrays.asList(mockRatePlan("R", "R", roomList, new Price(new BigDecimal(100.00 + i), "GBP"))));
    }
    return hotels;
  }


  public static Hotel mockHotel(String hotelCode, List<RatePlan> ratesPlans) {
    Hotel hotel = new Hotel();
    hotel.setHotelCode(hotelCode);
    hotel.setHotelBrand("pi");
    hotel.setLimitedAvailability(false);
    hotel.setAvailable(false);
    if (ratesPlans != null) {
      ratesPlans.forEach(ratePlan -> ratePlan.setCode(hotelCode));
      hotel.setRates(ratesPlans);
    } else {
      hotel.setRates(Collections.emptyList());
    }
    return hotel;
  }

  public static RatePlan mockRatePlan(String classification, String name, List<Room> rooms, Price price) {
    RatePlan ratePlan = new RatePlan();
    ratePlan.setClassification(classification);
    ratePlan.setName(name);
    ratePlan.setRooms(rooms);
    ratePlan.setTotalPrice(price);
    return ratePlan;
  }

  public static Room mockRoom(String type, int adults, int children, boolean cot, Price price,
      boolean limitedAvailability) {
    Room room = new Room();
    room.setType(type);
    room.setAdults(adults);
    room.setChildren(children);
    room.setCotRequired(cot);
    room.setTotalPrice(price);
    room.setLimitedAvailability(limitedAvailability);
    return room;
  }
}

