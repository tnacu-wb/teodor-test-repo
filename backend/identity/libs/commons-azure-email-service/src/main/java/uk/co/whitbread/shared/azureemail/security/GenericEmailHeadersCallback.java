package uk.co.whitbread.shared.azureemail.security;

import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;
import org.springframework.xml.transform.StringSource;
import uk.co.whitbread.shared.azureemail.properties.GenericEmailProperties;

@Component
@RequiredArgsConstructor
public class GenericEmailHeadersCallback {

  private static final String SOAP_ACTION_KEY = "SOAPAction";
  private final GenericEmailProperties azureGenericEmailProperties;

  public WebServiceMessageCallback addHeaders(String soapAction) {
    return webServiceMessage -> {
      addHttpHeaders(soapAction);
      addSoapHeaders(webServiceMessage);
    };
  }

  private void addHttpHeaders(String soapAction) throws IOException {
    final TransportContext context = TransportContextHolder.getTransportContext();
    final HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
    connection.addRequestHeader(azureGenericEmailProperties.getSubscriptionKeyHeaderName(),
        azureGenericEmailProperties.getSubscriptionKey());
    connection.addRequestHeader(SOAP_ACTION_KEY, soapAction);
  }

  private void addSoapHeaders(WebServiceMessage webServiceMessage)
      throws TransformerException {
    SoapMessage soapMessage = (SoapMessage) webServiceMessage;
    SoapHeader header = soapMessage.getSoapHeader();
    String soapHeaders = String.format(
        "<Security xmlns=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">\n"
            + "            <UsernameToken>\n"
            + "                <Username>%s</Username>\n"
            + "                <Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">%s</Password>\n"
            + "            </UsernameToken>\n"
            + "        </Security>",
        azureGenericEmailProperties.getUsername(),
        azureGenericEmailProperties.getPassword());
    StringSource securityHeader = new StringSource(soapHeaders);
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
    Transformer transformer = transformerFactory.newTransformer();
    transformer.transform(securityHeader, header.getResult());
  }
}
