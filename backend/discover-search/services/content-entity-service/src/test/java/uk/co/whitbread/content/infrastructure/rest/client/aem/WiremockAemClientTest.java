package uk.co.whitbread.content.infrastructure.rest.client.aem;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.InjectWireMock;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.apps.homepage.in.AppsHomepageRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.out.HotelInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.out.IndexHeaderDataRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemDlpProperties;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemHomepageProperties;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemProperties;
import uk.co.whitbread.content.infrastructure.rest.client.content.aem.adapter.AemClient;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.CategoryEnumDto;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.LabelsRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.LocalizationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.MultipleLabelsRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.adapter.CardManagementAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.model.out.CardManagementRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.model.out.CommonIconsRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.adapter.HeaderAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.out.HeaderRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.out.LayoutRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.aem.PageDataAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.model.DictionaryEnumDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.model.PageDataRequestDto;


@ExtendWith(MockitoExtension.class)
@ConfigureWireMock(name = "wmAemServer", port = 8080)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockAemClientTest {

  @Mock
  private AemProperties aemProperties;
  @Mock
  private AemDlpProperties aemDlpProperties;
  @Mock
  private AemHomepageProperties aemHomepageAppsProperties;
  @InjectWireMock("wmAemServer")
  WireMockServer wm;
  private final String path = "http://localhost:8080";
  private final WebClient webClient = WebClient.create(path);
  private final IndexHeaderDataRequestDto indexHeaderDto = IndexHeaderDataRequestDto.builder()
      .country("UK")
      .language("en")
      .businessBooker(null).build();
  private final LabelsRequestDto labelReq = LabelsRequestDto.builder()
      .category(CategoryEnumDto.MAIN)
      .country("UK")
      .language("en").build();
  private final LocalizationRequestDto localReq = LocalizationRequestDto.builder().country("UK")
      .language("en")
      .build();
  private final HotelInformationDto hotelReq = HotelInformationDto.builder().hotelId("1")
      .country("UK")
      .language("en").build();
  private final MultipleLabelsRequestDto multilabelReq = MultipleLabelsRequestDto.builder()
      .categories(List.of(CategoryEnumDto.MAIN))
      .country("UK").language("en")
      .build();
  private final HeaderRequestAemDto headerRequestAemDto = HeaderRequestAemDto.builder()
      .country("gb")
      .language("en")
      .build();
  private final LayoutRequestAemDto layoutRequestAemDto = LayoutRequestAemDto.builder()
      .language("en")
      .dictionary("common-layout")
      .build();
  private final CardManagementRequestAemDto cardManagementRequestAemDto = CardManagementRequestAemDto.builder()
      .language("en")
      .build();
  private final CommonIconsRequestAemDto commonIconsRequestAemDto = CommonIconsRequestAemDto.builder()
      .language("en")
      .build();
  private final AppsHomepageRequestDto homepage = AppsHomepageRequestDto.builder()
      .channel("PI")
      .country("gb")
      .language("en")
      .subchannel("apps")
      .build();
  private final PageDataRequestDto pageDataRequestDto = PageDataRequestDto.builder()
      .dictionaries(List.of(DictionaryEnumDto.LAYOUT_DICTIONARY, DictionaryEnumDto.USER_MANAGEMENT_DICTIONARY,
              DictionaryEnumDto.PROFILE_MANAGEMENT_DICTIONARY, DictionaryEnumDto.COMPANY_MANAGEMENT_DICTIONARY,
              DictionaryEnumDto.PAY_APPLICATION_DICTIONARY, DictionaryEnumDto.AUTH_DICTIONARY,
            DictionaryEnumDto.NOTIFICATIONS_DICTIONARY, DictionaryEnumDto.CONTACT_US_DICTIONARY))
      .language("en")
      .country("gb")
      .build();

  @BeforeEach
  void setUp() {
    // To clear any autoloaded mappings
    wm.resetAll();
    lenient().when(aemProperties.getIndexHeaderDataEndpoint()).thenReturn(
        "/{country}/{language}/index.header.data");
    lenient().when(aemProperties.getLabelsEndpoint()).thenReturn(
        "etc/designs/global/dictionaries/labels/i18n.jsondict.{language}");
    lenient().when(aemProperties.getSearchResultsDataEndpoint()).thenReturn(
        "/{country}/{language}/search.searchresults.data");
    lenient().when(aemProperties.getAllHotelDetailsEndpoint()).thenReturn(
        "{country}/{language}/hoteldirectory/list.hotels.data");
    lenient().when(aemProperties.getInnbContentEndpoint()).thenReturn(
        "{country}/{language}/content-service.header.detail/site/business-booker.json");
    lenient().when(aemProperties.getInnbLayoutEndpoint()).thenReturn(
        "etc/designs/global/dictionaries/innbusiness/{dictionary}/i18n.jsondict.{language}");
    lenient().when(aemProperties.getInnbCardManagementEndpoint()).thenReturn(
        "etc/designs/global/dictionaries/innbusiness/card-management/i18n.jsondict.{language}");
    lenient().when(aemProperties.getInnbCommonIconsEndpoint()).thenReturn(
        "etc/designs/global/dictionaries/common-icons/i18n.jsondict.{language}");
    lenient().when(aemProperties.getInnbCommonLayoutEndpoint()).thenReturn(
        "etc/designs/global/dictionaries/innbusiness/common-layout/i18n.jsondict.{language}");
    lenient().when(aemProperties.getInnbUserManagementEndpoint()).thenReturn(
            "/etc/designs/global/dictionaries/innbusiness/user-management/i18n.jsondict.{language}");
    lenient().when(aemProperties.getInnbProfileManagementEndpoint()).thenReturn(
            "/etc/designs/global/dictionaries/innbusiness/profile-management/i18n.jsondict.{language}");
    lenient().when(aemProperties.getInnbCompanyManagementEndpoint()).thenReturn(
        "/etc/designs/global/dictionaries/innbusiness/company-management/i18n.jsondict.{language}");
    lenient().when(aemProperties.getAppsHomepageEndPoint()).thenReturn(
        "/content/premier-inn/country/language/path/apps/home.model.json");
    lenient().when(aemProperties.getPayApplicationEndpoint()).thenReturn(
        "/etc/designs/global/dictionaries/innbusiness/pay-application/i18n.jsondict.{language}");
    lenient().when(aemProperties.getAuthEndpoint()).thenReturn(
        "/etc/designs/global/dictionaries/innbusiness/auth/i18n.jsondict.{language}");
    lenient().when(aemProperties.getNotificationsEndpoint()).thenReturn(
          "/etc/designs/global/dictionaries/innbusiness/notifications/i18n.jsondict.{language}");
    lenient().when(aemProperties.getInnbContactUsEndpoint()).thenReturn(
        "/etc/designs/global/dictionaries/innbusiness/contact-us/i18n.jsondict.{language}");
    lenient().when(aemProperties.getPromoLabelsEndpoint()).thenReturn(
        "etc/designs/global/dictionaries/promotions/i18n.jsondict.{language}");
    lenient().when(aemHomepageAppsProperties.getCountryParam()).thenReturn("country");
    lenient().when(aemHomepageAppsProperties.getLanguageParam()).thenReturn("language");
    lenient().when(aemHomepageAppsProperties.getPathParam()).thenReturn("path");
    lenient().when(aemHomepageAppsProperties.getSubchannelParam()).thenReturn("apps");
    wm.stubFor(get(urlPathMatching("/.*"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
            .withStatus(500)
            .withBody("exception")));
  }

  @AfterEach
  void cleanUp() {
    wm.resetAll();
  }

  @Test
  void testAemClientIndex_ShouldReturnException() {
    AemClient aemClient = new AemClient(webClient, aemProperties, aemDlpProperties, aemHomepageAppsProperties);

    //Act
    assertThrows(AemResponseException.class,
        () -> aemClient.getIndexHeaderData(indexHeaderDto));
  }


  @Test
  void testAemClientLabels_ShouldReturnException() {
    AemClient aemClient = new AemClient(webClient, aemProperties, aemDlpProperties, aemHomepageAppsProperties);

    //Act
    assertThrows(AemResponseException.class,
        () -> aemClient.getLabels(labelReq));
  }

  @Test
  void testAemClientSearchResults_ShouldReturnException() {
    AemClient aemClient = new AemClient(webClient, aemProperties, aemDlpProperties, aemHomepageAppsProperties);

    //Act
    assertThrows(AemResponseException.class,
        () -> aemClient.getSearchResultsData(localReq));
  }

  @Test
  void testAemClientAllHotel_ShouldReturnException() {
    AemClient aemClient = new AemClient(webClient, aemProperties, aemDlpProperties, aemHomepageAppsProperties);
    assertThrows(AemResponseException.class,
        () -> aemClient.getAllHotelDetails(hotelReq));
  }

  @Test
  void testAemClientSingleHotel_ShouldReturnException() {
    AemClient aemClient = new AemClient(webClient, aemProperties, aemDlpProperties, aemHomepageAppsProperties);
    assertThrows(AemResponseException.class,
        () -> aemClient.getSingleHotelInformation("1", "UK", "en"));
  }

  @Test
  void testAemClientMultipleLabels_ShouldReturnException() {
    AemClient aemClient = new AemClient(webClient, aemProperties, aemDlpProperties, aemHomepageAppsProperties);
    assertThrows(AemResponseException.class,
        () -> aemClient.getMultipleLabels(multilabelReq));
  }

  @Test
  void testLayoutAemClientLayout_ShouldReturnException() {
    HeaderAemClient headerAemClient = new HeaderAemClient(webClient, aemProperties);

    //Act
    assertThrows(AemResponseException.class,
        () -> headerAemClient.getLayoutInformation(layoutRequestAemDto));
  }

  @Test
  void testHeaderAemClientLayout_ShouldReturnException() {
    HeaderAemClient headerAemClient = new HeaderAemClient(webClient, aemProperties);

    //Act
    assertThrows(AemResponseException.class,
        () -> headerAemClient.getHeaderContentInformation(headerRequestAemDto));
  }

  @Test
  void testCardManagementAemClientCommonIcons_ShouldReturnException() {
    CardManagementAemClient cardManagementAemClient = new CardManagementAemClient(webClient, aemProperties);

    //Act
    assertThrows(AemResponseException.class,
        () -> cardManagementAemClient.getCommonIconsInformation(commonIconsRequestAemDto));
  }

  @Test
  void testCardManagementAemClientCardManagement_ShouldReturnException() {
    CardManagementAemClient cardManagementAemClient = new CardManagementAemClient(webClient, aemProperties);

    //Act
    assertThrows(AemResponseException.class,
        () -> cardManagementAemClient.getCardManagementContentInformation(cardManagementRequestAemDto));
  }

  @Test
  void testPageDataAemClient_ShouldReturnException() {
    PageDataAemClient aemClient = new PageDataAemClient(webClient, aemProperties);

    //Act
    assertThrows(AemResponseException.class,
        () -> aemClient.getPageData(pageDataRequestDto));
  }

  @Test
  void testAppsHomepageAemClient_ShouldReturnException() {
    AemClient aemClient = new AemClient(webClient, aemProperties, aemDlpProperties,
        aemHomepageAppsProperties);
    assertThrows(AemResponseException.class,
        () -> aemClient.getAppsHomepage(homepage));
  }
}
