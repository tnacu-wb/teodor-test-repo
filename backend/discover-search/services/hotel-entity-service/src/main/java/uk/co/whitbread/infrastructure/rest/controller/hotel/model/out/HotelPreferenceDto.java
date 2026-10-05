package uk.co.whitbread.infrastructure.rest.controller.hotel.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelPreferenceDto {

  private String description;
  private String code;
  private String preferenceGroup;
  private boolean housekeeping;
  private Integer orderSequence;
  private String hotelId;
  private String label;
}
