package uk.co.whitbread.basket.processor.infrastructure.queue.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "basket.kafka")
public class KafkaProperties {

  private String bootstrapServers;

  private String groupId;

  private Integer requestTimeout;

  private Integer heartBeatInterval;

  private Integer maxPollInterval;

  private Integer maxPollRecords;

  private Integer sessionTimeout;

}
