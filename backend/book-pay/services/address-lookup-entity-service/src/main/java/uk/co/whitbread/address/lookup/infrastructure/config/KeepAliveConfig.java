package uk.co.whitbread.address.lookup.infrastructure.config;

import static uk.co.whitbread.address.lookup.infrastructure.config.AddressLookupConstants.KEEP_ALIVE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.message.BasicHeaderElementIterator;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.config.QasProperties;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class KeepAliveConfig {

  private final QasProperties qasProperties;

  @Bean
  public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
    return (response, context) -> {
      BasicHeaderElementIterator it = new BasicHeaderElementIterator(
          response.headerIterator(KEEP_ALIVE));
      while (it.hasNext()) {
        HeaderElement he = it.next();
        String param = he.getName();
        String value = he.getValue();
        if (value != null && param.equalsIgnoreCase("timeout")) {
          try {
            return TimeValue.ofMilliseconds(Long.parseLong(value) * 1000);
          } catch (NumberFormatException exception) {
            log.error("timeout param is not a number [{}]", value, exception);
          }
        }
      }
      // If there is no Keep-Alive header. Keep the connection for aemConfiguration.getDefaultKeepAlive() seconds
      return TimeValue.ofMilliseconds(qasProperties.getDefaultKeepAlive() * 1000L);
    };
  }

}
