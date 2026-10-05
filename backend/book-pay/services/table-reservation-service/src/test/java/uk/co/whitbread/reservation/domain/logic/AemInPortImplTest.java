package uk.co.whitbread.reservation.domain.logic;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.domain.model.out.aem.AemCookieContentResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.AemFooterResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.AemHeaderResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.BookPage;
import uk.co.whitbread.reservation.domain.model.out.aem.Column;
import uk.co.whitbread.reservation.domain.model.out.aem.CookieGroup;
import uk.co.whitbread.reservation.domain.model.out.aem.CookiePolicies;
import uk.co.whitbread.reservation.domain.model.out.aem.IntroView;
import uk.co.whitbread.reservation.domain.model.out.aem.LinkItem;
import uk.co.whitbread.reservation.domain.model.out.aem.ManageView;
import uk.co.whitbread.reservation.domain.model.out.aem.NavbarItem;
import uk.co.whitbread.reservation.domain.model.out.aem.Tab;
import uk.co.whitbread.reservation.domain.model.out.aem.LocationsResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.ZonalUuidResponse;
import uk.co.whitbread.reservation.domain.model.out.getlabel.LabelData;
import uk.co.whitbread.reservation.domain.model.out.getlabel.LabelDataListResponse;
import uk.co.whitbread.reservation.infrastructure.rest.client.reservation.AemOutPortImpl;

@ExtendWith(MockitoExtension.class)
class AemInPortImplTest {
  @InjectMocks
  private AemInPortImpl aemInPortimpl;

  @Mock
  private AemOutPortImpl aemOutPortImpl;

  @BeforeEach
  public void setUp() {
  }

  @Test
  void footers() {
    AemFooterResponse aemFooterResponse = mockAemFooterResponse();
    when(aemOutPortImpl.getFooters(Mockito.anyString())).thenReturn(aemFooterResponse);
    AemFooterResponse response = aemInPortimpl.getFooters("abc");
    assertNotNull(response);

  }

  @Test
  void header() {
    AemHeaderResponse aemHeaderResponse = mockAemHeaderResponse();
    when(aemOutPortImpl.getHeaders(Mockito.anyString())).thenReturn(aemHeaderResponse);
    AemHeaderResponse response = aemInPortimpl.getHeaders("abc");
    assertNotNull(response);

  }

  @Test
  void bookPage() {
    BookPage bookPage = mockBookPageResponse();
    when(aemOutPortImpl.getBookPageContent(Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(bookPage);
    BookPage response = aemInPortimpl.getBookPageContent("abc","loc","subloc");
    assertNotNull(response);

  }


  @Test
  void getContentCookie() {
    AemCookieContentResponse aemCookieContentResponse = mockAemCookieContentResponse();
    when(aemOutPortImpl.getCookieContent()).thenReturn(aemCookieContentResponse);
    AemCookieContentResponse response = aemInPortimpl.getCookieContent();
    assertNotNull(response);

  }

  @Test
  void getZonalUid() {
    LocationsResponse locationsResponse = mockZonalUuidResponse();
    when(aemOutPortImpl.locations(Mockito.anyString(),Mockito.anyString(), Mockito.anyString())).thenReturn(locationsResponse);
    LocationsResponse response = aemInPortimpl.locations("abc", "location","subLocation");
    assertNotNull(response);

  }
  @Test
  void getLabel(){
    LabelDataListResponse labelDataListResponse = LabelDataListResponse.builder()
        .labels(List.of(new LabelData("key1", "value1"), new LabelData("key2", "value2")))
        .build();
    when(aemOutPortImpl.getLabel()).thenReturn(labelDataListResponse);
    LabelDataListResponse response = aemInPortimpl.getLabel();
    assertNotNull(response);

  }

  private LocationsResponse mockZonalUuidResponse() {
    ZonalUuidResponse response = new ZonalUuidResponse();
    response.setId("123");
    response.setTitle("Dummy Title");
    response.setPath("/dummy/path");
    response.setLatitude("123.456");
    response.setLongitude("-789.012");
    response.setAddress1("Dummy Address 1");
    response.setAddress2("Dummy Address 2");
    response.setAddress3("Dummy Address 3");
    response.setAddress4("Dummy Address 4");
    response.setExternalSystemIdentifier("ABC-123");
    response.setExternalSourceSystem("Dummy Source System");
    return LocationsResponse.builder().locations(List.of(new ZonalUuidResponse[]{response})).build();
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

  private BookPage mockBookPageResponse() {
    BookPage bookPage = new BookPage();
    bookPage.setHeroImageSrc("dummyHeroImageSrc");
    bookPage.setSubtitleName("dummySubtitleName");
    bookPage.setName("dummyName");
    bookPage.setHeroBackgroundImageSrc("dummyHeroBackgroundImageSrc");
    return bookPage;
  }

  private AemHeaderResponse mockAemHeaderResponse() {
    AemHeaderResponse response = new AemHeaderResponse();
    response.setLogoSrc("dummy-logo-src");
    response.setLogoAlt("dummy-logo-alt");
    response.setHomeAltSrc("dummy-home-alt-src");
    response.setMoreLocationName("dummy-more-location-name");
    List<String> locationSrc = Arrays.asList("dummy-location-src-1", "dummy-location-src-2");
    response.setLocationSrc(locationSrc);
    NavbarItem navbarItem = new NavbarItem();
    navbarItem.setName("xyz");
    LinkItem linkItem = new LinkItem();
    linkItem.setName("Name");
    linkItem.setOpenInNewTab(true);
    navbarItem.setLinkItems(List.of(linkItem));
    response.setNavbar(List.of(navbarItem));
    return response;
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

}
