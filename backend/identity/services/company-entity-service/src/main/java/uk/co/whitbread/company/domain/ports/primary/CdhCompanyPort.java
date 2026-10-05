package uk.co.whitbread.company.domain.ports.primary;

import uk.co.whitbread.company.domain.model.in.CompaniesSearchRequest;
import uk.co.whitbread.company.domain.model.out.CompaniesProfile;

public interface CdhCompanyPort {

  CompaniesProfile getCompaniesFromCdh(CompaniesSearchRequest companiesSearchRequest);

}
