package uk.co.whitbread.infrastructure.rest.client.accounts.model.out;

import java.util.List;
import lombok.Data;

@Data
public class CompanyDetailsResponse {

  private Company requestedCompany;
  private List<CellCode> companyCellCodes;
  private boolean success;

}
