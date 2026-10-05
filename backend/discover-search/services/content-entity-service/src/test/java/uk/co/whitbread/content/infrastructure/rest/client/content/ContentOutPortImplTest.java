package uk.co.whitbread.content.infrastructure.rest.client.content;

import io.getunleash.UnleashContext;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.assertj.core.api.Assertions;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.content.domain.model.ErrorCode;
import uk.co.whitbread.content.domain.model.apps.homepage.in.AppsHomepageRequest;
import uk.co.whitbread.content.domain.model.apps.homepage.out.AppsHomepageResponse;
import uk.co.whitbread.content.domain.model.dlp.in.DlpInformationRequest;
import uk.co.whitbread.content.domain.model.dlp.out.DlpInformation;
import uk.co.whitbread.content.domain.model.dlp.out.Hotel;
import uk.co.whitbread.content.domain.model.feature.FeatureFlag;
import uk.co.whitbread.content.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.content.domain.model.globalconfig.in.GlobalConfigRequest;
import uk.co.whitbread.content.domain.model.globalconfig.in.PromoConfigRequest;
import uk.co.whitbread.content.domain.model.globalconfig.out.GlobalConfig;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromoBox;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionItems;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsConfig;
import uk.co.whitbread.content.domain.model.globalconfig.out.PromotionsInformationResponse;
import uk.co.whitbread.content.domain.model.globalconfig.out.RoomClassConfig;
import uk.co.whitbread.content.domain.model.globalconfig.out.RoomClassOrder;
import uk.co.whitbread.content.domain.model.globalconfig.out.SearchRules;
import uk.co.whitbread.content.domain.model.globalconfig.out.MetaPromoRateMapping;
import uk.co.whitbread.content.domain.model.globalconfig.out.UpsellItemsExtras;
import uk.co.whitbread.content.domain.model.hotel.in.HotelInformationRequest;
import uk.co.whitbread.content.domain.model.hotel.in.HotelsInformationRequest;
import uk.co.whitbread.content.domain.model.hotel.out.Awards;
import uk.co.whitbread.content.domain.model.hotel.out.BookRestaurantCta;
import uk.co.whitbread.content.domain.model.hotel.out.BookingFlow;
import uk.co.whitbread.content.domain.model.hotel.out.BookingFlowItem;
import uk.co.whitbread.content.domain.model.hotel.out.FacilityCloseout;
import uk.co.whitbread.content.domain.model.hotel.out.FacilityCloseoutItem;
import uk.co.whitbread.content.domain.model.hotel.out.Menu;
import uk.co.whitbread.content.domain.model.hotel.out.Restaurant;
import uk.co.whitbread.content.domain.model.hotel.out.Reviews;
import uk.co.whitbread.content.domain.model.hotel.out.SubRatings;
import uk.co.whitbread.content.domain.model.hotel.out.TargetBookingFlowItem;
import uk.co.whitbread.content.domain.model.hotel.out.TripAdvisorReviews;
import uk.co.whitbread.content.domain.model.hotel.out.*;
import uk.co.whitbread.content.domain.model.index.header.data.in.IndexHeaderDataRequest;
import uk.co.whitbread.content.domain.model.index.header.data.in.LocalizationRequest;
import uk.co.whitbread.content.domain.model.index.header.data.out.IndexHeaderData;
import uk.co.whitbread.content.domain.model.labels.in.CategoryEnum;
import uk.co.whitbread.content.domain.model.labels.in.LabelsRequest;
import uk.co.whitbread.content.domain.model.labels.in.MultipleLabelsRequest;
import uk.co.whitbread.content.domain.model.pricefinder.in.PriceFinderGlobalConfigRequest;
import uk.co.whitbread.content.domain.model.pricefinder.out.*;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoBoxStatus;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoKind;
import uk.co.whitbread.content.domain.model.promoconfig.evaluator.PromoEvaluationStrategy;
import uk.co.whitbread.content.infrastructure.config.SrpFiltersConfig;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.ResourceNotFoundException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.mapper.*;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.apps.homepage.in.AppsHomepageRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.apps.homepage.out.AppsHomepageResponseDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.in.DlpInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out.CoordinatesDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out.DlpInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out.HotelDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.AllowedRoomTypesByOccupancy;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.BookingWidgetConfigDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.GlobalConfigDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.GlobalConfigDto.GlobalConfigDtoBuilder;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.PromoBoxDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.PromotionItemsDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.PromotionsConfigDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.RoomClassConfigDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.in.GlobalConfigRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.RoomUpgradeOptionsDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.globalconfig.out.RoomUpgradesDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.AncillaryCloseout;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.AncillaryCloseoutItem;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.CityTax;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.ImportantInfo;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.InfoItem;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.MessagingFlag;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.RoomClassConfiguration;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.TabGroup;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.TabItem;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.*;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.out.HotelInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.AemIndexHeaderDataDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.out.IndexHeaderDataRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.in.PriceFinderGlobalConfigRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.out.PriceFinderConfigDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.out.PriceFinderGlobalConfigDto;
import uk.co.whitbread.content.infrastructure.rest.client.content.aem.adapter.AemClient;
import uk.co.whitbread.content.infrastructure.rest.client.content.aem.adapter.HotelReviewClient;
import uk.co.whitbread.content.infrastructure.rest.client.content.exceptions.ContentException;
import uk.co.whitbread.content.infrastructure.rest.client.content.mapper.LabelsRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.content.mapper.LocalizationRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.content.mapper.MultipleLabelsRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.CategoryEnumDto;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.LabelsRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.LocalizationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.content.model.in.MultipleLabelsRequestDto;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoContext;
import uk.co.whitbread.content.domain.model.promoconfig.promoutils.PromoFlowType;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promobox.PromoBoxResolutionResult;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promobox.resolver.PromoBoxResolver;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.resolver.PromoFlowTypeResolver;
import uk.co.whitbread.content.infrastructure.rest.client.content.promotions.promoconfig.resolver.PromoStrategyRegistry;
import uk.co.whitbread.content.infrastructure.rest.client.snowdrop.SnowdropHotelsRetriever;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out.UpsellItemsExtrasDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.*;

import static java.util.List.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static uk.co.whitbread.content.domain.model.ErrorCode.*;

@ExtendWith(MockitoExtension.class)
class ContentOutPortImplTest {

  private static final String HOTEL_ID_EDIPAR = "EDIPAR";
  private static final String HOTEL_ID_LONEUS = "LONEUS";
  private static final String COUNTRY_GB = "gb";
  private static final String LANGUAGE_EN = "en";
  private static final String CHANNEL_ID = "DISTR";
  private static final String BRAND = "PI";
  private static final String DLP_PATH = "/england/bedfordshire/luton";

  @InjectMocks
  private ContentOutPortImpl contentOutPort;
  @Mock
  private AemClient aemClient;
  @Mock
  private HotelReviewClient hotelReviewClient;
  @Mock
  private LabelsRequestMapper contentRequestMapper;
  @Mock
  private MultipleLabelsRequestMapper multipleLabelsRequestMapper;
  @Mock
  private HotelInformationMapper hotelInformationMapper;
  @Mock
  private HotelInformationRequestMapper hotelInformationRequestMapper;
  @Mock
  private IndexHeaderDataMapper indexHeaderDataMapper;
  @Mock
  private SearchRulesMapper searchRulesMapper;
  @Mock
  private RoomClassConfigMapper roomClassConfigMapper;
  @Mock
  private GlobalConfigRequestMapper globalConfigRequestMapper;
  @Mock
  private AemClientDlpInformationMapper dlpInformationMapper;
  @Mock
  private LocalizationRequestMapper localizationRequestMapper;
  @Mock
  private IndexHeaderDataRequestMapper indexHeaderDataRequestMapper;
  @Mock
  private SrpFiltersConfig srpFiltersConfig;
  @Mock
  private AemClientAppsHomepageMapper homepageAppsMapper;
  @Mock
  private RoomUpgradeOptionsMapper roomUpgradeOptionsMapper;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @Mock
  private SnowdropHotelsRetriever snowdropHotelsRetriever;
  @Mock
  private PromotionsConfigMapper promotionsConfigMapper;
  @Mock
  private PriceFinderGlobalConfigMapper priceFinderGlobalConfigMapper;
  @Mock
  private PromoStrategyRegistry strategyRegistry;
  @Mock
  private PromoFlowTypeResolver flowTypeResolver;
  @Mock
  private PromoEvaluationStrategy promoEvaluationStrategy;
  @Mock
  private PromoBoxResolver promoBoxResolver;
  @Mock
  private UpsellItemsExtrasMapper upsellItemsExtrasMapper;

  @Captor
  ArgumentCaptor<AemHotelInformationDto> hotelInfoDtoCaptor;

  private final Exception exception = new Exception();

  @BeforeEach
  void promoBoxDefaults() {
    lenient()
        .when(promoBoxResolver.resolvePreEvaluation(any()))
        .thenReturn(PromoBoxResolutionResult.nonTerminal());

    lenient()
        .when(promoBoxResolver.resolvePostEvaluation(any(), any()))
        .thenReturn(PromoBoxResolutionResult.terminal(PromoBoxStatus.UNAVAILABLE));
  }

  @Test
  void getContent__ShouldReturnOK() {
    //Arrange
    when(aemClient.getLabels((any(LabelsRequestDto.class)))).thenReturn(mockContentAEMResponse());
    when(contentRequestMapper.toDtoModel(any())).thenReturn(new LabelsRequestDto());

    //Act
    var contentAEMResponse = contentOutPort.getLabels(
        LabelsRequest.builder().country(COUNTRY_GB).language(LANGUAGE_EN)
            .category(CategoryEnum.BOOKING).build());

    //Assert
    assertThat(contentAEMResponse, notNullValue());
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

  @Test
  void getContent_badInput_ShouldReturnException() {
    //Arrange
    var request = new LabelsRequest("null", "null", CategoryEnum.MAIN, null);
    var expectedMessage = String.format(
        "No labels for this getLabels request for country=%s, language=%s, category=%s",
        request.getCountry(), request.getLanguage(), request.getCategory());
    when(aemClient.getLabels(any())).thenReturn(null);
    //Act
    var actual =
        assertThrows(ContentException.class, () -> contentOutPort.getLabels(request));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(),
        is(DIGITAL_MULTIPLE_LABELS_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(DIGITAL_LABEL_EXCEPTION.getCode()));
  }

  @Test
  void getContent__ShouldReturnException() {
    //Arrange
    var request = new LabelsRequest("null", "null", CategoryEnum.MAIN, null);
    var expectedMessage = "Unable to get labels";
    when(aemClient.getLabels(any())).thenThrow(
        new AemResponseException(AEM_ALL_HOTELS_DETAILS_EXCEPTION, "Unable to get labels",
            exception));

    //Act
    var actual =
        assertThrows(AemResponseException.class, () -> contentOutPort.getLabels(request));

    //Assert
    assertException(actual, AEM_ALL_HOTELS_DETAILS_EXCEPTION, expectedMessage);
  }

  @Test
  void getContent_piPreCheckIn_badInput_ShouldReturnException() {
    //Arrange
    var request = new LabelsRequest("null", "null", CategoryEnum.PI_PRE_CHECKIN, null);
    var expectedMessage = String.format(
        "No labels for this getLabels request for country=%s, language=%s, category=%s",
        request.getCountry(), request.getLanguage(), request.getCategory());
    when(aemClient.getLabels(any())).thenReturn(null);

    //Act
    var actual =
        assertThrows(ContentException.class, () -> contentOutPort.getLabels(request));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(DIGITAL_LABEL_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(DIGITAL_LABEL_EXCEPTION.getCode()));
  }

  @Test
  void getContent_piPreCheckIn__ShouldReturnException() {
    //Arrange
    var request = new LabelsRequest("null", "null", CategoryEnum.PI_PRE_CHECKIN, null);
    var expectedMessage = String.format(
        "No labels for this getLabels request for country=%s, language=%s, category=%s",
        request.getCountry(), request.getLanguage(), request.getCategory());
    when(aemClient.getLabels(any())).thenReturn(Map.of());

    //Act
    var actual =
        assertThrows(ContentException.class, () -> contentOutPort.getLabels(request));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(),
        is(DIGITAL_MULTIPLE_LABELS_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(DIGITAL_LABEL_EXCEPTION.getCode()));
  }

  private Map<String, String> mockContentAEMResponse() {
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
  void getHotelsInformation__ShouldReturnOK() {
    //Arrange
    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenReturn(
        mockAemHotelInformationDto());
    when(hotelInformationMapper.toDomainModel(any(), any(), any(), anyBoolean())).thenReturn(
        mockEdiparHotelInformation());
    stubTripadvisorFlag(true);

    //Act
    var hotelInformation = contentOutPort.getHotelsInformation(
        HotelsInformationRequest.builder().hotelIds(List.of(HOTEL_ID_EDIPAR, HOTEL_ID_LONEUS))
            .country(COUNTRY_GB)
            .language(LANGUAGE_EN).build(), true);

    //Assert
    assertThat(hotelInformation, notNullValue());
    assertThat(hotelInformation, hasSize(2));
    assertEquals("TestName", hotelInformation.get(0).getName());
    assertEquals("PI", hotelInformation.get(0).getBrand());
    assertEquals(HOTEL_ID_EDIPAR, hotelInformation.get(0).getHotelId());

    Assertions.assertThat(hotelInformation.get(0).getRoomConfiguration().getTabItems()).hasSize(1)
        .extracting(uk.co.whitbread.content.domain.model.hotel.out.TabItem::getRoomName)
        .containsOnlyOnce("RoomTitle");
    Assertions.assertThat(hotelInformation.get(0).getRoomConfiguration().getTabGroups()).hasSize(1)
        .extracting(uk.co.whitbread.content.domain.model.hotel.out.TabGroup::getGroupName)
        .containsOnlyOnce("GroupTitle");

    test_restaurant(hotelInformation);
    test_importantInfo(hotelInformation);
    test_messagingFlag(hotelInformation);
    test_hotelFlags(hotelInformation);
    test_accessibilityInfo(hotelInformation);

    verify(hotelReviewClient, times(hotelInformation.size())).getTripAdvisorReviews(any(), any());
  }

  private void test_accessibilityInfo(List<HotelInformation> hotelInformation) {
    assertEquals("headerTest", hotelInformation.get(0).getAccessibilityInfo().getHeader());
    assertEquals("All accessible rooms at this hotel are double rooms.",
        hotelInformation.get(0).getAccessibilityInfo().getText());
    assertEquals("linkTextTest", hotelInformation.get(0).getAccessibilityInfo().getLinkText());
    assertEquals("3527934510", hotelInformation.get(0).getAccessibilityInfo().getPhoneNumber());
  }

  private void test_messagingFlag(List<HotelInformation> hotelInformation) {
    assertThat(hotelInformation.get(0).getMessagingFlag(), notNullValue());
    assertEquals("surrounding-area", hotelInformation.get(0).getMessagingFlag().getText());
    assertEquals("surrounding-area", hotelInformation.get(0).getMessagingFlag().getDescription());
    assertEquals("blue", hotelInformation.get(0).getMessagingFlag().getColor());

  }

  private void test_hotelFlags(List<HotelInformation> hotelInformation) {
    assertThat(hotelInformation.get(0).getHotelFlags(), notNullValue());
    assertEquals(true, hotelInformation.get(0).getHotelFlags().getIsEnabled());
    assertThat(hotelInformation.get(0).getHotelFlags().getFlagOverlay(), notNullValue());
    assertEquals("New Premier Plus rooms",
        hotelInformation.get(0).getHotelFlags().getFlagOverlay().getText());
    assertEquals("#D7C3FF",
        hotelInformation.get(0).getHotelFlags().getFlagOverlay().getTextColour());
    assertEquals("#0007",
        hotelInformation.get(0).getHotelFlags().getFlagOverlay().getBackgroundColour());
    assertEquals("/content/dam/icons/resources/icon-overlay.png",
        hotelInformation.get(0).getHotelFlags().getFlagOverlay().getBackgroundImage());
    assertThat(hotelInformation.get(0).getHotelFlags().getFlagBanner(), notNullValue());
    assertEquals("New restaurant now open",
        hotelInformation.get(0).getHotelFlags().getFlagBanner().getText());
    assertEquals("#FFFFFF",
        hotelInformation.get(0).getHotelFlags().getFlagBanner().getTextColour());
    assertEquals("#511E62",
        hotelInformation.get(0).getHotelFlags().getFlagBanner().getBackgroundColour());
    assertEquals("/content/dam/icons/resources/icon-banner.png",
        hotelInformation.get(0).getHotelFlags().getFlagBanner().getBackgroundImage());
  }

  private void test_restaurant(List<HotelInformation> hotelInformation) {
    assertEquals("logo-url/test.jpg", hotelInformation.get(0).getRestaurant().getLogoSrc());
    assertEquals("THYME", hotelInformation.get(0).getRestaurant().getName());
    assertEquals("Breakfast", hotelInformation.get(0).getRestaurant().getMenus().get(0).getName());
    assertEquals("/test/Global Breakfast.pdf",
        hotelInformation.get(0).getRestaurant().getMenus().get(0).getMenuSrc());
    assertEquals("Dinner", hotelInformation.get(0).getRestaurant().getMenus().get(1).getName());
    assertEquals("/test/Global Dinner.pdf",
        hotelInformation.get(0).getRestaurant().getMenus().get(1).getMenuSrc());
  }

  private void test_importantInfo(List<HotelInformation> hotelInformation) {
    assertEquals("Important Information", hotelInformation.get(0).getImportantInfo().getTitle());
    Assertions.assertThat(hotelInformation.get(0).getImportantInfo().getInfoItems()).hasSize(1)
        .extracting(uk.co.whitbread.content.domain.model.hotel.out.InfoItem::getText)
        .containsExactly("There is no air conditioning at this hotel.");
    Assertions.assertThat(hotelInformation.get(0).getImportantInfo().getInfoItems()).hasSize(1)
        .extracting(uk.co.whitbread.content.domain.model.hotel.out.InfoItem::getPriority)
        .containsExactly("1");
    Assertions.assertThat(hotelInformation.get(0).getImportantInfo().getInfoItems()).hasSize(1)
        .extracting(uk.co.whitbread.content.domain.model.hotel.out.InfoItem::getStartDate)
        .containsExactly("17/09/2022");
    Assertions.assertThat(hotelInformation.get(0).getImportantInfo().getInfoItems()).hasSize(1)
        .extracting(uk.co.whitbread.content.domain.model.hotel.out.InfoItem::getEndDate)
        .containsExactly("20/10/2022");
    Assertions.assertThat(hotelInformation.get(0).getImportantInfo().getInfoItems()).hasSize(1)
        .extracting(uk.co.whitbread.content.domain.model.hotel.out.InfoItem::isHideOnHdp)
        .containsExactly(true);
    Assertions.assertThat(hotelInformation.get(0).getImportantInfo().getInfoItems()).hasSize(1)
        .extracting(uk.co.whitbread.content.domain.model.hotel.out.InfoItem::isHideOnBookingFlow)
        .containsExactly(false);
  }

  @Test
  void getHotelsInformation__ShouldReturnResourceException() {
    //Arrange
    var expectedMessage = "Unable to get hotels information.";
    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenThrow(
        new AemResponseException(AEM_HOTELS_INFORMATION_EXCEPTION, expectedMessage, exception));
    stubTripadvisorFlag(false);
    var hotelInfoRequest = createHotelsInfoRequest();
    //Act
    var response = contentOutPort.getHotelsInformation(hotelInfoRequest, false);

    //Assert
    assertThat(response, notNullValue());
  }

  @Test
  void getHotelsInformation__ShouldFilterFacilitiesByOverlappingFacilityCloseout() {
    // Arrange
    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenReturn(mockAemHotelInformationDto());
    when(hotelInformationMapper.toDomainModel(any(), any(), any(), anyBoolean()))
        .thenAnswer(invocation -> mockHotelInformationWithFacilityCloseout());
    stubTripadvisorFlag(false);

    var hotelInfoRequest = HotelsInformationRequest.builder()
        .hotelIds(List.of(HOTEL_ID_EDIPAR))
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .stayStartDate(LocalDate.of(2026, Month.OCTOBER, 2))
        .stayEndDate(LocalDate.of(2026, Month.OCTOBER, 3))
        .build();

    // Act
    var response = contentOutPort.getHotelsInformation(hotelInfoRequest, false);

    // Assert
    assertThat(response, notNullValue());
    assertThat(response, hasSize(1));
    Assertions.assertThat(response.getFirst().getHotelFacilities())
        .extracting(HotelFacility::getCode)
        .containsExactly("LFT");
  }

  @Test
  void getHotelsInformation__ShouldKeepAllFacilities_WhenNoStayDatesProvided() {
    // Arrange
    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenReturn(mockAemHotelInformationDto());
    when(hotelInformationMapper.toDomainModel(any(), any(), any(), anyBoolean()))
        .thenAnswer(ignored -> mockHotelInformationWithFacilityCloseout());
    stubTripadvisorFlag(false);

    var hotelInfoRequest = HotelsInformationRequest.builder()
        .hotelIds(List.of(HOTEL_ID_EDIPAR))
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        // stayStartDate/stayEndDate intentionally omitted — no filtering expected
        .build();

    // Act
    var response = contentOutPort.getHotelsInformation(hotelInfoRequest, false);

    // Assert — all 3 facilities retained
    Assertions.assertThat(response.getFirst().getHotelFacilities())
        .extracting(HotelFacility::getCode)
        .containsExactlyInAnyOrder("COP", "DIN", "LFT");
  }

  @Test
  void getHotelsInformation__ShouldKeepAllFacilities_WhenStayDatesDoNotOverlapCloseout() {
    // Arrange
    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenReturn(mockAemHotelInformationDto());
    when(hotelInformationMapper.toDomainModel(any(), any(), any(), anyBoolean()))
        .thenAnswer(ignored -> mockHotelInformationWithFacilityCloseout());
    stubTripadvisorFlag(false);

    // Stay in Aug 2026 — before all closeouts which start 01/10/2026
    var hotelInfoRequest = HotelsInformationRequest.builder()
        .hotelIds(List.of(HOTEL_ID_EDIPAR))
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .stayStartDate(LocalDate.of(2026, Month.AUGUST, 1))
        .stayEndDate(LocalDate.of(2026, Month.AUGUST, 15))
        .build();

    // Act
    var response = contentOutPort.getHotelsInformation(hotelInfoRequest, false);

    // Assert — no overlap, all facilities kept
    Assertions.assertThat(response.getFirst().getHotelFacilities())
        .extracting(HotelFacility::getCode)
        .containsExactlyInAnyOrder("COP", "DIN", "LFT");
  }

  @Test
  void getHotelsInformation__ShouldFilterFacilities_WhenStayEndDateExactlyOnCloseoutStartDate() {
    // Arrange
    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenReturn(mockAemHotelInformationDto());
    when(hotelInformationMapper.toDomainModel(any(), any(), any(), anyBoolean()))
        .thenAnswer(ignored -> mockHotelInformationWithFacilityCloseout());
    stubTripadvisorFlag(false);

    // stayEndDate == closeout startDate (01/10/2026) — boundary: single shared day
    var hotelInfoRequest = HotelsInformationRequest.builder()
        .hotelIds(List.of(HOTEL_ID_EDIPAR))
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .stayStartDate(LocalDate.of(2026, Month.SEPTEMBER, 29))
        .stayEndDate(LocalDate.of(2026, Month.OCTOBER, 1))
        .build();

    // Act
    var response = contentOutPort.getHotelsInformation(hotelInfoRequest, false);

    // Assert — boundary overlap: COP and DIN removed, LFT kept
    Assertions.assertThat(response.getFirst().getHotelFacilities())
        .extracting(HotelFacility::getCode)
        .containsExactly("LFT");
  }

  @Test
  void getHotelsInformation__ShouldKeepAllFacilities_WhenStayStartDateAfterStayEndDate() {
    // Arrange
    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenReturn(mockAemHotelInformationDto());
    when(hotelInformationMapper.toDomainModel(any(), any(), any(), anyBoolean()))
        .thenAnswer(ignored -> mockHotelInformationWithFacilityCloseout());
    stubTripadvisorFlag(false);

    // Invalid range: start after end — guard returns emptySet
    var hotelInfoRequest = HotelsInformationRequest.builder()
        .hotelIds(List.of(HOTEL_ID_EDIPAR))
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .stayStartDate(LocalDate.of(2026, Month.OCTOBER, 5))
        .stayEndDate(LocalDate.of(2026, Month.OCTOBER, 1))
        .build();

    // Act
    var response = contentOutPort.getHotelsInformation(hotelInfoRequest, false);

    // Assert — invalid range treated as no-op
    Assertions.assertThat(response.getFirst().getHotelFacilities())
        .extracting(HotelFacility::getCode)
        .containsExactlyInAnyOrder("COP", "DIN", "LFT");
  }

  @Test
  void getHotelsInformation__ShouldKeepFacility_WhenCloseoutStartDateIsBlank() {
    // parseFacilityCloseoutDate returns null for blank date → closeout item skipped
    var copFacility = HotelFacility.builder().code("COP").name("Parking").weight(10).isVisible(true).build();
    var closeoutWithBlankStart = FacilityCloseoutItem.builder()
        .startDate("")            // blank → parseFacilityCloseoutDate returns null
        .endDate("31/10/2026")
        .facilityCodes(List.of("COP"))
        .build();
    var hotel = new HotelInformation();
    hotel.setHotelId("LONEUS");
    hotel.setHotelFacilities(new ArrayList<>(List.of(copFacility)));
    hotel.setFacilityCloseout(FacilityCloseout.builder().items(List.of(closeoutWithBlankStart)).build());

    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenReturn(mockAemHotelInformationDto());
    when(hotelInformationMapper.toDomainModel(any(), any(), any(), anyBoolean())).thenReturn(hotel);
    stubTripadvisorFlag(false);

    var hotelInfoRequest = HotelsInformationRequest.builder()
        .hotelIds(List.of(HOTEL_ID_EDIPAR))
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .stayStartDate(LocalDate.of(2026, Month.OCTOBER, 2))
        .stayEndDate(LocalDate.of(2026, Month.OCTOBER, 3))
        .build();

    // Act
    var response = contentOutPort.getHotelsInformation(hotelInfoRequest, false);

    // Assert — blank startDate → item skipped → COP not removed
    Assertions.assertThat(response.getFirst().getHotelFacilities())
        .extracting(HotelFacility::getCode)
        .containsExactly("COP");
  }

  @Test
  void getHotelsInformation__ShouldKeepFacility_WhenCloseoutDateHasInvalidFormat() {
    // parseFacilityCloseoutDate catches DateTimeParseException, logs warn, returns null → item skipped
    var dinFacility = HotelFacility.builder().code("DIN").name("Dinner").weight(20).isVisible(true).build();
    var closeoutWithWrongFormat = FacilityCloseoutItem.builder()
        .startDate("2026-10-01")  // wrong format — should be dd/MM/yyyy
        .endDate("2026-12-31")
        .facilityCodes(List.of("DIN"))
        .build();
    var hotel = new HotelInformation();
    hotel.setHotelId("LONEUS");
    hotel.setHotelFacilities(new ArrayList<>(List.of(dinFacility)));
    hotel.setFacilityCloseout(FacilityCloseout.builder().items(List.of(closeoutWithWrongFormat)).build());

    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenReturn(mockAemHotelInformationDto());
    when(hotelInformationMapper.toDomainModel(any(), any(), any(), anyBoolean())).thenReturn(hotel);
    stubTripadvisorFlag(false);

    var hotelInfoRequest = HotelsInformationRequest.builder()
        .hotelIds(List.of(HOTEL_ID_EDIPAR))
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .stayStartDate(LocalDate.of(2026, Month.OCTOBER, 2))
        .stayEndDate(LocalDate.of(2026, Month.OCTOBER, 3))
        .build();

    // Act
    var response = contentOutPort.getHotelsInformation(hotelInfoRequest, false);

    // Assert — invalid format → item skipped → DIN not removed
    Assertions.assertThat(response.getFirst().getHotelFacilities())
        .extracting(HotelFacility::getCode)
        .containsExactly("DIN");
  }

  @Test
  void getHotelInformation__ShouldReturnOK() {
    //Arrange
    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenReturn(
        mockAemHotelInformationDtoResponse());
    when(hotelInformationMapper.toDomainModel(any())).thenReturn(mockHotelInformation());
    stubTripadvisorFlag(false);

    //Act
    var hotelInformation = contentOutPort.getHotelInformation(createHotelInfoRequest(), false);

    assertThat(hotelInformation, notNullValue());
    assertEquals("TestName", hotelInformation.getName());
    assertEquals("PI", hotelInformation.getBrand());
    assertEquals("logo-url/test.jpg", hotelInformation.getRestaurant().getLogoSrc());
    assertEquals("THYME", hotelInformation.getRestaurant().getName());
    assertEquals("food-at-the-social.jpg", hotelInformation.getRestaurant().getBookingCardImage());
    assertEquals("Food at the Social", hotelInformation.getRestaurant().getBookingCardTitle());
    assertEquals("Restaurant", hotelInformation.getRestaurant().getBookingCardDescription());
    assertEquals("/content/dam/BackgroundImageRestaurants.png", hotelInformation.getRestaurant()
            .getBookingCardBackgroundImage());

    assertEquals("Table", hotelInformation.getBookRestaurantCta().getBookingCardCtaText());
    assertEquals("logo-url/book", hotelInformation.getBookRestaurantCta().getBookingCardCtaLink());
    assertEquals("sidebar-card-social", hotelInformation.getBookRestaurantCta().getBookingCardTrackingId());
    assertEquals("KINPTI", hotelInformation.getTripAdvisorReviews().getHotelCode());

    verify(hotelReviewClient, never()).getTripAdvisorReviews(any(), any());

    verify(hotelInformationMapper).toDomainModel(hotelInfoDtoCaptor.capture());
    AemHotelInformationDto capturedDto = hotelInfoDtoCaptor.getValue();
    assertNotNull(capturedDto);
    assertEquals("2025-09-15", capturedDto.getCityTax().getBookingDateFrom());
    assertEquals("2025-08-23", capturedDto.getCityTax().getEffectiveFrom());
  }

  @Test
  void getHotelInformation_with_cityTax_ShouldReturnOK() {
    //Arrange
    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenReturn(
        mockAemHotelInformationDtoResponseForCityTax("23-08-2025", "15-09-2025"));
    when(hotelInformationMapper.toDomainModel(any())).thenReturn(mockHotelInformation());
    stubTripadvisorFlag(false);

    //Act
    var hotelInformation = contentOutPort.getHotelInformation(createHotelInfoRequest(), false);

    assertThat(hotelInformation, notNullValue());
    assertEquals("TestName", hotelInformation.getName());
    assertEquals("PI", hotelInformation.getBrand());
    assertEquals("logo-url/test.jpg", hotelInformation.getRestaurant().getLogoSrc());
    assertEquals("food-at-the-social.jpg", hotelInformation.getRestaurant().getBookingCardImage());
    assertEquals("Food at the Social", hotelInformation.getRestaurant().getBookingCardTitle());
    assertEquals("Restaurant", hotelInformation.getRestaurant().getBookingCardDescription());
    assertEquals("/content/dam/BackgroundImageRestaurants.png", hotelInformation.getRestaurant()
            .getBookingCardBackgroundImage());
    assertEquals("Table", hotelInformation.getBookRestaurantCta().getBookingCardCtaText());
    assertEquals("logo-url/book", hotelInformation.getBookRestaurantCta().getBookingCardCtaLink());
    assertEquals("sidebar-card-social", hotelInformation.getBookRestaurantCta().getBookingCardTrackingId());
    assertEquals("THYME", hotelInformation.getRestaurant().getName());
    assertEquals("KINPTI", hotelInformation.getTripAdvisorReviews().getHotelCode());

    verify(hotelReviewClient, never()).getTripAdvisorReviews(any(), any());

    verify(hotelInformationMapper).toDomainModel(hotelInfoDtoCaptor.capture());
    AemHotelInformationDto capturedDto = hotelInfoDtoCaptor.getValue();
    assertNotNull(capturedDto);
    assertEquals("2025-09-15", capturedDto.getCityTax().getBookingDateFrom());
    assertEquals("2025-08-23", capturedDto.getCityTax().getEffectiveFrom());
  }

  @Test
  void getHotelInformation_with_cityTax_1_ShouldReturnOK() {
    //Arrange
    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenReturn(
        mockAemHotelInformationDtoResponseForCityTax("", ""));
    when(hotelInformationMapper.toDomainModel(any())).thenReturn(mockHotelInformation());
    stubTripadvisorFlag(false);

    //Act
    var hotelInformation = contentOutPort.getHotelInformation(createHotelInfoRequest(), false);

    assertThat(hotelInformation, notNullValue());
    assertEquals("TestName", hotelInformation.getName());
    assertEquals("PI", hotelInformation.getBrand());
    assertEquals("logo-url/test.jpg", hotelInformation.getRestaurant().getLogoSrc());
    assertEquals("food-at-the-social.jpg", hotelInformation.getRestaurant().getBookingCardImage());
    assertEquals("Food at the Social", hotelInformation.getRestaurant().getBookingCardTitle());
    assertEquals("Restaurant", hotelInformation.getRestaurant().getBookingCardDescription());
    assertEquals("/content/dam/BackgroundImageRestaurants.png", hotelInformation.getRestaurant()
            .getBookingCardBackgroundImage());
    assertEquals("Table", hotelInformation.getBookRestaurantCta().getBookingCardCtaText());
    assertEquals("logo-url/book", hotelInformation.getBookRestaurantCta().getBookingCardCtaLink());
    assertEquals("sidebar-card-social", hotelInformation.getBookRestaurantCta().getBookingCardTrackingId());
    assertEquals("THYME", hotelInformation.getRestaurant().getName());
    assertEquals("KINPTI", hotelInformation.getTripAdvisorReviews().getHotelCode());

    verify(hotelReviewClient, never()).getTripAdvisorReviews(any(), any());

    verify(hotelInformationMapper).toDomainModel(hotelInfoDtoCaptor.capture());
    AemHotelInformationDto capturedDto = hotelInfoDtoCaptor.getValue();
    assertNotNull(capturedDto);
    assertEquals("", capturedDto.getCityTax().getBookingDateFrom());
    assertEquals("", capturedDto.getCityTax().getEffectiveFrom());
  }

  @Test
  void getHotelInformation_with_cityTax_2_ShouldReturnOK() {
    //Arrange
    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenReturn(
        mockAemHotelInformationDtoResponseForCityTax("2025-08-23", "2025-09-15"));
    stubTripadvisorFlag(false);

    //Act
    assertThrows(DateTimeParseException.class, () -> {
      contentOutPort.getHotelInformation(createHotelInfoRequest(), false);
    });
  }

  @Test
  void getHotelInformation_TripadvisorFlagEnabled_ShouldReturnOK() {
    //Arrange
    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenReturn(
        mockAemHotelInformationDtoResponse());
    when(hotelInformationMapper.toDomainModel(any())).thenReturn(mockHotelInformation());
    stubTripadvisorFlag(true);

    //Act
    var hotelInformation = contentOutPort.getHotelInformation(createHotelInfoRequest(), true);

    assertThat(hotelInformation, notNullValue());
    assertEquals("TestName", hotelInformation.getName());
    assertEquals("PI", hotelInformation.getBrand());
    assertEquals("logo-url/test.jpg", hotelInformation.getRestaurant().getLogoSrc());
    assertEquals("food-at-the-social.jpg", hotelInformation.getRestaurant().getBookingCardImage());
    assertEquals("Food at the Social", hotelInformation.getRestaurant().getBookingCardTitle());
    assertEquals("Restaurant", hotelInformation.getRestaurant().getBookingCardDescription());
    assertEquals("/content/dam/BackgroundImageRestaurants.png", hotelInformation.getRestaurant()
            .getBookingCardBackgroundImage());
    assertEquals("Table", hotelInformation.getBookRestaurantCta().getBookingCardCtaText());
    assertEquals("logo-url/book", hotelInformation.getBookRestaurantCta().getBookingCardCtaLink());
    assertEquals("sidebar-card-social", hotelInformation.getBookRestaurantCta().getBookingCardTrackingId());
    assertEquals("THYME", hotelInformation.getRestaurant().getName());
    assertEquals("KINPTI", hotelInformation.getTripAdvisorReviews().getHotelCode());

    verify(hotelReviewClient).getTripAdvisorReviews(any(), any());
  }

  @Test
  void getHotelInformationBySlug__ShouldReturnOK() {
    //Arrange
    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenReturn(
        mockAemHotelInformationDtoResponse());
    when(hotelInformationRequestMapper.toDtoModel(any())).thenReturn(mockHotelInformationDto());
    when(hotelInformationMapper.toDomainModel(any())).thenReturn(mockHotelInformation());
    when(aemClient.getAllHotelDetails(any())).thenReturn(
        Collections.singletonList(mockHotelShortInformationDto()));
    stubTripadvisorFlag(false);

    //Act
    var hotelInformation = contentOutPort.getHotelInformationBySlug(createHotelInfoRequest());
    //Assert
    assertThat(hotelInformation, notNullValue());
    assertEquals("TestName", hotelInformation.getName());
    assertEquals("PI", hotelInformation.getBrand());
    assertEquals("logo-url/test.jpg", hotelInformation.getRestaurant().getLogoSrc());
    assertEquals("THYME", hotelInformation.getRestaurant().getName());
    assertEquals("Breakfast", hotelInformation.getRestaurant().getMenus().get(0).getName());
    assertEquals("/test/Global Breakfast.pdf",
        hotelInformation.getRestaurant().getMenus().get(0).getMenuSrc());
    assertEquals("Dinner", hotelInformation.getRestaurant().getMenus().get(1).getName());
    assertEquals("/test/Global Dinner.pdf",
        hotelInformation.getRestaurant().getMenus().get(1).getMenuSrc());
    Assertions.assertThat(hotelInformation.getRoomConfiguration().getTabItems()).hasSize(1)
        .extracting(uk.co.whitbread.content.domain.model.hotel.out.TabItem::getRoomName)
        .containsOnlyOnce("RoomTitle");
    Assertions.assertThat(hotelInformation.getRoomConfiguration().getTabGroups()).hasSize(1)
        .extracting(uk.co.whitbread.content.domain.model.hotel.out.TabGroup::getGroupName)
        .containsOnlyOnce("GroupTitle");
    assertEquals("KINPTI", hotelInformation.getTripAdvisorReviews().getHotelCode());
  }

  @Test
  void getHotelInformationBySlug__ShouldFilterFacilitiesByOverlappingFacilityCloseout() {
    // Arrange
    var hotelInfoRequest = HotelInformationRequest.builder()
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .slug("Slug")
        .stayStartDate(LocalDate.of(2026, Month.OCTOBER, 2))
        .stayEndDate(LocalDate.of(2026, Month.OCTOBER, 3))
        .build();

    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenReturn(
        mockAemHotelInformationDtoResponse());
    when(hotelInformationRequestMapper.toDtoModel(any())).thenReturn(mockHotelInformationDto());
    when(hotelInformationMapper.toDomainModel(any())).thenReturn(mockHotelInformationWithFacilityCloseout());
    when(aemClient.getAllHotelDetails(any())).thenReturn(
        Collections.singletonList(mockHotelShortInformationDto()));
    stubTripadvisorFlag(false);

    // Act
    var response = contentOutPort.getHotelInformationBySlug(hotelInfoRequest);

    // Assert
    assertThat(response, notNullValue());
    Assertions.assertThat(response.getHotelFacilities())
        .extracting(HotelFacility::getCode)
        .containsExactly("LFT");
  }

  @Test
  void getAllHotelsShortInformation__ShouldReturnOK() {
    //Arrange
    when(hotelInformationRequestMapper.toDtoModel(any())).thenReturn(mockHotelInformationDto());
    when(hotelInformationMapper.toShortHotelDomainModel(any())).thenReturn(mockHotelShortInformation());
    when(aemClient.getAllHotelDetails(any())).thenReturn(mockAllHotelsShortInformation());


    //Act
    var hotelInformation = contentOutPort.getAllHotelsShortInformation(COUNTRY_GB, LANGUAGE_EN);
    //Assert
    assertThat(hotelInformation, notNullValue());
    assertThat(hotelInformation, hasSize(2));
    assertEquals("LONEUS", hotelInformation.get(0).getCode());
    assertEquals("Loneus", hotelInformation.get(0).getTitle());
    assertEquals("PI", hotelInformation.get(0).getBrand());
    assertEquals("Slug", hotelInformation.get(0).getHotelPagePath());
  }

  @Test
  void getAllHotelsShortInformation__EmptyList() {
    //Arrange
    when(hotelInformationRequestMapper.toDtoModel(any())).thenReturn(mockHotelInformationDto());
    when(aemClient.getAllHotelDetails(any())).thenReturn(Collections.emptyList());


    //Act
    var hotelInformation = contentOutPort.getAllHotelsShortInformation(COUNTRY_GB, LANGUAGE_EN);
    //Assert
    assertThat(hotelInformation, notNullValue());
    assertThat(hotelInformation, hasSize(0));
  }

  private uk.co.whitbread.content.domain.model.hotel.out.TripAdvisorReviews mockHotelTripAdvisorReviewResponse() {
    Awards award = Awards.builder()
        .awardType("Travelers Choice")
        .image("https://static.tacdn.com/img2/travelers_choice/widgets/tchotel_2023_L.png")
        .year("2023")
        .build();

    SubRatings subRating = SubRatings.builder()
        .localisedName("Location")
        .ratingImageUrl("https://static.tacdn.com/img2/ratings/traveler/ss5.0.svg")
        .value(BigDecimal.valueOf(5.0))
        .build();

    Reviews review = Reviews.builder()
        .title("Very enjoyable stay.")
        .text("We stayed in The Premier Inn Kings Cross for 6 nights")
        .rating(BigDecimal.valueOf(4.0))
        .publishedDate("2024-02-12T07:09:56-0500")
        .tripType("Couples")
        .build();

    return TripAdvisorReviews.builder()
        .awards(List.of(award))
        .rating(BigDecimal.valueOf(4.0))
        .subRatings(List.of(subRating))
        .name("Premier Inn London King's Cross Hotel")
        .address("26-30 York Way Kings Cross, London N1 9AA England")
        .reviews(List.of(review))
        .numberOfReviews(6689)
        .writeReview(
            "https://www.tripadvisor.com/UserReview-g186338-d571109-Premier_Inn_London_King_s_Cross_Hotel-London_England.html?m=67640")
        .webUrl(
            "https://www.tripadvisor.com/Hotel_Review-g186338-d571109-Reviews-Premier_Inn_London_King_s_Cross_Hotel-London_England.html?m=67640")
        .locationId("571109")
        .hotelCode("KINPTI")
        .build();
  }

  private HotelInformationDto mockHotelInformationDto() {
    return HotelInformationDto.builder().country(COUNTRY_GB).language(LANGUAGE_EN).build();
  }

  @Test
  void getHotelInformationBySlug__ShouldReturnResourceNotFound() {
    //Arrange
    var hotelInfoRequest = createHotelInfoRequest();
    var expectedMessage = String.format(
        "HotelDetails From Aem with hotelShortInformationDtoList=%s, slug=%s not found", 1,
        hotelInfoRequest.getSlug());
    when(aemClient.getAllHotelDetails(any())).thenReturn(
        Collections.singletonList(HotelShortInformationDto.builder().build()));

    //Act
    var actual =
        assertThrows(ResourceNotFoundException.class,
            () -> contentOutPort.getHotelInformationBySlug(hotelInfoRequest));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(),
        is(DIGITAL_HOTEL_SLUG_NOT_FOUND_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(DIGITAL_HOTEL_SLUG_NOT_FOUND_EXCEPTION.getCode()));
  }

  @Test
  void givenHotelSlug_whenGetHotelInformation_thenReturnsCorrectRoomClassConfigurations() {
    // Arrange
    var mockHotelInformationResponse = createMockHotelInformationResponse();
    var mockRoomClassConfigurationDto = createMockRoomClassConfigurationDto();
    var mockAemResponse = mockAemHotelInformationDtoResponse();
    mockAemResponse.setRoomClassConfiguration(mockRoomClassConfigurationDto);

    when(aemClient.getSingleHotelInformation(any(), any(), any()))
        .thenReturn(mockAemResponse);
    when(hotelInformationRequestMapper.toDtoModel(any()))
        .thenReturn(mockHotelInformationDto());
    when(hotelInformationMapper.toDomainModel(any()))
        .thenReturn(mockHotelInformationResponse);
    when(aemClient.getAllHotelDetails(any()))
        .thenReturn(Collections.singletonList(mockHotelShortInformationDto()));

    stubTripadvisorFlag(false);

    // Act
    var hotelInformation = contentOutPort.getHotelInformationBySlug(createHotelInfoRequest());

    // Assert
    assertThat(hotelInformation, is(notNullValue()));
    assertThat(hotelInformation.getRoomClassConfiguration(), hasSize(2));
    assertThat(hotelInformation.getRoomClassConfiguration().get(0).getCode(), is("SV"));
    assertThat(hotelInformation.getRoomClassConfiguration().get(1).getCode(), is("PV"));
  }

  private HotelInformation createMockHotelInformationResponse() {
    var roomClassConfig1 = new uk.co.whitbread.content.domain.model.hotel.out.RoomClassConfiguration();
    roomClassConfig1.setTitle("Standard room with city view");
    roomClassConfig1.setCode("SV");

    var roomClassConfig2 = new uk.co.whitbread.content.domain.model.hotel.out.RoomClassConfiguration();
    roomClassConfig2.setTitle("Premier plus room with city view");
    roomClassConfig2.setCode("PV");

    var configList = new ArrayList<uk.co.whitbread.content.domain.model.hotel.out.RoomClassConfiguration>();
    configList.add(roomClassConfig1);
    configList.add(roomClassConfig2);

    var hotelInfo = mockHotelInformation();
    hotelInfo.setRoomClassConfiguration(configList);
    return hotelInfo;
  }

  private Map<String, RoomClassConfiguration> createMockRoomClassConfigurationDto() {
    var configDto = new HashMap<String, RoomClassConfiguration>();
    configDto.put("SV",
        RoomClassConfiguration.builder().code("SV").title("Standard room with city view").build());
    configDto.put("PV",
        RoomClassConfiguration.builder().code("PV").title("Premier plus room with city view")
            .build());
    return configDto;
  }

  @Test
  void getHotelPaymentInformation__ShouldReturnOK() {
    //Arrange
    var expectedPaymentInfo = new HotelPaymentInformation();
    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenReturn(
        mockAemHotelInformationDtoResponse());
    when(hotelInformationMapper.toHotelPaymentInformationModel(any())).thenReturn(
        expectedPaymentInfo);

    //Act
    var actualPaymentInformation = contentOutPort.getHotelPaymentInformation(
        HotelInformationRequest.builder().hotelId(HOTEL_ID_EDIPAR).country(COUNTRY_GB)
            .language(LANGUAGE_EN).build());

    //Assert
    assertThat(actualPaymentInformation, equalTo(expectedPaymentInfo));
  }

  @Test
  void getHotelPaymentInformation__ShouldReturnResourceNotFoundException() {
    //Arrange
    String expectedMessage = "OHIP error while retrieving Hotel Info.";
    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenThrow(
        new AemResponseException(AEM_HOTELS_INFORMATION_EXCEPTION, expectedMessage, exception));
    var hotelInformationRequest = HotelInformationRequest.builder()
        .hotelId(HOTEL_ID_EDIPAR)
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .build();

    //Act
    var actual =
        assertThrows(AemResponseException.class,
            () -> contentOutPort.getHotelPaymentInformation(hotelInformationRequest));

    //Asserts
    assertException(actual, ErrorCode.AEM_HOTELS_INFORMATION_EXCEPTION, expectedMessage);
  }

  @Test
  void getHotelInformation__ShouldReturnAemResponseException() {
    //Arrange
    var expectedMessage = "Unable to get hotels information.";
    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenThrow(
        new AemResponseException(
            AEM_HOTELS_INFORMATION_EXCEPTION, expectedMessage, exception));
    stubTripadvisorFlag(false);
    var hotelInfoRequest = createHotelInfoRequest();

    //Act
    var actual =
        assertThrows(AemResponseException.class,
            () -> contentOutPort.getHotelInformation(hotelInfoRequest, false));

    //Asserts
    assertException(actual, AEM_HOTELS_INFORMATION_EXCEPTION, expectedMessage);
  }

  @Test
  void getHotelInformation__AncillaryCloseout() {
    //Arrange
    var hotelInfoRequest = createHotelInfoRequest();
    final AemHotelInformationDto aemHotelInformationDto = mockSingleAemHotelInformationDtoResponse();
    mockAncillaryCloseout(aemHotelInformationDto);
    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenReturn(
        aemHotelInformationDto);
    when(hotelInformationMapper.toDomainModel(any())).thenReturn(mockSingleHotelInformation());
    stubTripadvisorFlag(false);

    //Act
    final HotelInformation hotelInformation = contentOutPort.getHotelInformation(hotelInfoRequest, false);

    //Asserts
    assertEquals("SE", hotelInformation.getAncillaryCloseout().getItems().get(0).getUpsellCodes());
    assertEquals("No meals available",
        hotelInformation.getAncillaryCloseout().getNoMealsHeading());
    assertEquals("Sorry, meals are not available for your selected dates",
        hotelInformation.getAncillaryCloseout().getNoMealsMessage());
  }

  @Test
  void getHotelInformation__AncillaryCloseout_NotFound() {
    //Arrange
    var hotelInfoRequest = createHotelInfoRequest();
    final AemHotelInformationDto aemHotelInformationDto = mockSingleAemHotelInformationDtoResponse();
    mockAncillaryCloseoutNotFound(aemHotelInformationDto);
    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenReturn(
        aemHotelInformationDto);
    when(hotelInformationMapper.toDomainModel(any())).thenReturn(mockSingleHotelInformation());
    stubTripadvisorFlag(false);

    //Act
    final HotelInformation hotelInformation = contentOutPort.getHotelInformation(hotelInfoRequest, false);

    //Asserts
    assertEquals(ContentOutPortImpl.NO_UPSELLING_CODES_FOUND,
        hotelInformation.getAncillaryCloseout().getItems().get(0).getUpsellCodes());
  }

  @Test
  void getHotelsWithFacilityFilter_ValidInput_ShouldReturnOK() {
    //Arrange
    String acoFacility = "ACO";
    List<String> returnedResultHotelsList = List.of("LONEUS", "LONKIN");

    //Act
    var retHotels =
        contentOutPort.getHotelsWithFacilityFilter(acoFacility, mockAllHotelsInformation());

    //Asserts
    assertThat(retHotels, notNullValue());
    assertThat(retHotels.getHotelIds(), notNullValue());
    Assertions.assertThat(retHotels.getHotelIds()).hasSameElementsAs(returnedResultHotelsList);
  }

  @Test
  void getHotelsWithFacilityFilter_EmptyInput_ShouldReturnOK() {
    //Arrange
    String acoFacility = "ACO";

    //Act
    var retHotels = contentOutPort.getHotelsWithFacilityFilter(acoFacility, List.of());

    //Asserts
    assertThat(retHotels, notNullValue());
    assertThat(retHotels.getHotelIds(), notNullValue());
    Assertions.assertThat(retHotels.getHotelIds()).isEmpty();
  }

  @Test
  void getAllHotelsInformation__ShouldReturnOK() {
    //Arrange
    when(aemClient.getSingleHotelInformation(any(), any(), any())).thenReturn(
        mockSingleAemHotelInformationDtoResponse());
    when(aemClient.getAllHotelDetails(any())).thenReturn(mockAllHotelsShortInformation());
    when(hotelInformationMapper.toDomainModel(any(), any(), any(), anyBoolean())).thenReturn(
        mockSingleHotelInformation());
    stubTripadvisorFlag(false);

    //Act
    var retHotelsInfo = contentOutPort.getAllHotelsInformation(COUNTRY_GB, LANGUAGE_EN);

    //Asserts
    assertThat(retHotelsInfo, notNullValue());
    Assertions.assertThat(retHotelsInfo)
        .hasSameElementsAs(List.of(mockSingleHotelInformation(), mockSingleHotelInformation()));
  }

  @Test
  void getAllHotelFacilityFilters__ShouldReturnOK() {
    //Arrange
    when(srpFiltersConfig.getFacilityCodes()).thenReturn(mockFacilityCodes());

    //Act
    var retHotelFacilityFilters = contentOutPort.getAllHotelFacilityFilters();

    //Asserts
    assertThat(retHotelFacilityFilters, notNullValue());
    Assertions.assertThat(retHotelFacilityFilters).hasSameElementsAs(mockFacilityCodes());
  }

  @Test
  void getHotelsOpeningSoon_PastOpenDate_ShouldReturnEmptyResult() {
    //Arrange
    List<HotelInformation> hotelInformations = mockHotelsInformation();
    hotelInformations.forEach(
        hotel -> hotel.setHotelOpeningDate(LocalDateTime.now().minusDays(1).toString()));
    //Act
    var hotelIds = contentOutPort.getHotelsOpeningSoon(hotelInformations);

    //Asserts
    assertThat(hotelIds, notNullValue());
    assertTrue(CollectionUtils.isEmpty(hotelIds.getHotelIds()));
  }

  @Test
  void getHotelsOpeningSoon_FutureOpenDate_ShouldReturnIds() {
    //Arrange
    List<HotelInformation> hotelInformations = mockHotelsInformation();
    hotelInformations.forEach(
        hotel -> hotel.setHotelOpeningDate(LocalDateTime.now().plusDays(1).toString()));
    var inputHotelIds = hotelInformations.stream().map(HotelInformation::getHotelId).toList();

    //Act
    var hotelIds = contentOutPort.getHotelsOpeningSoon(hotelInformations);

    //Asserts
    assertThat(hotelIds, notNullValue());
    assertThat(hotelIds.getHotelIds(), notNullValue());
    Assertions.assertThat(hotelIds.getHotelIds()).hasSameElementsAs(inputHotelIds);
  }

  @Test
  void getHotelsOpeningSoon_PastAndFutureOpenDate_ShouldReturnIds() {
    //Arrange
    HotelInformation hotel1 = mockSingleHotelInformation();
    hotel1.setHotelId("PAST");
    hotel1.setHotelOpeningDate(LocalDateTime.now().minusDays(1).toString());

    HotelInformation hotel2 = mockSingleHotelInformation();
    hotel2.setHotelId("TODAY");
    hotel2.setHotelOpeningDate(LocalDateTime.now().toString());

    HotelInformation hotel3 = mockSingleHotelInformation();
    hotel3.setHotelId("FUTURE");
    hotel3.setHotelOpeningDate(LocalDateTime.now().plusDays(1).toString());
    var hotelInformations = List.of(hotel1, hotel2, hotel3);

    //Act
    var openingSoonHotelIds = contentOutPort.getHotelsOpeningSoon(hotelInformations);

    //Asserts
    assertThat(openingSoonHotelIds, notNullValue());
    assertThat(openingSoonHotelIds.getHotelIds(), notNullValue());
    assertEquals(2, openingSoonHotelIds.getHotelIds().size());
    Assertions.assertThat(openingSoonHotelIds.getHotelIds())
        .hasSameElementsAs(List.of("TODAY", "FUTURE"));
  }

  @Test
  void getExtras__ShouldReturnOK() {
    //Arrange
    when(aemClient.getLabels((any(LabelsRequestDto.class)))).thenReturn(mockExtrasAEMResponse());
    when(contentRequestMapper.toDtoModel(COUNTRY_GB, LANGUAGE_EN, CategoryEnumDto.EXTRAS))
        .thenReturn(new LabelsRequestDto());

    //Act
    var response = contentOutPort.getExtras(COUNTRY_GB, LANGUAGE_EN);

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getExtrasLabels(), hasSize(3));
  }

  @Test
  void getExtras_contentDoesNotExist_ShouldReturnException() {
    //Arrange
    var request = new LabelsRequest("null", "null", CategoryEnum.EXTRAS, null);
    var expectedMessage = String.format(
        "No labels for this getExtras request for country=%s, language=%s, category=%s",
        request.getCountry(), request.getLanguage(), request.getCategory());
    when(aemClient.getLabels(any())).thenReturn(null);
    //Act
    var actual =
        assertThrows(ContentException.class, () -> contentOutPort.getExtras(null, null));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(),
        is(DIGITAL_EXTRAS_LABEL_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(DIGITAL_EXTRAS_LABEL_EXCEPTION.getCode()));
  }

  private List<String> mockFacilityCodes() {
    return List.of("ACO", "LFT");
  }

  private HotelInformation mockHotelInformation() {
    HotelInformation hotelInformation = new HotelInformation();
    hotelInformation.setName("TestName");
    hotelInformation.setBrand("PI");
    hotelInformation.setRestaurant(
        Restaurant.builder().logoSrc("logo-url/test.jpg").name("THYME").bookingCardImage("food-at-the-social.jpg")
                .bookingCardTitle("Food at the Social").bookingCardDescription("Restaurant")
                .bookingCardBackgroundImage("/content/dam/BackgroundImageRestaurants.png")
                .menus(createMenus())
            .build());
    hotelInformation.setBookRestaurantCta(
        BookRestaurantCta.builder().bookingCardCtaText("Table")
               .bookingCardCtaLink("logo-url/book")
               .bookingCardTrackingId("sidebar-card-social").build());
    hotelInformation.setRoomConfiguration(mockRoomConfiguration());
    hotelInformation.setBookingFlow(BookingFlow.builder().bookingFlowItems(createBookingFlowItems())
        .targetBookingFlowItems(createTarghetBookingFlowItems()).build());
    hotelInformation.setImportantInfo(mockImportantInfo());
    hotelInformation.setAccessibilityInfo(mockAccessibilityInfo());
    hotelInformation.setTripAdvisorReviews(mockHotelTripAdvisorReviewResponse());
    return hotelInformation;
  }

  private List<HotelInformation> mockHotelsInformation() {
    HotelInformation hotelInformation1 = new HotelInformation();
    hotelInformation1.setName("TestName");
    hotelInformation1.setHotelId(HOTEL_ID_EDIPAR);
    hotelInformation1.setBrand("PI");
    hotelInformation1.setRestaurant(
        Restaurant.builder().logoSrc("logo-url/test.jpg").name("THYME").menus(createMenus())
            .build());
    hotelInformation1.setRoomConfiguration(mockRoomConfiguration());
    hotelInformation1.setBookingFlow(
        BookingFlow.builder().bookingFlowItems(createBookingFlowItems())
            .targetBookingFlowItems(createTarghetBookingFlowItems()).build());
    hotelInformation1.setMessagingFlag(mockDomainMessagingFlag());
    hotelInformation1.setHotelFlags(mockDomainHotelFlags());
    hotelInformation1.setThumbnailImages(List.of(mockThumbnailImage()));
    hotelInformation1.setHotelOpeningDate("20-02-2022");
    hotelInformation1.setImportantInfo(mockImportantInfo());
    hotelInformation1.setAccessibilityInfo(mockAccessibilityInfo());

    HotelInformation hotelInformation2 = new HotelInformation();
    hotelInformation2.setName("TestName");
    hotelInformation2.setHotelId(HOTEL_ID_LONEUS);
    hotelInformation2.setBrand("PI");
    hotelInformation2.setRestaurant(
        Restaurant.builder().logoSrc("logo-url/test.jpg").name("THYME").menus(createMenus())
            .build());
    hotelInformation2.setRoomConfiguration(mockRoomConfiguration());
    hotelInformation2.setBookingFlow(
        BookingFlow.builder().bookingFlowItems(createBookingFlowItems())
            .targetBookingFlowItems(createTarghetBookingFlowItems()).build());
    hotelInformation2.setMessagingFlag(mockDomainMessagingFlag());
    hotelInformation2.setHotelFlags(mockDomainHotelFlags());
    hotelInformation2.setThumbnailImages(List.of(mockThumbnailImage()));
    hotelInformation2.setHotelOpeningDate("20-02-2022");
    hotelInformation2.setImportantInfo(mockImportantInfo());
    hotelInformation2.setAccessibilityInfo(mockAccessibilityInfo());

    return List.of(hotelInformation1, hotelInformation2);
  }

  private HotelInformation mockEdiparHotelInformation() {
    HotelInformation hotelInformation1 = new HotelInformation();
    hotelInformation1.setName("TestName");
    hotelInformation1.setHotelId(HOTEL_ID_EDIPAR);
    hotelInformation1.setBrand("PI");
    hotelInformation1.setRestaurant(
        Restaurant.builder().logoSrc("logo-url/test.jpg").name("THYME").menus(createMenus())
            .build());
    hotelInformation1.setRoomConfiguration(mockRoomConfiguration());
    hotelInformation1.setBookingFlow(
        BookingFlow.builder().bookingFlowItems(createBookingFlowItems())
            .targetBookingFlowItems(createTarghetBookingFlowItems()).build());
    hotelInformation1.setMessagingFlag(mockDomainMessagingFlag());
    hotelInformation1.setHotelFlags(mockDomainHotelFlags());
    hotelInformation1.setThumbnailImages(List.of(mockThumbnailImage()));
    hotelInformation1.setHotelOpeningDate("20-02-2022");
    hotelInformation1.setImportantInfo(mockImportantInfo());
    hotelInformation1.setAccessibilityInfo(mockAccessibilityInfo());

    return hotelInformation1;
  }

  private AemHotelInformationDto mockAemHotelInformationDto() {
    AemHotelInformationDto hotelInformationDto1 = new AemHotelInformationDto();
    hotelInformationDto1.setName("TestName");
    hotelInformationDto1.setCode(HOTEL_ID_EDIPAR);
    hotelInformationDto1.setBrand("PI");
    hotelInformationDto1.setHotelRoomConfiguration(mockHotelRoomConfiguration());
    hotelInformationDto1.setHotelOpeningDate("20-02-2022");
    hotelInformationDto1.setImages(List.of(mockHotelImage()));
    hotelInformationDto1.setMessagingFlag(mockMessagingFlag());
    hotelInformationDto1.setHotelFlags(mockAemHotelFlags());
    hotelInformationDto1.setImportantInfo(mockHotelImportantInfo());
    hotelInformationDto1.setAccessibilityInfo(mockAccessibilityInfoDto());

    return hotelInformationDto1;
  }

  private RoomConfiguration mockRoomConfiguration() {
    return RoomConfiguration.builder().tabItems(Collections.singletonList(mockTabItemModel()))
        .tabGroups(Collections.singletonList(mockTabGroupModel())).build();
  }

  private uk.co.whitbread.content.domain.model.hotel.out.TabItem mockTabItemModel() {
    return uk.co.whitbread.content.domain.model.hotel.out.TabItem.builder().roomName("RoomTitle")
        .roomType("standard")
        .roomDescription("roomConfigurationInfoText").build();
  }

  private uk.co.whitbread.content.domain.model.hotel.out.TabGroup mockTabGroupModel() {
    return uk.co.whitbread.content.domain.model.hotel.out.TabGroup.builder().groupId("double")
        .groupName("GroupTitle")
        .build();
  }

  private AemHotelInformationDto mockAemHotelInformationDtoResponseForCityTax(String effectiveFrom, String bookingDateFrom) {
    AemHotelInformationDto hotelInformationDto = new AemHotelInformationDto();
    hotelInformationDto.setName("TestName");
    hotelInformationDto.setCode("TLONKI");
    hotelInformationDto.setBrand("PI");
    hotelInformationDto.setHotelRoomConfiguration(mockHotelRoomConfiguration());
    hotelInformationDto.setImportantInfo(mockHotelImportantInfo());
    hotelInformationDto.setAccessibilityInfo(mockAccessibilityInfoDto());
    hotelInformationDto.setFacilities(mockAemHotelInformationFacility());
    hotelInformationDto.setCityTax(CityTax.builder()
        .effectiveFrom(effectiveFrom)
        .bookingDateFrom(bookingDateFrom)
        .build());
    return hotelInformationDto;
  }


  private AemHotelInformationDto mockAemHotelInformationDtoResponse() {
    AemHotelInformationDto hotelInformationDto = new AemHotelInformationDto();
    hotelInformationDto.setName("TestName");
    hotelInformationDto.setCode("TLONKI");
    hotelInformationDto.setBrand("PI");
    hotelInformationDto.setHotelRoomConfiguration(mockHotelRoomConfiguration());
    hotelInformationDto.setImportantInfo(mockHotelImportantInfo());
    hotelInformationDto.setAccessibilityInfo(mockAccessibilityInfoDto());
    hotelInformationDto.setFacilities(mockAemHotelInformationFacility());
    hotelInformationDto.setCityTax(CityTax.builder()
            .effectiveFrom("23-08-2025")
            .bookingDateFrom("15-09-2025")
        .build());
    return hotelInformationDto;
  }

  private AemHotelInformationDto mockSingleAemHotelInformationDtoResponse() {
    AemHotelInformationDto hotelInformationDto = new AemHotelInformationDto();
    hotelInformationDto.setName("TestName");
    hotelInformationDto.setCode("TLONKI");
    hotelInformationDto.setBrand("PI");
    hotelInformationDto.setCountryCodeISO("GB");
    hotelInformationDto.setHotelRoomConfiguration(mockHotelRoomConfiguration());
    hotelInformationDto.setImportantInfo(mockHotelImportantInfo());
    hotelInformationDto.setAccessibilityInfo(mockAccessibilityInfoDto());
    hotelInformationDto.setFacilities(mockAemHotelInformationFacility());
    hotelInformationDto.setAncillaryCloseout(mockAemHotelInformationAncillaryCloseout());
    return hotelInformationDto;
  }

  private void mockAncillaryCloseout(AemHotelInformationDto hotelInformationDto) {
    hotelInformationDto.setServiceCodeAndUpsellCodeMapping(Map.of("SE", "SE"));
  }

  private void mockAncillaryCloseoutNotFound(AemHotelInformationDto hotelInformationDto) {
    hotelInformationDto.setServiceCodeAndUpsellCodeMapping(Map.of("GE", "GE"));
  }

  private AncillaryCloseout mockAemHotelInformationAncillaryCloseout() {
    return AncillaryCloseout.builder()
        .noMealsHeading("No meals available")
        .noMealsMessage("Sorry, meals are not available for your selected dates")
        .items(
            List.of(AncillaryCloseoutItem.builder().serviceCode("SE").build()))
        .build();
  }

  private List<Facility> mockAemHotelInformationFacility() {
    var acoFacility = Facility.builder()
        .code("ACO")
        .description("Klimatisierte Zimmer")
        .ranking(10)
        .iconSrc("/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/ACO.svg")
        .visibility(true)
        .build();

    var lftFacility = Facility.builder()
        .code("LFT")
        .description("Fahrstuhl")
        .ranking(90)
        .iconSrc("/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/LFT.svg")
        .visibility(true)
        .build();

    return List.of(acoFacility, lftFacility);
  }

  private MessagingFlag mockMessagingFlag() {
    return MessagingFlag.builder().flagText("surrounding-area").flagDescription("surrounding-area")
        .flagColor("blue")
        .build();
  }

  private uk.co.whitbread.content.domain.model.hotel.out.MessagingFlag mockDomainMessagingFlag() {
    return uk.co.whitbread.content.domain.model.hotel.out.MessagingFlag.builder()
        .text("surrounding-area")
        .description("surrounding-area").color("blue").build();
  }

  private uk.co.whitbread.content.domain.model.hotel.out.HotelFlags mockDomainHotelFlags() {
    return uk.co.whitbread.content.domain.model.hotel.out.HotelFlags.builder()
        .isEnabled(true)
        .flagOverlay(uk.co.whitbread.content.domain.model.hotel.out.FlagDetail.builder()
            .text("New Premier Plus rooms")
            .textColour("#D7C3FF")
            .backgroundColour("#0007")
            .backgroundImage("/content/dam/icons/resources/icon-overlay.png")
            .build())
        .flagBanner(uk.co.whitbread.content.domain.model.hotel.out.FlagDetail.builder()
            .text("New restaurant now open")
            .textColour("#FFFFFF")
            .backgroundColour("#511E62")
            .backgroundImage("/content/dam/icons/resources/icon-banner.png")
            .build())
        .build();
  }

  private uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.HotelFlags mockAemHotelFlags() {
    return uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in.HotelFlags.builder()
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
        .build();
  }

  private Image mockHotelImage() {
    return Image.builder().altText("surrounding-area")
        .fileReference("/content/dam/pi/websites/hotelimages/gb/en/L/LONEUS/LONEUS 1.jpg")
        .tags(List.of("surrounding-area")).build();
  }

  private ThumbnailImage mockThumbnailImage() {
    return ThumbnailImage.builder()
        .imageSrc("/content/dam/pi/websites/hotelimages/gb/en/L/LONEUS/LONEUS 1.jpg")
        .tags(List.of("surrounding-area")).build();
  }

  private HotelRoomConfiguration mockHotelRoomConfiguration() {
    return HotelRoomConfiguration.builder().tabItems(Collections.singletonList(mockTabItem()))
        .tabGroups(Collections.singletonList(mockTabGroup())).build();
  }

  private TabItem mockTabItem() {
    return TabItem.builder().roomTitle("RoomTitle").roomType("family")
        .roomTypeCode("FMQUAD,FMFOUR")
        .fileReference(
            "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-family-room.jpg")
        .rateGridRoomDescription(
            "TEST - Most of our family rooms include a double or kingsize bed, plus a sofa bed and pull-out bed depending on the number of guests staying in the room.")
        .roomDescriptionText("roomConfigurationInfoText").build();
  }

  private TabGroup mockTabGroup() {
    return TabGroup.builder().groupId("double").groupTitle("GroupTitle").build();
  }

  private HotelInformationRequest createHotelInfoRequest() {
    return HotelInformationRequest.builder().language(LANGUAGE_EN).slug("Slug").country(COUNTRY_GB)
        .build();
  }

  private HotelsInformationRequest createHotelsInfoRequest() {
    return HotelsInformationRequest.builder().hotelIds(List.of(HOTEL_ID_EDIPAR, HOTEL_ID_LONEUS))
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN).build();
  }

  private HotelShortInformationDto mockHotelShortInformationDto() {
    return HotelShortInformationDto.builder().hotelPagePath("Slug").code("code").build();
  }

  private List<Menu> createMenus() {
    return of(Menu.builder().name("Breakfast").menuSrc("/test/Global Breakfast.pdf").build(),
        Menu.builder().name("Dinner").menuSrc("/test/Global Dinner.pdf").build());
  }

  private List<BookingFlowItem> createBookingFlowItems() {
    return of(BookingFlowItem.builder().rateCode("A")
            .bookingFlowPath("https://secure2.premierinn.com/de/de/booking-a1/extras.html")
            .bookingId("booking-a1").build(),
        BookingFlowItem.builder().rateCode("G")
            .bookingFlowPath("https://secure2.premierinn.com/de/de/booking-a1/extras.html")
            .bookingId("booking-a1")
            .build(), BookingFlowItem.builder().rateCode("Q")
            .bookingFlowPath("https://secure2.premierinn.com/de/de/booking-a1/extras.html")
            .bookingId("booking-a1")
            .build());
  }

  private List<TargetBookingFlowItem> createTarghetBookingFlowItems() {
    return of(TargetBookingFlowItem.builder().id("id1").rateCode("A")
            .bookingFlowPath("https://secure2.premierinn.com/de/de/booking-a1/extras.html").build(),
        TargetBookingFlowItem.builder().id("id2").rateCode("G")
            .bookingFlowPath("https://secure2.premierinn.com/de/de/booking-a1/extras.html").build(),
        TargetBookingFlowItem.builder().id("id3").rateCode("Q")
            .bookingFlowPath("https://secure2.premierinn.com/de/de/booking-a1/extras.html")
            .build());
  }

  @Test
  void getMultipleLabels__ShouldReturnOK() {
    //Arrange
    when(aemClient.getMultipleLabels((any(MultipleLabelsRequestDto.class)))).thenReturn(
        mockMultipleLabelsAEMResponse());
    when(multipleLabelsRequestMapper.toDtoModel(any())).thenReturn(new MultipleLabelsRequestDto());

    //Act
    var contentAEMResponse = contentOutPort.getMultipleLabels(MultipleLabelsRequest.builder()
        .categories(of(CategoryEnum.BOOKING, CategoryEnum.PI_BOOKINGS, CategoryEnum.MAIN,
            CategoryEnum.PI_PRE_CHECKIN))
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN).build());

    //Assert
    assertThat(contentAEMResponse, notNullValue());
    assertThat(contentAEMResponse, aMapWithSize(4));
    assertThat(contentAEMResponse, IsMapContaining.hasKey("booking"));
    assertThat(contentAEMResponse, IsMapContaining.hasKey("main"));
    assertThat(contentAEMResponse, IsMapContaining.hasKey("piBookings"));
    assertThat(contentAEMResponse, IsMapContaining.hasKey("piPreCheckIn"));
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
    assertThat(contentAEMResponse.get("piPreCheckIn"),
        IsMapContaining.hasEntry("precheckin.details.passport",
            "Passport Number(only in case of foreign nationals)"));
    assertThat(contentAEMResponse.get("piPreCheckIn"),
        IsMapContaining.hasEntry("precheckin.errors.empty.dependent",
            "Please enter your dependent"));
    assertThat(contentAEMResponse.get("piPreCheckIn"),
        IsMapContaining.hasEntry("precheckin.details.nationality",
            "Nationality(ies)*"));
  }

  @Captor
  ArgumentCaptor<MultipleLabelsRequestDto> captor;

  @Test
  void getMultipleLabels__ShouldSortCategoriesBeforeAemCall() {
    //Arrange
    when(aemClient.getMultipleLabels((any(MultipleLabelsRequestDto.class)))).thenReturn(
        mockMultipleLabelsAEMResponse());
    List<CategoryEnum> categories = of(CategoryEnum.BOOKING, CategoryEnum.PI_BOOKINGS,
        CategoryEnum.MAIN, CategoryEnum.PI_PRE_CHECKIN);
    List<CategoryEnumDto> categoryDtoList = new ArrayList<>(
        of(CategoryEnumDto.BOOKING, CategoryEnumDto.PI_BOOKINGS, CategoryEnumDto.MAIN,
            CategoryEnumDto.PI_PRE_CHECKIN));
    List<CategoryEnumDto> categoryDtoListToSort = new ArrayList<>(categoryDtoList);
    Collections.sort(categoryDtoListToSort);

    MultipleLabelsRequestDto dto = new MultipleLabelsRequestDto();
    dto.setCategories(categoryDtoList);
    when(multipleLabelsRequestMapper.toDtoModel(any())).thenReturn(dto);

    //Act
    contentOutPort.getMultipleLabels(MultipleLabelsRequest.builder()
        .categories(categories)
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN).build());

    //Assert
    verify(aemClient).getMultipleLabels(captor.capture());
    assertEquals(categoryDtoListToSort, captor.getValue().getCategories());
  }

  @Test
  void getMultipleLabels_badInput_ShouldReturnException() {
    //Arrange
    var request = new MultipleLabelsRequest("null", "null", List.of(CategoryEnum.MAIN,
        CategoryEnum.PI_PRE_CHECKIN));
    var expectedMessage = String.format(
        "No labels for this getMultipleLabels request for country=%s, language=%s,"
            + " categories=%s", request.getCountry(), request.getLanguage(),
        request.getCategories().size());
    when(multipleLabelsRequestMapper.toDtoModel(any())).thenReturn(new MultipleLabelsRequestDto());
    when(aemClient.getMultipleLabels(any())).thenReturn(null);

    //Act
    var actual =
        assertThrows(ContentException.class, () -> contentOutPort.getMultipleLabels(request));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(),
        is(DIGITAL_MULTIPLE_LABELS_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(DIGITAL_MULTIPLE_LABELS_EXCEPTION.getCode()));
  }

  @Test
  void getMultipleLabels__ShouldReturnException() {
    //Arrange
    var expectedMessage = "Unable to get labels.";
    when(multipleLabelsRequestMapper.toDtoModel(any())).thenReturn(new MultipleLabelsRequestDto());
    when(aemClient.getMultipleLabels(any())).thenThrow(
        new AemResponseException(AEM_LABELS_EXCEPTION, "Unable to get labels.", exception));
    var request = new MultipleLabelsRequest("null", "null", List.of(CategoryEnum.MAIN,
        CategoryEnum.PI_PRE_CHECKIN));
    //Act
    var actual =
        assertThrows(AemResponseException.class, () -> contentOutPort.getMultipleLabels(request));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getMessage(), is(expectedMessage));
  }

  @Test
  void getMultipleLabels__ShouldReturnExceptionForEmptyLabels() {
    //Arrange
    var request = new MultipleLabelsRequest("gb", "null", List.of(CategoryEnum.MAIN,
        CategoryEnum.PI_PRE_CHECKIN));
    var message = String.format(
        "No labels for this getMultipleLabels request for country=%s, language=%s,"
            + " categories=%s", request.getCountry(),
        request.getLanguage(), request.getCategories().size());
    when(multipleLabelsRequestMapper.toDtoModel(any())).thenReturn(new MultipleLabelsRequestDto());
    when(aemClient.getMultipleLabels(any())).thenReturn(new HashMap<>());

    //Act
    var actual =
        assertThrows(ContentException.class, () -> contentOutPort.getMultipleLabels(request));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(),
        is(DIGITAL_MULTIPLE_LABELS_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(message));
    assertThat(actual.getErrorCode(), is(DIGITAL_MULTIPLE_LABELS_EXCEPTION.getCode()));
  }

  private Map<String, Map<String, String>> mockMultipleLabelsAEMResponse() {
    Map<String, Map<String, String>> response = new HashMap<>();
    response.put("main", mockContentAEMResponse());
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
    response.put("booking", bookingResponse);
    Map<String, String> piBookingResponse = new HashMap<>();
    piBookingResponse.put("dashboard.bookings.contactUsInfo",
        "Call 0333 003 8101  *Calls are charged at the national rate");
    piBookingResponse.put("dashboard.bookings.newTotal", "New Total");
    piBookingResponse.put("dashboard.bookings.flexRateText",
        "Amend or cancel up to {hour} on arrival day");
    piBookingResponse.put("dashboard.bookings.priceFor", "Price for");
    piBookingResponse.put("dashboard.bookings.invalidEmail", "Please enter a valid email address");
    piBookingResponse.put("dashboard.bookings.alreadyHaveAccount", "Already have an account?");
    response.put("piBookings", piBookingResponse);
    Map<String, String> piPreCheckInResponse = new HashMap<>();
    piPreCheckInResponse.put("precheckin.details.passport",
        "Passport Number(only in case of foreign nationals)");
    piPreCheckInResponse.put("precheckin.errors.empty.dependent", "Please enter your dependent");
    piPreCheckInResponse.put("precheckin.details.nationality", "Nationality(ies)*");
    piPreCheckInResponse.put("precheckin.details.dateofbirth", "Date of Birth*");
    piPreCheckInResponse.put("precheckin.details.scheduleddate", "Scheduled Date of Departure*");
    piPreCheckInResponse.put("precheckin.details.surname", "Last name*");
    response.put("piPreCheckIn", piPreCheckInResponse);
    return response;
  }

  @Test
  void getIndexHeaderData__ShouldReturnOK() {
    //Arrange
    var indexHeaderData = new IndexHeaderData();

    when(aemClient.getIndexHeaderData((any(IndexHeaderDataRequestDto.class)))).thenReturn(
        new AemIndexHeaderDataDto());
    when(indexHeaderDataMapper.toDomainModel(any())).thenReturn(indexHeaderData);
    when(indexHeaderDataRequestMapper.toDto(any())).thenReturn(new IndexHeaderDataRequestDto());

    //Act
    var contentAEMResponse = contentOutPort.getIndexHeaderData(
        IndexHeaderDataRequest.builder().country(COUNTRY_GB).language(LANGUAGE_EN)
            .businessBooker(Boolean.FALSE)
            .build());

    //Assert
    assertThat(contentAEMResponse, equalTo(contentAEMResponse));
  }

  @Test
  void getIndexHeaderData__ShouldReturnException() {
    //Arrange
    var expectedMessage = "Unable to get index header data";
    when(indexHeaderDataRequestMapper.toDto(any())).thenReturn(new IndexHeaderDataRequestDto());
    when(aemClient.getIndexHeaderData((any(IndexHeaderDataRequestDto.class)))).thenThrow(
        new AemResponseException(AEM_INDEX_HEADER_DATA_EXCEPTION, expectedMessage, exception));
    var indexHeaderDataRequest = IndexHeaderDataRequest.builder()
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .businessBooker(Boolean.FALSE)
        .build();

    //Act
    var actual =
        assertThrows(AemResponseException.class,
            () -> contentOutPort.getIndexHeaderData(indexHeaderDataRequest));

    //Assert
    assertException(actual, AEM_INDEX_HEADER_DATA_EXCEPTION, expectedMessage);
  }

  @Test
  void getIndexHeaderData__ShouldReturnResourceNotFoundException() {
    //Arrange
    var expectedMessage = "Unable to get index header data.";
    when(indexHeaderDataRequestMapper.toDto(any())).thenReturn(new IndexHeaderDataRequestDto());
    when(aemClient.getIndexHeaderData((any(IndexHeaderDataRequestDto.class)))).thenThrow(
        new AemResponseException(AEM_INDEX_HEADER_DATA_EXCEPTION, expectedMessage, exception));
    var indexHeaderDataRequest = IndexHeaderDataRequest.builder()
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .businessBooker(Boolean.FALSE)
        .build();

    //Act
    var actual =
        assertThrows(AemResponseException.class, () -> contentOutPort.
            getIndexHeaderData(indexHeaderDataRequest));

    //Assert
    assertException(actual, AEM_INDEX_HEADER_DATA_EXCEPTION, expectedMessage);
  }

  @Test
  void getSearchResultsData__ShouldReturnResourceNotFoundException() {
    //Arrange
    var expectedMessage = "Unable to get search results data.";
    when(localizationRequestMapper.toDtoModel(any())).thenReturn(new LocalizationRequestDto());
    when(aemClient.getSearchResultsData(any())).thenThrow(
        new AemResponseException(AEM_SEARCH_RESULT_DATA_EXCEPTION, expectedMessage, exception));
    var localizationRequest = LocalizationRequest.builder()
        .language("en")
        .country("gb")
        .build();
    //Act
    var actual =
        assertThrows(AemResponseException.class, () -> contentOutPort.getSearchResultsData(
            localizationRequest));

    //Assert
    assertException(actual, AEM_SEARCH_RESULT_DATA_EXCEPTION, expectedMessage);
  }

  @Test
  void getSearchRules__ShouldReturnOk() {
    //Arrange
    GlobalConfigDtoBuilder globalConfigDtoBuilder = GlobalConfigDto.builder()
        .bookingWidgetConfig(
            BookingWidgetConfigDto.builder()
                .numberOfNights(4)
                .maxRooms(4)
                .maxArrivalDate(365)
                .allowedRoomTypesByOccupancy(of(
                    new AllowedRoomTypesByOccupancy(of("DIS"), 2, 1)
                ))
                .build()
        );

    when(aemClient.getGlobalConfig((any(GlobalConfigRequestDto.class)))).thenReturn(
        globalConfigDtoBuilder.build());
    when(searchRulesMapper.toDomainModel(any(GlobalConfigDto.class), any())).thenReturn(
        new SearchRules());
    when(globalConfigRequestMapper.toDto(any())).thenReturn(new GlobalConfigRequestDto());

    //Act
    SearchRules contentAEMResponse = contentOutPort.getSearchRules(
        GlobalConfigRequest.builder()
            .country(COUNTRY_GB)
            .language(LANGUAGE_EN)
            .channelId(CHANNEL_ID)
            .brand(BRAND)
            .build());

    //Assert
    assertThat(contentAEMResponse, equalTo(contentAEMResponse));
  }

  @Test
  void getSearchRules__ShouldReturnResourceNotFoundException() {
    String expectedMessage = "Unable to get search rules data.";
    when(globalConfigRequestMapper.toDto(any())).thenReturn(new GlobalConfigRequestDto());
    when(aemClient.getGlobalConfig(any())).thenThrow(
        new AemResponseException(AEM_SEARCH_RULES_DATA_EXCEPTION, expectedMessage, exception));

    GlobalConfigRequest globalConfigRequest = GlobalConfigRequest.builder()
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .channelId(CHANNEL_ID)
        .brand(BRAND)
        .build();

    //Act
    AemResponseException actual =
        assertThrows(AemResponseException.class, () -> contentOutPort.
            getSearchRules(globalConfigRequest));

    //Assert
    assertException(actual, AEM_SEARCH_RULES_DATA_EXCEPTION, expectedMessage);
  }

  @Test
  void getRoomClassConfig__ShouldReturnOk() {
    //Arrange
    when(aemClient.getRoomClassConfig((any(GlobalConfigRequestDto.class)))).thenReturn(
        RoomClassConfigDto.builder().build());
    final var roomClassConfig = RoomClassConfig.builder()
        .roomClassConfig(of(RoomClassOrder.builder().order(1).code("ON").build())).build();
    when(roomClassConfigMapper.toDomainModel(any(RoomClassConfigDto.class))).thenReturn(
        roomClassConfig);
    when(globalConfigRequestMapper.toDto(any())).thenReturn(
        GlobalConfigRequestDto.builder().build());

    //Act
    RoomClassConfig response = contentOutPort.getRoomClassConfig(
        GlobalConfigRequest.builder()
            .country(COUNTRY_GB)
            .language(LANGUAGE_EN)
            .channelId(CHANNEL_ID)
            .brand(BRAND)
            .build());

    //Assert
    assertThat(response, equalTo(roomClassConfig));
  }

  @Test
  void getRoomClassConfig__ShouldReturnResourceNotFoundException() {
    String expectedMessage = "Unable to get room class config.";
    when(globalConfigRequestMapper.toDto(any())).thenReturn(
        GlobalConfigRequestDto.builder().build());
    when(aemClient.getRoomClassConfig(any())).thenThrow(
        new AemResponseException(AEM_ROOM_CLASS_CONFIG_EXCEPTION, expectedMessage, exception));

    GlobalConfigRequest globalConfigRequest = GlobalConfigRequest.builder()
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .channelId(CHANNEL_ID)
        .brand(BRAND)
        .build();

    //Act
    AemResponseException actual =
        assertThrows(AemResponseException.class, () -> contentOutPort.
            getRoomClassConfig(globalConfigRequest));

    //Assert
    assertException(actual, AEM_ROOM_CLASS_CONFIG_EXCEPTION, expectedMessage);
  }

  @Test
  void getGlobalConfig__ShouldReturnOk() {
    //Arrange
    PriceFinderConfig priceFinderConfig = PriceFinderConfig.builder().build();

    PriceFinderConfigDto priceFinderConfigDto = PriceFinderConfigDto.builder().build();
    GlobalConfigDto globalConfigDto = GlobalConfigDto.builder()
            .priceFinderConfig(priceFinderConfigDto)
            .upsellItemsExtras(List.of(
                    UpsellItemsExtrasDto.builder()
                            .packageCode("HSCKIN")
                            .promoPackageCode("HSCKIF")
                            .promoText("Free Early Check In")
                            .build()
            ))
            .build();

    GlobalConfigDtoBuilder globalConfigDtoBuilder = GlobalConfigDto.builder()
        .bookingWidgetConfig(
            BookingWidgetConfigDto.builder()
                .numberOfNights(4)
                .maxRooms(4)
                .maxArrivalDate(365)
                .allowedRoomTypesByOccupancy(of(
                    new AllowedRoomTypesByOccupancy(of("DIS"), 2, 1)
                ))
                .build()
        )
        .roomUpgradeOptions(mockRoomUpgradeOptionsDto());

    when(aemClient.getGlobalConfig((any(GlobalConfigRequestDto.class)))).thenReturn(
            globalConfigDto);
    var roomClassConfig = RoomClassConfig.builder()
        .roomClassConfig(of(RoomClassOrder.builder().order(1).code("ON").build()))
        .build();
    var expectedPromotionConfig = mockExpectedPromotionConfig();
    var searchRules = SearchRules.builder()
        .maxRooms(4)
        .build();
    var upsellItemsExtra = UpsellItemsExtras.builder()
            .packageCode("HSCKIN")
            .promoPackageCode("HSCKIF")
            .promoText("Free Early Check In")
            .build();

    final var globalConfig = GlobalConfig.builder()
        .priceFinderConfig(priceFinderConfig)
        .roomClassConfig(of(RoomClassOrder.builder().order(1).code("ON").build()))
        .promotionsConfig(expectedPromotionConfig)
        .maxRoomsLim(searchRules)
        .upsellItemsExtras(List.of(upsellItemsExtra))
        .build();

    doReturn(priceFinderConfig).when(priceFinderGlobalConfigMapper)
            .toDomainModel(eq(priceFinderConfigDto));
    when(roomClassConfigMapper.toDomainModel(any(GlobalConfigDto.class))).thenReturn(
        roomClassConfig);
    when(searchRulesMapper.toDomainModel(any(GlobalConfigDto.class), any())).thenReturn(
        searchRules);
    when(promotionsConfigMapper.toDomainModel(nullable(PromotionsConfigDto.class))).thenReturn(expectedPromotionConfig);
    when(upsellItemsExtrasMapper.toDomainModel(anyList()))
            .thenReturn(List.of(upsellItemsExtra));
    when(globalConfigRequestMapper.toDto(any())).thenReturn(
        GlobalConfigRequestDto.builder().build());

    //Act
    var response = contentOutPort.getGlobalConfig(
        GlobalConfigRequest.builder()
            .country(COUNTRY_GB)
            .language(LANGUAGE_EN)
            .channelId(CHANNEL_ID)
            .brand(BRAND)
            .build());

    //Assert
    assertThat(response, equalTo(globalConfig));
    assertNotNull(globalConfigDto.getPriceFinderConfig(), "PriceFinderConfigDto should not be null");
  }

  private GlobalConfigDto mockGlobalConfigDto() {
    return GlobalConfigDto.builder()
        .promotionsConfig(mockExpectedPromotionConfigDto())
        .build();
  }

  private PromotionsConfigDto mockExpectedPromotionConfigDto() {
    return PromotionsConfigDto.builder()
        .promoItems(List.of(
            PromotionItemsDto.builder()
                .enabled(true)
                .promoCode("ABCDEF")
                .landingPage("promo-page-name")
                .numberOfNights(2)
                .maxRooms(4)
                .minRooms(1)
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
                .ratePlanCode("FLEX")
                .roomClass(List.of("PP","ST"))
                .promoWrongRoomClassMessage("Switch to PremierInn Plus rooms or Standard rooms")
                .promoWrongRatePlanMessage("Switch to Flex rate")
                .promoWrongRoomClassAndRatePlanMessage("Switch to Switch to PremierInn Plus rooms or Standard rooms and Flex rate")
                .build()
        ))
        .promoBox(mockPromoBoxDtoResponse())
        .build();
  }

  private PromotionsConfig mockExpectedPromotionConfig() {
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
                .ratePlanCode("FLEX")
                .roomClass(List.of("PP","ST"))
                .promoWrongRoomClassMessage("Switch to PremierInn Plus rooms or Standard rooms")
                .promoWrongRatePlanMessage("Switch to Flex rate")
                .promoWrongRoomClassAndRatePlanMessage("Switch to Switch to PremierInn Plus rooms or Standard rooms and Flex rate")
                .build()
        ))
        .promoBox(mockPromoBoxResponse())
            .metaPromoRateMapping(mockMetaPromoRateMapping())
        .build();
  }

  private List<MetaPromoRateMapping> mockMetaPromoRateMapping() {
    MetaPromoRateMapping metaPromoRateMapping = MetaPromoRateMapping.builder()
            .rate("FLEXRATE")
            .code("ADV")
            .build();
    return List.of(metaPromoRateMapping);
  }


  private PromoBox mockPromoBoxResponse() {
    return PromoBox.builder()
        .title("Enter voucher code")
        .button("Apply")
        .whenInvalid("Your voucher code is invalid. Please check it and try again.")
        .whenMultipleRedeem("You cannot redeem two voucher codes during the same booking.")
        .whenSuccess("Voucher applied successfully")
        .whenEmpty("Voucher code is required")
        .whenMaxRoomsExceeded("Maximum room limit exceeded")
        .whenMinRoomsNotMet("Minimum room requirement not met")
        .build();
  }

  private PromoBoxDto mockPromoBoxDtoResponse() {
    return PromoBoxDto.builder()
        .title("Enter voucher code")
        .button("Apply")
        .whenInvalid("Your voucher code is invalid. Please check it and try again.")
        .whenMultipleRedeem("You cannot redeem two voucher codes during the same booking.")
        .whenSuccess("Voucher applied successfully")
        .whenEmpty("Voucher code is required")
        .whenMaxRoomsExceeded("Maximum room limit exceeded")
        .whenMinRoomsNotMet("Minimum room requirement not met")
        .build();
  }

  private PromotionsInformationResponse mockPromotionsInformationResponse() {
    return PromotionsInformationResponse.builder()
        .promotionCode("ABCDEF")
        .landingPage("promo-page-name")
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
        .promoBox(mockPromoBoxResponse())
        .build();
  }

  private static RoomUpgradeOptionsDto mockRoomUpgradeOptionsDto() {
    return RoomUpgradeOptionsDto.builder()
        .roomUpgrades(of(RoomUpgradesDto.builder()
            .roomClass("SV")
            .heading("Upgrade to a room with a view")
            .description("<p>Stunning views...</p>")
            .imageUrl("url.jpg")
            .build()))
        .priceText("from £10 per night")
        .primaryButtonText("Upgrade now")
        .secondaryButtonText("Upgrade later")
        .build();
  }

  @Test
  void getGlobalConfig__ShouldReturnResourceNotFoundException() {
    String expectedMessage = "Unable to get global config data.";
    when(globalConfigRequestMapper.toDto(any())).thenReturn(
        GlobalConfigRequestDto.builder().build());
    when(aemClient.getGlobalConfig(any())).thenThrow(
        new AemResponseException(AEM_ROOM_CLASS_CONFIG_EXCEPTION, expectedMessage, exception));

    GlobalConfigRequest globalConfigRequest = GlobalConfigRequest.builder()
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .channelId(CHANNEL_ID)
        .brand(BRAND)
        .build();

    //Act
    AemResponseException actual =
        assertThrows(AemResponseException.class, () -> contentOutPort.
            getGlobalConfig(globalConfigRequest));

    //Assert
    assertException(actual, AEM_ROOM_CLASS_CONFIG_EXCEPTION, expectedMessage);
  }

  @Test
  void getDlpInformation__ShouldReturnOk() {
    //Arrange
    when(aemClient.getDlpInformation((any(DlpInformationRequestDto.class)))).thenReturn(
        DlpInformationDto.builder().build());
    final var dlpInformation = DlpInformation.builder()
        .hotels(of(Hotel.builder().order(1).code("LTNHTL").build())).build();
    when(dlpInformationMapper.toDomainModel(any())).thenReturn(dlpInformation);
    when(dlpInformationMapper.toDto(any())).thenReturn(DlpInformationRequestDto.builder().build());

    //Act
    DlpInformation response = contentOutPort.getDlpInformation(
        DlpInformationRequest.builder()
            .country(COUNTRY_GB)
            .language(LANGUAGE_EN)
            .dlpPath(DLP_PATH)
            .build());

    //Assert
    assertThat(response, equalTo(dlpInformation));
  }

  @Test
  void getDlpInformation__ShouldReturnResourceNotFoundException() {
    String expectedMessage = "Unable to get DLP information.";
    when(dlpInformationMapper.toDto(any())).thenReturn(DlpInformationRequestDto.builder().build());
    when(aemClient.getDlpInformation(any())).thenThrow(
        new AemResponseException(AEM_DLP_INFORMATION_EXCEPTION, expectedMessage, exception));

    DlpInformationRequest dlpInformationRequest = mockDlpInformationRequest();

    //Act
    AemResponseException actual =
        assertThrows(AemResponseException.class, () -> contentOutPort.
            getDlpInformation(dlpInformationRequest));

    //Assert
    assertException(actual, AEM_DLP_INFORMATION_EXCEPTION, expectedMessage);
    verifyNoInteractions(snowdropHotelsRetriever);
  }

  @Test
  void getDlpInformation__ShouldFetchHotelsFromSnowdrop_WhenHotelsListFromAemIsEmpty() {
    // Arrange
    DlpInformationRequest dlpInformationRequest = mockDlpInformationRequest();

    var dlpInformationRequestDto = DlpInformationRequestDto.builder()
            .dlpPath(DLP_PATH)
            .country(COUNTRY_GB)
            .language(LANGUAGE_EN)
            .build();

    var dlpInformationDto = DlpInformationDto.builder()
            .hotels(Collections.emptyList())
            .map(CoordinatesDto.builder().latitude(51.5074)
                    .longitude(-0.1278).radius("30").hideHotelDistance(false).build())
            .build();
    var mappedHotels = List.of(HotelDto.builder().code(HOTEL_ID_LONEUS).build());
    when(dlpInformationMapper.toDto(any())).thenReturn(dlpInformationRequestDto);
    when(aemClient.getDlpInformation(any())).thenReturn(dlpInformationDto);
    when(snowdropHotelsRetriever.mapHotelsFromSnowdrop(any(), any())).thenReturn(mappedHotels);
    when(dlpInformationMapper.toDomainModel(any())).thenReturn(DlpInformation.builder().build());

    // Act
    var result = contentOutPort.getDlpInformation(dlpInformationRequest);

    // Assert
    assertNotNull(result);
    assertEquals(mappedHotels.size(), dlpInformationDto.getHotels().size());
    verify(snowdropHotelsRetriever).mapHotelsFromSnowdrop(any(), any());
  }

  @Test
  void getDlpInformation__ShouldThrowContentException_WhenDlpInformationIsNull() {
    String expectedMessage = "Unable to get DLP information.";
    when(dlpInformationMapper.toDto(any())).thenReturn(DlpInformationRequestDto.builder().build());
    when(aemClient.getDlpInformation(any())).thenReturn(null);

    DlpInformationRequest dlpInformationRequest = mockDlpInformationRequest();

    //Act
    ContentException actual =
            assertThrows(ContentException.class, () -> contentOutPort.
                    getDlpInformation(dlpInformationRequest));

    //Assert
    assertEquals(expectedMessage, actual.getMessage());
    verify(aemClient).getDlpInformation(any());
    verifyNoInteractions(snowdropHotelsRetriever);
  }

  @Test
  void getHomepageApps__ShouldReturnOk() {
    //Arrange
    when(aemClient.getAppsHomepage((any(
        AppsHomepageRequestDto.class)))).thenReturn(
        AppsHomepageResponseDto.builder().build());
    final var requestModel = AppsHomepageResponse.builder().build();
    final var dto = AppsHomepageRequestDto.builder().build();
    when(homepageAppsMapper.toDto(any())).thenReturn(dto);
    when(homepageAppsMapper.toDomainModel(any(AppsHomepageResponseDto.class))).thenReturn(
        requestModel);

    //Act
    AppsHomepageResponse response = contentOutPort.getAppsHomepage(
        AppsHomepageRequest.builder()
            .country(COUNTRY_GB)
            .language(LANGUAGE_EN)
            .channel("PI")
            .build());

    //Assert
    assertEquals(response, requestModel);
  }

  @Test
  void getHomepageApps__ShouldReturnResourceNotFoundException() {
    String expectedMessage = "message";
    final var dto = AppsHomepageRequestDto.builder().build();
    when(homepageAppsMapper.toDto(any())).thenReturn(dto);
    when(aemClient.getAppsHomepage(any())).thenThrow(
        new AemResponseException(AEM_APPS_HOMEPAGE_CONFIG_EXCEPTION, expectedMessage, exception));

    //Act
    AppsHomepageRequest request = AppsHomepageRequest.builder()
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .channel("PI")
        .build();
    AemResponseException actual = assertThrows(AemResponseException.class,
        () -> contentOutPort.getAppsHomepage(request));

    //Assert
    assertException(actual, AEM_APPS_HOMEPAGE_CONFIG_EXCEPTION, expectedMessage);
  }

  @Test
  void getPromoConfig_whenAemReturnsNull_thenNoPromoReturned() {
    // Arrange
    LocalDate today = LocalDate.now();

    when(aemClient.getGlobalConfig(any())).thenReturn(null);
    when(promoBoxResolver.resolvePreEvaluation(any()))
        .thenReturn(PromoBoxResolutionResult.nonTerminal());
    when(promoBoxResolver.resolvePostEvaluation(any(), any()))
        .thenReturn(PromoBoxResolutionResult.terminal(PromoBoxStatus.UNAVAILABLE));

    var req = PromoConfigRequest.builder()
        .brand(BRAND)
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .channelId(CHANNEL_ID)
        .stayStartDate(today.plusDays(10).toString())
        .stayEndDate(today.plusDays(12).toString())
        .build();

    // Act
    var resp = contentOutPort.getPromoConfig(req);

    // Assert
    assertThat(resp.getShowPromo(), is(false));
    assertThat(resp.getPromoInvalidMessage(), is("No applicable promotions"));
  }

  @Test
  void getPromoConfig_whenPromotionsConfigNullOrEmpty_thenNoPromoReturned() {
    // Arrange
    LocalDate today = LocalDate.now();
    when(aemClient.getGlobalConfig(any())).thenReturn(GlobalConfigDto.builder().build());
    doReturn(null)
            .when(promotionsConfigMapper)
            .toDomainModel(nullable(PromotionsConfigDto.class));

    var req = PromoConfigRequest.builder()
        .stayStartDate(today.plusDays(10).toString())
        .stayEndDate(today.plusDays(12).toString())
        .build();

    //Act
    var resp = contentOutPort.getPromoConfig(req);

    assertThat(resp.getShowPromo(), is(false));
    assertThat(resp.getPromoInvalidMessage(), is("No applicable promotions"));

    when(promotionsConfigMapper.toDomainModel(nullable(PromotionsConfigDto.class)))
        .thenReturn(PromotionsConfig.builder().promoItems(List.of()).build());

    resp = contentOutPort.getPromoConfig(req);

    // Assert
    assertThat(resp.getShowPromo(), is(false));
    assertThat(resp.getPromoInvalidMessage(), is("No applicable promotions"));
  }

  @Test
  void getPromoConfig_landingPage_matchingCode_bookingValid_andStayValid_returnsPromoWithinWindow() {
    // Arrange
    PromotionsConfig domainConfig = mockExpectedPromotionConfig();
    when(aemClient.getGlobalConfig(any())).thenReturn(GlobalConfigDto.builder().build());
    when(promotionsConfigMapper.toDomainModel(nullable(PromotionsConfigDto.class))).thenReturn(domainConfig);
    when(flowTypeResolver.resolve(any()))
        .thenReturn(PromoFlowType.LANDING_PAGE);
    when(strategyRegistry.get(PromoFlowType.LANDING_PAGE))
        .thenReturn(promoEvaluationStrategy);
    when(promoEvaluationStrategy.evaluate(any(), any()))
        .thenReturn(mockLandingPagePromo());
    when(promoBoxResolver.resolvePreEvaluation(any()))
        .thenReturn(PromoBoxResolutionResult.nonTerminal());
    when(promoBoxResolver.resolvePostEvaluation(any(), any()))
        .thenReturn(PromoBoxResolutionResult.terminal(PromoBoxStatus.UNAVAILABLE));

    var req = landingRequest("2025-12-12",
        "2026-02-16", "2026-02-18");

    // Act
    var resp = contentOutPort.getPromoConfig(req);

    // Assert
    assertThat(resp.getShowPromo(), is(true));
    assertThat(resp.getIsWithinPromoWindow(), is(true));
    assertThat(resp.getPromotionCode(), is("ABCDEF"));
    assertThat(resp.getPromoInvalidMessage(), nullValue());
    assertThat(resp.getPromoExpiredMessage(), nullValue());
    assertThat(resp.getLandingPage(), notNullValue());
  }

  @Test
  void getPromoConfig_siteWide_firstEnabledPromo_valid_returnsPromoWithinWindow() {
    // Arrange
    PromotionsConfig domainConfig = mockExpectedPromotionConfig();
    domainConfig.getPromoItems().get(0).setLandingPage("");

    when(aemClient.getGlobalConfig(any()))
        .thenReturn(GlobalConfigDto.builder().build());
    doReturn(domainConfig)
            .when(promotionsConfigMapper)
            .toDomainModel(nullable(PromotionsConfigDto.class));
    when(flowTypeResolver.resolve(any()))
        .thenReturn(PromoFlowType.SITE_WIDE);
    when(strategyRegistry.get(PromoFlowType.SITE_WIDE))
        .thenReturn(promoEvaluationStrategy);
    when(promoEvaluationStrategy.evaluate(any(), any()))
        .thenReturn(
            PromotionsInformationResponse.builder()
                .showPromo(true)
                .isWithinPromoWindow(true)
                .promotionCode("ABCDEF")
                .promoKind(PromoKind.SITE_WIDE)
                .build()
        );

    PromoConfigRequest req = PromoConfigRequest.builder()
        .bookingDate("2025-12-12")
        .stayStartDate("2026-02-16")
        .stayEndDate("2026-02-18")
        .build();

    // Act
    PromotionsInformationResponse resp =
        contentOutPort.getPromoConfig(req);

    // Assert
    assertThat(resp.getShowPromo(), is(true));
    assertThat(resp.getIsWithinPromoWindow(), is(true));
    assertThat(resp.getPromotionCode(), is("ABCDEF"));
  }

  @ParameterizedTest(name = "[{index}] booking={0}, stayStart={1}, stayEnd={2} -> expectedExpired={3}")
  @CsvSource({
      "2025-11-30,2026-02-16,2026-02-18,false",
      "2026-01-01,2026-02-16,2026-02-18,true",
      "2025-12-12,2026-02-10,2026-02-11,false",
      "2025-12-12,2026-02-16,2026-02-17,false"
  })
  void landing_invalidCases_returnExpectedMessage(
      String bookingDate,
      String stayStart,
      String stayEnd,
      boolean expectedExpired) {

    // Arrange
    PromotionsConfig domainConfig = mockExpectedPromotionConfig();

    when(aemClient.getGlobalConfig(any()))
        .thenReturn(GlobalConfigDto.builder().build());
    when(promotionsConfigMapper.toDomainModel(nullable(PromotionsConfigDto.class)))
        .thenReturn(domainConfig);
    when(flowTypeResolver.resolve(any()))
        .thenReturn(PromoFlowType.LANDING_PAGE);
    when(strategyRegistry.get(PromoFlowType.LANDING_PAGE))
        .thenReturn(promoEvaluationStrategy);
    when(promoEvaluationStrategy.evaluate(any(), any()))
        .thenAnswer(invocation -> {
          if (expectedExpired) {
            return PromotionsInformationResponse.builder()
                .showPromo(true)
                .isWithinPromoWindow(false)
                .promoExpiredMessage("Expired")
                .build();
          }
          return PromotionsInformationResponse.builder()
              .showPromo(true)
              .isWithinPromoWindow(false)
              .promoInvalidMessage("Invalid")
              .build();
        });

    PromoConfigRequest req =
        landingRequest(bookingDate, stayStart, stayEnd);

    // Act
    PromotionsInformationResponse resp =
        contentOutPort.getPromoConfig(req);

    // Assert
    assertThat(resp.getShowPromo(), is(true));
    assertThat(resp.getIsWithinPromoWindow(), is(false));

    if (expectedExpired) {
      assertThat(resp.getPromoExpiredMessage(), notNullValue());
      assertThat(resp.getPromoInvalidMessage(), nullValue());
    } else {
      assertThat(resp.getPromoInvalidMessage(), notNullValue());
    }
  }

  @ParameterizedTest(name = "[{index}] siteWide booking={0}, stayStart={1}, stayEnd={2}")
  @CsvSource({
      "2025-11-30,2026-02-16,2026-02-18",
      "2025-12-12,2026-02-10,2026-02-11",
      "2025-12-12,2026-02-16,2026-02-16"
  })
  void siteWide_invalidCases_returnNoPromo(
      String bookingDate, String stayStart, String stayEnd) {

    // Arrange
    PromotionsConfig domainConfig = PromotionsConfig.builder()
        .promoItems(List.of(
            mockExpectedPromotionConfig().getPromoItems().get(0)
        ))
        .build();

    when(aemClient.getGlobalConfig(any()))
        .thenReturn(GlobalConfigDto.builder().build());
    when(promotionsConfigMapper.toDomainModel(nullable(PromotionsConfigDto.class)))
        .thenReturn(domainConfig);
    when(flowTypeResolver.resolve(any()))
        .thenReturn(PromoFlowType.SITE_WIDE);
    when(strategyRegistry.get(PromoFlowType.SITE_WIDE))
        .thenReturn(promoEvaluationStrategy);
    when(promoEvaluationStrategy.evaluate(any(), any()))
        .thenReturn(mockNoPromo());

    PromoConfigRequest req = PromoConfigRequest.builder()
        .bookingDate(bookingDate)
        .stayStartDate(stayStart)
        .stayEndDate(stayEnd)
        .build();

    // Act
    PromotionsInformationResponse resp =
        contentOutPort.getPromoConfig(req);

    // Assert
    assertThat(resp.getShowPromo(), is(false));
    assertThat(resp.getPromoInvalidMessage(),
        is("No applicable promotions"));
  }

  @Test
  void parseDates_invalidFormat_throwsDateTimeParseException() {
    // Arrange
    PromotionsConfig domainConfig = mockExpectedPromotionConfig();
    when(aemClient.getGlobalConfig(any())).thenReturn(GlobalConfigDto.builder().build());
    doReturn(domainConfig)
            .when(promotionsConfigMapper)
            .toDomainModel(nullable(PromotionsConfigDto.class));
    when(promoBoxResolver.resolvePreEvaluation(any()))
        .thenReturn(PromoBoxResolutionResult.nonTerminal());

    var req = landingRequest("12-12-2025",
        "2026-02-16", "2026-02-18");

    // Assert
    assertThrows(DateTimeParseException.class, () -> contentOutPort.getPromoConfig(req));
  }

  @Test
  void parseDates_stayStartAfterStayEnd_throwsIllegalArgumentException() {
    // Arrange
    PromotionsConfig domainConfig = mockExpectedPromotionConfig();
    when(aemClient.getGlobalConfig(any())).thenReturn(GlobalConfigDto.builder().build());
    doReturn(domainConfig)
            .when(promotionsConfigMapper)
            .toDomainModel(nullable(PromotionsConfigDto.class));
    when(promoBoxResolver.resolvePreEvaluation(any()))
        .thenReturn(PromoBoxResolutionResult.nonTerminal());

    var req = landingRequest("2025-12-12",
        "2026-02-20", "2026-02-18");

    // Act
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> contentOutPort.getPromoConfig(req));

    // Assert
    assertThat(ex.getMessage(), containsString("must not be after stayEndDate"));
  }

  @Test
  void getPromoConfig_Amend_validLandingPagePromo_returnsPromoWithinWindow() {
    // Arrange
    PromotionsConfig domainConfig = mockExpectedPromotionConfig();

    when(aemClient.getGlobalConfig(any()))
        .thenReturn(GlobalConfigDto.builder().build());
    when(promotionsConfigMapper.toDomainModel(nullable(PromotionsConfigDto.class)))
        .thenReturn(domainConfig);
    when(flowTypeResolver.resolve(any()))
        .thenReturn(PromoFlowType.AMEND);
    when(strategyRegistry.get(PromoFlowType.AMEND))
        .thenReturn(promoEvaluationStrategy);
    when(promoEvaluationStrategy.evaluate(any(), any()))
        .thenReturn(mockLandingPagePromo());

    PromoConfigRequest req = landingRequest(
        "2025-12-12",
        "2026-02-16",
        "2026-02-18"
    );
    req.setPromoKind(PromoKind.LANDING_PAGE);
    req.setIsAmendRequest(Boolean.TRUE);

    // Act
    PromotionsInformationResponse resp =
        contentOutPort.getPromoConfig(req);

    // Assert
    assertThat(resp.getShowPromo(), is(true));
    assertThat(resp.getIsWithinPromoWindow(), is(true));
    assertThat(resp.getPromotionCode(), is("ABCDEF"));
    assertThat(resp.getPromoInvalidMessage(), nullValue());
    assertThat(resp.getPromoExpiredMessage(), nullValue());
    assertThat(resp.getLandingPage(), notNullValue());
  }

  @Test
  void getPromoConfig_Amend_validSiteWidePagePromo_returnsPromoWithinWindow() {
    // Arrange
    PromotionsConfig domainConfig = mockExpectedPromotionConfig();
    domainConfig.getPromoItems().get(0).setLandingPage("");

    when(aemClient.getGlobalConfig(any()))
        .thenReturn(GlobalConfigDto.builder().build());
    when(promotionsConfigMapper.toDomainModel(nullable(PromotionsConfigDto.class)))
        .thenReturn(domainConfig);
    when(flowTypeResolver.resolve(any()))
        .thenReturn(PromoFlowType.AMEND);
    when(strategyRegistry.get(PromoFlowType.AMEND))
        .thenReturn(promoEvaluationStrategy);
    when(promoEvaluationStrategy.evaluate(any(), any()))
        .thenReturn(mockSiteWidePromo());

    PromoConfigRequest req = PromoConfigRequest.builder()
        .bookingDate("2025-12-12")
        .stayStartDate("2026-02-16")
        .stayEndDate("2026-02-18")
        .promotionCode("ABCDEF")
        .promoKind(PromoKind.SITE_WIDE)
        .isAmendRequest(Boolean.TRUE)
        .build();

    // Act
    PromotionsInformationResponse resp =
        contentOutPort.getPromoConfig(req);

    // Assert
    assertThat(resp.getShowPromo(), is(true));
    assertThat(resp.getIsWithinPromoWindow(), is(true));
    assertThat(resp.getPromotionCode(), is("ABCDEF"));
  }

  @Test
  void getPromoConfig_Amend_WithNullPromoKind() {
    // Arrange
    PromotionsConfig domainConfig = mockExpectedPromotionConfig();
    domainConfig.getPromoItems().get(0).setLandingPage("");

    when(aemClient.getGlobalConfig(any()))
        .thenReturn(GlobalConfigDto.builder().build());
    when(promotionsConfigMapper.toDomainModel(nullable(PromotionsConfigDto.class)))
        .thenReturn(domainConfig);
    when(flowTypeResolver.resolve(any()))
        .thenReturn(PromoFlowType.AMEND);
    when(strategyRegistry.get(PromoFlowType.AMEND))
        .thenReturn(promoEvaluationStrategy);
    when(promoEvaluationStrategy.evaluate(any(), any()))
        .thenReturn(mockSiteWidePromo());

    PromoConfigRequest req = PromoConfigRequest.builder()
        .bookingDate("2025-12-12")
        .stayStartDate("2026-02-16")
        .stayEndDate("2026-02-18")
        .promotionCode("ABCDEF")
        .promoKind(null)
        .isAmendRequest(Boolean.TRUE)
        .build();

    // Act
    PromotionsInformationResponse resp =
        contentOutPort.getPromoConfig(req);

    // Assert
    assertThat(resp.getShowPromo(), is(true));
    assertThat(resp.getIsWithinPromoWindow(), is(true));
    assertThat(resp.getPromoKind(), is(PromoKind.SITE_WIDE));
  }

  @Test
  void getPromoConfig_Amend_NoMatchingPromoCode_ShouldReturnNoPromo() {
    // Arrange
    PromotionsConfig domainConfig = mockExpectedPromotionConfig();
    domainConfig.getPromoItems()
        .forEach(p -> p.setPromoCode("DIFFERENT_CODE"));

    when(aemClient.getGlobalConfig(any()))
        .thenReturn(GlobalConfigDto.builder()
            .promotionsConfig(PromotionsConfigDto.builder().build())
            .build());
    when(promotionsConfigMapper.toDomainModel(nullable(PromotionsConfigDto.class)))
        .thenReturn(domainConfig);
    when(flowTypeResolver.resolve(any()))
        .thenReturn(PromoFlowType.AMEND);
    when(strategyRegistry.get(PromoFlowType.AMEND))
        .thenReturn(promoEvaluationStrategy);
    when(promoEvaluationStrategy.evaluate(any(), any()))
        .thenReturn(mockNoPromo());

    PromoConfigRequest req = PromoConfigRequest.builder()
        .bookingDate("2025-12-12")
        .stayStartDate("2026-02-16")
        .stayEndDate("2026-02-18")
        .promotionCode("ABCDEF")
        .promoKind(null)
        .isAmendRequest(Boolean.TRUE)
        .build();

    // Act
    PromotionsInformationResponse resp =
        contentOutPort.getPromoConfig(req);

    // Assert
    assertThat(resp.getShowPromo(), is(false));
    assertThat(resp.getIsWithinPromoWindow(), is(false));
    assertThat(resp.getPromoInvalidMessage(),
        is("No applicable promotions"));
  }

  @Test
  void promoConfig_promobox_labels_shouldReturnOk() {
    // Arrange
    PromotionsConfig domainConfig = mockExpectedPromotionConfig();
    domainConfig.getPromoItems().get(0).setLandingPage("");

    when(aemClient.getGlobalConfig(any()))
        .thenReturn(mockGlobalConfigDto());
    when(promotionsConfigMapper.toDomainModel(nullable(PromotionsConfigDto.class)))
        .thenReturn(domainConfig);
    when(flowTypeResolver.resolve(any()))
        .thenReturn(PromoFlowType.SITE_WIDE);
    when(strategyRegistry.get(PromoFlowType.SITE_WIDE))
        .thenReturn(promoEvaluationStrategy);
    when(promoEvaluationStrategy.evaluate(any(), any()))
        .thenReturn(mockNoPromo());
    when(promotionsConfigMapper.toPromotionsInformationResponseModel(any(), any()))
        .thenReturn(mockPromotionsInformationResponse());
    when(promoBoxResolver.resolvePostEvaluation(any(), any()))
        .thenReturn(PromoBoxResolutionResult.terminal(PromoBoxStatus.UNAVAILABLE));

    PromoConfigRequest req = PromoConfigRequest.builder()
        .bookingDate("2025-12-12")
        .stayStartDate("2026-02-16")
        .stayEndDate("2026-02-18")
        .build();

    // Act
    PromotionsInformationResponse resp =
        contentOutPort.getPromoConfig(req);

    // Assert
    assertThat(resp.getPromoBox().getButton(), is("Apply"));
    assertThat(resp.getPromoBox().getTitle(), is("Enter voucher code"));
  }

  @Test
  void flowResolver_amendPromoBox_shouldResolveAmend() {
    // Arrange: use REAL resolver
    PromoFlowTypeResolver resolver = new PromoFlowTypeResolver();

    PromoContext context = PromoContext.builder()
        .promoBox(true)
        .amendRequest(true)
        .build();

    // Act
    PromoFlowType type = resolver.resolve(context);

    // Assert
    assertThat(type, is(PromoFlowType.AMEND));
  }

  @Test
  void promoBox_validLandingPromo_shouldReturnPromo() {
    when(promoBoxResolver.resolvePreEvaluation(any()))
        .thenReturn(PromoBoxResolutionResult.nonTerminal());

    when(promoBoxResolver.resolvePostEvaluation(any(), any()))
        .thenReturn(PromoBoxResolutionResult.terminal(PromoBoxStatus.SUCCESS));

    when(aemClient.getGlobalConfig(any()))
        .thenReturn(GlobalConfigDto.builder().build());
    doReturn(mockExpectedPromotionConfig())
            .when(promotionsConfigMapper)
            .toDomainModel(nullable(PromotionsConfigDto.class));
    when(flowTypeResolver.resolve(any()))
        .thenReturn(PromoFlowType.PROMO_BOX);
    when(strategyRegistry.get(any()))
        .thenReturn(promoEvaluationStrategy);
    when(promoEvaluationStrategy.evaluate(any(), any()))
        .thenReturn(mockLandingPagePromo());

    PromoConfigRequest req = PromoConfigRequest.builder()
        .promotionCode("ABCDEF")
        .bookingDate("2025-12-12")
        .stayStartDate("2026-02-16")
        .stayEndDate("2026-02-18")
        .isPromoBox(true)
        .build();

    PromotionsInformationResponse resp = contentOutPort.getPromoConfig(req);

    assertThat(resp.getPromoBoxStatus(), is(PromoBoxStatus.SUCCESS));
    assertThat(resp.getPromoBoxMessageKey(), is("whenSuccess"));
    assertThat(resp.getShowPromo(), is(true));
  }

  @Test
  void getPromoConfig_whenRateAndRoomInvalid_shouldReturnErrorMessage(){
    PromotionsConfig domainConfig = mockExpectedPromotionConfig();

    when(aemClient.getGlobalConfig(any()))
            .thenReturn(GlobalConfigDto.builder().build());
    when(promotionsConfigMapper.toDomainModel(nullable(PromotionsConfigDto.class)))
            .thenReturn(domainConfig);
    when(flowTypeResolver.resolve(any()))
            .thenReturn(PromoFlowType.LANDING_PAGE);
    when(strategyRegistry.get(PromoFlowType.LANDING_PAGE))
            .thenReturn(promoEvaluationStrategy);
    when(promoEvaluationStrategy.evaluate(any(), any()))
            .thenReturn(mockLandingPagePromo());

    var req = landingRequest("2025-12-12",
            "2026-02-16", "2026-02-18");
    req.setPromoKind(PromoKind.UNIQUE);
    req.setOperaPromoCode("ABCDEF");
    req.setRoomClass("ST");
    req.setRateName("FLEX");

    // Act
    PromotionsInformationResponse resp =
            contentOutPort.getPromoConfig(req);

    // Assert
    assertThat(resp.getShowPromo(), is(true));
    assertThat(resp.getErrorRateAndRoomMessage(),nullValue());

  }

  @ParameterizedTest(name = "[{index}] roomClass={0}, rateName={1}")
  @CsvSource({
          "AA,AAAA",
          "AA,FLEX",
          "ST,AAAA"
  })
  void getPromoConfig_whenRateOrRoomInvalid(String roomClass, String rateName){
    //Arrange
    var req = landingRequest("2025-12-12",
            "2026-02-16", "2026-02-18");
    req.setPromoKind(PromoKind.UNIQUE);
    req.setOperaPromoCode("ABCDEF");
    req.setRoomClass(roomClass);
    req.setRateName(rateName);
    req.setNoOfRooms(2);

    // Act
    when(aemClient.getGlobalConfig(any())).thenReturn(mockGlobalConfigDto());
    when(promotionsConfigMapper.toPromotionsInformationResponseModel(
            any(PromotionsInformationResponse.class),
            any()))
            .thenAnswer(invocation -> invocation.getArgument(0));
    PromotionsInformationResponse resp =
            contentOutPort.getPromoConfig(req);

    // Assert
   assertThat(resp.getShowPromo(), is(false));
    assertThat(resp.getPromoInvalidMessage(),
            is("No applicable promotions"));
    assertThat(resp.getErrorRateAndRoomMessage(), notNullValue());
  }

  private static PromotionsInformationResponse mockNoPromo() {
    return PromotionsInformationResponse.builder()
        .showPromo(false)
        .isWithinPromoWindow(false)
        .promoInvalidMessage("No applicable promotions")
        .build();
  }

  private static PromotionsInformationResponse mockSiteWidePromo() {
    return PromotionsInformationResponse.builder()
        .showPromo(true)
        .isWithinPromoWindow(true)
        .promoKind(PromoKind.SITE_WIDE)
        .promotionCode("ABCDEF")
        .build();
  }

  private static PromotionsInformationResponse mockLandingPagePromo() {
    return PromotionsInformationResponse.builder()
        .showPromo(true)
        .isWithinPromoWindow(true)
        .promotionCode("ABCDEF")
        .landingPage("/promo")
        .promoKind(PromoKind.LANDING_PAGE)
        .build();
  }

  private PromoConfigRequest landingRequest(String bookingDate,
      String stayStart, String stayEnd) {
    return PromoConfigRequest.builder()
        .promotionCode("ABCDEF")
        .bookingDate(bookingDate)
        .stayStartDate(stayStart)
        .stayEndDate(stayEnd)
        .build();
  }

  private ImportantInfo mockHotelImportantInfo() {
    return ImportantInfo.builder().title("Important Information").infoItems(mockHotelInfoItem())
        .build();
  }

  private List<InfoItem> mockHotelInfoItem() {
    InfoItem infoItem =
        InfoItem.builder().text("There is no air conditioning at this hotel.").priority("1")
            .startDate("17/09/2022")
            .endDate("20/10/2022")
            .hideOnHdp(true)
            .hideOnBookingFlow(false)
            .htmlText("There is no air conditioning at this hotel. <a href=\"/gb/en/faq/our-rooms.html\">Learn more about our rooms</a>")
            .build();

    return List.of(infoItem);
  }

  private uk.co.whitbread.content.domain.model.hotel.out.ImportantInfo mockImportantInfo() {
    return uk.co.whitbread.content.domain.model.hotel.out.ImportantInfo.builder()
        .title("Important Information")
        .infoItems(mockInfoItem()).build();
  }

  private List<uk.co.whitbread.content.domain.model.hotel.out.InfoItem> mockInfoItem() {
    uk.co.whitbread.content.domain.model.hotel.out.InfoItem infoItem =
        uk.co.whitbread.content.domain.model.hotel.out.InfoItem.builder()
            .text("There is no air conditioning at this hotel.").priority("1")
            .startDate("17/09/2022")
            .endDate("20/10/2022")
            .hideOnHdp(true)
            .hideOnBookingFlow(false)
            .build();

    return List.of(infoItem);
  }

  private AccessibilityInfoDto mockAccessibilityInfoDto() {
    return AccessibilityInfoDto.builder().header("headerTest")
        .text("All accessible rooms at this hotel are double rooms.").linkText("linkTextTest")
        .phoneNumber("3527934510")
        .build();
  }

  private AccessibilityInfo mockAccessibilityInfo() {
    return AccessibilityInfo.builder().header("headerTest")
        .text("All accessible rooms at this hotel are double rooms.")
        .linkText("linkTextTest").phoneNumber("3527934510").build();
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

  private HotelInformation mockSingleHotelInformation() {
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
    hotelInformation1.setCountryCodeISO("GB");
    hotelInformation1.setRestaurant(
        Restaurant.builder().logoSrc("logo-url/test.jpg").name("THYME").build());
    hotelInformation1.setRoomConfiguration(mockRoomConfiguration());
    hotelInformation1.setHotelOpeningDate("20-02-2022");
    hotelInformation1.setImportantInfo(mockImportantInfo());
    hotelInformation1.setAccessibilityInfo(mockAccessibilityInfo());
    hotelInformation1.setHotelFacilities(List.of(acoFacility, lftFacility));

    uk.co.whitbread.content.domain.model.hotel.out.AncillaryCloseout ancillaryCloseout =
        uk.co.whitbread.content.domain.model.hotel.out.AncillaryCloseout.builder()
            .noMealsHeading("No meals available")
            .noMealsMessage("Sorry, meals are not available for your selected dates")
            .items(of(
                uk.co.whitbread.content.domain.model.hotel.out.AncillaryCloseoutItem.builder()
                    .serviceCode("SE").build()))
            .build();

    hotelInformation1.setAncillaryCloseout(ancillaryCloseout);

    return hotelInformation1;
  }

  private HotelInformation mockHotelInformationWithFacilityCloseout() {
    var copFacility = HotelFacility.builder().code("COP").name("Parking").weight(10).isVisible(true).build();
    var dinFacility = HotelFacility.builder().code("DIN").name("Dinner").weight(20).isVisible(true).build();
    var lftFacility = HotelFacility.builder().code("LFT").name("Lift").weight(30).isVisible(true).build();

    var overlappingCloseout = FacilityCloseoutItem.builder()
        .startDate("01/10/2026")
        .endDate("31/12/2026")
        .facilityCodes(List.of("COP", "DIN"))
        .build();

    var nonOverlappingCloseout = FacilityCloseoutItem.builder()
        .startDate("01/01/2027")
        .endDate("31/01/2027")
        .facilityCodes(List.of("LFT"))
        .build();

    var hotelInformation = new HotelInformation();
    hotelInformation.setHotelId("LONEUS");
    hotelInformation.setHotelFacilities(List.of(copFacility, dinFacility, lftFacility));
    hotelInformation.setFacilityCloseout(FacilityCloseout.builder()
        .items(List.of(overlappingCloseout, nonOverlappingCloseout))
        .build());
    return hotelInformation;
  }

  private List<HotelShortInformationDto> mockAllHotelsShortInformation() {
    var loneusHSI = HotelShortInformationDto.builder()
        .code("LONEUS")
        .hotelPagePath("Slug")
        .build();
    var lonkinHSI = HotelShortInformationDto.builder()
        .code("LONKIN")
        .hotelPagePath("Slug")
        .build();

    return List.of(loneusHSI, lonkinHSI);
  }

  private HotelShortInformation mockHotelShortInformation() {
    return HotelShortInformation.builder()
        .code("LONEUS")
        .brand("PI")
        .title("Loneus")
        .hotelPagePath("Slug")
        .build();
  }


  private static void assertException(AemResponseException actual, ErrorCode errorCode,
      String expectedMessage) {
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(),
        is(errorCode.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(errorCode.getCode()));
  }

  private Map<String, String> mockExtrasAEMResponse() {
    Map<String, String> response = new HashMap<>();
    response.put("ancillaries.extras.HSCOU2.order", "2");
    response.put("ancillaries.extras.HSCKIN.name", "Early check-in");
    response.put("ancillaries.extras.HSCKIN.order", "1");
    response.put("ancillaries.extras.HSCOU2.name", "Late check-out");
    response.put("ancillaries.extras.DBPROS.name", "Bottle of prosecco");
    response.put("ancillaries.extras.DBPROS.order", "3");
    response.put("ancillaries.extras.list", "HSCKIN,HSCOU2,DBPROS");
    return response;
  }

  private void stubTripadvisorFlag(boolean enabled) {
    var featureFlag = new FeatureFlag();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(eq(unleashWrapper.featureFlag().getFetchTripadvisorFeedback()),
        any(UnleashContext.class))).thenReturn(enabled);
  }

  private  DlpInformationRequest mockDlpInformationRequest() {
    return DlpInformationRequest.builder()
            .dlpPath(DLP_PATH)
            .country(COUNTRY_GB)
            .language(LANGUAGE_EN)
            .build();
  }

  @Test
  void getPriceFinderGlobalConfig__ShouldReturnOk() {
    //Arrange
    when(aemClient.getPriceFinderGlobalConfig((any(PriceFinderGlobalConfigRequestDto.class)))).thenReturn(
            PriceFinderGlobalConfigDto.builder().build());

    when(priceFinderGlobalConfigMapper.toDomainModel(any(PriceFinderGlobalConfigDto.class))).thenReturn(
            mockPriceFinderGlobalConfig());
    when(priceFinderGlobalConfigMapper.toDto(any(PriceFinderGlobalConfigRequest.class))).thenReturn(
            PriceFinderGlobalConfigRequestDto.builder().build());

    //Act
    PriceFinderGlobalConfig response = contentOutPort.getPriceFinderGlobalConfig(
            PriceFinderGlobalConfigRequest.builder()
                    .country(COUNTRY_GB)
                    .language(LANGUAGE_EN)
                    .channelId(CHANNEL_ID)
                    .brand(BRAND)
                    .build());

    //Assert
    assertThat(response, equalTo(mockPriceFinderGlobalConfig()));
  }

  @Test
  void getPriceFinderConfig__ShouldReturnResourceNotFoundException() {
    String expectedMessage = "Unable to get price finder global config.";
    //Arrange

    PriceFinderGlobalConfigRequest priceFinderGlobalConfigRequest = PriceFinderGlobalConfigRequest.builder()
            .country(COUNTRY_GB)
            .language(LANGUAGE_EN)
            .channelId(CHANNEL_ID)
            .brand(BRAND)
            .build();


    when(priceFinderGlobalConfigMapper.toDto(priceFinderGlobalConfigRequest)).thenReturn(
            PriceFinderGlobalConfigRequestDto.builder().build());
    when(aemClient.getPriceFinderGlobalConfig(any())).thenThrow(
            new AemResponseException(AEM_PRICE_FINDER_CONFIG_EXCEPTION, expectedMessage, exception));



    //Act
    AemResponseException actual =
            assertThrows(AemResponseException.class, () -> contentOutPort.
                    getPriceFinderGlobalConfig(priceFinderGlobalConfigRequest));

    //Assert
    assertException(actual, AEM_PRICE_FINDER_CONFIG_EXCEPTION, expectedMessage);
  }

  private void mockRoomValidation(int minRooms, int maxRooms) {

    GlobalConfigDto dto = mockGlobalConfigDto();

    PromotionItemsDto promoItem = dto.getPromotionsConfig()
            .getPromoItems()
            .get(0);

    promoItem.setPromoCode("ABCDEF");
    promoItem.setMinRooms(minRooms);
    promoItem.setMaxRooms(maxRooms);
    promoItem.setRatePlanCode("ROOM_ONLY");
    promoItem.setRoomClass(List.of("STANDARD"));

    when(aemClient.getGlobalConfig(any())).thenReturn(dto);

    when(promoBoxResolver.resolvePreEvaluation(any()))
            .thenReturn(PromoBoxResolutionResult.nonTerminal());

    when(promotionsConfigMapper.toPromotionsInformationResponseModel(any(), any()))
            .thenAnswer(invocation -> invocation.getArgument(0));
  }

  private PromoConfigRequest buildRequest(int roomCount) {

    var req = landingRequest(
            "2025-12-12",
            "2026-02-16",
            "2026-02-18");

    req.setPromoKind(PromoKind.UNIQUE);
    req.setOperaPromoCode("ABCDEF");
    req.setNoOfRooms(roomCount);
    req.setRateName("ROOM_ONLY");
    req.setRoomClass("STANDARD");

    return req;
  }

  @Test
  void getPromoConfig_whenRoomCountExceedsMaxRooms() {

    //Arrange
    PromoConfigRequest req = buildRequest(6);

    mockRoomValidation(2, 5);

    //Act
    PromotionsInformationResponse response = contentOutPort.getPromoConfig(req);

    //Assert
    assertNotNull(response);
    assertFalse(response.getShowPromo());
    assertEquals(PromoBoxStatus.MAX_ROOMS_EXCEEDED, response.getPromoBoxStatus());
  }

  @Test
  void getPromoConfig_whenRoomCountLessThanMinRooms() {

    //Arrange
    PromoConfigRequest req = buildRequest(1);

    mockRoomValidation(2, 5);

    //Act
    PromotionsInformationResponse response = contentOutPort.getPromoConfig(req);

    //Assert
    assertNotNull(response);
    assertFalse(response.getShowPromo());
    assertEquals(PromoBoxStatus.MIN_ROOMS_NOT_MET, response.getPromoBoxStatus());
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
    return PriceFinderGlobalConfig.builder().priceFinderConfig(PriceFinderConfig.builder()
            .priceFinderViews(List.of(priceFinderViews1, priceFinderViews2)).build()).build();
  }
}
