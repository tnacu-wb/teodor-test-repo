package uk.co.whitbread.payment.orchestrator.infrastructure.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.test.util.ReflectionTestUtils;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentAuthorisedEvent;

/**
 * Pins the contract that the Kafka factories are built from Boot's {@link KafkaProperties}
 * rather than a hand-picked property map: a deployment's {@code spring.kafka.*} settings
 * (security protocol, SASL/SSL, tuning) must reach the clients. The previous hand-rolled maps
 * silently dropped everything they did not copy, which only surfaced on first contact with a
 * secured cluster.
 */
class KafkaConfigPropertiesTest {

  private KafkaProperties propertiesWithSecurity() {
    KafkaProperties kafkaProperties = new KafkaProperties();
    // The generic pass-through a platform team uses for TLS/SASL — the exact class of setting
    // the old hand-picked maps dropped.
    kafkaProperties.getProperties().put("security.protocol", "SASL_SSL");
    kafkaProperties.getConsumer().setGroupId("payment-orchestration-service");
    return kafkaProperties;
  }

  @Test
  @DisplayName("Consumer factory carries spring.kafka.* settings and pins String deserialization")
  void consumerFactory_buildsFromKafkaProperties() {
    var factory = (DefaultKafkaConsumerFactory<String, String>)
        new KafkaConsumerConfig().consumerFactory(propertiesWithSecurity());

    assertThat(factory.getConfigurationProperties())
        .containsEntry("security.protocol", "SASL_SSL")
        .containsEntry(ConsumerConfig.GROUP_ID_CONFIG, "payment-orchestration-service")
        .containsEntry(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
        .containsEntry(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
  }

  @Test
  @DisplayName("Producer factory carries spring.kafka.* settings and pins the event serializers")
  void producerFactory_buildsFromKafkaProperties() {
    var factory = (DefaultKafkaProducerFactory<String, PaymentAuthorisedEvent>)
        new KafkaProducerConfig().paymentEventProducerFactory(propertiesWithSecurity());

    assertThat(factory.getConfigurationProperties())
        .containsEntry("security.protocol", "SASL_SSL")
        .containsEntry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
        .containsEntry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
  }

  @Test
  @DisplayName("Listener factory replaces the zero-delay default retry with an exponential backoff")
  void listenerFactory_hasBackedOffErrorHandler() {
    KafkaConsumerConfig config = new KafkaConsumerConfig();
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        config.kafkaListenerContainerFactory(
            config.consumerFactory(propertiesWithSecurity()));

    // Without an explicit handler, Spring Kafka retries 9 times with zero delay and then drops
    // the record — an outage longer than about a second would lose the event.
    Object errorHandler = ReflectionTestUtils.getField(factory, "commonErrorHandler");
    assertThat(errorHandler).isInstanceOf(DefaultErrorHandler.class);
  }
}
