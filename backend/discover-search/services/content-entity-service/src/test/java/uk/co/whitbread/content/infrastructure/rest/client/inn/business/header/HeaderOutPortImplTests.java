package uk.co.whitbread.content.infrastructure.rest.client.inn.business.header;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_HEADER_CONTENT_INFO_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_LAYOUT_INFO_EXCEPTION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.model.inn.business.header.in.HeaderRequest;
import uk.co.whitbread.content.domain.model.inn.business.header.in.LayoutRequest;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Authentication;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Bookings;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Cards;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Contact;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Content;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Country;
import uk.co.whitbread.content.domain.model.inn.business.header.out.DatePicker;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Employees;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Faq;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Form;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Global;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Header;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Help;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Home;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Layout;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Manage;
import uk.co.whitbread.content.domain.model.inn.business.header.out.ManageAccount;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Menu;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Options;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Profile;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Sidebar;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Spending;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Tour;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.adapter.HeaderAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.mapper.HeaderContentRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.mapper.HeaderContentResponseMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.mapper.LayoutRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.mapper.LayoutResponseMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.in.HeaderContentResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.in.LayoutResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.in.LayoutResponseDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.out.HeaderRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.out.LayoutRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.AuthenticationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.BrandDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.ContentDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.CountryDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.DatePickerDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.FormDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.GlobalDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.HeaderDto;

@ExtendWith(MockitoExtension.class)
class HeaderOutPortImplTests {

  @Mock
  private HeaderAemClient aemClient;

  @Mock
  private LayoutRequestMapper layoutRequestMapper;

  @Mock
  private LayoutResponseMapper layoutResponseMapper;

  @Mock
  private HeaderContentRequestMapper headerContentRequestMapper;

  @Mock
  private HeaderContentResponseMapper headerContentResponseMapper;

  @InjectMocks
  private HeaderOutPortImpl headerOutPort;

  Exception exception = new Exception();

  @Test
  void getLayoutShouldReturnException() {
    //Arrange
    var expectedMessage = "Unable to get layout information.";
    when(layoutRequestMapper.toDto(any())).thenReturn(new LayoutRequestAemDto());
    when(aemClient.getLayoutInformation(any())).thenThrow(
        new AemResponseException(AEM_LAYOUT_INFO_EXCEPTION, expectedMessage, exception));
    var layoutRequest = LayoutRequest.builder()
        .language("null")
        .dictionary("null")
        .build();

    //Act
    var actual = assertThrows(AemResponseException.class,
        () -> headerOutPort.getLayoutInformation(layoutRequest));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(AEM_LAYOUT_INFO_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(AEM_LAYOUT_INFO_EXCEPTION.getCode()));
  }

  @Test
  void getLayoutShouldReturnOk() {
    //Arrange
    when(aemClient.getLayoutInformation(any(LayoutRequestAemDto.class)))
        .thenReturn(getLayoutAemInformation());
    when(layoutRequestMapper.toDto(any())).thenReturn(new LayoutRequestAemDto());
    when(layoutResponseMapper.toModel(any())).thenReturn(mockLayoutInformation());

    //Act
    var layoutResponse = headerOutPort.getLayoutInformation(getLayoutRequestGbEn());

    //Assert
    assertThat(layoutResponse, notNullValue());
    assertEquals("Cards", layoutResponse.getManageAccount().getCards().getTitle());
    assertEquals("/content/dam/global/icons/common/payment-card.svg",
        layoutResponse.getManageAccount().getCards().getIcon());
    assertEquals("Manage account", layoutResponse.getManageAccount().getTitle());
    assertEquals("Company profile", layoutResponse.getManageAccount().getProfile().getTitle());
    assertEquals("Edit company details", layoutResponse.getManageAccount().getProfile().getLink());
    assertEquals("Company details", layoutResponse.getMenu().getManage().getOptions().getCompany());

    verify(layoutResponseMapper).toModel(any(LayoutResponseDto.class));
  }

  @Test
  void getHeaderContentShouldReturnException() {
    //Arrange
    var expectedMessage = "Unable to get InnBusiness header content information.";
    when(headerContentRequestMapper.toDto(any())).thenReturn(new HeaderRequestAemDto());
    when(aemClient.getLayoutInformation(any(LayoutRequestAemDto.class)))
        .thenReturn(getLayoutAemInformation());
    when(layoutRequestMapper.toDto(any())).thenReturn(new LayoutRequestAemDto());
    when(layoutResponseMapper.toModel(any())).thenReturn(mockLayoutInformation());
    when(aemClient.getHeaderContentInformation(any())).thenThrow(
        new AemResponseException(AEM_HEADER_CONTENT_INFO_EXCEPTION, expectedMessage, exception));
    var headerRequest = HeaderRequest.builder()
        .language("en")
        .country("gb")
        .build();

    //Act
    var actual = assertThrows(AemResponseException.class,
        () -> headerOutPort.getHeaderInformation(headerRequest));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(AEM_HEADER_CONTENT_INFO_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(AEM_HEADER_CONTENT_INFO_EXCEPTION.getCode()));
  }

  @Test
  void getHeaderShouldReturnOk() {
    //Arrange
    when(aemClient.getHeaderContentInformation(any(HeaderRequestAemDto.class)))
        .thenReturn(getHeaderAemInformation());
    when(headerContentRequestMapper.toDto(any())).thenReturn(new HeaderRequestAemDto());
    when(headerContentResponseMapper.toModel(any())).thenReturn(mockHeaderInformation());
    when(aemClient.getLayoutInformation(any(LayoutRequestAemDto.class)))
        .thenReturn(getLayoutAemInformation());
    when(layoutRequestMapper.toDto(any())).thenReturn(new LayoutRequestAemDto());
    when(layoutResponseMapper.toModel(any())).thenReturn(mockLayoutInformation());

    //Act
    var headerResponse = headerOutPort.getHeaderInformation(getHeaderRequestGbEn());

    //Assert
    assertThat(headerResponse, notNullValue());
    assertEquals("Manage account", headerResponse.getLayout().getManageAccount().getTitle());
    assertEquals("Bookings", headerResponse.getLayout().getMenu().getBookings().getLabel());
    assertEquals("Need Help?", headerResponse.getLayout().getHelp().getNeedHelp());
    assertEquals("/content/dam/global/icons/common/chevron-left.svg", headerResponse.getLayout().getSidebar().getCollapse());

    assertEquals("Inn Business", headerResponse.getContent().getHeader().getImageAlt());
    assertEquals("today", headerResponse.getContent().getGlobal().getToday());
    assertEquals("gb", headerResponse.getContent().getCountries().get(0).getCode());
    assertEquals("Where to?", headerResponse.getContent().getForm().getWhere());

    assertEquals("Double", headerResponse.getContent().getGlobal().getDoubleValue());
    assertEquals("September", headerResponse.getContent().getForm().getDatePicker().getMonths().get(8));
    assertEquals("Enter a location or hotel", headerResponse.getContent().getForm().getInvalidLocation());
    assertEquals("Log out", headerResponse.getContent().getAuthentication().getLogoutButton());

    verify(layoutResponseMapper).toModel(any(LayoutResponseDto.class));
  }

  private LayoutRequest getLayoutRequestGbEn() {
    return LayoutRequest.builder()
        .language("en")
        .dictionary("common-layout")
        .build();
  }

  private HeaderRequest getHeaderRequestGbEn() {
    return HeaderRequest.builder()
        .country("gb")
        .language("en")
        .build();
  }

  private LayoutResponseAemDto getLayoutAemInformation() {

    return LayoutResponseAemDto.builder()
        .cardsTitle("Cards")
        .cardsLink("View cards")
        .cardsIcon("/content/dam/global/icons/common/payment-card.svg")
        .manageAccountTitle("Manage account")
        .profileTitle("Company profile")
        .profileText(
            "Enhance your stay with us! Complete your profile to enjoy customised services.")
        .profileLink("Edit company details")
        .employeesTitle("Employees")
        .employeesLink("View employee")
        .employeesIcon("/content/dam/global/icons/common/person.svg")
        .bookingsLabel("Bookings")
        .menuBookingsIcon("/content/dam/global/icons/common/suitcase.svg")
        .menuBookingsIconActive("/content/dam/global/icons/common/suitcase-solid.svg")
        .menuSpendingLabel("Spending")
        .menuSpendingIcon("/content/dam/global/icons/common/chart-bars.svg")
        .menuSpendingIconActive("/content/dam/global/icons/common/chart-bars-solid.svg")
        .homeLabel("Home")
        .homeIcon("/content/dam/global/icons/common/home.svg")
        .homeIconActive("/content/dam/global/icons/common/home-solid.svg")
        .menuManageLabel("Manage")
        .menuManageIcon("/content/dam/global/icons/common/chart-pie.svg")
        .menuManageIconActive("/content/dam/global/icons/common/chart-pie-solid.svg")
        .faqLabel("FAQs")
        .helpFaqIcon("/content/dam/global/icons/common/speech-bubble.svg")
        .needHelp("Need Help?")
        .helpTourLabel("Tour")
        .helpTourIcon("/content/dam/global/icons/common/hand-right.svg")
        .helpContactLabel("Contact")
        .helpContactIcon("/content/dam/global/icons/common/person.svg")
        .sidebarCollapse("/content/dam/global/icons/common/chevron-left.svg")
        .sidebarExpand("/content/dam/global/icons/common/chevron-right.svg")
        .menuManageOptionsAlerts("Booking alerts")
        .menuManageOptionsAllowances("Booking allowances")
        .menuManageOptionsCards("Card management")
        .menuManageOptionsCompany("Company details")
        .menuManageOptionsEmployees("Manage employees")
        .menuManageOptionsQuestions("Employee questions")
        .build();
  }

  private HeaderContentResponseAemDto getHeaderAemInformation() {
    return HeaderContentResponseAemDto.builder()
        .content(ContentDto.builder()
            .header(HeaderDto.builder()
                .image("/content/dam/global/icons/brand/logo-pi-inn-business.svg")
                .imageAlt("Inn Business")
                .build())
            .global(GlobalDto.builder()
                .today("today")
                .tomorrow("tomorrow")
                .adult("adult")
                .adults("adults")
                .child("child")
                .children("children")
                .room("room")
                .rooms("rooms")
                .night("night")
                .nights("nights")
                .single("Single")
                .accessible("Accessible")
                .twin("Twin")
                .family("Family")
                .adultsLabel("Adults")
                .childrenLabel("Children")
                .done("Done")
                .doubleValue("Double")
                .labelAddRoom("Add another room")
                .iconAddRoom("/content/dam/global/icons/common/add.svg")
                .brand(BrandDto.builder()
                    .piLogo("/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc.svg")
                    .pidLogo("/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc.svg")
                    .hubLogo("/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc-hub.svg")
                    .zipLogo("/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc-zip.svg")
                    .build())
                .build())
            .countries(getCountries())
            .form(FormDto.builder()
                .where("Where to?")
                .whereIcon("/content/dam/global/icons/common/location.svg")
                .calendarIcon("/content/dam/global/icons/common/calendar.svg")
                .guestIcon("/content/dam/global/icons/common/person.svg")
                .whereDismissIcon("/content/dam/global/icons/common/dismiss-dark.svg")
                .search("Search")
                .hotelsLabel("Hotels")
                .searchIcon("/content/dam/global/icons/common/search-icon.svg")
                .searchEdit("Edit")
                .datePicker(DatePickerDto.builder()
                    .months(getMonths())
                    .weekdaysShort(getWeekdaysShort())
                    .reset("Reset")
                    .done("Done")
                    .checkOut("Check out")
                    .build())
                .room("Room")
                .adultsHelperText("Max 2 per room")
                .childrenHelperText("2-15 years")
                .includeCot("Include a cot?")
                .cotLimit("0-2 years")
                .roomType("Room Type")
                .removeRoom("Remove room")
                .invalidLocation("Enter a location or hotel")
                .invalidNights("Enter a valid number of nights")
                .invalidRooms("Enter a valid room composition")
                .build())
            .authentication(AuthenticationDto.builder()
                .myProfile("My Profile")
                .logoutButton("Log out")
                .build())
            .build())
        .build();
  }

  private Layout mockLayoutInformation() {
    return Layout.builder()
        .manageAccount(ManageAccount.builder()
            .cards(Cards.builder()
                .title("Cards")
                .link("View cards")
                .icon("/content/dam/global/icons/common/payment-card.svg")
                .build())
            .title("Manage account")
            .profile(Profile.builder()
                .title("Company profile")
                .text(
                    "Enhance your stay with us! Complete your profile to enjoy customised services.")
                .link("Edit company details")
                .build())
            .employees(Employees.builder()
                .title("Employees")
                .link("View employee")
                .icon("/content/dam/global/icons/common/person.svg")
                .build())
            .build())
        .menu(Menu.builder()
            .bookings(Bookings.builder()
                .label("Bookings")
                .icon("/content/dam/global/icons/common/suitcase.svg")
                .iconActive("/content/dam/global/icons/common/suitcase-solid.svg")
                .build())
            .spending(Spending.builder()
                .label("Spending")
                .icon("/content/dam/global/icons/common/chart-bars.svg")
                .iconActive("/content/dam/global/icons/common/chart-bars-solid.svg")
                .build())
            .home(Home.builder()
                .label("Home")
                .icon("/content/dam/global/icons/common/home.svg")
                .iconActive("/content/dam/global/icons/common/home-solid.svg")
                .build())
            .manage(Manage.builder()
                .label("Manage")
                .icon("/content/dam/global/icons/common/chart-pie.svg")
                .iconActive("/content/dam/global/icons/common/chart-pie-solid.svg")
                .options(Options.builder()
                    .employees("Manage employees")
                    .allowances("Booking allowances")
                    .alerts("Booking alerts")
                    .cards("Card management")
                    .questions("Employee questions")
                    .company("Company details")
                    .build())
                .build())
            .build())
        .help(Help.builder()
            .faq(Faq.builder()
                .label("FAQs")
                .icon("/content/dam/global/icons/common/speech-bubble.svg")
                .build())
            .needHelp("Need Help?")
            .tour(Tour.builder()
                .label("Tour")
                .icon("/content/dam/global/icons/common/hand-right.svg")
                .build())
            .contact(Contact.builder()
                .label("Contact")
                .icon("/content/dam/global/icons/common/person.svg")
                .build())
            .build())
        .sidebar(Sidebar.builder()
            .collapse("/content/dam/global/icons/common/chevron-left.svg")
            .expand("/content/dam/global/icons/common/chevron-right.svg")
            .build())
        .build();
  }

  private Content mockHeaderInformation() {

    return Content.builder()
            .header(Header.builder()
                .image("/content/dam/global/icons/brand/logo-pi-inn-business.svg")
                .imageAlt("Inn Business")
                .build())
            .global(Global.builder()
                .today("today")
                .tomorrow("tomorrow")
                .adult("adult")
                .adults("adults")
                .room("room")
                .rooms("rooms")
                .night("night")
                .nights("nights")
                .single("Single")
                .accessible("Accessible")
                .twin("Twin")
                .family("Family")
                .adultsLabel("Adults")
                .childrenLabel("Children")
                .done("Done")
                .doubleValue("Double")
                .labelAddRoom("Add another room")
                .iconAddRoom("/content/dam/global/icons/common/add.svg")
                .build())
            .countries(getCountriesMocked())
            .form(Form.builder()
                .where("Where to?")
                .whereIcon("/content/dam/global/icons/common/location.svg")
                .calendarIcon("/content/dam/global/icons/common/calendar.svg")
                .guestIcon("/content/dam/global/icons/common/person.svg")
                .whereDismissIcon("/content/dam/global/icons/common/dismiss-dark.svg")
                .search("Search")
                .hotelsLabel("Hotels")
                .searchIcon("/content/dam/global/icons/common/search-icon.svg")
                .searchEdit("Edit")
                .datePicker(DatePicker.builder()
                    .months(getMonths())
                    .weekdaysShort(getWeekdaysShort())
                    .reset("Reset")
                    .done("Done")
                    .checkOut("Check out")
                    .build())
                .room("Room")
                .adultsHelperText("Max 2 per room")
                .childrenHelperText("2-15 years")
                .includeCot("Include a cot?")
                .cotLimit("0-2 years")
                .roomType("Room Type")
                .removeRoom("Remove room")
                .invalidLocation("Enter a location or hotel")
                .invalidNights("Enter a valid number of nights")
                .invalidRooms("Enter a valid room composition")
                .build())
            .authentication(Authentication.builder()
                .myProfile("My Profile")
                .logoutButton("Log out")
                .build())
            .build();
  }

  private List<Country> getCountriesMocked() {
    var countries = Country.builder()
        .code("gb")
        .language("English")
        .flagUrl("/etc/clientlibs/pi-header/resources/images/british-round.svg")
        .url("/gb/en/inn-business/home.html")
        .build();

    return List.of(countries);
  }

  private List<CountryDto> getCountries() {
    var countries = CountryDto.builder()
        .code("gb")
        .language("English")
        .flagUrl("/etc/clientlibs/pi-header/resources/images/british-round.svg")
        .url("/gb/en/inn-business/home.html")
        .build();

    return List.of(countries);
  }

  private List<String> getMonths() {
    return List.of("January", "February", "March", "April", "May", "June", "July",
        "August", "September", "October", "November", "December");
  }

  private List<String> getWeekdaysShort() {
    return List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
  }
}

