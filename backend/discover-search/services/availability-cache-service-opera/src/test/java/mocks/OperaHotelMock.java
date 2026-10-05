package mocks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Room;

public class OperaHotelMock {

  private static final String PMS_SOURCE_OPERA = "OPERA";

  public static List<Hotel> buildAllHotels() {
    List<Hotel> hotelList = new ArrayList<>();
    List<Room> roomList = new ArrayList<>();
    List<RatePlan> ratePlans = new ArrayList<>();
    List<RatePlan> emptyRatePlans = Collections.emptyList();

    roomList.add(mockRoom("DB", 2, 1, true, new Price(new BigDecimal(100.00), "GBP")));
    roomList.add(mockRoom("S", 1, 0, false, new Price(new BigDecimal(100.00), "GBP")));

    ratePlans.add(mockRatePlan("A", roomList, new Price(new BigDecimal(100.00), "GBP")));
    ratePlans.add(mockRatePlan("F", roomList, new Price(new BigDecimal(40.00), "GBP")));
    ratePlans.add(mockRatePlan("S", roomList, new Price(new BigDecimal(78.00), "GBP")));
    ratePlans.add(mockRatePlan("A", roomList, new Price(new BigDecimal(450.00), "GBP")));
    ratePlans.add(mockRatePlan("A", roomList, new Price(new BigDecimal(80.00), "GBP")));

    //OXFORD", LONSLA - for OPERA hotels
    hotelList.add(mockHotel("OXFORD", emptyRatePlans, PMS_SOURCE_OPERA));
    hotelList.add(mockHotel("LONSLA", emptyRatePlans, PMS_SOURCE_OPERA));
    return hotelList;
  }

  public static List<Hotel> buildOperaHotels() {
    List<Hotel> hotelList = new ArrayList<>();
    List<Room> roomList = new ArrayList<>();
    List<RatePlan> ratePlans = new ArrayList<>();
    List<RatePlan> emptyRatePlans = Collections.emptyList();

    roomList.add(mockRoom("DB", 2, 1, true, new Price(new BigDecimal(100.00), "GBP")));
    roomList.add(mockRoom("S", 1, 0, false, new Price(new BigDecimal(100.00), "GBP")));

    ratePlans.add(mockRatePlan("A", roomList, new Price(new BigDecimal(100.00), "GBP")));
    ratePlans.add(mockRatePlan("F", roomList, new Price(new BigDecimal(40.00), "GBP")));
    ratePlans.add(mockRatePlan("S", roomList, new Price(new BigDecimal(78.00), "GBP")));
    ratePlans.add(mockRatePlan("A", roomList, new Price(new BigDecimal(450.00), "GBP")));
    ratePlans.add(mockRatePlan("A", roomList, new Price(new BigDecimal(80.00), "GBP")));

    //OXFORD", LONSLA - for OPERA hotels
    hotelList.add(mockHotel("OXFORD", emptyRatePlans, PMS_SOURCE_OPERA));
    hotelList.add(mockHotel("LONSLA", emptyRatePlans, PMS_SOURCE_OPERA));
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
    roomList.add(mockRoom("DB", 2, 1, true, new Price(new BigDecimal(100.00), "GBP")));
    roomList.add(mockRoom("S", 1, 0, false, new Price(new BigDecimal(100.00), "GBP")));

    hotels.forEach(hotel -> {
      hotel.setRates(
          Arrays.asList(mockRatePlan("R", roomList, new Price(new BigDecimal(100.00), "GBP"))));
    });
    return hotels;
  }

  public static List<Hotel> buildHotelsWithMultiRates(final List<String> hotelCodes) {
    final List<Hotel> hotels = buildHotels(hotelCodes);

    List<Room> roomList = new ArrayList<>();
    roomList.add(mockRoom("DB", 2, 1, true, new Price(new BigDecimal(100.00), "GBP")));
    roomList.add(mockRoom("S", 1, 0, false, new Price(new BigDecimal(100.00), "GBP")));

    for (int i = 0; i < hotels.size(); i++) {
      hotels.get(i).setRates(
          Arrays.asList(mockRatePlan("R", roomList, new Price(new BigDecimal(100.00 + i), "GBP"))));
    }
    return hotels;
  }

  public static Hotel mockHotel(String hotelCode, List<RatePlan> ratesPlans, String pmsSource) {
    Hotel hotel = new Hotel();
    hotel.setHotelCode(hotelCode);
    hotel.setHotelBrand("pi");
    hotel.setLimitedAvailability(false);
    hotel.setAvailable(false);
    hotel.setPmsSource(pmsSource);
    if (ratesPlans != null) {
      ratesPlans.forEach(ratePlan -> ratePlan.setCode(hotelCode));
      hotel.setRates(ratesPlans);
    } else {
      hotel.setRates(Collections.emptyList());
    }
    return hotel;
  }

  public static RatePlan mockRatePlan(String classification, List<Room> rooms, Price price) {
    RatePlan ratePlan = new RatePlan();
    ratePlan.setClassification(classification);
    ratePlan.setRooms(rooms);
    ratePlan.setTotalPrice(price);
    return ratePlan;
  }

  public static Room mockRoom(String type, int adults, int children, boolean cot, Price price) {
    Room room = new Room();
    room.setType(type);
    room.setAdults(adults);
    room.setChildren(children);
    room.setCotRequired(cot);
    room.setTotalPrice(price);
    room.setQtyRequested(new Long(1));
    return room;
  }

}
