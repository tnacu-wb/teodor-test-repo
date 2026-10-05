package uk.co.whitbread.payments.domain.model.out;

import lombok.Data;

@Data
public class CompanyDetailsResponse {

  private Company requestedCompany;
  private boolean allowCentralCreditCard;
  private boolean marketingAllowed;
  private boolean companyLockedForEditing;
  private boolean success;
}
