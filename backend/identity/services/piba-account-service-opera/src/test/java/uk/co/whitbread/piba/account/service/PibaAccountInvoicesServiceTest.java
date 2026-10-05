package uk.co.whitbread.piba.account.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.piba.account.util.TestUtil.SCHEME_CUSTOMER_ID;
import static uk.co.whitbread.piba.account.util.TestUtil.TETHERED_USER_GUID;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.piba.account.converter.WorldlineAccountTransformer;
import uk.co.whitbread.piba.account.exception.UnknownAccountException;
import uk.co.whitbread.piba.account.model.CustomerAccountCurrentBalances;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceListDownloadResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceResponse;
import uk.co.whitbread.piba.account.model.PibaTetheredGuidResponse;
import uk.co.whitbread.piba.account.model.TetheredUserDetailsResponse;
import uk.co.whitbread.piba.account.model.enums.Scheme;
import uk.co.whitbread.piba.account.properties.CdhProperties;
import uk.co.whitbread.piba.account.util.TestUtil;
import uk.co.whitbread.piba.account.util.WorldlineUtils;
import uk.co.whitbread.piba.account.validation.WorldLineAccountResponseValidator;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.piba.api.security.WorldLineWebServiceMessageCallback;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceDownload;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceDownloadResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceList;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceListResponse;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGetResponse;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PibaAccountInvoicesServiceTest {

    private final TestUtil testUtil = new TestUtil();
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
    private CustomerAccountInvoiceList mockCustomerAccountInvoiceList;
    @Mock
    private CustomerAccountInvoiceListResponse mockCustomerAccountInvoiceListResponse;
    @Mock
    private CustomerAccountInvoiceDownload mockCustomerAccountInvoiceDownload;
    @Mock
    private CustomerAccountInvoiceRequest mockCustomerAccountInvoiceRequest;
    @Mock
    private CustomerAccountInvoiceDownloadResponse mockCustomerAccountInvoiceDownloadResponse;
    @Mock
    private WorldLineProperties.Piba mockPibaProperties;
    @Mock
    private WorldLineProperties.Piba.Service mockPibaServiceProperties;
    @Mock
    private CdhRegistrationService cdhRegistrationService;
    @Mock
    private CdhProperties cdhProperties;
    @Mock
    private TokenService authTokenService;
    @Mock
    private WorldLineService worldLineService;
    @Mock
    private WorldlineUtils worldlineUtils;

    @BeforeEach
    public void setup(){
        when(mockWorldLineProperties.getPiba()).thenReturn(mockPibaProperties);
        when(mockPibaProperties.getService()).thenReturn(mockPibaServiceProperties);
        when(mockPibaServiceProperties.getUrl()).thenReturn("url");
    }

    @Test
    void getInvoicesReturnsSuccessfulResponse() {
        CustomerAccountInvoiceResponse customerAccountInvoiceResponse = testUtil.createCustomerAccountInvoiceResponse(10);

        when(mockWorldlineAccountTransformer.toCustomerAccountInvoiceRequest(mockCustomerAccountInvoiceRequest)).thenReturn(mockCustomerAccountInvoiceList);
        when(mockWorldlineAccountTransformer.toCustomerAccountInvoiceResponse(mockCustomerAccountInvoiceListResponse)).thenReturn(customerAccountInvoiceResponse);
        doNothing().when(mockWorldLineAccountResponseValidator).validate(mockCustomerAccountInvoiceListResponse);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
                mockWorldLineProperties.getPiba().getService().getUrl(),
                mockWorldlineAccountTransformer.toCustomerAccountInvoiceRequest(mockCustomerAccountInvoiceRequest),
                mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockCustomerAccountInvoiceListResponse);
        when(worldlineUtils.serializeObject(any())).thenReturn("mockedSerializedObject");

        CustomerAccountInvoiceResponse response  = objectUnderTest.viewInvoices(mockCustomerAccountInvoiceRequest);

        assertEquals(10, response.getResponse().getInvoices().size());
        assertEquals("1230", response.getResponse().getInvoices().get(0).getInvoiceNo());
    }

    @Test
    void getInvoicesV2ReturnsSuccessfulResponse() {
        CustomerAccountInvoiceResponse customerAccountInvoiceResponse = testUtil.createCustomerAccountInvoiceResponse(10);
        String authorization = "Bearer token";
        String tetheredGuid = "327f7a0c-9a33-41c2-808d-74f15f24797c";
        CdhEmployeeDetails cdhEmployeeDetails = Mockito.mock(CdhEmployeeDetails.class);
        when(cdhEmployeeDetails.getUserEmail()).thenReturn("user@example.com");
        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setEmployeeId("employeeId");
        employeeDetails.setCompanyId("companyId");
        PibaTetheredGuidResponse tetheredGuidResponse = new PibaTetheredGuidResponse();
        tetheredGuidResponse.setTetheredGuid(List.of(tetheredGuid));

        when(mockCustomerAccountInvoiceRequest.getTetheredUserGuid()).thenReturn(tetheredGuid);
        when(authTokenService.retrieveEmployeeDetailsAndVerifyToken(authorization))
            .thenReturn(employeeDetails);
        when(authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization))
            .thenReturn(cdhEmployeeDetails);
        when(cdhRegistrationService.getTetheredGuids(anyString(), anyString(), anyString()))
            .thenReturn(List.of(tetheredGuidResponse));
        when(cdhProperties.isEnableBbDataFetch()).thenReturn(false);
        when(mockWorldlineAccountTransformer.toCustomerAccountInvoiceRequest(mockCustomerAccountInvoiceRequest))
            .thenReturn(mockCustomerAccountInvoiceList);
        when(mockWorldlineAccountTransformer.toCustomerAccountInvoiceResponse(mockCustomerAccountInvoiceListResponse))
            .thenReturn(customerAccountInvoiceResponse);
        doNothing().when(mockWorldLineAccountResponseValidator).validate(mockCustomerAccountInvoiceListResponse);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
            mockWorldLineProperties.getPiba().getService().getUrl(),
            mockWorldlineAccountTransformer.toCustomerAccountInvoiceRequest(mockCustomerAccountInvoiceRequest),
            mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockCustomerAccountInvoiceListResponse);
        when(worldlineUtils.serializeObject(any())).thenReturn("mockedSerializedObject");

        CustomerAccountInvoiceResponse response = objectUnderTest.viewInvoicesV2(authorization, mockCustomerAccountInvoiceRequest);

        assertEquals(10, response.getResponse().getInvoices().size());
        assertEquals("1230", response.getResponse().getInvoices().get(0).getInvoiceNo());
    }

    @Test
    void getInvoicesV2ShouldFailWhenTetheredGuidDoesNotBelongToUser() {
        String authorization = "Bearer token";
        String tetheredGuid = "327f7a0c-9a33-41c2-808d-74f15f24797c";
        CdhEmployeeDetails cdhEmployeeDetails = Mockito.mock(CdhEmployeeDetails.class);
        when(cdhEmployeeDetails.getUserEmail()).thenReturn("user@example.com");
        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setEmployeeId("employeeId");
        employeeDetails.setCompanyId("companyId");
        PibaTetheredGuidResponse tetheredGuidResponse = new PibaTetheredGuidResponse();
        tetheredGuidResponse.setTetheredGuid(List.of(tetheredGuid));

        when(mockCustomerAccountInvoiceRequest.getTetheredUserGuid()).thenReturn("invalid-tethered-guid");
        when(authTokenService.retrieveEmployeeDetailsAndVerifyToken(authorization))
            .thenReturn(employeeDetails);
        when(authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization))
            .thenReturn(cdhEmployeeDetails);
        when(cdhRegistrationService.getTetheredGuids(anyString(), anyString(), anyString()))
            .thenReturn(List.of(tetheredGuidResponse));
        when(cdhProperties.isEnableBbDataFetch()).thenReturn(false);
        doNothing().when(mockWorldLineAccountResponseValidator).validate(mockCustomerAccountInvoiceListResponse);

        UnknownAccountException exception = assertThrows(
            UnknownAccountException.class,
            () -> objectUnderTest.viewInvoicesV2(authorization, mockCustomerAccountInvoiceRequest)
        );

        assertEquals("Invalid tethered guid for current user", exception.getMessage());
    }

    @Test
    void getInvoicesDownloadReturnsSuccessfulResponse() {
        CustomerAccountInvoiceListDownloadResponse downloadResponse = new CustomerAccountInvoiceListDownloadResponse();
        downloadResponse.setBinaryData(TestUtil.INVOICES_BINARY_DATA);
        downloadResponse.setFileExtension(".pdf");
        downloadResponse.setFileName("invoices.pdf");

        when(mockWorldlineAccountTransformer.toCustomerAccountInvoiceDownloadRequest(
            SCHEME_CUSTOMER_ID,
            TETHERED_USER_GUID,
                TestUtil.INVOICE_FILE_ID, Scheme.GB)).thenReturn(mockCustomerAccountInvoiceDownload);
        when(mockWorldlineAccountTransformer.toCustomerAccountInvoiceDownloadResponse(mockCustomerAccountInvoiceDownloadResponse))
                .thenReturn(downloadResponse);
        doNothing().when(mockWorldLineAccountResponseValidator).validate(mockCustomerAccountInvoiceDownloadResponse);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
                mockWorldLineProperties.getPiba().getService().getUrl(),
                mockWorldlineAccountTransformer.toCustomerAccountInvoiceDownloadRequest(
                    SCHEME_CUSTOMER_ID,
                    TETHERED_USER_GUID, TestUtil.INVOICE_FILE_ID, Scheme.GB),
                mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockCustomerAccountInvoiceDownloadResponse);
        when(worldlineUtils.serializeObject(any())).thenReturn("mockedSerializedObject");

        CustomerAccountInvoiceListDownloadResponse response  = objectUnderTest.downloadInvoices(
            SCHEME_CUSTOMER_ID,
            TETHERED_USER_GUID, TestUtil.INVOICE_FILE_ID, Scheme.GB);

        assertEquals(TestUtil.INVOICES_BINARY_DATA.length, response.getBinaryData().length);
        assertEquals(".pdf", response.getFileExtension());
        assertEquals("invoices.pdf", response.getFileName());
    }
    @Test
    void getInvoicesDownloadReturnsSuccessfulResponseForDE() {
        CustomerAccountInvoiceListDownloadResponse downloadResponse = new CustomerAccountInvoiceListDownloadResponse();
        downloadResponse.setBinaryData(TestUtil.INVOICES_BINARY_DATA);
        downloadResponse.setFileExtension(".pdf");
        downloadResponse.setFileName("invoices.pdf");

        when(mockWorldlineAccountTransformer.toCustomerAccountInvoiceDownloadRequest(
            SCHEME_CUSTOMER_ID,
            TETHERED_USER_GUID,
                TestUtil.INVOICE_FILE_ID, Scheme.DE)).thenReturn(mockCustomerAccountInvoiceDownload);
        when(mockWorldlineAccountTransformer.toCustomerAccountInvoiceDownloadResponse(mockCustomerAccountInvoiceDownloadResponse))
                .thenReturn(downloadResponse);
        doNothing().when(mockWorldLineAccountResponseValidator).validate(mockCustomerAccountInvoiceDownloadResponse);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
                mockWorldLineProperties.getPiba().getService().getUrl(),
                mockWorldlineAccountTransformer.toCustomerAccountInvoiceDownloadRequest(
                    SCHEME_CUSTOMER_ID,
                    TETHERED_USER_GUID, TestUtil.INVOICE_FILE_ID, Scheme.DE),
                mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockCustomerAccountInvoiceDownloadResponse);
        when(worldlineUtils.serializeObject(any())).thenReturn("mockedSerializedObject");

        CustomerAccountInvoiceListDownloadResponse response  = objectUnderTest.downloadInvoices(
            SCHEME_CUSTOMER_ID,
            TETHERED_USER_GUID, TestUtil.INVOICE_FILE_ID, Scheme.DE);

        assertEquals(TestUtil.INVOICES_BINARY_DATA.length, response.getBinaryData().length);
        assertEquals(".pdf", response.getFileExtension());
        assertEquals("invoices.pdf", response.getFileName());
    }

    @Test
    void getInvoicesDownloadV2ReturnsSuccessfulResponse() {
        CustomerAccountInvoiceListDownloadResponse downloadResponse = new CustomerAccountInvoiceListDownloadResponse();
        downloadResponse.setBinaryData(TestUtil.INVOICES_BINARY_DATA);
        downloadResponse.setFileExtension(".pdf");
        downloadResponse.setFileName("invoices.pdf");
        String authorization = "Bearer token";
        String tetheredGuid = "327f7a0c-9a33-41c2-808d-74f15f24797c";
        CdhEmployeeDetails cdhEmployeeDetails = Mockito.mock(CdhEmployeeDetails.class);
        when(cdhEmployeeDetails.getUserEmail()).thenReturn("user@example.com");
        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setEmployeeId("employeeId");
        employeeDetails.setCompanyId("companyId");
        PibaTetheredGuidResponse tetheredGuidResponse = new PibaTetheredGuidResponse();
        tetheredGuidResponse.setTetheredGuid(List.of(tetheredGuid));

        when(authTokenService.retrieveEmployeeDetailsAndVerifyToken(authorization))
            .thenReturn(employeeDetails);
        when(authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization))
            .thenReturn(cdhEmployeeDetails);
        when(cdhRegistrationService.getTetheredGuids(anyString(), anyString(), anyString()))
            .thenReturn(List.of(tetheredGuidResponse));
        when(cdhProperties.isEnableBbDataFetch()).thenReturn(false);
        doNothing().when(mockWorldLineAccountResponseValidator)
            .validate(mockCustomerAccountInvoiceListResponse);
        when(mockWorldlineAccountTransformer.toCustomerAccountInvoiceDownloadRequest(
            SCHEME_CUSTOMER_ID,
            TETHERED_USER_GUID,
            TestUtil.INVOICE_FILE_ID, Scheme.GB)).thenReturn(mockCustomerAccountInvoiceDownload);
        when(mockWorldlineAccountTransformer.toCustomerAccountInvoiceDownloadResponse(mockCustomerAccountInvoiceDownloadResponse))
            .thenReturn(downloadResponse);
        doNothing().when(mockWorldLineAccountResponseValidator).validate(mockCustomerAccountInvoiceDownloadResponse);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
            mockWorldLineProperties.getPiba().getService().getUrl(),
            mockWorldlineAccountTransformer.toCustomerAccountInvoiceDownloadRequest(
                SCHEME_CUSTOMER_ID,
                TETHERED_USER_GUID, TestUtil.INVOICE_FILE_ID, Scheme.GB),
            mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockCustomerAccountInvoiceDownloadResponse);
        when(worldlineUtils.serializeObject(any())).thenReturn("mockedSerializedObject");

        CustomerAccountInvoiceListDownloadResponse response  = objectUnderTest.downloadInvoicesV2(authorization, SCHEME_CUSTOMER_ID,
            TETHERED_USER_GUID, TestUtil.INVOICE_FILE_ID, Scheme.GB);

        assertEquals(TestUtil.INVOICES_BINARY_DATA.length, response.getBinaryData().length);
        assertEquals(".pdf", response.getFileExtension());
        assertEquals("invoices.pdf", response.getFileName());
    }

  @Test
  void getInvoicesDownloadV2ShouldFailWhenTetheredGuidDoesNotBelongToUser() {
    String authorization = "Bearer token";
    String tetheredGuid = "327f7a0c-9a33-41c2-808d-74f15f24797c";
    CdhEmployeeDetails cdhEmployeeDetails = Mockito.mock(CdhEmployeeDetails.class);
    when(cdhEmployeeDetails.getUserEmail()).thenReturn("user@example.com");
    EmployeeDetails employeeDetails = new EmployeeDetails();
    employeeDetails.setEmployeeId("employeeId");
    employeeDetails.setCompanyId("companyId");
    PibaTetheredGuidResponse tetheredGuidResponse = new PibaTetheredGuidResponse();
    tetheredGuidResponse.setTetheredGuid(List.of(tetheredGuid));

    when(authTokenService.retrieveEmployeeDetailsAndVerifyToken(authorization))
        .thenReturn(employeeDetails);
    when(authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization))
        .thenReturn(cdhEmployeeDetails);
    when(cdhRegistrationService.getTetheredGuids(anyString(), anyString(), anyString()))
        .thenReturn(List.of(tetheredGuidResponse));
    when(cdhProperties.isEnableBbDataFetch()).thenReturn(false);
    doNothing().when(mockWorldLineAccountResponseValidator)
        .validate(mockCustomerAccountInvoiceListResponse);

    UnknownAccountException exception = assertThrows(
        UnknownAccountException.class,
        () -> objectUnderTest.downloadInvoicesV2(authorization, SCHEME_CUSTOMER_ID, "invalid-guid", TestUtil.INVOICE_FILE_ID, Scheme.GB)
    );
    assertEquals("Invalid tethered guid for current user", exception.getMessage());
  }

    @Test
    void getBalanceSummaryV2ShouldSucceed() {
        String authorization = "Bearer token";
        String tetheredGuid = "327f7a0c-9a33-41c2-808d-74f15f24797c";
        CdhEmployeeDetails cdhEmployeeDetails = Mockito.mock(CdhEmployeeDetails.class);
        when(cdhEmployeeDetails.getUserEmail()).thenReturn("user@example.com");
        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setEmployeeId("employeeId");
        employeeDetails.setCompanyId("companyId");
        PibaTetheredGuidResponse tetheredGuidResponse = new PibaTetheredGuidResponse();
        tetheredGuidResponse.setTetheredGuid(List.of(tetheredGuid));
        TetheredUserDetailsGetResponse mockRegisteredUserDetailsResponse1 = Mockito.mock(
            TetheredUserDetailsGetResponse.class);
        CustomerAccountCurrentBalances customerAccountCurrentBalancesResponse = testUtil.populateCurrentBalancesResponse();
        TetheredUserDetailsResponse registeredUserResponse = testUtil.populateTetheredUserResponse(SCHEME_CUSTOMER_ID);

        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
            mockWorldLineProperties.getPiba().getService().getUrl(),
            mockWorldlineAccountTransformer.toTetheredUserDetailsRequest(TETHERED_USER_GUID, Scheme.GB),
            mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockRegisteredUserDetailsResponse1);
        when(mockWorldlineAccountTransformer.toCustomerAccountCurrentBalancesResponse(any())).thenReturn(
            customerAccountCurrentBalancesResponse);
        when(mockWorldlineAccountTransformer.toTetheredUserDetailsResponse(
            mockRegisteredUserDetailsResponse1)).thenReturn(registeredUserResponse);
        when(mockCustomerAccountInvoiceRequest.getTetheredUserGuid()).thenReturn(
            "invalid-tethered-guid");
        when(authTokenService.retrieveEmployeeDetailsAndVerifyToken(authorization))
            .thenReturn(employeeDetails);
        when(authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization))
            .thenReturn(cdhEmployeeDetails);
        when(cdhRegistrationService.getTetheredGuids(anyString(), anyString(), anyString()))
            .thenReturn(List.of(tetheredGuidResponse));
        when(cdhProperties.isEnableBbDataFetch()).thenReturn(false);
        doNothing().when(mockWorldLineAccountResponseValidator)
            .validate(mockCustomerAccountInvoiceListResponse);
        when(worldLineService.getUserDetails(tetheredGuid, Scheme.GB)).thenReturn(registeredUserResponse);


        var response = objectUnderTest.getCustomerAccountCurrentBalance(authorization,
            tetheredGuid, Scheme.GB);

        assertEquals(response, customerAccountCurrentBalancesResponse);
    }

    @Test
    void getBalanceSummaryV2ShouldFailWhenTetheredGuidDoesNotBelongToUser() {
        String authorization = "Bearer token";
        String tetheredGuid = "327f7a0c-9a33-41c2-808d-74f15f24797c";
        CdhEmployeeDetails cdhEmployeeDetails = Mockito.mock(CdhEmployeeDetails.class);
        when(cdhEmployeeDetails.getUserEmail()).thenReturn("user@example.com");
        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setEmployeeId("employeeId");
        employeeDetails.setCompanyId("companyId");
        PibaTetheredGuidResponse tetheredGuidResponse = new PibaTetheredGuidResponse();
        tetheredGuidResponse.setTetheredGuid(List.of(tetheredGuid));

        when(mockCustomerAccountInvoiceRequest.getTetheredUserGuid()).thenReturn(
            "invalid-tethered-guid");
        when(authTokenService.retrieveEmployeeDetailsAndVerifyToken(authorization))
            .thenReturn(employeeDetails);
        when(authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization))
            .thenReturn(cdhEmployeeDetails);
        when(cdhRegistrationService.getTetheredGuids(anyString(), anyString(), anyString()))
            .thenReturn(List.of(tetheredGuidResponse));
        when(cdhProperties.isEnableBbDataFetch()).thenReturn(false);
        doNothing().when(mockWorldLineAccountResponseValidator)
            .validate(mockCustomerAccountInvoiceListResponse);

        UnknownAccountException exception = assertThrows(
            UnknownAccountException.class,
            () -> objectUnderTest.getCustomerAccountCurrentBalance(authorization,
                "random-tethered-guid", Scheme.GB)
        );

        assertEquals("Invalid tethered guid for current user", exception.getMessage());
    }

}
