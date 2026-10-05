package uk.co.whitbread.payment.orchestrator.infrastructure.kafka;

import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentAuthorisedEvent;

/**
 * Kafka producer configuration for the {@code payment-authorised} topic.
 *
 * <p>Provides an explicitly typed {@link KafkaTemplate} for
 * {@link PaymentAuthorisedEvent} messages, using {@link StringSerializer}
 * for keys and {@link JacksonJsonSerializer} for values.
 *
 * <p>The producer factory builds its properties from Boot's {@link KafkaProperties}, so every
 * {@code spring.kafka.*} setting a deployment provides — security protocol, SASL/SSL
 * configuration, acks, compression — reaches the client rather than being silently dropped by
 * a hand-picked property map. The serializers are the only opinion imposed here.
 */
@Configuration
public class KafkaProducerConfig {

  @Bean
  public ProducerFactory<String, PaymentAuthorisedEvent> paymentEventProducerFactory(
      KafkaProperties kafkaProperties) {
    Map<String, Object> props = kafkaProperties.buildProducerProperties();
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, PaymentAuthorisedEvent> paymentEventKafkaTemplate(
      ProducerFactory<String, PaymentAuthorisedEvent> paymentEventProducerFactory) {
    return new KafkaTemplate<>(paymentEventProducerFactory);
  }
}
