package uk.co.whitbread.account.infrastructure.rest.client.customers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.account.domain.model.in.AccountRegistrationRequest;
import uk.co.whitbread.account.domain.model.out.AccountRegistrationResponse;
import uk.co.whitbread.account.domain.ports.secondary.CustomerRegistrationPort;
import uk.co.whitbread.account.infrastructure.rest.client.customers.mapper.CustomerRegistrationMapper;
import uk.co.whitbread.account.infrastructure.rest.client.customers.service.CustomerRegistrationClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerRegistrationPortImpl implements CustomerRegistrationPort {

  private final CustomerRegistrationClient customerRegistrationClient;
  private final CustomerRegistrationMapper customerRegistrationMapper;

  @Override
  public AccountRegistrationResponse registerCustomer(
      AccountRegistrationRequest accountRegistrationRequest, String country, String language) {
    return customerRegistrationMapper.toModel(
        customerRegistrationClient.registerCustomer(customerRegistrationMapper.toRequestDto(accountRegistrationRequest),
          country, language));
  }
}
