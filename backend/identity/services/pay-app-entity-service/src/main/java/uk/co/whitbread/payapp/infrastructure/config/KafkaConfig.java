package uk.co.whitbread.payapp.infrastructure.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import uk.co.whitbread.payapp.infrastructure.queue.model.ShareAppEmailNotificationEvent;

@Configuration
public class KafkaConfig {

  private final String bootstrapServers;

  public KafkaConfig(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
    this.bootstrapServers = bootstrapServers;
  }

  private ProducerFactory<String, ShareAppEmailNotificationEvent> emailNotificationsProducerFactory() {
    Map<String, Object> configProps = getDefaultConfigsProps();
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean(name = "notifications")
  public KafkaTemplate<String, ShareAppEmailNotificationEvent> emailNotificationsKafkaTemplate(
      @Value("${email.topics.notifications}") String notificationsTopic) {
    var kafkaTemplate = new KafkaTemplate<>(emailNotificationsProducerFactory());
    kafkaTemplate.setObservationEnabled(true);
    kafkaTemplate.setDefaultTopic(notificationsTopic);
    return kafkaTemplate;
  }

  private Map<String, Object> getDefaultConfigsProps() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        bootstrapServers);
    configProps.put(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class);
    configProps.put(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        JacksonJsonSerializer.class);
    configProps.put(ProducerConfig.ACKS_CONFIG, "all");
    return configProps;
  }
}
