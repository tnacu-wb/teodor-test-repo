package uk.co.whitbread.domain.model.hotel.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelPreferences {

  private String description;
  private String code;
  private String preferenceGroup;
  private boolean housekeeping;
  private Integer orderSequence;
  private String hotelId;
  private String label;
}
