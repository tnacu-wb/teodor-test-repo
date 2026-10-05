package uk.co.whitbread.content.infrastructure.rest.client.aem;

import static java.util.List.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_ALL_HOTELS_DETAILS_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_GLOBAL_CONFIG_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_HOTELS_INFORMATION_EXCEPTION;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_PRICE_FINDER_CONFIG_EXCEPTION;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemBadRequestException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.in.DlpInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out.DlpInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out.HotelDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.in.GlobalConfigRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.AllowedRoomTypesByOccupancy;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.BookingWidgetConfigDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.GlobalConfigDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.RoomClassConfigDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.RoomClassOrderDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.AemHotelInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.BookRestaurantCta;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.BookingFlow;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.BookingFlowItem;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.HotelRoomConfiguration;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.Location;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.Menu;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.Restaurant;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.TabItem;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.TargetBookingFlowItem;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.out.HotelsInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.AccountLink;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.AemIndexHeaderDataDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.Authentication;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.Business;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.BusinessAccountLink;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.Config;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.Content;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.Country;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.ForgottenPassword;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.Form;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.Global;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.Header;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.Leisure;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.Login;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.Notifications;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.Offer;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.Promotions;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.Results;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.SubMenuLink;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.out.IndexHeaderDataRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.in.PriceFinderGlobalConfigRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.out.DestinationsDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.out.HotelCodesDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.out.InfoMessagesDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.out.LocationsDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.out.PriceFinderConfigDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.out.PriceFinderGlobalConfigDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.out.PriceFinderViewsDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.out.SeoHreflangsDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemDlpProperties;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemHomepageProperties;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemProperties;
import uk.co.whitbread.content.infrastructure.rest.client.content.aem.adapter.AemClient;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.CategoryEnumDto;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.LabelsRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.MultipleLabelsRequestDto;
import uk.co.whitbread.content.infrastructure.rest.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class AemClientTests {

  private AemClient aemClient;
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
  @Mock
  private CustomTestResponseSpec customResponseSpec;

  @Mock
  private AemProperties aemProperties;

  @Mock
  private AemDlpProperties aemDlpProperties;
  @Mock
  private static AemHomepageProperties aemHomepeAppsproperties;

  @BeforeEach
  void init() {
    this.aemClient = new AemClient(webClient, aemProperties, aemDlpProperties, aemHomepeAppsproperties);
  }

  @Test
  void getContent_badInput_ShouldReturnException() {
    //Arrange
    LabelsRequestDto contentAEMRequest = createContentAEMRequest();
    initWebClient(aemProperties.getLabelsEndpoint(),
        "etc/designs/global/dictionaries/labels/i18n.jsondict.{language}");

    when(responseSpec.onStatus(any(),  any())).thenThrow(
        new AemResponseException(
            AEM_ALL_HOTELS_DETAILS_EXCEPTION,
            "Unable to get labels",
            new Exception()));
    //Act
    var actual =
        assertThrows(AemResponseException.class, () -> aemClient.getLabels(contentAEMRequest));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(),
        is(AEM_ALL_HOTELS_DETAILS_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is("Unable to get labels"));
    assertThat(actual.getErrorCode(), is(AEM_ALL_HOTELS_DETAILS_EXCEPTION.getCode()));
  }

  @Test
  void getContent__ShouldReturnOK() {
    //Arrange
    LabelsRequestDto contentAEMRequest = createContentAEMRequest();
    initWebClient(aemProperties.getLabelsEndpoint(),
        "etc/designs/global/dictionaries/labels/i18n.jsondict.{language}");
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
    })).thenReturn(
        mockContentAEMResponse());

    //Act
    final var contentAEMResponse =
        aemClient.getLabels(contentAEMRequest);

    //Assert
    assertThat(contentAEMResponse, IsMapContaining.hasEntry("account.dashboard.and", "and"));
    assertThat(contentAEMResponse, IsMapContaining.hasEntry("account.dashboard.room", "Room"));
    assertThat(contentAEMResponse,
        IsMapContaining.hasEntry("config.errorMessages.cardDetails.yourReference.valid",
            "Customer Reference may not exceed 24 characters and may only contain letters, numbers, spaces, hyphens or dots. (ER9)"));
    assertThat(contentAEMResponse,
        IsMapContaining.hasEntry("facilities.COP", "Chargeable offsite parking"));
    assertThat(contentAEMResponse,
        IsMapContaining.hasEntry("hoteldetails.mapgeneraldirections.title", "Directions:"));
    assertThat(contentAEMResponse,
        IsMapContaining.hasEntry("hoteldetails.rates.chooseroomtype", "Choose room type"));
    assertThat(contentAEMResponse,
        IsMapContaining.hasEntry("feedback.contactus.surname", "Last name"));
    assertThat(contentAEMResponse,
        IsMapContaining.hasEntry("config.errorMessages.cardDetails.issueNumber.required",
            "Issue number required"));
  }

  private void initWebClient(String aemProperties, String t) {
    when(aemProperties).thenReturn(
        t);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
  }

  private Mono<Map<String, String>> mockContentAEMResponse() {
    Map<String, String> response = new HashMap<>();
    response.put("account.dashboard.and", "and");
    response.put("account.dashboard.room", "Room");
    response.put("config.errorMessages.cardDetails.yourReference.valid",
        "Customer Reference may not exceed 24 characters and may only contain letters, numbers, spaces, hyphens or dots. (ER9)");
    response.put("facilities.COP", "Chargeable offsite parking");
    response.put("hoteldetails.mapgeneraldirections.title", "Directions:");
    response.put("hoteldetails.rates.chooseroomtype", "Choose room type");
    response.put("feedback.contactus.surname", "Last name");
    response.put("config.errorMessages.cardDetails.issueNumber.required", "Issue number required");
    return Mono.just(response);
  }

  private LabelsRequestDto createContentAEMRequest() {
    return LabelsRequestDto.builder()
        .language("en")
        .country("gb")
        .category(CategoryEnumDto.MAIN)
        .build();
  }

  @Test
  void getPiPreCheckInContent__ShouldReturnOK() {
    //Arrange
    LabelsRequestDto contentAEMRequest = createPiPreCheckInContentAEMRequest();
    initWebClient(aemProperties.getPiPreCheckInEndpoint(),
        "etc/designs/global/dictionaries/guest-checkin/i18n.jsondict.{language}");
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
    })).thenReturn(Mono.just(piPreCheckInLabels()));

    //Act
    final var contentAEMResponse = aemClient.getLabels(contentAEMRequest);

    //Assert
    assertPiPreCheckInLabels(contentAEMResponse, piPreCheckInLabels());
}

  private void assertPiPreCheckInLabels(Map<String, String> categoryMap, Map<String, String> labels) {
    assertThat(categoryMap, notNullValue());
    for (Map.Entry<String, String> entry : labels.entrySet()) {
      assertThat(categoryMap, IsMapContaining.hasEntry(entry.getKey(), entry.getValue()));
    }
  }

  private LabelsRequestDto createPiPreCheckInContentAEMRequest() {
    return LabelsRequestDto.builder()
        .language("de")
        .country("de")
        .category(CategoryEnumDto.PI_PRE_CHECKIN)
        .build();
  }

  @Test
  void getAemHotelInformationDto__ShouldReturnOK() {
    //Arrange
    var request = createHotelInfoRequest();

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(AemHotelInformationDto.class)).thenReturn(
        mockAemHotelInformationDtoResponse());

    //Act
    final var aemHotelInformationDtoResponse =
        aemClient.getSingleHotelInformation(request.getCountry(), request.getLanguage(),
            request.getHotelIds().get(0));

    //Assert
    assertThat(aemHotelInformationDtoResponse, notNullValue());
    assertEquals("TestName", aemHotelInformationDtoResponse.getName());
    assertEquals("TLONKI", aemHotelInformationDtoResponse.getCode());
    assertEquals("PI", aemHotelInformationDtoResponse.getBrand());
    assertEquals("logo-url/test.jpg", aemHotelInformationDtoResponse.getRestaurant().getImage());
    assertEquals("food-at-the-social.jpg", aemHotelInformationDtoResponse.getRestaurant()
            .getBookingCardImage());
    assertEquals("Table", aemHotelInformationDtoResponse.getBookRestaurantCta()
            .getBookingCardCtaText());
    assertEquals("logo-url/book", aemHotelInformationDtoResponse.getBookRestaurantCta()
            .getBookingCardCtaLink());
    assertEquals("THYME", aemHotelInformationDtoResponse.getRestaurant().getName());
    assertEquals(
        "Breakfast", aemHotelInformationDtoResponse.getRestaurant().getMenus().get(0).getName());
    assertEquals(
        "/test/Global Breakfast.pdf",
        aemHotelInformationDtoResponse.getRestaurant().getMenus().get(0).getPath());
    assertEquals(
        "Dinner", aemHotelInformationDtoResponse.getRestaurant().getMenus().get(1).getName());
    assertEquals(
        "/test/Global Dinner.pdf",
        aemHotelInformationDtoResponse.getRestaurant().getMenus().get(1).getPath());
    assertEquals("test-county", aemHotelInformationDtoResponse.getLocation().getCounty());
    assertThat(aemHotelInformationDtoResponse.getHotelFlags(), notNullValue());
    assertEquals(true, aemHotelInformationDtoResponse.getHotelFlags().getIsEnabled());
    assertEquals("New Premier Plus rooms",
        aemHotelInformationDtoResponse.getHotelFlags().getFlagOverlay().getText());
    assertEquals("#D7C3FF",
        aemHotelInformationDtoResponse.getHotelFlags().getFlagOverlay().getTextColour());
    assertEquals("New restaurant now open",
        aemHotelInformationDtoResponse.getHotelFlags().getFlagBanner().getText());
    assertEquals("#511E62",
        aemHotelInformationDtoResponse.getHotelFlags().getFlagBanner().getBackgroundColour());
  }

  @Test
  void getSingleHotelInformation_4xxError_ShouldReturnBadRequestException() {
    //Arrange
    var request = createHotelInfoRequest();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    String country = request.getCountry();
    String language = request.getLanguage();
    String hotelId = request.getHotelIds().get(0);
    var actual = assertThrows(AemBadRequestException.class,
        () -> aemClient.getSingleHotelInformation(country, language, hotelId));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(AEM_HOTELS_INFORMATION_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is("Unable to get hotel information"));
    assertThat(actual.getErrorCode(), is(AEM_HOTELS_INFORMATION_EXCEPTION.getCode()));
  }

  @Test
  void getSingleHotelInformation_5xxError_ShouldReturnResponseException() {
    //Arrange
    var request = createHotelInfoRequest();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    String country = request.getCountry();
    String language = request.getLanguage();
    String hotelId = request.getHotelIds().get(0);
    var actual = assertThrows(AemResponseException.class,
        () -> aemClient.getSingleHotelInformation(country, language, hotelId));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(AEM_HOTELS_INFORMATION_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is("Unable to get hotel information"));
    assertThat(actual.getErrorCode(), is(AEM_HOTELS_INFORMATION_EXCEPTION.getCode()));
  }

  private Mono<AemHotelInformationDto> mockAemHotelInformationDtoResponse() {
    AemHotelInformationDto hotelInformationDto = new AemHotelInformationDto();
    hotelInformationDto.setName("TestName");
    hotelInformationDto.setCode("TLONKI");
    hotelInformationDto.setBrand("PI");
    hotelInformationDto.setCountryCodeISO("GB");
    hotelInformationDto.setRestaurant(Restaurant.builder()
        .image("logo-url/test.jpg")
        .name("THYME")
        .bookingCardDescription("Restaurant")
        .bookingCardBackgroundImage("/content/dam/BackgroundImageRestaurants.png")
        .bookingCardTitle("Food at the Social")
        .bookingCardImage("food-at-the-social.jpg")
        .menus(createMenus())
        .build());
    hotelInformationDto.setBookingFlow(BookingFlow.builder()
        .bookingFlowItems(createBookingFlowItems())
        .targetBookingFlowItems(createTarghetBookingFlowItems())
        .build());
    hotelInformationDto.setBookRestaurantCta(BookRestaurantCta.builder()
         .bookingCardCtaText("Table")
         .bookingCardCtaLink("logo-url/book")
         .bookingCardTrackingId("sidebar-card-social")
         .build());
    hotelInformationDto.setHotelRoomConfiguration(HotelRoomConfiguration
        .builder().tabItems(createTabItems()).build());
    hotelInformationDto.setHotelFlags(
        uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.HotelFlags.builder()
            .isEnabled(true)
            .flagOverlay(uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.FlagDetail.builder()
                .text("New Premier Plus rooms")
                .textColour("#D7C3FF")
                .backgroundColour("#0007")
                .backgroundImage("/content/dam/icons/resources/icon-overlay.png")
                .build())
            .flagBanner(uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.FlagDetail.builder()
                .text("New restaurant now open")
                .textColour("#FFFFFF")
                .backgroundColour("#511E62")
                .backgroundImage("/content/dam/icons/resources/icon-banner.png")
                .build())
            .build());
    Location location = new Location();
    location.setCounty("test-county");
    hotelInformationDto.setLocation(location);
    return Mono.just(hotelInformationDto);
  }

  private HotelsInformationDto createHotelInfoRequest() {
    return HotelsInformationDto.builder()
        .hotelIds(List.of("DLONKI"))
        .language("en")
        .country("gb")
        .build();
  }

  private List<Menu> createMenus() {
    return of(
        Menu.builder()
            .name("Breakfast")
            .path("/test/Global Breakfast.pdf")
            .build(),
        Menu.builder()
            .name("Dinner")
            .path("/test/Global Dinner.pdf")
            .build());
  }

  private List<BookingFlowItem> createBookingFlowItems() {
    return of(
        BookingFlowItem.builder()
            .rateCode("A")
            .bookingFlowPath("https://secure2.premierinn.com/de/de/booking-a1/extras.html")
            .bookingId("booking-a1")
            .build(),
        BookingFlowItem.builder()
            .rateCode("G")
            .bookingFlowPath("https://secure2.premierinn.com/de/de/booking-a1/extras.html")
            .bookingId("booking-a1")
            .build(),
        BookingFlowItem.builder()
            .rateCode("Q")
            .bookingFlowPath("https://secure2.premierinn.com/de/de/booking-a1/extras.html")
            .bookingId("booking-a1")
            .build());
  }

  private List<TabItem> createTabItems() {
    return of(
        TabItem.builder()
            .roomType("accessible")
            .roomTypeCode("LOWDBL,WETDBL,WETTWN,LOW…OW,BRFDBL,BRFZPL,BRFTWN")
            .fileReference("/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID4-Accessible-Double.jpg")
            .build(),
        TabItem.builder()
            .roomType("double")
            .roomTypeCode("DOUBLE,ZPLDBL")
            .fileReference("/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg")
            .build(),
        TabItem.builder()
            .roomType("twin")
            .roomTypeCode("TWINRM,DBLDBL")
            .fileReference("/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4-Test/Artboard 3.jpg")
            .build());
  }

  private List<TargetBookingFlowItem> createTarghetBookingFlowItems() {
    return of(
        TargetBookingFlowItem.builder()
            .id("id1")
            .rateCode("A")
            .bookingFlowPath("https://secure2.premierinn.com/de/de/booking-a1/extras.html")
            .build(),
        TargetBookingFlowItem.builder()
            .id("id2")
            .rateCode("G")
            .bookingFlowPath("https://secure2.premierinn.com/de/de/booking-a1/extras.html")
            .build(),
        TargetBookingFlowItem.builder()
            .id("id3")
            .rateCode("Q")
            .bookingFlowPath("https://secure2.premierinn.com/de/de/booking-a1/extras.html")
            .build());
  }

  @Test
  void getMultipleLabels__ShouldReturnOK() {
    //Arrange
    MultipleLabelsRequestDto multipleLabelsRequest = createMultipleLabelsRequest();
    when(aemProperties.getLabelsEndpoint()).thenReturn(
        "etc/designs/global/dictionaries/labels/i18n.jsondict.{language}");
    when(aemProperties.getAmendLabelsEndpoint()).thenReturn(
        "etc/designs/global/dictionaries/pi-bookings/i18n.jsondict.{language}");
    when(aemProperties.getBookingLabelsEndpoint()).thenReturn(
        "etc/designs/global/dictionaries/booking/i18n.jsondict.{language}");
    when(aemProperties.getPiGroupBookingEndpoint()).thenReturn(
            "etc/designs/global/dictionaries/pi-group-booking/i18n.jsondict.{language}");
    initWebClient(aemProperties.getPiPreCheckInEndpoint(),
        "etc/designs/global/dictionaries/guest-checkin/i18n.jsondict.{language}");
    when(aemProperties.getExtrasLabelsEndpoint()).thenReturn(
            "etc/designs/global/dictionaries/extras/i18n.jsondict.{language}");
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {}))
            .thenReturn(mockContentAEMResponse())
            .thenReturn(mockPiBookingLabelsAEMResponse())
            .thenReturn(mockBookingsLabelsAEMResponse())
            .thenReturn(Mono.just(piPreCheckInLabels()))
            .thenReturn(Mono.just(extrasLabels()));

    //Act
    final var contentAEMResponse = aemClient.getMultipleLabels(multipleLabelsRequest);

    //Assert
    assertThat(contentAEMResponse, notNullValue());
    assertThat(contentAEMResponse, aMapWithSize(6));
    assertThat(contentAEMResponse, IsMapContaining.hasKey("booking"));
    assertThat(contentAEMResponse, IsMapContaining.hasKey("main"));
    assertThat(contentAEMResponse, IsMapContaining.hasKey("piBookings"));
    assertThat(contentAEMResponse, IsMapContaining.hasKey("piGroupBooking"));
    assertThat(contentAEMResponse, IsMapContaining.hasKey("extras"));
    assertThat(contentAEMResponse.get("main"),
        IsMapContaining.hasEntry("facilities.COP", "Chargeable offsite parking"));
    assertThat(contentAEMResponse.get("main"),
        IsMapContaining.hasEntry("hoteldetails.mapgeneraldirections.title", "Directions:"));
    assertThat(contentAEMResponse.get("booking"),
        IsMapContaining.hasEntry("paymentOptions.PAY_ON_ARRIVAL", "Pay on arrival"));
    assertThat(contentAEMResponse.get("booking"),
        IsMapContaining.hasEntry("cardNotPresent.memorableWordRequired", "Required"));
    assertThat(contentAEMResponse.get("booking"),
        IsMapContaining.hasEntry("cc.na.PIBA_ALLOWED_ONLY_IN_UK",
            "Your stored card ending in {cardEnding} is not accepted at this hotel"));
    assertThat(contentAEMResponse.get("piBookings"),
        IsMapContaining.hasEntry("dashboard.bookings.flexRateText",
            "Amend or cancel up to {hour} on arrival day"));
    assertThat(contentAEMResponse.get("piBookings"),
        IsMapContaining.hasEntry("dashboard.bookings.contactUsInfo",
            "Call 0333 003 8101  *Calls are charged at the national rate"));
    assertThat(contentAEMResponse.get("piBookings"),
        IsMapContaining.hasEntry("dashboard.bookings.alreadyHaveAccount",
            "Already have an account?"));

    assertLabels(contentAEMResponse, "piPreCheckIn", piPreCheckInLabels());
    assertLabels(contentAEMResponse, "extras", extrasLabels());
  }

  private void assertLabels(Map<String, Map<String, String>> contentAEMResponse, String category,
      Map<String, String> labels) {
    assertThat(contentAEMResponse, IsMapContaining.hasKey(category));
    Map<String, String> categoryMap = contentAEMResponse.get(category);
    assertThat(categoryMap, notNullValue());
    for (Map.Entry<String, String> entry : labels.entrySet()) {
      assertThat(categoryMap, IsMapContaining.hasEntry(entry.getKey(), entry.getValue()));
    }
  }

  private MultipleLabelsRequestDto createMultipleLabelsRequest() {
    return MultipleLabelsRequestDto.builder()
        .language("en")
        .country("gb")
        .categories(
            of(CategoryEnumDto.MAIN, CategoryEnumDto.PI_BOOKINGS, CategoryEnumDto.BOOKING,
                CategoryEnumDto.PI_PRE_CHECKIN, CategoryEnumDto.PI_GROUP_BOOKING, CategoryEnumDto.EXTRAS))
        .build();
  }

  private Mono<Map<String, String>> mockPiBookingLabelsAEMResponse() {
    Map<String, String> piBookingResponse = new HashMap<>();
    piBookingResponse.put("dashboard.bookings.contactUsInfo",
        "Call 0333 003 8101  *Calls are charged at the national rate");
    piBookingResponse.put("dashboard.bookings.newTotal", "New Total");
    piBookingResponse.put("dashboard.bookings.flexRateText",
        "Amend or cancel up to {hour} on arrival day");
    piBookingResponse.put("dashboard.bookings.priceFor", "Price for");
    piBookingResponse.put("dashboard.bookings.invalidEmail", "Please enter a valid email address");
    piBookingResponse.put("dashboard.bookings.alreadyHaveAccount", "Already have an account?");
    return Mono.just(piBookingResponse);
  }

  private Mono<Map<String, String>> mockBookingsLabelsAEMResponse() {
    Map<String, String> bookingResponse = new HashMap<>();
    bookingResponse.put("details.userForm.titles.mr", "Mr");
    bookingResponse.put("paymentQuestions.poNumber", "Purchase order number (Optional)");
    bookingResponse.put("paymentOptions.PAY_ON_ARRIVAL", "Pay on arrival");
    bookingResponse.put("details.userForm.lastNameRequired",
        "Please enter your last name (max 30 characters)");
    bookingResponse.put("cc.na.cardFallback", "Card ending {cardEnding} is not enabled");
    bookingResponse.put("cardNotPresent.memorableWordRequired", "Required");
    bookingResponse.put("cc.na.PIBA_ALLOWED_ONLY_IN_UK",
        "Your stored card ending in {cardEnding} is not accepted at this hotel");
    bookingResponse.put("cc.LEISURE_STORED_CARD", "Personal stored card");
    return Mono.just(bookingResponse);
  }

  private Map<String, String> extrasLabels() {
    Map<String, String> extrasResponse = new HashMap<>();
    extrasResponse.put("ancillaries.extras.not.available", "No extras selected");
    extrasResponse.put("ancillaries.extras.HSCKIN.name", "Early check-in)");
    return extrasResponse;
  }

  private Map<String, String> piPreCheckInLabels() {
    Map<String, String> labelsMap = new HashMap<>();
    labelsMap.put("precheckin.details.passport",
        "Passport Number(only in case of foreign nationals)");
    labelsMap.put("precheckin.errors.empty.dependent", "Please enter your dependent");
    labelsMap.put("precheckin.details.nationality", "Nationality(ies)*");
    labelsMap.put("precheckin.details.dateofbirth", "Date of Birth*");
    labelsMap.put("precheckin.details.scheduleddate", "Scheduled Date of Departure*");
    labelsMap.put("precheckin.details.surname", "Last name*");
    labelsMap.put("precheckin.details.postalcode", "Postal code*");
    labelsMap.put("precheckin.bookingnumber", "Booking Reference*");
    labelsMap.put("precheckin.details.firstname", "First name*");
    labelsMap.put("precheckin.details.address", "Street, House number*");
    labelsMap.put("precheckin.details.arrivaldate", "Arrival Date*");
    labelsMap.put("precheckin.details.city", "City*");
    labelsMap.put("precheckin.details.country", "Country*");

    return labelsMap;
  }

  @Test
  void getIndexHotelData__ShouldReturnOK() {
    //Arrange
    IndexHeaderDataRequestDto indexHeaderDataRequestDto = indexHeaderDataRequestDtoMock(
        Boolean.FALSE);

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(AemIndexHeaderDataDto.class)).thenReturn(
        mockAemIndexHotelDataDtoResponse());

    //Act
    final var contentAEMResponse =
        aemClient.getIndexHeaderData(indexHeaderDataRequestDto);

    //Assert
    assertThat(contentAEMResponse, notNullValue());
    assertThat(contentAEMResponse.getContent(), notNullValue());

    test_globalElements(contentAEMResponse);
    test_formElements(contentAEMResponse);
    test_authentication(contentAEMResponse);

    assertThat(contentAEMResponse.getContent().getResults(), notNullValue());
    test_notificationElements(contentAEMResponse);

    assertThat(contentAEMResponse.getContent().getCountries(), notNullValue());
    assertEquals("English", contentAEMResponse.getContent().getCountries().get(0).getLanguage());
    assertEquals("German", contentAEMResponse.getContent().getCountries().get(1).getLanguage());
    assertEquals("/etc/clientlibs/pi-header/resources/images/british-round.svg",
        contentAEMResponse.getContent().getCountries().get(0).getFlagUrl());
    assertEquals("/etc/clientlibs/pi-header/resources/images/germany-round.svg",
        contentAEMResponse.getContent().getCountries().get(1).getFlagUrl());
    assertEquals("/content/dam/pi/websites/desktop/icons/brand/pi-logo-rest-easy.svg",
        contentAEMResponse.getContent().getHeader().getImage());
    assertEquals("FCDNLR30",
        contentAEMResponse.getContent().getGlobal().getOffers().get(0).getRatePlanCode());
    assertEquals("15010601",
        contentAEMResponse.getContent().getGlobal().getOffers().get(0).getCorpId());
    assertEquals("summer-sale",
            contentAEMResponse.getContent().getGlobal().getPromotions().get(0).getPage());
  }

  @Test
  void getSearchRules__ShouldReturnOK() {
    //Arrange
    GlobalConfigRequestDto globalConfigRequestDto = globalConfigRequestDtoMock();

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(GlobalConfigDto.class)).thenReturn(mockGlobalConfigDto());

    //Act
    final GlobalConfigDto globalConfigResponse =
        aemClient.getGlobalConfig(globalConfigRequestDto);

    //Assert
    assertThat(globalConfigResponse, notNullValue());
    assertEquals(Integer.valueOf(4), globalConfigResponse.getBookingWidgetConfig().getNumberOfNights());
    assertEquals(4, globalConfigResponse.getBookingWidgetConfig().getMaxRooms());
    assertEquals(300, globalConfigResponse.getBookingWidgetConfig().getMaxArrivalDate());

    List<AllowedRoomTypesByOccupancy> acceptedRoomTypesList = globalConfigResponse.getBookingWidgetConfig().getAllowedRoomTypesByOccupancy();
    assertThat(acceptedRoomTypesList, notNullValue());

    assertTrue(acceptedRoomTypesList.get(0).getAcceptedRoomTypes().contains("DIS"));
    assertEquals(2, acceptedRoomTypesList.get(0).getAdultsNumber());
    assertEquals(1, acceptedRoomTypesList.get(0).getChildrenNumber());
  }

  private Mono<GlobalConfigDto> mockGlobalConfigDto() {
    AllowedRoomTypesByOccupancy acceptedRoomTypes = new AllowedRoomTypesByOccupancy();
    acceptedRoomTypes.setAcceptedRoomTypes(List.of("DIS"));
    acceptedRoomTypes.setAdultsNumber(2);
    acceptedRoomTypes.setChildrenNumber(1);

    BookingWidgetConfigDto bookingWidgetConfigDto = new BookingWidgetConfigDto();
    bookingWidgetConfigDto.setNumberOfNights(4);
    bookingWidgetConfigDto.setMaxRooms(4);
    bookingWidgetConfigDto.setMaxArrivalDate(300);
    bookingWidgetConfigDto.setAllowedRoomTypesByOccupancy(List.of(acceptedRoomTypes));

    GlobalConfigDto globalConfigDto = new GlobalConfigDto();
    globalConfigDto.setBookingWidgetConfig(bookingWidgetConfigDto);

    return Mono.just(globalConfigDto);

  }

  @Test
  void getRoomClassConfig__ShouldReturnOK() {
    //Arrange
    GlobalConfigRequestDto globalConfigRequestDto = globalConfigRequestDtoMock();

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RoomClassConfigDto.class)).thenReturn(
        mockRoomClassConfigDtoResponse());

    //Act
    final RoomClassConfigDto roomClassConfigDto = aemClient
        .getRoomClassConfig(globalConfigRequestDto);

    //Assert
    assertThat(roomClassConfigDto, notNullValue());
    final List<RoomClassOrderDto> roomClassConfig = roomClassConfigDto.getRoomClassConfig();
    assertThat(roomClassConfig, notNullValue());
    assertThat(roomClassConfig, hasSize(2));
    final RoomClassOrderDto roomClassOrderDto1 = roomClassConfig.get(0);
    assertThat(roomClassOrderDto1.getCode(), is("ON"));
    assertThat(roomClassOrderDto1.getOrder(), is(1));
    final RoomClassOrderDto roomClassOrderDto2 = roomClassConfig.get(1);
    assertThat(roomClassOrderDto2.getCode(), is("TW"));
    assertThat(roomClassOrderDto2.getOrder(), is(2));
  }

  @Test
  void getGlobalConfig__ShouldReturnOK() {
    //Arrange
    var globalConfigRequestDto = globalConfigRequestDtoMock();

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(GlobalConfigDto.class)).thenReturn(
        mockGlobalConfigDtoResponse());

    //Act
    final var contentAEMResponse = aemClient.getGlobalConfig(globalConfigRequestDto);

    //Assert
    assertThat(contentAEMResponse, notNullValue());
    assertEquals(4, contentAEMResponse.getBookingWidgetConfig().getMaxRooms());
    final var roomConfig = contentAEMResponse.getRoomClassConfig();
    assertThat(roomConfig, notNullValue());
    assertThat(roomConfig, hasSize(2));
    final RoomClassOrderDto roomClassOrderDto1 = roomConfig.get(0);
    assertThat(roomClassOrderDto1.getCode(), is("ON"));
    assertThat(roomClassOrderDto1.getOrder(), is(1));
    final RoomClassOrderDto roomClassOrderDto2 = roomConfig.get(1);
    assertThat(roomClassOrderDto2.getCode(), is("TW"));
    assertThat(roomClassOrderDto2.getOrder(), is(2));
  }

  @Test
  void getGlobalConfig_aemError_ShouldReturnException() {
    //Arrange
    var globalConfigRequestDto = globalConfigRequestDtoMock();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    var actual =
        assertThrows(AemResponseException.class, () -> aemClient.getGlobalConfig(globalConfigRequestDto));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(AEM_GLOBAL_CONFIG_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is("Unable to get global config data."));
    assertThat(actual.getErrorCode(), is(AEM_GLOBAL_CONFIG_EXCEPTION.getCode()));
  }

  @Test
  void getDlpInformation__ShouldReturnOK() {
    //Arrange
    var dlpInformationRequestDto = dlpInformationRequestDtoMock();

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(DlpInformationDto.class)).thenReturn(
        mockDlpInformationDtoResponse());

    //Act
    var dlpInformationDto = aemClient.getDlpInformation(dlpInformationRequestDto);

    //Assert
    assertThat(dlpInformationDto, notNullValue());
    var hotels = dlpInformationDto.getHotels();
    assertThat(hotels, notNullValue());
    assertThat(hotels, hasSize(2));
    var hotel1 = hotels.get(0);
    assertThat(hotel1.getCode(), is("ON"));
    assertThat(hotel1.getOrder(), is(1));
    var hotel2 = hotels.get(1);
    assertThat(hotel2.getCode(), is("TW"));
    assertThat(hotel2.getOrder(), is(2));
  }

  private Mono<RoomClassConfigDto> mockRoomClassConfigDtoResponse() {
    RoomClassOrderDto roomClassOrderDto1 = RoomClassOrderDto.builder().code("ON").order(1).build();
    RoomClassOrderDto roomClassOrderDto2 = RoomClassOrderDto.builder().code("TW").order(2).build();
    RoomClassConfigDto roomClassConfigDto = RoomClassConfigDto.builder()
        .roomClassConfig(List.of(roomClassOrderDto1, roomClassOrderDto2)).build();
    return Mono.just(roomClassConfigDto);
  }

  private Mono<DlpInformationDto> mockDlpInformationDtoResponse() {
    HotelDto hotelDto1 = HotelDto.builder().code("ON").order(1).build();
    HotelDto hotelDto2 = HotelDto.builder().code("TW").order(2).build();
    DlpInformationDto dlpInformationDto = DlpInformationDto.builder()
        .hotels(List.of(hotelDto1, hotelDto2)).build();
    return Mono.just(dlpInformationDto);
  }

  private void test_authentication(AemIndexHeaderDataDto contentAEMResponse) {
    assertThat(contentAEMResponse.getContent().getAuthentication(), notNullValue());
    assertEquals("Your account",
        contentAEMResponse.getContent().getAuthentication().getAccountDescription());
    assertEquals("Remember me", contentAEMResponse.getContent().getAuthentication().getLogin()
        .getRememberMeLabel());
    assertEquals("Your email address or password is incorrect",
        contentAEMResponse.getContent().getAuthentication().getLogin().getBadCredentialsError());
    assertEquals("Your session expired, please login again",
        contentAEMResponse.getContent().getAuthentication().getLogin().getSessionExpired());
    assertEquals("Please enter a valid e-mail address",
        contentAEMResponse.getContent().getAuthentication().getLogin().getInvalidEmail());
    assertEquals("Sign up to My Premier Inn",
        contentAEMResponse.getContent().getAuthentication().getLogin().getLeisure()
            .getSignupButton());
    assertEquals("My Premier Inn",
        contentAEMResponse.getContent().getAuthentication().getLogin().getLeisure().getTab());
    assertEquals("We don’t recognise this email address",
        contentAEMResponse.getContent().getAuthentication().getForgottenPassword()
            .getNotRegisteredError());
    assertEquals("Cancel",
        contentAEMResponse.getContent().getAuthentication().getForgottenPassword()
            .getCancel());
    assertEquals("Email sent",
        contentAEMResponse.getContent().getAuthentication().getForgottenPassword()
            .getEmailSentHeader());
    assertEquals("Looks like something went wrong, please try again later",
        contentAEMResponse.getContent().getAuthentication().getForgottenPassword()
            .getGenericError());
    assertEquals("We've sent you an email with instructions to reset your password",
        contentAEMResponse.getContent().getAuthentication().getForgottenPassword()
            .getEmailSentMessage());
  }

  private void test_globalElements(AemIndexHeaderDataDto contentAEMResponse) {
    assertThat(contentAEMResponse.getContent().getGlobal(), notNullValue());
    assertEquals(
        "Done", contentAEMResponse.getContent().getGlobal().getDone());
    assertEquals(
        "Single", contentAEMResponse.getContent().getGlobal().getSingle());
    assertEquals(
        "room", contentAEMResponse.getContent().getGlobal().getRoom());
    assertEquals(
        "Twin", contentAEMResponse.getContent().getGlobal().getTwin());
    assertEquals(
        "adult", contentAEMResponse.getContent().getGlobal().getAdult());
    assertEquals(
        "child", contentAEMResponse.getContent().getGlobal().getChild());
    assertEquals(
        "night", contentAEMResponse.getContent().getGlobal().getNight());
    assertEquals("rooms", contentAEMResponse.getContent().getGlobal().getRooms());
    assertEquals("Today", contentAEMResponse.getContent().getGlobal().getToday());
    assertEquals(4, contentAEMResponse.getContent().getGlobal().getMaxRooms());
    assertEquals("Adults", contentAEMResponse.getContent().getGlobal().getAdultsLabel());
    assertEquals("Children", contentAEMResponse.getContent().getGlobal().getChildrenLabel());
    assertEquals(
        "children", contentAEMResponse.getContent().getGlobal().getChildren());
    assertEquals(
        "Rooms", contentAEMResponse.getContent().getGlobal().getRoomsLabel());
  }

  private void test_formElements(AemIndexHeaderDataDto contentAEMResponse) {
    assertThat(contentAEMResponse.getContent().getForm(), notNullValue());
    assertEquals(
        "Enter place, postcode or hotel", contentAEMResponse.getContent().getForm().getWhere());
    assertEquals(
        "Booking reference *",
        contentAEMResponse.getContent().getForm().getBookingReferenceLabel());
    assertEquals(
        "Surname", contentAEMResponse.getContent().getForm().getBookingSurname());
    assertEquals(
        "Please enter a valid room composition",
        contentAEMResponse.getContent().getForm().getInvalidRooms());
    assertEquals(
        "Please enter a location or a hotel",
        contentAEMResponse.getContent().getForm().getInvalidLocation());
    assertEquals(
        "//www.premierinn.com/gb/en/hotels.html",
        contentAEMResponse.getContent().getForm().getSnowdropErrorUrl());
    assertEquals(
        "Include a cot?", contentAEMResponse.getContent().getForm().getIncludeCot());
    assertEquals("or", contentAEMResponse.getContent().getForm().getSnowdropErrorLinkConnector());
    assertEquals("Check in online",
        contentAEMResponse.getContent().getForm().getCheckinOnlineLandingPageTitle());
    assertEquals(9, contentAEMResponse.getContent().getForm().getNumberOfNights());
    assertEquals(
        "Spend more time enjoying your stay by checking in online.When you check in online you can collect your room key as soon as you arrive, plus you can add breakfast and Ultimate Wi-Fi to your booking in advance."
        , contentAEMResponse.getContent().getForm().getCheckinOnlineLandingPageDescription());
  }

  private void test_notificationElements(AemIndexHeaderDataDto contentAEMResponse) {
    assertThat(contentAEMResponse.getContent().getResults().getNotifications(), notNullValue());
    assertEquals(
        "If you’d like to make a booking for more than 4 rooms then please email Group.Enquiries@whitbread.com.",
        contentAEMResponse.getContent().getResults().getNotifications()
            .getBusinessGroupBookingMessage());
    assertEquals("To add another room, please make a seperate booking.",
        contentAEMResponse.getContent().getResults().getNotifications().getAddAnotherRoom());
    assertEquals(
        "If you’d like to book five rooms or more, please call us and we’ll be happy to help.",
        contentAEMResponse.getContent().getResults().getNotifications().getGroupBookingMessage());
    assertEquals(
        "Sorry, we couldn’t find any hotels with available rooms. Try changing your search area.",
        contentAEMResponse.getContent().getResults().getNotifications().getNoResults());
    assertEquals("Unable to add more rooms",
        contentAEMResponse.getContent().getResults().getNotifications().getGroupBookingHeader());
    assertEquals(
            "For a group booking of 5 to 9 rooms, call us on 0333 003 8101. To book 10 rooms or more, please complete the <a class=\\\"wb-a\\\" href=\\\"why/groups/form{groupBookingLink}\\\">group booking form</a> and we will be in contact to discuss your enquiry.",
            contentAEMResponse.getContent().getResults().getNotifications().getGroupBookingFormPageMessage());
  }

  @Test
  void getBbIndexHotelData__ShouldReturnOK() {
    //Arrange
    IndexHeaderDataRequestDto indexHeaderDataRequestDto = indexHeaderDataRequestDtoMock(
        Boolean.TRUE);

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(AemIndexHeaderDataDto.class)).thenReturn(
        mockAemIndexHotelDataDtoResponseForBb());

    //Act
    final var contentAEMResponse =
        aemClient.getIndexHeaderData(indexHeaderDataRequestDto);

    //Assert
    assertThat(contentAEMResponse, notNullValue());
    assertThat(contentAEMResponse.getConfig(), notNullValue());
    assertEquals("Account settings",
        contentAEMResponse.getConfig().getAuthentication().getAccountLinks().get(0).getTitle());
    assertEquals("/gb/en/business-booker/accountTestURL",
        contentAEMResponse.getConfig().getAuthentication().getAccountLinks().get(0).getUrl());
    assertEquals("Company management",
        contentAEMResponse.getConfig().getAuthentication().getBusiness().getBusinessAccountLinks().get(0).getTitle());
    assertEquals("Manage employees",
        contentAEMResponse.getConfig().getAuthentication().getBusiness().getBusinessAccountLinks().get(0)
            .getSubMenuLinks().get(0).getTitle());
    assertEquals("/gb/en/business-booker/testURL",
        contentAEMResponse.getConfig().getAuthentication().getBusiness().getBusinessAccountLinks().get(0)
            .getSubMenuLinks().get(0).getUrl());
    assertEquals("Business login",
        contentAEMResponse.getConfig().getAuthentication().getBusiness().getBusinessLogin());
    assertEquals("Travel for business",
        contentAEMResponse.getConfig().getAuthentication().getBusiness().getTravelForBusiness());
    assertEquals("Business domain",
        contentAEMResponse.getConfig().getAuthentication().getBusiness().getBusinessDomain());

    // Additional test scenario where businessBooker is null

    when(responseSpec.bodyToMono(AemIndexHeaderDataDto.class)).thenReturn(
            mockAemIndexHotelDataDtoResponseForBb());

    // Act
    final var contentAEMResponseNonBb = aemClient.getIndexHeaderData(indexHeaderDataRequestDtoMock(null));

    // Assert
    assertThat(contentAEMResponseNonBb, notNullValue());
    assertThat(contentAEMResponseNonBb.getConfig(), notNullValue());
  }

  @Test
  void getPriceFinderGlobalFinderConfig__ShouldReturnOK() {
    //Arrange
    PriceFinderGlobalConfigRequestDto priceFinderglobalConfigRequestDtoMock = priceFinderGlobalConfigRequestDtoMock();

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PriceFinderGlobalConfigDto.class)).thenReturn(mockPriceFinderGlobalConfigDtoResponse());

    //Act
    final PriceFinderGlobalConfigDto priceFinderConfigDto = aemClient
            .getPriceFinderGlobalConfig(priceFinderglobalConfigRequestDtoMock);

    //Assert
    assertThat(priceFinderConfigDto, notNullValue());
    final List<PriceFinderViewsDto> priceFinderViewsDto = priceFinderConfigDto.getPriceFinderConfig()
            .getPriceFinderViews();
    assertThat(priceFinderViewsDto, notNullValue());
    assertThat(priceFinderViewsDto, hasSize(2));
    final PriceFinderViewsDto priceFinderViewsDto1 = priceFinderViewsDto.get(0);
    assertThat(priceFinderViewsDto1.getBannerColour(), is("#FFFFFF"));
    assertThat(priceFinderViewsDto1.getDestinations().get(0).getLocations().get(0).getLocationId(),
            is("ChIJdd4hrwug2EcRmSrV3Vo6llI"));
    final PriceFinderViewsDto priceFinderViewsDto2 = priceFinderViewsDto.get(1);
    assertThat(priceFinderViewsDto2.getBannerColour(), is("#FFFcFF"));
    assertThat(priceFinderViewsDto2.getDestinations().get(0).getLocations().get(1).getLocationId(),
            is("Ch4IJ2_UmUkxNekgRqmv-BDgUvtk"));
  }

  @Test
  void getPriceFinderGlobalFinderConfig_aemError_ShouldReturnException() {
    //Arrange
    var priceFinderGlobalConfigRequestDto = priceFinderGlobalConfigRequestDtoMock();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    var actual =
            assertThrows(AemResponseException.class, () ->
                    aemClient.getPriceFinderGlobalConfig(priceFinderGlobalConfigRequestDto));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(AEM_PRICE_FINDER_CONFIG_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is("Unable to get price finder global config data."));
    assertThat(actual.getErrorCode(), is(AEM_PRICE_FINDER_CONFIG_EXCEPTION.getCode()));
  }

  private Mono<AemIndexHeaderDataDto> mockAemIndexHotelDataDtoResponseForBb() {
    var config = Config.builder()
        .authentication(getAuthentication())
        .build();

    AemIndexHeaderDataDto indexHeaderDataDto = new AemIndexHeaderDataDto();
    indexHeaderDataDto.setConfig(config);

    return Mono.just(indexHeaderDataDto);
  }

  private Authentication getAuthentication() {
    var accountLink = AccountLink.builder()
        .title("Account settings")
        .url("/gb/en/business-booker/accountTestURL")
        .build();

    return Authentication.builder()
        .accountLinks(List.of(accountLink))
        .business(getBusiness())
        .build();
  }

  private Business getBusiness() {
    return Business.builder()
        .businessAccountLinks(getBusinessAccountLinks())
        .businessLogin("Business login")
        .travelForBusiness("Travel for business")
        .businessDomain("Business domain")
        .build();
  }

  private List<BusinessAccountLink> getBusinessAccountLinks() {
    var businessAccountLink = BusinessAccountLink.builder()
        .title("Company management")
        .subMenuLinks(getSubMenuLinks())
        .build();

    return List.of(businessAccountLink);
  }

  private List<SubMenuLink> getSubMenuLinks() {
    var subMenuLink = SubMenuLink.builder()
        .title("Manage employees")
        .url("/gb/en/business-booker/testURL")
        .build();

    return List.of(subMenuLink);
  }

  private GlobalConfigRequestDto globalConfigRequestDtoMock() {
    return GlobalConfigRequestDto.builder()
        .country("gb")
        .language("en")
        .channelId("DISTR")
        .brand("PI")
        .build();
  }

  private DlpInformationRequestDto dlpInformationRequestDtoMock() {
    return DlpInformationRequestDto.builder()
        .country("gb")
        .language("en")
        .dlpPath("/england/bedfordshire/luton")
        .build();
  }

  private IndexHeaderDataRequestDto indexHeaderDataRequestDtoMock(Boolean businessBooker) {
    return IndexHeaderDataRequestDto.builder()
        .country("gb")
        .language("en")
        .businessBooker(businessBooker)
        .build();
  }

  private Mono<AemIndexHeaderDataDto> mockAemIndexHotelDataDtoResponse() {
    var promotion = Promotions.builder()
            .maxRoomsAmend(2)
            .maxRooms(2)
            .numberOfNights(2)
            .promoCode("ST20RU")
            .page("summer-sale")
            .enabled(true)
            .build();
    var offer = Offer.builder()
        .maxRooms(2)
        .cellCode("")
        .page("travel-industry-rate")
        .numberOfNights(9)
        .corpId("15010601")
        .ratePlanCode("FCDNLR30")
        .build();
    var global = Global.builder()
        .single("Single")
        .done("Done")
        .room("room")
        .twin("Twin")
        .adult("adult")
        .child("child")
        .night("night")
        .rooms("rooms")
        .today("Today")
        .maxRooms(4)
        .adultsLabel("Adults")
        .childrenLabel("Children")
        .children("children")
        .roomsLabel("Rooms")
        .offers(List.of(offer))
        .promotions(List.of(promotion))
        .build();

    var form = Form.builder()
        .childrenHelperText("2-15 years")
        .invalidFutureDate(
            "Unable to book over a year in advance, rates below are for availability today")
        .summary("You searched for")
        .invalidReference("Invalid reference")
        .bookingReference("Booking reference")
        .adultsHelperText("Max 2 per room")
        .hotelsLabel("Hotels")
        .change("Change search")
        .invalidNights("Please enter a valid number of nights")
        .bookingSurnameLabel("Booking surname *")
        .changeGuestRoom("Change guest/room type")
        .lowerPrices("Lower prices")
        .invalidDate("Please enter a valid date")
        .search("Search")
        .update("Update")
        .bookingTypeInvalid(
            "To search for Business Booker stays, please visit our Business Booker portal")
        .findBookingDescription(
            "Please enter your details below. You'll find your booking reference in your confirmation email.")
        .snowdropErrorHotelDirectory("Load hotel directory")
        .cotLimit("0-2 years")
        .groupBooking("Group bookings")
        .roomType("Room type")
        .invalidPastDate(
            "Your selected date is in the past, rates below are for availability today")
        .bookingInvalid(
            "Some of these details are missing or invalid. Please provide correct details")
        .searchBookingError(
            "We are unable to retrieve your booking at the moment. Please try again later")
        .where("Enter place, postcode or hotel")
        .bookingReferenceLabel("Booking reference *")
        .bookingSurname("Surname")
        .invalidRooms("Please enter a valid room composition")
        .invalidLocation("Please enter a location or a hotel")
        .snowdropErrorUrl("//www.premierinn.com/gb/en/hotels.html")
        .includeCot("Include a cot?")
        .snowdropErrorLinkConnector("or")
        .checkinOnlineLandingPageTitle("Check in online")
        .removeRoom("Remove room")
        .snowdropErrorRetry("Retry")
        .arrivalDateLabel("Arrival date *")
        .checkinOnlineLandingPageFormTitle("Find a booking")
        .checkout("Check out:")
        .numberOfNights(9)
        .findBookingTitle("Amend or cancel a booking")
        .snowdropError("Oh dear, something went wrong when we tried to load the suggestions.")
        .checkinOnlineLandingPageDescription(
            "Spend more time enjoying your stay by checking in online.When you check in online you can collect your room key as soon as you arrive, plus you can add breakfast and Ultimate Wi-Fi to your booking in advance.")
        .build();

    var notifications = Notifications.builder()
        .businessGroupBookingMessage(
            "If you’d like to make a booking for more than 4 rooms then please email Group.Enquiries@whitbread.com.")
        .addAnotherRoom("To add another room, please make a seperate booking.")
        .groupBookingMessage(
            "If you’d like to book five rooms or more, please call us and we’ll be happy to help.")
            .groupBookingFormPageMessage(
                    "For a group booking of 5 to 9 rooms, call us on 0333 003 8101. To book 10 rooms or more, please complete the <a class=\\\"wb-a\\\" href=\\\"why/groups/form{groupBookingLink}\\\">group booking form</a> and we will be in contact to discuss your enquiry.")
        .noResults(
            "Sorry, we couldn’t find any hotels with available rooms. Try changing your search area.")
        .groupBookingHeader("Unable to add more rooms")
        .build();

    var results = Results.builder()
        .notifications(notifications)
        .build();

    var countryEn = Country.builder()
        .language("English")
        .flagUrl("/etc/clientlibs/pi-header/resources/images/british-round.svg")
        .build();

    var countryDe = Country.builder()
        .language("German")
        .flagUrl("/etc/clientlibs/pi-header/resources/images/germany-round.svg")
        .build();

    var countries = List.of(countryEn, countryDe);

    var header = Header.builder()
        .image("/content/dam/pi/websites/desktop/icons/brand/pi-logo-rest-easy.svg")
        .build();

    var leisure = Leisure.builder()
        .signupButton("Sign up to My Premier Inn")
        .tab("My Premier Inn")
        .build();

    var login = Login.builder()
        .rememberMeLabel("Remember me")
        .badCredentialsError("Your email address or password is incorrect")
        .sessionExpired("Your session expired, please login again")
        .invalidEmail("Please enter a valid e-mail address")
        .leisure(leisure)
        .build();

    var forgottenPassword = ForgottenPassword.builder()
        .notRegisteredError("We don’t recognise this email address")
        .cancel("Cancel")
        .emailSentHeader("Email sent")
        .genericError("Looks like something went wrong, please try again later")
        .emailSentMessage("We've sent you an email with instructions to reset your password")
        .build();

    var authentication = Authentication.builder()
        .accountDescription("Your account")
        .login(login)
        .forgottenPassword(forgottenPassword)
        .build();

    var content = Content.builder()
        .global(global)
        .form(form)
        .results(results)
        .countries(countries)
        .header(header)
        .authentication(authentication)
        .build();

    AemIndexHeaderDataDto indexHeaderDataDto = new AemIndexHeaderDataDto();
    indexHeaderDataDto.setContent(content);

    return Mono.just(indexHeaderDataDto);
  }

  private Mono<GlobalConfigDto> mockGlobalConfigDtoResponse() {

    var roomClassOrderDto1 = RoomClassOrderDto.builder().code("ON").order(1).build();
    var roomClassOrderDto2 = RoomClassOrderDto.builder().code("TW").order(2).build();

    var bookingWidgetConfig = BookingWidgetConfigDto.builder()
        .maxRooms(4)
        .maxRoomsAmend(5)
        .build();

    return Mono.just(GlobalConfigDto.builder()
        .roomClassConfig(List.of(roomClassOrderDto1, roomClassOrderDto2))
        .bookingWidgetConfig(bookingWidgetConfig)
        .build());
  }

  private Mono<PriceFinderGlobalConfigDto> mockPriceFinderGlobalConfigDtoResponse() {
    HotelCodesDto hotelCodesDto1 = HotelCodesDto.builder().code("LONHOL").order(1).build();
    HotelCodesDto hotelCodesDto2 = HotelCodesDto.builder().code("FRAMTI").order(2).build();

    InfoMessagesDto infoMessagesDto1 = InfoMessagesDto.builder()
            .messageTitle("messageTitle")
            .messageSubtitle("messageSubtitle")
            .messageType("messageType")
            .messageOrder(1).build();
    InfoMessagesDto infoMessagesDto2 = InfoMessagesDto.builder()
            .messageTitle("messageTitle2")
            .messageSubtitle("messageSubtitle2")
            .messageType("messageType2")
            .messageOrder(2).build();
    LocationsDto locationsdto1     = LocationsDto.builder()
            .locationId("ChIJdd4hrwug2EcRmSrV3Vo6llI")
            .locationName("London")
            .locationOrder(30)
            .build();
    LocationsDto locationsdto2     = LocationsDto.builder()
            .locationId("ChIJ2_UmUkxNekgRqmv-BDgUvtk")
            .locationName("Manchester")
            .locationOrder(30)
            .build();
    SeoHreflangsDto seoHreflangsDto1 = SeoHreflangsDto.builder()
            .hreflang("en-gb")
            .href("https://www.hotel.com/gb/en/price-finder/campaign/test")
            .build();
    SeoHreflangsDto seoHreflangsDto2 = SeoHreflangsDto.builder()
            .hreflang("en-gb1")
            .href("https://www.hotel.com/gb/en/price-finder/campaign/test1")
            .build();
    DestinationsDto destinationsDto1 = DestinationsDto.builder()
            .locations(List.of(locationsdto1,locationsdto2)).build();

    PriceFinderViewsDto priceFinderViewsDto1 = PriceFinderViewsDto.builder()
            .path("gb/en/price-finder/campaign/test")
            .bannerImage("/image")
            .bannerColour("#FFFFFF")
            .bannerHeadline("bannerHeadline")
            .bannerSubtext("bannerSubtext")
            .termsLabel("termsLabel")
            .dateRangeStart("31/01/2025")
            .dateRangeEnd("31/12/2025")
            .checkinDate("31/11/2025")
            .highlightedPriceRangeStep(5)
            .highlightedPriceRangeMin(45)
            .highlightedPriceRangeMax(80)
            .highlightedPricePrimaryColour("#AAAAAA")
            .highlightedPriceSecondaryColour("#BBBBBB")
            .filterRoomSelected("DB")
            .locationId("ChIJdd4hrwug2EcRmSrV3Vo6llI")
            .locationName("London")
            .locationRadius(30.0)
            .hotelCodes(List.of(hotelCodesDto1, hotelCodesDto2))
            .destinations(List.of(destinationsDto1))
            .infoMessages(List.of(infoMessagesDto1, infoMessagesDto2))
            .seoMetaTitle("seoMetaTitle")
            .seoMetaDescription("seoMetaDescription")
            .seoCardImageUrl("seoCardImageUrl")
            .seoRobots("seoRobots")
            .seoHreflangs(List.of(seoHreflangsDto1, seoHreflangsDto2))
            .build();

    HotelCodesDto hotelCodesDto3 = HotelCodesDto.builder().code("LONFOL").order(3).build();
    HotelCodesDto hotelCodesDto4 = HotelCodesDto.builder().code("FTAMTI").order(4).build();
    InfoMessagesDto infoMessagesDto3 = InfoMessagesDto.builder()
            .messageTitle("messageTitle")
            .messageSubtitle("messageSubtitle")
            .messageType("messageType")
            .messageOrder(1).build();
    InfoMessagesDto infoMessagesDto4 = InfoMessagesDto.builder()
            .messageTitle("messageTitle2")
            .messageSubtitle("messageSubtitle2")
            .messageType("messageType2")
            .messageOrder(2).build();
    LocationsDto locationsdto3     = LocationsDto.builder()
            .locationId("ChIJdd4hrwug2EcRmSrV3Vo6llI")
            .locationName("LiverPool")
            .locationOrder(3)
            .build();
    LocationsDto locationsdto4     = LocationsDto.builder()
            .locationId("Ch4IJ2_UmUkxNekgRqmv-BDgUvtk")
            .locationName("Edinburgh")
            .locationOrder(4)
            .build();
    SeoHreflangsDto seoHreflangsDto3 = SeoHreflangsDto.builder()
            .hreflang("en-gb")
            .href("https://www.hotel.com/gb/en/price-finder/campaign/test")
            .build();
    SeoHreflangsDto seoHreflangsDto4 = SeoHreflangsDto.builder()
            .hreflang("en-gb1")
            .href("https://www.hotel.com/gb/en/price-finder/campaign/test1")
            .build();
    DestinationsDto destinationsDto2 = DestinationsDto.builder()
            .locations(List.of(locationsdto3,locationsdto4)).build();

    PriceFinderViewsDto priceFinderViewsDto2 = PriceFinderViewsDto.builder()
            .path("gb/en/price-finder/campaign/test")
            .bannerImage("/image1")
            .bannerColour("#FFFcFF")
            .bannerHeadline("bannerHeadline1")
            .bannerSubtext("bannerSubtext1")
            .termsLabel("termsLabel1")
            .dateRangeStart("3/01/2025")
            .dateRangeEnd("1/12/2025")
            .checkinDate("3/11/2025")
            .highlightedPriceRangeStep(3)
            .highlightedPriceRangeMin(25)
            .highlightedPriceRangeMax(90)
            .highlightedPricePrimaryColour("#AA3AAAA")
            .highlightedPriceSecondaryColour("#BBB3BBB")
            .filterRoomSelected("DB")
            .locationId("ChIJdd4hrwug22EcRmSrV3Vo6llI")
            .locationName("London")
            .locationRadius(40.0)
            .hotelCodes(List.of(hotelCodesDto3, hotelCodesDto4))
            .destinations(List.of(destinationsDto2))
            .infoMessages(List.of(infoMessagesDto3, infoMessagesDto4))
            .seoMetaTitle("seoMetaTitle3")
            .seoMetaDescription("seoMetaDescription4")
            .seoCardImageUrl("seoCardImageUrl")
            .seoRobots("seoRobots1")
            .seoHreflangs(List.of(seoHreflangsDto3, seoHreflangsDto4))
            .build();
    PriceFinderConfigDto priceFinderConfig = PriceFinderConfigDto.builder()
            .priceFinderViews(List.of(priceFinderViewsDto1, priceFinderViewsDto2)).build();
    PriceFinderGlobalConfigDto priceFinderGlobalConfigDto = new PriceFinderGlobalConfigDto();
    priceFinderGlobalConfigDto.setPriceFinderConfig(priceFinderConfig);
    return Mono.just(priceFinderGlobalConfigDto);
  }

  private PriceFinderGlobalConfigRequestDto priceFinderGlobalConfigRequestDtoMock() {
    return PriceFinderGlobalConfigRequestDto.builder()
            .country("gb")
            .language("en")
            .channelId("DISTR")
            .brand("PI")
            .build();
  }
}
