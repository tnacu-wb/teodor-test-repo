package uk.co.whitbread.reservation.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileType {

  private CustomerType customer;

  private ProfileTypeEmails emails;

  private ProfileTypeAddresses addresses;
}
