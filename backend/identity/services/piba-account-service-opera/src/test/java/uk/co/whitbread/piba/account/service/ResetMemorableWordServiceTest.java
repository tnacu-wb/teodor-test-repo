package uk.co.whitbread.piba.account.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.piba.account.converter.WorldlineAccountTransformer;
import uk.co.whitbread.piba.account.model.ResetMemorableWordRequest;
import uk.co.whitbread.piba.account.model.ResetMemorableWordResponse;
import uk.co.whitbread.piba.account.model.TetheredLoginResponse;
import uk.co.whitbread.piba.account.model.UpdateMemorableWordRequest;
import uk.co.whitbread.piba.account.model.enums.Scheme;
import uk.co.whitbread.piba.account.util.WorldlineUtils;
import uk.co.whitbread.piba.account.validation.WorldLineAccountResponseValidator;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.piba.api.security.WorldLineWebServiceMessageCallback;
import worldline.mst.bsm.api.b2b.pi.data.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.piba.account.util.TestUtil.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ResetMemorableWordServiceTest {
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
    private UpdateMemorableWordRequest mockUpdateMemorableWordRequest;

    @Mock
    private UpdateMemorableWord mockUpdateMemorableWord;

    @Mock
    private UpdateMemorableWordResponse mockUpdateMemorableWordResponse;
    
    @Mock
    private LoginTetheredUser mockLoginTetheredUser;
    
    @Mock
    private LoginTetheredUserResponse mockLoginTetheredUserResponse;
    
    @Mock
    private LoginTetheredUserResponseType mockLoginTetheredUserResponseType;
    
    @Mock
    private NewSessionType mockNewSessionType;
    
    @Mock
    private WorldLineProperties.Piba mockPibaProperties;
    @Mock
    private WorldLineProperties.Piba.Service mockPibaServiceProperties;

    @Mock
    private WorldlineUtils worldlineUtils;

    @BeforeEach
    public void setup(){
        when(mockWorldLineProperties.getPiba()).thenReturn(mockPibaProperties);
        when(mockPibaProperties.getService()).thenReturn(mockPibaServiceProperties);
        when(mockPibaServiceProperties.getUrl()).thenReturn("url");
        when(worldlineUtils.serializeObject(any())).thenReturn("mockedSerializedObject");
    }
    
    
    @Test
    void postResetMemorableWordReturnsSuccessfulResponse() {
        
        ResetMemorableWordResponse resetMemorableWordResponse = new ResetMemorableWordResponse("3462");
       
        when(mockLoginTetheredUserResponse.getResponse()).thenReturn(mockLoginTetheredUserResponseType);
        when(mockLoginTetheredUserResponseType.getNewSession()).thenReturn(mockNewSessionType);
        when(mockLoginTetheredUserResponseType.getNewSession()).thenReturn(mockNewSessionType);
        when(mockNewSessionType.getSessionId()).thenReturn(SESSION_ID);
        when(mockNewSessionType.getSharedSecret()).thenReturn(SHARED_SECRET_ID);
        when(mockWorldlineAccountTransformer.toLoginTetheredUserRequest(any(), any()))
            .thenReturn(mockLoginTetheredUser);
        
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
                mockWorldLineProperties.getPiba().getService().getUrl(),
                mockWorldlineAccountTransformer.toLoginTetheredUserRequest(TETHERED_USER_GUID, Scheme.GB),
                mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockLoginTetheredUserResponse);
    
        TetheredLoginResponse loginResponse = objectUnderTest.worldLineLoginTetheredUser(TETHERED_USER_GUID, Scheme.GB);
    
        assertEquals(SESSION_ID, loginResponse.getSessionId());
        assertEquals(SHARED_SECRET_ID, loginResponse.getSharedSecret());
    
        when(mockWorldlineAccountTransformer.toUpdateMemorableWordRequest(mockUpdateMemorableWordRequest)).thenReturn(
                mockUpdateMemorableWord);
        
        when(mockWorldlineAccountTransformer.toResetMemorableWordResponse(mockUpdateMemorableWordResponse))
                .thenReturn(resetMemorableWordResponse);
        
        doNothing().when(mockWorldLineAccountResponseValidator).validate(mockUpdateMemorableWordResponse);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
                mockWorldLineProperties.getPiba().getService().getUrl(),
                mockWorldlineAccountTransformer.toUpdateMemorableWordRequest(mockUpdateMemorableWordRequest),
                mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockUpdateMemorableWordResponse);

        ResetMemorableWordResponse response = objectUnderTest.resetMemorableWord(mockUpdateMemorableWordRequest);
        
        assertEquals("3462", response.getResultCode());
        
    }
    
    
    @Test
    void tetherLoginsSuccessfulResponse() {
        ResetMemorableWordRequest request = new ResetMemorableWordRequest();
        request.setTetheredUserGuid(TETHERED_USER_GUID);
        
        when(mockLoginTetheredUserResponseType.getNewSession()).thenReturn(mockNewSessionType);
        when(mockNewSessionType.getSessionId()).thenReturn(SESSION_ID);
        when(mockNewSessionType.getSharedSecret()).thenReturn(SHARED_SECRET_ID);
        when(mockWorldlineAccountTransformer.toLoginTetheredUserRequest(any(), any()))
            .thenReturn(mockLoginTetheredUser);
        
        when(mockWorldlineWebServiceTemplate
                .marshalSendAndReceive(mockWorldLineProperties.getPiba().getService().getUrl(),
                        mockWorldlineAccountTransformer.toLoginTetheredUserRequest("guid", Scheme.GB),
                        mockWorldLineWebServiceMessageCallback)).thenReturn(mockLoginTetheredUserResponse);
        when(mockLoginTetheredUserResponse.getResponse()).thenReturn(mockLoginTetheredUserResponseType);
        doNothing().when(mockWorldLineAccountResponseValidator).validate(mockLoginTetheredUserResponse);

        TetheredLoginResponse response = objectUnderTest.worldLineLoginTetheredUser("guid", Scheme.GB);
        
        assertThat(response.getSessionId()).isEqualTo(SESSION_ID);
        assertThat(response.getSharedSecret()).isEqualTo(SHARED_SECRET_ID);
    }

    @Test
    void postResetMemorableWordReturnsSuccessfulResponseDE() {

        ResetMemorableWordResponse resetMemorableWordResponse = new ResetMemorableWordResponse("1322");

        when(mockLoginTetheredUserResponse.getResponse()).thenReturn(mockLoginTetheredUserResponseType);
        when(mockLoginTetheredUserResponseType.getNewSession()).thenReturn(mockNewSessionType);
        when(mockLoginTetheredUserResponseType.getNewSession()).thenReturn(mockNewSessionType);
        when(mockNewSessionType.getSessionId()).thenReturn(SESSION_ID);
        when(mockNewSessionType.getSharedSecret()).thenReturn(SHARED_SECRET_ID);
        when(mockWorldlineAccountTransformer.toLoginTetheredUserRequest(any(), any()))
            .thenReturn(mockLoginTetheredUser);

        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
                mockWorldLineProperties.getPiba().getService().getUrl(),
                mockWorldlineAccountTransformer.toLoginTetheredUserRequest(TETHERED_USER_GUID, Scheme.DE),
                mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockLoginTetheredUserResponse);

        TetheredLoginResponse loginResponse = objectUnderTest.worldLineLoginTetheredUser(TETHERED_USER_GUID, Scheme.DE);

        assertEquals(SESSION_ID, loginResponse.getSessionId());
        assertEquals(SHARED_SECRET_ID, loginResponse.getSharedSecret());

        when(mockWorldlineAccountTransformer.toUpdateMemorableWordRequest(mockUpdateMemorableWordRequest)).thenReturn(
                mockUpdateMemorableWord);

        when(mockWorldlineAccountTransformer.toResetMemorableWordResponse(mockUpdateMemorableWordResponse))
                .thenReturn(resetMemorableWordResponse);

        doNothing().when(mockWorldLineAccountResponseValidator).validate(mockUpdateMemorableWordResponse);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
                mockWorldLineProperties.getPiba().getService().getUrl(),
                mockWorldlineAccountTransformer.toUpdateMemorableWordRequest(mockUpdateMemorableWordRequest),
                mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockUpdateMemorableWordResponse);

        ResetMemorableWordResponse response = objectUnderTest.resetMemorableWord(mockUpdateMemorableWordRequest);

        assertEquals("1322", response.getResultCode());

    }

}
