package uk.co.whitbread.account.infrastructure.rest.controller.account.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountRegistrationResponseDto {
  private boolean success;
  private String sessionId;
  private String customerId;
  private boolean existingCompany;
  private boolean existingEmployee;
}
