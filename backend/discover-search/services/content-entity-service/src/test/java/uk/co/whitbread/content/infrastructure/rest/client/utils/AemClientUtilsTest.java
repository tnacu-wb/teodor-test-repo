package uk.co.whitbread.content.infrastructure.rest.client.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.co.whitbread.content.infrastructure.rest.client.utils.AemClientUtils.getAppsHomepageEndpoint;
import static uk.co.whitbread.content.infrastructure.rest.client.utils.AemClientUtils.getPageDlpUriPath;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemDlpProperties;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemHomepageProperties;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemProperties;
import uk.co.whitbread.content.infrastructure.rest.client.content.exceptions.ContentException;

class AemClientUtilsTest {

  private static AemProperties aemProperties;
  private static AemDlpProperties aemDlpProperties;
  private static AemHomepageProperties aemHomepeAppsproperties;
  private static String homepageFormat = "/content/premier-inn/%s-%s/%s/apps/home.model.json";

  @BeforeAll
  public static void setUpAemProperties() {
    var countryParam = "countryParam";
    var languageParam = "languageParam";
    var dlpPathParam = "dlpPathParam";
    var pathParam = "pathParam";
    var subchannelParam = "subchannelParam";
    aemProperties = new AemProperties();
    aemProperties.setPageDlpEndpoint(String.format(
        "/%s/%s/content-service.page-dlp.detail/path/%s.json",
        countryParam, languageParam, dlpPathParam));
    aemProperties.setAppsHomepageEndPoint(
        String.format(homepageFormat, countryParam, languageParam, pathParam));
    aemDlpProperties = new AemDlpProperties();
    aemDlpProperties.setCountryParam(countryParam);
    aemDlpProperties.setLanguageParam(languageParam);
    aemDlpProperties.setDlpPathParam(dlpPathParam);
    aemHomepeAppsproperties = new AemHomepageProperties();
    aemHomepeAppsproperties.setCountryParam(countryParam);
    aemHomepeAppsproperties.setLanguageParam(languageParam);
    aemHomepeAppsproperties.setPathParam(pathParam);
    aemHomepeAppsproperties.setSubchannelParam(subchannelParam);
  }

  @ParameterizedTest
  @ValueSource(strings = {"/england/bedfordshire/luton", "/england/bedfordshire", "/england",
      "england/bedfordshire/luton", "england/bedfordshire", "england"})
  void testGetPageDlpUriCountry_success(String dlpPath) {
    var uriPath = getPageDlpUriPath(dlpPath, "gb", "en", aemProperties, aemDlpProperties);
    var expectedUri = "/gb/en/content-service.page-dlp.detail/path/" + dlpPath + ".json";
    assertEquals(expectedUri, uriPath);
  }

  @ParameterizedTest
  @CsvSource({
      "pi, apps, en, gb, premier-inn",
      "PI, APPS, en, gb, premier-inn",
      "PI, apps, de, de, premier-inn",
      "bb, apps, en, gb, business-booker",
      "BB, apps, en, gb, business-booker",
      "BB, apps, de, de, business-booker",
      "pi, web, en, gb, premier-inn",
      "PI, WEB, en, gb, premier-inn",
      "PI, web, de, de, premier-inn",
      "bb, web, en, gb, business-booker",
      "BB, web, en, gb, business-booker",
      "BB, web, de, de, business-booker"})
  void testGetHomepageApps_success(String channel, String subchannel, String language,
      String country, String path) {
    var uriPath = getAppsHomepageEndpoint(channel, subchannel, country, language, aemProperties,
        aemHomepeAppsproperties);
    var expectedUri = String.format(homepageFormat, country, language, path);
    assertEquals(expectedUri, uriPath);
  }

  @ParameterizedTest
  @CsvSource({"test, apps, en, gb, No AEM channel for: test",
      "test, apps, de, de, No AEM channel for: test"})
  void testGetHomepageApps_failed(String channel, String subchannel, String language,
      String country, String message) {
    var exc = assertThrows(ContentException.class, () ->
        getAppsHomepageEndpoint(channel, subchannel, country, language, aemProperties,
            aemHomepeAppsproperties));
    assertThat(exc.getMessage()).isEqualTo(message);
  }

  @Test
  void testGetHomepageSubchannel_failed() {
    var exc = assertThrows(ContentException.class, () ->
        getAppsHomepageEndpoint("PI", "subchannel", "gb", "en", aemProperties,
            aemHomepeAppsproperties));
    assertThat(exc.getMessage()).isEqualTo("No AEM subchannel for: subchannel");
  }
}