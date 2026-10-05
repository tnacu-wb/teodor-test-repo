package uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.DistributionHotelAvailResultSet;
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
@ContextConfiguration(initializers = {HotelAvailabilitiesJpaRepositoryIT.Initializer.class})
@Slf4j
public class HotelAvailabilitiesJpaRepositoryIT {

  public static JdbcDatabaseContainer postgreSQLContainer =
      new PostgreSQLContainer("postgres:11")
          .withInitScript("schema_v1.1.sql")
          .withDatabaseName("integration-tests-db")
          .withUsername("sa")
          .withPassword("sa");
  @Autowired
  private HotelAvailabilitiesJpaRepository hotelAvailabilitiesJpaRepository;

  @Test
  public void findAvailabilitiesForOperaTest() {
    final List<String> operaHotels = new ArrayList<>();
    operaHotels.add("OXFORD");
    final Set<String> operaRooms = new HashSet<>();
    operaRooms.add("SB");
    LocalDate arrivalDate = LocalDate.of(2023, 1, 1);
    LocalDate departureDate = LocalDate.of(2023, 1, 2);

    List<HotelAvailabilitiesResultSet> availabilitiesResultSets =
        hotelAvailabilitiesJpaRepository.findAvailabilitiesForOpera(operaHotels,
            LocalDate.parse("2023-01-01"), LocalDate.parse("2023-01-02"), operaRooms);
    assertNotNull(availabilitiesResultSets);
    List<HotelAvailabilitiesResultSet> availabilitiesResultSetsOpera =
        availabilitiesResultSets.stream()
            .filter(hotelAvailabilitiesResultSet -> hotelAvailabilitiesResultSet.getPmsSource().equals("OPERA"))
            .collect(Collectors.toList());
    log.info("availabilitiesResultSetsOpera - {}", availabilitiesResultSetsOpera);
    assertFalse(availabilitiesResultSetsOpera.isEmpty());

  }

  @Test
  public void findAvailabilitiesForDistributionTest() {
    //To test - Opera Hotel OXFORD and one roomType - SB and only fetch SB data alone
    final List<String> operaHotels = new ArrayList<>();
    operaHotels.add("OXFORD");
    final Set<String> roomTypeList = new HashSet<>();
    roomTypeList.add("SB");
    final Set<String> rateCodeList = new HashSet<>();
    rateCodeList.add("FLEXRATE");

    List<DistributionHotelAvailResultSet> availabilitiesResultSets =
        hotelAvailabilitiesJpaRepository.findAvailabilitiesForDistribution(operaHotels,
            LocalDate.parse("2023-01-01"), LocalDate.parse("2023-01-02"), roomTypeList, rateCodeList);
    assertNotNull(availabilitiesResultSets);
    List<DistributionHotelAvailResultSet> availabilitiesResultSetsDistribution =
        availabilitiesResultSets.stream()
            .filter(hotelAvailabilitiesResultSet -> hotelAvailabilitiesResultSet.getPmsSource().equals("OPERA"))
            .collect(Collectors.toList());
    log.info("availabilitiesResultSetsForDistribution - {}", availabilitiesResultSetsDistribution);
    assertFalse(availabilitiesResultSetsDistribution.isEmpty());
    assertEquals(3, availabilitiesResultSetsDistribution.size());

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
