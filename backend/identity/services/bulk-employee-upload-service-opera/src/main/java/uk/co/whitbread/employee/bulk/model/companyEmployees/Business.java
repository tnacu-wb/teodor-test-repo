package uk.co.whitbread.employee.bulk.model.companyEmployees;

import lombok.Data;

@Data
public class Business {
  
  private Boolean dismissMPILink;
  private Boolean miSetupRequired;
  private String myPILink;
  private Boolean tethered;
}
