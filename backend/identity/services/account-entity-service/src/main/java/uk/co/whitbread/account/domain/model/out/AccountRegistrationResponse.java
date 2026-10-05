package uk.co.whitbread.account.domain.model.out;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class AccountRegistrationResponse {
  private boolean success;
  private String sessionId;
  private String customerId;
  private boolean existingCompany;
  private boolean existingEmployee;

  public AccountRegistrationResponse(boolean success, String sessionId, String customerId,
      boolean existingCompany, boolean existingEmployee) {
    this.success = success;
    this.sessionId = sessionId;
    this.customerId = customerId;
    this.existingCompany = existingCompany;
    this.existingEmployee = existingEmployee;
  }
}
