package uk.co.whitbread.content.infrastructure.rest.controller.header.data;

import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.content.domain.model.index.header.data.in.IndexHeaderDataRequest;
import uk.co.whitbread.content.domain.model.index.header.data.out.AccountLink;
import uk.co.whitbread.content.domain.model.index.header.data.out.AmazonChat;
import uk.co.whitbread.content.domain.model.index.header.data.out.Authentication;
import uk.co.whitbread.content.domain.model.index.header.data.out.Brand;
import uk.co.whitbread.content.domain.model.index.header.data.out.Business;
import uk.co.whitbread.content.domain.model.index.header.data.out.Config;
import uk.co.whitbread.content.domain.model.index.header.data.out.ContactBanner;
import uk.co.whitbread.content.domain.model.index.header.data.out.Content;
import uk.co.whitbread.content.domain.model.index.header.data.out.Country;
import uk.co.whitbread.content.domain.model.index.header.data.out.ForgottenPassword;
import uk.co.whitbread.content.domain.model.index.header.data.out.Form;
import uk.co.whitbread.content.domain.model.index.header.data.out.Global;
import uk.co.whitbread.content.domain.model.index.header.data.out.Header;
import uk.co.whitbread.content.domain.model.index.header.data.out.IndexHeaderData;
import uk.co.whitbread.content.domain.model.index.header.data.out.Leisure;
import uk.co.whitbread.content.domain.model.index.header.data.out.Login;
import uk.co.whitbread.content.domain.model.index.header.data.out.Menu;
import uk.co.whitbread.content.domain.model.index.header.data.out.NavOption;
import uk.co.whitbread.content.domain.model.index.header.data.out.Notifications;
import uk.co.whitbread.content.domain.model.index.header.data.out.Offer;
import uk.co.whitbread.content.domain.model.index.header.data.out.Promotions;
import uk.co.whitbread.content.domain.model.index.header.data.out.Results;
import uk.co.whitbread.content.domain.model.index.header.data.out.RoomCodes;
import uk.co.whitbread.content.domain.model.index.header.data.out.PromotionBanner;
import uk.co.whitbread.content.domain.model.index.header.data.out.SubNav;
import uk.co.whitbread.content.domain.ports.primary.ContentInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.mapper.IndexHeaderDataDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.mapper.IndexHeaderDataRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.in.IndexHeaderDataRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.AuthenticationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.AmazonChatDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.BrandDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.BusinessDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.ConfigDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.ContactBannerDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.ContentDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.CountryDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.DatePickerDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.ForgottenPasswordDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.FormDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.GlobalDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.HeaderDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.IndexHeaderDataDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.LeisureDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.LoginDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.MenuDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.NavOptionDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.NotificationsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.PromotionDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.OfferDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.ResultsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.RoomCodesDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.PromotionBannerDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.SubNavDto;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.HeroDto;

@ExtendWith(MockitoExtension.class)
class IndexHeaderDataControllerTest {

  @InjectMocks
  private IndexHeaderDataController indexHeaderDataControllerUnderTest;
  @Mock
  private ContentInPort contentInPort;
  @Mock
  private IndexHeaderDataDtoMapper indexHeaderDataDtoMapper;
  @Mock
  private IndexHeaderDataRequestDtoMapper indexHeaderDataRequestDtoMapper;


  @Test
  void getIndexHeaderData__ShouldReturnOK200() {
    //Arrange
    var indexHeaderDataRequestDto = IndexHeaderDataRequestDto.builder()
            .country("gb").language("en").businessBooker(Boolean.FALSE).build();

    var indexHeaderDataRequest = IndexHeaderDataRequest.builder()
            .country("gb").language("en").businessBooker(Boolean.FALSE).build();

    Mockito.when(indexHeaderDataRequestDtoMapper.toDomainModel(indexHeaderDataRequestDto))
        .thenReturn(indexHeaderDataRequest);
    Mockito.when(contentInPort.getIndexHeaderData(indexHeaderDataRequest))
        .thenReturn(getIndexHeaderData());
    Mockito.when(indexHeaderDataDtoMapper.toDtoModel(getIndexHeaderData()))
        .thenReturn(getIndexHeaderDataDto());

    //act
    final ResponseEntity<IndexHeaderDataDto> response =
        indexHeaderDataControllerUnderTest.getIndexHeaderData(
            indexHeaderDataRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void getIndexHeaderData__ShouldFindHeaderData() {
    //Arrange
    var indexHeaderDataRequestDto = IndexHeaderDataRequestDto.builder()
        .country("gb").language("en").businessBooker(Boolean.FALSE).build();

    var indexHeaderDataRequest = IndexHeaderDataRequest.builder()
        .country("gb").language("en").businessBooker(Boolean.FALSE).build();

    Mockito.when(indexHeaderDataRequestDtoMapper.toDomainModel(indexHeaderDataRequestDto))
        .thenReturn(indexHeaderDataRequest);
    Mockito.when(contentInPort.getIndexHeaderData(indexHeaderDataRequest))
        .thenReturn(getIndexHeaderData());
    Mockito.when(indexHeaderDataDtoMapper.toDtoModel(getIndexHeaderData()))
        .thenReturn(getIndexHeaderDataDto());

    //act
    final var request = indexHeaderDataRequestDtoMapper.toDomainModel(indexHeaderDataRequestDto);
    final var indexHeaderData = contentInPort.getIndexHeaderData(request);
    final var domainContentRequest = indexHeaderDataDtoMapper.toDtoModel(indexHeaderData);
    final ResponseEntity<IndexHeaderDataDto> response =
        indexHeaderDataControllerUnderTest.getIndexHeaderData(
            indexHeaderDataRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(),
        domainContentRequest.getContent().getMenu().getPromoCode(),
        response.getBody().getContent().getMenu().getPromoCode());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getMenu().getMobileMenuButton(),
        response.getBody().getContent().getMenu().getMobileMenuButton());
    assertEquals(response.toString(), domainContentRequest.getContent().getHeader().getImage(),
        response.getBody().getContent().getHeader().getImage());

    assertEquals(response.toString(), domainContentRequest.getContent().getSubNav().get(0).getTitle(),
        response.getBody().getContent().getSubNav().get(0).getTitle());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getSubNav().get(0).getNavOptions().get(0).getTitle(),
        response.getBody().getContent().getSubNav().get(0).getNavOptions().get(0).getTitle());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getSubNav().get(0).getNavOptions().get(0).getUrl(),
        response.getBody().getContent().getSubNav().get(0).getNavOptions().get(0).getUrl());

    assertEquals(response.toString(),
            domainContentRequest.getContent().getHero().getBackgroundColor(),
            response.getBody().getContent().getHero().getBackgroundColor());
    assertEquals(response.toString(),
            domainContentRequest.getContent().getHero().getHeadingTextShort(),
            response.getBody().getContent().getHero().getHeadingTextShort());
    test_globalElements(response, domainContentRequest);
    test_countries(response, domainContentRequest);
    test_notifications(response, domainContentRequest);
    test_authentication(response, domainContentRequest);
  }

  private void test_authentication(ResponseEntity<IndexHeaderDataDto> response,
      IndexHeaderDataDto domainContentRequest) {
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getLogin().getLeisure().getFormLabel(),
        response.getBody().getContent().getAuthentication().getLogin().getLeisure().getFormLabel());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getLogin().getLeisure().getEmailPlaceholder(),
        response.getBody().getContent().getAuthentication().getLogin().getLeisure().getEmailPlaceholder());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getLogin().getLeisure().getLoginButton(),
        response.getBody().getContent().getAuthentication().getLogin().getLeisure().getLoginButton());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getLogin().getBusiness().getFormLabel(),
        response.getBody().getContent().getAuthentication().getLogin().getBusiness().getFormLabel());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getLogin().getBusiness().getEmailPlaceholder(),
        response.getBody().getContent().getAuthentication().getLogin().getBusiness().getEmailPlaceholder());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getLogin().getBusiness().getLoginButton(),
        response.getBody().getContent().getAuthentication().getLogin().getBusiness().getLoginButton());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getLogin().getBusiness().getBusinessLogin(),
        response.getBody().getContent().getAuthentication().getLogin().getBusiness().getBusinessLogin());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getLogin().getBusiness().getTravelForBusiness(),
        response.getBody().getContent().getAuthentication().getLogin().getBusiness().getTravelForBusiness());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getLogin().getBusiness().getBusinessDomain(),
        response.getBody().getContent().getAuthentication().getLogin().getBusiness().getBusinessDomain());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getLogin().getPasswordPlaceholder(),
        response.getBody().getContent().getAuthentication().getLogin().getPasswordPlaceholder());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getLogin().getForgotPassword(),
        response.getBody().getContent().getAuthentication().getLogin().getForgotPassword());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getLogin().getSignupMessage(),
        response.getBody().getContent().getAuthentication().getLogin().getSignupMessage());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getLogin().getSignupLink(),
        response.getBody().getContent().getAuthentication().getLogin().getSignupLink());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getForgottenPassword().getLeisure().getFormLabel(),
        response.getBody().getContent().getAuthentication().getForgottenPassword().getLeisure().getFormLabel());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getForgottenPassword().getLeisure().getEmailPlaceholder(),
        response.getBody().getContent().getAuthentication().getForgottenPassword().getLeisure().getEmailPlaceholder());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getForgottenPassword().getLeisure().getFormTitle(),
        response.getBody().getContent().getAuthentication().getForgottenPassword().getLeisure().getFormTitle());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getForgottenPassword().getLeisure().getSubmitButton(),
        response.getBody().getContent().getAuthentication().getForgottenPassword().getLeisure().getSubmitButton());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getForgottenPassword().getBusiness().getFormLabel(),
        response.getBody().getContent().getAuthentication().getForgottenPassword().getBusiness().getFormLabel());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getForgottenPassword().getBusiness().getEmailPlaceholder(),
        response.getBody().getContent().getAuthentication().getForgottenPassword().getBusiness().getEmailPlaceholder());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getForgottenPassword().getBusiness().getFormTitle(),
        response.getBody().getContent().getAuthentication().getForgottenPassword().getBusiness().getFormTitle());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getForgottenPassword().getBusiness().getSubmitButton(),
        response.getBody().getContent().getAuthentication().getForgottenPassword().getBusiness().getSubmitButton());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getForgottenPassword().getBusiness().getBusinessLogin(),
        response.getBody().getContent().getAuthentication().getForgottenPassword().getBusiness().getBusinessLogin());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getForgottenPassword().getBusiness().getTravelForBusiness(),
        response.getBody().getContent().getAuthentication().getForgottenPassword().getBusiness().getTravelForBusiness());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getForgottenPassword().getBusiness().getBusinessDomain(),
        response.getBody().getContent().getAuthentication().getForgottenPassword().getBusiness().getBusinessDomain());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getForgottenPassword().getCancel(),
        response.getBody().getContent().getAuthentication().getForgottenPassword().getCancel());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getAccountLinks(),
        response.getBody().getContent().getAuthentication().getAccountLinks());
    assertEquals(response.toString(),
            domainContentRequest.getContent().getAuthentication().getSignUpButton(),
            response.getBody().getContent().getAuthentication().getSignUpButton());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getAuthentication().getLogoutButton(),
        response.getBody().getContent().getAuthentication().getLogoutButton());
  }
  private void test_notifications(ResponseEntity<IndexHeaderDataDto> response,
      IndexHeaderDataDto domainContentRequest) {
    assertEquals(response.toString(),
        domainContentRequest.getResults().getNotifications().getGroupBookingHeader(),
        response.getBody().getResults().getNotifications().getGroupBookingHeader());
    assertEquals(response.toString(),
        domainContentRequest.getResults().getNotifications().getGroupBookingMessage(),
        response.getBody().getResults().getNotifications().getGroupBookingMessage());
    assertEquals(response.toString(),
            domainContentRequest.getResults().getNotifications().getGroupBookingFormPageMessage(),
            response.getBody().getResults().getNotifications().getGroupBookingFormPageMessage());
  }
  private void test_countries(ResponseEntity<IndexHeaderDataDto> response,
      IndexHeaderDataDto domainContentRequest) {
    assertEquals(response.toString(),
        domainContentRequest.getContent().getCountries().get(0).getLanguage(),
        response.getBody().getContent().getCountries().get(0).getLanguage());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getCountries().get(1).getLanguage(),
        response.getBody().getContent().getCountries().get(1).getLanguage());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getCountries().get(0).getFlagUrl(),
        response.getBody().getContent().getCountries().get(0).getFlagUrl());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getCountries().get(1).getFlagUrl(),
        response.getBody().getContent().getCountries().get(1).getFlagUrl());
  }

  private void test_globalElements(ResponseEntity<IndexHeaderDataDto> response,
      IndexHeaderDataDto domainContentRequest) {
    assertEquals(response.toString(), domainContentRequest.getContent().getGlobal().getAddRoom(),
        response.getBody().getContent().getGlobal().getAddRoom());
    assertEquals(response.toString(), domainContentRequest.getContent().getGlobal().getDone(),
        response.getBody().getContent().getGlobal().getDone());
    assertEquals(response.toString(), domainContentRequest.getContent().getGlobal().getRoom(),
        response.getBody().getContent().getGlobal().getRoom());
    assertEquals(response.toString(), domainContentRequest.getContent().getGlobal().getRoomLabel(),
        response.getBody().getContent().getGlobal().getRoomLabel());
    assertEquals(response.toString(), domainContentRequest.getContent().getGlobal().getSingle(),
        response.getBody().getContent().getGlobal().getSingle());
    assertEquals(response.toString(), domainContentRequest.getContent().getGlobal().getTwin(),
        response.getBody().getContent().getGlobal().getTwin());
    assertEquals(response.toString(), domainContentRequest.getContent().getGlobal().getAccessible(),
        response.getBody().getContent().getGlobal().getAccessible());
    assertEquals(response.toString(), domainContentRequest.getContent().getGlobal().getFamily(),
        response.getBody().getContent().getGlobal().getFamily());
    assertEquals(response.toString(), domainContentRequest.getContent().getGlobal().getAdult(),
        response.getBody().getContent().getGlobal().getAdult());
    assertEquals(response.toString(), domainContentRequest.getContent().getGlobal().getAdults(),
        response.getBody().getContent().getGlobal().getAdults());
    assertEquals(response.toString(), domainContentRequest.getContent().getGlobal().getChild(),
        response.getBody().getContent().getGlobal().getChild());
    assertEquals(response.toString(), domainContentRequest.getContent().getGlobal().getChildren(),
        response.getBody().getContent().getGlobal().getChildren());
    assertEquals(response.toString(), domainContentRequest.getContent().getGlobal().getNight(),
        response.getBody().getContent().getGlobal().getNight());
    assertEquals(response.toString(), domainContentRequest.getContent().getGlobal().getRooms(),
        response.getBody().getContent().getGlobal().getRooms());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getGlobal().getAdultsLabel(),
        response.getBody().getContent().getGlobal().getAdultsLabel());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getGlobal().getChildrenLabel(),
        response.getBody().getContent().getGlobal().getChildrenLabel());
    assertEquals(response.toString(), domainContentRequest.getContent().getGlobal().getToday(),
        response.getBody().getContent().getGlobal().getToday());
    assertEquals(response.toString(), domainContentRequest.getContent().getGlobal().getTomorrow(),
        response.getBody().getContent().getGlobal().getTomorrow());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getGlobal().getBrand().getZipLogoWhite(),
        response.getBody().getContent().getGlobal().getBrand().getZipLogoWhite());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getGlobal().getBrand().getPiLogo(),
        response.getBody().getContent().getGlobal().getBrand().getPiLogo());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getGlobal().getBrand().getPidLogo(),
        response.getBody().getContent().getGlobal().getBrand().getPidLogo());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getGlobal().getBrand().getZipLogo(),
        response.getBody().getContent().getGlobal().getBrand().getZipLogo());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getGlobal().getBrand().getPi(),
        response.getBody().getContent().getGlobal().getBrand().getPi());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getGlobal().getBrand().getHub(),
        response.getBody().getContent().getGlobal().getBrand().getHub());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getGlobal().getBrand().getPid(),
        response.getBody().getContent().getGlobal().getBrand().getPid());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getGlobal().getBrand().getZip(),
        response.getBody().getContent().getGlobal().getBrand().getZip());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getGlobal().getBrand().getHubLogo(),
        response.getBody().getContent().getGlobal().getBrand().getHubLogo());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getGlobal().getBrand().getHubBadge(),
        response.getBody().getContent().getGlobal().getBrand().getHubBadge());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getGlobal().getDoubleLabel(),
        response.getBody().getContent().getGlobal().getDoubleLabel());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getGlobal().getThirdParties(),
        response.getBody().getContent().getGlobal().getThirdParties());
    assertEquals(response.toString(),
        domainContentRequest.getContent().getGlobal().getOffers(),
        response.getBody().getContent().getGlobal().getOffers());
    assertEquals(response.toString(),
            domainContentRequest.getContent().getGlobal().getPromotions(),
            response.getBody().getContent().getGlobal().getPromotions());
  }

  @Test
  void getIndexHeaderData__ShouldNotFindHeaderData() {
    //Arrange
    var indexHeaderDataRequestDto = IndexHeaderDataRequestDto.builder()
        .country("gb").language("en").businessBooker(Boolean.FALSE).build();

    var indexHeaderDataRequest = IndexHeaderDataRequest.builder()
        .country("gb").language("en").businessBooker(Boolean.FALSE).build();

    Mockito.when(indexHeaderDataRequestDtoMapper.toDomainModel(indexHeaderDataRequestDto))
        .thenReturn(indexHeaderDataRequest);
    Mockito.when(contentInPort.getIndexHeaderData(indexHeaderDataRequest))
        .thenReturn(getIndexHeaderData());
    Mockito.when(indexHeaderDataDtoMapper.toDtoModel(getIndexHeaderData()))
        .thenReturn(getIndexHeaderDataDto());

    //act
    final var request = indexHeaderDataRequestDtoMapper.toDomainModel(indexHeaderDataRequestDto);
    final var indexHeaderData = contentInPort.getIndexHeaderData(request);
    final var domainContentRequest = indexHeaderDataDtoMapper.toDtoModel(indexHeaderData);
    final ResponseEntity<IndexHeaderDataDto> response =
        indexHeaderDataControllerUnderTest.getIndexHeaderData(
            null);

    //Assert
    Assertions.assertNull(response.getBody());
  }

  private IndexHeaderData getIndexHeaderData() {
    return IndexHeaderData.builder()
        .content(getContent())
        .config(getConfig())
        .build();
  }

  private Content getContent() {
    return Content.builder()
        .global(getGlobal())
        .form(getForm())
        .results(getResults())
        .menu(getMenu())
        .countries(getCountries())
        .header(getHeader())
        .subNav(getSubNav())
        .authentication(getAuthentication())
        .contactBanner(getContactBanner())
        .build();
  }

  private List<SubNav> getSubNav() {
    var subNav = SubNav.builder()
        .title("Short breaks")
        .navOptions(getNavOptions())
        .build();
    return List.of(subNav);
  }

  private List<NavOption> getNavOptions() {
    var navOption = NavOption.builder()
        .title("City breaks")
        .url("/gb/en/short-breaks/city-breaks.html?INTCMP=topNav")
        .build();

    return List.of(navOption);
  }

  private Authentication getAuthentication() {
    return Authentication.builder()
        .login(getLogin())
        .forgottenPassword(getForgottenPassword())
        .accountLinks(List.of(getAccountLink()))
        .signUpButton("Sign up")
        .logoutButton("Log out")
        .build();

  }

  private ForgottenPassword getForgottenPassword() {
    var leisure = Leisure.builder()
        .formLabel("We will send you an email with instructions to reset your password")
        .emailPlaceholder("Email address")
        .formTitle("Reset your password")
        .submitButton("Submit")
        .build();

    var business = Business.builder()
        .formLabel("We will send you an email with instructions to reset your password")
        .emailPlaceholder("Email address")
        .formTitle("Reset your password")
        .submitButton("Submit")
        .businessLogin("Business login")
        .travelForBusiness("Travel for business")
        .businessDomain("Business domain")
        .build();

    return ForgottenPassword.builder()
        .leisure(leisure)
        .business(business)
        .cancel("Cancel")
        .build();
  }

  private Login getLogin() {
    var leisure = Leisure.builder()
        .formLabel("Log into your Premier Inn account")
        .emailPlaceholder("Email address")
        .loginButton("Log in")
        .build();

    var business = Business.builder()
        .formLabel("Log into your Premier Inn account")
        .emailPlaceholder("Email address")
        .loginButton("Log in")
        .businessLogin("Business login")
        .travelForBusiness("Travel for business")
        .businessDomain("Business domain")
        .build();

    return Login.builder()
        .leisure(leisure)
        .business(business)
        .passwordPlaceholder("Password")
        .forgotPassword("Forgotten password?")
        .signupMessage("Don't have an account yet?")
        .signupLink("Sign up here")
        .build();
  }

  private Header getHeader() {
    return Header.builder()
        .image("/content/dam/pi/websites/desktop/icons/brand/pi-logo-rest-easy.svg")
        .build();
  }

  private List<Country> getCountries() {
    var countryEn = Country.builder()
        .language("English")
        .flagUrl("/etc/clientlibs/pi-header/resources/images/british-round.svg")
        .build();

    var countryDe = Country.builder()
        .language("German")
        .flagUrl("/etc/clientlibs/pi-header/resources/images/germany-round.svg")
        .build();

    return List.of(countryEn, countryDe);
  }

  private Global getGlobal() {
    Global global = new Global();
    global.setDone("Done");
    global.setRoom("room");
    global.setAddRoom("Add room");
    global.setRoomLabel("Room");
    global.setSingle("Single");
    global.setTwin("Twin");
    global.setAccessible("Accessible");
    global.setFamily("Family");
    global.setAdult("adult");
    global.setAdults("adults");
    global.setChild("child");
    global.setChildren("children");
    global.setNight("night");
    global.setRooms("rooms");
    global.setAdultsLabel("Adults");
    global.setChildrenLabel("Children");
    global.setThirdParties("TRA,TRIVAGO,GHF,BMP");
    global.setToday("Today");
    global.setTomorrow("Tomorrow");
    global.setDoubleLabel("Double");
    global.setBrand(getBrand());
    global.setPromotions(getPromotion());
    global.setOffers(getOffer());
    return global;
  }

  private List<Promotions> getPromotion() {
    var promotionResponse = Promotions.builder()
            .maxRooms(2)
            .maxRoomsAmend(2)
            .numberOfNights(2)
            .promoCode("ST20RU")
            .page("summer-sale")
            .enabled(true)
            .build();
    return List.of(promotionResponse);
  }

  private List<Offer> getOffer() {
    var offerResponse = Offer.builder()
        .maxRooms(2)
        .cellCode("")
        .page("travel-industry-rate")
        .numberOfNights(9)
        .corpId("15010601")
        .ratePlanCode("FCDNLR30")
        .build();
    return List.of(offerResponse);
  }

  private Form getForm() {
    Form form = new Form();
    form.setChildrenHelperText("2-15 years");
    form.setAdultsHelperText("Max 2 per room");
    form.setCotLimit("0-2 years");
    form.setIncludeCot("Include a cot?");
    form.setRemoveRoom("Remove room");
    form.setCheckout("Check out:");
    form.setRoomType("Room type");
    form.setWhere("Enter place, postcode or hotel");
    return form;
  }

  private Results getResults() {
    return Results.builder()
        .notifications(getNotifications())
        .build();
  }

  private Notifications getNotifications() {
    return Notifications.builder()
        .groupBookingHeader("Unable to add more rooms")
        .groupBookingMessage(
            "If you’d like to book five rooms or more, please call us and we’ll be happy to help.")
        .ccuiGroupBookingMessage(
            "If caller wishes to add more than 9 rooms then please ask them to contact the Groups Team at group.enquiries@whitbread.com")
        .build();
  }

  private Menu getMenu() {
    return Menu.builder()
        .mobileMenuButton("Menu")
        .language("Language")
        .business("Business")
        .languageButton("English")
        .tick("/etc/clientlibs/pi-header/resources/images/tick.svg")
        .logIn("Log in")
        .discoverPi("Discover Premier Inn")
        .findBooking("Manage booking")
        .bookHotel("Search for a hotel")
        .guestAccount("Guest account")
        .changeLogs("Change logs")
        .agentMemo("Agent memo")
        .promoCode("Promo code")
        .build();
  }

  private Brand getBrand() {
    return Brand.builder()
        .zipLogoWhite("/content/dam/pi/websites/desktop/icons/brand/pi-icon-zip-white.svg")
        .piLogo("/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc.svg")
        .pidLogo("/images/pi-icon-disc.svg")
        .zipLogo("/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc-zip.svg")
        .pi("Premier Inn Rest Easy")
        .hub("Hub by Premier Inn")
        .pid("Premier Inn")
        .zip("ZIP by Premier Inn")
        .hubLogo("/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc-hub.svg")
        .hubBadge("/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-hub.svg")
        .zipBadge("/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-zip.svg")
        .build();
  }

  private Config getConfig() {
    Config config = new Config();
    RoomCodes roomCodes = new RoomCodes();
    roomCodes.setFamily("FAM");
    roomCodes.setAccessible("DIS");
    roomCodes.setSingle("SB");
    roomCodes.setTwin("TWIN");
    roomCodes.setDoubleValue("DB");
    config.setRoomCodes(roomCodes);
    config.setPromotionBanner(getPromotionBanner());
    config.setAmazonChat(getAmazonChat());
    return config;
  }

  private AmazonChat getAmazonChat() {
    return AmazonChat.builder()
            .amazonChatUrl("https://chat-uat2.premierinn.digital/connectwidget/static/amazon-connect-chat-interface-client.js")
            .chatIcon("/content/dam/pi/websites/target/live_chat_bold_gif.gif")
            .amazonAuthUrl("https://chat-uat.premierinn.com/uat/getJwt")
            .amazonChatId("a8bce959-7dc6-4628-9413-9ebdeb8a6f91")
            .amazonChatSnippetId("QVFJREFIajZXQjcyUXcrNFBEZVZpU0tIdzdFSjJTRnF3a2E2WXVITnhwS1FrcWpBd1FGRTNLU1NON1h0ZjNzUkJ3UTUvQUJxQUFBQWJqQnNCZ2txaGtpRzl3MEJCd2FnWHpCZEFnRUFNRmdHQ1NxR1NJYjNEUUVIQVRBZUJnbGdoa2dCWlFNRUFTNHdFUVFNVjlnT1lySGhnZFY4dVlmbkFnRVFnQ3ZlalVhUytFVGdReFJ4ODF0cWdaTityNFdPNGo5WER1dVZkb1RMVXAxeVYrWi9Ya0ZLb3BhOVJNMnU6OlZISGxLT3Avelc5Ui95bXVWeVlQTmxoclg4cUV0OUhCYWIwSWpab1hkYkphQ0x2dlB2TG1GUTMxTU5lRG9DRHBETXNtNUVlb1JBUHFwQmpUeEFKT0ZGTnZJcU4yNTByLzZZeFIwSEV6YjlRZnFmQXJOVmJERWNGS1I0cjFIYmtHNkVIRkFXVHRGc0VmVU9LYS9OeW12TkQxcFlNc0Zwcz0=")
            .chatBotStatus("SHOW_LIMITED_PAGES")
            .webChatEnabledPages(List.of("contact-us", "/account/dashboard", "/profile"))
            .build();
  }

  private PromotionBanner getPromotionBanner() {
    PromotionBanner promotionBanner = new PromotionBanner();
    promotionBanner.setEnabled(true);
    promotionBanner.setIcon("/content/dam/global/icons/common/price-tag.svg");
    promotionBanner.setTitle("Summer Sale: 20% off");
    promotionBanner.setDescription("Save 20% off when you stay 3 nights or more until 30 September 2025");
    promotionBanner.setTerms("Terms and Conditions");
    promotionBanner.setSrpNotificationTitle("Select a hotel to see if discount applies your stay.");
    promotionBanner.setSrpNotificationText("Prices shown here not include your discount yet.");
    return promotionBanner;
  }

  private IndexHeaderDataDto getIndexHeaderDataDto() {
    IndexHeaderDataDto indexHeaderDataDto = new IndexHeaderDataDto();
    indexHeaderDataDto.setContent(getContentDto());
    indexHeaderDataDto.setForm(getFormDto());
    indexHeaderDataDto.setDatePicker(new DatePickerDto("Reset", "Invalid Date", "Check out"));
    indexHeaderDataDto.setResults(getResultsDto());
    indexHeaderDataDto.setContactBanner(getContactBannerDto());
    indexHeaderDataDto.setConfig(getConfigDto());
    return indexHeaderDataDto;
  }

  private ContentDto getContentDto() {
    ContentDto contentDto = new ContentDto();
    contentDto.setGlobal(getGlobalDto());
    contentDto.setMenu(getMenuDto());
    contentDto.setCountries(getCountriesDto());
    contentDto.setHeader(getHeaderDto());
    contentDto.setSubNav(getSubNavDto());
    contentDto.setAuthentication(getAuthenticationDto());
    contentDto.setHero(getHeroDto());
    return contentDto;
  }

  private List<SubNavDto> getSubNavDto() {
    var subNav = SubNavDto.builder()
        .title("Short breaks")
        .navOptions(getNavOptionsDto())
        .build();
    return List.of(subNav);
  }

  private List<NavOptionDto> getNavOptionsDto() {
    var navOption = NavOptionDto.builder()
        .title("City breaks")
        .url("/gb/en/short-breaks/city-breaks.html?INTCMP=topNav")
        .build();

    return List.of(navOption);
  }

  private AuthenticationDto getAuthenticationDto() {
    return AuthenticationDto.builder()
        .login(getLoginDto())
        .forgottenPassword(getForgottenPasswordDto())
        .signUpButton("Sign up")
        .logoutButton("Log out")
        .build();
  }

  private ForgottenPasswordDto getForgottenPasswordDto() {
    var leisure = LeisureDto.builder()
        .formLabel("We will send you an email with instructions to reset your password")
        .emailPlaceholder("Email address")
        .formTitle("Reset your password")
        .submitButton("Submit")
        .build();

    var business = BusinessDto.builder()
        .formLabel("We will send you an email with instructions to reset your password")
        .emailPlaceholder("Email address")
        .formTitle("Reset your password")
        .submitButton("Submit")
        .businessLogin("Business login")
        .travelForBusiness("Travel for business")
        .businessDomain("Business domain")
        .build();

    return ForgottenPasswordDto.builder()
        .leisure(leisure)
        .business(business)
        .cancel("Cancel")
        .build();
  }

  private LoginDto getLoginDto() {
    var leisure = LeisureDto.builder()
        .formLabel("Log into your Premier Inn account")
        .emailPlaceholder("Email address")
        .loginButton("Log in")
        .build();

    var business = BusinessDto.builder()
        .formLabel("Log into your Premier Inn account")
        .emailPlaceholder("Email address")
        .loginButton("Log in")
        .businessLogin("Business login")
        .travelForBusiness("Travel for business")
        .businessDomain("Business domain")
        .build();

    return LoginDto.builder()
        .leisure(leisure)
        .business(business)
        .passwordPlaceholder("Password")
        .forgotPassword("Forgotten password?")
        .signupMessage("Don't have an account yet?")
        .signupLink("Sign up here")
        .build();
  }

  private HeaderDto getHeaderDto() {
    return HeaderDto.builder()
        .image("/content/dam/pi/websites/desktop/icons/brand/pi-logo-rest-easy.svg")
        .build();
  }

  private List<CountryDto> getCountriesDto() {
    var countryEnDto = CountryDto.builder()
        .language("English")
        .flagUrl("/etc/clientlibs/pi-header/resources/images/british-round.svg")
        .build();

    var countryDeDto = CountryDto.builder()
        .language("German")
        .flagUrl("/etc/clientlibs/pi-header/resources/images/germany-round.svg")
        .build();

    return List.of(countryEnDto, countryDeDto);
  }

  private GlobalDto getGlobalDto() {
    GlobalDto globalDto = new GlobalDto();
    globalDto.setDone("Done");
    globalDto.setRoom("room");
    globalDto.setRoomLabel("Room");
    globalDto.setSingle("Single");
    globalDto.setTwin("Twin");
    globalDto.setAccessible("Accessible");
    globalDto.setFamily("Family");
    globalDto.setAdult("adult");
    globalDto.setAdults("adults");
    globalDto.setChild("child");
    globalDto.setChildren("children");
    globalDto.setNight("night");
    globalDto.setRooms("rooms");
    globalDto.setAdultsLabel("Adults");
    globalDto.setChildrenLabel("Children");
    globalDto.setToday("Today");
    globalDto.setTomorrow("Tomorrow");
    globalDto.setDoubleLabel("Double");
    globalDto.setBrand(getBrandDto());
    globalDto.setPromotions(getPromotionDto());
    globalDto.setOffers(getOfferDto());
    return globalDto;
  }

  private List<PromotionDto> getPromotionDto() {
    var promotionResponseDto = PromotionDto.builder()
            .maxRooms(2)
            .maxRoomsAmend(2)
            .numberOfNights(2)
            .promoCode("ST20RU")
            .page("summer-sale")
            .enabled(true)
            .build();
    return List.of(promotionResponseDto);
  }

  private List<OfferDto> getOfferDto() {
    var offerResponseDto = OfferDto.builder()
        .maxRooms(2)
        .cellCode("")
        .page("travel-industry-rate")
        .numberOfNights(9)
        .corpId("15010601")
        .ratePlanCode("FCDNLR30")
        .build();
    return List.of(offerResponseDto);
  }

  private BrandDto getBrandDto() {
    return BrandDto.builder()
        .zipLogoWhite("/content/dam/pi/websites/desktop/icons/brand/pi-icon-zip-white.svg")
        .piLogo("/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc.svg")
        .pidLogo("/images/pi-icon-disc.svg")
        .zipLogo("/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc-zip.svg")
        .pi("Premier Inn Rest Easy")
        .hub("Hub by Premier Inn")
        .pid("Premier Inn")
        .zip("ZIP by Premier Inn")
        .hubLogo("/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc-hub.svg")
        .hubBadge("/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-hub.svg")
        .zipBadge("/content/dam/pi/websites/desktop/icons/brand/pi-icon-badge-zip.svg")
        .build();
  }

  private FormDto getFormDto() {
    FormDto formDto = new FormDto();
    formDto.setChildrenHelperText("2-15 years");
    formDto.setAdultsHelperText("Max 2 per room");
    formDto.setCotLimit("0-2 years");
    formDto.setIncludeCot("Include a cot?");
    formDto.setRemoveRoom("Remove room");
    formDto.setCheckout("Check out:");
    formDto.setRoomType("Room type");
    formDto.setWhere("Enter place, postcode or hotel");
    return formDto;
  }

  private ResultsDto getResultsDto() {
    ResultsDto resultsDto = new ResultsDto();
    NotificationsDto notificationsDto = new NotificationsDto();
    notificationsDto.setGroupBookingHeader("Unable to add more rooms");
    notificationsDto.setGroupBookingMessage(
        "If you’d like to book five rooms or more, please call us and we’ll be happy to help.");
    resultsDto.setNotifications(notificationsDto);
    return resultsDto;
  }

  private HeroDto getHeroDto() {
    return HeroDto.builder()
            .backgroundColor("ffffff")
            .bottomCaptionShow(false)
            .bottomCaptionShadow(false)
            .subHeadingText("There’s no better time to start planning your next adventure")
            .subHeadingFontColor("FFFFFF")
            .backgroundImage("/content/dam/pi/websites/desktop/homepage/hero-content/plain-purple-hero-2037.png")
            .headingFontColor("FFFFFF")
            .subHeadingFontShadow(true)
            .headingTextShort("Cold days, comfy rooms")
            .subHeadingFontWeight("500")
            .bottomCaptionArrowShow(false)
            .bottomCaptionText(" ")
            .subHeadingShow(true)
            .headingFontShadow(true)
            .footnotePosRight(false)
            .headingTextLong("There’s no better time to start planning your next adventure")
            .headingFontWeight("700")
            .build();
  }

  private MenuDto getMenuDto() {
    return MenuDto.builder()
        .mobileMenuButton("Menu")
        .language("Language")
        .business("Business")
        .languageButton("English")
        .tick("/etc/clientlibs/pi-header/resources/images/tick.svg")
        .logIn("Log in")
        .discoverPi("Discover Premier Inn")
        .findBooking("Manage booking")
        .bookHotel("Search for a hotel")
        .guestAccount("Guest account")
        .changeLogs("Change logs")
        .agentMemo("Agent memo")
        .promoCode("Promo code")
        .build();
  }

  private ConfigDto getConfigDto() {
    ConfigDto configDto = new ConfigDto();
    RoomCodesDto roomCodesDto = new RoomCodesDto();
    roomCodesDto.setFamily("FAM");
    roomCodesDto.setAccessible("DIS");
    roomCodesDto.setSingle("SB");
    roomCodesDto.setTwin("TWIN");
    roomCodesDto.setDoubleValue("DB");
    configDto.setRoomCodes(roomCodesDto);
    configDto.setPromotionBanner(getPromotionBannerDto());
    configDto.setAmazonChat(getAmazonChatDto());
    return configDto;
  }

  private AmazonChatDto getAmazonChatDto() {
    return AmazonChatDto.builder()
            .amazonChatUrl("https://chat-uat2.premierinn.digital/connectwidget/static/amazon-connect-chat-interface-client.js")
            .chatIcon("/content/dam/pi/websites/target/live_chat_bold_gif.gif")
            .amazonAuthUrl("https://chat-uat.premierinn.com/uat/getJwt")
            .amazonChatId("a8bce959-7dc6-4628-9413-9ebdeb8a6f91")
            .amazonChatSnippetId("QVFJREFIajZXQjcyUXcrNFBEZVZpU0tIdzdFSjJTRnF3a2E2WXVITnhwS1FrcWpBd1FGRTNLU1NON1h0ZjNzUkJ3UTUvQUJxQUFBQWJqQnNCZ2txaGtpRzl3MEJCd2FnWHpCZEFnRUFNRmdHQ1NxR1NJYjNEUUVIQVRBZUJnbGdoa2dCWlFNRUFTNHdFUVFNVjlnT1lySGhnZFY4dVlmbkFnRVFnQ3ZlalVhUytFVGdReFJ4ODF0cWdaTityNFdPNGo5WER1dVZkb1RMVXAxeVYrWi9Ya0ZLb3BhOVJNMnU6OlZISGxLT3Avelc5Ui95bXVWeVlQTmxoclg4cUV0OUhCYWIwSWpab1hkYkphQ0x2dlB2TG1GUTMxTU5lRG9DRHBETXNtNUVlb1JBUHFwQmpUeEFKT0ZGTnZJcU4yNTByLzZZeFIwSEV6YjlRZnFmQXJOVmJERWNGS1I0cjFIYmtHNkVIRkFXVHRGc0VmVU9LYS9OeW12TkQxcFlNc0Zwcz0=")
            .chatBotStatus("SHOW_LIMITED_PAGES")
            .webChatEnabledPages(List.of("contact-us", "/account/dashboard", "/profile"))
            .build();
  }

  private PromotionBannerDto getPromotionBannerDto() {
    PromotionBannerDto promotionBannerDto = new PromotionBannerDto();
    promotionBannerDto.setEnabled(true);
    promotionBannerDto.setIcon("/content/dam/global/icons/common/price-tag.svg");
    promotionBannerDto.setTitle("Summer Sale: 20% off");
    promotionBannerDto.setDescription("Save 20% off when you stay 3 nights or more until 30 September 2025");
    promotionBannerDto.setTerms("Terms and Conditions");
    promotionBannerDto.setSrpNotificationTitle("Select a hotel to see if discount applies your stay.");
    promotionBannerDto.setSrpNotificationText("Prices shown here not include your discount yet.");
    return promotionBannerDto;
  }

  private AccountLink getAccountLink(){
    return AccountLink.builder()
        .url("BookingsUrl")
        .title("Bookings")
        .icon("BookingsIcon")
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
