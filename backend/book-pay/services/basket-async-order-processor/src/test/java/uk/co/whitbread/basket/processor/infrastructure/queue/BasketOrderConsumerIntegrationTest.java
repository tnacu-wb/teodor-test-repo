package uk.co.whitbread.basket.processor.infrastructure.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
import uk.co.whitbread.basket.processor.domain.model.in.BasketOrder;
import uk.co.whitbread.basket.processor.domain.ports.primary.BasketOrderProcessInPort;
import uk.co.whitbread.basket.processor.infrastructure.queue.mapper.BasketOrderMapper;
import uk.co.whitbread.basket.processor.infrastructure.queue.model.order.BasketOrderEvent;
import uk.co.whitbread.basket.processor.infrastructure.queue.model.order.BasketRequestAction;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.exception.AmendReservationException;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.exception.HotelReservationException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@EnableKafka
@EmbeddedKafka(partitions = 1,
        topics = {"test.topic", "test.topic-retry-0", "test.topic-retry-1", "test.topic-retry-2", "test.topic-retry-3", "test.topic-dlt"})
@SpringBootTest(properties = {"spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "basket.topics.orders.name=test.topic",
        "basket.topics.orders.concurrency=1",
        "basket.kafka.bootstrapServers=${spring.embedded.kafka.brokers}"
})
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class BasketOrderConsumerIntegrationTest {

  private final HotelReservationException exception = new HotelReservationException(
      "Exception on purpose for integration test.", "", new Exception(), 100);
  private static final String TOPIC = "test.topic";
  private static final String AMEND_3123 = "AMEND#3123";
  private static final String STAY_3123 = "STAY#3123";
  private static final String REFERENCE_1 = "REFERENCE1";
  private static final String REFERENCE_12 = "REFERENCE12";
  private static final String REFERENCE_123 = "REFERENCE123";
  @Captor
  ArgumentCaptor<BasketOrderEvent> argumentCaptor;
  private Producer<String, String> producer;
  private final String REQ_ACTION = "reqAction";
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private EmbeddedKafkaBroker embeddedKafkaBroker;
  @Autowired
  private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
  @MockitoSpyBean
  private BasketOrderConsumer kafkaConsumer;

  @MockitoBean
  private BasketOrderMapper basketOrderMapper;

  @MockitoBean
  private BasketOrderProcessInPort basketOrderProcessInPort;

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
    void testKafkaMessages_successFlow() throws JsonProcessingException {
        //Arrange
        BasketOrderEvent basketOrderEvent = BasketOrderEvent.builder()
                .eventId(AMEND_3123)
                .basketReference(REFERENCE_1)
                .data(Map.of(REQ_ACTION, BasketRequestAction.AMEND.getReqAction()))
                .build();
        String message = objectMapper.writeValueAsString(basketOrderEvent);
        when(basketOrderMapper.toDomain(basketOrderEvent)).thenReturn(new BasketOrder());

        //Act
        producer.send(new ProducerRecord<>(TOPIC, AMEND_3123, message));
        producer.flush();

        //Assert
        verify(kafkaConsumer, timeout(5000).times(1)).onOrder(argumentCaptor.capture());
        verify(kafkaConsumer, never()).dltHandler(argumentCaptor.capture());

        BasketOrderEvent record = argumentCaptor.getValue();
        assertNotNull(record);
        assertEquals(REFERENCE_1, record.getBasketReference());
    }

  @Test
  void testKafkaMessages_amendNotRetriedAfterException() throws JsonProcessingException {
    //Arrange
    var order = new BasketOrder();
    BasketOrderEvent basketOrderEvent = BasketOrderEvent.builder()
        .eventId(AMEND_3123)
        .basketReference(REFERENCE_12)
        .data(Map.of(REQ_ACTION, BasketRequestAction.AMEND.getReqAction()))
        .build();
    String message = objectMapper.writeValueAsString(basketOrderEvent);
    when(basketOrderMapper.toDomain(basketOrderEvent)).thenReturn(order);
    doThrow(new AmendReservationException("Exception on purpose for integration test.", "",
        new Exception(), 100)).
        when(basketOrderProcessInPort).processAmend(order);
    doNothing().when(kafkaConsumer).dltHandler(any());

    //Act
    producer.send(new ProducerRecord<>(TOPIC, AMEND_3123, message));
    producer.flush();

    //Assert
    verify(kafkaConsumer, timeout(5000).times(1)).onOrder(argumentCaptor.capture());
    verify(kafkaConsumer, timeout(5000).times(1)).dltHandler(argumentCaptor.capture());

    BasketOrderEvent record = argumentCaptor.getValue();
    assertNotNull(record);
    assertEquals(REFERENCE_12, record.getBasketReference());
    }


  @ParameterizedTest
  @EnumSource(value = BasketRequestAction.class, names = {"CANCEL", "COMMIT"})
  void testKafkaMessages_retryMechanismAfterException(BasketRequestAction arg)
      throws JsonProcessingException {

    //Arrange
    var order = new BasketOrder();
    BasketOrderEvent basketOrderEvent = BasketOrderEvent.builder()
        .eventId(STAY_3123)
        .basketReference(REFERENCE_123)
        .data(Map.of(REQ_ACTION, arg.getReqAction()))
        .build();
    String message = objectMapper.writeValueAsString(basketOrderEvent);
    when(basketOrderMapper.toDomain(basketOrderEvent)).thenReturn(order);

    switch (BasketRequestAction.valueOf(arg.getReqAction())) {
      case CANCEL -> doThrow(exception).when(basketOrderProcessInPort).processCancelOrder(order);
      default -> doThrow(exception).when(basketOrderProcessInPort).processOrder(order);
    }
    doNothing().when(kafkaConsumer).dltHandler(any());

    //Act
    producer.send(new ProducerRecord<>(TOPIC, STAY_3123, message));
    producer.flush();

    //Assert
    verify(kafkaConsumer, timeout(15000).times(5)).onOrder(argumentCaptor.capture());
    verify(kafkaConsumer, timeout(15000).times(1)).dltHandler(argumentCaptor.capture());

    BasketOrderEvent record = argumentCaptor.getValue();
    assertNotNull(record);
    assertEquals(REFERENCE_123, record.getBasketReference());
    }

    @AfterAll
    void shutdown() {
        producer.close();
    }
}
