package uk.co.whitbread.reservation.infrastructure.rest.controller.booking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import uk.co.whitbread.reservation.domain.model.out.aem.AemCookieContentResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.AemFooterResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.AemHeaderResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.BookPage;
import uk.co.whitbread.reservation.domain.model.out.aem.Column;
import uk.co.whitbread.reservation.domain.model.out.aem.CookieGroup;
import uk.co.whitbread.reservation.domain.model.out.aem.CookiePolicies;
import uk.co.whitbread.reservation.domain.model.out.aem.IntroView;
import uk.co.whitbread.reservation.domain.model.out.aem.LinkItem;
import uk.co.whitbread.reservation.domain.model.out.aem.LocationsResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.ManageView;
import uk.co.whitbread.reservation.domain.model.out.aem.NavbarItem;
import uk.co.whitbread.reservation.domain.model.out.aem.Tab;
import uk.co.whitbread.reservation.domain.model.out.aem.ZonalUuidResponse;
import uk.co.whitbread.reservation.domain.model.out.getlabel.LabelData;
import uk.co.whitbread.reservation.domain.model.out.getlabel.LabelDataListResponse;
import uk.co.whitbread.reservation.domain.ports.primary.AemInPort;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.BookPageResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.CookieContentResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.FooterResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.HeaderResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.LabelDataResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.ZonalUuidResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.AemCookieContentResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.AemFooterResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.AemHeaderResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.BookPageDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.ColumnDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.CookieGroupDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.CookiePoliciesDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.IntroViewDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.LabelDataDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.LinkItemDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.ManageViewDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.NavbarItemDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.TabDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.ZonalUuidResponseDto;

@SpringBootTest(classes = AemController.class)
@WebAppConfiguration
@EnableWebMvc
class AemControllerTest {
  private final String AEM_HEADER_SERVICE = "/event/v1/headers";
  private final String AEM_FOOTER_SERVICE = "/event/v1/footers";
  private final String AEM_BOOK_PAGE_SERVICE = "/event/v1/book-page";
  private final String AEM_ZONAL_SERVICE = "/event/v1/locations";

  private MockMvc mockMvc;
  @Autowired
  WebApplicationContext webApplicationContext;

  @MockitoBean
  AemInPort aemInPort;
  @MockitoBean
  private HeaderResponseMapper headerResponseMapper;
  @MockitoBean
  private FooterResponseMapper footerResponseMapper;
  @MockitoBean
  private BookPageResponseMapper bookPageResponseMapper;
  @MockitoBean
  private CookieContentResponseMapper cookieContentResponseMapper;
  @MockitoBean
  private ZonalUuidResponseMapper zonalUuidResponseMapper;

 @MockitoBean
  private LabelDataResponseMapper labelDataResponseMapper;

  @BeforeEach
  void beforeSetup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  void aemHeader_whenValidRequestGiven_returnWithSuccess() throws Exception {
    Mockito.when(
        aemInPort.getHeaders(Mockito.anyString())).thenReturn(mockHeaderResponse());
    Mockito.when(headerResponseMapper.toDto(Mockito.any())).thenReturn(mockHeaderDtoResponse());
    MvcResult mvcResult = mockMvc.perform(
        MockMvcRequestBuilders.get(AEM_HEADER_SERVICE).param("restaurant", "abc")
            .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    Assertions.assertEquals(200, status);
  }

  @Test
  void aemHeader_whenBadRequest() throws Exception {
    Mockito.when(
        aemInPort.getHeaders(Mockito.anyString())).thenReturn(mockHeaderResponse());
    Mockito.when(headerResponseMapper.toDto(Mockito.any())).thenReturn(mockHeaderDtoResponse());
    MvcResult mvcResult = mockMvc.perform(
        MockMvcRequestBuilders.get(AEM_HEADER_SERVICE)
            .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    Assertions.assertEquals(400, status);
  }

  @Test
  void aemFooter_whenValidRequestGiven_returnWithSuccess() throws Exception {
    Mockito.when(
        aemInPort.getFooters(Mockito.anyString())).thenReturn(mockAemFooterResponse());
    Mockito.when(footerResponseMapper.toDto(Mockito.any())).thenReturn(mockAemFooterResponseDto());
    MvcResult mvcResult = mockMvc.perform(
        MockMvcRequestBuilders.get(AEM_FOOTER_SERVICE).param("restaurant", "abc")
            .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    Assertions.assertEquals(200, status);
  }

  @Test
  void aemFooter_whenValidBadRequest() throws Exception {
    Mockito.when(
        aemInPort.getFooters(Mockito.anyString())).thenReturn(mockAemFooterResponse());
    Mockito.when(footerResponseMapper.toDto(Mockito.any())).thenReturn(mockAemFooterResponseDto());
    MvcResult mvcResult = mockMvc.perform(
        MockMvcRequestBuilders.get(AEM_FOOTER_SERVICE)
            .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    Assertions.assertEquals(400, status);
  }

  @Test
  void aemBookPage_whenValidRequestGiven_returnWithSuccess() throws Exception {
    Mockito.when(
            aemInPort.getBookPageContent(Mockito.anyString(),Mockito.anyString(),Mockito.anyString()))
        .thenReturn(mockBookPageResponse());
    Mockito.when(bookPageResponseMapper.toDto(Mockito.any())).thenReturn(mockBookPageResponseDto());
    MvcResult mvcResult = mockMvc.perform(
        MockMvcRequestBuilders.get(AEM_BOOK_PAGE_SERVICE).param("restaurant", "abc")
            .param("location","abc")
            .param("subLocation","abc")
            .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    Assertions.assertEquals(200, status);
  }

  @Test
  void aemBookPage_whenBadRequest() throws Exception {
    Mockito.when(
            aemInPort.getBookPageContent(Mockito.anyString(),Mockito.anyString(),Mockito.anyString()))
        .thenReturn(mockBookPageResponse());
    Mockito.when(bookPageResponseMapper.toDto(Mockito.any())).thenReturn(mockBookPageResponseDto());
    MvcResult mvcResult = mockMvc.perform(
        MockMvcRequestBuilders.get(AEM_BOOK_PAGE_SERVICE)
            .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    Assertions.assertEquals(400, status);
  }

  @Test
  void aemCookieConsent_whenValidRequestGiven_returnWithSuccess() throws Exception {
    String aemCookieConsentService = "/event/v1/cookie-consent";
    Mockito.when(
        aemInPort.getCookieContent()).thenReturn(mockAemCookieContentResponse());
    Mockito.when(cookieContentResponseMapper.toDto(Mockito.any()))
        .thenReturn(mockAemCookieContentResponseDto());
    MvcResult mvcResult = mockMvc.perform(
        MockMvcRequestBuilders.get(aemCookieConsentService)
            .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    Assertions.assertEquals(200, status);
  }


  @Test
  void aemZonalUuid_whenValidRequestGiven_returnWithSuccess() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("restaurant", "abc");
    params.add("location","location");
    params.add("subLocation", "subLocation");

    Mockito.when(
            aemInPort.locations(Mockito.anyString(),Mockito.anyString(),Mockito.anyString()))
        .thenReturn(mockZonalUuidResponse());
    Mockito.when(zonalUuidResponseMapper.toDto(Mockito.any()))
        .thenReturn(mockZonalUuidResponseDto());
    MvcResult mvcResult = mockMvc.perform(
        MockMvcRequestBuilders.get(AEM_ZONAL_SERVICE).accept(MediaType.APPLICATION_JSON_VALUE)
            .params(params)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    Assertions.assertEquals(200, status);
  }

  @Test
  void aemZonalUuid_whenBadRequest() throws Exception {
    Mockito.when(
            aemInPort.locations(Mockito.anyString(),Mockito.anyString(),Mockito.anyString()))
        .thenReturn(mockZonalUuidResponse());
    Mockito.when(zonalUuidResponseMapper.toDto(Mockito.any()))
        .thenReturn(mockZonalUuidResponseDto());
    MvcResult mvcResult = mockMvc.perform(
        MockMvcRequestBuilders.get(AEM_ZONAL_SERVICE)
            .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    Assertions.assertEquals(400, status);
  }
  @Test
  void getLabel() throws Exception {
    String aemGetLabel = "/event/v1/label-data";
    Mockito.when(
        aemInPort.getLabel()).thenReturn(mockGetLabelResponse());
    Mockito.when(labelDataResponseMapper.toDto(Mockito.any()))
        .thenReturn(mockGetLabelResponseDto());
    MvcResult mvcResult = mockMvc.perform(
        MockMvcRequestBuilders.get(aemGetLabel)
            .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    Assertions.assertEquals(200, status);
  }
  private LabelDataListResponse mockGetLabelResponse() {
    return LabelDataListResponse.builder()
        .labels(List.of(new LabelData("key1", "value1"), new LabelData("key2", "value2")))
        .build();
  }
  private List<LabelDataDto> mockGetLabelResponseDto(){
    List<LabelDataDto> labelDataList = new ArrayList<>();

    // Add LabelData objects to the list
    labelDataList.add(new LabelDataDto("key1", "value1"));
    labelDataList.add(new LabelDataDto("key2", "value2"));
    return labelDataList;
  }



  private LocationsResponse mockZonalUuidResponse() {
    ZonalUuidResponse response = new ZonalUuidResponse();
    response.setId("1");
    response.setTitle("Title1");
    ZonalUuidResponse response1 = new ZonalUuidResponse();
    response1.setId("2");
    response1.setTitle("Title2");
    return LocationsResponse.builder().locations(List.of(response, response1)).build();
  }

  private List<ZonalUuidResponseDto> mockZonalUuidResponseDto() {
    ZonalUuidResponseDto response = new ZonalUuidResponseDto();
    response.setId("1");
    response.setTitle("Title1");
    ZonalUuidResponseDto response1 = new ZonalUuidResponseDto();
    response1.setId("2");
    response1.setTitle("Title2");
    return List.of(new ZonalUuidResponseDto[]{response, response1});
  }

  private AemCookieContentResponse mockAemCookieContentResponse() {
    AemCookieContentResponse aemCookieContentResponse = new AemCookieContentResponse();
    CookiePolicies cookiePolicies = new CookiePolicies();
    cookiePolicies.setBrand("brand");
    cookiePolicies.setVersion("version");
    IntroView introView = new IntroView();
    introView.setDescription("desc");
    introView.setTitle("OG");
    introView.setAcceptAllButtonText("xyx");
    introView.setManageButtonText("text");
    cookiePolicies.setIntroView(introView);
    ManageView manageView = new ManageView();
    manageView.setDescription("manage");
    manageView.setAlwaysActiveText("active");
    manageView.setTitle("OG");
    CookieGroup cookieGroup = new CookieGroup();
    cookieGroup.setCookieName("cook");
    cookieGroup.setDescription("OG-PK");
    cookieGroup.setTitle("OG");
    cookieGroup.setAlwaysActive(true);
    manageView.setCookieGroup(List.of(cookieGroup));
    cookiePolicies.setManageView(manageView);
    aemCookieContentResponse.setCookiePolicies(cookiePolicies);
    return aemCookieContentResponse;
  }

  private AemCookieContentResponseDto mockAemCookieContentResponseDto() {
    AemCookieContentResponseDto aemCookieContentResponse = new AemCookieContentResponseDto();
    CookiePoliciesDto cookiePolicies = new CookiePoliciesDto();
    cookiePolicies.setBrand("brand");
    cookiePolicies.setVersion("version");
    IntroViewDto introView = new IntroViewDto();
    introView.setDescription("desc");
    introView.setTitle("OG");
    introView.setAcceptAllButtonText("xyx");
    introView.setManageButtonText("text");
    cookiePolicies.setIntroView(introView);
    ManageViewDto manageView = new ManageViewDto();
    manageView.setDescription("manage");
    manageView.setAlwaysActiveText("active");
    manageView.setTitle("OG");
    CookieGroupDto cookieGroup = new CookieGroupDto();
    cookieGroup.setCookieName("cook");
    cookieGroup.setDescription("OG-PK");
    cookieGroup.setTitle("OG");
    cookieGroup.setAlwaysActive(true);
    manageView.setCookieGroup(List.of(cookieGroup));
    cookiePolicies.setManageView(manageView);
    aemCookieContentResponse.setCookiePolicies(cookiePolicies);
    return aemCookieContentResponse;
  }

  private BookPage mockBookPageResponse() {
    BookPage bookPage = new BookPage();
    bookPage.setHeroImageSrc("dummyHeroImageSrc");
    bookPage.setSubtitleName("dummySubtitleName");
    bookPage.setName("dummyName");
    bookPage.setHeroBackgroundImageSrc("dummyHeroBackgroundImageSrc");
    return bookPage;
  }

  private BookPageDto mockBookPageResponseDto() {
    BookPageDto bookPage = new BookPageDto();
    bookPage.setHeroImageSrc("dummyHeroImageSrc");
    bookPage.setSubtitleName("dummySubtitleName");
    bookPage.setName("dummyName");
    bookPage.setHeroBackgroundImageSrc("dummyHeroBackgroundImageSrc");
    return bookPage;
  }

  private AemFooterResponse mockAemFooterResponse() {
    AemFooterResponse aemFooterResponse = new AemFooterResponse();
    aemFooterResponse.setCopyrightInfo("abc");
    aemFooterResponse.setLegalCopyRightLabel("abc");
    Tab tab = new Tab();
    tab.setName("abc");
    Column column = new Column();
    LinkItem linkItem = new LinkItem();
    linkItem.setLinkSrc("abc/xyz");
    linkItem.setOpenInNewTab(true);
    linkItem.setName("mno");
    column.setLinkItems(List.of(linkItem));
    tab.setColumns(List.of(column));
    aemFooterResponse.setTabs(List.of(tab));
    return aemFooterResponse;

  }

  private AemFooterResponseDto mockAemFooterResponseDto() {
    AemFooterResponseDto aemFooterResponse = new AemFooterResponseDto();
    aemFooterResponse.setCopyrightInfo("abc");
    aemFooterResponse.setLegalCopyRightLabel("abc");
    TabDto tab = new TabDto();
    tab.setName("abc");
    ColumnDto column = new ColumnDto();
    LinkItemDto linkItem = new LinkItemDto();
    linkItem.setLinkSrc("abc/xyz");
    linkItem.setOpenInNewTab(true);
    linkItem.setName("mno");
    column.setLinkItems(List.of(linkItem));
    tab.setColumns(List.of(column));
    aemFooterResponse.setTabs(List.of(tab));
    return aemFooterResponse;

  }

  private AemHeaderResponse mockHeaderResponse() {
    AemHeaderResponse aemHeaderResponse = new AemHeaderResponse();
    aemHeaderResponse.setLogoSrc("sample_logo.png");
    aemHeaderResponse.setLogoAlt("Sample Logo");
    aemHeaderResponse.setHomeAltSrc("sample_home.png");
    aemHeaderResponse.setMoreLocationName("Sample Location");
    NavbarItem navbarItem = new NavbarItem();
    navbarItem.setName("name");
    aemHeaderResponse.setNavbar(List.of(navbarItem));
    aemHeaderResponse.setLocationSrc(Arrays.asList("blr", "HYD"));
    return
        aemHeaderResponse;
  }

  private AemHeaderResponseDto mockHeaderDtoResponse() {
    AemHeaderResponseDto aemHeaderResponse = new AemHeaderResponseDto();
    aemHeaderResponse.setLogoSrc("sample_logo.png");
    aemHeaderResponse.setLogoAlt("Sample Logo");
    aemHeaderResponse.setHomeAltSrc("sample_home.png");
    aemHeaderResponse.setMoreLocationName("Sample Location");
    NavbarItemDto navbarItem = new NavbarItemDto();
    navbarItem.setName("name");
    aemHeaderResponse.setNavbar(List.of(navbarItem));
    aemHeaderResponse.setLocationSrc(Arrays.asList("blr", "HYD"));
    return
        aemHeaderResponse;
  }


}
