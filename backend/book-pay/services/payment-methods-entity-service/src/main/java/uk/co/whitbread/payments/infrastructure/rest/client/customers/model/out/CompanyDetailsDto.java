package uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out;

import lombok.Data;

@Data
public class CompanyDetailsDto {

  private String companyName;
  private String alternateCompanyName;
  private int numberOfEmployees;
}
