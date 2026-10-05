package uk.co.whitbread.hotel.register.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.register.exceptions.CdhServiceException;
import uk.co.whitbread.hotel.register.mapper.CustomerMapper;
import uk.co.whitbread.hotel.register.model.RegisterAccountRequest;
import uk.co.whitbread.hotel.register.model.RegisterAccountResponse;
import uk.co.whitbread.shared.cdh.CustomerDataService;
import uk.co.whitbread.shared.cdh.exception.CDHException;
import uk.co.whitbread.shared.cdh.model.CustomerAccountRequest;
import uk.co.whitbread.shared.cdh.model.CustomerAccountResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class PIUniversalLoginService {

  private static final String REGISTER_ERROR_MESSAGE = "An error occurred in CDH while attempting to register a new customer.";

  private final CustomerDataService customerDataService;
  private final CustomerMapper mapper;
  private final MarketingService marketingService;

  public RegisterAccountResponse registerAccount(RegisterAccountRequest registerAccountRequest) {
    CustomerAccountRequest customerAccountRequest = mapper.toCdhRequest(registerAccountRequest);

    log.info("Creating account in CDH.");
    try {
      CustomerAccountResponse customerAccount = customerDataService.createCustomerAccount(customerAccountRequest);
      RegisterAccountResponse response = mapper.toCustomerResponse(customerAccount);

      marketingService.updateMarketingOptIn(
          registerAccountRequest.getUpdatePreferencesRequest(),
          registerAccountRequest.getEmail());

      return response;
    } catch (CDHException e) {
      log.error("Error while creating account in CDH.", e);
      throw new CdhServiceException(REGISTER_ERROR_MESSAGE);
    }
  }
}
