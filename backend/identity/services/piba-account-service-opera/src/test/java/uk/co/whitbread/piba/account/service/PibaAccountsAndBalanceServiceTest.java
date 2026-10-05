package uk.co.whitbread.piba.account.service;

import java.util.Comparator;
import java.util.concurrent.Executor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.piba.account.converter.WorldlineAccountTransformer;
import uk.co.whitbread.piba.account.exception.InValidTokenException;
import uk.co.whitbread.piba.account.exception.PibaAccountException;
import uk.co.whitbread.piba.account.exception.PibaGuidException;
import uk.co.whitbread.piba.account.model.*;
import uk.co.whitbread.piba.account.model.enums.RegistrationRoles;
import uk.co.whitbread.piba.account.model.enums.Scheme;
import uk.co.whitbread.piba.account.properties.CdhProperties;
import uk.co.whitbread.piba.account.util.TestUtil;
import uk.co.whitbread.piba.account.util.TransactionFileWriter;
import uk.co.whitbread.piba.account.util.WorldlineUtils;
import uk.co.whitbread.piba.account.validation.WorldLineAccountResponseValidator;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.piba.api.security.WorldLineWebServiceMessageCallback;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewCurrentBalances;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewCurrentBalancesResponse;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGetResponse;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.co.whitbread.piba.account.util.TestUtil.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PibaAccountsAndBalanceServiceTest {

    private static class SameThreadExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }

    private PibaAccountService objectUnderTest;

    @Mock(name = "worldlineWebServiceTemplate")
    private WebServiceTemplate mockWorldlineWebServiceTemplate;

    @Mock
    private WorldlineAccountTransformer mockWorldlineAccountTransformer;

    @Mock
    private WorldLineProperties mockWorldLineProperties;

    @Mock
    private WorldLineAccountResponseValidator mockWorldLineAccountResponseValidator;

    @Mock
    private WorldLineWebServiceMessageCallback mockWorldLineWebServiceMessageCallback;

    @Mock
    private CustomerAccountCurrentBalancesRequest mockCustomerAccountCurrentBalancesRequest;

    @Mock
    private CustomerAccountViewCurrentBalancesResponse mockCustomerAccountViewCurrentBalancesResponse;
    @Mock
    private CustomerAccountViewCurrentBalancesResponse mockCustomerAccountViewCurrentBalancesResponse2;
    @Mock
    private CustomerAccountViewCurrentBalancesResponse mockCustomerAccountViewCurrentBalancesResponse3;
    @Mock
    private CustomerAccountViewCurrentBalances mockCustomerAccountViewCurrentBalances;

    @Mock
    private TetheredUserDetailsGetResponse mockRegisteredUserDetailsResponse1;
    @Mock
    private TetheredUserDetailsGetResponse mockRegisteredUserDetailsResponse2;
    @Mock
    private TetheredUserDetailsGetResponse mockRegisteredUserDetailsResponse3;

    private final TestUtil testUtil = new TestUtil();

    @Mock
    private WorldLineProperties.Piba mockPibaProperties;
    @Mock
    private WorldLineProperties.Piba.Service mockPibaServiceProperties;
    @Mock
    private PibaGuidServiceClient mockPibaGuidServiceClient;
    @Mock
    private TokenService mockAuthTokenService;
    @Mock
    private CdhProperties mockCdhProperties;
    @Mock
    private CdhRegistrationService mockCdhRegistrationService;
    @Mock
    private TransactionFileWriter mockTransactionFileWriter;
    @Mock
    private WorldLineService worldLineService;
    @Mock
    private InvoicesService invoicesService;
    @Mock
    private WorldlineUtils worldlineUtils;

    @BeforeEach
    void setup() {
        when(mockWorldLineProperties.getPiba()).thenReturn(mockPibaProperties);
        when(mockPibaProperties.getService()).thenReturn(mockPibaServiceProperties);
        when(mockPibaServiceProperties.getUrl()).thenReturn("url");
        when(mockCdhProperties.isEnableBbDataFetch()).thenReturn(false);
        objectUnderTest = new PibaAccountService(
            mockWorldlineWebServiceTemplate,
            mockWorldlineAccountTransformer,
            mockWorldLineProperties,
            mockWorldLineAccountResponseValidator,
            mockWorldLineWebServiceMessageCallback,
            mockTransactionFileWriter,
            mockPibaGuidServiceClient,
            mockCdhRegistrationService,
            mockAuthTokenService,
            mockCdhProperties,
            new SameThreadExecutor(),
            worldLineService,
            invoicesService,
            worldlineUtils
        );
    }

    @Test
    void getCustomerDetailsReturnsSuccessfulResponse() {
        PibaTetheredGuidResponse response = testUtil.createPibaTetheredGuidResponse(COMPANY_ID, EMPLOYEE_ID);

        when(mockPibaGuidServiceClient.getGuids(COMPANY_ID, EMPLOYEE_ID)).thenReturn(response);

        PibaTetheredGuidResponse expectedTetheredGuidsResponse = objectUnderTest.getTetheredGuids(COMPANY_ID, EMPLOYEE_ID);
        List<String> expectedTetheredGuids = expectedTetheredGuidsResponse.getTetheredGuid();

        assertEquals(3, expectedTetheredGuids.size());
        assertEquals(TestUtil.TETHERED_USER_GUID, expectedTetheredGuids.get(0));

    }

    @Test
    void getCustomerDetailsReturnsErrorResponse() {

        when(mockPibaGuidServiceClient.getGuids(COMPANY_ID, EMPLOYEE_ID)).thenThrow(new PibaGuidException("Error"));

        assertThatThrownBy(() -> objectUnderTest.getTetheredGuids(COMPANY_ID, EMPLOYEE_ID))
                .isInstanceOf(PibaGuidException.class)
                .hasMessageContaining("Error");

    }

    @Test
    void getTetheredUserDetailsReturnsSuccessfulResponse() {
        TetheredUserDetailsResponse tetheredUserDetailsResponse = testUtil.createTetheredUserDetailsResponse();
        when(worldLineService.getUserDetails(any(),any())).thenReturn(tetheredUserDetailsResponse);

        TetheredUserDetailsResponse userDetails = objectUnderTest.getUserDetails(TETHERED_USER_GUID, Scheme.GB);

        assertEquals(TETHERED_USER_GUID, userDetails.getTetheredUserOverview().getTetheredUserGuid());
        assertEquals(TETHERED_USER_GUID, userDetails.getTetheredUserOverview().getApiUserGuid());
        assertEquals(ACCOUNT_HOLDER, userDetails.getTetheredUserOverview().getUserRole());
        assertEquals(3, userDetails.getTetheredUserOverview().getMyCards());
        assertEquals(SCHEME_CUSTOMER_ID, userDetails.getCustomerAccountOverview().getSchemeCustomerId());
        assertEquals(SCHEME_CUSTOMER_ID, userDetails.getCustomerAccountOverview().getPrimarySchemeCustomerId());
        assertEquals(ACCOUNT_NAME, userDetails.getCustomerAccountOverview().getAccountName());
        assertEquals(ACCOUNT_NUMBER, userDetails.getCustomerAccountOverview().getAccountNumber());
    }

    @Test
    void getTetheredUserDetailsReturnsSuccessfulResponse_CostCenter() {
        TetheredUserDetailsResponse tetheredUserDetailsResponse = testUtil.createTetheredUserDetailsResponse();
        tetheredUserDetailsResponse.getTetheredUserOverview().setUserRole(COST_CENTRE_HOLDER);

        when(worldLineService.getUserDetails(any(),any())).thenReturn(tetheredUserDetailsResponse);
        TetheredUserDetailsResponse userDetails = objectUnderTest.getUserDetails(TETHERED_USER_GUID,  Scheme.GB);

        assertEquals(TETHERED_USER_GUID, userDetails.getTetheredUserOverview().getTetheredUserGuid());
        assertEquals(TETHERED_USER_GUID, userDetails.getTetheredUserOverview().getApiUserGuid());
        assertEquals(COST_CENTRE_HOLDER, userDetails.getTetheredUserOverview().getUserRole());
        assertEquals(3, userDetails.getTetheredUserOverview().getMyCards());
        assertEquals(SCHEME_CUSTOMER_ID, userDetails.getCustomerAccountOverview().getSchemeCustomerId());
        assertEquals(SCHEME_CUSTOMER_ID, userDetails.getCustomerAccountOverview().getPrimarySchemeCustomerId());
        assertEquals(ACCOUNT_NAME, userDetails.getCustomerAccountOverview().getAccountName());
        assertEquals(ACCOUNT_NUMBER, userDetails.getCustomerAccountOverview().getAccountNumber());
    }

    @Test
    void viewCurrentBalanceBySchemeCustomerIdReturnsSuccessfulResponse() {
        CustomerAccountCurrentBalances accountCurrentBalances = testUtil.populateCurrentBalancesResponse();
        when(mockWorldlineAccountTransformer.toCustomerAccountCurrentBalancesRequest(mockCustomerAccountCurrentBalancesRequest))
                .thenReturn(mockCustomerAccountViewCurrentBalances);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
                mockWorldLineProperties.getPiba().getService().getUrl(),
                mockWorldlineAccountTransformer.toCustomerAccountCurrentBalancesRequest(mockCustomerAccountCurrentBalancesRequest),
                mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockCustomerAccountViewCurrentBalancesResponse);

        when(mockWorldlineAccountTransformer.toCustomerAccountCurrentBalancesResponse(mockCustomerAccountViewCurrentBalancesResponse))
                .thenReturn(accountCurrentBalances);

        CustomerAccountCurrentBalances currentBalances = objectUnderTest.viewCurrentBalanceBySchemeCustomerId(mockCustomerAccountCurrentBalancesRequest);

        assertEquals(BigDecimal.valueOf(240.0), currentBalances.getCurrentBalance().getAmount());
        assertEquals(BigDecimal.valueOf(101.2), currentBalances.getAvailable().getAmount());
        assertEquals(BigDecimal.valueOf(100.0), currentBalances.getOutstanding().getAmount());
        assertEquals(BigDecimal.valueOf(0.00), currentBalances.getNewTransactions().getAmount());
        assertEquals(BigDecimal.valueOf(660), currentBalances.getCreditLimit().getAmount());
        assertEquals(BigDecimal.valueOf(0.00), currentBalances.getInterimPayments().getAmount());
        assertEquals("GBP", currentBalances.getCreditLimit().getCurrencyCode());
        assertEquals("£", currentBalances.getCreditLimit().getCurrencySymbol());
        assertEquals(SCHEME_CUSTOMER_ID, currentBalances.getSchemeCustomerId());
    }

    @Test
    void viewCurrentBalanceReturnsSuccessfulResponse() {
        getAccountsAndViewCurrentBalanceMocks();
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken("Bearer random Token")).thenReturn(testUtil.createEmployeeDetails());
        ReflectionTestUtils.setField(objectUnderTest, "tetheringPlusEnabledFlag", false);
        CustomerAccountCurrentBalancesResponse response = objectUnderTest.viewCurrentBalance("Bearer random Token", true);
        viewCurrentBalanceAsserts(response);
    }

    @Test
    void viewCurrentBalanceCdhEnabledReturnsSuccessfulResponse() {
        getAccountsAndViewCurrentBalanceMocks();
        when(mockCdhProperties.isEnableBbDataFetch()).thenReturn(true);
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken("Bearer random Token")).thenReturn(testUtil.createCdhEmployeeDetails());
        ReflectionTestUtils.setField(objectUnderTest, "tetheringPlusEnabledFlag", false);
        CustomerAccountCurrentBalancesResponse response = objectUnderTest.viewCurrentBalance("Bearer random Token", true);
        viewCurrentBalanceAsserts(response);
    }

    @Test
    void viewCurrentBalanceReturnsBalanceWithErrorMessageWhenGetUserDetailsReturnsError() {
        PibaTetheredGuidResponse pibaGuidResponse = testUtil.createPibaTetheredGuidResponse(COMPANY_ID, EMPLOYEE_ID);
        Currency currency = new Currency();
        currency.setAmount(BigDecimal.valueOf(100));
        currency.setCurrencyCode("GBP");
    
        when(mockPibaGuidServiceClient.getGuids(COMPANY_ID, EMPLOYEE_ID)).thenReturn(pibaGuidResponse);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
                mockWorldLineProperties.getPiba().getService().getUrl(),
                mockWorldlineAccountTransformer.toTetheredUserDetailsRequest(TETHERED_USER_GUID, Scheme.GB),
                mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockRegisteredUserDetailsResponse1);
        doThrow(new PibaAccountException("Error")).when(mockWorldLineAccountResponseValidator).validate(mockRegisteredUserDetailsResponse1);
        doReturn(testUtil.createEmployeeDetails()).when(mockAuthTokenService).retrieveEmployeeDetailsAndVerifyToken("Bearer random Token");
        ReflectionTestUtils.setField(objectUnderTest, "tetheringPlusEnabledFlag", false);
        CustomerAccountCurrentBalancesResponse response = objectUnderTest.viewCurrentBalance("Bearer random Token", true);
        List<CustomerAccountCurrentBalances> currentBalance = response.getCurrentBalances();
        assertEquals("Error while retrieving tethered user details", currentBalance.get(0).getErrorCode());

    }

    @Test
    void submit_shouldHandleErrorFromAuthService() {

        // When
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(any())).thenReturn(new EmployeeDetails());
        //Then
        assertThatThrownBy(() -> objectUnderTest.viewCurrentBalance("Bearer Token", true))
                .isInstanceOf(InValidTokenException.class)
                .hasMessageContaining("Error");
    }

    private void getAccountsAndViewCurrentBalanceMocks() {
        PibaTetheredGuidResponse pibaGuidsResponse = testUtil.createPibaTetheredGuidResponse(COMPANY_ID, EMPLOYEE_ID);
        CustomerAccountCurrentBalances customerAccountCurrentBalancesResponse = testUtil.populateCurrentBalancesResponse();
        CustomerAccountCurrentBalances customerAccountCurrentBalancesResponse2 = testUtil.populateCurrentBalancesResponse();
        CustomerAccountCurrentBalances customerAccountCurrentBalancesResponse3 = testUtil.populateCurrentBalancesResponse();
        when(mockPibaGuidServiceClient.getGuids(COMPANY_ID, EMPLOYEE_ID)).thenReturn(pibaGuidsResponse);
        when(mockPibaGuidServiceClient.getMultiGuids(COMPANY_ID, EMPLOYEE_ID)).thenReturn(List.of(pibaGuidsResponse));
        when(mockWorldlineAccountTransformer.toCustomerAccountCurrentBalancesRequest(any())).thenReturn(mockCustomerAccountViewCurrentBalances);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
            mockWorldLineProperties.getPiba().getService().getUrl(),
            mockCustomerAccountViewCurrentBalances,
            mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockCustomerAccountViewCurrentBalancesResponse)
            .thenReturn(mockCustomerAccountViewCurrentBalancesResponse2)
            .thenReturn(mockCustomerAccountViewCurrentBalancesResponse3);
        when(mockWorldlineAccountTransformer.toCustomerAccountCurrentBalancesResponse(mockCustomerAccountViewCurrentBalancesResponse)).thenReturn(customerAccountCurrentBalancesResponse);
        when(mockWorldlineAccountTransformer.toCustomerAccountCurrentBalancesResponse(mockCustomerAccountViewCurrentBalancesResponse2)).thenReturn(customerAccountCurrentBalancesResponse2);
        when(mockWorldlineAccountTransformer.toCustomerAccountCurrentBalancesResponse(mockCustomerAccountViewCurrentBalancesResponse3)).thenReturn(customerAccountCurrentBalancesResponse3);
        TetheredUserDetailsResponse registeredUserResponse1 = testUtil.populateTetheredUserResponse(SCHEME_CUSTOMER_ID);
        TetheredUserDetailsResponse registeredUserResponse2 = testUtil.populateTetheredUserResponse(SCHEME_CUSTOMER_ID2);
        TetheredUserDetailsResponse registeredUserResponse3 = testUtil.populateTetheredUserResponse(SCHEME_CUSTOMER_ID3);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
            mockWorldLineProperties.getPiba().getService().getUrl(),
            mockWorldlineAccountTransformer.toTetheredUserDetailsRequest(TETHERED_USER_GUID, Scheme.GB),
            mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockRegisteredUserDetailsResponse1)
            .thenReturn(mockRegisteredUserDetailsResponse2)
            .thenReturn(mockRegisteredUserDetailsResponse3);
        when(mockWorldlineAccountTransformer.toTetheredUserDetailsResponse(mockRegisteredUserDetailsResponse1)).thenReturn(registeredUserResponse1);
        when(mockWorldlineAccountTransformer.toTetheredUserDetailsResponse(mockRegisteredUserDetailsResponse2)).thenReturn(registeredUserResponse2);
        when(mockWorldlineAccountTransformer.toTetheredUserDetailsResponse(mockRegisteredUserDetailsResponse3)).thenReturn(registeredUserResponse3);
        when(mockCdhRegistrationService.getTetheredGuids(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL)).thenReturn(List.of(pibaGuidsResponse));
        when(worldLineService.getUserDetails("327f7a0c-9a33-41c2-808d-74f15f24797c",Scheme.GB)).thenReturn(registeredUserResponse1);
        when(worldLineService.getUserDetails("a8c30dab-ba44-4b5b-ada6-b241ef65f8ac",Scheme.GB)).thenReturn(registeredUserResponse2);
        when(worldLineService.getUserDetails("a8c30dab-ba44-4b5b-ada6-b241ef65f8fc",Scheme.GB)).thenReturn(registeredUserResponse3);
    }

    private void viewCurrentBalanceAsserts(CustomerAccountCurrentBalancesResponse response) {
        Currency currency = new Currency(BigDecimal.valueOf(101.2), "GBP", "£");
        List<CustomerAccountCurrentBalances> currentBalance = response.getCurrentBalances();
        assertEquals(TestUtil.TETHERED_USER_GUID, currentBalance.get(0).getTetheredGuid());
        assertEquals(SCHEME_CUSTOMER_ID, currentBalance.get(0).getSchemeCustomerId());
        assertEquals(currency, currentBalance.get(0).getAvailable());
        assertEquals(BigDecimal.valueOf(101.2), currentBalance.get(0).getAvailable().getAmount());
        assertEquals(2, currentBalance.get(0).getRegistrationRoles().size());
        assertTrue(currentBalance.get(0).getRegistrationRoles().contains(RegistrationRoles.ACCOUNT_HOLDER));
        assertTrue(currentBalance.get(0).getRegistrationRoles().contains(RegistrationRoles.CARD_HOLDER));
        assertEquals(1, currentBalance.get(1).getRegistrationRoles().size());
        assertTrue(currentBalance.get(1).getRegistrationRoles().contains(RegistrationRoles.FINANCE_USER));
        assertEquals(2, currentBalance.get(2).getRegistrationRoles().size());
        assertTrue(currentBalance.get(2).getRegistrationRoles().contains(RegistrationRoles.FINANCE_USER));
        assertTrue(currentBalance.get(2).getRegistrationRoles().contains(RegistrationRoles.CARD_HOLDER));
    }

    private void getAccountsAsserts(CustomerAccountsResponse response) {
        //response for accounts are processed in parallel so the order is not guaranteed
        List<CustomerAccount> accounts = response.getAccounts().stream()
            .sorted(Comparator.comparingInt(CustomerAccount::getSchemeCustomerId).reversed())
            .toList();
        assertEquals(TestUtil.TETHERED_USER_GUID, accounts.get(0).getTetheredGuid());
        assertEquals(SCHEME_CUSTOMER_ID, accounts.get(0).getSchemeCustomerId());
        assertEquals(2, accounts.get(0).getRegistrationRoles().size());
        assertTrue(accounts.get(0).getRegistrationRoles().contains(RegistrationRoles.ACCOUNT_HOLDER));
        assertTrue(accounts.get(0).getRegistrationRoles().contains(RegistrationRoles.CARD_HOLDER));
        assertEquals(1, accounts.get(1).getRegistrationRoles().size());
        assertTrue(accounts.get(1).getRegistrationRoles().contains(RegistrationRoles.FINANCE_USER));
        assertEquals(2, accounts.get(2).getRegistrationRoles().size());
        assertTrue(accounts.get(2).getRegistrationRoles().contains(RegistrationRoles.FINANCE_USER));
        assertTrue(accounts.get(2).getRegistrationRoles().contains(RegistrationRoles.CARD_HOLDER));
    }

    private void viewCurrentBalanceCostCenterMocks() {
        PibaTetheredGuidResponse pibaGuidsResponse = testUtil.createSinglePibaTetheredGuidResponse(COMPANY_ID, EMPLOYEE_ID);
        when(mockPibaGuidServiceClient.getGuids(COMPANY_ID, EMPLOYEE_ID)).thenReturn(pibaGuidsResponse);
        TetheredUserDetailsResponse registeredUserResponse = testUtil.populateCostCenterTetheredUserResponse();
        when(worldLineService.getUserDetails("327f7a0c-9a33-41c2-808d-74f15f24797c",Scheme.DE)).thenReturn(registeredUserResponse);
    }
    @Test
    void viewCurrentBalanceCostCenterUser() {
        viewCurrentBalanceCostCenterMocks();
        when(mockCdhProperties.isEnableBbDataFetch()).thenReturn(true);
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken("Bearer random Token")).thenReturn(testUtil.createCdhEmployeeDetails());
        ReflectionTestUtils.setField(objectUnderTest, "tetheringPlusEnabledFlag", false);
        CustomerAccountCurrentBalancesResponse response = objectUnderTest.viewCurrentBalance("Bearer random Token", true);

        List<CustomerAccountCurrentBalances> currentBalance = response.getCurrentBalances();
        assertEquals(TestUtil.TETHERED_USER_GUID, currentBalance.get(0).getTetheredGuid());
        assertNull(currentBalance.get(0).getCurrentBalance());
        assertNull(currentBalance.get(0).getOutstanding());
    }

    @ParameterizedTest
    @CsvSource({"true", "false"})
    void getAccountsReturnsSuccessfulResponse(Boolean viewAll) {
        getAccountsAndViewCurrentBalanceMocks();
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken("Bearer random Token")).thenReturn(testUtil.createEmployeeDetails());
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken("Bearer random Token")).thenReturn(testUtil.createCdhEmployeeDetailsWithEmail());
        CustomerAccountsResponse response = objectUnderTest.getAccounts("Bearer random Token", viewAll, false);
        getAccountsAsserts(response);
    }
}

