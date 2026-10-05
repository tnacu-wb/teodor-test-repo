package troubleshooting;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Slf4j
class KafkaTroubleshootingTest {


  @Test
  @Disabled("#Troubleshooting# Test locally that a message resides in Kafka and can be read")
  void kafka__readContentFromTopic() {
    //given
    var topic = "orders";
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringDeserializer");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
    //then
    assertDoesNotThrow(() -> {
      try (KafkaConsumer<String, String> myConsumer = new KafkaConsumer<>(props)) {
        myConsumer.subscribe(List.of(topic));  // list of topics to subscribe to
        while (true) {
          ConsumerRecords<String, String> records = myConsumer.poll(Duration.ofMillis(20));
          var found = false;
          for (ConsumerRecord<String, String> record : records) {
            found = true;
            log.info("##TROUBLESHOOT## Topic: {}, Partition: {}, Offset:{}, Key:{}, Value: {}",
                record.topic(), record.partition(), record.offset(), record.key(), record.value());
          }
          if (found) {
            break;
          }
        }
      }
    });
  }

  @Test
  @Disabled("#Troubleshooting# Test locally that a message is sent")
  void kafka__pushContentToTopic() {
    //given
    var topic = "orders";
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    //then
    assertDoesNotThrow(() -> {
      try (KafkaProducer<String, String> myProducer = new KafkaProducer<>(props)) {
        ProducerRecord<String, String> producerRecord =
            new ProducerRecord<>(topic, "Add your content here ...");
        myProducer.send(producerRecord);
        log.info("##TROUBLESHOOT## record sent!");
      }
    });
  }
}
