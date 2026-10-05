package uk.co.whitbread.content.domain.logic;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.utils.AppsHomepageUtils.createHomepageAppsRequest;
import static uk.co.whitbread.content.utils.AppsHomepageUtils.mock_HomepageApps;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.hamcrest.collection.IsMapContaining;
import org.hamcrest.collection.IsMapWithSize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.util.ReflectionTestUtils;
import uk.co.whitbread.content.domain.logic.mapper.HotelInformationExtendedMapper;
import uk.co.whitbread.content.domain.model.dlp.in.DlpInformationRequest;
import uk.co.whitbread.content.domain.model.dlp.out.DlpInformation;
import uk.co.whitbread.content.domain.model.dlp.out.Hotel;
import uk.co.whitbread.content.domain.model.globalconfig.in.GlobalConfigRequest;
import uk.co.whitbread.content.domain.model.globalconfig.out.AcceptedRoomTypes;
import uk.co.whitbread.content.domain.model.globalconfig.out.GlobalConfig;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionItems;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsConfig;
import uk.co.whitbread.content.domain.model.globalconfig.out.RoomClassConfig;
import uk.co.whitbread.content.domain.model.globalconfig.out.RoomClassOrder;
import uk.co.whitbread.content.domain.model.globalconfig.out.SearchRules;
import uk.co.whitbread.content.domain.model.hotel.in.HotelInformationRequest;
import uk.co.whitbread.content.domain.model.hotel.in.HotelsInformationRequest;
import uk.co.whitbread.content.domain.model.hotel.out.AcceptedCreditCard;
import uk.co.whitbread.content.domain.model.hotel.out.AccessibilityInfo;
import uk.co.whitbread.content.domain.model.hotel.out.AncillaryCloseout;
import uk.co.whitbread.content.domain.model.hotel.out.AncillaryCloseoutItem;
import uk.co.whitbread.content.domain.model.hotel.out.BookingFlow;
import uk.co.whitbread.content.domain.model.hotel.out.BookingFlowItem;
import uk.co.whitbread.content.domain.model.hotel.out.BookRestaurantCta;
import uk.co.whitbread.content.domain.model.hotel.out.HotelFacility;
import uk.co.whitbread.content.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.content.domain.model.hotel.out.HotelInformation;
import uk.co.whitbread.content.domain.model.hotel.out.HotelInformationExtended;
import uk.co.whitbread.content.domain.model.hotel.out.HotelPaymentInformation;
import uk.co.whitbread.content.domain.model.hotel.out.HotelShortInformation;
import uk.co.whitbread.content.domain.model.hotel.out.HotelsOpeningSoonResult;
import uk.co.whitbread.content.domain.model.hotel.out.HotelsWithFacilityFilterResult;
import uk.co.whitbread.content.domain.model.hotel.out.ImportantInfo;
import uk.co.whitbread.content.domain.model.hotel.out.InfoItem;
import uk.co.whitbread.content.domain.model.hotel.out.Menu;
import uk.co.whitbread.content.domain.model.hotel.out.Restaurant;
import uk.co.whitbread.content.domain.model.hotel.out.RoomConfiguration;
import uk.co.whitbread.content.domain.model.hotel.out.TabGroup;
import uk.co.whitbread.content.domain.model.hotel.out.TabItem;
import uk.co.whitbread.content.domain.model.hotel.out.TargetBookingFlowItem;
import uk.co.whitbread.content.domain.model.index.header.data.in.IndexHeaderDataRequest;
import uk.co.whitbread.content.domain.model.index.header.data.in.LocalizationRequest;
import uk.co.whitbread.content.domain.model.index.header.data.out.*;
import uk.co.whitbread.content.domain.model.labels.in.CategoryEnum;
import uk.co.whitbread.content.domain.model.labels.in.LabelsRequest;
import uk.co.whitbread.content.domain.model.labels.out.Extras;
import uk.co.whitbread.content.domain.model.labels.out.ExtrasLabel;
import uk.co.whitbread.content.domain.model.pricefinder.in.PriceFinderGlobalConfigRequest;
import uk.co.whitbread.content.domain.model.pricefinder.out.Destinations;
import uk.co.whitbread.content.domain.model.pricefinder.out.HotelCodes;
import uk.co.whitbread.content.domain.model.pricefinder.out.InfoMessages;
import uk.co.whitbread.content.domain.model.pricefinder.out.Locations;
import uk.co.whitbread.content.domain.model.pricefinder.out.PriceFinderConfig;
import uk.co.whitbread.content.domain.model.pricefinder.out.PriceFinderGlobalConfig;
import uk.co.whitbread.content.domain.model.pricefinder.out.PriceFinderViews;
import uk.co.whitbread.content.domain.model.pricefinder.out.SeoHreflangs;
import uk.co.whitbread.content.domain.model.searchresults.data.out.Api;
import uk.co.whitbread.content.domain.model.searchresults.data.out.Brand;
import uk.co.whitbread.content.domain.model.searchresults.data.out.Config;
import uk.co.whitbread.content.domain.model.searchresults.data.out.Content;
import uk.co.whitbread.content.domain.model.searchresults.data.out.Facilities;
import uk.co.whitbread.content.domain.model.searchresults.data.out.Global;
import uk.co.whitbread.content.domain.model.searchresults.data.out.Hreflang;
import uk.co.whitbread.content.domain.model.searchresults.data.out.Result;
import uk.co.whitbread.content.domain.model.searchresults.data.out.Results;
import uk.co.whitbread.content.domain.model.searchresults.data.out.SearchResultsData;
import uk.co.whitbread.content.domain.model.searchresults.data.out.Seo;
import uk.co.whitbread.content.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.content.domain.ports.secondary.OhipOutPort;
import uk.co.whitbread.content.infrastructure.rest.client.content.exceptions.OhipException;


@ExtendWith(MockitoExtension.class)
class ContentInPortImplTest {

  @InjectMocks
  private ContentInPortImpl contentInPort;

  @Mock
  private ContentOutPort contentOutPort;
  @Mock
  private OhipOutPort ohipOutPort;
  @Mock
  private CacheManager cacheManager;

  private static final String COUNTRY_GB = "gb";
  private static final String LANGUAGE_EN = "en";

  @Test
  void getLabels__ShouldReturnOk() {
    //Arrange
    when(this.contentOutPort.getLabels(any())).thenReturn(mockContentResponse());

    //Act
    final var contentAEMResponse = contentInPort.getLabels(createContentRequest());

    //Assert
    assertThat(contentAEMResponse, notNullValue());
    assertThat(contentAEMResponse, IsMapContaining.hasEntry("account.dashboard.and", "and"));
    assertThat(contentAEMResponse, IsMapContaining.hasEntry("account.dashboard.room", "Room"));
    assertThat(contentAEMResponse,
        IsMapContaining.hasEntry("config.errorMessages.cardDetails.yourReference.valid",
            "Customer Reference may not exceed 24 characters and may only contain letters, " +
                "numbers, spaces, hyphens or dots. (ER9)"));
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

  @Test
  void getLabels__FilterAndShouldReturnOk() {
    //Arrange
    when(this.contentOutPort.getLabels(any())).thenReturn(mockContentResponse());

    //Act
    final var contentAEMResponse = contentInPort.getLabels(createContentRequestWithFilter());

    //Assert
    assertThat(contentAEMResponse, notNullValue());
    assertThat(contentAEMResponse, IsMapWithSize.aMapWithSize(2));
    assertThat(contentAEMResponse, IsMapContaining.hasEntry("account.dashboard.room", "Room"));
    assertThat(contentAEMResponse, IsMapContaining.hasEntry("feedback.contactus.surname", "Last name"));
  }

  @Test
  void getHotelPaymentInformation__ShouldReturnOk() {
    //Arrange
    var expectedResponse = new HotelPaymentInformation();
    given(contentOutPort.getHotelPaymentInformation(any())).willReturn(expectedResponse);

    //Act
    var actualResponse = contentInPort.getHotelPaymentInformation(createHotelInformationDto());

    //Assert
    assertThat(actualResponse, equalTo(expectedResponse));
  }

  @Test
  void getExtras__ShouldReturnOk() {
    //Arrange
    when(this.contentOutPort.getExtras(any(), any())).thenReturn(getExtras());

    //Act
    final var response = contentInPort.getExtras(any(),any());

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getExtrasLabels(), hasSize(3));
  }

  private LabelsRequest createContentRequest() {
    return LabelsRequest.builder()
        .category(CategoryEnum.BOOKING)
        .country("gb")
        .language("en")
        .build();
  }

  private LabelsRequest createContentRequestWithFilter() {
    return LabelsRequest.builder()
        .category(CategoryEnum.BOOKING)
        .country("gb")
        .language("en")
        .labels(List.of("account.dashboard.room", "feedback.contactus.surname"))
        .build();
  }

  private Map<String, String> mockContentResponse() {
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
    return response;
  }

  @Test
  void getHotelInformation__ShouldReturnOk() {
    //Arrange
    when(this.contentOutPort.getHotelInformation(any(), eq(false))).thenReturn(mockHotelInformation());

    ReflectionTestUtils.setField(contentInPort, "hotelInformationExtendedMapper",
        Mappers.getMapper(HotelInformationExtendedMapper.class));

    //Act
    final var aemResponse = contentInPort.getHotelInformation(createHotelInformationDto());

    //Assert
    assertThat(aemResponse, notNullValue());
    assertEquals("Test Hotel", aemResponse.getName());
    assertEquals("PI", aemResponse.getBrand());
    assertEquals("testurl/image/logo-url.jpg", aemResponse.getRestaurant().getLogoSrc());
    assertEquals("food-at-the-social.jpg", aemResponse.getRestaurant().getBookingCardImage());
    assertEquals("Food at the Social", aemResponse.getRestaurant().getBookingCardTitle());
    assertEquals("Delicious food served all day long.", aemResponse.getRestaurant()
            .getBookingCardDescription());
    assertEquals("/content/dam/BackgroundImageRestaurants.png", aemResponse.getRestaurant()
            .getBookingCardBackgroundImage());
    assertEquals("Book a table", aemResponse.getBookRestaurantCta().getBookingCardCtaText());
    assertEquals("/gb/en/restaurants/birmingham-necairport/book", aemResponse.
            getBookRestaurantCta().getBookingCardCtaLink());
    assertEquals("sidebar-card-social", aemResponse.getBookRestaurantCta().getBookingCardTrackingId());
    assertEquals("Important Information", aemResponse.getImportantInfo().getTitle());
    assertEquals("headerTest", aemResponse.getAccessibilityInfo().getHeader());
    assertEquals("All accessible rooms at this hotel are double rooms.",
        aemResponse.getAccessibilityInfo().getText());
    assertEquals("linkTextTest", aemResponse.getAccessibilityInfo().getLinkText());
    assertEquals("3527934510", aemResponse.getAccessibilityInfo().getPhoneNumber());
    assertEquals("booking-business",
        aemResponse.getBookingFlow().getBookingFlowItems().get(0).getBookingIdBB());
    assertEquals("test-county", aemResponse.getCounty());
    assertEquals("SE", aemResponse.getAncillaryCloseout().getItems().get(0).getServiceCode());
    assertEquals("No meals available", aemResponse.getAncillaryCloseout().getNoMealsHeading());
    assertEquals("Sorry, meals are not available for your selected dates",
        aemResponse.getAncillaryCloseout().getNoMealsMessage());
    assertEquals("de-de", aemResponse.getSeo().getHreflangs().get(0).getHreflang());
    assertEquals("https://www.premierinn.com/de/de/home.html",
            aemResponse.getSeo().getHreflangs().get(0).getHref());

    Assertions.assertThat(aemResponse.getRoomConfiguration().getTabItems())
        .hasSize(1)
        .extracting(TabItem::getRoomName)
        .containsOnlyOnce("RoomTitle");
    Assertions.assertThat(aemResponse.getRoomConfiguration().getTabGroups())
        .hasSize(1)
        .extracting(TabGroup::getGroupName)
        .containsOnlyOnce("GroupTitle");
    Assertions.assertThat(aemResponse.getRestaurant().getMenus())
        .hasSize(2)
        .extracting(Menu::getMenuSrc)
        .containsExactlyInAnyOrder("/test/Global Dinner.pdf", "/test/Global Breakfast.pdf");
    Assertions.assertThat(aemResponse.getSeo().getHreflangs())
            .hasSize(1);

    test_paymentCodeTypes(aemResponse);
    test_importantInfo(aemResponse);

  }

  private void test_paymentCodeTypes(HotelInformationExtended aemResponse) {
    Assertions.assertThat(aemResponse.getPaymentCodeTypes()).hasSize(2);
    Assertions.assertThat(aemResponse.getPaymentCodeTypes())
        .extracting(AcceptedCreditCard::getCode).containsExactly("AC", "AM");
    Assertions.assertThat(aemResponse.getPaymentCodeTypes())
        .extracting(AcceptedCreditCard::getCode).containsExactly("AC", "AM");
    Assertions.assertThat(aemResponse.getPaymentCodeTypes())
        .extracting(AcceptedCreditCard::getCode3cp).containsExactly("MC", "AX");
    Assertions.assertThat(aemResponse.getPaymentCodeTypes())
        .extracting(AcceptedCreditCard::getCodeOpera).containsExactly("MC", "AX");
    Assertions.assertThat(aemResponse.getPaymentCodeTypes())
        .extracting(AcceptedCreditCard::getFeeAmount).containsExactly("", "");
    Assertions.assertThat(aemResponse.getPaymentCodeTypes())
        .extracting(AcceptedCreditCard::getFeeCurrency).containsExactly("", "");
    Assertions.assertThat(aemResponse.getPaymentCodeTypes())
        .extracting(AcceptedCreditCard::getListOrder).containsExactly("3", "6");
    Assertions.assertThat(aemResponse.getPaymentCodeTypes())
        .extracting(AcceptedCreditCard::getName)
        .containsExactly("Mastercard Credit", "American Express");
    Assertions.assertThat(aemResponse.getPaymentCodeTypes())
        .extracting(AcceptedCreditCard::getPaymentOnly).containsExactly(false, false);
    Assertions.assertThat(aemResponse.getPaymentCodeTypes())
        .extracting(AcceptedCreditCard::getSchemeLogo)
        .containsExactly("/content/dam/global/booking/Mastercard.jpg",
            "/content/dam/global/booking/AX.jpg");
  }

  private void test_importantInfo(HotelInformationExtended aemResponse) {
    Assertions.assertThat(aemResponse.getImportantInfo().getInfoItems())
        .hasSize(1)
        .extracting(InfoItem::getText)
        .containsExactly("There is no air conditioning at this hotel.");
    Assertions.assertThat(aemResponse.getImportantInfo().getInfoItems())
        .hasSize(1)
        .extracting(InfoItem::getPriority)
        .containsExactly("1");
    Assertions.assertThat(aemResponse.getImportantInfo().getInfoItems())
        .hasSize(1)
        .extracting(InfoItem::getStartDate)
        .containsExactly("17/09/2022");
    Assertions.assertThat(aemResponse.getImportantInfo().getInfoItems())
            .hasSize(1)
            .extracting(InfoItem::getHtmlText)
            .containsExactly("There is no air conditioning at this hotel. <a href=\"/gb/en/faq/our-rooms.html\">Learn more about our rooms</a>");
    Assertions.assertThat(aemResponse.getImportantInfo().getInfoItems())
        .hasSize(1)
        .extracting(InfoItem::getEndDate)
        .containsExactly("20/10/2022");
    Assertions.assertThat(aemResponse.getImportantInfo().getInfoItems())
        .hasSize(1)
        .extracting(InfoItem::isHideOnHdp)
        .containsExactly(true);
    Assertions.assertThat(aemResponse.getImportantInfo().getInfoItems())
        .hasSize(1)
        .extracting(InfoItem::isHideOnBookingFlow)
        .containsExactly(false);
  }

  @Test
  void getHotelInformationForDistr__ShouldReturnOk() {
    //Arrange
    when(this.contentOutPort.getHotelInformation(any(), eq(false))).thenReturn(mockHotelInformation());
    when(this.ohipOutPort.getHotelInfo(any())).thenReturn(mockHotelInfo());

    ReflectionTestUtils.setField(contentInPort, "hotelInformationExtendedMapper",
        Mappers.getMapper(HotelInformationExtendedMapper.class));
    HotelInformationRequest hotelInformationDto = createHotelInformationDto();
    hotelInformationDto.setChannel("DISTR");

    //Act
    final var aemResponse = contentInPort.getHotelInformation(hotelInformationDto);

    //Assert
    assertThat(aemResponse, notNullValue());
    assertEquals("Test Hotel", aemResponse.getName());
    assertEquals("PI", aemResponse.getBrand());
    assertEquals("testurl/image/logo-url.jpg", aemResponse.getRestaurant().getLogoSrc());
    assertEquals("Important Information", aemResponse.getImportantInfo().getTitle());
    assertEquals("headerTest", aemResponse.getAccessibilityInfo().getHeader());
    assertEquals("All accessible rooms at this hotel are double rooms.",
        aemResponse.getAccessibilityInfo().getText());
    assertEquals("linkTextTest", aemResponse.getAccessibilityInfo().getLinkText());
    assertEquals("3527934510", aemResponse.getAccessibilityInfo().getPhoneNumber());
    assertEquals("booking-business",
        aemResponse.getBookingFlow().getBookingFlowItems().get(0).getBookingIdBB());
    assertEquals("GBP", aemResponse.getCurrencyCode());
    assertEquals("E", aemResponse.getLanguageCode());
    assertEquals("Europe/London", aemResponse.getTimeZone());
    assertEquals("01/01/1970, 15:00", aemResponse.getCheckInTime());
    assertEquals("01/01/1970, 12:00", aemResponse.getCheckOutTime());
    assertEquals("SE", aemResponse.getAncillaryCloseout().getItems().get(0).getServiceCode());
    assertEquals("No meals available", aemResponse.getAncillaryCloseout().getNoMealsHeading());
    assertEquals("Sorry, meals are not available for your selected dates",
        aemResponse.getAncillaryCloseout().getNoMealsMessage());

    Assertions.assertThat(aemResponse.getRoomConfiguration().getTabItems())
        .hasSize(1)
        .extracting(TabItem::getRoomName)
        .containsOnlyOnce("RoomTitle");
    Assertions.assertThat(aemResponse.getRoomConfiguration().getTabGroups())
        .hasSize(1)
        .extracting(TabGroup::getGroupName)
        .containsOnlyOnce("GroupTitle");
    Assertions.assertThat(aemResponse.getRestaurant().getMenus())
        .hasSize(2)
        .extracting(Menu::getMenuSrc)
        .containsExactlyInAnyOrder("/test/Global Dinner.pdf", "/test/Global Breakfast.pdf");

    test_paymentCodeTypesDistr(aemResponse);
    test_importantInfoDistr(aemResponse);

  }

  private void test_paymentCodeTypesDistr(HotelInformationExtended aemResponse) {
    Assertions.assertThat(aemResponse.getPaymentCodeTypes()).hasSize(2);
    Assertions.assertThat(aemResponse.getPaymentCodeTypes())
        .extracting(AcceptedCreditCard::getCode).containsExactly("AC", "AM");
    Assertions.assertThat(aemResponse.getPaymentCodeTypes())
        .extracting(AcceptedCreditCard::getCode).containsExactly("AC", "AM");
    Assertions.assertThat(aemResponse.getPaymentCodeTypes())
        .extracting(AcceptedCreditCard::getCode3cp).containsExactly("MC", "AX");
    Assertions.assertThat(aemResponse.getPaymentCodeTypes())
        .extracting(AcceptedCreditCard::getCodeOpera).containsExactly("MC", "AX");
    Assertions.assertThat(aemResponse.getPaymentCodeTypes())
        .extracting(AcceptedCreditCard::getFeeAmount).containsExactly("", "");
    Assertions.assertThat(aemResponse.getPaymentCodeTypes())
        .extracting(AcceptedCreditCard::getFeeCurrency).containsExactly("", "");
    Assertions.assertThat(aemResponse.getPaymentCodeTypes())
        .extracting(AcceptedCreditCard::getListOrder).containsExactly("3", "6");
    Assertions.assertThat(aemResponse.getPaymentCodeTypes())
        .extracting(AcceptedCreditCard::getName)
        .containsExactly("Mastercard Credit", "American Express");
    Assertions.assertThat(aemResponse.getPaymentCodeTypes())
        .extracting(AcceptedCreditCard::getPaymentOnly).containsExactly(false, false);
    Assertions.assertThat(aemResponse.getPaymentCodeTypes())
        .extracting(AcceptedCreditCard::getSchemeLogo)
        .containsExactly("/content/dam/global/booking/Mastercard.jpg",
            "/content/dam/global/booking/AX.jpg");
  }

  private void test_importantInfoDistr(HotelInformationExtended aemResponse) {
    Assertions.assertThat(aemResponse.getImportantInfo().getInfoItems())
        .hasSize(1)
        .extracting(InfoItem::getText)
        .containsExactly("There is no air conditioning at this hotel.");
    Assertions.assertThat(aemResponse.getImportantInfo().getInfoItems())
        .hasSize(1)
        .extracting(InfoItem::getPriority)
        .containsExactly("1");
    Assertions.assertThat(aemResponse.getImportantInfo().getInfoItems())
        .hasSize(1)
        .extracting(InfoItem::getStartDate)
        .containsExactly("17/09/2022");
    Assertions.assertThat(aemResponse.getImportantInfo().getInfoItems())
        .hasSize(1)
        .extracting(InfoItem::getEndDate)
        .containsExactly("20/10/2022");
    Assertions.assertThat(aemResponse.getImportantInfo().getInfoItems())
        .hasSize(1)
        .extracting(InfoItem::isHideOnHdp)
        .containsExactly(true);
    Assertions.assertThat(aemResponse.getImportantInfo().getInfoItems())
        .hasSize(1)
        .extracting(InfoItem::isHideOnBookingFlow)
        .containsExactly(false);
  }

  @Test
  void getHotelInformationForDistr__ShouldReturnException() {
    //Arrange
    var errorCode= 1;
    var message = "message";
    var debugMess = "debug";
    when(this.contentOutPort.getHotelInformation(any(), eq(false))).thenReturn(mockHotelInformation());
    when(this.ohipOutPort.getHotelInfo(any())).thenThrow(
        new OhipException(message, debugMess, new Exception(), errorCode));

    ReflectionTestUtils.setField(contentInPort, "hotelInformationExtendedMapper",
        Mappers.getMapper(HotelInformationExtendedMapper.class));
    HotelInformationRequest hotelInformationDto = createHotelInformationDto();
    hotelInformationDto.setChannel("DISTR");

    //Act
    var actual =
        assertThrows(OhipException.class,
            () -> contentInPort.getHotelInformation(hotelInformationDto));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(message));
    assertThat(actual.getMessage(), is(debugMess));
    assertThat(actual.getErrorCode(), is(errorCode));
  }

  @Test
  void getHubHotelInformation__ShouldReturnOk() {
    //Arrange
    when(this.contentOutPort.getHotelInformation(any(), eq(false))).thenReturn(mockHubHotelInformation());

    ReflectionTestUtils.setField(contentInPort, "hotelInformationExtendedMapper",
        Mappers.getMapper(HotelInformationExtendedMapper.class));

    //Act
    final var aemResponse = contentInPort.getHotelInformation(createHotelInformationDto());

    //Assert
    assertThat(aemResponse, notNullValue());
    assertEquals("Test Hotel", aemResponse.getName());
    assertEquals("HUB", aemResponse.getBrand());
    assertEquals("booking-business-hub",
        aemResponse.getBookingFlow().getBookingFlowItems().get(0).getBookingIdBB());

  }

  @Test
  void getHotelsInformation__ShouldReturnOk() {
    //Arrange
    when(this.contentOutPort.getHotelsInformation(any(), anyBoolean())).thenReturn(mockHotelsInformation());

    //Act
    final var aemResponse = contentInPort.getHotelsInformation(createHotelsInformationDto());

    //Assert
    assertThat(aemResponse, notNullValue());
    assertThat(aemResponse, hasSize(1));
    assertEquals("Test Hotel", aemResponse.get(0).getName());
    assertEquals("PI", aemResponse.get(0).getBrand());
    assertEquals("testurl/image/logo-url.jpg", aemResponse.get(0).getRestaurant().getLogoSrc());
    assertEquals("food-at-the-social.jpg", aemResponse.get(0).getRestaurant().getBookingCardImage());
    assertEquals("Food at the Social", aemResponse.get(0).getRestaurant().getBookingCardTitle());
    assertEquals("Delicious food served all day long.", aemResponse.get(0).getRestaurant()
            .getBookingCardDescription());
    assertEquals("/content/dam/BackgroundImageRestaurants.png", aemResponse.get(0).getRestaurant()
            .getBookingCardBackgroundImage());
    assertEquals("Book a table", aemResponse.get(0).getBookRestaurantCta().getBookingCardCtaText());
    assertEquals("/gb/en/restaurants/birmingham-necairport/book", aemResponse.get(0).getBookRestaurantCta()
            .getBookingCardCtaLink());
    assertEquals("sidebar-card-social", aemResponse.get(0).getBookRestaurantCta().getBookingCardTrackingId());
    assertEquals("Important Information", aemResponse.get(0).getImportantInfo().getTitle());
    assertEquals("headerTest", aemResponse.get(0).getAccessibilityInfo().getHeader());
    assertEquals("All accessible rooms at this hotel are double rooms.",
        aemResponse.get(0).getAccessibilityInfo().getText());
    assertEquals("linkTextTest", aemResponse.get(0).getAccessibilityInfo().getLinkText());
    assertEquals("3527934510", aemResponse.get(0).getAccessibilityInfo().getPhoneNumber());

    Assertions.assertThat(aemResponse.get(0).getRoomConfiguration().getTabItems())
        .hasSize(1)
        .extracting(TabItem::getRoomName)
        .containsOnlyOnce("RoomTitle");
    Assertions.assertThat(aemResponse.get(0).getRoomConfiguration().getTabGroups())
        .hasSize(1)
        .extracting(TabGroup::getGroupName)
        .containsOnlyOnce("GroupTitle");
    Assertions.assertThat(aemResponse.get(0).getRestaurant().getMenus())
        .hasSize(2)
        .extracting(Menu::getMenuSrc)
        .containsExactlyInAnyOrder("/test/Global Dinner.pdf", "/test/Global Breakfast.pdf");
    Assertions.assertThat(aemResponse.get(0).getImportantInfo().getInfoItems())
        .hasSize(1)
        .extracting(InfoItem::getText)
        .containsExactly("There is no air conditioning at this hotel.");
    Assertions.assertThat(aemResponse.get(0).getImportantInfo().getInfoItems())
        .hasSize(1)
        .extracting(InfoItem::getPriority)
        .containsExactly("1");
    Assertions.assertThat(aemResponse.get(0).getImportantInfo().getInfoItems())
        .hasSize(1)
        .extracting(InfoItem::getStartDate)
        .containsExactly("17/09/2022");
    Assertions.assertThat(aemResponse.get(0).getImportantInfo().getInfoItems())
        .hasSize(1)
        .extracting(InfoItem::getEndDate)
        .containsExactly("20/10/2022");
    Assertions.assertThat(aemResponse.get(0).getImportantInfo().getInfoItems())
        .hasSize(1)
        .extracting(InfoItem::isHideOnHdp)
        .containsExactly(true);
    Assertions.assertThat(aemResponse.get(0).getImportantInfo().getInfoItems())
        .hasSize(1)
        .extracting(InfoItem::isHideOnBookingFlow)
        .containsExactly(false);
  }

  @Test
  void getSearchResultsData__ShouldReturnOk() {
    //Arrange
    LocalizationRequest localizationRequest = new LocalizationRequest("gb", "en");
    when(this.contentOutPort.getSearchResultsData(localizationRequest)).thenReturn(
        mockSearchResultsData());

    //Act
    final var aemResponse = contentInPort.getSearchResultsData(localizationRequest);

    //Assert
    assertThat(aemResponse, notNullValue());
    assertEquals("Sold out", aemResponse.getContent().getResults().getResult().getFullyBooked());

    assertEquals("Last few rooms",
        aemResponse.getContent().getResults().getResult().getAvailabilityWarning());

    assertEquals("Open soon", aemResponse.getContent().getResults().getResult().getOpeningSoon());

    assertEquals("Opening on", aemResponse.getContent().getResults().getResult().getOpeningOn());

    assertEquals("miles",
        aemResponse.getContent().getResults().getResult().getDistanceUnitPlural());

    assertEquals("from your search",
        aemResponse.getContent().getResults().getResult().getFromLocation());

    assertEquals("From", aemResponse.getContent().getResults().getResult().getPriceFrom());

    assertEquals("View details",
        aemResponse.getContent().getResults().getResult().getViewDetails());

    assertEquals("Premier Plus", aemResponse.getContent().getResults().getResult().getFacilities()
        .getPremierPlusRoom());

    assertEquals("Premier Plus rooms",
        aemResponse.getContent().getResults().getResult().getFacilities().getStandardExtraRoom());

    assertEquals("Standard Extra rooms",
        aemResponse.getContent().getResults().getResult().getFacilities().getBusinessRoom());

    assertEquals("Parking",
        aemResponse.getContent().getResults().getResult().getFacilities().getParking());

    assertEquals("No parking available",
        aemResponse.getContent().getResults().getResult().getFacilities().getNoParking());

    assertEquals("Free parking",
        aemResponse.getContent().getResults().getResult().getFacilities().getFreeParking());

    assertEquals("TestHubBadge",
        aemResponse.getContent().getGlobal().getBrand().getHubBadge());

    assertEquals("en-gb", aemResponse.getContent().getSeo().getHreflangs().get(0).getHreflang());
    assertEquals("https://www.premierinn.com/gb/en/home.html",
            aemResponse.getContent().getSeo().getHreflangs().get(0).getHref());
    Assertions.assertThat(aemResponse.getContent().getSeo().getHreflangs())
            .hasSize(2);
  }

  @Test
  void getSearchResultsData__ShouldNotReturnOk() {
    //Arrange
    LocalizationRequest localizationRequest = new LocalizationRequest("gb", "en");
    when(this.contentOutPort.getSearchResultsData(localizationRequest)).thenReturn(
        mockSearchResultsData());

    //Act
    final var aemResponse = contentInPort.getSearchResultsData(localizationRequest);

    //Assert
    assertThat(aemResponse, notNullValue());
    assertEquals("Sold out", aemResponse.getContent().getResults().getResult().getFullyBooked());
  }

  @Test
  void getIndexHeaderData__ShouldReturnOk() {
    //Arrange
    when(this.contentOutPort.getIndexHeaderData(any())).thenReturn(mockIndexHeaderData(Boolean.TRUE));

    //Act
    final var aemResponse = contentInPort.getIndexHeaderData(createIndexHeaderData());

    //Assert
    assertThat(aemResponse, notNullValue());
    assertEquals("/content/dam/pi/websites/desktop/icons/brand/pi-logo-rest-easy.svg",
        aemResponse.getContent().getHeader().getImage());
    Assertions.assertThat(aemResponse.getContent().getCountries())
        .hasSize(2)
        .extracting(Country::getLanguage)
        .containsExactlyInAnyOrder("English", "German");
    Assertions.assertThat(aemResponse.getContent().getCountries())
        .hasSize(2)
        .extracting(Country::getFlagUrl)
        .containsExactlyInAnyOrder("/etc/clientlibs/pi-header/resources/images/british-round.svg",
            "/etc/clientlibs/pi-header/resources/images/germany-round.svg");
    Assertions.assertThat(aemResponse.getContent().getSubNav())
        .hasSize(1)
        .extracting(SubNav::getTitle)
        .containsExactly("Short breaks");
    Assertions.assertThat(aemResponse.getContent().getSubNav())
        .hasSize(1)
        .extracting(SubNav::getNavOptions)
        .containsExactly(getNavOptions());
    assertEquals("summer-sale", aemResponse.getContent().getGlobal().getPromotions().get(0).getPage());
    assertEquals("15010601", aemResponse.getContent().getGlobal().getOffers().get(0).getCorpId());
    assertEquals("FCDNLR30",
        aemResponse.getContent().getGlobal().getOffers().get(0).getRatePlanCode());

    test_contentAuthentication(aemResponse);
    test_configAuthentication(aemResponse);
  }

  @Test
  void getSearchRules__ShouldReturnOk() {
    //Arrange
    when(this.contentOutPort.getSearchRules(any())).thenReturn(mockSearchRules());

    //Act
    final var aemResponse = contentInPort.getSearchRules(createGlobalConfigRequest());

    //Assert
    assertThat(aemResponse, notNullValue());
    makeAssertions(aemResponse);
  }

  @Test
  void getSearchRules__Travel_Industry_Rate_ShouldReturnOk() {
    //Arrange
    when(this.contentOutPort.getSearchRules(any())).thenReturn(mockTravelIndustryRateSearchRules());

    //Act
    var searchRulesRequest = createGlobalConfigRequest();
    searchRulesRequest.setChannelId("FCDNLR30");
    final var aemResponse = contentInPort.getSearchRules(searchRulesRequest);

    //Assert
    assertThat(aemResponse, notNullValue());
    Assertions.assertThat(aemResponse.getMaxNights()).isEqualTo(9);
    Assertions.assertThat(aemResponse.getMaxRooms()).isEqualTo(5);
    Assertions.assertThat(aemResponse.getMaxRoomsAmend()).isEqualTo(2);
    Assertions.assertThat(aemResponse.getMaxArrivalDate()).isEqualTo(365);
  }

  @Test
  void getRoomClassConfig__ShouldReturnOk() {
    //Arrange
    when(this.contentOutPort.getRoomClassConfig(any())).thenReturn(mockRoomClassConfig());

    //Act
    final var response = contentInPort.getRoomClassConfig(createGlobalConfigRequest());

    //Assert
    assertThat(response, notNullValue());
    final var roomClassConfig = response.getRoomClassConfig();
    makeAssertions(roomClassConfig);
  }

  @Test
  void getGlobalConfig__ShouldReturnOk() {
    //Arrange
    when(this.contentOutPort.getGlobalConfig(any())).thenReturn(mockGlobalConfig());

    //Act
    final var aemResponse = contentInPort.getGlobalConfig(createGlobalConfigRequest());

    //Assert
    assertThat(aemResponse, notNullValue());
    makeAssertions(aemResponse.getMaxRoomsLim());
    makeAssertions(aemResponse.getRoomClassConfig());
    makeAssertions(aemResponse.getPromotionsConfig());
  }

  @Test
  void getDlpInformation__ShouldReturnOk() {
    //Arrange
    when(this.contentOutPort.getDlpInformation(any())).thenReturn(mockDlpInformation());

    //Act
    final var response = contentInPort.getDlpInformation(createDlpInformationRequest());

    //Assert
    assertThat(response, notNullValue());
    final var hotels = response.getHotels();
    assertThat(hotels, notNullValue());
    assertThat(hotels, hasSize(2));
    final var hotel1 = hotels.get(0);
    final var hotel2 = hotels.get(1);
    assertThat(hotel1.getOrder(), is(1));
    assertThat(hotel1.getCode(), is("ON"));
    assertThat(hotel2.getOrder(), is(2));
    assertThat(hotel2.getCode(), is("TW"));
  }

  private void test_contentAuthentication(IndexHeaderData aemResponse) {
    assertEquals("Log into your Premier Inn account",
        aemResponse.getContent().getAuthentication().getLogin().getLeisure().getFormLabel());
    assertEquals("Email address",
        aemResponse.getContent().getAuthentication().getLogin().getLeisure().getEmailPlaceholder());
    assertEquals("Log in",
        aemResponse.getContent().getAuthentication().getLogin().getLeisure().getLoginButton());
    assertEquals("Sign up to My Premier Inn",
        aemResponse.getContent().getAuthentication().getLogin().getLeisure().getSignupButton());
    assertEquals("My Premier Inn", aemResponse.getContent().getAuthentication().getLogin().getLeisure()
        .getTab());
    assertEquals("Password",
        aemResponse.getContent().getAuthentication().getLogin().getPasswordPlaceholder());
    assertEquals("Forgotten password?",
        aemResponse.getContent().getAuthentication().getLogin().getForgotPassword());
    assertEquals("Don't have an account yet?",
        aemResponse.getContent().getAuthentication().getLogin().getSignupMessage());
    assertEquals("Sign up here",
        aemResponse.getContent().getAuthentication().getLogin().getSignupLink());
    assertEquals("Remember me", aemResponse.getContent().getAuthentication().getLogin().getRememberMeLabel());
    assertEquals("Your email address or password is incorrect",
        aemResponse.getContent().getAuthentication().getLogin().getBadCredentialsError());
    assertEquals("Your session expired, please login again",
        aemResponse.getContent().getAuthentication().getLogin().getSessionExpired());
    assertEquals("Please enter a valid e-mail address",
        aemResponse.getContent().getAuthentication().getLogin().getInvalidEmail());
    assertEquals("We will send you an email with instructions to reset your password",
        aemResponse.getContent().getAuthentication().getForgottenPassword().getLeisure()
            .getFormLabel());
    assertEquals("Email address",
        aemResponse.getContent().getAuthentication().getForgottenPassword().getLeisure()
            .getEmailPlaceholder());
    assertEquals("Reset your password",
        aemResponse.getContent().getAuthentication().getForgottenPassword().getLeisure()
            .getFormTitle());
    assertEquals("Submit",
        aemResponse.getContent().getAuthentication().getForgottenPassword().getLeisure()
            .getSubmitButton());
    assertEquals("Cancel",
        aemResponse.getContent().getAuthentication().getForgottenPassword().getCancel());
    assertEquals("We don’t recognise this email address",
        aemResponse.getContent().getAuthentication().getForgottenPassword().getNotRegisteredError());
    assertEquals("Email sent",
        aemResponse.getContent().getAuthentication().getForgottenPassword().getEmailSentHeader());
    assertEquals("Looks like something went wrong, please try again later",
        aemResponse.getContent().getAuthentication().getForgottenPassword().getGenericError());
    assertEquals("We've sent you an email with instructions to reset your password",
        aemResponse.getContent().getAuthentication().getForgottenPassword().getEmailSentMessage());
  }

  private void test_configAuthentication(IndexHeaderData aemResponse) {
    Assertions.assertThat(aemResponse.getConfig().getAuthentication().getAccountLinks())
        .hasSize(1)
        .extracting(AccountLink::getTitle)
        .containsExactly("Account settings");
    Assertions.assertThat(aemResponse.getConfig().getAuthentication().getAccountLinks())
        .hasSize(1)
        .extracting(AccountLink::getUrl)
        .containsExactly("/gb/en/business-booker/accountTestURL");
    Assertions.assertThat(
            aemResponse.getConfig().getAuthentication().getBusiness().getBusinessAccountLinks())
        .hasSize(1)
        .extracting(BusinessAccountLink::getTitle)
        .containsExactly("Company management");
    Assertions.assertThat(
            aemResponse.getConfig().getAuthentication().getBusiness().getBusinessAccountLinks())
        .hasSize(1)
        .extracting(BusinessAccountLink::getSubMenuLinks)
        .containsExactly(getSubMenuLinks());
    assertEquals("Business login",
        aemResponse.getConfig().getAuthentication().getBusiness().getBusinessLogin());
    assertEquals("Travel for business",
        aemResponse.getConfig().getAuthentication().getBusiness().getTravelForBusiness());
    assertEquals("Business domain",
        aemResponse.getConfig().getAuthentication().getBusiness().getBusinessDomain());
    assertEquals(true,
            aemResponse.getConfig().getFeatures().getAnnouncement());
    assertEquals("Log out",
        aemResponse.getContent().getAuthentication().getLogoutButton());
  }

  @Test
  void getIndexHeaderData__ShouldReturnAnnouncementText() {
    //Arrange
    when(this.contentOutPort.getIndexHeaderData(any())).thenReturn(mockIndexHeaderData(Boolean.TRUE));

    //Act
    final var aemResponse = contentInPort.getIndexHeaderData(createIndexHeaderData());

    //Assert
    assertEquals("info", aemResponse.getContent().getAnnouncement().getType());
    assertEquals("Test Announcement", aemResponse.getContent().getAnnouncement().getText());
  }

  @Test
  void getIndexHeaderData__ShouldNotReturnAnnouncementText() {
    //Arrange
    when(this.contentOutPort.getIndexHeaderData(any())).thenReturn(mockIndexHeaderData(Boolean.FALSE));

    //Act
    final var aemResponse = contentInPort.getIndexHeaderData(createIndexHeaderData());

    //Assert
    assertNull(aemResponse.getContent().getAnnouncement().getText());
  }

  @Test
  void updateHotelsFacilitiesCache__ShouldReturnOk() {
    //Arrange
    when(this.contentOutPort.getAllHotelFacilityFilters()).thenReturn(mockHotelFacilityFilters());
    when(this.contentOutPort.getAllHotelsInformation("gb", "en")).thenReturn(
        mockAllHotelsInformation());
    when(this.contentOutPort.getHotelsWithFacilityFilter(any(), any())).thenReturn(
        mockHotelIdsFacilityFiltered());

    //Act
    assertDoesNotThrow(() -> contentInPort.updateHotelsFacilitiesCache());

    //Assert

  }

  private IndexHeaderDataRequest createIndexHeaderData() {
    return IndexHeaderDataRequest.builder()
        .country("gb")
        .language("en")
        .build();
  }

  private GlobalConfigRequest createGlobalConfigRequest() {
    return GlobalConfigRequest.builder()
        .country("gb")
        .language("en")
        .channelId("DISTR")
        .brand("PI")
        .build();
  }

  private DlpInformationRequest createDlpInformationRequest() {
    return DlpInformationRequest.builder()
        .country("gb")
        .language("en")
        .dlpPath("/england/bedfordshire/luton")
        .build();
  }

  @ParameterizedTest
  @CsvSource({"false", "true"})
  void updateHotelsOpeningSoonCache__ShouldReturnOk(boolean isOpeningSoonCached) {
    //Arrange
    when(this.contentOutPort.getAllHotelsInformation("gb", "en")).thenReturn(
        mockAllHotelsInformation());
    when(this.contentOutPort.getHotelsOpeningSoon(any())).thenReturn(mockHotelIdsOpeningSoon());
    if (isOpeningSoonCached) {
      when(this.cacheManager.getCache(any())).thenReturn(Mockito.mock(Cache.class));
    }

    //Act
    assertDoesNotThrow(() -> contentInPort.updateHotelsOpeningSoonCache());

    //Assert
    verify(contentOutPort, times(1)).getHotelsOpeningSoon(mockAllHotelsInformation());
  }

  @ParameterizedTest
  @CsvSource({"false", "true"})
  void getAllHotelsShortInformation__ShouldReturnOk() {

    //Arrange
    when(this.contentOutPort.getAllHotelsShortInformation(COUNTRY_GB, LANGUAGE_EN)).thenReturn(
        mockAllShortHotelsInformation());

    //Act
    assertDoesNotThrow(() -> contentInPort.getAllHotelsShortInformation(COUNTRY_GB, LANGUAGE_EN));

    //Assert
    verify(contentOutPort, times(1)).getAllHotelsShortInformation(COUNTRY_GB, LANGUAGE_EN);
  }

  @Test
  void getHomepageApps__ShouldReturnOk() {
    //Arrange
    var req = mock_HomepageApps();
    when(this.contentOutPort.getAppsHomepage(any())).thenReturn(req);

    //Act
    final var aemResponse = contentInPort.getAppsHomepage(createHomepageAppsRequest());

    //Assert
    assertNotNull(aemResponse);
    assertEquals(req.getLogo(), aemResponse.getLogo());
    assertEquals(req.getHeading(), aemResponse.getHeading());
    assertEquals(req.getDestinationCards(), aemResponse.getDestinationCards());
    assertEquals(req.getPromoCards(), aemResponse.getPromoCards());
    assertEquals(req.getContentCards(), aemResponse.getContentCards());
    assertEquals(req.getNotification(), aemResponse.getNotification());
  }

  private List<NavOption> getNavOptions() {
    var navOption = NavOption.builder()
        .title("City breaks")
        .url("/gb/en/short-breaks/city-breaks.html?INTCMP=topNav")
        .build();

    return List.of(navOption);
  }

  private List<SubNav> getSubNav() {
    var subNav = SubNav.builder()
        .title("Short breaks")
        .navOptions(getNavOptions())
        .build();
    return List.of(subNav);
  }

  private Country getCountry(String language, String flagUrl) {
    return Country.builder()
        .language(language)
        .flagUrl(flagUrl)
        .build();
  }

  private ForgottenPassword getForgottenPassword() {
    var leisure = Leisure.builder()
        .formLabel("We will send you an email with instructions to reset your password")
        .emailPlaceholder("Email address")
        .formTitle("Reset your password")
        .submitButton("Submit")
        .build();

    return ForgottenPassword.builder()
        .leisure(leisure)
        .cancel("Cancel")
        .notRegisteredError("We don’t recognise this email address")
        .emailSentHeader("Email sent")
        .genericError("Looks like something went wrong, please try again later")
        .emailSentMessage("We've sent you an email with instructions to reset your password")
        .build();
  }

  private Login getLogin() {
    var leisure = Leisure.builder()
        .formLabel("Log into your Premier Inn account")
        .emailPlaceholder("Email address")
        .loginButton("Log in")
        .signupButton("Sign up to My Premier Inn")
        .tab("My Premier Inn")
        .build();

    return Login.builder()
        .leisure(leisure)
        .passwordPlaceholder("Password")
        .forgotPassword("Forgotten password?")
        .signupMessage("Don't have an account yet?")
        .signupLink("Sign up here")
        .rememberMeLabel("Remember me")
        .badCredentialsError("Your email address or password is incorrect")
        .sessionExpired("Your session expired, please login again")
        .invalidEmail("Please enter a valid e-mail address")
        .build();
  }

  private Header getHeader() {
    return Header.builder()
        .image("/content/dam/pi/websites/desktop/icons/brand/pi-logo-rest-easy.svg")
        .build();
  }

  private IndexHeaderData mockIndexHeaderData(Boolean showAnnouncement) {
    var countryEn = getCountry("English",
        "/etc/clientlibs/pi-header/resources/images/british-round.svg");
    var countryDe = getCountry("German",
        "/etc/clientlibs/pi-header/resources/images/germany-round.svg");

    var content = uk.co.whitbread.content.domain.model.index.header.data.out.Content.builder()
        .countries(List.of(countryEn, countryDe))
        .header(getHeader())
        .subNav(getSubNav())
        .authentication(getAuthentication())
        .announcement(getAnnouncement(showAnnouncement))
        .global(getGlobal())
        .build();

    var config = uk.co.whitbread.content.domain.model.index.header.data.out.Config.builder()
        .authentication(getAuthentication())
        .features(getFeatures())
        .build();

    return IndexHeaderData.builder().content(content).config(config).build();
  }

  private uk.co.whitbread.content.domain.model.index.header.data.out.Global getGlobal() {
    var promotion = Promotions.builder()
            .maxRooms(2)
            .maxRoomsAmend(2)
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

    return uk.co.whitbread.content.domain.model.index.header.data.out.Global.builder()
        .offers(List.of(offer))
        .promotions(List.of(promotion))
        .build();
  }

  private SearchRules mockSearchRules() {
    return SearchRules.builder()
        .maxRooms(4)
        .maxRoomsAmend(2)
        .maxNights(4)
        .maxArrivalDate(364)
        .roomOccupancies(mockAcceptedRoomTypes())
        .build();
  }

  private GlobalConfig mockGlobalConfig() {
    return GlobalConfig.builder()
        .maxRoomsLim(mockSearchRules())
        .roomClassConfig(mockRoomClassConfig().getRoomClassConfig())
        .promotionsConfig(mockPromotionsConfig())
        .build();
  }

  private PromotionsConfig mockPromotionsConfig() {
    return PromotionsConfig.builder()
        .promoItems(List.of(
            PromotionItems.builder()
                .enabled(true)
                .promoCode("ABCDEF")
                .landingPage("promo-page-name")
                .numberOfNights(2)
                .maxRooms(4)
                .bookingStartDate(
                    LocalDate.parse("01/12/2025", DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .bookingEndDate(
                    LocalDate.parse("31/12/2025", DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .stayStartDate(
                    LocalDate.parse("15/02/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .stayEndDate(
                    LocalDate.parse("20/02/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .promoBannerColour("#511E62")
                .promoBannerIcon("/content/dam/global/icons/common/price-tag-orange-16.svg")
                .promoBannerTitle(
                    "<span style='color: #FDB913;'><b>Summer Sale: 20% off</b></span>")
                .promoBannerSubtitle(
                    "<b>Select one of our hotels to see your discount.</b> Prices shown here don’t include your discount yet.")
                .promoInvalidMessage(
                    "<b>No promotions apply to this search.</b> Save 20% off when you stay 2 nights or more until 31 December 2025. <a href='/gb/en/terms/booking-terms-and-conditions.html'>Terms and conditions</a>")
                .promoAmendMessage(
                    "<b>Your booking includes a promotion.</b> Cancel this booking and rebook.")
                .promoExpiredMessage(
                    "<b>This promotion has expired.</b> You can still complete your booking at the regular price.")
                 .promoBannerVisibility(List.of("HDP"))
                .build()
        ))
        .build();
  }

  private SearchRules mockTravelIndustryRateSearchRules() {
    return SearchRules.builder()
        .maxRooms(5)
        .maxRoomsAmend(2)
        .maxNights(9)
        .maxArrivalDate(365)
        .roomOccupancies(mockAcceptedRoomTypes())
        .build();
  }

  private RoomClassConfig mockRoomClassConfig() {
    return RoomClassConfig.builder().roomClassConfig(
        List.of(RoomClassOrder.builder().code("ON").order(1).build(),
            RoomClassOrder.builder().code("TW").order(2).build())).build();
  }

  private DlpInformation mockDlpInformation() {
    return DlpInformation.builder().hotels(
        List.of(Hotel.builder().code("ON").order(1).build(),
            Hotel.builder().code("TW").order(2).build())).build();
  }

  private Authentication getAuthentication() {
    return Authentication.builder()
        .login(getLogin())
        .forgottenPassword(getForgottenPassword())
        .business(getBusiness())
        .accountLinks(getAccountLinks())
        .logoutButton("Log out")
        .accountDescription("Your account")
        .build();
  }

  private Features getFeatures() {
    return Features.builder()
            .announcement(true)
            .build();
  }

  private Announcement getAnnouncement(Boolean showAnnouncement) {
    return Announcement.builder()
        .type("info")
        .text(Boolean.TRUE.equals(showAnnouncement) ? "Test Announcement" : null)
        .browserCompatibilityMessage("Test Message")
        .build();
  }

  private List<AccountLink> getAccountLinks() {
    var accountLink = AccountLink.builder()
        .title("Account settings")
        .url("/gb/en/business-booker/accountTestURL")
        .build();

    return List.of(accountLink);
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

  private HotelInformationRequest createHotelInformationDto() {
    return HotelInformationRequest.builder().hotelId("TESTTT").country("en").language("en").build();
  }

  private HotelsInformationRequest createHotelsInformationDto() {
    return HotelsInformationRequest.builder().hotelIds(List.of("TESTTT")).country("gb")
        .language("en").build();
  }

  private RoomConfiguration mockRoomConfiguration() {
    return RoomConfiguration.builder().tabItems(Collections.singletonList(mockTabItem()))
        .tabGroups(Collections.singletonList(mockTabGroup())).build();
  }

  private TabItem mockTabItem() {
    return TabItem.builder().roomName("RoomTitle").roomType("standard")
        .roomDescription("roomConfigurationInfoText").build();
  }

  private TabGroup mockTabGroup() {
    return TabGroup.builder().groupId("double").groupName("GroupTitle").build();
  }

  private HotelInformation mockHotelInformation() {
    return HotelInformation.builder()
        .brand("PI")
        .name("Test Hotel")
        .restaurant(
            Restaurant.builder()
                .logoSrc("testurl/image/logo-url.jpg")
                .name("THYME")
                .description(
                    "Bei Thyme wird eine exklusive Kombination aus traditionellen und modernen Gerichten serviert, " +
                        "qdie frisch für Sie zubereitet werden.")
                .bookingCardImage("food-at-the-social.jpg").bookingCardTitle("Food at the Social")
                .bookingCardDescription("Delicious food served all day long.")
                .bookingCardBackgroundImage("/content/dam/BackgroundImageRestaurants.png")
                .menus(mockMenus())
                .build()).roomConfiguration(mockRoomConfiguration()).bookingFlow(
            BookingFlow.builder()
                .bookingFlowItems(mockBookingFlowItems())
                .targetBookingFlowItems(mockTargetBookingFlowItems()).build())
        .bookRestaurantCta(
            BookRestaurantCta.builder()
                .bookingCardCtaText("Book a table")
                .bookingCardCtaLink("/gb/en/restaurants/birmingham-necairport/book")
                .bookingCardTrackingId("sidebar-card-social")
                .build())
        .importantInfo(mockImportantInfo())
        .accessibilityInfo(mockAccessibilityInfo())
        .paymentCodeTypes(List.of(AcceptedCreditCard.builder()
                .code("AC")
                .code3cp("MC")
                .codeOpera("MC")
                .feeAmount("")
                .feeCurrency("")
                .listOrder("3")
                .name("Mastercard Credit")
                .paymentOnly(false)
                .schemeLogo("/content/dam/global/booking/Mastercard.jpg").build(),
            AcceptedCreditCard.builder()
                .code("AM")
                .code3cp("AX")
                .codeOpera("AX")
                .feeAmount("")
                .feeCurrency("")
                .listOrder("6")
                .name("American Express")
                .paymentOnly(false)
                .schemeLogo("/content/dam/global/booking/AX.jpg").build()))
        .county("test-county")
        .ancillaryCloseout(
            AncillaryCloseout.builder()
                .noMealsHeading("No meals available")
                .noMealsMessage("Sorry, meals are not available for your selected dates")
                .items(List.of(AncillaryCloseoutItem.builder().serviceCode("SE").build())).build())
            .seo(uk.co.whitbread.content.domain.model.hotel.out.Seo.builder()
                    .hreflangs(List.of(uk.co.whitbread.content.domain.model.hotel.out.Hreflang.builder()
                            .hreflang("de-de")
                            .href("https://www.premierinn.com/de/de/home.html").build())).build())
        .build();
  }

  private HotelInfo mockHotelInfo() {
    return HotelInfo.builder()
        .currencyCode("GBP")
        .languageCode("E")
        .hotelTimeZone("Europe/London")
        .checkInTime("01/01/1970, 15:00")
        .checkOutTime("01/01/1970, 12:00")
        .build();
  }

  private HotelInformation mockHubHotelInformation() {
    return HotelInformation.builder().brand("HUB").name("Test Hotel").restaurant(
            Restaurant.builder()
                .bookingCardImage("food-at-the-social.jpg")
                .bookingCardTitle("Food at the Social")
                .bookingCardDescription("Delicious food served all day long.")
                .bookingCardBackgroundImage("/content/dam/BackgroundImageRestaurants.png")
                .logoSrc("testurl/image/logo-url.jpg")
                .name("THYME")
                .description(
                    "Bei Thyme wird eine exklusive Kombination aus traditionellen und modernen Gerichten serviert, " +
                            "qdie frisch für Sie zubereitet werden.")
                .menus(mockMenus())
                .build()).roomConfiguration(mockRoomConfiguration()).bookingFlow(
            BookingFlow.builder()
                .bookingFlowItems(mockHubBookingFlowItems())
                .targetBookingFlowItems(mockTargetBookingFlowItems()).build())
            .bookRestaurantCta(
                    BookRestaurantCta.builder()
                            .bookingCardCtaText("Book a table")
                            .bookingCardCtaLink("/gb/en/restaurants/birmingham-necairport/book")
                            .bookingCardTrackingId("sidebar-card-social")
                            .build())
        .importantInfo(mockImportantInfo())
        .accessibilityInfo(mockAccessibilityInfo())
        .build();
  }

  private List<HotelInformation> mockHotelsInformation() {
    var hotelInformation1 = HotelInformation.builder().brand("PI").name("Test Hotel").restaurant(
            Restaurant.builder()
                .bookingCardImage("food-at-the-social.jpg")
                .bookingCardTitle("Food at the Social")
                .bookingCardDescription("Delicious food served all day long.")
                .bookingCardBackgroundImage("/content/dam/BackgroundImageRestaurants.png")
                .logoSrc("testurl/image/logo-url.jpg")
                .name("THYME")
                .description(
                    "Bei Thyme wird eine exklusive Kombination aus traditionellen und modernen Gerichten serviert, " +
                        "die frisch für Sie zubereitet werden.")
                .menus(mockMenus())
                .build()).roomConfiguration(mockRoomConfiguration()).bookingFlow(
            BookingFlow.builder()
                .bookingFlowItems(mockBookingFlowItems())
                .targetBookingFlowItems(mockTargetBookingFlowItems()).build())
            .bookRestaurantCta(
                    BookRestaurantCta.builder()
                            .bookingCardCtaText("Book a table")
                            .bookingCardCtaLink("/gb/en/restaurants/birmingham-necairport/book")
                            .bookingCardTrackingId("sidebar-card-social")
                            .build())
        .importantInfo(mockImportantInfo())
        .accessibilityInfo(mockAccessibilityInfo())
        .build();
    return List.of(hotelInformation1);
  }

  private List<Menu> mockMenus() {
    return List.of(
        Menu.builder()
            .name("Breakfast")
            .menuSrc("/test/Global Breakfast.pdf")
            .description("<p><b>Sie kommen zu uns zum Abendessen?</b>")
            .menuLabel("Unsere Speisekarte")
            .build(),
        Menu.builder()
            .name("Dinner")
            .menuSrc("/test/Global Dinner.pdf")
            .description("<p><b>Für unsere Kleinen</b>")
            .menuLabel("Unser Kindermenü")
            .build());
  }

  private List<BookingFlowItem> mockBookingFlowItems() {
    return List.of(
        BookingFlowItem.builder()
            .rateCode("A")
            .rateCategory("A")
            .bookingFlowPath("https://secure2.premierinn.com/de/de/booking-a1/extras.html")
            .bookingId("booking-a1")
            .bookingIdBB("booking-business")
            .build(),
        BookingFlowItem.builder()
            .rateCode("G")
            .rateCategory("G")
            .bookingFlowPath("https://secure2.premierinn.com/de/de/booking-a1/extras.html")
            .bookingId("booking-a1")
            .bookingIdBB("booking-business")
            .build(),
        BookingFlowItem.builder()
            .rateCode("Q")
            .rateCategory("Q")
            .bookingFlowPath("https://secure2.premierinn.com/de/de/booking-a1/extras.html")
            .bookingId("booking-a1")
            .bookingIdBB("booking-business")
            .build());
  }

  private List<BookingFlowItem> mockHubBookingFlowItems() {
    return List.of(
        BookingFlowItem.builder()
            .rateCode("A")
            .rateCategory("A")
            .bookingFlowPath("https://secure2.premierinn.com/de/de/booking-a1/extras.html")
            .bookingId("booking-a1")
            .bookingIdBB("booking-business-hub")
            .build(),
        BookingFlowItem.builder()
            .rateCode("G")
            .rateCategory("G")
            .bookingFlowPath("https://secure2.premierinn.com/de/de/booking-a1/extras.html")
            .bookingId("booking-a1")
            .bookingIdBB("booking-business-hub")
            .build(),
        BookingFlowItem.builder()
            .rateCode("Q")
            .rateCategory("Q")
            .bookingFlowPath("https://secure2.premierinn.com/de/de/booking-a1/extras.html")
            .bookingId("booking-a1")
            .bookingIdBB("booking-business-hub")
            .build());
  }

  private List<TargetBookingFlowItem> mockTargetBookingFlowItems() {
    return List.of(
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

  private SearchResultsData mockSearchResultsData() {
    return new SearchResultsData(mockSearchResultsDataContent(), mockConfig());
  }

  private Content mockSearchResultsDataContent() {
    return Content.builder()
        .results(mockResults())
        .global(mockSearchResultsDataGlobal())
            .seo(mockSeo()).build();
  }

  private List<AcceptedRoomTypes> mockAcceptedRoomTypes() {
    AcceptedRoomTypes acceptedRoomTypes = AcceptedRoomTypes.builder()
        .acceptedRoomTypes(List.of("DIS", "FAM"))
        .adultsNumber(2)
        .childrenNumber(0)
        .build();
    return List.of(acceptedRoomTypes);
  }

  private Global mockSearchResultsDataGlobal() {
    return Global.builder().brand(mockBrand()).build();
  }

  private Brand mockBrand() {
    return Brand.builder().hubBadge("TestHubBadge").build();
  }

  private Results mockResults() {

    return Results.builder()
        .result(mockResult())
        .build();
  }

  private Result mockResult() {
    return Result.builder()
        .fullyBooked("Sold out")
        .availabilityWarning("Last few rooms")
        .openingSoon("Open soon")
        .openingOn("Opening on")
        .distanceUnitPlural("miles")
        .fromLocation("from your search")
        .priceFrom("From")
        .viewDetails("View details")
        .facilities(mockFacilities())
        .build();
  }

  private Seo mockSeo() {
    Hreflang hreflang1 = new Hreflang("en-gb", "https://www.premierinn.com/gb/en/home.html");
    Hreflang hreflang2 = new Hreflang("de-de", "https://www.premierinn.com/de/de/home.html");

    return Seo.builder()
            .pageDescription("From booking to bed, we’re here to help you rest easy.")
            .pageTitle("Search results | Premier Inn")
            .cardImageUrl("/content/dam/pi/websites/facebook-twitter/premierinn-ogimage.jpg")
            .hreflangs(List.of(hreflang1, hreflang2))
            .build();
  }

  private Facilities mockFacilities() {
    return Facilities.builder()
        .premierPlusRoom("Premier Plus")
        .standardExtraRoom("Premier Plus rooms")
        .businessRoom("Standard Extra rooms")
        .parking("Parking")
        .noParking("No parking available")
        .freeParking("Free parking")
        .build();
  }

  private Config mockConfig() {
    return Config.builder().api(mockApi()).build();
  }

  private Api mockApi() {
    return Api.builder().initialPageSize("40")
        .radius("50")
        .lazyLoadPageSize("10")
        .build();
  }

  private ImportantInfo mockImportantInfo() {
    return ImportantInfo.builder()
        .title("Important Information").infoItems(mockInfoItem()).build();
  }

  private List<InfoItem> mockInfoItem() {
    InfoItem infoItem = InfoItem.builder()
        .text("There is no air conditioning at this hotel.")
        .htmlText("There is no air conditioning at this hotel. <a href=\"/gb/en/faq/our-rooms.html\">Learn more about our rooms</a>")
        .priority("1")
        .startDate("17/09/2022")
        .endDate("20/10/2022")
        .hideOnHdp(true)
        .hideOnBookingFlow(false)
        .build();

    return List.of(infoItem);
  }

  private AccessibilityInfo mockAccessibilityInfo() {
    return AccessibilityInfo.builder()
        .header("headerTest")
        .text("All accessible rooms at this hotel are double rooms.")
        .linkText("linkTextTest")
        .phoneNumber("3527934510")
        .build();
  }

  private List<String> mockHotelFacilityFilters() {
    return List.of("ACO", "LFT", "MEE");
  }

  private HotelsWithFacilityFilterResult mockHotelIdsFacilityFiltered() {
    return new HotelsWithFacilityFilterResult(List.of("LONEUS", "TKINPT", "LONKIN"));
  }

  private HotelsOpeningSoonResult mockHotelIdsOpeningSoon() {
    return new HotelsOpeningSoonResult(List.of("LONFIN", "BERAIR"));
  }

  private List<HotelInformation> mockAllHotelsInformation() {
    var acoFacility = HotelFacility.builder()
        .code("ACO")
        .name("Klimatisierte Zimmer")
        .weight(10)
        .icon("/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/ACO.svg")
        .isVisible(true)
        .build();

    var lftFacility = HotelFacility.builder()
        .code("LFT")
        .name("Fahrstuhl")
        .weight(90)
        .icon("/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/LFT.svg")
        .isVisible(true)
        .build();

    HotelInformation hotelInformation1 = new HotelInformation();
    hotelInformation1.setName("TestName");
    hotelInformation1.setHotelId("LONEUS");
    hotelInformation1.setBrand("PI");
    hotelInformation1.setRestaurant(
        Restaurant.builder().logoSrc("logo-url/test.jpg").name("THYME").build());
    hotelInformation1.setRoomConfiguration(mockRoomConfiguration());
    hotelInformation1.setHotelOpeningDate("20-02-2022");
    hotelInformation1.setImportantInfo(mockImportantInfo());
    hotelInformation1.setAccessibilityInfo(mockAccessibilityInfo());
    hotelInformation1.setHotelFacilities(List.of(acoFacility, lftFacility));

    HotelInformation hotelInformation2 = new HotelInformation();
    hotelInformation2.setName("TestName");
    hotelInformation2.setHotelId("LONKIN");
    hotelInformation2.setBrand("PI");
    hotelInformation2.setRestaurant(
        Restaurant.builder().logoSrc("logo-url/test.jpg").name("THYME").build());
    hotelInformation2.setRoomConfiguration(mockRoomConfiguration());
    hotelInformation2.setBookingFlow(BookingFlow.builder().build());
    hotelInformation2.setHotelOpeningDate("20-02-2022");
    hotelInformation2.setImportantInfo(mockImportantInfo());
    hotelInformation2.setAccessibilityInfo(mockAccessibilityInfo());
    hotelInformation2.setHotelFacilities(List.of(acoFacility, lftFacility));

    return List.of(hotelInformation1, hotelInformation2);
  }

  private ExtrasLabel getExtras() {
    return ExtrasLabel.builder().extrasLabels(List.of(
        Extras.builder().id("HSCKIN").name("Early check-in").order(1).build(),
        Extras.builder().id("HSCOU2").name("Late check-out").order(2).build(),
        Extras.builder().id("DBPROS").name("Bottle of prosecco").order(3).build())).build();
  }

  private static List<HotelShortInformation> mockAllShortHotelsInformation() {
    var acoFacility = HotelShortInformation.builder()
        .code("ACO")
        .title("Klimatisierte Zimmer")
        .brand("PI")
        .hotelPagePath("dummyPageACO")
        .build();

    var lftFacility = HotelShortInformation.builder()
        .code("LFT")
        .title("Fahrstuhl")
        .brand("PI")
        .hotelPagePath("dummyPageLFT")
        .build();

    return List.of(acoFacility, lftFacility);
  }

  private static void makeAssertions(SearchRules searchRules) {
    Assertions.assertThat(searchRules.getMaxNights()).isEqualTo(4);
    Assertions.assertThat(searchRules.getMaxRooms()).isEqualTo(4);
    Assertions.assertThat(searchRules.getMaxRoomsAmend()).isEqualTo(2);
    Assertions.assertThat(searchRules.getMaxArrivalDate()).isEqualTo(364);
    Assertions.assertThat(searchRules.getRoomOccupancies())
        .hasSize(1)
        .extracting(AcceptedRoomTypes::getAcceptedRoomTypes)
        .containsExactly(List.of("DIS", "FAM"));
    Assertions.assertThat(searchRules.getRoomOccupancies())
        .hasSize(1)
        .extracting(AcceptedRoomTypes::getAdultsNumber)
        .containsExactly(2);
    Assertions.assertThat(searchRules.getRoomOccupancies())
        .hasSize(1)
        .extracting(AcceptedRoomTypes::getChildrenNumber)
        .containsExactly(0);
  }

  private static void makeAssertions(List<RoomClassOrder> roomClassConfig) {
    assertThat(roomClassConfig, notNullValue());
    assertThat(roomClassConfig, hasSize(2));
    final var roomClassOrder1 = roomClassConfig.get(0);
    final var roomClassOrder2 = roomClassConfig.get(1);
    assertThat(roomClassOrder1.getOrder(), is(1));
    assertThat(roomClassOrder1.getCode(), is("ON"));
    assertThat(roomClassOrder2.getOrder(), is(2));
    assertThat(roomClassOrder2.getCode(), is("TW"));
  }

  private static void makeAssertions(PromotionsConfig promotionsConfig) {
    Assertions.assertThat(promotionsConfig.getPromoItems())
        .isNotEmpty()
        .hasSize(1);

    var promo = promotionsConfig.getPromoItems().get(0);

    Assertions.assertThat(promo.isEnabled()).isTrue();
    Assertions.assertThat(promo.getPromoCode()).isEqualTo("ABCDEF");
    Assertions.assertThat(promo.getLandingPage()).isEqualTo("promo-page-name");
    Assertions.assertThat(promo.getNumberOfNights()).isEqualTo(2);
    Assertions.assertThat(promo.getMaxRooms()).isEqualTo(4);

    Assertions.assertThat(promo.getBookingStartDate())
        .isEqualTo(LocalDate.parse("01/12/2025", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    Assertions.assertThat(promo.getBookingEndDate())
        .isEqualTo(LocalDate.parse("31/12/2025", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    Assertions.assertThat(promo.getStayStartDate())
        .isEqualTo(LocalDate.parse("15/02/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    Assertions.assertThat(promo.getStayEndDate())
        .isEqualTo(LocalDate.parse("20/02/2026", DateTimeFormatter.ofPattern("dd/MM/yyyy")));

    Assertions.assertThat(promo.getPromoBannerColour()).isEqualTo("#511E62");
    Assertions.assertThat(promo.getPromoBannerIcon())
        .isEqualTo("/content/dam/global/icons/common/price-tag-orange-16.svg");
    Assertions.assertThat(promo.getPromoBannerTitle())
        .isEqualTo("<span style='color: #FDB913;'><b>Summer Sale: 20% off</b></span>");
    Assertions.assertThat(promo.getPromoBannerSubtitle()).isEqualTo(
        "<b>Select one of our hotels to see your discount.</b> Prices shown here don’t include your discount yet.");
    Assertions.assertThat(promo.getPromoInvalidMessage()).isEqualTo(
        "<b>No promotions apply to this search.</b> Save 20% off when you stay 2 nights or more until 31 December 2025. <a href='/gb/en/terms/booking-terms-and-conditions.html'>Terms and conditions</a>");
    Assertions.assertThat(promo.getPromoExpiredMessage()).isEqualTo(
        "<b>This promotion has expired.</b> You can still complete your booking at the regular price.");
  }

  @Test
  void getPriceFinderGlobalConfig__ShouldReturnOk() {
    //Arrange
    when(this.contentOutPort.getPriceFinderGlobalConfig(any())).thenReturn(mockPriceFinderGlobalConfig());

    //Act
    final var response = contentInPort.getPriceFinderConfig(createPriceFinderGlobalConfigRequest());

    //Assert
    assertThat(response, notNullValue());
    final var  priceFinderConfig = response.getPriceFinderConfig().getPriceFinderViews();
    makeAssertionsForPriceFinderGlobalConfig(priceFinderConfig);
  }

  @Test
  void getPriceFinderGlobalConfig__ShouldReturnEmptyList() {
    //Arrange
    when(this.contentOutPort.getPriceFinderGlobalConfig(any())).thenReturn(mockPriceFinderGlobalConfigEmptyList());

    //Act
    final var response = contentInPort.getPriceFinderConfig(createPriceFinderGlobalConfigRequestPath());

    //Assert
    assertThat(response, notNullValue());
    final var  priceFinderConfig = response.getPriceFinderConfig().getPriceFinderViews();
    makeAssertionsForPriceFinderGlobalConfigEmpty(priceFinderConfig);
  }

  @Test
  void getPriceFinderGlobalConfig__ShouldReturnEmptyListForEmptyCheck() {
    //Arrange
    when(this.contentOutPort.getPriceFinderGlobalConfig(any())).thenReturn(mockPriceFinderGlobalConfigEmptyList());

    //Act
    final var response = contentInPort.getPriceFinderConfig(createPriceFinderGlobalConfigRequestPathEmpty());

    //Assert
    assertThat(response, notNullValue());
    final var  priceFinderConfig = response.getPriceFinderConfig().getPriceFinderViews();
    makeAssertionsForPriceFinderGlobalConfigEmpty(priceFinderConfig);
  }

  @Test
  void getPriceFinderGlobalConfig__ShouldReturnEmptyForNullCheck() {
    //Arrange
    when(this.contentOutPort.getPriceFinderGlobalConfig(any())).thenReturn(mockPriceFinderGlobalConfigEmptyList());

    //Act
    final var response = contentInPort.getPriceFinderConfig(createPriceFinderGlobalConfigRequestPathNull());

    //Assert
    assertThat(response, notNullValue());
    final var  priceFinderConfig = response.getPriceFinderConfig().getPriceFinderViews();
    makeAssertionsForPriceFinderGlobalConfigEmpty(priceFinderConfig);
  }

  private PriceFinderGlobalConfig mockPriceFinderGlobalConfig() {
    HotelCodes hotelCodes1 = HotelCodes.builder().code("LONHOL").order(1).build();
    HotelCodes hotelCodes2 = HotelCodes.builder().code("FRAMTI").order(2).build();

    InfoMessages infoMessages1 = InfoMessages.builder()
            .messageTitle("messageTitle")
            .messageSubtitle("messageSubtitle")
            .messageType("messageType")
            .messageOrder(1).build();
    InfoMessages infoMessages2 = InfoMessages.builder()
            .messageTitle("messageTitle2")
            .messageSubtitle("messageSubtitle2")
            .messageType("messageType2")
            .messageOrder(2).build();
    Locations locations1  = Locations.builder()
            .locationId("ChIJdd4hrwug2EcRmSrV3Vo6llI")
            .locationName("London")
            .locationOrder(30)
            .build();
    Locations locations2 = Locations.builder()
            .locationId("ChIJ2_UmUkxNekgRqmv-BDgUvtk")
            .locationName("Manchester")
            .locationOrder(30)
            .build();
    SeoHreflangs seoHreflangs1 = SeoHreflangs.builder()
            .hreflang("en-gb")
            .href("https://www.hotel.com/gb/en/price-finder/campaign/test")
            .build();
    SeoHreflangs seoHreflangs2 = SeoHreflangs.builder()
            .hreflang("en-gb1")
            .href("https://www.hotel.com/gb/en/price-finder/campaign/test1")
            .build();
    Destinations destinations1 = Destinations.builder()
            .locations(List.of(locations1,locations2)).build();

    PriceFinderViews priceFinderViews1 = PriceFinderViews.builder()
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
            .hotelCodes(List.of(hotelCodes1, hotelCodes2))
            .destinations(List.of(destinations1))
            .infoMessages(List.of(infoMessages1, infoMessages2))
            .seoMetaTitle("seoMetaTitle")
            .seoMetaDescription("seoMetaDescription")
            .seoCardImageUrl("seoCardImageUrl")
            .seoRobots("seoRobots")
            .seoHreflangs(List.of(seoHreflangs1, seoHreflangs2))
            .build();

    HotelCodes hotelCodes3 = HotelCodes.builder().code("LONFOL").order(3).build();
    HotelCodes hotelCodes4 = HotelCodes.builder().code("FTAMTI").order(4).build();
    InfoMessages infoMessages3 = InfoMessages.builder()
            .messageTitle("messageTitle")
            .messageSubtitle("messageSubtitle")
            .messageType("messageType")
            .messageOrder(1).build();
    InfoMessages infoMessages4 = InfoMessages.builder()
            .messageTitle("messageTitle2")
            .messageSubtitle("messageSubtitle2")
            .messageType("messageType2")
            .messageOrder(2).build();
    Locations locations3 = Locations.builder()
            .locationId("ChIJdd4hrwug2EcRmSrV3Vo6llI")
            .locationName("LiverPool")
            .locationOrder(3)
            .build();
    Locations locations4 = Locations.builder()
            .locationId("Ch4IJ2_UmUkxNekgRqmv-BDgUvtk")
            .locationName("Edinburgh")
            .locationOrder(4)
            .build();
    SeoHreflangs seoHreflangs3 = SeoHreflangs.builder()
            .hreflang("en-gb")
            .href("https://www.hotel.com/gb/en/price-finder/campaign/test")
            .build();
    SeoHreflangs seoHreflangs4 = SeoHreflangs.builder()
            .hreflang("en-gb1")
            .href("https://www.hotel.com/gb/en/price-finder/campaign/test1")
            .build();
    Destinations destinations2 = Destinations.builder()
            .locations(List.of(locations3,locations4)).build();

    PriceFinderViews priceFinderViews2 = PriceFinderViews.builder()
            .path("/content/paths/to/content")
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
            .locationId("ChIJdd4hrwug2EcRmSrV3Vo6llI")
            .locationName("London")
            .locationRadius(40.0)
            .hotelCodes(List.of(hotelCodes3, hotelCodes4))
            .destinations(List.of(destinations2))
            .infoMessages(List.of(infoMessages3, infoMessages4))
            .seoMetaTitle("seoMetaTitle3")
            .seoMetaDescription("seoMetaDescription4")
            .seoCardImageUrl("seoCardImageUrl")
            .seoRobots("seoRobots1")
            .seoHreflangs(List.of(seoHreflangs3, seoHreflangs4))
            .build();
    PriceFinderConfig priceFinderConfig = PriceFinderConfig.builder()
            .priceFinderViews(List.of(priceFinderViews1, priceFinderViews2)).build();
    return PriceFinderGlobalConfig.builder().priceFinderConfig(priceFinderConfig).build();
  }

  private static void makeAssertionsForPriceFinderGlobalConfig(List<PriceFinderViews> priceFinderViews) {
    Assertions.assertThat(priceFinderViews.size()).isEqualTo(1);
    Assertions.assertThat(priceFinderViews.get(0).getHotelCodes().get(0).getCode()).isEqualTo("LONFOL");
    Assertions.assertThat(priceFinderViews.get(0).getDestinations().get(0).getLocations()
            .get(0).getLocationId()).isEqualTo("ChIJdd4hrwug2EcRmSrV3Vo6llI");

  }

  private PriceFinderGlobalConfigRequest createPriceFinderGlobalConfigRequest() {
    return PriceFinderGlobalConfigRequest.builder()
            .country("gb")
            .language("en")
            .channelId("DISTR")
            .brand("PI")
            .path("/content/paths/to/content")
            .build();
  }

  private PriceFinderGlobalConfig mockPriceFinderGlobalConfigEmptyList() {
    return PriceFinderGlobalConfig.builder()
            .priceFinderConfig(PriceFinderConfig.builder()
                    .priceFinderViews(Collections.emptyList())
                    .build())
            .build();
  }

  private PriceFinderGlobalConfigRequest createPriceFinderGlobalConfigRequestPath() {
    return PriceFinderGlobalConfigRequest.builder()
            .country("gb")
            .language("en")
            .channelId("DISTR")
            .brand("PI")
            .path("/")
            .build();
  }

  private PriceFinderGlobalConfigRequest createPriceFinderGlobalConfigRequestPathEmpty() {
    return PriceFinderGlobalConfigRequest.builder()
            .country("gb")
            .language("en")
            .channelId("DISTR")
            .brand("PI")
            .path("")
            .build();
  }

  private PriceFinderGlobalConfigRequest createPriceFinderGlobalConfigRequestPathNull() {
    return PriceFinderGlobalConfigRequest.builder()
            .country("gb")
            .language("en")
            .channelId("DISTR")
            .brand("PI")
            .path("null")
            .build();
  }

  private static void makeAssertionsForPriceFinderGlobalConfigEmpty(List<PriceFinderViews> priceFinderViews) {
    Assertions.assertThat(priceFinderViews.size()).isEqualTo(0);
  }
}
