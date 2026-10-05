package uk.co.whitbread.cdh.domain.ports.primary;

import uk.co.whitbread.cdh.domain.model.account.in.CompanySearchCriteria;
import uk.co.whitbread.cdh.domain.model.account.out.Company;
import uk.co.whitbread.cdh.domain.model.account.out.CompanySearch;

public interface CompanyInPort {

  Company getCompany(String companyAccountId, String accessContext, String accessedBy);

  CompanySearch getCompanies(CompanySearchCriteria companySearchCriteria);
}
