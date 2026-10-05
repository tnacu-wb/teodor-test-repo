package uk.co.whitbread.address.lookup.infrastructure.rest.client.address;

import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPMessage;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.config.QasProperties;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoginWebServiceMessageCallback implements WebServiceMessageCallback {

  private static final String AUTH_TOKEN_HEADER_NAME = "auth-token";
  private final QasProperties qasProperties;

  @Override
  public void doWithMessage(WebServiceMessage message) {

    if (message instanceof SaajSoapMessage soapMessage) {

      try {

        final Optional<MimeHeaders> mimeHeaders = Optional.ofNullable(soapMessage.getSaajMessage())
            .map(SOAPMessage::getMimeHeaders);

        if (mimeHeaders.isPresent()) {
          mimeHeaders.get().setHeader(AUTH_TOKEN_HEADER_NAME, qasProperties.getAuthToken());

        } else {
          log.error("Unable to retrieve mime headers");
        }

      } catch (Exception ex) {
        log.error("Unable to create SOAP headers", ex);
      }
    }
  }
}
