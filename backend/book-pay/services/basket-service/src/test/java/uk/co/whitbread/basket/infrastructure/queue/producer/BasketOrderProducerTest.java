package uk.co.whitbread.basket.infrastructure.queue.producer;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micrometer.tracing.Tracer;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.exception.PaymentException;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.basket.out.CleanUpTime;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketItem;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentOption;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;
import uk.co.whitbread.basket.domain.model.payments.out.BasketRequestAction;
import uk.co.whitbread.basket.domain.model.payments.out.BookingConfirmationDetails;
import uk.co.whitbread.basket.infrastructure.queue.model.BasketOrderEvent;
import uk.co.whitbread.basket.infrastructure.queue.processor.BasketOrderMapper;
import uk.co.whitbread.basket.infrastructure.repository.BasketRepository;
import uk.co.whitbread.basket.infrastructure.repository.mapper.BasketEntityMapper;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketEntity;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@ExtendWith(MockitoExtension.class)
class BasketOrderProducerTest {

  @Mock
  private KafkaTemplate<String, BasketOrderEvent> kafkaTemplate;

  @Mock
  private BasketOrderMapper basketOrderMapper;

  @Mock
  private CleanUpTime cleanUpTime;

  @Mock
  private BasketRepository basketRepository;

  @Mock
  private BasketEntityMapper basketEntityMapper;

  private BasketOrderProducer basketOrderProducer;
  private static final Instant FIXED_INSTANT = Instant.parse("2024-10-21T15:00:00Z");
  private static final long CLEAN_UP_TIME_LONG = 5000;

  @Spy
  private ConcurrentTracer concurrentTracer = new ConcurrentTracer(Tracer.NOOP);

  @BeforeEach
  public void setUp() {
    basketOrderProducer =
        new BasketOrderProducer(kafkaTemplate, basketOrderMapper, basketEntityMapper, basketRepository, concurrentTracer, cleanUpTime);
  }

  @Test
  void testSendOrderAsync() {
    // Arrange
    when(basketOrderMapper.processBasket(any(Basket.class), any(String.class),
        any(BookingConfirmationDetails.class), any())).thenReturn(mockBasketOrderEvents());
    when(kafkaTemplate.getDefaultTopic()).thenReturn("orders");
    when(kafkaTemplate.send(any(String.class), any(String.class), any(BasketOrderEvent.class)))
        .thenReturn(mock(CompletableFuture.class));

    // Act
    basketOrderProducer.sendOrderAsync(mock(Basket.class), BasketRequestAction.COMMIT.getReqAction(),
        mock(BookingConfirmationDetails.class), null);

    // Assert
    verify(kafkaTemplate, times(2)).send(any(String.class), any(String.class), any(BasketOrderEvent.class));
  }

  @Test
  void testSendAmendOrderAsync() {
    // Arrange
    final var itemTypes = new HashMap<String, Set<String>>();
    itemTypes.put("STAY", new HashSet<>(asList("sourceId", "details.field1")));

    final var items = new ArrayList<BasketItem>();
    final var item1Details = new HashMap<String, String>();
    item1Details.put("field1", "value1");

    final var basketItem = BasketItem.builder()
        .type("STAY")
        .sourceId("123")
        .details(item1Details)
        .build();
    items.add(basketItem);
    final var basket = Basket.builder()
        .reference("reference")
        .basketId("basket-id")
        .paymentOption("PAY_NOW")
        .paymentID("12345")
        .emailAddress("user@whitbread.com")
        .itemTypes(itemTypes)
        .items(items)
        .channel("PI")
        .paymentChannel("WEB")
        .originalBasketId("original-basket-reference")
        .build();
    final var paymentConfirmation = PaymentsConfirmation.builder()
        .paymentId("12345")
        .token("token")
        .emailAddress("jane.done@wb.com")
        .channel("PI")
        .bookingReference("reference")
        .language("en")
        .paymentOptionSelected(PaymentOption.PAY_NOW.toString())
        .ccAgentId("jane.done@wb.com")
        .build();

    when(kafkaTemplate.getDefaultTopic()).thenReturn("orders");
    when(kafkaTemplate.send(any(String.class), any(String.class), any(BasketOrderEvent.class)))
        .thenReturn(mock(CompletableFuture.class));

    // Act
    basketOrderProducer.sendAmendAsync(basket, paymentConfirmation, BasketRequestAction.AMEND.getReqAction());

    // Assert
    verify(kafkaTemplate, times(1)).send(any(String.class), any(String.class), any(BasketOrderEvent.class));
  }

  @Test
  void testSendAmendOrderAsyncWithEmptyValues() {
    // Arrange
    final var itemTypes = new HashMap<String, Set<String>>();
    itemTypes.put("STAY", new HashSet<>(asList("sourceId", "details.field1")));

    final var items = new ArrayList<BasketItem>();
    final var item1Details = new HashMap<String, String>();
    item1Details.put("field1", "value1");

    final var basketItem = BasketItem.builder()
        .type("STAY")
        .sourceId("123")
        .details(item1Details)
        .build();
    items.add(basketItem);
    final var basket = Basket.builder()
        .reference("reference")
        .basketId("basket-id")
        .paymentOption("PAY_NOW")
        .paymentID("12345")
        .emailAddress("user@whitbread.com")
        .itemTypes(itemTypes)
        .items(items)
        .channel("PI")
        .paymentChannel("WEB")
        .originalBasketId("original-basket-reference")
        .build();
    final var paymentConfirmation = PaymentsConfirmation.builder()
        .paymentId("12345")
        .token("token")
        .emailAddress("")
        .channel("PI")
        .bookingReference("reference")
        .language("en")
        .paymentOptionSelected(PaymentOption.PAY_NOW.toString())
        .ccAgentId("")
        .build();

    when(kafkaTemplate.getDefaultTopic()).thenReturn("orders");
    when(kafkaTemplate.send(any(String.class), any(String.class), any(BasketOrderEvent.class)))
        .thenReturn(mock(CompletableFuture.class));

    // Act
    basketOrderProducer.sendAmendAsync(basket, paymentConfirmation, BasketRequestAction.AMEND.getReqAction());

    // Assert
    verify(kafkaTemplate, times(1)).send(any(String.class), any(String.class), any(BasketOrderEvent.class));
  }


  @SneakyThrows
  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void testSend_Exception(boolean entity) {
    // Arrange
    CompletableFuture<SendResult<String, BasketOrderEvent>> comp = mock(CompletableFuture.class);

    when(comp.whenComplete(Mockito.any(BiConsumer.class))).thenAnswer(invocation -> {
      BiConsumer<SendResult<String, BasketOrderEvent>, Throwable> consumer = invocation.getArgument(
          0);
      consumer.accept(null, new Exception("message"));
      if (entity) {
        return new Throwable("message");
      } else {
        return null;
      }
    });

    Map<String, String> map = new HashMap<>();
    map.put("tempBookingRef", "111");
    var basketOrderEvent = BasketOrderEvent.builder()
        .data(map)
        .eventId("123")
        .basketReference("ref").build();
    when(kafkaTemplate.send(null, "123", basketOrderEvent))
        .thenReturn(comp);
    ArgumentCaptor<BasketEntity> captor = ArgumentCaptor.forClass(BasketEntity.class);
    MockedStatic<Instant> mockedStatic = mockStatic(Instant.class, Mockito.CALLS_REAL_METHODS);
    if (entity) {
      BasketEntity basketEntity = BasketEntity.builder().build();
      Optional<BasketEntity> opt = Optional.of(basketEntity);
      when(basketRepository.getByReference(basketOrderEvent.getBasketReference()))
          .thenReturn(opt);
      when(basketRepository.save(basketEntity)).thenReturn(Mono.just(basketEntity));
      when(basketEntityMapper.toBasketStatusModel(any())).thenReturn(BasketStatus.AMEND_FAILED);
      var clock = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);
      var mockedInstant = Instant.now(clock);
      mockedStatic.when(Instant::now).thenReturn(mockedInstant);
      when(cleanUpTime.getCleanUpTime(any(), any())).thenReturn(CLEAN_UP_TIME_LONG);
    } else {
      when(basketRepository.getByReference(basketOrderEvent.getBasketReference()))
          .thenReturn(Optional.empty());
    }
    //Act
    if (entity) {
      var exception = assertThrows(PaymentException.class,
          () -> ReflectionTestUtils.invokeMethod(basketOrderProducer, "send", basketOrderEvent));
      //Assert
      String actualMessage = exception.getMessage();
      assertTrue(actualMessage.contains("AMEND_FAILED"));
      verify(basketRepository, times(1)).save(captor.capture());
      final var basketEntityResult = captor.getValue();
      assertEquals(CLEAN_UP_TIME_LONG, basketEntityResult.getCleanUpTime());
      assertEquals(BasketStatus.AMEND_FAILED.name(), basketEntityResult.getStatus().name());
      verify(cleanUpTime, times(1)).getCleanUpTime(BasketStatus.AMEND_FAILED, FIXED_INSTANT);
    } else {
      var exception = assertThrows(PaymentException.class,
          () -> ReflectionTestUtils.invokeMethod(basketOrderProducer, "send", basketOrderEvent));
      //Assert
      String actualMessage = exception.getMessage();
      assertTrue(actualMessage.contains("could not be updated to FAILED"));
    }
    mockedStatic.close();
  }

  @Test
  void testThrowPaymentException() {
    final String message = "test1234";
    var exception = assertThrows(PaymentException.class,
        () -> ReflectionTestUtils.invokeMethod(basketOrderProducer, "throwPaymentException",
            ErrorCode.DIGITAL_OUT_OF_SYNC_EXCEPTION, message));
    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(message));
  }

  private List<BasketOrderEvent> mockBasketOrderEvents() {
    return asList(
        BasketOrderEvent.builder()
            .basketReference("REF123")
            .eventId("123")
            .build(),
        BasketOrderEvent.builder()
            .basketReference("REF456")
            .eventId("456")
            .build()
    );
  }


}
