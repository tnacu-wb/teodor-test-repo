package uk.co.whitbread.hotel.card.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InnBusinessCorrespondenceAddress {

  private String title;
  private String forename;
  private String surname;
  private String line1;
  private String line2;
  private String line3;
  private String line4;
  private String postCode;
  private String countryCodeISO;

  // When True, WorldLine will use this address as a default card delivery address when the cardholder registers their card.
  // When False, this is considered an ad-hoc address, and it will not be associated with the registered user.
  private boolean associateAddressWithFutureCardholder;

}
