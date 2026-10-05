package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CheckInProfileDetailsDto {

  private CheckInCustomerDto customer;
  private AddressesDto addresses;
  private CheckInTelephonesDto telephones;
  private EmailsDto emails;
  private String profileType;


}
