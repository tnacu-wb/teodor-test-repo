package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInTelephoneDto {

  private String phoneTechType;
  private String phoneUseType;
  private String phoneNumber;
  private String orderSequence;
  private boolean primaryInd;
}