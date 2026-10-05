package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.PostgresIntegrationTestConfig;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelLocationEntity;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.LocationPriceEntity;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelLocationJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.LocationPriceJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.LocationPriceResponse;

@Disabled("Not compatible with Kaniko executor")

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {PostgresIntegrationTestConfig.class})
@ActiveProfiles(profiles = {"increase-batch-size"})
@AutoConfigureTestRestTemplate
class LocationPriceControllerIT {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private HotelLocationJpaRepository hotelLocationJpaRepository;

  @Autowired
  private LocationPriceJpaRepository locationPriceJpaRepository;

  @AfterEach
  void clearRecords() {
    hotelLocationJpaRepository.deleteAll();
    locationPriceJpaRepository.deleteAll();
  }

  @Test
  void getBestPricedHotelsSuccessTest() {

    final LocalDate date1 = LocalDate.now().plusDays(3);
    final LocalDate date2 = LocalDate.now().plusDays(4);
    final String startDate = date1.minusDays(1).toString();
    final String endDate = date2.plusDays(1).toString();

    final HotelLocationEntity hotelLocationEntity1 =
        buildHotelLocationEntity("MANOLD", "testPlaceId1");
    final HotelLocationEntity hotelLocationEntity2 =
        buildHotelLocationEntity("OXFORD", "testPlaceId2");

    hotelLocationJpaRepository.save(hotelLocationEntity1);
    hotelLocationJpaRepository.save(hotelLocationEntity2);

    List<HotelLocationEntity> hotelLocationEntityList = hotelLocationJpaRepository.findAll();
    assertEquals(2, hotelLocationEntityList.size());

    final LocationPriceEntity locationPriceEntity1 =
        buildLocationPriceEntity("testPlaceId1", date1, "5.5", "G");
    final LocationPriceEntity locationPriceEntity2 =
        buildLocationPriceEntity("testPlaceId2", date2, "6.5", "G");

    locationPriceJpaRepository.save(locationPriceEntity1);
    locationPriceJpaRepository.save(locationPriceEntity2);

    List<LocationPriceEntity> locationPriceEntityList = locationPriceJpaRepository.findAll();
    assertEquals(2, locationPriceEntityList.size());

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/search/locations/prices")
        .queryParam("startDate", startDate)
        .queryParam("endDate", endDate)
        .queryParam("priceThreshold", 7.5)
        .queryParam("altPriceThreshold", 10.5)
        .build().toUriString();

    final ResponseEntity<LocationPriceResponse> response =
        testRestTemplate.getForEntity(uri, LocationPriceResponse.class);

    assertThat(response).isNotNull();
    assertEquals(response.getStatusCode(), HttpStatus.OK);
    assertEquals(2, response.getBody().getBestPricedHotels().size());
  }

  @Test
  void emptyListIfAppropriateRecordNotInDbTest() {

    final LocalDate date1 = LocalDate.now().plusDays(3);
    final LocalDate date2 = LocalDate.now().plusDays(4);
    final String startDate = date1.minusDays(1).toString();
    final String endDate = date2.plusDays(1).toString();

    final HotelLocationEntity hotelLocationEntity1 =
        buildHotelLocationEntity("PDUBAI", "testPlaceId3");
    final HotelLocationEntity hotelLocationEntity2 =
        buildHotelLocationEntity("DDUBAI", "testPlaceId4");

    hotelLocationJpaRepository.save(hotelLocationEntity1);
    hotelLocationJpaRepository.save(hotelLocationEntity2);

    List<HotelLocationEntity> hotelLocationEntityList = hotelLocationJpaRepository.findAll();
    assertEquals(2, hotelLocationEntityList.size());

    //make sure that there is no corresponding record in location price
    locationPriceJpaRepository.deleteAll();

    List<LocationPriceEntity> locationPriceEntityList = locationPriceJpaRepository.findAll();
    assertEquals(0, locationPriceEntityList.size());

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/search/locations/prices")
        .queryParam("startDate", startDate)
        .queryParam("endDate", endDate)
        .queryParam("priceThreshold", 7.5)
        .queryParam("altPriceThreshold", 10.5)
        .build().toUriString();

    final ResponseEntity<LocationPriceResponse> response =
        testRestTemplate.getForEntity(uri, LocationPriceResponse.class);

    assertThat(response).isNotNull();
    assertEquals(response.getStatusCode(), HttpStatus.OK);
    assertEquals(0, response.getBody().getBestPricedHotels().size());
  }


  @Test
  void badRequestWhenStartAndEndDateAreSameTest() {

    final String startDate = LocalDate.now().plusDays(3).toString();
    final String endDate = startDate;

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/search/locations/prices")
        .queryParam("startDate", startDate)
        .queryParam("endDate", endDate)
        .queryParam("priceThreshold", 7.5)
        .queryParam("altPriceThreshold", 10.5)
        .build().toUriString();

    final ResponseEntity<LocationPriceResponse> response =
        testRestTemplate.getForEntity(uri, LocationPriceResponse.class);

    assertThat(response).isNotNull();
    assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  @Test
  void badRequestWhenStartDateIsPastDateTest() {

    final String startDate = LocalDate.now().minusDays(2).toString();
    final String endDate = LocalDate.now().plusDays(5).toString();

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/search/locations/prices")
        .queryParam("startDate", startDate)
        .queryParam("endDate", endDate)
        .queryParam("priceThreshold", 7.5)
        .queryParam("altPriceThreshold", 10.5)
        .build().toUriString();

    final ResponseEntity<LocationPriceResponse> response =
        testRestTemplate.getForEntity(uri, LocationPriceResponse.class);

    assertThat(response).isNotNull();
    assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  @Test
  void badRequestWhenEndDateIsBeforeStartDateTest() {

    final String startDate = LocalDate.now().plusDays(5).toString();
    final String endDate = LocalDate.now().plusDays(3).toString();

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/search/locations/prices")
        .queryParam("startDate", startDate)
        .queryParam("endDate", endDate)
        .queryParam("priceThreshold", 7.5)
        .queryParam("altPriceThreshold", 10.5)
        .build().toUriString();

    final ResponseEntity<LocationPriceResponse> response =
        testRestTemplate.getForEntity(uri, LocationPriceResponse.class);

    assertThat(response).isNotNull();
    assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  private LocationPriceEntity buildLocationPriceEntity(
      final String placeId, final LocalDate date, final String price, String currency) {
    return LocationPriceEntity.builder()
        .placeId(placeId)
        .date(date)
        .price(new BigDecimal(price))
        .currency(currency)
        .build();
  }

  private HotelLocationEntity buildHotelLocationEntity(
      final String hotelCode, final String placeId) {
    return HotelLocationEntity.builder()
        .hotelCode(hotelCode)
        .placeId(placeId)
        .build();
  }

}
