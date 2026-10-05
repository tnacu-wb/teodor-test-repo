package uk.co.whitbread.basket.infrastructure.queue.producer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.in;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

import uk.co.whitbread.basket.domain.model.email.out.EmailNotificationEventType;
import uk.co.whitbread.basket.infrastructure.config.KafkaConfig;
import uk.co.whitbread.basket.infrastructure.queue.model.EmailItem;
import uk.co.whitbread.basket.infrastructure.queue.model.EmailNotificationEvent;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    classes = {
        EmailNotificationProducer.class,
        KafkaConfig.class
    }
)
@EmbeddedKafka(topics = {"notifications"}, adminTimeout = 60)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
class EmailNotificationProducerIntegrationTest {

    @Autowired
    private EmailNotificationProducer emailNotificationProducer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockitoBean
    private ConcurrentTracer tracer;

    private Consumer<String, EmailNotificationEvent> consumer;

    @BeforeEach
    void setUp() {
        when(tracer.wrap(any(BiConsumer.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> configs =
            new HashMap<>(KafkaTestUtils.consumerProps(embeddedKafkaBroker, "email", true));

        consumer = new DefaultKafkaConsumerFactory<>(
            configs,
            new StringDeserializer(),
            new JacksonJsonDeserializer<>(EmailNotificationEvent.class)
        ).createConsumer();

        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void cleanUp() {
        consumer.close();
    }

    @Test
    void testEmailNotificationEvent() {
        // Arrange
        Map<String, String> details = new HashMap<>();
        details.put("emailAddress", "test@gmail.com");

        List<EmailItem> items = Collections.singletonList(
            EmailItem.builder()
                .sourceId("30000")
                .type("STAY")
                .sourceSystemId("OPERA")
                .build()
        );

        String eventId = UUID.randomUUID().toString();

        var createdAt = Instant.now().toString();

        var emailNotificationEvent = EmailNotificationEvent.builder()
            .id(eventId)
            .bookingReference("REF12345")
            .type(EmailNotificationEventType.CONFIRM.toString())
            .createdAt(createdAt)
            .items(items)
            .details(details)
            .excludedPurposes(new ArrayList<>())
            .build();

        // Act
        emailNotificationProducer.sendEmailNotificationEvent(emailNotificationEvent);

        ConsumerRecord<String, EmailNotificationEvent> consumerRecord =
            KafkaTestUtils.getSingleRecord(consumer, "notifications");

        EmailNotificationEvent emailNotificationEventResponse = consumerRecord.value();

        assertThat(consumerRecord.key(), is(eventId));
        assertThat(emailNotificationEventResponse.getId(), is(eventId));
        assertThat(emailNotificationEventResponse.getType(), is(EmailNotificationEventType.CONFIRM.toString()));
        assertThat(emailNotificationEventResponse.getBookingReference(), is("REF12345"));
        assertThat(emailNotificationEventResponse.getCreatedAt(), is(createdAt));
        assertThat(emailNotificationEventResponse.getItems(), everyItem(is(in((items)))));
        assertThat(emailNotificationEventResponse.getExcludedPurposes(), is((empty())));
        assertThat(emailNotificationEventResponse.getDetails().get("emailAddress"), is("test@gmail.com"));
    }
}