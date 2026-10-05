package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.config.PostgresIntegrationTestConfig;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.entity.HotelMigrationStatusEntity;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.read.HotelMigrationStatusJpaRepository;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.repository.write.HotelMigrationStatusJpaRepositoryWriter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("Class not compatible with Kaniko")
@DirtiesContext
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ContextConfiguration(initializers = {PostgresIntegrationTestConfig.class})
public class HotelAvailabilityOnDemandRefreshControllerIT {

  private static final String OPERA_PMS_SRC = "OPERA";

  @Autowired
  private HotelMigrationStatusJpaRepository hotelMigrationRepo;

  @Autowired
  private HotelMigrationStatusJpaRepositoryWriter hmsJpaRepositoryWriter;

  private TestRestTemplate testRestTemplate;

  @LocalServerPort
  private int port;

  @BeforeEach
  public void setup() {
    testRestTemplate = new TestRestTemplate();
  }

  @Test
  public void badRequestWhenStartDateIsBeforeCurrent() {

    final String responseMessage = "Start date or End date can't be past !";

    final String startDate = LocalDate.now().minusDays(2).toString();

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/refresh/hotels")
        .queryParam("hotelIds", "TKINPT,MENOLD")
        .queryParam("startDate", startDate )
        .build().toUriString();

    final ResponseEntity<String> response =
        testRestTemplate.getForEntity(uri, String.class);

    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isEqualToIgnoringCase(responseMessage);

  }

  @Test
  public void badRequestWhenEqualStartAndEndDate() {

    final String responseMessage = "Start date or End date can't be past !";

    final String startDate = LocalDate.now().minusDays(2).toString();

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/refresh/hotels")
        .queryParam("hotelIds", "TKINPT,MENOLD")
        .queryParam("startDate", startDate )
        .queryParam("endDate", startDate)
        .build().toUriString();

    final ResponseEntity<String> response =
        testRestTemplate.getForEntity(uri, String.class);

    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isEqualToIgnoringCase(responseMessage);

  }

  @Test
  public void badRequestWhenStartDateGreaerThenEndTest() {

    final String responseMessage = "Given start date greater than end date !";

    final String startDate = LocalDate.now().minusDays(2).toString();
    final String endDate = LocalDate.now().minusDays(5).toString();

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/refresh/hotels")
        .queryParam("hotelIds", "TKINPT,MENOLD")
        .queryParam("startDate", startDate )
        .queryParam("endDate", endDate)
        .build().toUriString();

    final ResponseEntity<String> response =
        testRestTemplate.getForEntity(uri, String.class);

    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isEqualToIgnoringCase(responseMessage);

  }


  @Test
  public void shouldNotRunOnDemandIfAllHotelsAreBartNoDatesPassed() {

    final String responseMessage =
        "On demand refresh run for Opera hotels: TKINPT";

    hotelMigrationRepo.deleteAll();

    //Make sure that there is no OPERA  hotels in DB
    Set<HotelMigrationStatusEntity> hotelMigrationStatusEntitySet =
        hotelMigrationRepo.findByPmsSource(OPERA_PMS_SRC);

    assertThat(hotelMigrationStatusEntitySet).isNullOrEmpty();

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/refresh/hotels")
        .queryParam("hotelIds", "TKINPT,MENOLD")
        .build().toUriString();

    final ResponseEntity<String> response =
        testRestTemplate.getForEntity(uri, String.class);

    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PARTIAL_CONTENT);
    assertThat(response.getBody()).isEqualToIgnoringCase(responseMessage);

  }

  @Test
  public void shouldRunOnDemandForOperaHotelsNoStartEndDatePassed() {

    hmsJpaRepositoryWriter.deleteAll();

    final String onDemandJonRunMsg = "On demand refresh run for Opera hotels: PDUBAI,OXFORD";

    final HotelMigrationStatusEntity hotelMigrationStatusEntity1 =
        buildHotelMigrationStatusEntity("OXFORD", OPERA_PMS_SRC, true);

    final HotelMigrationStatusEntity hotelMigrationStatusEntity2 =
        buildHotelMigrationStatusEntity("PDUBAI", OPERA_PMS_SRC, true);

    final Set<HotelMigrationStatusEntity> hotelMigrationStatusEntitySetExpected =
        new HashSet<>();

    hotelMigrationStatusEntitySetExpected.add(hotelMigrationStatusEntity1);
    hotelMigrationStatusEntitySetExpected.add(hotelMigrationStatusEntity2);

    final List<String> hotelCodesSetExpected = hotelMigrationStatusEntitySetExpected.stream()
        .map(HotelMigrationStatusEntity::getHotelCode).collect(Collectors.toList());

    hotelMigrationRepo.save(hotelMigrationStatusEntity1);
    hotelMigrationRepo.save(hotelMigrationStatusEntity2);

    //Make sure that there are OPERA  hotels in DB
    final Set<HotelMigrationStatusEntity> hotelMigrationStatusEntitySet =
        hotelMigrationRepo.findByPmsSource(OPERA_PMS_SRC);

    final List<String> hotelCodesSetActual = hotelMigrationStatusEntitySet.stream()
        .map(hotelMigrationStatusEntity -> hotelMigrationStatusEntity.getHotelCode())
        .collect(Collectors.toList());

    assertThat(hotelMigrationStatusEntitySet).isNotNull();
    assertEquals(hotelMigrationStatusEntitySetExpected.size(),
        hotelMigrationStatusEntitySet.size());
    assertTrue(hotelCodesSetExpected.containsAll(hotelCodesSetActual));

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/refresh/hotels")
        .queryParam("hotelIds", "OXFORD,PDUBAI")
        .build().toUriString();

    final ResponseEntity<String> response =
        testRestTemplate.getForEntity(uri, String.class);

    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    assertThat(response.getBody()).isEqualToIgnoringCase(onDemandJonRunMsg);

  }

  @Test
  public void shouldRunOnDemandForOperaHotelsValidStartEndDatePassed() {

    final String startDate = LocalDate.now().plusDays(2).toString();
    final String endDate = LocalDate.now().plusMonths(1).toString();

    final String onDemandJonRunMsg = "On demand refresh run for Opera hotels: PDUBAI,OXFORD";

    final HotelMigrationStatusEntity hotelMigrationStatusEntity1 =
        buildHotelMigrationStatusEntity("OXFORD", OPERA_PMS_SRC, true);

    final HotelMigrationStatusEntity hotelMigrationStatusEntity2 =
        buildHotelMigrationStatusEntity("PDUBAI", OPERA_PMS_SRC, true);

    Set<HotelMigrationStatusEntity> hotelMigrationStatusEntitySetExpected =
        new HashSet<>();

    hotelMigrationStatusEntitySetExpected.add(hotelMigrationStatusEntity1);
    hotelMigrationStatusEntitySetExpected.add(hotelMigrationStatusEntity2);

    final List<String> hotelCodesSetExpected = hotelMigrationStatusEntitySetExpected.stream()
        .map(HotelMigrationStatusEntity::getHotelCode).collect(Collectors.toList());

    hotelMigrationRepo.save(hotelMigrationStatusEntity1);
    hotelMigrationRepo.save(hotelMigrationStatusEntity2);

    //Make sure that there are OPERA  hotels in DB
    final Set<HotelMigrationStatusEntity> hotelMigrationStatusEntitySet =
        hotelMigrationRepo.findByPmsSource(OPERA_PMS_SRC);

    List<String> hotelCodesSetActual = hotelMigrationStatusEntitySet.stream()
        .map(hotelMigrationStatusEntity -> hotelMigrationStatusEntity.getHotelCode())
        .collect(Collectors.toList());

    assertThat(hotelMigrationStatusEntitySet).isNotNull();
    assertEquals(hotelMigrationStatusEntitySetExpected.size(),
        hotelMigrationStatusEntitySet.size());
    assertTrue(hotelCodesSetExpected.containsAll(hotelCodesSetActual));

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/refresh/hotels")
        .queryParam("hotelIds", "OXFORD,PDUBAI")
        .queryParam("startDate", startDate)
        .queryParam("endDate", endDate)
        .build().toUriString();

    final ResponseEntity<String> response =
        testRestTemplate.getForEntity(uri, String.class);

    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    assertThat(response.getBody()).isEqualToIgnoringCase(onDemandJonRunMsg);

  }

  @Test
  public void shouldNotRunOnDemandIfAllHotelsAreBartValidStartDateTest() {

    final String startDate = LocalDate.now().plusDays(2).toString();

    final String responseMessage =
        "On demand refresh run for Opera hotels: TKINPT";

    hotelMigrationRepo.deleteAll();

    //Make sure that there is no OPERA  hotels in DB
    Set<HotelMigrationStatusEntity> hotelMigrationStatusEntitySet =
        hotelMigrationRepo.findByPmsSource(OPERA_PMS_SRC);

    assertThat(hotelMigrationStatusEntitySet).isNullOrEmpty();

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/refresh/hotels")
        .queryParam("hotelIds", "TKINPT,MENOLD")
        .queryParam("startDate", startDate)
        .build().toUriString();

    final ResponseEntity<String> response =
        testRestTemplate.getForEntity(uri, String.class);

    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PARTIAL_CONTENT);
    assertThat(response.getBody()).isEqualToIgnoringCase(responseMessage);

  }

  @Test
  public void shouldNotRunOnDemandIfAllHotelsAreBartValidStartEndDateTest() {

    final String startDate = LocalDate.now().plusDays(2).toString();
    final String endDate = LocalDate.now().plusMonths(1).toString();

    final String responseMessage =
        "On demand refresh run for Opera hotels: TKINPT";

    hotelMigrationRepo.deleteAll();

    //Make sure that there is no OPERA  hotels in DB
    Set<HotelMigrationStatusEntity> hotelMigrationStatusEntitySet =
        hotelMigrationRepo.findByPmsSource(OPERA_PMS_SRC);

    assertThat(hotelMigrationStatusEntitySet).isNullOrEmpty();

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/refresh/hotels")
        .queryParam("hotelIds", "TKINPT,MENOLD")
        .queryParam("startDate", startDate)
        .queryParam("endDate", endDate)
        .build().toUriString();

    final ResponseEntity<String> response =
        testRestTemplate.getForEntity(uri, String.class);

    assertThat(response).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PARTIAL_CONTENT);
    assertThat(response.getBody()).isEqualToIgnoringCase(responseMessage);

  }

  private HotelMigrationStatusEntity buildHotelMigrationStatusEntity(
      final String hotelCode, final String pmsSource, final boolean onSale) {
    return HotelMigrationStatusEntity.builder()
        .hotelCode(hotelCode)
        .pmsSource(pmsSource)
        .onSale(onSale)
        .updatedOn(LocalDateTime.now())
        .build();
  }

}
