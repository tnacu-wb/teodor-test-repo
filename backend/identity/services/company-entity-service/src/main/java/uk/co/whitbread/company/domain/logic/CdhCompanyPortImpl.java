package uk.co.whitbread.company.domain.logic;

import lombok.AllArgsConstructor;
import uk.co.whitbread.company.domain.model.in.CompaniesSearchRequest;
import uk.co.whitbread.company.domain.model.out.CompaniesProfile;
import uk.co.whitbread.company.domain.ports.primary.CdhCompanyPort;
import uk.co.whitbread.company.domain.ports.secondary.CdhCompanyOutPort;

@AllArgsConstructor
public class CdhCompanyPortImpl implements CdhCompanyPort {

  private static final int RESULTS_SEARCH_CAP = 20;
  private static final String ACCESSED_BY = "company-entity-service";
  private static final String ACCESS_CONTEXT = "OPERA";
  private final CdhCompanyOutPort cdhCompanyOutPort;

  @Override
  public CompaniesProfile getCompaniesFromCdh(CompaniesSearchRequest companiesSearchRequest) {
    var extendedProfileRequest = companiesSearchRequest.toBuilder()
        //business decision was to cap this at max 20 records
        .pageSize(Math.min(companiesSearchRequest.getPageSize(), RESULTS_SEARCH_CAP))
        .accessedBy(ACCESSED_BY)
        .accessContext(ACCESS_CONTEXT)
        .build();
    return cdhCompanyOutPort.getCompaniesFromCdh(extendedProfileRequest);
  }
}

