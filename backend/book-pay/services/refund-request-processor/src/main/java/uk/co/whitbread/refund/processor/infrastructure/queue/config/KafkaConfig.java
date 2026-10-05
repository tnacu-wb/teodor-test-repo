package uk.co.whitbread.refund.processor.infrastructure.queue.config;

import brave.Tracing;
import brave.kafka.clients.KafkaTracing;
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
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import uk.co.whitbread.refund.processor.infrastructure.queue.model.ack.ItemAcknowledgeEvent;
import uk.co.whitbread.refund.processor.infrastructure.queue.model.refund.RefundRequestEvent;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConfig {

  private final KafkaProperties kafkaProperties;

  @Value("${rrp.topics.ack}")
  private String ackTopic;

  private ProducerFactory<String, ItemAcknowledgeEvent> ackProducerFactory() {
    Map<String, Object> configProps = getConfigProps();
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  public KafkaTemplate<String, ItemAcknowledgeEvent> ackKafkaTemplate() {
    var template = new KafkaTemplate<>(ackProducerFactory());
    template.setDefaultTopic(ackTopic);
    template.setObservationEnabled(true);

    return template;
  }


  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, RefundRequestEvent> refundRequestFactory() {
    ConcurrentKafkaListenerContainerFactory<String, RefundRequestEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.getContainerProperties().setObservationEnabled(true);
    factory.setConsumerFactory(consumerFactory());
    factory.setCommonErrorHandler(new DefaultErrorHandler());
    return factory;
  }

  @Bean
  public ConsumerFactory<String, RefundRequestEvent> consumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
    props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
    props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, "false");
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, RefundRequestEvent.class);
    props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProperties.getRequestTimeout());
    props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, kafkaProperties.getHeartBeatInterval());
    props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaProperties.getMaxPollInterval());
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaProperties.getMaxPollRecords());
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaProperties.getSessionTimeout());
    props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());
    return new DefaultKafkaConsumerFactory<>(props);
  }

  private ProducerFactory<String, RefundRequestEvent> dlProducerFactory() {
    Map<String, Object> configProps = getConfigProps();
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  public KafkaTemplate<String, RefundRequestEvent> dlKafkaTemplate() {
    var kafkaTemplate = new KafkaTemplate<>(dlProducerFactory());
    kafkaTemplate.setObservationEnabled(true);
    return kafkaTemplate;
  }

  private Map<String, Object> getConfigProps() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        kafkaProperties.getBootstrapServers());
    configProps.put(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class);
    configProps.put(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        JsonSerializer.class);
    return configProps;
  }

  @Bean
  KafkaTracing kafkaTracing(Tracing tracing) {
    return KafkaTracing.create(tracing);
  }
}
