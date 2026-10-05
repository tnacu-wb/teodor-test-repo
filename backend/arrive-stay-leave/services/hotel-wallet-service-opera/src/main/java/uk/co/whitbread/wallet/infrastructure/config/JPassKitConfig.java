package uk.co.whitbread.wallet.infrastructure.config;

import de.brendamour.jpasskit.signing.IPKSigningUtil;
import de.brendamour.jpasskit.signing.PKInMemorySigningUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JPassKitConfig {
  @Bean
  public IPKSigningUtil getIpkSigningUtil() {
    return new PKInMemorySigningUtil();
  }
}
