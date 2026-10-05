package uk.co.whitbread.hotel.register.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InnBRegistrationStepTwoResponse {

  private String email;
  private String companyId;

}