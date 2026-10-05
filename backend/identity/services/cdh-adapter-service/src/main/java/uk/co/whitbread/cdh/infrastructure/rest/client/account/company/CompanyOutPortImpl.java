package uk.co.whitbread.cdh.infrastructure.rest.client.account.company;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.cdh.domain.model.account.in.CompanySearchCriteria;
import uk.co.whitbread.cdh.domain.model.account.out.Company;
import uk.co.whitbread.cdh.domain.model.account.out.CompanySearch;
import uk.co.whitbread.cdh.domain.ports.secondary.CompanyOutPort;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyOutPortImpl implements CompanyOutPort {

  private final CompanyClient companyClient;

  @Override
  public Company getCompany(String companyAccountId, String accessContext, String accessedBy) {
    return companyClient.getCompany(companyAccountId, accessContext, accessedBy);
  }

  @Override
  public CompanySearch getCompanies(CompanySearchCriteria companySearchCriteria) {
    return companyClient.getCompanies(companySearchCriteria);
  }
}
