package uk.co.whitbread.basket.confirmation.processor.infrastructure.queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.queue.model.in.BasketAcknowledgeEventDto;

@EnableKafka
@EmbeddedKafka(partitions = 1,
    topics = {"test.topic", "test.topic-retry-0", "test.topic-retry-1", "test.topic-retry-2", "test.topic-retry-3", "test.topic-dlt"})
@SpringBootTest(properties = {"spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "basket.confirmation.topics.ack.name=test.topic",
    "basket.confirmation.topics.ack.concurrency=1",
    "basket.kafka.bootstrapServers=${spring.embedded.kafka.brokers}"
})
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class BasketAcknowledgeConsumerTest {

  @Captor
  ArgumentCaptor<ConsumerRecord<String, BasketAcknowledgeEventDto>> argumentCaptor;
  private Producer<String, String> producer;
  @Autowired
  private EmbeddedKafkaBroker embeddedKafkaBroker;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
  @MockitoSpyBean
  private BasketAcknowledgeConsumer kafkaConsumer;


  @BeforeAll
  void setUp() {
    Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
    configs.put("key.serializer", StringSerializer.class);
    producer = new DefaultKafkaProducerFactory<String, String>(configs).createProducer();

    for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers()) {
      ContainerTestUtils.waitForAssignment(messageListenerContainer,
          embeddedKafkaBroker.getPartitionsPerTopic());
    }
  }

  @Test
  void testLogKafkaMessages() throws JsonProcessingException {
    //Arrange
    //Act
    String message = objectMapper.writeValueAsString(BasketAcknowledgeEventDto.builder()
        .basketReference("TEST123456")
        .build());
    producer.send(new ProducerRecord<>("test.topic", message));
    producer.flush();

    //Assert
    verify(kafkaConsumer, timeout(5000).times(1)).onAcknowledge(argumentCaptor.capture());

    ConsumerRecord<String, BasketAcknowledgeEventDto> record = argumentCaptor.getValue();
    assertNotNull(record);
    assertEquals("TEST123456", record.value().getBasketReference());
  }

  @AfterAll
  void shutdown() {
    producer.close();
  }
}
