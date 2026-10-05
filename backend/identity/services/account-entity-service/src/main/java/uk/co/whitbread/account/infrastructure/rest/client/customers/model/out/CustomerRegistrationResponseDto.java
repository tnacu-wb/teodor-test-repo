package uk.co.whitbread.account.infrastructure.rest.client.customers.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CustomerRegistrationResponseDto {
  private boolean success;
  private String sessionId;
  private String customerId;
  private boolean existingCompany;
  private boolean existingEmployee;
}
