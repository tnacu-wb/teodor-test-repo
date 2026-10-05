package uk.co.whitbread.domain.ports.secondary;


import uk.co.whitbread.infrastructure.rest.client.accounts.model.out.CompanyDetailsResponse;

public interface AccountServiceOutPort {

  CompanyDetailsResponse getCompanyDetails(String authorization, String bartCompanyId);

}
