package uk.co.whitbread.infrastructure.rest.client.accounts.model.out;

import lombok.Data;

@Data
public class CompanyDetails {

  private String companyName;
  private String alternateCompanyName;
  private int numberOfEmployees;
}
