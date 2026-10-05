package uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.service.properties;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "comment")
@Slf4j
public class CommentTypeProperties {

  Map<String, String> type;

  @PostConstruct
  void init() {
    log.info("The Types are :: {}", type);
  }

}
