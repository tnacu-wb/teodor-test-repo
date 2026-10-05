package uk.co.whitbread.payment.orchestrator.infrastructure.kafka;

import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

/**
 * Kafka consumer configuration for the payment orchestration service.
 *
 * <p>{@link EnableKafka} activates {@code @KafkaListener} annotation processing,
 * which registers the {@link BookingCompletedEventConsumer} with the Kafka
 * listener container.
 *
 * <p>The consumer factory builds its properties from Boot's {@link KafkaProperties}, so every
 * {@code spring.kafka.*} setting a deployment provides — security protocol, SASL/SSL
 * configuration, client id, poll tuning — reaches the client. A hand-picked property map here
 * would silently drop everything it did not copy, which is precisely the failure mode that
 * only shows up on first contact with a secured cluster. The only opinion this class imposes
 * is the String deserialization the listener expects.
 */
@Configuration
@EnableKafka
@Profile("!integration")
public class KafkaConsumerConfig {

  @Bean
  public ConsumerFactory<String, String> consumerFactory(KafkaProperties kafkaProperties) {
    Map<String, Object> props = kafkaProperties.buildConsumerProperties();
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    return new DefaultKafkaConsumerFactory<>(props);
  }

  /**
   * Listener container factory with a real retry backoff.
   *
   * <p>The default error handler retries nine times with zero delay — an outage of the Temporal
   * frontend lasting longer than about a second would burn every attempt and drop the record.
   * The exponential backoff below (1s doubling to a 30s cap, ~5 minutes in total) rides out a
   * deploy or restart of the dependency instead.
   *
   * <p>A record that exhausts the backoff is logged and skipped, not dead-lettered — a
   * deliberate decision, not an oversight: the payment workflow's booking-completion poll (see
   * {@code PaymentWorkflowImpl#awaitBookingOutcome}) re-derives a lost
   * {@code BookingCompletedEvent} from the Basket Service within its polling horizon, so the
   * event stream is a fast path with a designed recovery, not the sole source of truth.
   */
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
      ConsumerFactory<String, String> consumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);

    ExponentialBackOff backOff = new ExponentialBackOff(1_000L, 2.0);
    backOff.setMaxInterval(30_000L);
    backOff.setMaxElapsedTime(300_000L);
    factory.setCommonErrorHandler(new DefaultErrorHandler(backOff));
    return factory;
  }

}
