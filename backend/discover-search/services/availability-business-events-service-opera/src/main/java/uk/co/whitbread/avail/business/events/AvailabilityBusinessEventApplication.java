package uk.co.whitbread.avail.business.events;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.unleash.features.config.UnleashAutoConfiguration;

@Import(UnleashAutoConfiguration.class)
@SpringBootApplication(scanBasePackages = "uk.co.whitbread")
public class AvailabilityBusinessEventApplication {

  @Value("${spring.jackson.time-zone:Europe/London}")
  private String timezone;

  @PostConstruct
  void started() {
    TimeZone.setDefault(TimeZone.getTimeZone(timezone));
  }

  @Bean
  public JavaTimeModule javaTimeModule() {
    final JavaTimeModule javaTimeModule = new JavaTimeModule();
    javaTimeModule.addDeserializer(LocalDateTime.class,
        new LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    javaTimeModule.addDeserializer(LocalDate.class,
        new LocalDateDeserializer(DateTimeFormatter.ISO_LOCAL_DATE));
    return javaTimeModule;
  }

  public static void main(String[] args) {
    SpringApplication.run(AvailabilityBusinessEventApplication.class, args);
  }

}
