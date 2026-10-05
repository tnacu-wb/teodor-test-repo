package uk.co.whitbread.infrastructure.rest.client.accounts.model.out;

import java.util.List;
import lombok.Data;

@Data
public class Company {

  private CompanyDetails companyDetails;
  private List<CellCode> companyCellCodes;

}
