package uk.co.whitbread.ohip.domain.model.profile.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProfileAddress {

  @JsonProperty("isValidated")
  private boolean isValidated;
  private List<String> addressLine;
  private String cityName;
  private String postalCode;
  private String state;
  private Country country;
  private String language;
  private String type;
  private boolean primaryInd;


}