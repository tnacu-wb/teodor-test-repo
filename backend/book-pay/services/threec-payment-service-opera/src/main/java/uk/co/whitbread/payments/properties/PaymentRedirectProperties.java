package uk.co.whitbread.payments.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "gpay.redirect")
@Configuration
@Data
public class PaymentRedirectProperties {

  String success;
  String failure;
}
