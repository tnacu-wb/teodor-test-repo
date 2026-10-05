package uk.co.whitbread.piba.account.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.piba.account.converter.WorldlineAccountTransformer;
import uk.co.whitbread.piba.account.model.CreditProposeLimitRequest;
import uk.co.whitbread.piba.account.model.CreditProposeLimitResponse;
import uk.co.whitbread.piba.account.util.WorldlineUtils;
import uk.co.whitbread.piba.account.validation.WorldLineAccountResponseValidator;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.piba.api.security.WorldLineWebServiceMessageCallback;
import worldline.mst.bsm.api.b2b.pi.data.CreditProposeNewLimit;
import worldline.mst.bsm.api.b2b.pi.data.CreditProposeNewLimitResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProposeNewCreditLimitServiceTest {
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
    private CreditProposeLimitRequest mockCreditProposeLimitRequest;

    @Mock
    private CreditProposeNewLimit mockCreditProposeNewLimit;

    @Mock
    private  CreditProposeNewLimitResponse mockCreditProposeNewLimitResponse;

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
    }

    @Test
    void postProposeNewCreditLimitReturnsSuccessfulResponse() {

        CreditProposeLimitResponse creditProposeLimitResponse = new CreditProposeLimitResponse("888991ff-d0ee-435d-ac78-57e91556deca");

        when(mockWorldlineAccountTransformer.toCreditProposeNewLimitRequest(mockCreditProposeLimitRequest)).thenReturn(
                mockCreditProposeNewLimit);
        when(mockWorldlineAccountTransformer.toCreditProposeNewLimitResponse(mockCreditProposeNewLimitResponse)).thenReturn(creditProposeLimitResponse);

        doNothing().when(mockWorldLineAccountResponseValidator).validate(mockCreditProposeNewLimitResponse);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
                mockWorldLineProperties.getPiba().getService().getUrl(),
                mockWorldlineAccountTransformer.toCreditProposeNewLimitRequest(mockCreditProposeLimitRequest),
                mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockCreditProposeNewLimitResponse);
        when(worldlineUtils.serializeObject(any())).thenReturn("mockedSerializedObject");

        CreditProposeLimitResponse response  = objectUnderTest.proposeNewCreditLimit(mockCreditProposeLimitRequest);

        assertEquals("888991ff-d0ee-435d-ac78-57e91556deca", response.getRequestId());

    }

}
