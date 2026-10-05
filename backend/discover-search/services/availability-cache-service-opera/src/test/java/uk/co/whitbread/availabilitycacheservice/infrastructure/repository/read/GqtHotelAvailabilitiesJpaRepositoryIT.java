package uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;

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
@ContextConfiguration(initializers = {GqtHotelAvailabilitiesJpaRepositoryIT.Initializer.class})
@Slf4j
public class GqtHotelAvailabilitiesJpaRepositoryIT {

  public static JdbcDatabaseContainer postgreSQLContainer =
      new PostgreSQLContainer("postgres:11")
          .withInitScript("gqt_schema_v1.1.sql")
          .withDatabaseName("integration-tests-db")
          .withUsername("sa")
          .withPassword("sa");
  @Autowired
  private GqtHotelAvailabilitiesJpaRepository gqtHotelAvailabilitiesJpaRepository;

  @Test
  public void findAvailabilitiesForGqtOperaTest() {
    final List<String> operaHotels = new ArrayList<>();
    operaHotels.add("MANOLD");
    operaHotels.add("LONEUS");
    operaHotels.add("IPSMTI");
    operaHotels.add("NEWTHY");
    final LocalDate arrivalDate = LocalDate.parse("2023-03-27");
    final LocalDate departureDate = LocalDate.parse("2023-03-30");

    List<HotelAvailabilitiesResultSet> availabilitiesResultSets =
        gqtHotelAvailabilitiesJpaRepository.
            findAvailabilitiesForGqtOpera(operaHotels, arrivalDate, departureDate);

    assertNotNull(availabilitiesResultSets);
    List<HotelAvailabilitiesResultSet> availabilitiesResultSetsOpera =
        availabilitiesResultSets.stream()
            .filter(hotelAvailabilitiesResultSet -> hotelAvailabilitiesResultSet.getPmsSource()
                .equals("OPERA"))
            .collect(Collectors.toList());
    assertFalse(availabilitiesResultSetsOpera.isEmpty());
    //total 8 records out of which 2 is having quantity as 0, so expected size is 8-2=6
    assertEquals(6, availabilitiesResultSetsOpera.size());
  }

  @Test
  public void noRecordsShouldBeFoundIfGivenHotelCodesAreNotThereInDb() {
    final List<String> operaHotels = new ArrayList<>();
    operaHotels.add("OXFORD");
    operaHotels.add("LONSLA");
    final LocalDate arrivalDate = LocalDate.parse("2023-01-01");
    final LocalDate departureDate = LocalDate.parse("2023-01-02");

    List<HotelAvailabilitiesResultSet> availabilitiesResultSets =
        gqtHotelAvailabilitiesJpaRepository.
            findAvailabilitiesForGqtOpera(operaHotels, arrivalDate, departureDate);

    assertNotNull(availabilitiesResultSets);
    assertTrue(availabilitiesResultSets.isEmpty());

  }

  @Test
  public void noRecordsShouldBeFoundWhenAvailabilitiesAreNotInGivenRange() {
    final List<String> operaHotels = new ArrayList<>();
    operaHotels.add("MANOLD");
    operaHotels.add("LONEUS");
    operaHotels.add("IPSMTI");
    operaHotels.add("NEWTHY");
    final LocalDate arrivalDate = LocalDate.parse("2023-04-01");
    final LocalDate departureDate = LocalDate.parse("2023-04-10");

    List<HotelAvailabilitiesResultSet> availabilitiesResultSets =
        gqtHotelAvailabilitiesJpaRepository.
            findAvailabilitiesForGqtOpera(operaHotels, arrivalDate, departureDate);

    assertNotNull(availabilitiesResultSets);
    assertTrue(availabilitiesResultSets.isEmpty());

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
