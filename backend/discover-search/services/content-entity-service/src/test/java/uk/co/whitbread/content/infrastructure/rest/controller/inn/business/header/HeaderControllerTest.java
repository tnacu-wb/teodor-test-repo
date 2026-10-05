package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header;

import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.content.domain.model.inn.business.header.in.HeaderRequest;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Authentication;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Bookings;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Brand;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Cards;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Contact;
import uk.co.whitbread.content.domain.model.inn.business.header.out.ContactBanner;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Content;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Country;
import uk.co.whitbread.content.domain.model.inn.business.header.out.DatePicker;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Employees;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Faq;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Form;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Global;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Header;
import uk.co.whitbread.content.domain.model.inn.business.header.out.HeaderResponse;
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
import uk.co.whitbread.content.domain.ports.primary.HeaderInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.mapper.HeaderRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.mapper.HeaderResponseDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.in.HeaderRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.AuthenticationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.BookingsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.CardsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.ContactBannerDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.ContactDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.ContentDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.CountryDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.DatePickerDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.EmployeesDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.FaqDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.FormDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.GlobalDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.HeaderDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.HeaderResponseDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.HelpDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.HomeDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.LayoutDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.ManageAccountDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.ManageDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.MenuDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.OptionsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.ProfileDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.SidebarDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.SpendingDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.TourDto;

@ExtendWith(MockitoExtension.class)
class HeaderControllerTest {

  @InjectMocks
  private HeaderController headerController;
  @Mock
  private HeaderInPort headerInPort;
  @Mock
  private HeaderRequestDtoMapper headerRequestDtoMapper;
  @Mock
  private HeaderResponseDtoMapper headerResponseDtoMapper;

  @Test
  void getHeaderInformationShouldReturnOK200(){
    //Arrange
    var headerRequestDto = getHeaderRequestDtoGbEn();
    var headerRequest = getHeaderRequestGbEn();

    Mockito.when(headerRequestDtoMapper.toModel(headerRequestDto))
        .thenReturn(headerRequest);
    Mockito.when(headerInPort.getHeaderInformation(headerRequest))
        .thenReturn(getHeaderInformation());
    Mockito.when(headerResponseDtoMapper.toDto(getHeaderInformation()))
        .thenReturn(getHeaderInformationDto());

    //Act
    final ResponseEntity<HeaderResponseDto> response =
        headerController.getHeaderInfo(headerRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void getHeaderInformation__ShouldFindHeaderInformation() {
    //Arrange
    var headerRequestDto = getHeaderRequestDtoGbEn();
    var headerRequest = getHeaderRequestGbEn();

    Mockito.when(headerRequestDtoMapper.toModel(headerRequestDto))
        .thenReturn(headerRequest);
    Mockito.when(headerInPort.getHeaderInformation(headerRequest))
        .thenReturn(getHeaderInformation());
    Mockito.when(headerResponseDtoMapper.toDto(getHeaderInformation()))
        .thenReturn(getHeaderInformationDto());

    //Act
    final var request = headerRequestDtoMapper.toModel(headerRequestDto);
    final var headerInformation = headerInPort.getHeaderInformation(request);
    final var domainContentRequest = headerResponseDtoMapper.toDto(headerInformation);
    final ResponseEntity<HeaderResponseDto> response =
        headerController.getHeaderInfo(headerRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), domainContentRequest.getContent().getHeader().getImageAlt(),
        Objects.requireNonNull(response.getBody()).getContent().getHeader().getImageAlt());
    assertEquals(response.toString(), domainContentRequest.getContent().getGlobal().getLabelAddRoom(),
        response.getBody().getContent().getGlobal().getLabelAddRoom());
    assertEquals(response.toString(), domainContentRequest.getContent().getGlobal().getDoubleValue(),
        response.getBody().getContent().getGlobal().getDoubleValue());
    assertEquals(response.toString(), domainContentRequest.getLayout().getHelp().getFaq().getLabel(),
        response.getBody().getLayout().getHelp().getFaq().getLabel());
    assertEquals(response.toString(), domainContentRequest.getLayout().getMenu().getManage().getOptions().getAlerts(),
        response.getBody().getLayout().getMenu().getManage().getOptions().getAlerts());
    assertEquals(response.toString(), domainContentRequest.getContent().getContactBanner().getType(),
        Objects.requireNonNull(response.getBody()).getContent().getContactBanner().getType());
    assertEquals(response.toString(), domainContentRequest.getContent().getContactBanner().getDate(),
        response.getBody().getContent().getContactBanner().getDate());
  }

  @Test
  void getHeaderInformation__ShouldIncludeContactBanner() {
    //Arrange
    var headerRequestDto = getHeaderRequestDtoGbEn();
    var headerRequest = getHeaderRequestGbEn();

    Mockito.when(headerRequestDtoMapper.toModel(headerRequestDto))
        .thenReturn(headerRequest);
    Mockito.when(headerInPort.getHeaderInformation(headerRequest))
        .thenReturn(getHeaderInformation());
    Mockito.when(headerResponseDtoMapper.toDto(getHeaderInformation()))
        .thenReturn(getHeaderInformationDto());

    //Act
    final ResponseEntity<HeaderResponseDto> response =
        headerController.getHeaderInfo(headerRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    Assertions.assertNotNull(response.getBody());
    Assertions.assertNotNull(response.getBody().getContent().getContactBanner());
    assertEquals(response.toString(), "info", response.getBody().getContent().getContactBanner().getType());
    assertEquals(response.toString(), "31st March 2026", response.getBody().getContent().getContactBanner().getDate());
    assertEquals(response.toString(), "31st March 2026 We can not currently process Visa card transactions.",
        response.getBody().getContent().getContactBanner().getText());
    Assertions.assertEquals(3, response.getBody().getContent().getContactBanner().getEnabledPages().size());
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

  private HeaderResponse getHeaderInformation() {
    return HeaderResponse.builder()
        .content(Content.builder()
            .header(Header.builder()
                .image("/content/dam/global/icons/brand/logo-pi-inn-business.svg")
                .imageAlt("Inn Business")
                .build())
            .global(Global.builder()
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
                .brand(Brand.builder()
                    .piLogo("/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc.svg")
                    .pidLogo("/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc.svg")
                    .hubLogo("/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc-hub.svg")
                    .zipLogo("/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc-zip.svg")
                    .build())
                .build())
            .countries(getCountries())
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
                .invalidLocation("Enter a location or hotel")
                .invalidNights("Enter a valid number of nights")
                .invalidRooms("Enter a valid room composition")
                .build())
            .authentication(Authentication.builder()
                .myProfile("My Profile")
                .logoutButton("Log out")
                .build())
            .contactBanner(getContactBanner())
            .build())
        .layout(mockLayoutInformation())
        .build();
  }

  private List<Country> getCountries() {
    var countries = Country.builder()
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

  private HeaderResponseDto getHeaderInformationDto() {
    return HeaderResponseDto.builder()
        .content(getContentDto())
        .layout(getLayoutDto())
        .build();
  }

  private ContentDto getContentDto() {
    return ContentDto.builder()
        .header(HeaderDto.builder()
            .image("/content/dam/global/icons/brand/logo-pi-inn-business.svg")
            .imageAlt("Inn Business")
            .build())
        .global(GlobalDto.builder()
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
        .countries(getCountriesDto())
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
            .invalidLocation("Enter a location or hotel")
            .invalidNights("Enter a valid number of nights")
            .invalidRooms("Enter a valid room composition")
            .build())
        .authentication(AuthenticationDto.builder()
            .myProfile("My Profile")
            .logoutButton("Log out")
            .build())
        .contactBanner(getContactBannerDto())
        .build();
  }

  private List<CountryDto> getCountriesDto() {
    var countries = CountryDto.builder()
        .code("gb")
        .language("English")
        .flagUrl("/etc/clientlibs/pi-header/resources/images/british-round.svg")
        .url("/gb/en/inn-business/home.html")
        .build();

    return List.of(countries);
  }

  private LayoutDto getLayoutDto() {
    return LayoutDto.builder()
        .manageAccount(ManageAccountDto.builder()
            .cards(CardsDto.builder()
                .title("Cards")
                .link("View cards")
                .icon("/content/dam/global/icons/common/payment-card.svg")
                .build())
            .title("Manage account")
            .profile(ProfileDto.builder()
                .title("Company profile")
                .text(
                    "Enhance your stay with us! Complete your profile to enjoy customised services.")
                .link("Edit company details")
                .build())
            .employees(EmployeesDto.builder()
                .title("Employees")
                .link("View employee")
                .icon("/content/dam/global/icons/common/person.svg")
                .build())
            .build())
        .menu(MenuDto.builder()
            .bookings(BookingsDto.builder()
                .label("Bookings")
                .icon("/content/dam/global/icons/common/suitcase.svg")
                .iconActive("/content/dam/global/icons/common/suitcase-solid.svg")
                .build())
            .spending(SpendingDto.builder()
                .label("Spending")
                .icon("/content/dam/global/icons/common/chart-bars.svg")
                .iconActive("/content/dam/global/icons/common/chart-bars-solid.svg")
                .build())
            .home(HomeDto.builder()
                .label("Home")
                .icon("/content/dam/global/icons/common/home.svg")
                .iconActive("/content/dam/global/icons/common/home-solid.svg")
                .build())
            .manage(ManageDto.builder()
                .label("Manage")
                .icon("/content/dam/global/icons/common/chart-pie.svg")
                .iconActive("/content/dam/global/icons/common/chart-pie-solid.svg")
                .options(OptionsDto.builder()
                    .employees("Manage employees")
                    .allowances("Booking allowances")
                    .alerts("Booking alerts")
                    .cards("Card management")
                    .questions("Employee questions")
                    .company("Company details")
                    .build())
                .build())
            .build())
        .help(HelpDto.builder()
            .faq(FaqDto.builder()
                .label("FAQs")
                .icon("/content/dam/global/icons/common/speech-bubble.svg")
                .build())
            .needHelp("Need Help?")
            .tour(TourDto.builder()
                .label("Tour")
                .icon("/content/dam/global/icons/common/hand-right.svg")
                .build())
            .contact(ContactDto.builder()
                .label("Contact")
                .icon("/content/dam/global/icons/common/person.svg")
                .build())
            .build())
        .sidebar(SidebarDto.builder()
            .collapse("/content/dam/global/icons/common/chevron-left.svg")
            .expand("/content/dam/global/icons/common/chevron-right.svg")
            .build())
        .build();
  }

  private HeaderRequest getHeaderRequestGbEn() {
    return HeaderRequest.builder()
        .country("gb")
        .language("en")
        .build();
  }

  private HeaderRequestDto getHeaderRequestDtoGbEn() {
    return HeaderRequestDto.builder()
        .country("gb")
        .language("en")
        .build();
  }

  private ContactBanner getContactBanner() {
    return ContactBanner.builder()
        .type("info")
        .text("31st March 2026 We can not currently process Visa card transactions.")
        .date("31st March 2026")
        .enabledPages(List.of("/gb/en/contact-us.html", "/de/de/konkat.html", "en-gb/contact-us"))
        .build();
  }

  private ContactBannerDto getContactBannerDto() {
    return ContactBannerDto.builder()
        .type("info")
        .text("31st March 2026 We can not currently process Visa card transactions.")
        .date("31st March 2026")
        .enabledPages(List.of("/gb/en/contact-us.html", "/de/de/konkat.html", "en-gb/contact-us"))
        .build();
  }

}
