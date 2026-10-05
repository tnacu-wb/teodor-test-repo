package uk.co.whitbread.reservation.domain.model.index.header.data.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Config {

  private String locale;
  private String groupBookingUrl;
  private BusinessHours businessHours;
  private RoomCodes roomCodes;
  private HotelBrands hotelBrands;
  private Api api;
  private HotelDetails hotelDetails;
  private Authentication authentication;
  private Features features;
  private Search search;
  private BookingSearch bookingSearch;
  private Map map;
  private Reservations reservations;
}
