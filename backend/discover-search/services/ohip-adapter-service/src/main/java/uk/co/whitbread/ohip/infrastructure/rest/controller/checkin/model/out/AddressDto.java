package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {

  private boolean isValidated;
  private List<String> addressLine;
  private String cityName;
  private String postalCode;
  private String state;
  private String language;
  private String type;
  private boolean primaryInd;


}