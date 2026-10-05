package uk.co.whitbread.ohip.domain.model.checkin.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CheckInTelephone {

  private String phoneTechType;
  private String phoneUseType;
  private String phoneNumber;
  private String orderSequence;
  private boolean primaryInd;
}