package uk.co.whitbread.piba.account.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.piba.account.converter.WorldlineAccountTransformer;
import uk.co.whitbread.piba.account.exception.InValidTokenException;
import uk.co.whitbread.piba.account.exception.PibaAccountException;
import uk.co.whitbread.piba.account.model.Currency;
import uk.co.whitbread.piba.account.model.CustomerAccountCurrentBalances;
import uk.co.whitbread.piba.account.model.CustomerAccountCurrentBalancesResponse;
import uk.co.whitbread.piba.account.model.PibaTetheredGuidResponse;
import uk.co.whitbread.piba.account.model.TetheredUserDetailsResponse;
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
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.piba.account.util.TestUtil.COMPANY_ID;
import static uk.co.whitbread.piba.account.util.TestUtil.EMPLOYEE_ID;
import static uk.co.whitbread.piba.account.util.TestUtil.SCHEME_CUSTOMER_ID;
import static uk.co.whitbread.piba.account.util.TestUtil.SCHEME_CUSTOMER_ID2;
import static uk.co.whitbread.piba.account.util.TestUtil.SCHEME_CUSTOMER_ID3;
import static uk.co.whitbread.piba.account.util.TestUtil.USER_EMAIL;

@ExtendWith(MockitoExtension.class)
class PibaAccountBalanceSummaryServiceTest {

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
    @Mock
    private WorldLineProperties.Piba mockPibaProperties;
    @Mock
    private WorldLineProperties.Piba.Service mockPibaServiceProperties;
    @Mock
    private TokenService mockAuthTokenService;
    @Mock
    private CdhProperties mockCdhProperties;
    @Mock
    private CdhRegistrationService mockCdhRegistrationService;
    @Mock
    private WorldLineService worldLineService;
    @Mock
    private TransactionFileWriter mockTransactionFileWriter;
    @Mock
    private PibaGuidServiceClient mockPibaGuidServiceClient;
    @Mock
    private InvoicesService invoicesService;
    @Mock
    private WorldlineUtils worldlineUtils;

    private final TestUtil testUtil = new TestUtil();

    @BeforeEach
    void setup() {
        Mockito.lenient().when(mockWorldLineProperties.getPiba()).thenReturn(mockPibaProperties);
        Mockito.lenient().when(mockPibaProperties.getService()).thenReturn(mockPibaServiceProperties);
        Mockito.lenient().when(mockPibaServiceProperties.getUrl()).thenReturn("url");
        Executor worldLineExecutor = Runnable::run;
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
            worldLineExecutor,
            worldLineService,
            invoicesService,
            worldlineUtils
        );
    }

    @Test
    void viewCurrentBalanceSummary_shouldHandleErrorFromAuthService() {
        // Arrange
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(any())).thenReturn(new EmployeeDetails());

        // Assert
        assertThatThrownBy(() -> objectUnderTest.viewCurrentBalanceSummary("Bearer Token", true))
            .isInstanceOf(InValidTokenException.class)
            .hasMessageContaining("Error");
    }

    @Test
    void viewCurrentBalanceSummary_shouldReturnSuccessfulResponse() {
        // Arrange
        viewCurrentBalanceSummaryMocks();
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken("Bearer random Token")).thenReturn(testUtil.createEmployeeDetails());
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken("Bearer random Token")).thenReturn(testUtil.createCdhEmployeeDetailsWithEmail());

        // Assert
        CustomerAccountCurrentBalancesResponse response = objectUnderTest.viewCurrentBalanceSummary("Bearer random Token", true);
        viewCurrentBalanceAsserts(response);
        verify(worldLineService, times(3)).getUserDetails(any(), any());
    }

    @Test
    void viewCurrentBalanceSummary_shouldReturnBalanceWithErrorMessageWhenGetUserDetailsReturnsError() {
        // Arrange
        PibaTetheredGuidResponse pibaGuidResponse = testUtil.createPibaTetheredGuidResponse(COMPANY_ID, EMPLOYEE_ID);
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken("Bearer random Token")).thenReturn(testUtil.createEmployeeDetails());
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken("Bearer random Token")).thenReturn(testUtil.createCdhEmployeeDetailsWithEmail());
        when(mockCdhRegistrationService.getTetheredGuids(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL)).thenReturn(List.of(pibaGuidResponse));
        doThrow(new PibaAccountException("Error")).when(worldLineService).getUserDetails(any(),any());

        // Act
        CustomerAccountCurrentBalancesResponse response = objectUnderTest.viewCurrentBalanceSummary("Bearer random Token", true);

        // Assert
        List<CustomerAccountCurrentBalances> currentBalance = response.getCurrentBalances();
        assertEquals("Error while retrieving tethered user details", currentBalance.get(0).getErrorCode());
    }

    @Test
    void viewCurrentBalanceSummary_shouldNotGetBalanceForCostCenterUser() {
        // Arrange
        PibaTetheredGuidResponse pibaGuidsResponse = testUtil.createSinglePibaTetheredGuidResponse(COMPANY_ID, EMPLOYEE_ID);
        when(mockCdhRegistrationService.getTetheredGuids(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL)).thenReturn(List.of(pibaGuidsResponse));
        TetheredUserDetailsResponse registeredUserResponse = testUtil.populateCostCenterTetheredUserResponse();
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken("Bearer random Token")).thenReturn(testUtil.createEmployeeDetails());
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken("Bearer random Token")).thenReturn(testUtil.createCdhEmployeeDetailsWithEmail());
        when(worldLineService.getUserDetails(any(),any())).thenReturn(registeredUserResponse);

        // Act
        CustomerAccountCurrentBalancesResponse response = objectUnderTest.viewCurrentBalanceSummary("Bearer random Token", true);

        // Assert
        List<CustomerAccountCurrentBalances> currentBalance = response.getCurrentBalances();
        assertEquals(TestUtil.TETHERED_USER_GUID, currentBalance.get(0).getTetheredGuid());
        assertNull(currentBalance.get(0).getCurrentBalance());
        assertNull(currentBalance.get(0).getOutstanding());
    }

    @Test
    void viewCurrentBalanceSummary_shouldNotGetBalanceForAccountCardHolder() {
        // Arrange
        PibaTetheredGuidResponse pibaGuidsResponse = testUtil.createSinglePibaTetheredGuidResponse(COMPANY_ID, EMPLOYEE_ID);
        when(mockCdhRegistrationService.getTetheredGuids(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL)).thenReturn(List.of(pibaGuidsResponse));
        TetheredUserDetailsResponse registeredUserResponse = testUtil.populateCostCenterTetheredUserResponse();
        registeredUserResponse.getTetheredUserOverview().setUserRole(RegistrationRoles.ACCOUNT_CARD_HOLDER.getRegistrationRoles());
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken("Bearer random Token")).thenReturn(testUtil.createEmployeeDetails());
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken("Bearer random Token")).thenReturn(testUtil.createCdhEmployeeDetailsWithEmail());
        when(worldLineService.getUserDetails(any(),any())).thenReturn(registeredUserResponse);

        // Act
        CustomerAccountCurrentBalancesResponse response = objectUnderTest.viewCurrentBalanceSummary("Bearer random Token", true);

        // Assert
        List<CustomerAccountCurrentBalances> currentBalance = response.getCurrentBalances();
        assertEquals(TestUtil.TETHERED_USER_GUID, currentBalance.get(0).getTetheredGuid());
        assertNull(currentBalance.get(0).getCurrentBalance());
        assertNull(currentBalance.get(0).getOutstanding());
    }

    private void viewCurrentBalanceSummaryMocks() {
        PibaTetheredGuidResponse pibaGuidsResponse = testUtil.createPibaTetheredGuidResponse(COMPANY_ID, EMPLOYEE_ID);
        CustomerAccountCurrentBalances customerAccountCurrentBalancesResponse = testUtil.populateCurrentBalancesResponse();
        CustomerAccountCurrentBalances customerAccountCurrentBalancesResponse2 = testUtil.populateCurrentBalancesResponse();
        CustomerAccountCurrentBalances customerAccountCurrentBalancesResponse3 = testUtil.populateCurrentBalancesResponse();
        when(mockWorldlineAccountTransformer.toCustomerAccountCurrentBalancesRequest(any())).thenReturn(mockCustomerAccountViewCurrentBalances);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
            mockWorldLineProperties.getPiba().getService().getUrl(),
            mockCustomerAccountViewCurrentBalances,
            mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockCustomerAccountViewCurrentBalancesResponse)
            .thenReturn(mockCustomerAccountViewCurrentBalancesResponse2)
            .thenReturn(mockCustomerAccountViewCurrentBalancesResponse3);
        when(mockWorldlineAccountTransformer.toCustomerAccountCurrentBalancesResponse(mockCustomerAccountViewCurrentBalancesResponse))
            .thenReturn(customerAccountCurrentBalancesResponse);
        when(mockWorldlineAccountTransformer.toCustomerAccountCurrentBalancesResponse(mockCustomerAccountViewCurrentBalancesResponse2))
            .thenReturn(customerAccountCurrentBalancesResponse2);
        when(mockWorldlineAccountTransformer.toCustomerAccountCurrentBalancesResponse(mockCustomerAccountViewCurrentBalancesResponse3))
            .thenReturn(customerAccountCurrentBalancesResponse3);
        TetheredUserDetailsResponse registeredUserResponse1 = testUtil.populateTetheredUserResponse(SCHEME_CUSTOMER_ID);
        TetheredUserDetailsResponse registeredUserResponse2 = testUtil.populateTetheredUserResponse(SCHEME_CUSTOMER_ID2);
        TetheredUserDetailsResponse registeredUserResponse3 = testUtil.populateTetheredUserResponse(SCHEME_CUSTOMER_ID3);
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
        assertEquals(TestUtil.TETHERED_USER_GUID2, currentBalance.get(1).getTetheredGuid());
        assertEquals(SCHEME_CUSTOMER_ID2, currentBalance.get(1).getSchemeCustomerId());
        assertEquals(1, currentBalance.get(1).getRegistrationRoles().size());
        assertTrue(currentBalance.get(1).getRegistrationRoles().contains(RegistrationRoles.FINANCE_USER));
        assertEquals(TestUtil.TETHERED_USER_GUID3, currentBalance.get(2).getTetheredGuid());
        assertEquals(SCHEME_CUSTOMER_ID3, currentBalance.get(2).getSchemeCustomerId());
        assertEquals(2, currentBalance.get(2).getRegistrationRoles().size());
        assertTrue(currentBalance.get(2).getRegistrationRoles().contains(RegistrationRoles.FINANCE_USER));
        assertTrue(currentBalance.get(2).getRegistrationRoles().contains(RegistrationRoles.CARD_HOLDER));
    }

}

