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
import uk.co.whitbread.piba.registration.model.RegistrationAuthenticationRequest;
import uk.co.whitbread.piba.registration.model.RegistrationAuthenticationResponse;
import uk.co.whitbread.piba.registration.util.TestUtil;
import uk.co.whitbread.piba.registration.util.WorldlineUtils;
import uk.co.whitbread.piba.registration.validation.WorldLineRegistrationResponseValidator;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationAuthenticate;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationAuthenticateResponse;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static uk.co.whitbread.piba.registration.util.TestUtil.PRIMARY_SCHEME_CUSTOMER_ID;
import static uk.co.whitbread.piba.registration.util.TestUtil.REGISTRATION_ROLE;

@ExtendWith(MockitoExtension.class)
class PibaRegistrationAuthenticateServiceTest {

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
    private RegistrationAuthenticateResponse mockRegistrationAuthenticateResponse;


    @Mock
    private RegistrationAuthenticate mockRegistrationAuthenticate;


    @Mock
    private RegistrationAuthenticationRequest mockRegistrationAuthenticationRequest;
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
   void authenticateReturnsSuccessfulResponse(){
        RegistrationAuthenticationResponse registrationAuthenticationResponse = new RegistrationAuthenticationResponse();

        registrationAuthenticationResponse.setRegistrationCodeInfo( testUtil.buildRegistrationCodeInfo());
        registrationAuthenticationResponse.setRegistrationPrePopulatedItems(testUtil.buildPrePopulatedItems());

        when(mockWorldlineRegistrationTransformer.toRegistrationAuthenticateSoapRequest(mockRegistrationAuthenticationRequest)).thenReturn(mockRegistrationAuthenticate);
        when(mockWorldlineRegistrationTransformer.toRegistrationAuthResponse(mockRegistrationAuthenticateResponse)).thenReturn(registrationAuthenticationResponse);
        doNothing().when(mockWorldLineRegistrationResponseValidator).validate(mockRegistrationAuthenticateResponse);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
                mockWorldLineProperties.getPiba().getService().getUrl(),
                mockWorldlineRegistrationTransformer.toRegistrationAuthenticateSoapRequest(mockRegistrationAuthenticationRequest),
                mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockRegistrationAuthenticateResponse);

        RegistrationAuthenticationResponse registrationInfoResponseActual=objectUnderTest.authenticate(mockRegistrationAuthenticationRequest);

        assertEquals(PRIMARY_SCHEME_CUSTOMER_ID,registrationInfoResponseActual.getRegistrationCodeInfo().getPrimarySchemeCustomerId());
        assertEquals(REGISTRATION_ROLE,registrationInfoResponseActual.getRegistrationCodeInfo().getRegistrationRole());
        assertEquals("Forename",registrationInfoResponseActual.getRegistrationPrePopulatedItems().getForename());
        assertEquals("01457891111",registrationInfoResponseActual.getRegistrationPrePopulatedItems().getLandlineNumber());

    }

    @Test
   void authenticateReturnsErrorResponse(){


        when(mockWorldlineRegistrationTransformer.toRegistrationAuthenticateSoapRequest(mockRegistrationAuthenticationRequest)).thenReturn(mockRegistrationAuthenticate);

        doThrow(new PibaRegistrationException("Error")).when(mockWorldLineRegistrationResponseValidator).validate(mockRegistrationAuthenticateResponse);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
                mockWorldLineProperties.getPiba().getService().getUrl(),
                mockWorldlineRegistrationTransformer.toRegistrationAuthenticateSoapRequest(mockRegistrationAuthenticationRequest),
                mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockRegistrationAuthenticateResponse);

        //Then
        assertThatThrownBy(() -> objectUnderTest.authenticate(mockRegistrationAuthenticationRequest))
                .isInstanceOf(PibaRegistrationException.class)
                .hasMessageContaining("Error");
    }

}
