package uk.co.whitbread.payments.domain.model.out;

import lombok.Data;

@Data
public class CompanyDetails {
  private String companyName;
  private String alternateCompanyName;
  private int numberOfEmployees;
}
