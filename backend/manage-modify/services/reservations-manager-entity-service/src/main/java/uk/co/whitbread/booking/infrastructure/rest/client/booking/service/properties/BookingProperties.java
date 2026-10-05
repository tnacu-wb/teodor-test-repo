package uk.co.whitbread.booking.infrastructure.rest.client.booking.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.accounts")
public class BookingProperties {

  private String url;
  private String singleBookingEndpoint;
  private String cancelBookingEndpoint;
  private String sendConfirmationEmailEndpoint;
  private String sendInvoiceEmailEndpoint;
}
