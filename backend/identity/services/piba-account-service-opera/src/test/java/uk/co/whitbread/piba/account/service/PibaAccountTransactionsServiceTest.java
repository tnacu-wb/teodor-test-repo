package uk.co.whitbread.piba.account.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.piba.account.converter.WorldlineAccountTransformer;
import uk.co.whitbread.piba.account.model.CustomerAccountCardTransaction;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsResponse;
import uk.co.whitbread.piba.account.model.PibaTetheredGuidResponse;
import uk.co.whitbread.piba.account.model.TransactionsFileResponse;
import uk.co.whitbread.piba.account.model.enums.Scheme;
import uk.co.whitbread.piba.account.properties.CdhProperties;
import uk.co.whitbread.piba.account.util.TestUtil;
import uk.co.whitbread.piba.account.util.TransactionFileWriter;
import uk.co.whitbread.piba.account.util.WorldlineUtils;
import uk.co.whitbread.piba.account.validation.WorldLineAccountResponseValidator;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.piba.api.security.WorldLineWebServiceMessageCallback;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewTransactions;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewTransactionsResponse;

@ExtendWith(MockitoExtension.class)
class PibaAccountTransactionsServiceTest {
    @InjectMocks
    private PibaAccountService objectUnderTest;

    @Mock
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
    private CustomerAccountViewTransactions mockCustomerAccountViewTransactions;

    @Mock
    private CustomerAccountViewTransactionsResponse mockCustomerAccountViewTransactionsResponse;

    @Mock
    private CustomerAccountTransactionsRequest mockCustomerAccountTransactionsRequest;
    private final TestUtil testUtil = new TestUtil();

    @Mock
    private WorldLineProperties.Piba mockPibaProperties;

    @Mock
    private WorldLineProperties.Piba.Service mockPibaServiceProperties;

    @Mock
    private TransactionFileWriter transactionFileWriter;

    @Mock
    private TokenService mockAuthTokenService;

    @Mock
    private CdhRegistrationService mockCdhRegistrationService;

    @Mock
    private CdhProperties mockCdhProperties;
    @Mock
    private WorldLineService worldLineService;
    @Mock
    private PibaGuidServiceClient mockPibaGuidServiceClient;
    @Mock
    private InvoicesService invoicesService;
    @Mock
    private WorldlineUtils worldlineUtils;

    @BeforeEach
    void setup(){
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
            transactionFileWriter,
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
    void getTransactionsReturnsSuccessfulResponse() {

        CustomerAccountTransactionsResponse customerAccountTransactionsResponse = testUtil.populateViewTransactionsResponse(60);


        when(mockWorldlineAccountTransformer.toCustomerAccountTransactionsRequest(mockCustomerAccountTransactionsRequest)).thenReturn(mockCustomerAccountViewTransactions);
        when(mockWorldlineAccountTransformer.toCustomerAccountTransactionsResponse(mockCustomerAccountViewTransactionsResponse)).thenReturn(customerAccountTransactionsResponse);
        doNothing().when(mockWorldLineAccountResponseValidator).validate(mockCustomerAccountViewTransactionsResponse);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
                mockWorldLineProperties.getPiba().getService().getUrl(),
                mockWorldlineAccountTransformer.toCustomerAccountTransactionsRequest(mockCustomerAccountTransactionsRequest),
                mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockCustomerAccountViewTransactionsResponse);

        CustomerAccountTransactionsResponse customerAccountTransactionsResponseActual=objectUnderTest.viewTransactions(mockCustomerAccountTransactionsRequest);

        assertEquals(1,customerAccountTransactionsResponseActual.getResponse().getTransactions().size());
        assertEquals("customerRef",customerAccountTransactionsResponseActual.getResponse().getTransactions().get(0).getCustomerOwnRef());

    }

    @Test
    void downloadTransactionsReturnsSuccessfulResponseWithTwoPage() {
        CustomerAccountTransactionsResponse customerAccountTransactionsResponse1 = testUtil.populateViewTransactionsResponse(2);

        when(mockWorldlineAccountTransformer.toCustomerAccountTransactionsRequest(mockCustomerAccountTransactionsRequest)).thenReturn(mockCustomerAccountViewTransactions);
        when(mockWorldlineAccountTransformer.toCustomerAccountTransactionsResponse(mockCustomerAccountViewTransactionsResponse)).thenReturn(customerAccountTransactionsResponse1);
         doNothing().when(mockWorldLineAccountResponseValidator).validate(mockCustomerAccountViewTransactionsResponse);

        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
                mockWorldLineProperties.getPiba().getService().getUrl(),
                mockWorldlineAccountTransformer.toCustomerAccountTransactionsRequest(mockCustomerAccountTransactionsRequest),
                mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockCustomerAccountViewTransactionsResponse);

        List<CustomerAccountCardTransaction> customerAccountTransactionsResponseActual = objectUnderTest.retrieveTransactions(mockCustomerAccountTransactionsRequest);

        assertEquals(2,customerAccountTransactionsResponseActual.size());
    }

    @Test
    void downloadTransactionsReturnsSuccessfulResponseWithTenPage() {
        CustomerAccountTransactionsResponse customerAccountTransactionsResponse1 = testUtil.populateViewTransactionsResponse(10);

        when(mockWorldlineAccountTransformer.toCustomerAccountTransactionsRequest(mockCustomerAccountTransactionsRequest)).thenReturn(mockCustomerAccountViewTransactions);
        when(mockWorldlineAccountTransformer.toCustomerAccountTransactionsResponse(mockCustomerAccountViewTransactionsResponse)).thenReturn(customerAccountTransactionsResponse1);
        doNothing().when(mockWorldLineAccountResponseValidator).validate(mockCustomerAccountViewTransactionsResponse);

        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
                mockWorldLineProperties.getPiba().getService().getUrl(),
                mockWorldlineAccountTransformer.toCustomerAccountTransactionsRequest(mockCustomerAccountTransactionsRequest),
                mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockCustomerAccountViewTransactionsResponse);

        List<CustomerAccountCardTransaction> customerAccountTransactionsResponseActual = objectUnderTest.retrieveTransactions(mockCustomerAccountTransactionsRequest);

        assertEquals(10,customerAccountTransactionsResponseActual.size());
    }

    @Test
    void downloadTransactionsForInvoice_handlesPaginationAsExpected() {
        int schemeCustomerId = 123;
        String tetheredUserGuid = "test-guid";
        int invoiceNumber = 456;
        String token = "Bearer token";

        // Given
        when(mockCdhProperties.isEnableBbDataFetch()).thenReturn(false);
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(token))
            .thenReturn(new EmployeeDetails("1", "2"));
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(token))
            .thenReturn(mock(CdhEmployeeDetails.class));
        when(mockCdhRegistrationService.getTetheredGuids(any(), any(), any()))
            .thenReturn(List.of(new PibaTetheredGuidResponse("1", "2", List.of(tetheredUserGuid), null)));
        LocalDate startDate = LocalDate.of(2025, 12, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 14);
        when(invoicesService.getDateRangeForInvoice(any(), any(), any(), any()))
            .thenReturn(Pair.of(startDate, endDate));
        CustomerAccountTransactionsResponse mockResponse = testUtil.populateViewTransactionsResponse(2);

        PibaAccountService spyObjectUnderTest = spy(objectUnderTest);

        ArgumentCaptor<CustomerAccountTransactionsRequest> requestCaptor = ArgumentCaptor.forClass(CustomerAccountTransactionsRequest.class);
        doAnswer(invocation -> {
            invocation.getArgument(0);
            return mockResponse; // Return the mock response for each request
        }).when(spyObjectUnderTest).viewTransactions(requestCaptor.capture());

        doNothing().when(transactionFileWriter).populateExcel(any(), any(), any(), any());

        // When
        TransactionsFileResponse result =
            spyObjectUnderTest.downloadTransactionsForInvoice(schemeCustomerId, tetheredUserGuid, invoiceNumber, token, "1.1.1.1", Scheme.GB);

        // Then
        verify(spyObjectUnderTest, times(2)).viewTransactions(any());

        // Assert captured requests
        List<CustomerAccountTransactionsRequest> capturedRequests = requestCaptor.getAllValues();
        assertEquals(2, capturedRequests.size());

        // Assert fields on each captured request
        assertEquals(1, capturedRequests.get(0).getPagingRequest().getPage());
        assertEquals(30, capturedRequests.get(0).getPagingRequest().getMaximumDisplayRows());
        assertEquals(invoiceNumber, capturedRequests.get(0).getSearchCriteria().getInvoiceNumberSearch().getInvoiceNumber());

        assertEquals(2, capturedRequests.get(1).getPagingRequest().getPage());
        assertEquals(30, capturedRequests.get(1).getPagingRequest().getMaximumDisplayRows());
        assertEquals(invoiceNumber, capturedRequests.get(1).getSearchCriteria().getInvoiceNumberSearch().getInvoiceNumber());

        // Assert result
        assertNotNull(result);
        ArgumentCaptor<List<CustomerAccountCardTransaction>> captor = ArgumentCaptor.forClass(List.class);
        verify(transactionFileWriter).populateExcel(any(), captor.capture(), eq(startDate), eq(endDate));
        verify(invoicesService).getDateRangeForInvoice(Optional.of(mockResponse.getResponse().getTransactions().get(0).getInvoiceDate()),
            "1.1.1.1", Scheme.GB, tetheredUserGuid);
        assertEquals(2, captor.getValue().size());
        assertEquals("Invoice_456_20211201.xls", result.fileName());
    }

    @Test
    void downloadTransactionsForAccountV2_ReturnsExpectedFileResponse() {
        int schemeCustomerId = 123;
        String tetheredUserGuid = "test-guid";
        String token = "Bearer token";

        // Given
        when(mockCdhProperties.isEnableBbDataFetch()).thenReturn(false);
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(token))
            .thenReturn(new EmployeeDetails("1", "2"));
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(token))
            .thenReturn(mock(CdhEmployeeDetails.class));
        when(mockCdhRegistrationService.getTetheredGuids(any(), any(), any()))
            .thenReturn(
                List.of(new PibaTetheredGuidResponse("1", "2", List.of(tetheredUserGuid), null)));

        CustomerAccountTransactionsResponse customerAccountTransactionsResponse1 = testUtil.populateViewTransactionsResponse(
            2);

        when(
            mockWorldlineAccountTransformer.toCustomerAccountTransactionsRequest(any())).thenReturn(
            mockCustomerAccountViewTransactions);
        when(mockWorldlineAccountTransformer.toCustomerAccountTransactionsResponse(
            any())).thenReturn(customerAccountTransactionsResponse1);
        doNothing().when(mockWorldLineAccountResponseValidator)
            .validate((CustomerAccountViewTransactionsResponse) any());

        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
            mockWorldLineProperties.getPiba().getService().getUrl(),
            mockWorldlineAccountTransformer.toCustomerAccountTransactionsRequest(
                mockCustomerAccountTransactionsRequest),
            mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockCustomerAccountViewTransactionsResponse);

        // When
        TransactionsFileResponse result = objectUnderTest.downloadTransactionsForAccountV2(
            schemeCustomerId, tetheredUserGuid, Scheme.GB, token);

        // Then
        assertNotNull(result);
        assertNotNull(result.fileName());
    }
}
