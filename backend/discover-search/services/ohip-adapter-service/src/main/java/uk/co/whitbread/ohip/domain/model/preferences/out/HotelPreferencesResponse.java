package uk.co.whitbread.ohip.domain.model.preferences.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelPreferencesResponse {

  private List<HotelPreferences> hotelPreferences;
}
