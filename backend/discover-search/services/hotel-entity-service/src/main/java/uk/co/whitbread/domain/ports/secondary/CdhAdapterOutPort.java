package uk.co.whitbread.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.domain.model.cdh.CdhSearchCompaniesRequest;

public interface CdhAdapterOutPort {

  List<String> getCompanySuppressRates(String companyId);

  String getCompanyAccountIdFromCdh(CdhSearchCompaniesRequest cdhSearchBookingsRequest);
}