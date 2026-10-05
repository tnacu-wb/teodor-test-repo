package uk.co.whitbread.basket.infrastructure.queue.producer;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketItem;
import uk.co.whitbread.basket.domain.model.basket.out.CleanUpTime;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.AccountCompanyItems;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.CcuiExtraItems;
import uk.co.whitbread.basket.domain.model.payments.out.BasketRequestAction;
import uk.co.whitbread.basket.domain.model.payments.out.BookingConfirmationDetails;
import uk.co.whitbread.basket.domain.model.payments.out.CardData;
import uk.co.whitbread.basket.infrastructure.config.KafkaConfig;
import uk.co.whitbread.basket.infrastructure.queue.model.BasketOrderEvent;
import uk.co.whitbread.basket.infrastructure.queue.processor.BasketOrderMapper;
import uk.co.whitbread.basket.infrastructure.repository.BasketRepository;
import uk.co.whitbread.basket.infrastructure.repository.mapper.BasketEntityMapper;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = {
        BasketOrderProducer.class,
        BasketOrderMapper.class,
        KafkaConfig.class
    }
)
@EmbeddedKafka(topics = {"orders"}, adminTimeout = 60)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
class BasketOrderProducerIntegrationTest {

    public static final String BASKET_REFERENCE = "BSK-80569f9b-063b-436d-bfcc-4af7e46f9340";
    public static final String BOOKING_REFERENCE = "BSK1234567";

    @Autowired
    private BasketOrderProducer basketOrderProducer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockitoBean
    private BasketEntityMapper basketEntityMapper;

    @MockitoBean
    private BasketRepository basketRepository;

    @MockitoBean
    private CleanUpTime cleanUpTime;

    @MockitoBean
    private ConcurrentTracer tracer;

    private Consumer<String, BasketOrderEvent> consumer;

    @BeforeEach
    void setUp() {
        when(tracer.wrap(any(BiConsumer.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> configs =
            new HashMap<>(KafkaTestUtils.consumerProps(embeddedKafkaBroker, "basket", true));

        consumer = new DefaultKafkaConsumerFactory<>(
            configs,
            new StringDeserializer(),
            new JacksonJsonDeserializer<>(BasketOrderEvent.class)
        ).createConsumer();

        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void cleanUp() {
        consumer.close();
    }

    @Test
    void testBasketOrder() {
        final var itemTypes = new HashMap<String, Set<String>>();
        itemTypes.put("STAY", new HashSet<>(asList("sourceId", "details.field1", "details.field2")));

        final var item1Details = new HashMap<String, String>();
        item1Details.put("field1", "value1");
        item1Details.put("field2", "value2");

        final var basketItem = BasketItem.builder()
            .type("STAY")
            .sourceId("123")
            .details(item1Details)
            .build();

        final var items = new ArrayList<BasketItem>();
        items.add(basketItem);

        final var basket = Basket.builder()
            .reference(BOOKING_REFERENCE)
            .basketId(BASKET_REFERENCE)
            .paymentOption("PAY_ON_ARRIVAL")
            .paymentID("12345")
            .emailAddress("user@whitbread.com")
            .threeDSIndicator("1")
            .itemTypes(itemTypes)
            .items(items)
            .ccuiExtraItems(CcuiExtraItems.builder()
                .accountCompanyItems(AccountCompanyItems.builder()
                    .companyId("1249087")
                    .build())
                .build())
            .build();

        final var cardInfo = CardData.builder()
            .token("12345")
            .expirationDate("06/80")
            .cardHolderName("Sm9obg== U21pdGg=")
            .last4Digits("4567")
            .build();

        final var bookingConfirmationDetails = BookingConfirmationDetails.builder()
            .cardData(cardInfo)
            .paymentType("VA")
            .cardType("VA")
            .paymentMethod("DVA")
            .build();

        basketOrderProducer.sendOrderAsync(
            basket,
            BasketRequestAction.COMMIT.getReqAction(),
            bookingConfirmationDetails,
            null
        );

        ConsumerRecord<String, BasketOrderEvent> consumerRecord =
            KafkaTestUtils.getSingleRecord(consumer, "orders");

        BasketOrderEvent basketOrderEvent = consumerRecord.value();

        assertThat(consumerRecord.key(), is("STAY#123"));
        assertThat(basketOrderEvent.getEventId(), is("STAY#123"));
        assertThat(basketOrderEvent.getBookingReference(), is(BOOKING_REFERENCE));
        assertThat(basketOrderEvent.getBasketReference(), is(BASKET_REFERENCE));
        assertThat(basketOrderEvent.getData().get("field1"), is("value1"));
        assertThat(basketOrderEvent.getData().get("field2"), is("value2"));
    }
}