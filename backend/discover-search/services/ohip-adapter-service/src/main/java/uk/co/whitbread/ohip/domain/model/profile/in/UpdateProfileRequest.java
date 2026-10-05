package uk.co.whitbread.ohip.domain.model.profile.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {

  private List<GuestDetails> guestDetails;
  private boolean isThirdPartySourceCode;


}
