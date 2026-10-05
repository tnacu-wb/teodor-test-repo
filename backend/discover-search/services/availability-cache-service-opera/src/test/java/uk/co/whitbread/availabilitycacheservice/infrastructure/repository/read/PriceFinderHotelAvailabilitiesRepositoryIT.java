package uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import uk.co.whitbread.availabilitycacheservice.AvailabilityCacheServiceApplication;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.pricefinder.PriceFinderResultSet;

@Disabled("Not compatible with Kaniko executor")
@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = {"increase-batch-size"})
@SpringBootTest(classes = {AvailabilityCacheServiceApplication.class},
    properties = {
        "spring.datasource.driver-class-name=org.postgresql.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.PostgreSQLDialect",
        "spring.jpa.hibernate.default_schema=avail_cache"
    })
@ContextConfiguration(initializers = {PriceFinderHotelAvailabilitiesRepositoryIT.Initializer.class})
@Slf4j
public class PriceFinderHotelAvailabilitiesRepositoryIT {

  public static JdbcDatabaseContainer postgreSQLContainer =
      new PostgreSQLContainer("postgres:11")
          .withInitScript("gqt_schema_v1.1.sql")
          .withDatabaseName("integration-tests-db")
          .withUsername("sa")
          .withPassword("sa");
  @Autowired
  private PriceFinderHotelAvailabilitiesRepository repository;

  @Test
  public void findAvailabilitiesForPriceFinderTest() {
    final List<String> operaHotels = new ArrayList<>();
    operaHotels.add("LONKIN");
    operaHotels.add("LONEUS");
    final LocalDate arrivalDate = LocalDate.parse("2025-12-12");
    final LocalDate departureDate = LocalDate.parse("2025-12-16");

    List<PriceFinderResultSet> priceFinderResultSets =
        repository.findAvailabilitiesForPriceFinder(operaHotels, arrivalDate, departureDate);

    assertNotNull(priceFinderResultSets);

    List<PriceFinderResultSet> priceFinderResultSetList =
        priceFinderResultSets.stream()
            .toList();
    assertFalse(priceFinderResultSetList.isEmpty());
    assertEquals(5, priceFinderResultSetList.size());
  }

  static class Initializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of(
          "spring.datasource.writer.jdbc-url=" + postgreSQLContainer.getJdbcUrl(),
          "spring.datasource.writer.username=" + postgreSQLContainer.getUsername(),
          "spring.datasource.writer.password=" + postgreSQLContainer.getPassword(),
          //reader
          "spring.datasource.reader.jdbc-url=" + postgreSQLContainer.getJdbcUrl(),
          "spring.datasource.reader.username=" + postgreSQLContainer.getUsername(),
          "spring.datasource.reader.password=" + postgreSQLContainer.getPassword(),
          //writer datasource
          "spring.datasource.writer.jdbc-url=" + postgreSQLContainer.getJdbcUrl(),
          "spring.datasource.writer.username=" + postgreSQLContainer.getUsername(),
          "spring.datasource.writer.password=" + postgreSQLContainer.getPassword()
      ).applyTo(configurableApplicationContext.getEnvironment());
    }
  }
}
