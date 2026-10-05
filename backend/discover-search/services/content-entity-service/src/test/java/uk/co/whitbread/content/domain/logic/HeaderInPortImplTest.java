package uk.co.whitbread.content.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import uk.co.whitbread.content.domain.model.inn.business.header.out.Brand;
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
import uk.co.whitbread.content.domain.ports.secondary.HeaderOutPort;

@ExtendWith(MockitoExtension.class)
class HeaderInPortImplTest {

  @Mock
  private HeaderOutPort headerOutPort;

  @InjectMocks
  private HeaderInPortImpl headerInPort;

  @Test
  void getLayoutInformationShouldReturnOk() {
    when(this.headerOutPort.getLayoutInformation(any())).thenReturn(mockLayoutInformation());

    final var aemResponse = headerInPort.getLayoutInformation(createLayoutRequest());

    assertEquals("Manage account", aemResponse.getManageAccount().getTitle());
    assertEquals("Bookings", aemResponse.getMenu().getBookings().getLabel());
    assertEquals("Need Help?", aemResponse.getHelp().getNeedHelp());
    assertEquals("/content/dam/global/icons/common/chevron-left.svg", aemResponse.getSidebar().getCollapse());
    assertEquals("Company details", aemResponse.getMenu().getManage().getOptions().getCompany());

    verify(headerOutPort).getLayoutInformation(any());
  }

  @Test
  void getHeaderInformationShouldReturnOk() {
    when(this.headerOutPort.getHeaderInformation(any())).thenReturn(mockHeaderInformation());

    final var aemResponse = headerInPort.getHeaderInformation(createHeaderRequest());

    assertEquals("Manage account", aemResponse.getLayout().getManageAccount().getTitle());
    assertEquals("Bookings", aemResponse.getLayout().getMenu().getBookings().getLabel());
    assertEquals("Need Help?", aemResponse.getLayout().getHelp().getNeedHelp());
    assertEquals("/content/dam/global/icons/common/chevron-left.svg", aemResponse.getLayout().getSidebar().getCollapse());

    assertEquals("Inn Business", aemResponse.getContent().getHeader().getImageAlt());
    assertEquals("today", aemResponse.getContent().getGlobal().getToday());
    assertEquals("gb", aemResponse.getContent().getCountries().get(0).getCode());
    assertEquals("Where to?", aemResponse.getContent().getForm().getWhere());

    assertEquals("Double", aemResponse.getContent().getGlobal().getDoubleValue());
    assertEquals("September", aemResponse.getContent().getForm().getDatePicker().getMonths().get(8));
    assertEquals("Enter a location or hotel", aemResponse.getContent().getForm().getInvalidLocation());
    assertEquals("Log out", aemResponse.getContent().getAuthentication().getLogoutButton());

    verify(headerOutPort).getHeaderInformation(any());
  }

  private LayoutRequest createLayoutRequest(){
    return LayoutRequest.builder()
        .language("en")
        .dictionary("common-layout")
        .build();
  }

  private HeaderRequest createHeaderRequest() {
    return HeaderRequest.builder()
        .country("gb")
        .language("en")
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

  private HeaderResponse mockHeaderInformation() {

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
                .removeRoom("Remove room")
                .invalidLocation("Enter a location or hotel")
                .invalidNights("Enter a valid number of nights")
                .invalidRooms("Enter a valid room composition")
                .build())
            .authentication(Authentication.builder()
                .myProfile("My Profile")
                .logoutButton("Log out")
                .build())
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
}
