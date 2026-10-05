package uk.co.whitbread.ohip.domain.model.checkin.out;

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
public class CheckInProfileDetails {

  private CheckInCustomer customer;
  private Addresses addresses;
  private CheckInTelephones telephones;
  private Emails emails;
  private String profileType;


}
