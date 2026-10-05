package uk.co.whitbread.hotel.account.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.hotel.account.fixture.AzureEmailFixture.createAccountUpdate;
import static uk.co.whitbread.hotel.account.fixture.CdhCustomerFixture.createCustomerAccountRequest;
import static uk.co.whitbread.hotel.account.fixture.CdhCustomerFixture.createCustomerAccountResponse;
import static uk.co.whitbread.hotel.account.fixture.CdhCustomerFixture.createGetCustomerAccountResponse;
import static uk.co.whitbread.hotel.account.fixture.CdhEmployeeFixture.createEmployeeAccountRequest;
import static uk.co.whitbread.hotel.account.fixture.CdhEmployeeFixture.createEmployeeAccountResponse;
import static uk.co.whitbread.hotel.account.fixture.CdhEmployeeFixture.createGetEmployeeResponse;
import static uk.co.whitbread.hotel.account.fixture.CustomerFixture.createCustomer;
import static uk.co.whitbread.hotel.account.fixture.CustomerRequestFixture.createCustomerResponse;
import static uk.co.whitbread.hotel.account.model.BookingChannelCode.CBT;
import static uk.co.whitbread.hotel.account.model.HotelBrandCode.PI;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.hotel.account.client.payment.Payment3CP;
import uk.co.whitbread.hotel.account.client.payment.model.CreateTokenResponse;
import uk.co.whitbread.hotel.account.client.pibaAccount.PibaAccountClient;
import uk.co.whitbread.hotel.account.client.pibaAccount.model.PibaAccount;
import uk.co.whitbread.hotel.account.client.pibaAccount.model.PibaAccountsResponse;
import uk.co.whitbread.hotel.account.exceptions.AccountNotFoundException;
import uk.co.whitbread.hotel.account.mapper.CustomerMapper;
import uk.co.whitbread.hotel.account.mapper.EmployeeMapper;
import uk.co.whitbread.hotel.account.model.Address;
import uk.co.whitbread.hotel.account.model.BillingAddress;
import uk.co.whitbread.hotel.account.model.Business;
import uk.co.whitbread.hotel.account.model.ContactDetail;
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
import uk.co.whitbread.hotel.account.service.worldline.model.Scheme;
import uk.co.whitbread.hotel.account.utils.account.CdhCustomerTransformer;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.auth.exception.Auth0ApiException;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.auth.properties.ManagementProperties;
import uk.co.whitbread.shared.auth.service.ManagementService;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.azureemail.model.AccountUpdate;
import uk.co.whitbread.shared.azureemail.service.AzureEmailService;
import uk.co.whitbread.shared.cdh.CustomerDataService;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.exception.CDHException;
import uk.co.whitbread.shared.cdh.model.CustomerAccountRequest;
import uk.co.whitbread.shared.cdh.model.CustomerAccountResponse;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.SearchCustomerAccountRequest;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HotelAccountsServiceTest {
    private static final String AUTHORIZATION = "authorization";
    private static final String CUSTOMER_ID = "some.custome@email.com";
    private static final String CDH_CUSTOMER_ACCOUNT_ID = "custAccId";
    private static final String COUNTRY_CODE_ISO = "iso_country_code";
    private static final String EMAIL_CURRENT = "email2-test";
    public static final String COMPANY_ID = "company-id-test";
    public static final String EMPLOYEE_ID = "employee-id-test";
    public static final String COMPANY_ACCOUNT_ID = "c7ab3e7c-54fd-4011-961e-05b468176c1b";
    public static final String EMPLOYEE_ACCOUNT_ID = "8d2786bc-7efa-4ecd-9d3f-277757c096eb";
    private final CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
            .userEmail(EMAIL_CURRENT)
            .build();

    @Mock
    private AzureEmailService emailService;
    @Mock
    private ContactDetail contactDetailCurrent;
    @Mock
    private CustomerRequest partialCustomerRequest;
    @Mock
    private Customer customer;
    @Mock
    private CustomerResponse customerResponse;
    @Mock
    private Payment3CP payment3CP;
    @Mock
    private CustomerRequest partialUpdateRequest;
    @Mock
    private Customer customerMock;
    @Mock
    private CreateTokenResponse createTokenResponse;

    @InjectMocks
    @Spy
    private HotelAccountsService sut;

    @Mock
    private PaymentPreference paymentPreference;
    @Mock
    private PaymentCard paymentCard;
    @Mock
    private TokenService authTokenService;
    @Mock
    private Auth0LeisureService auth0LeisureService;
    @Mock
    private Auth0BusinessService auth0BusinessService;
    @Mock
    private CdhService cdhService;
    @Mock
    private CustomerDataService customerDataService;
    @Mock
    private EmployeeDataService employeeDataService;
    @Mock
    private CdhCustomerTransformer cdhCustomerTransformer;
    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private EmployeeMapper employeeMapper;
    @Mock
    private CountriesService countriesService;
    @Mock
    private PibaAccountClient pibaAccountClient;
    @Mock
    private WorldlineService worldlineService;
    @Mock
    private UnleashWrapper<FeatureFlag> unleashWrapper;

    private ContactDetail contactDetail;

    @BeforeEach
    void setUp() {
        var featureFlag = new FeatureFlag();
        featureFlag.setCdhApiDeprecation(new FeatureFlag.Feature());
        lenient().when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
        lenient().when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(false);

        Address address = new Address();
        address.setCountryCode("XZ");
        contactDetail = new ContactDetail();
        contactDetail.setAddress(address);
        doReturn(contactDetail).when(customer).getContactDetail();

        when(contactDetailCurrent.getEmail()).thenReturn(EMAIL_CURRENT);
        when(customerResponse.isSuccess()).thenReturn(true);

        when(customer.getContactDetail()).thenReturn(contactDetailCurrent);
        when(partialCustomerRequest.getContactDetail()).thenReturn(contactDetailCurrent);

        when(countriesService.getCountryCodeISO(anyString())).thenReturn(COUNTRY_CODE_ISO);
    }

    @Test
    void updatePiCdhCustomer_paymentPreferenceCompleted_success() {
        GetCustomerAccountResponse getResponse = createGetCustomerAccountResponse(
                CDH_CUSTOMER_ACCOUNT_ID, EMAIL_CURRENT);
        CustomerAccountRequest request = createCustomerAccountRequest(EMAIL_CURRENT);
        CustomerAccountResponse putResponse = createCustomerAccountResponse(
                CDH_CUSTOMER_ACCOUNT_ID);
        AccountUpdate accountUpdate = createAccountUpdate(EMAIL_CURRENT);

        when(cdhService.getCustomerAccount(CDH_CUSTOMER_ACCOUNT_ID, EMAIL_CURRENT)).thenReturn(
                Optional.of(getResponse));
        when(customerMapper.toCustomer(getResponse)).thenReturn(customerMock);
        when(cdhCustomerTransformer.transformToCustomerAccountRequest(partialUpdateRequest,
                customerMock)).thenReturn(request);
        when(customerDataService.updateCustomerAccount(CDH_CUSTOMER_ACCOUNT_ID,
                request, EMAIL_CURRENT)).thenReturn(putResponse);
        when(customerMapper.toAccountUpdate(request)).thenReturn(accountUpdate);
        when(customerMapper.toCustomerResponse(putResponse)).thenReturn(
                createCustomerResponse(CDH_CUSTOMER_ACCOUNT_ID));

        when(partialUpdateRequest.getPaymentPreference()).thenReturn(paymentPreference);
        when(paymentPreference.getPaymentCard()).thenReturn(paymentCard);

        when(partialUpdateRequest.getContactDetail()).thenReturn(contactDetailCurrent);
        when(partialUpdateRequest.getPaymentPreference().getPaymentCard().getBillingAddress()).thenReturn(
            BillingAddress.builder().line1("line 1").countryCode("RO").postCode("100200").build());
        when(paymentCard.getExpiryDate()).thenReturn("10/24");
        when(paymentCard.getCardNumber()).thenReturn("4111111111111103");

        when(payment3CP.createToken(any())).thenReturn(createTokenResponse);
        when(createTokenResponse.getToken()).thenReturn("token");

        when(customerMock.getPaymentPreference()).thenReturn(paymentPreference);
        when(paymentPreference.getPaymentCard()).thenReturn(paymentCard);

        CustomerResponse cdhResponse = sut.updatePiCdhCustomer(CDH_CUSTOMER_ACCOUNT_ID,
                partialUpdateRequest);

        verify(auth0LeisureService).updateEmailOrPasswordInAuth0(customerMock, partialUpdateRequest, true);
        verify(customerDataService).updateCustomerAccount(CDH_CUSTOMER_ACCOUNT_ID, request,
                EMAIL_CURRENT);
        verify(emailService, timeout(1000)).sendAccountUpdateEmail(accountUpdate);
        verify(paymentCard).setCardToken(any());
        verify(paymentCard).setCardType(createTokenResponse.getCardType());
        verify(payment3CP).createToken(any());
        assertTrue(cdhResponse.isSuccess());
        assertEquals(CDH_CUSTOMER_ACCOUNT_ID, cdhResponse.getCustomerId());
    }

    @Test
    void updatePiCdhCustomer_paymentPreferenceNotCompleted_success() {
        GetCustomerAccountResponse getResponse = createGetCustomerAccountResponse(
                CDH_CUSTOMER_ACCOUNT_ID, EMAIL_CURRENT);
        CustomerAccountRequest request = createCustomerAccountRequest(EMAIL_CURRENT);
        CustomerAccountResponse putResponse = createCustomerAccountResponse(
                CDH_CUSTOMER_ACCOUNT_ID);
        AccountUpdate accountUpdate = createAccountUpdate(EMAIL_CURRENT);

        when(cdhService.getCustomerAccount(CDH_CUSTOMER_ACCOUNT_ID, EMAIL_CURRENT)).thenReturn(
                Optional.of(getResponse));
        when(customerMapper.toCustomer(getResponse)).thenReturn(customerMock);
        when(cdhCustomerTransformer.transformToCustomerAccountRequest(partialUpdateRequest,
                customerMock)).thenReturn(request);
        when(customerDataService.updateCustomerAccount(CDH_CUSTOMER_ACCOUNT_ID,
                request, EMAIL_CURRENT)).thenReturn(putResponse);
        when(customerMapper.toAccountUpdate(request)).thenReturn(accountUpdate);
        when(customerMapper.toCustomerResponse(putResponse)).thenReturn(
                createCustomerResponse(CDH_CUSTOMER_ACCOUNT_ID));

        when(partialUpdateRequest.getContactDetail()).thenReturn(contactDetailCurrent);
        when(partialUpdateRequest.getContactDetail().getAddress()).thenReturn(
                Address.builder().line1("line 1").countryCode("RO").postCode("100200").build());
        when(paymentCard.getExpiryDate()).thenReturn("10/24");

        when(payment3CP.createToken(any())).thenReturn(createTokenResponse);
        when(createTokenResponse.getToken()).thenReturn("token");

        when(customerMock.getPaymentPreference()).thenReturn(paymentPreference);
        when(paymentPreference.getPaymentCard()).thenReturn(paymentCard);

        CustomerResponse cdhResponse = sut.updatePiCdhCustomer(CDH_CUSTOMER_ACCOUNT_ID,
                partialUpdateRequest);

        verify(auth0LeisureService).updateEmailOrPasswordInAuth0(customerMock, partialUpdateRequest, true);
        verify(customerDataService).updateCustomerAccount(CDH_CUSTOMER_ACCOUNT_ID, request,
                EMAIL_CURRENT);
        verify(emailService, timeout(1000)).sendAccountUpdateEmail(accountUpdate);
        verify(paymentCard, times(0)).setCardToken(any());
        verify(paymentCard, times(0)).setCardType(any());
        verify(payment3CP, times(0)).createToken(any());
        assertTrue(cdhResponse.isSuccess());
        assertEquals(CDH_CUSTOMER_ACCOUNT_ID, cdhResponse.getCustomerId());
    }

    @Test
    void updatePiCdhCustomer_paymentPreferenceCompleted_cardNumberMasked_success() {
        GetCustomerAccountResponse getResponse = createGetCustomerAccountResponse(
                CDH_CUSTOMER_ACCOUNT_ID, EMAIL_CURRENT);
        CustomerAccountRequest request = createCustomerAccountRequest(EMAIL_CURRENT);
        CustomerAccountResponse putResponse = createCustomerAccountResponse(
                CDH_CUSTOMER_ACCOUNT_ID);
        AccountUpdate accountUpdate = createAccountUpdate(EMAIL_CURRENT);

        when(cdhService.getCustomerAccount(CDH_CUSTOMER_ACCOUNT_ID, EMAIL_CURRENT)).thenReturn(
                Optional.of(getResponse));
        when(customerMapper.toCustomer(getResponse)).thenReturn(customerMock);
        when(cdhCustomerTransformer.transformToCustomerAccountRequest(partialUpdateRequest,
                customerMock)).thenReturn(request);
        when(customerDataService.updateCustomerAccount(CDH_CUSTOMER_ACCOUNT_ID,
                request, EMAIL_CURRENT)).thenReturn(putResponse);
        when(customerMapper.toAccountUpdate(request)).thenReturn(accountUpdate);
        when(customerMapper.toCustomerResponse(putResponse)).thenReturn(
                createCustomerResponse(CDH_CUSTOMER_ACCOUNT_ID));

        when(partialUpdateRequest.getPaymentPreference()).thenReturn(paymentPreference);
        when(paymentPreference.getPaymentCard()).thenReturn(paymentCard);
        when(paymentCard.getCardNumber()).thenReturn("************2233");

        when(partialUpdateRequest.getContactDetail()).thenReturn(contactDetailCurrent);
        when(partialUpdateRequest.getPaymentPreference().getPaymentCard().getBillingAddress()).thenReturn(
            BillingAddress.builder().line1("line 1").countryCode("RO").postCode("100200").build());
        when(paymentCard.getExpiryDate()).thenReturn("10/24");

        when(payment3CP.createToken(any())).thenReturn(createTokenResponse);
        when(createTokenResponse.getToken()).thenReturn("token");

        when(customerMock.getPaymentPreference()).thenReturn(paymentPreference);
        when(paymentPreference.getPaymentCard()).thenReturn(paymentCard);

        CustomerResponse cdhResponse = sut.updatePiCdhCustomer(CDH_CUSTOMER_ACCOUNT_ID,
                partialUpdateRequest);

        verify(auth0LeisureService).updateEmailOrPasswordInAuth0(customerMock, partialUpdateRequest, true);
        verify(customerDataService).updateCustomerAccount(CDH_CUSTOMER_ACCOUNT_ID, request,
                EMAIL_CURRENT);
        verify(emailService, timeout(1000)).sendAccountUpdateEmail(accountUpdate);
        verify(paymentCard, times(0)).setCardToken(any());
        verify(paymentCard, times(0)).setCardType(any());
        verify(payment3CP, times(0)).createToken(any());
        assertTrue(cdhResponse.isSuccess());
        assertEquals(CDH_CUSTOMER_ACCOUNT_ID, cdhResponse.getCustomerId());
    }

    @Test
    void updatePiCdhCustomer_throwsAccountNotFoundException() {
        when(cdhService.getCustomerAccount(CDH_CUSTOMER_ACCOUNT_ID, EMAIL_CURRENT)).thenReturn(
                Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> sut.updatePiCdhCustomer(CDH_CUSTOMER_ACCOUNT_ID, partialCustomerRequest),
                String.format("Account with id %s was not found", CDH_CUSTOMER_ACCOUNT_ID));
    }

    @Test
    void updatePiCdhCustomer_throwsCdhException() {
        GetCustomerAccountResponse getResponse = createGetCustomerAccountResponse(
                CDH_CUSTOMER_ACCOUNT_ID, EMAIL_CURRENT);
        CustomerAccountRequest request = createCustomerAccountRequest(EMAIL_CURRENT);


        when(cdhService.getCustomerAccount(CDH_CUSTOMER_ACCOUNT_ID, EMAIL_CURRENT)).thenReturn(
                Optional.of(getResponse));
        when(customerMapper.toCustomer(getResponse)).thenReturn(customerMock);
        when(cdhCustomerTransformer.transformToCustomerAccountRequest(partialCustomerRequest,
                customerMock)).thenReturn(request);
        when(customerDataService.updateCustomerAccount(CDH_CUSTOMER_ACCOUNT_ID,
                request, EMAIL_CURRENT)).thenThrow(new CDHException());

        when(payment3CP.createToken(any())).thenReturn(CreateTokenResponse.builder().token("100").build());

        when(partialCustomerRequest.getPaymentPreference()).thenReturn(paymentPreference);
        when(paymentPreference.getPaymentCard()).thenReturn(paymentCard);

        when(partialCustomerRequest.getContactDetail()).thenReturn(contactDetailCurrent);
        when(partialCustomerRequest.getPaymentPreference().getPaymentCard().getBillingAddress()).thenReturn(
            BillingAddress.builder().line1("line 1").countryCode("RO").postCode("100200").build());
        when(paymentCard.getExpiryDate()).thenReturn("10/24");
        when(paymentCard.getCardNumber()).thenReturn("4111111111111103");

        when(customerMock.getPaymentPreference()).thenReturn(paymentPreference);
        when(paymentPreference.getPaymentCard()).thenReturn(paymentCard);

        assertThrows(CDHException.class, () -> sut.updatePiCdhCustomer(CDH_CUSTOMER_ACCOUNT_ID, partialCustomerRequest));

        verify(auth0LeisureService).updateEmailOrPasswordInAuth0(customerMock, partialCustomerRequest, true);
        verify(auth0LeisureService).rollbackUserUpdate(customerMock, partialCustomerRequest, true);
    }

    @Test
    void updateBbCdhEmployee_success() {
        var mockedFeatureFlag = mock(FeatureFlag.class);
        when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getCdhApiDeprecation()))
            .thenReturn(true);
        var employee = createGetEmployeeResponse(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID,
                EMAIL_CURRENT);
        var customer = createCustomer(EMAIL_CURRENT);
        var updateRequest = createEmployeeAccountRequest(EMAIL_CURRENT);
        var response = createEmployeeAccountResponse(EMPLOYEE_ACCOUNT_ID);

        when(employeeDataService.getEmployeeV2(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID,
                EMAIL_CURRENT)).thenReturn(Optional.of(employee));
        when(employeeMapper.toCustomer(employee)).thenReturn(customer);
        when(employeeMapper.toEmployeeAccountRequest(employee)).thenReturn(updateRequest);
        when(employeeMapper.toEmployeeAccountRequest(partialCustomerRequest,
                updateRequest)).thenReturn(updateRequest);
        when(employeeDataService.updateEmployeeAccount(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID,
                updateRequest, EMAIL_CURRENT)).thenReturn(response);
        when(employeeMapper.toCustomerResponse(response)).thenReturn(
                createCustomerResponse(EMPLOYEE_ACCOUNT_ID));

        var cdhResponse = sut.updateBbCdhEmployee(cdhEmployeeDetails, partialCustomerRequest);

        verify(auth0BusinessService).updateEmailOrPasswordInAuth0(customer, partialCustomerRequest, true);
        verify(employeeDataService).updateEmployeeAccount(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID,
                updateRequest, EMAIL_CURRENT);
        assertTrue(cdhResponse.isSuccess());
        assertEquals(EMPLOYEE_ACCOUNT_ID, cdhResponse.getCustomerId());
    }

    @Test
    void updateBbCdhEmployee_throwsAccountNotFoundException() {
        when(employeeDataService.getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID,
                EMAIL_CURRENT)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> sut.updateBbCdhEmployee(cdhEmployeeDetails, partialCustomerRequest),
                String.format("Employee %s from company %s was not found", EMPLOYEE_ACCOUNT_ID,
                        COMPANY_ACCOUNT_ID));
    }

    @Test
    void updateBbCdhEmployee_throwsCdhException() {
        var employee = createGetEmployeeResponse(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID,
                EMAIL_CURRENT);
        var customer = createCustomer(EMAIL_CURRENT);
        var updateRequest = createEmployeeAccountRequest(EMAIL_CURRENT);

        when(employeeDataService.getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID,
                EMAIL_CURRENT)).thenReturn(Optional.of(employee));
        when(employeeMapper.toCustomer(employee)).thenReturn(customer);
        when(employeeMapper.toEmployeeAccountRequest(employee)).thenReturn(updateRequest);
        when(employeeMapper.toEmployeeAccountRequest(partialCustomerRequest,
                updateRequest)).thenReturn(updateRequest);
        when(employeeDataService.updateEmployeeAccount(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID,
                updateRequest, EMAIL_CURRENT)).thenThrow(new CDHException());

        assertThrows(CDHException.class, () -> sut.updateBbCdhEmployee(cdhEmployeeDetails, partialCustomerRequest));

        verify(auth0BusinessService).updateEmailOrPasswordInAuth0(customer, partialCustomerRequest, true);
        verify(auth0BusinessService).rollbackUserUpdate(customer, partialCustomerRequest, true);
    }

    @Test
    void deletePICdhCustomer_piDataFetchEnabled() {
        sut.deleteCdhPICustomer(CDH_CUSTOMER_ACCOUNT_ID, CUSTOMER_ID);

        verify(customerDataService).deleteCustomerAccount(CDH_CUSTOMER_ACCOUNT_ID, CUSTOMER_ID);
        verify(auth0LeisureService).deleteUser(CUSTOMER_ID);
    }

    @Test
    void deletePICdhCustomer_throwsException() {
        ManagementService managementService = mock(ManagementService.class);
        ManagementProperties managementProperties = mock(ManagementProperties.class);
        when(auth0LeisureService.getAuthManagementService()).thenReturn(managementService);
        when(managementService.getManagementProperties()).thenReturn(managementProperties);
        when(managementProperties.getConnection()).thenReturn("connection");
        doThrow(new Auth0ApiException("error", 500, "error", "error")).when(auth0LeisureService).deleteUser(CUSTOMER_ID);

        assertThrows(AuthServiceException.class, () -> sut.deleteCdhPICustomer(CDH_CUSTOMER_ACCOUNT_ID, CUSTOMER_ID));
    }

    @Test
    void shouldSearchCustomer() {
        SearchCustomerRequest searchCustomerRequest = new SearchCustomerRequest();
        searchCustomerRequest.setEmail(EMAIL_CURRENT);

        SearchCustomerAccountRequest searchCustomerAccountRequest = SearchCustomerAccountRequest.builder()
                .email(EMAIL_CURRENT).build();

        GetCustomerAccountResponse getCustomerAccountResponse = createGetCustomerAccountResponse(
                CDH_CUSTOMER_ACCOUNT_ID, EMAIL_CURRENT);

        when(cdhCustomerTransformer.transformToSearchCustomerAccountRequest(searchCustomerRequest))
                .thenReturn(searchCustomerAccountRequest);
        when(cdhService.getCustomerAccounts(any(), any())).thenReturn(any());
        when(cdhCustomerTransformer.getCustomerAccountResponseToCustomer(List.of(getCustomerAccountResponse)))
                .thenReturn(List.of(getCustomer()));

        List<Customer> result = sut.getCustomer(searchCustomerRequest, EMAIL_CURRENT);
        assertNotNull(result);

    }

    @Test
    void testShouldGetPiCustomerFromCdh() {
        GetCustomerAccountResponse customerAccountResponse = new GetCustomerAccountResponse();
        doReturn(Optional.of(customerAccountResponse)).when(customerDataService)
                .getCustomerAccount(eq(CDH_CUSTOMER_ACCOUNT_ID), eq(CUSTOMER_ID));
        doReturn(Optional.of(CDH_CUSTOMER_ACCOUNT_ID)).when(authTokenService)
                .retrieveCustomerAccountIdAndVerifyToken(any());
        doReturn(getCustomer()).when(cdhCustomerTransformer)
                .getCustomerAccountResponseToCustomer(customerAccountResponse);

        sut.getCustomer(new HotelCustomerRequest(AUTHORIZATION, CUSTOMER_ID, PI, false, CBT));

        verify(customerDataService).getCustomerAccount(CDH_CUSTOMER_ACCOUNT_ID, CUSTOMER_ID);
    }

    @Test
    void testShouldGetPiCustomerFromCdh_withCdhFeatureFlagEnabled() {
        var mockedFeatureFlag = mock(FeatureFlag.class);
        when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getCdhApiDeprecation()))
            .thenReturn(true);
        GetCustomerAccountResponse customerAccountResponse = new GetCustomerAccountResponse();
        when(customerDataService.getCustomerAccountV3(CDH_CUSTOMER_ACCOUNT_ID, CUSTOMER_ID))
            .thenReturn(Optional.of(customerAccountResponse));
        when(authTokenService.retrieveCustomerAccountIdAndVerifyToken(any()))
            .thenReturn(Optional.of(CDH_CUSTOMER_ACCOUNT_ID));
        when(cdhCustomerTransformer.getCustomerAccountResponseToCustomer(customerAccountResponse))
            .thenReturn(getCustomer());

        sut.getCustomer(new HotelCustomerRequest(AUTHORIZATION, CUSTOMER_ID, PI, false, CBT));

        verify(customerDataService).getCustomerAccountV3(CDH_CUSTOMER_ACCOUNT_ID, CUSTOMER_ID);
    }

    @Test
    void testShouldGetIbTetheredCustomerFromCdh() {
        var mockedFeatureFlag = mock(FeatureFlag.class);
        when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getCdhApiDeprecation()))
            .thenReturn(true);
        doReturn(PibaAccountsResponse.builder()
            .accounts(List.of(PibaAccount.builder().build(),
                PibaAccount.builder().tetheredGuid("tetheredGuid").build())).build())
            .when(pibaAccountClient).getPibaAccounts(any(), anyBoolean());
        doReturn(Optional.of(new GetEmployeeResponse())).when(employeeDataService)
            .getEmployeeV2(any(), any(), any());
        doReturn(buildCdhEmployeeDetails()).when(authTokenService)
            .retrieveCdhEmployeeDetailsAndVerifyToken(any());
        doReturn(createEmployeeDetails(COMPANY_ID, EMPLOYEE_ID)).when(authTokenService)
            .retrieveEmployeeDetailsAndVerifyToken(any());
        doReturn(getCustomer()).when(employeeMapper).toCustomer(any());

        var result = sut.getCustomer(
            HotelCustomerRequest.builder().authorization(AUTHORIZATION).customerId(CUSTOMER_ID)
                .business(true).build());

        assertTrue(result.getBusiness().isTethered());
    }

    @Test
    void testGetIbTetheredCustomerFromCdh_allTetheredGuidsAreNull() {
        doReturn(PibaAccountsResponse.builder()
            .accounts(List.of(PibaAccount.builder().build(), PibaAccount.builder().build())).build())
            .when(pibaAccountClient).getPibaAccounts(any(), anyBoolean());
        doReturn(Optional.of(new GetEmployeeResponse())).when(employeeDataService)
            .getEmployee(any(), any(), any());
        doReturn(buildCdhEmployeeDetails()).when(authTokenService)
            .retrieveCdhEmployeeDetailsAndVerifyToken(any());
        doReturn(createEmployeeDetails(COMPANY_ID, EMPLOYEE_ID)).when(authTokenService)
            .retrieveEmployeeDetailsAndVerifyToken(any());
        doReturn(getCustomer()).when(employeeMapper).toCustomer(any());

        var result = sut.getCustomer(
            HotelCustomerRequest.builder().authorization(AUTHORIZATION).customerId(CUSTOMER_ID)
                .business(true).build());

        assertFalse(result.getBusiness().isTethered());
    }

    @Test
    void testShouldGetIbUnTetheredCustomerFromCdh() {
        doReturn(PibaAccountsResponse.builder().accounts(List.of()).build())
            .when(pibaAccountClient).getPibaAccounts(any(), anyBoolean());
        doReturn(Optional.of(new GetEmployeeResponse())).when(employeeDataService)
            .getEmployee(any(), any(), any());
        doReturn(buildCdhEmployeeDetails()).when(authTokenService)
            .retrieveCdhEmployeeDetailsAndVerifyToken(any());
        doReturn(createEmployeeDetails(COMPANY_ID, EMPLOYEE_ID)).when(authTokenService)
            .retrieveEmployeeDetailsAndVerifyToken(any());
        doReturn(getCustomer()).when(employeeMapper).toCustomer(any());

        var result = sut.getCustomer(
            HotelCustomerRequest.builder().authorization(AUTHORIZATION).customerId(CUSTOMER_ID)
                .business(true).build());

        assertFalse(result.getBusiness().isTethered());
    }

    private EmployeeDetails createEmployeeDetails(String companyId, String employeeId) {
        return new EmployeeDetails(companyId, employeeId);
    }

    private static CdhEmployeeDetails buildCdhEmployeeDetails() {
        return CdhEmployeeDetails.builder().employeeAccountId(EMPLOYEE_ACCOUNT_ID)
                .companyAccountId(COMPANY_ACCOUNT_ID).build();
    }

    private Customer getCustomer() {
        Customer customer = new Customer();
        customer.setBusiness(new Business());
        customer.setContactDetail(contactDetail);
        PaymentPreference paymentPreference = new PaymentPreference();
        BillingAddress billingAddress = BillingAddress.builder()
            .line1("line1")
            .countryCode("GB")
            .postCode("EC1A 1AB")
            .build();
        PaymentCard paymentCard = PaymentCard.builder()
            .cardNumber("12334567890123456")
            .expiryDate("11/30")
            .cardToken("1222222222223456")
            .build();
        paymentCard.setBillingAddress(billingAddress);
        paymentPreference.setPaymentCard(paymentCard);
        customer.setPaymentPreference(paymentPreference);
        return customer;
    }

    @Test
    void updateWLContactDetails_success() {
        var pibaAccount = buildPibaAccount();
        var contactDetails = new CustomerRequest();
        contactDetails.setContactDetail(contactDetail);

        doReturn(PibaAccountsResponse.builder().accounts(List.of(pibaAccount)).build())
            .when(pibaAccountClient).getPibaAccounts(any(), anyBoolean());
        doNothing().when(worldlineService).updateContactDetails(any(), any());

        sut.updateWLContactDetails("authorization", contactDetails, CdhEmployeeDetails.builder().build());

        var pibaAccountCaptor = ArgumentCaptor.forClass(PibaAccount.class);
        var contactDetailCaptor = ArgumentCaptor.forClass(CustomerRequest.class);

        verify(worldlineService, timeout(1000))
            .updateContactDetails(pibaAccountCaptor.capture(), contactDetailCaptor.capture());

        assertThat(pibaAccountCaptor.getValue(), is(pibaAccount));
        assertThat(contactDetailCaptor.getValue(), is(contactDetails));

    }

    @Test
    void updateWLContactDetails_whenMoreThanOneAccount_success() {
        var pibaAccount1 = buildPibaAccount();
        var pibaAccount2 = buildPibaAccount();
        var contactDetails = new CustomerRequest();
        contactDetails.setContactDetail(contactDetail);

        doReturn(PibaAccountsResponse.builder().accounts(List.of(pibaAccount1, pibaAccount2)).build())
            .when(pibaAccountClient).getPibaAccounts(any(), anyBoolean());
        doNothing().when(worldlineService).updateContactDetails(any(), any());

        sut.updateWLContactDetails("authorization", contactDetails, CdhEmployeeDetails.builder().build());

        var pibaAccountCaptor = ArgumentCaptor.forClass(PibaAccount.class);
        var contactDetailCaptor = ArgumentCaptor.forClass(CustomerRequest.class);

        verify(worldlineService, timeout(1000).times(2))
            .updateContactDetails(pibaAccountCaptor.capture(), contactDetailCaptor.capture());

        List<PibaAccount> capturedAccounts = pibaAccountCaptor.getAllValues();
        assertThat(capturedAccounts.get(0), is(pibaAccount1));
        assertThat(capturedAccounts.get(1), is(pibaAccount2));
        assertThat(contactDetailCaptor.getValue(), is(contactDetails));
    }

    @Test
    void updateWLContactDetails_whenMoreThanOneAccountAndError_shouldNotThrow() {
        var pibaAccount1 = buildPibaAccount();
        var pibaAccount2 = buildPibaAccount();
        pibaAccount2.setAccountName("Different Account Name");
        var contactDetails = new CustomerRequest();
        contactDetails.setContactDetail(contactDetail);
        var mockCdhEmployeeDetails = CdhEmployeeDetails.builder().build();

        doReturn(PibaAccountsResponse.builder().accounts(List.of(pibaAccount1, pibaAccount2)).build())
            .when(pibaAccountClient).getPibaAccounts(any(), anyBoolean());
        doThrow(new RuntimeException()).when(worldlineService)
            .updateContactDetails(eq(pibaAccount2), any());

        sut.updateWLContactDetails("authorization", contactDetails,
                mockCdhEmployeeDetails);

        var pibaAccountCaptor = ArgumentCaptor.forClass(PibaAccount.class);
        var contactDetailCaptor = ArgumentCaptor.forClass(CustomerRequest.class);

        verify(worldlineService, timeout(1000).times(2))
            .updateContactDetails(pibaAccountCaptor.capture(), contactDetailCaptor.capture());

        List<PibaAccount> capturedAccounts = pibaAccountCaptor.getAllValues();
        assertThat(capturedAccounts.get(0), is(pibaAccount1));
        assertThat(capturedAccounts.get(1), is(pibaAccount2));
        assertThat(contactDetailCaptor.getValue(), is(contactDetails));
    }

    @Test
    void updateWLContactDetails_noAccountsReturnedByPibaAccountService() {
        doReturn(PibaAccountsResponse.builder().accounts(List.of()).build())
            .when(pibaAccountClient).getPibaAccounts(any(), anyBoolean());
        doNothing().when(worldlineService).updateContactDetails(any(), any());

        sut.updateWLContactDetails("authorization", new CustomerRequest(),
            CdhEmployeeDetails.builder().build());

        verify(worldlineService, timeout(1000).times(0)).updateContactDetails(any(), any());
    }

    private PibaAccount buildPibaAccount() {
        return PibaAccount.builder().schemeCustomerId(1).scheme(Scheme.GB).accountNumber("accNo")
            .accountName("accName").tetheredGuid("tetheredUserGuid").apiUserGuid("apiUserGuid")
            .build();
    }
}
