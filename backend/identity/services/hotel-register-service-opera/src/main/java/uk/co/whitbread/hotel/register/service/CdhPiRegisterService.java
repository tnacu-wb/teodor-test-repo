package uk.co.whitbread.hotel.register.service;

import static uk.co.whitbread.hotel.register.utils.register.Utils.sanitizeInputString;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.register.client.HotelReservationEntityClient;
import uk.co.whitbread.hotel.register.exceptions.CdhServiceException;
import uk.co.whitbread.hotel.register.mapper.CustomerMapper;
import uk.co.whitbread.hotel.register.model.Customer;
import uk.co.whitbread.hotel.register.model.CustomerResponse;
import uk.co.whitbread.hotel.register.model.ReservationRequest;
import uk.co.whitbread.hotel.register.properties.CdhProperties;
import uk.co.whitbread.hotel.register.service.auth0.Auth0Service;
import uk.co.whitbread.shared.auth.exception.Auth0ApiException;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.cdh.CustomerDataService;
import uk.co.whitbread.shared.cdh.exception.CDHException;
import uk.co.whitbread.shared.cdh.model.CustomerAccountRequest;
import uk.co.whitbread.shared.cdh.model.CustomerAccountResponse;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CdhPiRegisterService {

  private static final String GENERIC_ERROR_MESSAGE = "An error occurred when attempting to register a new customer.";

  private final CustomerDataService cdhCustomerService;
  private final Auth0Service auth0LeisureService;
  private final HotelReservationEntityClient hotelReservationEntityClient;
  private final EmailService emailService;
  private final CustomerMapper customerMapper;
  private final CdhProperties cdhProperties;

  @Async
  public void createPiCustomerInCdhAsync(Customer newCustomer, String guestHistoryNumber,
      LocalDate guestHistoryCreated) {

    log.info("Making CDH create attempt");
    CustomerAccountRequest cdhRequest = customerMapper.toCdhRequest(newCustomer,
        guestHistoryNumber, guestHistoryCreated);
    try {
      cdhCustomerService.createCustomerAccount(cdhRequest);
    } catch (Exception e) {
      log.error("Error while creating account in CDH", e);
    }
  }

  public CustomerResponse piRegisterInCdh(Customer newCustomer, String language) {
    final String email = newCustomer.getContactDetail().getEmail();
    try {
      auth0LeisureService.saveUserInAuth0(
          email,
          newCustomer.getPassword(),
          null);
      final CustomerAccountResponse customerAccount = createPICustomerInCdh(newCustomer);
      final String cdhCustomerAccountId = customerAccount.getCustomerAccountId();
      log.info("Account creation in CDH is successful. CustomerAccountId is {}",
          cdhCustomerAccountId);
      updateOperaReservation(customerAccount, newCustomer);
      final CustomerResponse cdhResponse = customerMapper.toCustomerResponse(
          newCustomer.getContactDetail().getEmail());
      emailService.sendAsyncPiRegisterEmail(newCustomer, language);
      return cdhResponse;
    } catch (Auth0ApiException e) {
      throw new AuthServiceException(
          String.format("Could not create MyPI user %s in Auth0.", email), e);
    }
  }

  private CustomerAccountResponse createPICustomerInCdh(Customer newCustomer) {
    String email = newCustomer.getContactDetail().getEmail();
    log.info("Creating account in CDH.");
    CustomerAccountRequest cdhRequest = customerMapper.toCdhRequest(newCustomer);
    cdhRequest.setBartGuestHistoryNumber(UUID.randomUUID().toString());
    try {
      return cdhCustomerService.createCustomerAccount(cdhRequest);
    } catch (CDHException e) {
      log.error("Error while creating account in CDH", e);
      throw new CdhServiceException(GENERIC_ERROR_MESSAGE);
    }
  }

  public void updateOperaReservation(CustomerAccountResponse customerAccount,
      Customer newCustomer) {
    if (customerAccount.getCustomerAccountId() != null
        && newCustomer.getBasketReference() != null) {
      var customerAccountId = customerAccount.getCustomerAccountId();
      var basketReference = newCustomer.getBasketReference();

      log.info("Received details for BasketReference {} and CustomerAccountId {}",
          sanitizeInputString(basketReference), sanitizeInputString(customerAccountId));

      ReservationRequest payloadToLinkLeisureCustomer = new ReservationRequest(
          basketReference,
          customerAccountId);

      try {
        log.info("Posting to `hotel-reservation-entity-service` with  the Payload {}",
            payloadToLinkLeisureCustomer);
        hotelReservationEntityClient.linkLeisureCustomer(payloadToLinkLeisureCustomer);
      } catch (CDHException _) {
        log.error(
            "Error while updating reservation for BasketReference {} and CustomerAccountId {} with Payload {}",
            sanitizeInputString(basketReference),
            sanitizeInputString(customerAccountId), payloadToLinkLeisureCustomer);
        throw new CdhServiceException(
            "Failed to link the leisure customer due to an unknown error.");
      }
    }
  }
}
