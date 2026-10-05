package uk.co.whitbread.wallet.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelInfo {
  private String hotelId;
  private String hotelName;
  private String brand;
  private Double latitude;
  private Double longitude;
  private String address;
  private String country;
  private String phone;
  private String parking;
  private String links;
}
