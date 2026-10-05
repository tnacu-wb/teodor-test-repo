package uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.availabilitycacheservice.AvailabilityCacheServiceApplication;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelMigrationStatusEntity;

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
//@ContextConfiguration(initializers = {HotelMigrationStatusReaderRepositoryIT.Initializer.class})

class HotelMigrationStatusReaderRepositoryIT {

  @Autowired
  private HotelMigrationStatusReaderRepository hotelMigrationStatusReaderRepository;

//  public static JdbcDatabaseContainer postgreSQLContainer =
//      new PostgreSQLContainer("postgres:11")
//          .withInitScript("schema.sql")
//          .withDatabaseName("integration-tests-db")
//          .withUsername("sa")
//          .withPassword("sa");
//
//  static class Initializer
//      implements ApplicationContextInitializer<ConfigurableApplicationContext> {
//
//    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
//      TestPropertyValues.of(
//          "spring.datasource.writer.jdbc-url=" + postgreSQLContainer.getJdbcUrl(),
//          "spring.datasource.writer.username=" + postgreSQLContainer.getUsername(),
//          "spring.datasource.writer.password=" + postgreSQLContainer.getPassword(),
//          //reader
//          "spring.datasource.reader.jdbc-url=" + postgreSQLContainer.getJdbcUrl(),
//          "spring.datasource.reader.username=" + postgreSQLContainer.getUsername(),
//          "spring.datasource.reader.password=" + postgreSQLContainer.getPassword(),
//          //writer datasource
//          "spring.datasource.writer.jdbc-url=" + postgreSQLContainer.getJdbcUrl(),
//          "spring.datasource.writer.username=" + postgreSQLContainer.getUsername(),
//          "spring.datasource.writer.password=" + postgreSQLContainer.getPassword()
//      ).applyTo(configurableApplicationContext.getEnvironment());
//    }
//  }

  @Test
  void getMigrationStatusForHotelsTest() {
    List<HotelMigrationStatusEntity> hotelMigEntities = hotelMigrationStatusReaderRepository
        .getMigrationStatusForHotels(Arrays.asList("LONCLA", "LONBAN", "OXFORD"));
    assertEquals(3, hotelMigEntities.size());
    assertEquals(1, hotelMigEntities.stream().filter(
            hotelMigrationStatusEntity2 ->
                hotelMigrationStatusEntity2.getPmsSource().equals("OPERA")).toList().size());
  }

  @Test
  void getMigrationStatusForHotelsTestWithAllHotelsPresent() {
    List<HotelMigrationStatusEntity> hotelMigEntities = hotelMigrationStatusReaderRepository
        .getMigrationStatusForHotels(Arrays.asList("LONHAM", "LONHOL"));
    assertEquals(2, hotelMigEntities.size());
    assertEquals(1, hotelMigEntities.stream().filter(
            hotelMigrationStatusEntity2 ->
                hotelMigrationStatusEntity2.getPmsSource().equals("OPERA")).toList().size());
  }


  @Test
  void getMigrationStatusForHotelsTestWithEmptyHotels() {

    List<HotelMigrationStatusEntity> hotelMigEntities = hotelMigrationStatusReaderRepository
        .getMigrationStatusForHotels(Collections.emptyList());
    assertTrue(hotelMigEntities.isEmpty());
  }


}
