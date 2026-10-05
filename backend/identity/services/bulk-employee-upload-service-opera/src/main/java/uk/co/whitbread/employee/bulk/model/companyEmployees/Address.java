package uk.co.whitbread.employee.bulk.model.companyEmployees;

import lombok.Data;

@Data
public class Address {
  
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String addressLine4;
  private String addressLine5;
  private String companyName;
  private String countryCode;
  private String postCode;
  private String type;
}
