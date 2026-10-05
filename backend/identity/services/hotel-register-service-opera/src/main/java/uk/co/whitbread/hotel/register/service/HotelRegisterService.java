package uk.co.whitbread.hotel.register.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.register.mapper.AccountMapper;
import uk.co.whitbread.hotel.register.model.AppsCustomer;
import uk.co.whitbread.hotel.register.model.Customer;
import uk.co.whitbread.hotel.register.model.CustomerResponse;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepOneRequest;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepOneResponse;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepTwoRequest;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepTwoResponse;


@Slf4j
@Service
@RequiredArgsConstructor
public class HotelRegisterService {

  private final CdhPiRegisterService cdhPiRegisterService;
  private final CdhBbRegisterService cdhBbRegisterService;
  private final MarketingService marketingService;
  private final AccountMapper accountMapper;

  public CustomerResponse createCustomer(Customer newCustomer, String language, boolean business) {

    log.debug("Called HotelRegisterService.createCustomer");
    if (business) {
      return cdhBbRegisterService.bbRegisterInCdh(newCustomer, language);
    } else {
      return cdhPiRegisterService.piRegisterInCdh(newCustomer, language);
    }
  }

  public InnBRegistrationStepOneResponse registerInnBStepOne(
      InnBRegistrationStepOneRequest request) {
    log.debug("Called HotelRegisterService.registerInnBStepOne");
    var response = cdhBbRegisterService.registerInnBStepOneInCdh(request);

    if (!response.isExistingEmployee()) {
      marketingService.updateMarketingOptIn(request.getUpdatePreferencesRequest(),
          request.getEmail());
    }

    return response;
  }

  public InnBRegistrationStepTwoResponse registerInnBStepTwo(
      InnBRegistrationStepTwoRequest request) {
    log.debug("Called HotelRegisterService.registerInnBStepTwo");
    var response = cdhBbRegisterService.registerInnBStepTwoInCdh(request);
    marketingService.updateMarketingOptIn(request.getUpdatePreferencesRequest(),
        response.getEmail());

    return response;
  }

  public CustomerResponse registerAccount(AppsCustomer newCustomer, String language) {
    log.debug("Called HotelRegisterService.registerAccount");
    return createCustomer(accountMapper.toAccountRequest(newCustomer), language, false);
  }
}
