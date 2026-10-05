package uk.co.whitbread.ohip.domain.model.profile.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Telephone {

  private String phoneTechType;
  private String phoneUseType;
  private String phoneNumber;
  private String orderSequence;
  private boolean primaryInd;
}