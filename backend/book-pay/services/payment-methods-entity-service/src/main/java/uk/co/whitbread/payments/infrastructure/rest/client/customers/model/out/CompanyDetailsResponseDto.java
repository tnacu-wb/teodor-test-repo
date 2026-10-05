package uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out;

import lombok.Data;

@Data
public class CompanyDetailsResponseDto {

  private CompanyDto requestedCompany;
  private boolean allowCentralCreditCard;
  private boolean marketingAllowed;
  private boolean companyLockedForEditing;
  private boolean success;

}
