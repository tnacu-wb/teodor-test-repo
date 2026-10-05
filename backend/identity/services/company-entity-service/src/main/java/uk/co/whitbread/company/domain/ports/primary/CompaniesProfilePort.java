package uk.co.whitbread.company.domain.ports.primary;

import uk.co.whitbread.company.domain.model.in.CompaniesProfileRequest;
import uk.co.whitbread.company.domain.model.out.CompaniesProfile;
import uk.co.whitbread.company.domain.model.out.CompanyProfile;

public interface CompaniesProfilePort {

  CompaniesProfile getCompaniesProfile(CompaniesProfileRequest companiesProfileRequest);

  CompanyProfile getCompanyProfile(String corporateId, boolean isNegotiatedRatesExcluded);

  CompanyProfile getCompanyWithNegotiatedRatesById(String corpCompanyId);

  CompanyProfile getCompanyByOperaId(String operaId);

}
