package uk.co.whitbread.cdh.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.cdh.domain.model.account.in.CompanySearchCriteria;
import uk.co.whitbread.cdh.domain.model.account.out.Company;
import uk.co.whitbread.cdh.domain.model.account.out.CompanySearch;
import uk.co.whitbread.cdh.domain.ports.primary.CompanyInPort;
import uk.co.whitbread.cdh.domain.ports.secondary.CompanyOutPort;

@Slf4j
@RequiredArgsConstructor
@Component
public class CompanyInPortImpl implements CompanyInPort {

  private final CompanyOutPort companyOutPort;

  @Override
  public Company getCompany(String companyAccountId, String accessContext, String accessedBy) {
    return this.companyOutPort.getCompany(companyAccountId, accessContext, accessedBy);
  }

  @Override
  public CompanySearch getCompanies(CompanySearchCriteria companySearchCriteria) {
    return this.companyOutPort.getCompanies(companySearchCriteria);
  }
}
