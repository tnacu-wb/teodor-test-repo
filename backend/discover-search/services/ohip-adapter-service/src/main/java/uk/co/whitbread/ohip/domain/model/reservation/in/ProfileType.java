package uk.co.whitbread.ohip.domain.model.reservation.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileType {

  private CustomerType customer;
  private ProfileTypeEmails emails;
  private ProfileTypeTelephones telephones;
  private ProfileTypeAddresses addresses;

}
