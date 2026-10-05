package uk.co.whitbread.reservation.domain.model.srp.out;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelAvailabilityResponse implements Serializable {

  private String hotelId;
  private String name;
  private Double distance;
  private String unit;
  private Boolean available;
  private Boolean limitedAvailability;
  private Cost lowestRoomRate;
  private String pmsSource;
  private Boolean hotelOpeningSoon;
  private String cellCode;
}
