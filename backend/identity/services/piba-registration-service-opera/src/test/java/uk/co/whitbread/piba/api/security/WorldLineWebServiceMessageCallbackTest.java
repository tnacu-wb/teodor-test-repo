package uk.co.whitbread.piba.api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.xml.transform.StringResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WorldLineWebServiceMessageCallbackTest {
    private static final String REQUEST_TYPE = "worldLineRequest";
    private static final String REQUEST_NODE_NAME = "Request";
    private static final String CRED_NODE_NAME = "TrustedPartnerCredentials";
    private static final String PASSWORD_NODE_NAME = "Password";
    private static final String PASSWORD = "password";
    private static final String HEADER_NODE_NAME = "Header";


    @Mock(extraInterfaces = SoapMessage.class)
    private WebServiceMessage webServiceMessage;
    @Mock
    private DOMSource mockDomSource;
    @Mock
    private Node mockNode;
    @Mock
    private Node mockRequestNode;
    @Mock
    private Node mockTrustedPartnerCredentialsNode;
    @Mock
    private Node mockPasswordNode;
    @Mock
    private Node mockHeaderNode;
    @Mock
    private NodeList mockNodeList;
    @Mock
    private Document mockDocument;
    @Mock
    private WorldLineProperties mockWorldLineProperties;
    @Mock
    private Transformer mockTransformer;

    @InjectMocks
    @Spy
    private WorldLineWebServiceMessageCallback objectUnderTests;

    @Mock
    private WorldLineProperties.Piba mockPibaProperties;

    @BeforeEach
    public void setUp()  {
        when(mockWorldLineProperties.getPiba()).thenReturn(mockPibaProperties);
        when(mockPibaProperties.getPassword()).thenReturn(PASSWORD);
        when(((DOMSource)webServiceMessage.getPayloadSource())).thenReturn(mockDomSource);

        Mockito.doReturn(mockRequestNode).when(objectUnderTests).findSoapNode(mockNodeList, REQUEST_NODE_NAME);

        when(mockNodeList.item(0)).thenReturn(mockNode);
        when(mockNode.getLocalName()).thenReturn(REQUEST_NODE_NAME);

        when(mockRequestNode.getChildNodes()).thenReturn(mockNodeList);
        Mockito.doReturn(mockTrustedPartnerCredentialsNode).when(objectUnderTests).findSoapNode(mockNodeList, CRED_NODE_NAME);
        Mockito.doReturn(mockHeaderNode).when(objectUnderTests).findSoapNode(mockNodeList, HEADER_NODE_NAME);

        when(mockTrustedPartnerCredentialsNode.getChildNodes()).thenReturn(mockNodeList);
        Mockito.doReturn(mockPasswordNode).when(objectUnderTests).findSoapNode(mockNodeList, PASSWORD_NODE_NAME);

        when(mockPasswordNode.getOwnerDocument()).thenReturn(mockDocument);
    }

    @Test
    void testDoWithMessageSuccess() throws Exception {

        when(mockDomSource.getNode()).thenReturn(mockNode);
        when(mockNode.getLocalName()).thenReturn(REQUEST_TYPE);
        when(mockNode.getChildNodes()).thenReturn(mockNodeList);

        Mockito.doReturn(mockTransformer).when(objectUnderTests).createTransformer();

        objectUnderTests.doWithMessage(webServiceMessage);


        verify(mockTransformer).transform(any(DOMSource.class), any(StringResult.class));
    }

    @Test
    void testDoWithMessageShouldHandleTransformException() throws TransformerException {

        when(mockDomSource.getNode()).thenReturn(mockNode);
        when(mockNode.getLocalName()).thenReturn(REQUEST_TYPE);
        when(mockNode.getChildNodes()).thenReturn(mockNodeList);

        Mockito.doReturn(mockTransformer).when(objectUnderTests).createTransformer();
        doThrow(new TransformerException("Error")).when(mockTransformer).transform(any(DOMSource.class), any(StringResult.class));

        assertThatThrownBy(() -> objectUnderTests.doWithMessage(webServiceMessage))
                .isInstanceOf(TransformerException.class);
    }

    @Test
    void testFindNode() {
        Mockito.doCallRealMethod().when(objectUnderTests).findSoapNode(mockNodeList, REQUEST_NODE_NAME);
        when(mockNodeList.item(0)).thenReturn(mockNode);
        when(mockNodeList.getLength()).thenReturn(1);
        when(mockNode.getLocalName()).thenReturn(REQUEST_NODE_NAME);
        objectUnderTests.findSoapNode(mockNodeList, REQUEST_NODE_NAME);
        verify(mockNodeList, atLeastOnce()).item(0);
    }

    @Test
    void testNullDomSource() throws TransformerException {

        when(((DOMSource)webServiceMessage.getPayloadSource())).thenReturn(null);
        objectUnderTests.doWithMessage(webServiceMessage);
        verify(mockDomSource, never()).getNode();
    }


    @Test
    void testNullRequestNode() throws TransformerException {

        when(mockDomSource.getNode()).thenReturn(mockNode);
        when(mockNode.getLocalName()).thenReturn(REQUEST_TYPE);
        when(mockNode.getChildNodes()).thenReturn(null);
        objectUnderTests.doWithMessage(webServiceMessage);
        verify(mockRequestNode, never()).getChildNodes();
    }

    @Test
    void testNullTrustedPartnerCredentialsNode() throws TransformerException {

        when(mockDomSource.getNode()).thenReturn(mockNode);
        when(mockNode.getLocalName()).thenReturn(REQUEST_TYPE);
        when(mockNode.getChildNodes()).thenReturn(mockNodeList);
        when(mockRequestNode.getChildNodes()).thenReturn(null);
        objectUnderTests.doWithMessage(webServiceMessage);
        verify(mockTrustedPartnerCredentialsNode, never()).getChildNodes();
    }

    @Test
    void testNullPasswordNode() throws TransformerException {
        when(mockDomSource.getNode()).thenReturn(mockNode);
        when(mockNode.getLocalName()).thenReturn(REQUEST_TYPE);
        when(mockNode.getChildNodes()).thenReturn(mockNodeList);

        when(mockTrustedPartnerCredentialsNode.getChildNodes()).thenReturn(null);

        objectUnderTests.doWithMessage(webServiceMessage);
        verify(mockPasswordNode, never()).getOwnerDocument();
    }
}
