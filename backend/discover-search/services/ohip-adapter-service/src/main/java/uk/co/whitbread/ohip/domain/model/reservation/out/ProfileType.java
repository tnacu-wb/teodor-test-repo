package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileType {

  private ProfileIdResponse profileId;
  private NameTypeResponse name;
  private AddressTypeResponse address;
  private EmailTypeResponse email;
  private List<TelephoneTypeResponse> telephones;
}
