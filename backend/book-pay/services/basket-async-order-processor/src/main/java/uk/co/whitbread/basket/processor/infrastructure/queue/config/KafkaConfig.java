package uk.co.whitbread.basket.processor.infrastructure.queue.config;

import io.micrometer.observation.ObservationRegistry;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import uk.co.whitbread.basket.processor.infrastructure.queue.model.ack.ItemAcknowledgeEvent;
import uk.co.whitbread.basket.processor.infrastructure.queue.model.order.BasketOrderEvent;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConfig {

  @Value("${basket.event.order.prefix.stayOrderPrefix}")
  private String stayOrderPrefix;

  @Value("${basket.event.order.prefix.amendOrderPrefix}")
  private String amendOrderPrefix;

  @Value("${basket.topics.ack}")
  private String ackTopic;

  private final KafkaProperties kafkaProperties;
  private final ObservationRegistry observationRegistry;

  private Map<String, Object> getKafkaConfig() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        kafkaProperties.getBootstrapServers());
    configProps.put(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class);
    configProps.put(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        JacksonJsonSerializer.class);
    return configProps;
  }

  private ProducerFactory<String, ItemAcknowledgeEvent> ackProducerFactory() {
    return new DefaultKafkaProducerFactory<>(getKafkaConfig());
  }

  @Bean
  public KafkaTemplate<String, ItemAcknowledgeEvent> ackKafkaTemplate() {
    var template = new KafkaTemplate<>(ackProducerFactory());
    template.setDefaultTopic(ackTopic);
    template.setObservationRegistry(observationRegistry);

    return template;
  }

  private ProducerFactory<String, BasketOrderEvent> dlProducerFactory() {
    return new DefaultKafkaProducerFactory<>(getKafkaConfig());
  }

  @Bean
  public KafkaTemplate<String, BasketOrderEvent> dlKafkaTemplate() {
    var template = new KafkaTemplate<>(dlProducerFactory());
    template.setObservationRegistry(observationRegistry);

    return template;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, BasketOrderEvent> basketOrderFactory() {
    ConcurrentKafkaListenerContainerFactory<String, BasketOrderEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.getContainerProperties().setObservationRegistry(observationRegistry);
    factory.setConsumerFactory(consumerFactory());
    factory.setRecordFilterStrategy(order -> !(order.key().startsWith(stayOrderPrefix)
        || order.key().startsWith(amendOrderPrefix)));
    factory.setCommonErrorHandler(new DefaultErrorHandler());
    return factory;
  }

  private ConsumerFactory<String, BasketOrderEvent> consumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
    props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
    props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
    props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, "false");
    props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, BasketOrderEvent.class);
    props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProperties.getRequestTimeout());
    props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, kafkaProperties.getHeartBeatInterval());
    props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaProperties.getMaxPollInterval());
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaProperties.getMaxPollRecords());
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaProperties.getSessionTimeout());
    props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());
    return new DefaultKafkaConsumerFactory<>(props);
  }

}
