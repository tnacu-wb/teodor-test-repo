package uk.co.whitbread.content.infrastructure.rest.client.inn.business.header;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import uk.co.whitbread.content.domain.model.inn.business.header.in.HeaderRequest;
import uk.co.whitbread.content.domain.model.inn.business.header.in.LayoutRequest;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Content;
import uk.co.whitbread.content.domain.model.inn.business.header.out.HeaderResponse;
import uk.co.whitbread.content.domain.model.inn.business.header.out.Layout;
import uk.co.whitbread.content.domain.ports.secondary.HeaderOutPort;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.adapter.HeaderAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.mapper.HeaderContentRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.mapper.HeaderContentResponseMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.mapper.LayoutRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.mapper.LayoutResponseMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.in.LayoutResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.in.LayoutResponseDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.BookingsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.CardsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.ContactDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.EmployeesDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.FaqDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.HelpDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.HomeDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.ManageAccountDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.ManageDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.MenuDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.OptionsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.ProfileDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.SidebarDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.SpendingDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.TourDto;

@Slf4j
@RequiredArgsConstructor
public class HeaderOutPortImpl implements HeaderOutPort {

  private final HeaderAemClient aemClient;
  private final LayoutRequestMapper layoutRequestMapper;
  private final LayoutResponseMapper layoutResponseMapper;
  private final HeaderContentRequestMapper headerContentRequestMapper;
  private final HeaderContentResponseMapper headerContentResponseMapper;

  public static final String COMMON_LAYOUT = "common-layout";


  @Override
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "InnbLayoutCache")
  public Layout getLayoutInformation(LayoutRequest layoutRequest) {

    var request = layoutRequestMapper.toDto(layoutRequest);
    var layoutResponseAemDto = aemClient.getLayoutInformation(request);
    var layoutResponseDto = convertAemLayoutToDto(layoutResponseAemDto);

    return layoutResponseMapper.toModel(layoutResponseDto);
  }

  @Override
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "InnbHeaderCache")
  public HeaderResponse getHeaderInformation(HeaderRequest headerRequest) {

    var layoutRequest = new LayoutRequest(COMMON_LAYOUT, headerRequest.getLanguage());
    Layout layout = getLayoutInformation(layoutRequest);

    var headerContentRequest = new HeaderRequest(headerRequest.getCountry(),
        headerRequest.getLanguage());
    Content content = headerContentResponseMapper.toModel(aemClient.getHeaderContentInformation(
        headerContentRequestMapper.toDto(headerContentRequest)));

    return new HeaderResponse(content, layout);
  }

  private static LayoutResponseDto convertAemLayoutToDto(
      LayoutResponseAemDto layoutResponseAemDto) {

    SidebarDto sidebar = new SidebarDto();
    sidebar.setCollapse(layoutResponseAemDto.getSidebarCollapse());
    sidebar.setExpand(layoutResponseAemDto.getSidebarExpand());

    ManageAccountDto manageAccount = getManageAccountDto(layoutResponseAemDto);
    MenuDto menu = getMenuDto(layoutResponseAemDto);
    HelpDto help = getHelpDto(layoutResponseAemDto);

    LayoutResponseDto layout = new LayoutResponseDto();
    layout.setManageAccount(manageAccount);
    layout.setMenu(menu);
    layout.setHelp(help);
    layout.setSidebar(sidebar);
    return layout;
  }

  private static HelpDto getHelpDto(LayoutResponseAemDto layoutResponseAemDto) {
    HelpDto help = new HelpDto();
    FaqDto faq = new FaqDto(layoutResponseAemDto.getFaqLabel(),
        layoutResponseAemDto.getHelpFaqIcon());

    ContactDto contact = new ContactDto();
    contact.setLabel(layoutResponseAemDto.getHelpContactLabel());
    contact.setIcon(layoutResponseAemDto.getHelpContactIcon());

    TourDto tour = new TourDto(layoutResponseAemDto.getHelpTourLabel(),
        layoutResponseAemDto.getHelpTourIcon());

    help.setFaq(faq);
    help.setNeedHelp(layoutResponseAemDto.getNeedHelp());
    help.setTour(tour);
    help.setContact(contact);
    return help;
  }

  private static ManageAccountDto getManageAccountDto(LayoutResponseAemDto layoutResponseAemDto) {
    CardsDto cards = new CardsDto();
    cards.setTitle(layoutResponseAemDto.getCardsTitle());
    cards.setLink(layoutResponseAemDto.getCardsLink());
    cards.setIcon(layoutResponseAemDto.getCardsIcon());

    ProfileDto profile = new ProfileDto();
    profile.setTitle(layoutResponseAemDto.getProfileTitle());
    profile.setLink(layoutResponseAemDto.getProfileLink());
    profile.setText(layoutResponseAemDto.getProfileText());

    EmployeesDto employees = new EmployeesDto();
    employees.setTitle(layoutResponseAemDto.getEmployeesTitle());
    employees.setLink(layoutResponseAemDto.getEmployeesLink());
    employees.setIcon(layoutResponseAemDto.getEmployeesIcon());

    ManageAccountDto manageAccount = new ManageAccountDto();
    manageAccount.setCards(cards);
    manageAccount.setProfile(profile);
    manageAccount.setTitle(layoutResponseAemDto.getManageAccountTitle());
    manageAccount.setEmployees(employees);
    return manageAccount;
  }

  private static MenuDto getMenuDto(LayoutResponseAemDto layoutResponseAemDto) {
    BookingsDto bookings = new BookingsDto();
    bookings.setLabel(layoutResponseAemDto.getBookingsLabel());
    bookings.setIcon(layoutResponseAemDto.getMenuBookingsIcon());
    bookings.setIconActive(layoutResponseAemDto.getMenuBookingsIconActive());

    SpendingDto spending = new SpendingDto();
    spending.setLabel(layoutResponseAemDto.getMenuSpendingLabel());
    spending.setIcon(layoutResponseAemDto.getMenuSpendingIcon());
    spending.setIconActive(layoutResponseAemDto.getMenuSpendingIconActive());

    HomeDto home = new HomeDto();
    home.setLabel(layoutResponseAemDto.getHomeLabel());
    home.setIcon(layoutResponseAemDto.getHomeIcon());
    home.setIconActive(layoutResponseAemDto.getHomeIconActive());

    ContactDto contact = new ContactDto();
    contact.setLabel(layoutResponseAemDto.getMenuContactLabel());
    contact.setIcon(layoutResponseAemDto.getMenuContactIcon());
    contact.setIconActive(layoutResponseAemDto.getMenuContactIconActive());

    ManageDto manage = getManageDto(layoutResponseAemDto);

    MenuDto menu = new MenuDto();
    menu.setBookings(bookings);
    menu.setHome(home);
    menu.setManage(manage);
    menu.setSpending(spending);
    menu.setContact(contact);
    return menu;
  }

  private static ManageDto getManageDto(LayoutResponseAemDto layoutResponseAemDto) {
    OptionsDto options = getOptionsDto(layoutResponseAemDto);

    ManageDto manage = new ManageDto();
    manage.setLabel(layoutResponseAemDto.getMenuManageLabel());
    manage.setIcon(layoutResponseAemDto.getMenuManageIcon());
    manage.setIconActive(layoutResponseAemDto.getMenuManageIconActive());
    manage.setOptions(options);

    return manage;
  }

  private static OptionsDto getOptionsDto(LayoutResponseAemDto layoutResponseAemDto) {
    OptionsDto options = new OptionsDto();
    options.setEmployees(layoutResponseAemDto.getMenuManageOptionsEmployees());
    options.setAllowances(layoutResponseAemDto.getMenuManageOptionsAllowances());
    options.setAlerts(layoutResponseAemDto.getMenuManageOptionsAlerts());
    options.setCards(layoutResponseAemDto.getMenuManageOptionsCards());
    options.setQuestions(layoutResponseAemDto.getMenuManageOptionsQuestions());
    options.setCompany(layoutResponseAemDto.getMenuManageOptionsCompany());

    return options;
  }

}
