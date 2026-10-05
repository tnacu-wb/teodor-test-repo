package uk.co.whitbread.shared.azureemail.security;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;
import uk.co.whitbread.shared.azureemail.properties.PTIEmailProperties;

@Component
@RequiredArgsConstructor
public class PTIEmailHeadersCallback {

  private final PTIEmailProperties ptiEmailProperties;

  public WebServiceMessageCallback addHeaders() {
    return webServiceMessage -> addSubscriptionKeyHeader();
  }

  private void addSubscriptionKeyHeader() throws IOException {
    final TransportContext context = TransportContextHolder.getTransportContext();
    final HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
    connection.addRequestHeader(ptiEmailProperties.getSubscriptionKeyHeaderName(),
        ptiEmailProperties.getSubscriptionKey());
  }
}
