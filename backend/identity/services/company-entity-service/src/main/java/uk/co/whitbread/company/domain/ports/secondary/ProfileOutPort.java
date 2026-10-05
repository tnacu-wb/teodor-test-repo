package uk.co.whitbread.company.domain.ports.secondary;

import uk.co.whitbread.company.domain.model.in.CompaniesProfileRequest;
import uk.co.whitbread.company.domain.model.out.CompaniesProfile;
import uk.co.whitbread.company.domain.model.out.CompanyProfile;

public interface ProfileOutPort {
  CompaniesProfile getCompaniesProfile(CompaniesProfileRequest companiesProfileRequest);

  CompanyProfile getCompanyProfileByCorporateId(String corporateId,
      boolean isNegotiatedRatesExcluded);

  CompanyProfile getCompanyProfileByCompanyId(String companyId,
      boolean isNegotiatedRatesExcluded);

}
