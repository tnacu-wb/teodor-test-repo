package uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data;

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
  private PromotionBanner promotionBanner;
  private HotelBrands hotelBrands;
  private Api api;
  private HotelDetails hotelDetails;
  private Authentication authentication;
  private Features features;
  private Search search;
  private BookingSearch bookingSearch;
  private Experiments experiments;
  private Map map;
  private Reservations reservations;
  private AmazonChat amazonChat;
}
