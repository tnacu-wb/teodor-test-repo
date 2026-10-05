package uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.co.whitbread.availabilitycacheservice.AvailabilityCacheServiceApplication;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.DistributionHotelAvailResultSet;

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
@ContextConfiguration(initializers = {HotelAvailabilitiesJpaRepositoryForDistributionIT.Initializer.class})
@Testcontainers
@Slf4j
public class HotelAvailabilitiesJpaRepositoryForDistributionIT {

  @Container
  public static JdbcDatabaseContainer postgreSQLContainer =
      new PostgreSQLContainer("postgres:11")
          .withInitScript("schema_v1.2.sql")
          .withDatabaseName("integration-tests-db")
          .withUsername("sa")
          .withPassword("sa");
  @Autowired
  private HotelAvailabilitiesJpaRepository hotelAvailabilitiesJpaRepository;

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
    assertEquals(4, availabilitiesResultSetsDistribution.size());

  }

  @Test
  public void findAvailabilitiesForDistributionWithEmptyResultsForQtyZeroTest() {
    //To test - Opera Hotel OXFORD and one roomType - FAM which has quantity as 0
    final List<String> operaHotels = new ArrayList<>();
    operaHotels.add("OXFORD");
    final Set<String> roomTypeList = new HashSet<>();
    roomTypeList.add("FAM");
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
    assertTrue(availabilitiesResultSetsDistribution.isEmpty());
    assertEquals(0, availabilitiesResultSetsDistribution.size());

  }

  @Test
  public void findAvailabilitiesForDistributionWithEmptyResultsNotPresentInDbTest() {
    //To test - Opera Hotel OXFORD and one roomType - DIS which is not present in DB
    final List<String> operaHotels = new ArrayList<>();
    operaHotels.add("OXFORD");
    final Set<String> roomTypeList = new HashSet<>();
    roomTypeList.add("DIS");
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
    assertTrue(availabilitiesResultSetsDistribution.isEmpty());
    assertEquals(0, availabilitiesResultSetsDistribution.size());

  }

  @Test
  public void findAvailabilitiesForDistributionWithMultiRoomsTest() {
    //To test - Opera Hotel OXFORD and with multi roomType - FAM,SB which has quantities as 0,10
    final List<String> operaHotels = new ArrayList<>();
    operaHotels.add("OXFORD");
    final Set<String> roomTypeList = new HashSet<>();
    roomTypeList.add("FAM");
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
    assertEquals(4, availabilitiesResultSetsDistribution.size());

  }

  @Test
  public void findAvailabilitiesForDistributionWithOperaHotelsRoomsTest() {
    final List<String> operaHotels = new ArrayList<>();
    operaHotels.add("OXFORD");
    operaHotels.add("LONSLA");
    final Set<String> roomTypeList = new HashSet<>();
    roomTypeList.add("FAM");
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

    List<DistributionHotelAvailResultSet> availabilitiesResultSetsDistributionLonsla =
        availabilitiesResultSets.stream()
            .filter(hotelAvailabilitiesResultSet -> hotelAvailabilitiesResultSet.getHotelCode().equals("LONSLA"))
            .collect(Collectors.toList());
    log.info("availabilitiesResultSetsForDistribution - {}", availabilitiesResultSetsDistribution);
    assertFalse(availabilitiesResultSetsDistribution.isEmpty());
    assertTrue(availabilitiesResultSetsDistributionLonsla.isEmpty());
    assertEquals(4, availabilitiesResultSetsDistribution.size());

  }

  @Test
  public void findAvailabilitiesForDistributionWithMultiRoomsWithQtyTest() {
    //DB and SB has quantity >0 and FAM has 0 quantity
    final List<String> operaHotels = new ArrayList<>();
    operaHotels.add("OXFORD");
    final Set<String> roomTypeList = new HashSet<>();
    roomTypeList.add("DB");
    roomTypeList.add("SB");
    roomTypeList.add("FAM");
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
    assertEquals(7, availabilitiesResultSetsDistribution.size());
    assertEquals(3, availabilitiesResultSetsDistribution.stream()
        .filter(hotelAvailabilitiesResultSet -> hotelAvailabilitiesResultSet.getRoomType().equals("DB"))
        .count());
    assertEquals(0, availabilitiesResultSetsDistribution.stream()
        .filter(hotelAvailabilitiesResultSet -> hotelAvailabilitiesResultSet.getRoomType().equals("FAM"))
        .count());
    assertEquals(4, availabilitiesResultSetsDistribution.stream()
        .filter(hotelAvailabilitiesResultSet -> hotelAvailabilitiesResultSet.getRoomType().equals("SB"))
        .count());

  }

  @Test
  public void findAvailabilitiesForDistributionWithMultiDatesTest() {
    //DB and SB has quantity >0 and FAM has 0 quantity and for 2 days
    final List<String> operaHotels = new ArrayList<>();
    operaHotels.add("OXFORD");
    final Set<String> roomTypeList = new HashSet<>();
    roomTypeList.add("DB");
    roomTypeList.add("SB");
    roomTypeList.add("FAM");
    final Set<String> rateCodeList = new HashSet<>();
    rateCodeList.add("FLEXRATE");

    List<DistributionHotelAvailResultSet> availabilitiesResultSets =
        hotelAvailabilitiesJpaRepository.findAvailabilitiesForDistribution(operaHotels,
            LocalDate.parse("2023-01-01"), LocalDate.parse("2023-01-03"), roomTypeList, rateCodeList);
    assertNotNull(availabilitiesResultSets);
    List<DistributionHotelAvailResultSet> availabilitiesResultSetsDistribution =
        availabilitiesResultSets.stream()
            .filter(hotelAvailabilitiesResultSet -> hotelAvailabilitiesResultSet.getPmsSource().equals("OPERA"))
            .collect(Collectors.toList());

    log.info("availabilitiesResultSetsForDistribution - {}", availabilitiesResultSetsDistribution);
    assertFalse(availabilitiesResultSetsDistribution.isEmpty());
    assertEquals(14, availabilitiesResultSetsDistribution.size());
    assertEquals(6, availabilitiesResultSetsDistribution.stream()
        .filter(hotelAvailabilitiesResultSet -> hotelAvailabilitiesResultSet.getRoomType().equals("DB"))
        .count());
    assertEquals(0, availabilitiesResultSetsDistribution.stream()
        .filter(hotelAvailabilitiesResultSet -> hotelAvailabilitiesResultSet.getRoomType().equals("FAM"))
        .count());
    assertEquals(8, availabilitiesResultSetsDistribution.stream()
        .filter(hotelAvailabilitiesResultSet -> hotelAvailabilitiesResultSet.getRoomType().equals("SB"))
        .count());

  }

  @Test
  public void findAvailabilitiesForDistributionWithMultiDatesNotForAllDaysTest() {
    //DB and SB has quantity >0 and FAM has 0 quantity and for 3 days and Db has it for 2 days alone
    final List<String> operaHotels = new ArrayList<>();
    operaHotels.add("OXFORD");
    final Set<String> roomTypeList = new HashSet<>();
    roomTypeList.add("DB");
    roomTypeList.add("SB");
    roomTypeList.add("FAM");
    final Set<String> rateCodeList = new HashSet<>();
    rateCodeList.add("FLEXRATE");

    List<DistributionHotelAvailResultSet> availabilitiesResultSets =
        hotelAvailabilitiesJpaRepository.findAvailabilitiesForDistribution(operaHotels,
            LocalDate.parse("2023-01-01"), LocalDate.parse("2023-01-04"), roomTypeList, rateCodeList);
    assertNotNull(availabilitiesResultSets);
    List<DistributionHotelAvailResultSet> availabilitiesResultSetsDistribution =
        availabilitiesResultSets.stream()
            .filter(hotelAvailabilitiesResultSet -> hotelAvailabilitiesResultSet.getPmsSource().equals("OPERA"))
            .collect(Collectors.toList());

    log.info("availabilitiesResultSetsForDistribution - {}", availabilitiesResultSetsDistribution);
    assertFalse(availabilitiesResultSetsDistribution.isEmpty());
    assertFalse(availabilitiesResultSetsDistribution.isEmpty());
    assertEquals(14, availabilitiesResultSetsDistribution.size());
    assertEquals(0, availabilitiesResultSetsDistribution.stream()
        .filter(hotelAvailabilitiesResultSet -> hotelAvailabilitiesResultSet.getAvailableDate()
            .equals(LocalDate.parse("2023-01-03")))
        .count());
    assertEquals(6, availabilitiesResultSetsDistribution.stream()
        .filter(hotelAvailabilitiesResultSet -> hotelAvailabilitiesResultSet.getRoomType().equals("DB"))
        .count());
    assertEquals(0, availabilitiesResultSetsDistribution.stream()
        .filter(hotelAvailabilitiesResultSet -> hotelAvailabilitiesResultSet.getRoomType().equals("FAM"))
        .count());
    assertEquals(8, availabilitiesResultSetsDistribution.stream()
        .filter(hotelAvailabilitiesResultSet -> hotelAvailabilitiesResultSet.getRoomType().equals("SB"))
        .count());


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
