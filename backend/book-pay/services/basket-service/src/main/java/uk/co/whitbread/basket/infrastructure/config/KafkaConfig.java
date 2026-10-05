package uk.co.whitbread.basket.infrastructure.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import uk.co.whitbread.basket.infrastructure.queue.model.BasketOrderEvent;
import uk.co.whitbread.basket.infrastructure.queue.model.BookingCompletedEvent;
import uk.co.whitbread.basket.infrastructure.queue.model.EmailNotificationEvent;
import uk.co.whitbread.basket.infrastructure.queue.model.PaymentAuthorisedEvent;
import uk.co.whitbread.basket.infrastructure.queue.model.PaymentAuthorisedEventDTO;
import uk.co.whitbread.basket.infrastructure.queue.model.RefundRequestEvent;

@Configuration
@EnableKafka
public class KafkaConfig {

  private final String bootstrapServers;

  public KafkaConfig(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
    this.bootstrapServers = bootstrapServers;
  }

  private ProducerFactory<String, BasketOrderEvent> basketOrderProducerFactory() {
    Map<String, Object> configProps = getDefaultConfigsProps();
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  private ProducerFactory<String, EmailNotificationEvent> emailNotificationsProducerFactory() {
    Map<String, Object> configProps = getDefaultConfigsProps();
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  private ProducerFactory<String, RefundRequestEvent> refundsProducerFactory() {
    Map<String, Object> configProps = getDefaultConfigsProps();
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  private ProducerFactory<String, BookingCompletedEvent> bookingCompletedProducerFactory() {
    Map<String, Object> configProps = getDefaultConfigsProps();
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean(name = "orders")
  public KafkaTemplate<String, BasketOrderEvent> ordersKafkaTemplate(
      @Value("${basket.topics.orders}") String ordersTopic) {
    var kafkaTemplate = new KafkaTemplate<>(basketOrderProducerFactory());
    kafkaTemplate.setDefaultTopic(ordersTopic);
    kafkaTemplate.setObservationEnabled(true);
    return kafkaTemplate;
  }

  @Bean(name = "notifications")
  public KafkaTemplate<String, EmailNotificationEvent> emailNotificationsKafkaTemplate(
      @Value("${basket.topics.notifications}") String notificationsTopic) {
    var kafkaTemplate = new KafkaTemplate<>(emailNotificationsProducerFactory());
    kafkaTemplate.setObservationEnabled(true);
    kafkaTemplate.setDefaultTopic(notificationsTopic);
    return kafkaTemplate;
  }

  @Bean(name = "refunds")
  public KafkaTemplate<String, RefundRequestEvent> refundsKafkaTemplate(
      @Value("${basket.topics.refunds}") String refundsTopic) {
    var kafkaTemplate = new KafkaTemplate<>(refundsProducerFactory());
    kafkaTemplate.setObservationEnabled(true);
    kafkaTemplate.setDefaultTopic(refundsTopic);
    return kafkaTemplate;
  }

  @Bean(name = "bookingCompleted")
  public KafkaTemplate<String, BookingCompletedEvent> bookingCompletedKafkaTemplate(
      @Value("${basket.topics.bookingCompleted:booking-completed}")
      String bookingCompletedTopic) {
    var kafkaTemplate = new KafkaTemplate<>(bookingCompletedProducerFactory());
    kafkaTemplate.setObservationEnabled(true);
    kafkaTemplate.setDefaultTopic(bookingCompletedTopic);
    return kafkaTemplate;
  }

  @Bean(name = "paymentsConsumerFactory")
  public ConsumerFactory<String, PaymentAuthorisedEventDTO> paymentsConsumerFactory(
      @Value("${basket.kafka.consumer.paymentAuthorised.groupId:basket-service-payment-authorised}")
      String paymentsConsumerGroupId) {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configProps.put(ConsumerConfig.GROUP_ID_CONFIG, paymentsConsumerGroupId);
    configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
    configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JacksonJsonDeserializer.class);
    configProps.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "uk.co.whitbread.basket.infrastructure.queue.model");
    configProps.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);
    configProps.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, PaymentAuthorisedEventDTO.class.getName());
    return new DefaultKafkaConsumerFactory<>(configProps);
  }

  @Bean(name = "paymentsFactory")
  public ConcurrentKafkaListenerContainerFactory<String, PaymentAuthorisedEventDTO>
      paymentsKafkaListenerContainerFactory(
          ConsumerFactory<String, PaymentAuthorisedEventDTO> paymentsConsumerFactory) {
    var factory = new ConcurrentKafkaListenerContainerFactory<String, PaymentAuthorisedEventDTO>();
    factory.setConsumerFactory(paymentsConsumerFactory);
    factory.getContainerProperties().setObservationEnabled(true);
    return factory;
  }

  @Bean(name = "paymentsDlKafkaTemplate")
  public KafkaTemplate<String, PaymentAuthorisedEvent> paymentsDlKafkaTemplate() {
    var kafkaTemplate = new KafkaTemplate<>(paymentsDlProducerFactory());
    kafkaTemplate.setObservationEnabled(true);
    return kafkaTemplate;
  }

  @Bean
  public NewTopic paymentAuthorisedTopic(
      @Value("${basket.topics.paymentAuthorised:payment-authorised}") String paymentAuthorisedTopic,
      @Value("${basket.topics.paymentAuthorisedPartitions:3}") int paymentAuthorisedPartitions,
      @Value("${basket.topics.paymentAuthorisedReplicas:1}") short paymentAuthorisedReplicas) {
    return TopicBuilder.name(paymentAuthorisedTopic)
        .partitions(paymentAuthorisedPartitions)
        .replicas(paymentAuthorisedReplicas)
        .build();
  }

  @Bean
  public NewTopic bookingCompletedTopic(
      @Value("${basket.topics.bookingCompleted:booking-completed}")
      String bookingCompletedTopic,
      @Value("${basket.topics.bookingCompletedPartitions:3}")
      int bookingCompletedPartitions,
      @Value("${basket.topics.bookingCompletedReplicas:1}")
      short bookingCompletedReplicas) {
    return TopicBuilder.name(bookingCompletedTopic)
        .partitions(bookingCompletedPartitions)
        .replicas(bookingCompletedReplicas)
        .build();
  }

  private ProducerFactory<String, PaymentAuthorisedEvent> paymentsDlProducerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(configProps);
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
        JsonSerializer.class);
    configProps.put(ProducerConfig.ACKS_CONFIG, "all");
    return configProps;
  }

}
