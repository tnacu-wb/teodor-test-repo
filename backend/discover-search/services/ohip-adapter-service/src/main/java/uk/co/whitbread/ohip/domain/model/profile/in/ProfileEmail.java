package uk.co.whitbread.ohip.domain.model.profile.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEmail {

  private String emailAddress;
  private String type;
  private String typeDescription;
  private String primaryInd;

}
