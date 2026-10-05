package uk.co.whitbread.business.tether.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.business.tether.converter.WorldlineBusinessTetherTransformer;
import uk.co.whitbread.business.tether.exception.BusinessTetherException;
import uk.co.whitbread.business.tether.model.LoginCriteria;
import uk.co.whitbread.business.tether.model.Scheme;
import uk.co.whitbread.business.tether.model.TetheredLoginResponse;
import uk.co.whitbread.business.tether.utils.WorldlineUtils;
import uk.co.whitbread.business.tether.validation.WorldLineTetherResponseValidator;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.piba.api.security.WorldLineWebServiceMessageCallback;
import worldline.mst.bsm.api.b2b.pi.data.LoginTetheredUser;
import worldline.mst.bsm.api.b2b.pi.data.LoginTetheredUserResponse;
import worldline.mst.bsm.api.b2b.pi.data.LoginTetheredUserResponseType;
import worldline.mst.bsm.api.b2b.pi.data.NewSessionType;
import worldline.mst.bsm.api.b2b.pi.data.RefreshSession;
import worldline.mst.bsm.api.b2b.pi.data.RefreshSessionResponse;

@ExtendWith(MockitoExtension.class)
public class BusinessTetherLoginServiceTest {
    private static final String WORLDLINE_SERVICE_URL = "business tether login service url";
    private static final String WORLD_LINE_SESSION_ID = "worldlineSessionId";
    private static final String WORLD_SHARED_SECRET_ID = "worldLineSharedSecret";
    private static final String TETHERED_GUID = "TetheredGuid";
    private static final String CULTURE_CODE = "cultureCode";
    private static final String CLIENT_MESSAGE_ID = "clientMessageId";

    @InjectMocks
    @Spy
    private BusinessTetherLoginService sut;

    @Mock
    private WebServiceTemplate worldlineWebServiceTemplate;
    @Mock
    private WorldLineProperties worldLineProperties;
    @Mock
    private LoginTetheredUser mockLoginTetheredUser;
    @Mock
    private LoginTetheredUserResponse mockLoginTetheredUserResponse;
    @Mock
    private LoginTetheredUserResponseType mockLoginTetheredUserResponseType;
    @Mock
    private RefreshSessionResponse mockRefreshSessionResponse;
    @Mock
    private NewSessionType mockNewSessionType;
    @Mock
    private WorldlineBusinessTetherTransformer mockWorldlineTetherLoginTransformer;
    @Mock
    private WorldLineTetherResponseValidator mockWorldLineTetherResponseValidator;
    @Mock
    private WorldLineWebServiceMessageCallback mockWorldLineWebServiceMessageCallback;
    @Mock
    private WorldLineProperties.Piba mockPibaProperties;
    @Mock
    private WorldLineProperties.Piba.Service mockPibaServiceProperties;
    @Mock
    private WorldLineProperties.Gb mockWorldLineGBProperties;
    @Mock
    private WorldLineProperties.De mockWorldLineDEProperties;
    @Mock
    private WorldlineUtils mockWorldlineUtils;

    private LoginCriteria loginCriteria;

    @BeforeEach
    public void setUp() {
        lenient().when(worldLineProperties.getPiba()).thenReturn(mockPibaProperties);
        lenient().when(mockPibaProperties.getService()).thenReturn(mockPibaServiceProperties);
        lenient().when(mockPibaServiceProperties.getUrl()).thenReturn(WORLDLINE_SERVICE_URL);
        lenient().when(mockWorldlineUtils.serializeObject(any())).thenReturn("mockedSerializedObject");
        //when(worldLineProperties.getClientMessageId()).thenReturn(CLIENT_MESSAGE_ID);
        //when(worldLineProperties.getCultureCode()).thenReturn(CULTURE_CODE);
    }

    @Test
    public void login_shouldMakeRequestWithGUID() {
        //Given
        loginCriteria = new LoginCriteria();
        loginCriteria.setGuid(TETHERED_GUID);

        when(mockLoginTetheredUserResponseType.getNewSession()).thenReturn(mockNewSessionType);
        when(mockNewSessionType.getSessionId()).thenReturn(WORLD_LINE_SESSION_ID);
        when(mockNewSessionType.getSharedSecret()).thenReturn(WORLD_SHARED_SECRET_ID);

        when(mockWorldlineTetherLoginTransformer.toLoginTetheredUserRequest(any(), any()))
                .thenReturn(mockLoginTetheredUser);

        when(worldlineWebServiceTemplate.marshalSendAndReceive(WORLDLINE_SERVICE_URL,
                mockLoginTetheredUser,
                mockWorldLineWebServiceMessageCallback))
                .thenReturn(mockLoginTetheredUserResponse);

        // When
        when(mockLoginTetheredUserResponse.getResponse()).thenReturn(mockLoginTetheredUserResponseType);
        when(mockWorldLineTetherResponseValidator.validate(mockLoginTetheredUserResponse)).thenReturn(Optional.empty());
        TetheredLoginResponse response = sut.login(loginCriteria);

        //Then
        assertThat(response.getSessionId()).isEqualTo(WORLD_LINE_SESSION_ID);
        assertThat(response.getSharedSecret()).isEqualTo(WORLD_SHARED_SECRET_ID);
    }

    @Test
    public void login_shouldMakeRequestWithSharedSecret() {
        //Given
        loginCriteria = new LoginCriteria();
        loginCriteria.setWorldlineSessionId(WORLD_LINE_SESSION_ID);
        loginCriteria.setWorldlineSharedSecret(WORLD_SHARED_SECRET_ID);
        loginCriteria.setScheme(Scheme.GB.name());
        when(worldLineProperties.getGb()).thenReturn(mockWorldLineGBProperties);

        when(worldlineWebServiceTemplate.marshalSendAndReceive(any(String.class),
                any(RefreshSession.class),
                any(WorldLineWebServiceMessageCallback.class)))
                .thenReturn(mockRefreshSessionResponse);

        // When
        when(mockWorldLineTetherResponseValidator.validate(mockRefreshSessionResponse)).thenReturn(Optional.empty());
        TetheredLoginResponse response = sut.login(loginCriteria);

        //Then
        assertThat(response.getSessionId()).isEqualTo(WORLD_LINE_SESSION_ID);
        assertThat(response.getSharedSecret()).isEqualTo(WORLD_SHARED_SECRET_ID);
    }


    @Test
    public void login_shouldHandleErrorFromWorldline() {
        //Given
        loginCriteria = new LoginCriteria();
        loginCriteria.setGuid(TETHERED_GUID);

        when(mockWorldlineTetherLoginTransformer.toLoginTetheredUserRequest(any(), any()))
                .thenReturn(mockLoginTetheredUser);

        when(worldlineWebServiceTemplate.marshalSendAndReceive(WORLDLINE_SERVICE_URL,
                mockLoginTetheredUser,
                mockWorldLineWebServiceMessageCallback))
                .thenReturn(mockLoginTetheredUserResponse);

        // When
        when(mockLoginTetheredUserResponse.getResponse()).thenReturn(mockLoginTetheredUserResponseType);
        when(mockWorldLineTetherResponseValidator.validate(mockLoginTetheredUserResponse)).thenReturn(Optional.of("Error"));

        //Then
        assertThatThrownBy(() -> sut.login(loginCriteria))
                .isInstanceOf(BusinessTetherException.class)
                .hasMessageContaining("Error");
    }
}
