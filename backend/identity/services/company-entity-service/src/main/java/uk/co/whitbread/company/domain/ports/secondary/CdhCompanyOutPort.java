package uk.co.whitbread.company.domain.ports.secondary;

import uk.co.whitbread.company.domain.model.in.CompaniesSearchRequest;
import uk.co.whitbread.company.domain.model.out.CompaniesProfile;

public interface CdhCompanyOutPort {

  CompaniesProfile getCompaniesFromCdh(CompaniesSearchRequest companiesSearchRequest);

}
