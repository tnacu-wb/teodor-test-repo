package uk.co.whitbread.content.infrastructure.rest.client.inn.business.header;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_HEADER_CONTENT_INFO_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_LAYOUT_INFO_EXCEPTION;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.adapter.HeaderAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.in.HeaderContentResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.in.LayoutResponseAemDto;
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
@SuppressWarnings("unchecked")
class HeaderAemClientTests {

  @InjectMocks
  private HeaderAemClient headerAemClient;

  @Mock
  private WebClient webClient;

  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @Mock
  private WebClient.ResponseSpec responseSpec;

  @BeforeEach
  public void initWebClient() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
  }

  @Test
  void getLayoutInfoShouldReturnOK() {
    //Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(LayoutResponseAemDto.class)).thenReturn(getLayoutAemInformation());

    //Act
    final var layoutAemResponse =
        headerAemClient.getLayoutInformation(createLayoutRequest());

    //Assert
    assertThat(layoutAemResponse, notNullValue());
  }

  @Test
  void getHeaderContentInfoShouldReturnOK() {
    //Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HeaderContentResponseAemDto.class)).thenReturn(getHeaderAemInformation());

    //Act
    final var headerContentAemResponse =
        headerAemClient.getHeaderContentInformation(createHeaderContentRequest());

    //Assert
    assertThat(headerContentAemResponse, notNullValue());

  }

  @Test
  void getLayoutInfoShouldReturnException() {
    //Arrange
    LayoutRequestAemDto layoutRequestAemDto = createLayoutRequest();
    var exception = new AemResponseException(
        AEM_LAYOUT_INFO_EXCEPTION,
        "Unable to get InnBusiness layout information.",
        new Exception());
    when(responseSpec.onStatus(any(), any())).thenThrow(exception);

    //Act
    var actual =
        assertThrows(AemResponseException.class,
            () -> headerAemClient.getLayoutInformation(layoutRequestAemDto));

    //Assert
    assertThat(actual, notNullValue());

    assertThat(actual.getGlobalErrTextTemplate(),
        is(AEM_LAYOUT_INFO_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is("Unable to get InnBusiness layout information."));
    assertThat(actual.getErrorCode(), is(AEM_LAYOUT_INFO_EXCEPTION.getCode()));
  }

  @Test
  void getHeaderContentInfoShouldReturnException() {
    //Arrange
    HeaderRequestAemDto headerRequestAemDto = createHeaderContentRequest();
    var exception = new AemResponseException(
        AEM_HEADER_CONTENT_INFO_EXCEPTION,
        "Unable to get InnBusiness header content information.",
        new Exception());
    when(responseSpec.onStatus(any(), any())).thenThrow(exception);

    //Act
    var actual =
        assertThrows(AemResponseException.class,
            () -> headerAemClient.getHeaderContentInformation(headerRequestAemDto));

    //Assert
    assertThat(actual, notNullValue());

    assertThat(actual.getGlobalErrTextTemplate(),
        is(AEM_HEADER_CONTENT_INFO_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is("Unable to get InnBusiness header content information."));
    assertThat(actual.getErrorCode(), is(AEM_HEADER_CONTENT_INFO_EXCEPTION.getCode()));
  }

  private LayoutRequestAemDto createLayoutRequest() {
    return LayoutRequestAemDto.builder()
        .language("en")
        .dictionary("common-layout")
        .build();
  }

  private HeaderRequestAemDto createHeaderContentRequest() {
    return HeaderRequestAemDto.builder()
        .country("gb")
        .language("en")
        .build();
  }

  private Mono<LayoutResponseAemDto> getLayoutAemInformation() {

    var response = LayoutResponseAemDto.builder()
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
    return Mono.just(response);
  }

  private Mono<HeaderContentResponseAemDto> getHeaderAemInformation() {
    var response = HeaderContentResponseAemDto.builder()
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

    return Mono.just(response);
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
