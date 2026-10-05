package uk.co.whitbread.ohip.domain.model.checkin.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.profile.in.Country;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Address {

  private List<String> addressLine;
  private String cityName;
  private String postalCode;
  private Country country;
  private String language;
  private String type;
  private boolean primaryInd;


}