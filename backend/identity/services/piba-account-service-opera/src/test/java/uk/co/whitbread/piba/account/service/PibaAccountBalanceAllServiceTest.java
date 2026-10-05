package uk.co.whitbread.piba.account.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.piba.account.converter.WorldlineAccountTransformer;
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
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.piba.account.util.TestUtil.*;

@ExtendWith(MockitoExtension.class)
class PibaAccountBalanceAllServiceTest {
    private static final String COMPANY_ID = "55";
    private static final String EMPLOYEE_ID = "22";

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
    private TransactionFileWriter mockTransactionFileWriter;
    @Mock
    private PibaGuidServiceClient mockPibaGuidServiceClient;
    @Mock
    private TokenService mockAuthTokenService;
    @Mock
    private PibaTetheredGuidResponse mockPibaTetheredGuidResponse;
    @Mock
    private CdhProperties mockCdhProperties;
    @Mock
    private WorldLineService worldLineService;
    @Mock
    private CdhRegistrationService mockCdhRegistrationService;
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
     void viewCurrentBalanceReturnsSuccessfulResponse() {
        viewCurrentBalanceMocks();
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken("Bearer random Token")).thenReturn(createEmployeeDetails());
        CustomerAccountCurrentBalancesResponse response = objectUnderTest.viewCurrentBalance("Bearer random Token", true);
        viewCurrentBalanceAsserts(response);
    }

    private EmployeeDetails createEmployeeDetails(){
        return new EmployeeDetails(COMPANY_ID, EMPLOYEE_ID);
    }
    private void viewCurrentBalanceMocks() {
        List<PibaTetheredGuidResponse> pibaGuidsResponse = testUtil.createPibaTetheredGuidResponseMultiple(COMPANY_ID, EMPLOYEE_ID);
        CustomerAccountCurrentBalances customerAccountCurrentBalancesResponse = testUtil.populateCurrentBalancesResponse();
        CustomerAccountCurrentBalances customerAccountCurrentBalancesResponse2 = testUtil.populateCurrentBalancesResponse();
        CustomerAccountCurrentBalances customerAccountCurrentBalancesResponse3 = testUtil.populateCurrentBalancesResponse();
        when(mockPibaGuidServiceClient.getMultiGuids(COMPANY_ID, EMPLOYEE_ID)).thenReturn(pibaGuidsResponse);
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
        when(worldLineService.getUserDetails("327f7a0c-9a33-41c2-808d-74f15f24797c",Scheme.GB)).thenReturn(registeredUserResponse1);
        when(worldLineService.getUserDetails("a8c30dab-ba44-4b5b-ada6-b241ef65f8ac",Scheme.GB)).thenReturn(registeredUserResponse2);
        when(worldLineService.getUserDetails("a8c30dab-ba44-4b5b-ada6-b241ef65f8fc",Scheme.DE)).thenReturn(registeredUserResponse3);
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


}

