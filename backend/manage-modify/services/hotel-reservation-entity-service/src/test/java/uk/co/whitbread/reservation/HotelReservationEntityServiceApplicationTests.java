package uk.co.whitbread.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = HotelReservationEntityServiceApplication.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HotelReservationEntityServiceApplicationTests {

  @Autowired
  ApplicationContext applicationContext;

  @Value("${spring.application.name}")
  String applicationName;

  @Test
  void contextLoads() {
    assertThat(applicationContext).isNotNull();
  }

  @Test
  void applicationNameIsConfigured() {
    assertThat(applicationName).isEqualTo("hotel-reservation-entity-service");
  }

  @Test
  void keyBeansArePresent() {
    assertThat(applicationContext.containsBean("ohipAdapterWebClient")).isTrue();
    assertThat(applicationContext.containsBean("basketWebClient")).isTrue();
    assertThat(applicationContext.containsBean("customOpenApi")).isTrue();
  }
}
