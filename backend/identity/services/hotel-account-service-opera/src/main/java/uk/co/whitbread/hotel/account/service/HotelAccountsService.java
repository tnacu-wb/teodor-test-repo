package uk.co.whitbread.hotel.account.service;

import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.account.client.payment.Payment3CP;
import uk.co.whitbread.hotel.account.client.payment.model.CardHolder;
import uk.co.whitbread.hotel.account.client.payment.model.CreateTokenRequest;
import uk.co.whitbread.hotel.account.client.payment.model.CreateTokenResponse;
import uk.co.whitbread.hotel.account.client.pibaAccount.PibaAccountClient;
import uk.co.whitbread.hotel.account.client.pibaAccount.model.PibaAccount;
import uk.co.whitbread.hotel.account.exceptions.AccountNotFoundException;
import uk.co.whitbread.hotel.account.exceptions.InvalidTokenException;
import uk.co.whitbread.hotel.account.mapper.CustomerMapper;
import uk.co.whitbread.hotel.account.mapper.EmployeeMapper;
import uk.co.whitbread.hotel.account.model.Address;
import uk.co.whitbread.hotel.account.model.BillingAddress;
import uk.co.whitbread.hotel.account.model.Customer;
import uk.co.whitbread.hotel.account.model.CustomerRequest;
import uk.co.whitbread.hotel.account.model.CustomerResponse;
import uk.co.whitbread.hotel.account.model.HotelCustomerRequest;
import uk.co.whitbread.hotel.account.model.PaymentCard;
import uk.co.whitbread.hotel.account.model.PaymentPreference;
import uk.co.whitbread.hotel.account.model.SearchCustomerRequest;
import uk.co.whitbread.hotel.account.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.account.model.feature.UnleashWrapper;
import uk.co.whitbread.hotel.account.service.auth0.Auth0BusinessService;
import uk.co.whitbread.hotel.account.service.auth0.Auth0LeisureService;
import uk.co.whitbread.hotel.account.service.cdh.CdhService;
import uk.co.whitbread.hotel.account.service.worldline.WorldlineService;
import uk.co.whitbread.hotel.account.utils.account.CdhCustomerTransformer;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.exception.Auth0ApiException;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.azureemail.service.AzureEmailService;
import uk.co.whitbread.shared.cdh.CustomerDataService;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.exception.CDHException;
import uk.co.whitbread.shared.cdh.model.CustomerAccountRequest;
import uk.co.whitbread.shared.cdh.model.CustomerAccountResponse;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

@Slf4j
@RequiredArgsConstructor
@Service
public class HotelAccountsService {

    private final AzureEmailService emailService;
    private final Payment3CP payment3CP;
    private final TokenService authTokenService;
    private final Auth0LeisureService auth0LeisureService;
    private final Auth0BusinessService auth0BusinessService;
    private final CdhService cdhService;
    private final CustomerDataService customerDataService;
    private final EmployeeDataService employeeDataService;
    private final CdhCustomerTransformer cdhCustomerTransformer;
    private final CustomerMapper customerMapper;
    private final EmployeeMapper employeeMapper;
    private final CountriesService countriesService;
    private final PibaAccountClient pibaAccountClient;
    private final WorldlineService worldlineService;
    private final UnleashWrapper<FeatureFlag> unleashWrapper;

    public CustomerResponse updatePiCdhCustomer(String customerAccountId,
        CustomerRequest partialUpdateRequest) {
        log.debug("Called HotelAccountsService.updatePiCdhCustomer");

        String email = partialUpdateRequest.getContactDetail().getEmail();
        Optional<GetCustomerAccountResponse> optionalCdhAccount = cdhService.getCustomerAccount(
            customerAccountId, email);
        if (optionalCdhAccount.isEmpty()) {
            throw new AccountNotFoundException(
                String.format("Account with id %s was not found", customerAccountId));
        }
        Customer customer = customerMapper.toCustomer(optionalCdhAccount.get());
        auth0LeisureService.updateEmailOrPasswordInAuth0(customer, partialUpdateRequest, true);
        try {
            setCardToken(partialUpdateRequest, email);
            CustomerAccountRequest updatedAccount = cdhCustomerTransformer.transformToCustomerAccountRequest(
                partialUpdateRequest, customer);
            CustomerAccountResponse response = customerDataService.updateCustomerAccount(
                customerAccountId, updatedAccount, email);
            CompletableFuture.runAsync(() -> emailService.sendAccountUpdateEmail(
                customerMapper.toAccountUpdate(updatedAccount)));

            return customerMapper.toCustomerResponse(response);
        } catch (CDHException ex) {
            auth0LeisureService.rollbackUserUpdate(customer, partialUpdateRequest, true);
            throw ex;
        }
    }

    private void setCardToken(CustomerRequest partialUpdateRequest, String email) {
        if (partialUpdateRequest.getPaymentPreference() == null
            || partialUpdateRequest.getPaymentPreference().getPaymentCard() == null
            || partialUpdateRequest.getPaymentPreference().getPaymentCard().getCardNumber() == null
            || partialUpdateRequest.getPaymentPreference().getPaymentCard().getCardNumber().matches("^\\*+\\d{4}$")) {
            return;
        }
        CreateTokenRequest createTokenRequest = createCreateTokenRequest(partialUpdateRequest, email);
        CreateTokenResponse createTokenResponse = payment3CP.createToken(createTokenRequest);
        partialUpdateRequest.getPaymentPreference().getPaymentCard().setCardToken(createTokenResponse.getToken());
        partialUpdateRequest.getPaymentPreference().getPaymentCard().setCardType(createTokenResponse.getCardType());
    }

    private static CreateTokenRequest createCreateTokenRequest(CustomerRequest partialUpdateRequest, String email) {
        PaymentCard paymentCard = partialUpdateRequest.getPaymentPreference().getPaymentCard();
        String expiryDate = paymentCard.getExpiryDate();
        String[] parts = expiryDate.split("/");
        String expiryMonth = parts[0];
        String expiryYear = parts[1];
        BillingAddress address = partialUpdateRequest.getPaymentPreference().getPaymentCard().getBillingAddress();

        return CreateTokenRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .cardNumber(paymentCard.getCardNumber())
                .expiryMonth(expiryMonth)
                .expiryYear(expiryYear)
                .cardHolder(
                        CardHolder.builder()
                                .address(
                                        uk.co.whitbread.hotel.account.client.payment.model.Address.builder()
                                                .line1(address.getLine1())
                                                .countryCode(address.getCountryCode())
                                                .postalCode(address.getPostCode())
                                                .build()
                                )
                                .email(email)
                                .cardHolderName(paymentCard.getCardHolderName())
                                .build()
                ).build();
    }

    public CustomerResponse updateBbCdhEmployee(CdhEmployeeDetails employeeDetails,
        CustomerRequest partialUpdateRequest) {
        log.debug("Called HotelAccountsService.updateBbCdhCustomer");

        var companyAccountId = employeeDetails.getCompanyAccountId();
        var employeeAccountId = employeeDetails.getEmployeeAccountId();
        var email = employeeDetails.getUserEmail();
        var employeeOptional = unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
            ? employeeDataService.getEmployeeV2(companyAccountId, employeeAccountId,
            email)
            : employeeDataService.getEmployee(companyAccountId, employeeAccountId,
            email);
        var employeeResponse = employeeOptional.orElseThrow(() -> new AccountNotFoundException(
            String.format("Employee %s from company %s was not found", employeeAccountId,
                companyAccountId)));
        Customer customer = employeeMapper.toCustomer(employeeResponse);
        auth0BusinessService.updateEmailOrPasswordInAuth0(customer, partialUpdateRequest, true);
        try {
            setCardToken(partialUpdateRequest, email);
            var employeeRequest = employeeMapper.toEmployeeAccountRequest(employeeResponse);
            var updatedEmployee = employeeMapper.toEmployeeAccountRequest(partialUpdateRequest, employeeRequest);
            var response = employeeDataService.updateEmployeeAccount(companyAccountId, employeeAccountId, updatedEmployee, email);
            return employeeMapper.toCustomerResponse(response);
        } catch (CDHException ex) {
            auth0BusinessService.rollbackUserUpdate(customer, partialUpdateRequest, true);
            throw ex;
        }
    }

    public Customer getCustomer(HotelCustomerRequest request) {
        log.debug("Called HotelAccountsService.getCustomer");

        Customer customer;
        if (request.isBusiness()) {
            customer = getBbCustomer(request);
        } else {
            customer = getPiCustomer(request);
        }
        Address address = customer.getContactDetail().getAddress();
        if (Objects.nonNull(address)) {
            String countryCode = address.getCountryCode();
            if (StringUtils.isNotBlank(countryCode)) {
                address.setCountryCodeISO(getCountryCodeISO(countryCode));
            }
        }

        if (Objects.isNull(customer.getContactDetail().getTitle())) {
            customer.getContactDetail().setTitle(StringUtils.EMPTY);
        }
        // Session ID is deprecated, but the mobile app client still expects it to be present due to this issue:
        // https://whitbreadis.atlassian.net/browse/CTECH-5111
        customer.setSessionId("deprecated");
        return customer;
    }

    private String getCountryCodeISO(String countryCode) {
        String countryCodeISO = countriesService.getCountryCodeISO(countryCode);
        return StringUtils.isBlank(countryCodeISO) ? countryCode : countryCodeISO;
    }

    private Customer getBbCustomer(HotelCustomerRequest request) {
        log.debug("Called HotelAccountsService.getBbCustomer");

        String authorization = request.getAuthorization();
        CdhEmployeeDetails employeeDetails = getCdhEmployeeDetails(authorization);
        String employeeAccountId = employeeDetails.getEmployeeAccountId();
        String companyAccountId = employeeDetails.getCompanyAccountId();
        return buildCdhEmployeeResponse(request, authorization, employeeAccountId, companyAccountId);
    }

    private Customer getPiCustomer(HotelCustomerRequest request) {
        log.debug("Called HotelAccountsService.getPiCustomer");

        String authorization = request.getAuthorization();
        if (StringUtils.isBlank(authorization)) {
            throw new InvalidTokenException("No authorization provided for customer request");
        }
        String cdhCustomerAccountId = getCdhCustomerAccountId(authorization);
        if (StringUtils.isBlank(cdhCustomerAccountId)) {
            throw new AccountNotFoundException("No CDH customer account found for provided authorization");
        }
        return getPiCustomerFromCdh(cdhCustomerAccountId, request.getCustomerId());
    }

    private String retrieveTetherGuid(String authorization)
            throws InvalidTokenException {

        var pibaAccounts = pibaAccountClient.getPibaAccounts(authorization, true).getAccounts();
        for (PibaAccount pibaAccount : pibaAccounts) {
            var tetheredGuid = pibaAccount.getTetheredGuid();
            if (StringUtils.isNotBlank(tetheredGuid)) {
              return tetheredGuid;
            }
        }
        return null;
    }

    public void deleteCdhPICustomer(String customerAccountId, String email) {
        customerDataService.deleteCustomerAccount(customerAccountId, email);
        try {
            auth0LeisureService.deleteUser(email);
        } catch (Auth0ApiException e) {
            String connection = auth0LeisureService.getAuthManagementService()
                .getManagementProperties().getConnection();
            throw new AuthServiceException(
                String.format("Error while trying to delete user %s from %s", email,
                    connection), e);
        }
    }

    private Customer getPiCustomerFromCdh(String customerAccountId, String accessedBy) {
        final Optional<GetCustomerAccountResponse> customerAccountResponse =
            unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
                ? customerDataService.getCustomerAccountV3(customerAccountId, accessedBy)
                : customerDataService.getCustomerAccount(customerAccountId, accessedBy);
        if (customerAccountResponse.isPresent()) {
            return cdhCustomerTransformer.getCustomerAccountResponseToCustomer(
                customerAccountResponse.get());
        } else {
            throw new AccountNotFoundException(
                String.format("Account with customer account ID %s was not found", customerAccountId));
        }
    }

    protected Customer getBbCustomerFromCdh(String companyAccountId, String employeeAccountId,
        String accessedBy) {
        final Optional<GetEmployeeResponse> optionalEmployee =
            unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
                ? employeeDataService.getEmployeeV2(companyAccountId, employeeAccountId, accessedBy)
                : employeeDataService.getEmployee(companyAccountId, employeeAccountId, accessedBy);

        final GetEmployeeResponse employee = optionalEmployee.orElseThrow(() -> new AccountNotFoundException(
            String.format(
                "The employee with employeeAccountId = %s was not found in the company with companyAccountId = %s",
                employeeAccountId, companyAccountId)));
        Customer customer = employeeMapper.toCustomer(employee);
        return setPersonalCardIdOnCdhBbProfile(customer);
    }

    private Customer setPersonalCardIdOnCdhBbProfile(Customer customer) {
        PaymentPreference paymentPreference = customer.getPaymentPreference();
        if (paymentPreference != null) {
            PaymentCard paymentCard = paymentPreference.getPaymentCard();
            if (paymentCard != null && paymentCard.getCardNumber() != null) {
                paymentCard.setCardID("1");
            } else {
                paymentPreference.setPaymentCard(null);
            }
        }
        return customer;
    }

    private String getCdhCustomerAccountId(String authorization) {
        return authTokenService.retrieveCustomerAccountIdAndVerifyToken(authorization).orElse(null);
    }

    private CdhEmployeeDetails getCdhEmployeeDetails(String authorization) {
        return authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
    }

    private Customer buildCdhEmployeeResponse(HotelCustomerRequest request, String authorization,
                                              String employeeAccountId, String companyAccountId) {
        Customer customer;
        customer = getBbCustomerFromCdh(companyAccountId, employeeAccountId, request.getCustomerId());
        ofNullable(authorization).map(this::retrieveTetherGuid)
            .ifPresent(customer::setTetheredGuid);
        return customer;
    }

  public List<Customer> getCustomer(SearchCustomerRequest searchRequest, String email) {
    return cdhCustomerTransformer.getCustomerAccountResponseToCustomer(
            cdhService.getCustomerAccounts(
                    cdhCustomerTransformer.transformToSearchCustomerAccountRequest(searchRequest), email));
  }

    public void updateWLContactDetails(String authorization, CustomerRequest contactDetails,
        CdhEmployeeDetails cdhEmployeeDetails) {

        var pibaAccountsResponse = pibaAccountClient.getPibaAccounts(authorization, true);
        var accounts = pibaAccountsResponse.getAccounts();
        if (nonNull(accounts) && !accounts.isEmpty()) {
            worldlineService.updateContactDetails(accounts.get(0), contactDetails);
            if (accounts.size() > 1)
                // Update other accounts asynchronously
                updateWLContactDetailsFireAndForget(accounts.subList(1, accounts.size()),
                    contactDetails, cdhEmployeeDetails);
        }
    }

    public void updateWLContactDetailsFireAndForget(List<PibaAccount> accounts, CustomerRequest contactDetails,
        CdhEmployeeDetails cdhEmployeeDetails) {

        // Run on background thread to avoid blocking main thread
        CompletableFuture.runAsync(() -> accounts.forEach(account -> {
            try {
                log.debug(String.format("Update for account: %s", account.getTetheredGuid()));
                worldlineService.updateContactDetails(account, contactDetails);
            } catch (Exception e) {
                log.error(String.format(
                    "Error updating user contact details in Worldline for companyAccountId=%s, employeeAccountId=%s, scheme=%s, tetheredUserGuid=%s",
                    cdhEmployeeDetails.getCompanyAccountId(),
                    cdhEmployeeDetails.getEmployeeAccountId(),
                    account.getScheme(),
                    account.getTetheredGuid()), e);
            }
        }));
    }
}
