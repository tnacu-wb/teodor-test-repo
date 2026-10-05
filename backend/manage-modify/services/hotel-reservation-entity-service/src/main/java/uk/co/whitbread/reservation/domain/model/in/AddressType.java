package uk.co.whitbread.reservation.domain.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressType {

  @JsonProperty("isValidated")
  private boolean validated;
  private List<String> addressLine;
  private String cityName;
  private String postalCode;
  private String state;
  private Country country;
  private String language;
  private String type;
  private boolean primaryInd;

}
