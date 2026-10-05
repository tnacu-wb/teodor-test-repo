package uk.co.whitbread.avail.business.events;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DirtiesContext
@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = {AvailabilityBusinessEventsServiceIT.Initializer.class})
@SpringBootTest(classes = {AvailabilityBusinessEventApplication.class},
    properties = {
        "opera.clientId=testUserName",
        "opera.clientSecret=testPassword",
        "SubscribeBusinessEventsColdStartSvc.runner.enabled=false",
        "spring.datasource.driver-class-name=org.postgresql.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.hibernate.default_schema=avail_cache"
    })
@Testcontainers
@Disabled
public class AvailabilityBusinessEventsServiceIT {

  @Container
  public static final JdbcDatabaseContainer postgreSQLContainer =
      new PostgreSQLContainer("postgres:latest")
          .withInitScript("schema.sql")
          .withDatabaseName("integration-tests-db")
          .withUsername("sa")
          .withPassword("sa");

  static class Initializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of(
          "spring.datasource.writer.jdbc-url=" + postgreSQLContainer.getJdbcUrl(),
          "spring.datasource.writer.username=" + postgreSQLContainer.getUsername(),
          "spring.datasource.writer.password=" + postgreSQLContainer.getPassword()
      ).applyTo(configurableApplicationContext.getEnvironment());
    }
  }

  @Test
  public void contextLoads() {
    assertTrue(true);
  }

}