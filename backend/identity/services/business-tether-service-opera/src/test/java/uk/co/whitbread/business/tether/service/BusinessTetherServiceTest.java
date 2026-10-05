package uk.co.whitbread.business.tether.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.business.tether.converter.WorldlineBusinessTetherTransformer;
import uk.co.whitbread.business.tether.exception.BusinessTetherException;
import uk.co.whitbread.business.tether.exception.ErrorCodes;
import uk.co.whitbread.business.tether.exception.InValidTokenException;
import uk.co.whitbread.business.tether.model.PibaTetheredGuidRequest;
import uk.co.whitbread.business.tether.model.Scheme;
import uk.co.whitbread.business.tether.model.TetherLinkRequest;
import uk.co.whitbread.business.tether.utils.WorldlineUtils;
import uk.co.whitbread.business.tether.validation.WorldLineTetherResponseValidator;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.piba.api.security.WorldLineWebServiceMessageCallback;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import worldline.mst.bsm.api.b2b.pi.data.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BusinessTetherServiceTest {
    private static final String BUSINESS_ACCOUNT_SERVICE_URL = "business tether service url";
    private static final String TETHERED_GUID = "TetheredGuid";
    private static final String CARD_NUMBER = "3089500110017200017";
    private static final String ACCOUNT_NUMBER = "3089503200100176";
    private static final String INVALID_LINKID = "invalidLinkId";
    private static final String INVALID_LINKID_MESSAGE = "Link ID contains invalid account number or card number";

    private final TetherLinkRequest tetherLinkRequest = new TetherLinkRequest();

    @InjectMocks
    @Spy
    private BusinessTetherService sut;

    @Mock
    private WorldlineBusinessTetherTransformer mockWorldlineBusinessTetherTransformer;
    @Mock
    private WebServiceTemplate webServiceTemplate;
    @Mock
    private WorldLineProperties mockWorldlineProperties;
    @Mock
    private WorldLineTetherResponseValidator mockWorldlineTetherResponseValidator;
    @Mock
    private WorldLineWebServiceMessageCallback mockLoginWebServiceMessageCallback;
    @Mock
    private TetherByAccountNumber mockTetherByAccount;
    @Mock
    private TetherByAccountNumberResponse mockTetherByAccountResponse;
    @Mock
    private TetherByAccountNumberResponseType mockTetherByAccountNumberResponseType;
    @Mock
    private TetherDetailsType mockTetherDetailsType;
    @Mock
    private TetherByCardNumber mockTetherByCard;
    @Mock
    private TetherByCardNumberResponse mockTetherByCardResponse;
    @Mock
    private TetherByCardNumberResponseType mockTetherByCardNumberResponseType;
    @Mock
    private WorldLineProperties.Piba mockPibaProperties;
    @Mock
    private WorldLineProperties.Piba.Service mockPibaServiceProperties;
    @Mock
    private PibaGuidServiceClient mockPibaGuidServiceClient;
    @Mock
    private TokenService mockAuthTokenService;
    @Mock
    private SchemeExtractor mockSchemeExtractor;
    @Mock
    private CdhRegistrationService cdhRegistrationService;
    @Mock
    private WorldlineUtils mockWorldlineUtils;

    @BeforeEach
    void setUp() {
        when(mockWorldlineProperties.getPiba()).thenReturn(mockPibaProperties);
        when(mockPibaProperties.getService()).thenReturn(mockPibaServiceProperties);
        when(mockPibaServiceProperties.getUrl()).thenReturn(BUSINESS_ACCOUNT_SERVICE_URL);
        when(mockWorldlineUtils.serializeObject(any())).thenReturn("mockedSerializedObject");
        when(mockWorldlineBusinessTetherTransformer.toTetherByAccountRequest(tetherLinkRequest, Scheme.GB))
                .thenReturn(mockTetherByAccount);
        when(mockWorldlineBusinessTetherTransformer.toTetherByCardRequest(tetherLinkRequest, Scheme.GB))
                .thenReturn(mockTetherByCard);
    }

    @Test
    void tetherByAccountOrCard_shouldMakeRequestByAccount() {
        // Given
        tetherLinkRequest.setLinkId(ACCOUNT_NUMBER);
        tetherLinkRequest.setLinkCode("13353");
        tetherLinkRequest.setMemorableWord("Hello");
        setupWorldlineTetherByAccount();
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(any())).thenReturn(createEmployeeDetails());
        when(mockSchemeExtractor.extractScheme(any())).thenReturn(Scheme.GB);
        doNothing().when(sut).saveTetherInformation(any(),any(), any());
        // Then
        assertThat(sut.tetherByAccountOrCardRequest(tetherLinkRequest,"Bearer Token")).isEqualTo(TETHERED_GUID);
        verify(cdhRegistrationService, never()).registerTetheredGuids(any(), any(), any(), any());
    }

    @Test
    void tetherByAccountOrCard_shouldSaveTetherInformationWithGBScheme() {
        // Given
        tetherLinkRequest.setLinkId(ACCOUNT_NUMBER);
        tetherLinkRequest.setLinkCode("13353");
        tetherLinkRequest.setMemorableWord("Hello");
        setupWorldlineTetherByAccount();
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(any())).thenReturn(createEmployeeDetails());
        when(mockSchemeExtractor.extractScheme(any())).thenReturn(Scheme.GB);
        doNothing().when(sut).saveTetherInformation(any(),any(), any());
        sut.tetherByAccountOrCardRequest(tetherLinkRequest,"Bearer Token");
        // Then
        verify(sut, times(1)).saveTetherInformation(any(), any(), eq(Scheme.GB));
    }

    @Test
    void saveTetherInformation_shouldMapToPibaTetheredGuidRequestAndSavePibaGuid() {
        // Given
        EmployeeDetails employeeDetails = createEmployeeDetails();
        when(mockWorldlineBusinessTetherTransformer.toPibaTetheredGuidRequest(any(), any(), any()))
                .thenReturn(createPibaTetheredGuidRequest(employeeDetails));
        // When
        sut.saveTetherInformation(TETHERED_GUID,employeeDetails, Scheme.GB);
        // Then
        verify(mockWorldlineBusinessTetherTransformer, times(1))
                .toPibaTetheredGuidRequest(eq(TETHERED_GUID), eq(employeeDetails), eq(Scheme.GB));
        verify(mockPibaGuidServiceClient, times(1))
                .savePibaGuid(createPibaTetheredGuidRequest(employeeDetails));
    }

    @Test
     void tetherByAccountOrCard_shouldHandleInvalidLinkId() {
        //Given
        tetherLinkRequest.setLinkId(INVALID_LINKID);
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(any())).thenReturn(createEmployeeDetails());
        //Then
        assertThatThrownBy(() -> sut.tetherByAccountOrCardRequest(tetherLinkRequest,"Bearer Token"))
                .isInstanceOf(BusinessTetherException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCodes.INVALID_ACCOUNT_OR_CARD_VAL.getCode())
                .hasMessageContaining(INVALID_LINKID_MESSAGE);
    }

    @Test
    void tetherByAccountOrCard_shouldHandleErrorFromWorldline() {
        //Given
        tetherLinkRequest.setLinkId(ACCOUNT_NUMBER);
        when(mockWorldlineBusinessTetherTransformer.toTetherByAccountRequest(any(), any()))
                .thenReturn(mockTetherByAccount);
        when(webServiceTemplate.marshalSendAndReceive(BUSINESS_ACCOUNT_SERVICE_URL,
                mockTetherByAccount,
                mockLoginWebServiceMessageCallback))
                .thenReturn(mockTetherByAccountResponse);
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(any())).thenReturn(createEmployeeDetails());
        // When
        doThrow(new BusinessTetherException("Error")).when(mockWorldlineTetherResponseValidator).validate(mockTetherByAccountResponse);
        //Then
        assertThatThrownBy(() -> sut.tetherByAccountOrCardRequest(tetherLinkRequest,"Bearer Token"))
                .isInstanceOf(BusinessTetherException.class)
                .hasMessageContaining("Error");
    }
    
    @Test
    void tetherByAccountOrCard_shouldHandleErrorFromAuthService() {
        //Given
        tetherLinkRequest.setLinkId(ACCOUNT_NUMBER);
        // When
        doThrow(new InValidTokenException("Error")).when(sut).getEmployeeDetails(any());
        //Then
        assertThatThrownBy(() -> sut.tetherByAccountOrCardRequest(tetherLinkRequest,"Bearer Token"))
                .isInstanceOf(InValidTokenException.class)
                .hasMessageContaining("Error");
    }

    @Test
    void tetherByAccountOrCard_shouldMakeRequestByCard() {
        // Given
        tetherLinkRequest.setLinkId(CARD_NUMBER);
        setupWorldlineTetherByCard();
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(any())).thenReturn(createEmployeeDetails());
        when(mockSchemeExtractor.extractScheme(any())).thenReturn(Scheme.GB);
        doNothing().when(sut).saveTetherInformation(any(),any(), any());
        // Then
        assertThat(sut.tetherByAccountOrCardRequest(tetherLinkRequest,"Bearer Token")).isEqualTo(TETHERED_GUID);
        verify(cdhRegistrationService, never()).registerTetheredGuids(any(), any(), any(), any());
    }

    @Test
    void tetherByAccountOrCard_shouldMakeRequestByAccountAndSaveInCdh() {
        // Given
        tetherLinkRequest.setLinkId(ACCOUNT_NUMBER);
        tetherLinkRequest.setLinkCode("13353");
        tetherLinkRequest.setMemorableWord("Hello");
        tetherLinkRequest.setSaveInCdh(true);
        setupWorldlineTetherByAccount();

        var cdhDetails = createCdhEmployeeDetails();
        var details = createEmployeeDetails();
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(any())).thenReturn(details);
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(any())).thenReturn(cdhDetails);
        when(mockSchemeExtractor.extractScheme(any())).thenReturn(Scheme.GB);
        doNothing().when(cdhRegistrationService).registerTetheredGuids(any(), any(), any(), any());

        //
        sut.tetherByAccountOrCardRequest(tetherLinkRequest, "Bearer Token");

        // Then
        verify(cdhRegistrationService, times(1)).registerTetheredGuids(TETHERED_GUID, details, Scheme.GB, cdhDetails.getUserEmail());
    }

    @Test
    void tetherByAccountOrCard_shouldMakeRequestByCardAndSaveInCdh() {
        // Given
        tetherLinkRequest.setLinkId(CARD_NUMBER);
        tetherLinkRequest.setSaveInCdh(true);
        setupWorldlineTetherByCard();

        var cdhDetails = createCdhEmployeeDetails();
        var details = createEmployeeDetails();
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(any())).thenReturn(details);
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(any())).thenReturn(cdhDetails);
        when(mockSchemeExtractor.extractScheme(any())).thenReturn(Scheme.GB);
        doNothing().when(cdhRegistrationService).registerTetheredGuids(any(), any(), any(), any());

        //
        sut.tetherByAccountOrCardRequest(tetherLinkRequest, "Bearer Token");

        // Then
        verify(cdhRegistrationService, times(1)).registerTetheredGuids(TETHERED_GUID, details, Scheme.GB, cdhDetails.getUserEmail());
    }

    private void setupWorldlineTetherByAccount() {
        when(webServiceTemplate.marshalSendAndReceive(BUSINESS_ACCOUNT_SERVICE_URL,
            mockTetherByAccount,
            mockLoginWebServiceMessageCallback))
            .thenReturn(mockTetherByAccountResponse);
        doNothing().when(mockWorldlineTetherResponseValidator).validate(mockTetherByAccountResponse);
        when(mockTetherByAccountResponse.getResponse()).thenReturn(mockTetherByAccountNumberResponseType);
        when(mockTetherByAccountNumberResponseType.getTetherDetails()).thenReturn(mockTetherDetailsType);
        when(mockTetherDetailsType.getTetheredUserGuid()).thenReturn(TETHERED_GUID);
    }

    private void setupWorldlineTetherByCard() {
        when(webServiceTemplate.marshalSendAndReceive(BUSINESS_ACCOUNT_SERVICE_URL,
            mockTetherByCard,
            mockLoginWebServiceMessageCallback))
            .thenReturn(mockTetherByCardResponse);
        doNothing().when(mockWorldlineTetherResponseValidator).validate(mockTetherByCardResponse);
        when(mockTetherByCardResponse.getResponse()).thenReturn(mockTetherByCardNumberResponseType);
        when(mockTetherByCardNumberResponseType.getTetherDetails()).thenReturn(mockTetherDetailsType);
        when(mockTetherDetailsType.getTetheredUserGuid()).thenReturn(TETHERED_GUID);
    }

    private EmployeeDetails createEmployeeDetails(){
        return new EmployeeDetails("50","1");
    }

    private CdhEmployeeDetails createCdhEmployeeDetails(){
        return CdhEmployeeDetails.builder().companyAccountId("50").employeeAccountId("1").userEmail("email").build();
    }

    private PibaTetheredGuidRequest createPibaTetheredGuidRequest(EmployeeDetails employeeDetails) {
        return new PibaTetheredGuidRequest(Integer.parseInt(employeeDetails.getCompanyId()),
                Integer.parseInt(employeeDetails.getEmployeeId()), BusinessTetherServiceTest.TETHERED_GUID, Scheme.GB);
    }
}
