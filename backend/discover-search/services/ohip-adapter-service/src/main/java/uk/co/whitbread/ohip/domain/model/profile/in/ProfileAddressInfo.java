package uk.co.whitbread.ohip.domain.model.profile.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProfileAddressInfo {

  private ProfileAddress address;

}
