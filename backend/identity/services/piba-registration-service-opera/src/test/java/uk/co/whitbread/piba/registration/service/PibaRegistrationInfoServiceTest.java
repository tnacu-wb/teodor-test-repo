package uk.co.whitbread.piba.registration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.piba.api.security.WorldLineWebServiceMessageCallback;
import uk.co.whitbread.piba.registration.converter.WorldlineRegistrationTransformer;
import uk.co.whitbread.piba.registration.exception.PibaRegistrationException;
import uk.co.whitbread.piba.registration.model.RegistrationInfoResponse;
import uk.co.whitbread.piba.registration.util.TestUtil;
import uk.co.whitbread.piba.registration.util.WorldlineUtils;
import uk.co.whitbread.piba.registration.validation.WorldLineRegistrationResponseValidator;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationGetInfo;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationGetInfoResponse;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static uk.co.whitbread.piba.registration.util.TestUtil.*;

@ExtendWith(MockitoExtension.class)
class PibaRegistrationInfoServiceTest {

    @InjectMocks
    private PibaRegistrationService objectUnderTest;
    @Mock
    private WebServiceTemplate mockWorldlineWebServiceTemplate;

    @Mock
    private WorldlineRegistrationTransformer mockWorldlineRegistrationTransformer;

    @Mock
    private WorldLineProperties mockWorldLineProperties;

    @Mock
    private WorldLineRegistrationResponseValidator mockWorldLineRegistrationResponseValidator;

    @Mock
    private WorldLineWebServiceMessageCallback mockWorldLineWebServiceMessageCallback;

    @Mock
    private RegistrationGetInfoResponse mockRegistrationGetInfoResponse;

    @Mock
    private RegistrationGetInfo mockRegistrationGetInfo;

    private final TestUtil testUtil = new TestUtil();

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
    void getInfoReturnsSuccessfulResponse(){
        RegistrationInfoResponse registrationInfoResponse = new RegistrationInfoResponse();

        registrationInfoResponse.setRegistrationCodeInfo( testUtil.buildRegistrationCodeInfo());

        when(mockWorldlineRegistrationTransformer.toRegistrationGetInfoSoapRequest(REGISTRATION_CODE)).thenReturn(mockRegistrationGetInfo);
        when(mockWorldlineRegistrationTransformer.toRegistrationInfoResponse(mockRegistrationGetInfoResponse)).thenReturn(registrationInfoResponse);
        doNothing().when(mockWorldLineRegistrationResponseValidator).validate(mockRegistrationGetInfoResponse);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
                mockWorldLineProperties.getPiba().getService().getUrl(),
                mockWorldlineRegistrationTransformer.toRegistrationGetInfoSoapRequest(REGISTRATION_CODE),
                mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockRegistrationGetInfoResponse);

        RegistrationInfoResponse registrationInfoResponseActual=objectUnderTest.getInfo(REGISTRATION_CODE);

        assertEquals(PRIMARY_SCHEME_CUSTOMER_ID,registrationInfoResponseActual.getRegistrationCodeInfo().getPrimarySchemeCustomerId());
        assertEquals(REGISTRATION_ROLE,registrationInfoResponseActual.getRegistrationCodeInfo().getRegistrationRole());

    }

    @Test
    void getInfoReturnsErrorResponse(){

        when(mockWorldlineRegistrationTransformer.toRegistrationGetInfoSoapRequest(REGISTRATION_CODE)).thenReturn(mockRegistrationGetInfo);

        doThrow(new PibaRegistrationException("Error")).when(mockWorldLineRegistrationResponseValidator).validate(mockRegistrationGetInfoResponse);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
                mockWorldLineProperties.getPiba().getService().getUrl(),
                mockWorldlineRegistrationTransformer.toRegistrationGetInfoSoapRequest(REGISTRATION_CODE),
                mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockRegistrationGetInfoResponse);

        //Then
        assertThatThrownBy(() -> objectUnderTest.getInfo(REGISTRATION_CODE))
                .isInstanceOf(PibaRegistrationException.class)
                .hasMessageContaining("Error");
    }

}
