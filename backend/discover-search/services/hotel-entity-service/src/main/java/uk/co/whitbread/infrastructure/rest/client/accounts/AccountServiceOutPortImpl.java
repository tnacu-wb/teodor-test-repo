package uk.co.whitbread.infrastructure.rest.client.accounts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.domain.ports.secondary.AccountServiceOutPort;
import uk.co.whitbread.infrastructure.rest.client.accounts.model.out.CompanyDetailsResponse;
import uk.co.whitbread.infrastructure.rest.client.accounts.service.AccountServiceClient;

@Slf4j
@RequiredArgsConstructor
@Component
public class AccountServiceOutPortImpl implements AccountServiceOutPort {

  private final AccountServiceClient accountServiceClient;

  @Override
  public CompanyDetailsResponse getCompanyDetails(String authorization, String companyId) {
    return accountServiceClient.getCompanyDetails(authorization, companyId);
  }
}
