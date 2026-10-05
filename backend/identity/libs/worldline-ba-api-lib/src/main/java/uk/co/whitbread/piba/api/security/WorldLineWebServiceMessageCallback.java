package uk.co.whitbread.piba.api.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.xml.transform.StringResult;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

@Slf4j
@Component
public class WorldLineWebServiceMessageCallback implements WebServiceMessageCallback {
    @Autowired
    private WorldLineProperties worldLineProperties;

    @Override
    public void doWithMessage(WebServiceMessage message) throws TransformerException {
        String soapAction;
        try {

            Node trustedPartnerCredentialsNode ;
            NodeList nodeList;
            Node headerNode;

            DOMSource domSource = (DOMSource)message.getPayloadSource();

            if (domSource == null) {
                log.error("WorldLineWebServiceMessageCallback - DOMSource is null");
                return;
            }

            soapAction = domSource.getNode().getNamespaceURI()+"/IB2BPIAPI/"+domSource.getNode().getLocalName();
            ((SoapMessage) message).setSoapAction(soapAction);

            nodeList = domSource.getNode().getChildNodes();

            Node requestNode = findSoapNode(nodeList, "Request");

            if (requestNode == null) {
                log.error("WorldLineWebServiceMessageCallback - Request element is missing");
                return;
            }
            trustedPartnerCredentialsNode = findSoapNode(requestNode.getChildNodes(), "TrustedPartnerCredentials");
            headerNode = findSoapNode(requestNode.getChildNodes(), "Header");

            if (headerNode == null) {
                log.error("WorldLineWebServiceMessageCallback - header is missing");
                return;
            }

            if (trustedPartnerCredentialsNode == null) {
                log.error("WorldLineWebServiceMessageCallback - TrustedPartnerCredentials element is missing");
                return;
            }
            Node cultureCodeNode = findSoapNode(headerNode.getChildNodes(), "CultureCode");
            Node passwordNode = findSoapNode(trustedPartnerCredentialsNode.getChildNodes(), "Password");


            if (passwordNode != null) {
                String worldLinePasswordProperty = getWorldLinePasswordProperty(cultureCodeNode);
                CDATASection cdata = passwordNode.getOwnerDocument().createCDATASection(worldLinePasswordProperty);
                passwordNode.appendChild(cdata);
            }

            Transformer transformer = createTransformer();
            StringResult result = new StringResult();
            transformer.transform(domSource, result);

        } catch (TransformerException e) {
            log.error("Error adding TrustedPartnerCredentials password to Worldline SOAP call.", e);
            throw e;
        }
    }

    private String getWorldLinePasswordProperty(Node cultureCodeNode) {
        String worldLinePasswordProperty = worldLineProperties.getPiba().getPassword();
        if (cultureCodeNode != null) {
            String cultureCode = cultureCodeNode.getTextContent();
            if (cultureCode.contains("DE"))
                worldLinePasswordProperty = worldLineProperties.getDe().getPassword();
            else if (cultureCode.contains("GB"))
                worldLinePasswordProperty = worldLineProperties.getGb().getPassword();
        }
        return worldLinePasswordProperty;
    }


    protected Transformer createTransformer() throws TransformerConfigurationException {
        return TransformerFactory.newInstance().newTransformer();
    }

    protected Node findSoapNode(NodeList nodeList, String matchingName) {
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (matchingName != null && nodeList.item(i).getLocalName().toLowerCase()
                        .compareTo(matchingName.toLowerCase()) == 0) {
                    return nodeList.item(i);
                }
            }
        }
        return null;
    }

}

