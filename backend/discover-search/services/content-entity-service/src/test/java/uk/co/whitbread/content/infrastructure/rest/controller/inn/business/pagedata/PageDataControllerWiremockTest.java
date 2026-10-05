package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.pagedata;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.Arrays;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.pagedata.model.in.DictionaryEnumDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.pagedata.model.in.PageDataRequestDto;

@EnableWireMock
@ConfigureWireMock(name = "wmPageDataControllerServer", port = 8080)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class PageDataControllerWiremockTest {

  private static final String COUNTRY_GB = "gb";
  private static final String LANGUAGE_EN = "en";
  private static final String LANGUAGE_DE = "de";
  private static final String EN_HOME_RESPONSE = "{"
        + "\"home.innbusinessPay.upcomingBookings.viewAllBookings.link\": \"View all bookings\","
        + "\"home.innbusinessPay.upcomingBookings.heading\": \"Upcoming bookings\"}";
  private static final String EN_SPENDING_REPORTING_RESPONSE = "{"
        + "\"spending.reporting.subheading\": \"Spent this month\","
        + "\"management.info.report.choose.dates\": \"Choose reporting dates\"}";
  private static final String DE_HOME_RESPONSE = "{"
        + "\"home.innbusinessPay.upcomingBookings.viewAllBookings.link\": \"Bevorstehende Buchungen\","
        + "\"home.innbusinessPay.upcomingBookings.heading\": \"Anträge\"}";
  private static final String DE_SPENDING_REPORTING_RESPONSE = "{"
        + "\"spending.reporting.subheading\": \"Ausgaben in diesem Monat\","
        + "\"management.info.report.choose.dates\": \"Berichtszeitraum auswählen\"}";

  @Autowired
  private PageDataController pageDataController;

  @InjectWireMock("wmPageDataControllerServer")
  WireMockServer wm;

  @BeforeEach
  void init() {
    wm.stubFor(get("/etc/designs/global/dictionaries/innbusiness/home/i18n.jsondict.en")
          .willReturn(aResponse()
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withStatus(200)
                .withBody(EN_HOME_RESPONSE)));
    wm.stubFor(get("/etc/designs/global/dictionaries/innbusiness/spending-reporting/i18n.jsondict.en")
          .willReturn(aResponse()
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withStatus(200)
                .withBody(EN_SPENDING_REPORTING_RESPONSE)));
    wm.stubFor(get("/etc/designs/global/dictionaries/innbusiness/home/i18n.jsondict.de")
          .willReturn(aResponse()
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withStatus(200)
                .withBody(DE_HOME_RESPONSE)));
    wm.stubFor(get("/etc/designs/global/dictionaries/innbusiness/spending-reporting/i18n.jsondict.de")
          .willReturn(aResponse()
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withStatus(200)
                .withBody(DE_SPENDING_REPORTING_RESPONSE)));
    wm.start();
  }

  @AfterEach
  void shutDown() {
    wm.stop();
  }

  @Test
  void getPageData_WhenHomepageAndReportingCatalogEnglishRequested_ThenCorrectResponse() {
    var result = pageDataController.getPageData(PageDataRequestDto.builder()
          .dictionaries(Arrays.asList(
                DictionaryEnumDto.HOMEPAGE_DICTIONARY,
                DictionaryEnumDto.SPENDING_REPORTING_DICTIONARY))
          .country(COUNTRY_GB)
          .language(LANGUAGE_EN)
          .build());

    assertEquals(2, result.size());
    assertThat(result, IsMapContaining.hasKey("homepageEndpoint"));
    assertThat(result, IsMapContaining.hasKey("spendingReportingEndpoint"));
    assertEquals("View all bookings", result.get("homepageEndpoint").get("home.innbusinessPay.upcomingBookings.viewAllBookings.link"));
    assertEquals("Upcoming bookings", result.get("homepageEndpoint").get("home.innbusinessPay.upcomingBookings.heading"));
    assertEquals("Spent this month", result.get("spendingReportingEndpoint").get("spending.reporting.subheading"));
    assertEquals("Choose reporting dates", result.get("spendingReportingEndpoint").get("management.info.report.choose.dates"));
  }

  @Test
  void getPageData_WhenHomepageAndReportingCatalogGermanRequested_ThenCorrectResponse() {
    var result = pageDataController.getPageData(PageDataRequestDto.builder()
          .dictionaries(Arrays.asList(
                DictionaryEnumDto.HOMEPAGE_DICTIONARY,
                DictionaryEnumDto.SPENDING_REPORTING_DICTIONARY))
          .country(COUNTRY_GB)
          .language(LANGUAGE_DE)
          .build());

    assertEquals(2, result.size());
    assertThat(result, IsMapContaining.hasKey("homepageEndpoint"));
    assertThat(result, IsMapContaining.hasKey("spendingReportingEndpoint"));
    assertEquals("Bevorstehende Buchungen", result.get("homepageEndpoint").get("home.innbusinessPay.upcomingBookings.viewAllBookings.link"));
    assertEquals("Anträge", result.get("homepageEndpoint").get("home.innbusinessPay.upcomingBookings.heading"));
    assertEquals("Ausgaben in diesem Monat", result.get("spendingReportingEndpoint").get("spending.reporting.subheading"));
    assertEquals("Berichtszeitraum auswählen", result.get("spendingReportingEndpoint").get("management.info.report.choose.dates"));
  }
}
