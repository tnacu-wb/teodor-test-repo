package uk.co.whitbread.basket.processor.infrastructure.queue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.co.whitbread.basket.processor.infrastructure.queue.model.ack.ItemAcknowledgeEvent;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"ack"})
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BasketAcknowledgeProducerIntegrationTest {
  @Autowired
  private BasketAcknowledgeProducer basketAcknowledgeProducer;

  @Autowired
  private EmbeddedKafkaBroker embeddedKafkaBroker;

  @Value("${config.service.reservation.status.reserved}")
  private String reservedStatus;

  private Consumer<String, ItemAcknowledgeEvent> consumer;

  @BeforeEach
  void setUp() {
    Map<String, Object> configs = new HashMap<>(
        KafkaTestUtils.consumerProps(embeddedKafkaBroker, "basket", true));
    consumer = new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(), 
        new JacksonJsonDeserializer<>(ItemAcknowledgeEvent.class)).createConsumer();
    embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "ack");
  }

  @AfterEach
  void cleanUp() {
    consumer.close();
  }

  @Test
  void testSendAck_statusSuccess() {
    // Arrange
    var basketReference = "123";
    var itemId = "456";
    var reqAction = "COMMIT";
    // Act
    basketAcknowledgeProducer.sendAckAsync(basketReference, itemId, reqAction, reservedStatus);

    // Assert
    ConsumerRecord<String, ItemAcknowledgeEvent> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "ack");
    ItemAcknowledgeEvent itemAcknowledgeEvent = consumerRecord.value();

    assertThat(consumerRecord.key(), is("123"));
    assertThat(itemAcknowledgeEvent.getItemId(), is("456"));
    assertThat(itemAcknowledgeEvent.getBasketReference(), is("123"));
    assertThat(itemAcknowledgeEvent.getStatus(), is(0));
  }

  @Test
  void testSendAck_statusFailed() {
    // Arrange
    var basketReference = "123";
    var itemId = "456";
    var reqAction = "COMMIT";
    // Act
    basketAcknowledgeProducer.sendAckAsync(basketReference, itemId, reqAction, "Cancelled");

    // Assert
    ConsumerRecord<String, ItemAcknowledgeEvent> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "ack");
    ItemAcknowledgeEvent itemAcknowledgeEvent = consumerRecord.value();

    assertThat(consumerRecord.key(), is("123"));
    assertThat(itemAcknowledgeEvent.getItemId(), is("456"));
    assertThat(itemAcknowledgeEvent.getBasketReference(), is("123"));
    assertThat(itemAcknowledgeEvent.getStatus(), is(1));
    assertThat(itemAcknowledgeEvent.getErrors().get(0), is("Reservation request status is: Cancelled"));
  }

  @Test
  void testSendAck_statusSuccessCiol() {
    // Arrange
    var basketReference = "123";
    var itemId = "456";
    var reqAction = "COMMIT";
    // Act
    basketAcknowledgeProducer.sendAckAsync(basketReference, itemId, reqAction, "Reserved");

    // Assert
    ConsumerRecord<String, ItemAcknowledgeEvent> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "ack");
    ItemAcknowledgeEvent itemAcknowledgeEvent = consumerRecord.value();

    assertThat(consumerRecord.key(), is("123"));
    assertThat(itemAcknowledgeEvent.getItemId(), is("456"));
    assertThat(itemAcknowledgeEvent.getBasketReference(), is("123"));
    assertThat(itemAcknowledgeEvent.getStatus(), is(0));
  }
}
