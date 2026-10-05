package uk.co.whitbread.availabilitycacheservice.infrastructure.config;

import jakarta.annotation.PostConstruct;
import java.security.Security;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class NetworkSecurityManager {

  private static final String NW_CACHE_TTL_PROP_NM = "networkaddress.cache.ttl";
  private static final String NW_CACHE_NEG_TTL_PROP_NM = "networkaddress.cache.negative.ttl";

  // CHECKSTYLE:OFF
  @Value("${" + NW_CACHE_TTL_PROP_NM + ":60}")
  @SuppressWarnings("all")
  private String NW_CACHE_TTL_PROP_VAL;

  @Value("${" + NW_CACHE_NEG_TTL_PROP_NM + ":60}")
  @SuppressWarnings("all")
  private String NW_CACHE_NEG_TTL_PROP_VAL;
  // CHECKSTYLE:ON

  @PostConstruct
  public void init() {
    Security.setProperty(NW_CACHE_TTL_PROP_NM, NW_CACHE_TTL_PROP_VAL);
    Security.setProperty(NW_CACHE_NEG_TTL_PROP_NM, NW_CACHE_NEG_TTL_PROP_VAL);
    log.info("Network DNS Cache TTL : {}", Security.getProperty(NW_CACHE_TTL_PROP_NM));
    log.info("Network DNS Cache Negative TTL : {}", Security.getProperty(NW_CACHE_NEG_TTL_PROP_NM));
  }
}
