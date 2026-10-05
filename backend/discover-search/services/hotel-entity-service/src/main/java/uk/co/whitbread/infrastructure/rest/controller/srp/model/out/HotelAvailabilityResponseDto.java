package uk.co.whitbread.infrastructure.rest.controller.srp.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelAvailabilityResponseDto {

  private String hotelId;
  private String name;
  private String distance;
  private String unit;
  private Boolean available;
  private Boolean limitedAvailability;
  private CostDto lowestRoomRate;
  private String pmsSource;
  private String cellCode;
  private Boolean hasMlosRestriction;
  private Double score;
  private Integer numberOfRoomsAvailable;
}
