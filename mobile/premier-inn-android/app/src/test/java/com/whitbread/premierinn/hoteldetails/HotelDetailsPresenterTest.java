package com.whitbread.premierinn.hoteldetails;

import static com.google.common.truth.Truth.assertThat;
import static com.whitbread.premierinn.domain.common.Constants.EMPLOYEE_RATE_PLAN_CODE;
import static com.whitbread.premierinn.domain.common.Constants.EMPTY_STRING_DOMAIN;
import static com.whitbread.premierinn.domain.common.Constants.OPERA_SOURCE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import androidx.annotation.NonNull;

import com.whitbread.premierinn.api.response.InstanceFactory;
import com.whitbread.premierinn.api.response.availability.Menu;
import com.whitbread.premierinn.api.response.availability.Restaurant;
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager;
import com.whitbread.premierinn.common.AppConfiguration;
import com.whitbread.premierinn.common.CheckInCheckOutStringProvider;
import com.whitbread.premierinn.common.RoomBooking;
import com.whitbread.premierinn.common.StringResourceProvider;
import com.whitbread.premierinn.common.analytics.FirebaseLogger;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.mapper.CommonMappersKt;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager;
import com.whitbread.premierinn.data.graphql.BookingInformationException;
import com.whitbread.premierinn.data.graphql.CreateReservationException;
import com.whitbread.premierinn.data.graphql.mapper.GraphQLCancelOnHoldReservationMapperKt;
import com.whitbread.premierinn.data.graphql.mapper.GraphQLHotelAvailabilityMapperKt;
import com.whitbread.premierinn.data.graphql.mapper.GraphQLHotelInfoMappersKt;
import com.whitbread.premierinn.data.graphql.mapper.GraphQLPackagesMapperKt;
import com.whitbread.premierinn.data.remote.graphql.contracts.CancelOnHoldReservationGraphQLContract;
import com.whitbread.premierinn.data.remote.graphql.contracts.HotelAvailabilityGraphQLContract;
import com.whitbread.premierinn.data.remote.graphql.contracts.HotelInfoGraphQLContract;
import com.whitbread.premierinn.data.remote.graphql.contracts.PackagesGraphQLContract;
import com.whitbread.premierinn.data.remote.graphql.contracts.PromotionsInformationGraphQLContract;
import com.whitbread.premierinn.data.remote.graphql.contracts.RoomClassConfigGraphQLContract;
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn;
import com.whitbread.premierinn.domain.common.Availability;
import com.whitbread.premierinn.domain.common.DailyRate;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.domain.common.RatePlanOpera;
import com.whitbread.premierinn.domain.common.RoomCriteria;
import com.whitbread.premierinn.domain.common.RoomOpera;
import com.whitbread.premierinn.domain.common.RoomType;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.AnnouncementDomain;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelInformationDomain;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.ImportantInfoDomain;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.InfoItem;
import com.whitbread.premierinn.domain.common.hoteldetails.usecase.GraphQLHotelDetailsUseCase;
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn;
import com.whitbread.premierinn.domain.customer.entity.AccessLevel;
import com.whitbread.premierinn.domain.graphql.common.usecase.GraphQLHoldBookingUseCase;
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelAvailabilityDomain;
import com.whitbread.premierinn.domain.graphql.hdp.entity.RateClassificationsDomain;
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomRateDomain;
import com.whitbread.premierinn.domain.graphql.hdp.usecase.GraphQLHDPUseCase;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HoldBookingRequestBody;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilityRequestBody;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomClassConfigRequestBody;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomSearch;
import com.whitbread.premierinn.domain.graphql.roomClassConfig.entity.RoomClassConfig;
import com.whitbread.premierinn.domain.graphql.roomClassConfig.usecase.GetRoomClassConfigUseCase;
import com.whitbread.premierinn.domain.hotel.entity.Hotel;
import com.whitbread.premierinn.domain.hotel.entity.HotelBookingAvailabilityState;
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository;
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource;
import com.whitbread.premierinn.domain.search.entity.Location;
import com.whitbread.premierinn.hoteldetails.event.ReadMoreClickEvent;
import com.whitbread.premierinn.hoteldetails.event.StartSummaryOrRoomSelectionEvent;
import com.whitbread.premierinn.hoteldetails.hdpOperaExtensions.HDPExtensionsKt;
import com.whitbread.premierinn.hoteldetails.hotelfulldescription.RoomRatePlan;
import com.whitbread.premierinn.hoteldetails.uimodel.FacilitiesUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.FullyBookedUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.ImportantInfoUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.MapUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.PageInfoUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.TabbedContentUiModel;
import com.whitbread.premierinn.searchresults.HotelListItem;
import com.whitbread.premierinn.searchresults.SearchResultsInput;
import com.whitbread.premierinn.summary.SummaryInput;
import com.whitbread.premierinn.utils.RxJavaTestRule;
import com.whitbread.premierinn.utils.model.CustomerFixture;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import kotlin.Pair;

@RunWith(MockitoJUnitRunner.class)
public class HotelDetailsPresenterTest {

    @Rule
    public RxJavaTestRule rxJavaTestRule = new RxJavaTestRule();

    private static final String HOTEL_CODE = "LONHMP";
    private static final String HOTEL_CODE_BANBRI = "BANBRI";
    private static final String SEARCH_QUERY = "NW3";
    private static final float LAT = 51.502460f;
    private static final float LON = -0.123291f;
    @Mock
    private TrackingAnalytics trackingAnalytics;
    @Mock
    private FirebaseLogger firebaseLogger;
    @Mock
    private HotelDetailsMessageProvider messageProvider;
    @Mock
    private ContentManagedResourceRepository contentManagedResourceRepository;
    @Mock
    private HotelDetailsPresenter.View view;
    @Mock
    private GetStringResource getStringResource;
    @Mock
    private LogService crashlyticsLogger;
    @Mock
    private SimplePersistenceManager storage;
    @Mock
    private BusinessPersistenceManager businessStorage;
    @Mock
    private StringResourceProvider stringResourceProviderMock;
    @Mock
    private IsFeatureOn isFeatureOn;

    @Mock
    private IsCustomerLoggedIn isCustomerLoggedIn;
    @Mock
    private DeviceLocaleProvider deviceLocaleProvider;
    @Mock
    private StringResourceProvider stringResourceProvider;
    @Mock
    private GraphQLHDPUseCase graphQLHDPUseCase;
    @Mock
    private GraphQLHotelDetailsUseCase graphQLHotelDetailsUseCase;
    @Mock
    private GetRoomClassConfigUseCase getRoomClassConfigUseCase;
    @Mock
    private GraphQLHoldBookingUseCase graphQLHoldBookingUseCase;
    @Mock
    private BathroomSelectionInput bathroomSelectionInput;
    @Mock
    private SummaryInput summaryInput;
    @Mock
    SelectedHotel selectedHotel;
    @Mock
    SelectedRate selectedRate;
    @Mock
    CheckInCheckOutStringProvider checkInCheckOutStringProvider;
    @Mock
    AppConfiguration configuration;
    @Mock
    SummaryInput.Builder summaryInputBuilder;

    private SearchResultsInput searchResultsInput;
    private HotelDetailsInput hotelDetailsInputFromDirectHotelSearch;
    private HotelDetailsInput hotelDetailsInputFromFrequentBooking;
    private HotelDetailsInput hotelDetailsInputFromSearchResults;
    private HotelInfoGraphQLContract.HotelInfoData hotelInfoGraphQLContractResponse;
    private HotelInfoGraphQLContract.HotelInfoData hotelInfoGraphQLContractResponseFailure;
    private HotelInfoGraphQLContract.HotelInfoData hotelInfoGraphQLContractResponseRestaurantNoDisclaimer;
    private HotelInfoGraphQLContract.HotelInfoData hotelInfoGraphQLContractResponseRestaurantNoMenus;
    private CancelOnHoldReservationGraphQLContract.CancelOnHoldReservationData cancelOnHoldReservationData;
    private HotelAvailabilityGraphQLContract.HotelAvailabilityData hotelAvailabilityDataResponse;
    private HotelAvailabilityGraphQLContract.HotelAvailabilityData hotelAvailabilityRatesAndRoomTypeDataResponse;
    private HotelAvailabilityGraphQLContract.HotelAvailabilityData hotelAvailabilityRatesWithPromoCodeAndRoomTypeDataResponse;
    private HotelAvailabilityGraphQLContract.HotelAvailabilityData hotelAvailabilityWithAccessibleRoomsDataResponse;
    private PromotionsInformationGraphQLContract.PromotionsInformationData promotionsInformationDataResponse;

    private RoomClassConfigGraphQLContract.RoomClassConfigData roomClassConfigSuccessResponse;
    RoomClassConfig mockRoomClassConfig = mock(RoomClassConfig.class);
    List<RoomClassConfig> mockRoomClassConfigList = singletonList(mockRoomClassConfig);
    private PackagesGraphQLContract.PackagesData packagesSuccessResponse;
    private PackagesGraphQLContract.PackagesData packagesWithNullResponse;
    private PackagesGraphQLContract.PackagesData packagesWithNullMealsWithExtrasesponse;

    @Before
    public void setUp() throws Exception {

        when(deviceLocaleProvider.getDeviceLocale()).thenReturn(Locale.UK);
        when(deviceLocaleProvider.getDeviceLanguage()).thenReturn("EN");
        when(deviceLocaleProvider.getCountryIfRegion(any())).thenReturn("GB");
        when(contentManagedResourceRepository
                .getStringSingle(ContentManagedResourceRepository.Key.ALL_CHECK_IN_CHECK_OUT_TIMES.getValue()))
                .thenReturn(Single.just(
                        "{\"ukCheckInTimes\":{\"bookingDetailsCheckInInfo\": \"from 3pm\",\"bookingDetailsCheckOutInfo\": \"before 12pm\","
                                + "\"summaryOrPaymentBreakdownCheckInInfo\": \"Check-in at 3:00pm\","
                                + "\"summaryOrPaymentBreakdownCheckOutInfo\": \"Check-out by 12:00pm\"},"
                                + "\"germanyCheckInTimes\": {\"bookingDetailsCheckInInfo\": \"after 6pm\","
                                + "\"bookingDetailsCheckOutInfo\": \"before 3pm\","
                                + "\"summaryOrPaymentBreakdownCheckInInfo\": \"Check-in at 6:00pm\","
                                + "\"summaryOrPaymentBreakdownCheckOutInfo\": \"Check-out by 3:00pm\"}}"));

        when(storage.getFreeBreakfastPromotionCode()).thenReturn(EMPTY_STRING_DOMAIN);

        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(true));

        //default values of inputs
        searchResultsInput = SearchResultsInput.builder()
                .placeName(SEARCH_QUERY) //search query
                .latitude(LAT).longitude(LON)
                .arrivalDate(LocalDate.of(2018, 3, 1))
                .departureDate(LocalDate.of(2018, 3, 2))
                .numRooms(1)
                .adults(new ArrayList<>(singletonList(1)))
                .children(new ArrayList<>(singletonList(0)))
                .infants(new ArrayList<>(singletonList(0)))
                .cots(new ArrayList<>(singletonList(false)))
                .roomTypeCodes(new ArrayList<>(singletonList(RoomType.DOUBLE.name())))
                .build();

        HotelListItem hotelListItem = new HotelListItem("London Something", "LONHMP", Hotel.Brand.PI,
                new Location(LAT, LON), 1.0d, "1.0 miles", "", null, OPERA_SOURCE, EMPTY_STRING_DOMAIN, null,
                Availability.OK, null, "120", null, 0);

        hotelDetailsInputFromSearchResults = HotelDetailsInput.fromSearchResults(hotelListItem, searchResultsInput, false)
                .hotelCode(HOTEL_CODE_BANBRI)
                .hotelName("Bangor (Gwynedd, North Wales)")
                .brand("PI")
                .hotelImageUrl("/content/dam/pi/websites/hotelimages/gb/en/M/BANBRI/BANBRI 1.jpg")
                .build();

        hotelDetailsInputFromDirectHotelSearch = HotelDetailsInput.builder()
                .searchResultsInput(searchResultsInput)
                .hotelName(searchResultsInput.placeName())
                .brand("PI")
                .distanceFromSearchedLocation(0f)
                .cameFromMapView(false)
                .hotelCode(HOTEL_CODE_BANBRI)
                .build();

        hotelDetailsInputFromFrequentBooking = HotelDetailsInput.fromFrequentBooking(HOTEL_CODE).build();

        hotelInfoGraphQLContractResponse = InstanceFactory.create(HotelInfoGraphQLContract.HotelInfoData.class,
                "apiTest/graphql/hotel_info_gql.json");
        hotelInfoGraphQLContractResponseFailure = InstanceFactory.create(HotelInfoGraphQLContract.HotelInfoData.class,
                "apiTest/graphql/hotel_info_failure_with_null_gql.json");
        hotelInfoGraphQLContractResponseRestaurantNoDisclaimer = InstanceFactory.create(HotelInfoGraphQLContract.HotelInfoData.class,
                "apiTest/graphql/hotel_info_restaurant_no_disclaimer_gql.json");
        hotelInfoGraphQLContractResponseRestaurantNoMenus = InstanceFactory.create(HotelInfoGraphQLContract.HotelInfoData.class,
                "apiTest/graphql/hotel_info_restaurant_no_menu_gql.json");
        hotelAvailabilityDataResponse = InstanceFactory.create(HotelAvailabilityGraphQLContract.HotelAvailabilityData.class,
                "apiTest/graphql/hotel_availability_gql.json");
        hotelAvailabilityRatesAndRoomTypeDataResponse = InstanceFactory.create(
                HotelAvailabilityGraphQLContract.HotelAvailabilityData.class,
                "apiTest/graphql/hotel_availability_and_rates_info_and_roomtypes_gql.json");
        hotelAvailabilityRatesWithPromoCodeAndRoomTypeDataResponse = InstanceFactory.create(
                HotelAvailabilityGraphQLContract.HotelAvailabilityData.class,
                "apiTest/graphql/hotel_availability_and_rates_with_promoCode_info_and_roomtypes_gql.json");
        hotelAvailabilityWithAccessibleRoomsDataResponse = InstanceFactory.create(
                HotelAvailabilityGraphQLContract.HotelAvailabilityData.class,
                "apiTest/graphql/hotel_availability_with_accessible_rooms_gql.json");
        cancelOnHoldReservationData = InstanceFactory.create(CancelOnHoldReservationGraphQLContract.CancelOnHoldReservationData.class,
                "apiTest/graphql/cancel_on_hold_reservation_gql.json");
        roomClassConfigSuccessResponse = InstanceFactory.create(
                RoomClassConfigGraphQLContract.RoomClassConfigData.class,
                "apiTest/graphql/room_class_config_success_gql.json"
        );
        packagesSuccessResponse = InstanceFactory.create(
                PackagesGraphQLContract.PackagesData.class,
                "apiTest/graphql/packages_success_gql.json"
        );
        packagesWithNullResponse = InstanceFactory.create(
                PackagesGraphQLContract.PackagesData.class,
                "apiTest/graphql/packages_null_gql.json"
        );
        packagesWithNullMealsWithExtrasesponse = InstanceFactory.create(
                PackagesGraphQLContract.PackagesData.class,
                "apiTest/graphql/packages_null_extras_not_null_gql.json"
        );
        promotionsInformationDataResponse = InstanceFactory.create(
            PromotionsInformationGraphQLContract.PromotionsInformationData.class,
            "apiTest/graphql/promotions_information_gql.json"
        );

        mockRoomClassConfigList = roomClassConfigSuccessResponse
                .getData()
                .getRoomClassConfig()
                .getRoomClassConfigItems()
                .stream()
                .map(item -> new RoomClassConfig(item.getCode(), item.getOrder()))
                .collect(Collectors.toList());

        // Mock the new HDP promotions+availability chain to always return a valid Observable
        when(graphQLHDPUseCase.fetchPromotionsAndHotelAvailability(any(), any(), any(), any(), any(), any(), anyBoolean()))
                .thenReturn(Observable.just(new HotelBookingAvailabilityState(
                        GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess.INSTANCE,
                        null, null, null, EMPTY_STRING_DOMAIN, false, null, null
                )));
    }

    @Test
    public void whenHotelCallHDPUseCase() {
        mockSuccessfulHotelInfoAndAvailability();

        createInstanceFrom(hotelDetailsInputFromSearchResults);

        verify(graphQLHotelDetailsUseCase).fetchHotelInfoFromGQL(anyString(), anyString(), anyString());
        verify(graphQLHDPUseCase).fetchPromotionsAndHotelAvailability(any(), any(), any(), any(), any(), any(), anyBoolean());
    }

    @Test
    public void showToolbarTitles_when_data_comes_from_input1() {
        //given
        when(messageProvider.getCommonStringsProvider()).thenReturn(stringResourceProviderMock);
        when(stringResourceProviderMock.formattedInputSearchCriteria(searchResultsInput)).thenReturn("someOperaText");
        mockSuccessfulHotelInfoAndAvailability();

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        //when
        presenter.bindToolbarTitles(view);

        //then
        verify(view).showToolBarTitle("Bangor (Gwynedd, North Wales)");
        verifyNoMoreInteractions(view);
    }

    @Test
    public void showToolbarTitles_when_data_comes_from_input2() {
        //given
        when(messageProvider.getCommonStringsProvider()).thenReturn(stringResourceProviderMock);
        when(stringResourceProviderMock.formattedInputSearchCriteria(searchResultsInput)).thenReturn("someOperaText");
        mockSuccessfulHotelInfoAndAvailability();

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromDirectHotelSearch);

        //when
        presenter.bindToolbarTitles(view);

        //then
        verify(view).showToolBarTitle(SEARCH_QUERY);
        verifyNoMoreInteractions(view);
    }

    @Test
    public void hotelBrandIsSetCorrectlyDuringConstruction() {
        // Given
        mockSuccessfulHotelInfoAndAvailability();

        // When
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromDirectHotelSearch);

        // Then - verify brand is set from input
        assertThat(presenter.hotelBrand).isEqualTo(Hotel.Brand.PI);
    }

    @Test
    public void showFacilitiesWhenRetrieved() {
        mockSuccessfulHotelInfoAndAvailability();
        when(messageProvider.shortDescription(any())).thenReturn("Some test description");

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromDirectHotelSearch);

        presenter.bindTopFacilitiesUiModel(view);

        verify(view).addUiModelToRecyclerView(any());
    }

    @Test
    public void doNotShowFacilities_whenFacilitiesIsEmpty() {
        mockHotelInfoNoFacilitiesWithAvailabilityAndPackagesSuccess();

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromDirectHotelSearch);

        presenter.bindTopFacilitiesUiModel(view);

        verifyNoMoreInteractions(view);
    }

    @Test
    public void given_rateplan_convertToRoomRate() {
        mockSuccessfulHotelInfoAndAvailability();

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);
        ArrayList<RatePlanOpera> mockRatePlan = new ArrayList<>();

        ArrayList<DailyRate> mockDailyRates = new ArrayList<>(Arrays.asList(
                new DailyRate(LocalDate.of(2022, 10, 20), new PriceDomain(999.0f, "GBP")),
                new DailyRate(LocalDate.of(2022, 10, 21), new PriceDomain(999.0f, "GBP")),
                new DailyRate(LocalDate.of(2022, 10, 22), new PriceDomain(999.0f, "GBP"))
        ));
        ArrayList<RoomOpera> operaRoomsStd = new ArrayList<>();
        ArrayList<RoomOpera> operaRoomsAlt = new ArrayList<>();
        List<String> specialRequests = Arrays.asList("SING1");
        RoomOpera mockRooms = new RoomOpera(1, RoomType.DOUBLE,
                "DOUBLE", new PriceDomain(999.0f, "GBP"),
                PriceDomain.Companion.createDefault(),
                "FLEXRATE", false, 1, 0, 0, mockDailyRates, null, specialRequests);

        RoomOpera mockAltRooms =  new RoomOpera(1, RoomType.DOUBLE,
                "DOUBLE", new PriceDomain(999.0f, "GBP"),
                PriceDomain.Companion.createDefault(),
                "FLEXRATE", false, 1, 0, 0, mockDailyRates, null, specialRequests);
        operaRoomsStd.add(mockRooms);
        operaRoomsAlt.add(mockAltRooms);

        RatePlanOpera rateplan = new RatePlanOpera("FLEXRATE",
                "FLEXRATE", EMPTY_STRING_DOMAIN, new PriceDomain(2997.0f, "GBP"),
                PriceDomain.Companion.createDefault(),
                operaRoomsStd, operaRoomsAlt, new ArrayList<>(), new ArrayList<>());

        mockRatePlan.add(rateplan);

        ArrayList<RateClassificationsDomain> listOfClassificationDomain = new ArrayList<>();
        listOfClassificationDomain.add(new RateClassificationsDomain("FLEXRATE", "1", "Flex",
                "Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival", "",
                "", emptyList()));

        presenter.listOfRateClassification = listOfClassificationDomain;

        List<RoomRatePlan> roomRatePlans = presenter.mapToRoomRatePlans(mockRatePlan);

        Assert.assertEquals(roomRatePlans.size(), 2);
        Assert.assertEquals(roomRatePlans.get(0).getRoomRate().rateType(), "FLEXRATE");
        Assert.assertEquals(roomRatePlans.get(0).getRoomRate().rateName(), "Flex");
        Assert.assertEquals(roomRatePlans.get(0).getRoomRate().classification(), "FLEXRATE");
    }

    @Test
    public void showHotelHeadingFromNetwork() {
        //given
        mockSuccessfulHotelInfoAndAvailability();
        when(messageProvider.getCommonStringsProvider()).thenReturn(stringResourceProviderMock);
        when(stringResourceProviderMock.formattedSearchCriteria(searchResultsInput)).thenReturn(new Pair<>("some text", "some text"));

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromDirectHotelSearch);

        //when
        presenter.bindHotelHeadingUiModelFromNetwork(view);

        //then
        verify(view).addUiModelToRecyclerView(any());
        verifyNoMoreInteractions(view);
    }

    @Test
    public void doNotShowHotelHeadingFromNetwork() {
        //given
        mockHotelInfoFailureWithAvailabilityAndPackageSuccess();

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromDirectHotelSearch);

        //when
        presenter.bindHotelHeadingUiModelFromNetwork(view);

        //then
        verifyNoInteractions(view);
    }

    @Test
    public void showMapInformation_whenMapDataIsAvailable() {
        mockSuccessfulHotelInfoAndAvailability();
        when(messageProvider.shortDescription(any())).thenReturn("Some test short directions");

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromDirectHotelSearch);

        presenter.bindMapUIModel(view);
        verify(view).addUiModelToRecyclerView(any(MapUiModel.class));
    }

    @Test
    public void startAboutThisHotelBottomSheetFragment_onCLickingReadMore() {
        when(view.onClickReadMore()).thenReturn(Observable.just(new ReadMoreClickEvent(3)));
        when(messageProvider.hotelDescription(any())).thenReturn("Mock hotel description");
        when(messageProvider.hotelDirections(any())).thenReturn("Mock hotel directions");
        when(messageProvider.parkingDescriptionFromHtml(any())).thenReturn("Mock parking description");

        mockSuccessfulHotelInfoAndAvailability();

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        presenter.bindReadMoreClicks(view);

        verify(view).onClickReadMore();
        verify(view).showAboutThisHotelBottomSheetFragment(any());
        verifyNoMoreInteractions(view);
    }

    @Test
    public void startHDP_with_isAppPromotionalIncentiveActive_true_then_runAppIncentivePromo() {
        String promoCode = "AAAA";
        when(isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED)).thenReturn(true);
        when(storage.isAppPromotionalIncentiveAvailable()).thenReturn(true);

        //App incentive takes precedence over free breakfast so no need to mock free breakfast here

        when(getStringResource.invoke(ContentManagedResourceRepository.Key.APP_PROMO_CODE)).thenReturn(promoCode);

        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt
                        .mapToHotelInformationGQL(hotelInfoGraphQLContractResponse)));

        HotelAvailabilityDomain hotelAvailabilityDomain =
                GraphQLHotelAvailabilityMapperKt.
                        mapToHotelAvailabilityGQL(hotelAvailabilityRatesWithPromoCodeAndRoomTypeDataResponse, null);
        GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess success =
                GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess.INSTANCE;

        when(graphQLHDPUseCase.fetchPromotionsAndHotelAvailability(any(), any(), any(), any(), any(), any(), anyBoolean()))
                .thenReturn(Observable.just(new HotelBookingAvailabilityState(success,
                        hotelAvailabilityDomain,
                        hotelAvailabilityDomain.getListOfRatesClassification(),
                        hotelAvailabilityDomain.getListOfRoomTypeInfo(), promoCode, false, null, null)));

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        boolean isPromoRateAvailable =
                hotelAvailabilityDomain
                        .getRoomRateDomainList()
                        .stream()
                        .anyMatch(item -> Objects.equals(item.getPromotionCode(), promoCode));

        verify(graphQLHotelDetailsUseCase).fetchHotelInfoFromGQL(anyString(), anyString(), anyString());
        verify(graphQLHDPUseCase).fetchPromotionsAndHotelAvailability(any(), any(), any(), any(), any(), any(), anyBoolean());
        Assert.assertTrue(isPromoRateAvailable);
        Assert.assertEquals(promoCode, presenter.promoCode);
    }

    @Test
    public void startHDP_with_AppPromoIncentiveEnabled_false_and_isFreeBreakfastPromotionActive_false_then_doNotRunAppIncentivePromo() {
        when(isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED)).thenReturn(false);

        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt
                        .mapToHotelInformationGQL(hotelInfoGraphQLContractResponse)));

        HotelAvailabilityDomain hotelAvailabilityDomain =
                GraphQLHotelAvailabilityMapperKt.
                        mapToHotelAvailabilityGQL(hotelAvailabilityRatesAndRoomTypeDataResponse, null);
        GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess success =
                GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess.INSTANCE;
        when(graphQLHDPUseCase.fetchPromotionsAndHotelAvailability(any(), any(), any(), any(), any(), any(), anyBoolean()))
                .thenReturn(Observable.just(new HotelBookingAvailabilityState(success,
                        hotelAvailabilityDomain,
                        hotelAvailabilityDomain.getListOfRatesClassification(),
                        hotelAvailabilityDomain.getListOfRoomTypeInfo(), EMPTY_STRING_DOMAIN, false, null, null)));

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);


        verify(graphQLHotelDetailsUseCase).fetchHotelInfoFromGQL(anyString(), anyString(), anyString());
        verify(graphQLHDPUseCase).fetchPromotionsAndHotelAvailability(any(), any(), any(), any(), any(), any(), anyBoolean());

        Assert.assertEquals(EMPTY_STRING_DOMAIN, presenter.promoCode);
    }

    @Test
    public void startHDP_with_AppPromoIncentiveAvailable_false_and_isFreeBreakfastPromotionActive_false_then_doNotRunAppIncentivePromo() {
        when(isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED)).thenReturn(true);
        when(storage.isAppPromotionalIncentiveAvailable()).thenReturn(false);

        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt
                        .mapToHotelInformationGQL(hotelInfoGraphQLContractResponse)));

        HotelAvailabilityDomain hotelAvailabilityDomain =
                GraphQLHotelAvailabilityMapperKt.
                        mapToHotelAvailabilityGQL(hotelAvailabilityRatesAndRoomTypeDataResponse, null);
        GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess success =
                GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess.INSTANCE;
        when(graphQLHDPUseCase.fetchPromotionsAndHotelAvailability(any(), any(), any(), any(), any(), any(), anyBoolean()))
                .thenReturn(Observable.just(new HotelBookingAvailabilityState(success,
                        hotelAvailabilityDomain,
                        hotelAvailabilityDomain.getListOfRatesClassification(),
                        hotelAvailabilityDomain.getListOfRoomTypeInfo(), EMPTY_STRING_DOMAIN, false, null, null)));

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);


        verify(graphQLHotelDetailsUseCase).fetchHotelInfoFromGQL(anyString(), anyString(), anyString());
        verify(graphQLHDPUseCase).fetchPromotionsAndHotelAvailability(any(), any(), any(), any(), any(), any(), anyBoolean());

        Assert.assertEquals(EMPTY_STRING_DOMAIN, presenter.promoCode);
    }

    @Test
    public void startHDP_with_isAppPromotionalIncentiveActive_false_and_isFreeBreakfastPromotionActive_true_then_runFreeBreakfastPromo() {
        String promoCode = "AAAA";
        when(isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED)).thenReturn(false);

        when(storage.getFreeBreakfastPromotionCode()).thenReturn(promoCode);

        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt
                        .mapToHotelInformationGQL(hotelInfoGraphQLContractResponse)));

        HotelAvailabilityDomain hotelAvailabilityDomain =
                GraphQLHotelAvailabilityMapperKt.
                        mapToHotelAvailabilityGQL(hotelAvailabilityRatesWithPromoCodeAndRoomTypeDataResponse, null);
        GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess success =
                GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess.INSTANCE;
        when(graphQLHDPUseCase.fetchPromotionsAndHotelAvailability(any(), any(), any(), any(), any(), any(), anyBoolean()))
                .thenReturn(Observable.just(new HotelBookingAvailabilityState(success,
                        hotelAvailabilityDomain,
                        hotelAvailabilityDomain.getListOfRatesClassification(),
                        hotelAvailabilityDomain.getListOfRoomTypeInfo(), promoCode, false, null, null)));

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        boolean isPromoRateAvailable =
                hotelAvailabilityDomain
                        .getRoomRateDomainList()
                        .stream()
                        .anyMatch(item -> Objects.equals(item.getPromotionCode(), promoCode));

        verify(graphQLHotelDetailsUseCase).fetchHotelInfoFromGQL(anyString(), anyString(), anyString());
        verify(graphQLHDPUseCase).fetchPromotionsAndHotelAvailability(any(), any(), any(), any(), any(), any(), anyBoolean());
        Assert.assertTrue(isPromoRateAvailable);
        Assert.assertEquals(promoCode, presenter.promoCode);
    }

    @Test
    public void startHDP_with_AppPromoIncentiveEnabled_false_and_AppPromoIncentiveAvailable_true_then_runFreeBreakfastPromo() {
        String promoCode = "AAAA";
        when(isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED)).thenReturn(false);

        lenient().when(storage.isAppPromotionalIncentiveAvailable()).thenReturn(true);

        when(storage.getFreeBreakfastPromotionCode()).thenReturn(promoCode);

        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt
                        .mapToHotelInformationGQL(hotelInfoGraphQLContractResponse)));

        HotelAvailabilityDomain hotelAvailabilityDomain =
                GraphQLHotelAvailabilityMapperKt.
                        mapToHotelAvailabilityGQL(hotelAvailabilityRatesWithPromoCodeAndRoomTypeDataResponse, null);
        GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess success =
                GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess.INSTANCE;
        when(graphQLHDPUseCase.fetchPromotionsAndHotelAvailability(any(), any(), any(), any(), any(), any(), anyBoolean()))
                .thenReturn(Observable.just(new HotelBookingAvailabilityState(success,
                        hotelAvailabilityDomain,
                        hotelAvailabilityDomain.getListOfRatesClassification(),
                        hotelAvailabilityDomain.getListOfRoomTypeInfo(), promoCode, false, null, null)));

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        boolean isPromoRateAvailable =
                hotelAvailabilityDomain
                        .getRoomRateDomainList()
                        .stream()
                        .anyMatch(item -> Objects.equals(item.getPromotionCode(), promoCode));

        verify(graphQLHotelDetailsUseCase).fetchHotelInfoFromGQL(anyString(), anyString(), anyString());
        verify(graphQLHDPUseCase).fetchPromotionsAndHotelAvailability(any(), any(), any(), any(), any(), any(), anyBoolean());
        Assert.assertTrue(isPromoRateAvailable);
        Assert.assertEquals(promoCode, presenter.promoCode);
    }

    @Test
    public void startHDP_with_AppPromoIncentiveEnabled_true_and_AppPromoIncentiveAvailable_false_then_runFreeBreakfastPromo() {
        String promoCode = "AAAA";
        when(isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED)).thenReturn(true);
        when(storage.isAppPromotionalIncentiveAvailable()).thenReturn(false);

        when(storage.getFreeBreakfastPromotionCode()).thenReturn(promoCode);

        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt
                        .mapToHotelInformationGQL(hotelInfoGraphQLContractResponse)));

        HotelAvailabilityDomain hotelAvailabilityDomain =
                GraphQLHotelAvailabilityMapperKt.
                        mapToHotelAvailabilityGQL(hotelAvailabilityRatesWithPromoCodeAndRoomTypeDataResponse, null);
        GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess success =
                GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess.INSTANCE;
        when(graphQLHDPUseCase.fetchPromotionsAndHotelAvailability(any(), any(), any(), any(), any(), any(), anyBoolean()))
                .thenReturn(Observable.just(new HotelBookingAvailabilityState(success,
                        hotelAvailabilityDomain,
                        hotelAvailabilityDomain.getListOfRatesClassification(),
                        hotelAvailabilityDomain.getListOfRoomTypeInfo(), promoCode, false, null, null)));

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        boolean isPromoRateAvailable =
                hotelAvailabilityDomain
                        .getRoomRateDomainList()
                        .stream()
                        .anyMatch(item -> Objects.equals(item.getPromotionCode(), promoCode));

        verify(graphQLHotelDetailsUseCase).fetchHotelInfoFromGQL(anyString(), anyString(), anyString());
        verify(graphQLHDPUseCase).fetchPromotionsAndHotelAvailability(any(), any(), any(), any(), any(), any(), anyBoolean());
        Assert.assertTrue(isPromoRateAvailable);
        Assert.assertEquals(promoCode, presenter.promoCode);
    }

    @Test
    public void bindRoomClassConfig_withSuccessResponse() {
        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt
                        .mapToHotelInformationGQL(hotelInfoGraphQLContractResponse)));
        when(graphQLHDPUseCase.fetchPromotionsAndHotelAvailability(any(), any(), any(), any(), any(), any(), anyBoolean()))
                .thenReturn(Observable.just(mock(HotelBookingAvailabilityState.class)));
        when(getRoomClassConfigUseCase.fetchRoomClassConfig(any(RoomClassConfigRequestBody.class)))
                .thenReturn(Observable.just(mockRoomClassConfigList));
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromDirectHotelSearch);

        presenter.bindRoomClassConfig();

        Assert.assertEquals(mockRoomClassConfigList, presenter.sortedRoomClassConfigs);
    }

    @Test
    public void bindRoomClassConfig_withErrorResponse() {
        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt.mapToHotelInformationGQL(hotelInfoGraphQLContractResponse)));
        when(graphQLHDPUseCase.fetchPromotionsAndHotelAvailability(any(), any(), any(), any(), any(), any(), anyBoolean()))
                .thenReturn(Observable.just(mock(HotelBookingAvailabilityState.class)));
        RuntimeException error = new RuntimeException("Network error");
        when(getRoomClassConfigUseCase.fetchRoomClassConfig(any(RoomClassConfigRequestBody.class)))
                .thenReturn(Observable.error(error));
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromDirectHotelSearch);

        presenter.bindRoomClassConfig();

        verify(crashlyticsLogger).log("Error loading RoomClassConfig: " + error);
    }

    @Test
    public void bindReleaseBooking() {
        //given
        mockSuccessfulHotelInfoAndAvailability();
        mockCancelOnHoldReservationSuccess();
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);
        presenter.bindHotelCode();

        Assert.assertNotNull(presenter);

        //when
        presenter.bindReleaseBooking(view, "AXR-8E23F668-0884-67AI-67RT-3CHSJFLSB");

        //then
        verify(graphQLHDPUseCase).cancelOnHoldReservation(anyString(), anyString());
        verify(view).reBindBookingAvailabilitySubscriptions();
    }

    @Test
    public void displayAllRateTypesAreMappedFromAvailabilityResponse() {
        mockSuccessfulHotelInfoAndAvailability();

        createInstanceFrom(hotelDetailsInputFromSearchResults);

        HotelAvailabilityDomain hotelAvailabilityDomain =
                GraphQLHotelAvailabilityMapperKt.
                        mapToHotelAvailabilityGQL(hotelAvailabilityRatesAndRoomTypeDataResponse, null);

        // Then - verify the use cases were called during presenter creation
        verify(graphQLHotelDetailsUseCase).fetchHotelInfoFromGQL(anyString(), anyString(), anyString());
        verify(graphQLHDPUseCase).fetchPromotionsAndHotelAvailability(any(), any(), any(), any(), any(), any(), anyBoolean());

        // Verify room rates from availability domain
        List<RoomRateDomain> roomRates = hotelAvailabilityDomain.getRoomRateDomainList();
        assertThat(roomRates).isNotNull();
        assertThat(roomRates).isNotEmpty();
        assertThat(roomRates).hasSize(3);

        // Verify all rate types (rate plan codes) in order
        List<String> actualRatePlanCodes = roomRates.stream()
                .map(RoomRateDomain::getRatePlanCode)
                .collect(Collectors.toList());

        assertThat(actualRatePlanCodes).containsExactly("FLEXRATE", "ADVANCE", "STDDISAP");
    }

    @Test
    public void bindRatePlanOrFullyBookedUiModelShowsFullyBookedWhenNoRatesAvailable() {
        // Given
        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt
                        .mapToHotelInformationGQL(hotelInfoGraphQLContractResponse)));

        HotelAvailabilityDomain emptyAvailability = mock(HotelAvailabilityDomain.class);

        when(graphQLHDPUseCase.fetchPromotionsAndHotelAvailability(any(), any(), any(), any(), any(), any(), anyBoolean()))
                .thenReturn(Observable.just(new HotelBookingAvailabilityState(
                        GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess.INSTANCE,
                        emptyAvailability, emptyList(), emptyList(), EMPTY_STRING_DOMAIN, false, null, null)));

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        // When
        Disposable disposable = presenter.bindRatePlanOrFullyBookedUiModel(view);

        // Then - verify FullyBookedUiModel with FULLY_BOOKED type is added
        assertThat(disposable).isNotNull();
        verify(view).addUiModelToRecyclerView(argThat(uiModel ->
                uiModel instanceof FullyBookedUiModel
                        && ((FullyBookedUiModel) uiModel).type() == FullyBookedUiModel.Type.FULLY_BOOKED
        ));
        disposable.dispose();
    }

    @Test
    public void lookUpClassificationReturnsCorrectRatePlanForFlexRate() {
        mockSuccessfulHotelInfoAndAvailability();
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);
        HotelAvailabilityDomain hotelAvailabilityDomain =
                GraphQLHotelAvailabilityMapperKt.mapToHotelAvailabilityGQL(hotelAvailabilityRatesAndRoomTypeDataResponse, null);

        RoomRateDomain result = presenter.lookUpClassification("FLEXRATE", hotelAvailabilityDomain);

        // Verify FLEXRATE exists and has correct data from mock
        assertThat(result).isNotNull();
        assertThat(result.getRatePlanCode()).isEqualTo("FLEXRATE");
        assertThat(result.getRoomTypesDomainList()).isNotEmpty();
        assertThat(result.getRoomTypesDomainList()).hasSize(1);
        assertThat(result.getRoomTypesDomainList().get(0).getRoomType()).isEqualTo("DB");
    }

    @Test
    public void lookUpClassificationReturnsStandardRateWithAccessibleRoom() {
        // Given - availability with accessible rooms
        mockSuccessfulHotelInfoAndAvailability();
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);
        HotelAvailabilityDomain hotelAvailabilityDomain =
                GraphQLHotelAvailabilityMapperKt.mapToHotelAvailabilityGQL(hotelAvailabilityWithAccessibleRoomsDataResponse, null);

        // When
        RoomRateDomain result = presenter.lookUpClassification("STANDARD", hotelAvailabilityDomain);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRatePlanCode()).isEqualTo("STANDARD");
        assertThat(result.getRoomTypesDomainList()).isNotEmpty();
        assertThat(result.getRoomTypesDomainList()).hasSize(2); // DIS (accessible) and DB (double)
        // Verify DIS (accessible) room type - triggers "Select" button instead of "Book"
        assertThat(result.getRoomTypesDomainList().get(0).getRoomType()).isEqualTo("DIS");
        // Verify accessible room has LOWDBL pmsRoomType (lowered bath)
        assertThat(result.getRoomTypesDomainList().get(0).getRoomOptionsDomainList().get(0).getPmsRoomType()).isEqualTo("LOWDBL");
    }

    @Test
    public void lookUpClassificationReturnsNullWhenRatePlanNotFound() {
        // Given
        mockSuccessfulHotelInfoAndAvailability();
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);
        HotelAvailabilityDomain hotelAvailabilityDomain =
                GraphQLHotelAvailabilityMapperKt.mapToHotelAvailabilityGQL(hotelAvailabilityRatesAndRoomTypeDataResponse, null);

        // When - search for a rate plan that doesn't exist in mock data(flexrate, advance, stddisap)
        RoomRateDomain result = presenter.lookUpClassification("NONEXISTENT_RATE", hotelAvailabilityDomain);

        // Then
        assertThat(result).isNull();
    }

    @Test
    public void lookUpClassificationReturnsNullWhenRatePlanCodeIsEmpty() {
        // Given
        mockSuccessfulHotelInfoAndAvailability();
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);
        HotelAvailabilityDomain hotelAvailabilityDomain =
                GraphQLHotelAvailabilityMapperKt.mapToHotelAvailabilityGQL(hotelAvailabilityRatesAndRoomTypeDataResponse, null);

        // When - search with empty rate plan code
        RoomRateDomain result = presenter.lookUpClassification("", hotelAvailabilityDomain);

        // Then
        assertThat(result).isNull();
    }

    @Test
    public void lookUpClassificationReturnsNullWhenHotelAvailabilityIsNull() {
        // Given
        mockSuccessfulHotelInfoAndAvailability();
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        // When
        RoomRateDomain result = presenter.lookUpClassification("FLEXRATE", null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    public void lookUpClassificationReturnsEmployeeRatePlanWhenClassificationIsEmployeeAndCellCodeMatches() {
        // Given
        mockSuccessfulHotelInfoAndAvailability();
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        // Create mock HotelAvailabilityDomain with employee rate
        RoomRateDomain employeeRatePlan = mock(RoomRateDomain.class);
        when(employeeRatePlan.getCellCode()).thenReturn("EMP01"); // EMPLOYEE_CODE

        HotelAvailabilityDomain hotelAvailabilityDomain = mock(HotelAvailabilityDomain.class);
        when(hotelAvailabilityDomain.getRoomRateDomainList()).thenReturn(singletonList(employeeRatePlan));

        // When - search with EMPLOYEE classification
        RoomRateDomain result = presenter.lookUpClassification("EMPLOYEE", hotelAvailabilityDomain);

        // Then - should return the employee rate plan based on cellCode match
        assertThat(result).isNotNull();
        assertThat(result.getCellCode()).isEqualTo("EMP01");
    }

    @Test
    public void lookUpClassificationReturnsNullWhenClassificationIsEmployeeButCellCodeDoesNotMatch() {
        // Given
        mockSuccessfulHotelInfoAndAvailability();
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        // Create mock HotelAvailabilityDomain without employee rate
        RoomRateDomain regularRatePlan = mock(RoomRateDomain.class);
        when(regularRatePlan.getCellCode()).thenReturn("OTHER_CODE");
        when(regularRatePlan.getRatePlanCode()).thenReturn("FLEXRATE");

        HotelAvailabilityDomain hotelAvailabilityDomain = mock(HotelAvailabilityDomain.class);
        when(hotelAvailabilityDomain.getRoomRateDomainList()).thenReturn(singletonList(regularRatePlan));

        // When - search with EMPLOYEE classification but no matching cellCode
        RoomRateDomain result = presenter.lookUpClassification("EMPLOYEE", hotelAvailabilityDomain);

        // Then - should return null as cellCode doesn't match EMP01
        assertThat(result).isNull();
    }

    @Test
    public void bindHotelAvailabilityUpdatesDatesAndRoomCriteriaWhenProvided() {
        mockSuccessfulHotelInfoAndAvailability();

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        LocalDate originalArrival = hotelDetailsInputFromSearchResults.searchResultsInput().arrivalDate();
        LocalDate originalDeparture = hotelDetailsInputFromSearchResults.searchResultsInput().departureDate();

        // Use fixed dates to avoid flakiness
        LocalDate newArrival = LocalDate.of(2025, 1, 10);
        LocalDate newDeparture = LocalDate.of(2025, 1, 11);

        // Provide a new set of RoomCriteria
        List<RoomCriteria> newRoomCriteria =
                java.util.Collections.singletonList(RoomCriteria.Companion.createWithDefaults());

        List<RoomSearch> expectedRoomSearch = HDPExtensionsKt.toRoomSearchCriteria(newRoomCriteria);

        // When - bind with new dates and new room criteria
        presenter.bindHotelAvailability(view, newArrival, newDeparture, newRoomCriteria);

        // Then - fetchPromotionsAndHotelAvailability should be called twice:
        // 1) once in the constructor with original input dates & room search
        // 2) once here with the new dates and converted room criteria
        ArgumentCaptor<HotelAvailabilityRequestBody> requestBodyCaptor =
                ArgumentCaptor.forClass(HotelAvailabilityRequestBody.class);

        verify(graphQLHDPUseCase, times(2)).fetchPromotionsAndHotelAvailability(
                requestBodyCaptor.capture(), any(), any(), any(), any(), any(), anyBoolean()
        );

        List<HotelAvailabilityRequestBody> allRequests = requestBodyCaptor.getAllValues();
        assertThat(allRequests).hasSize(2);

        // Verify FIRST call used original dates (constructor-triggered)
        HotelAvailabilityRequestBody first = allRequests.get(0);
        assertThat(first.getArrival()).isEqualTo(originalArrival.toString());
        assertThat(first.getDeparture()).isEqualTo(originalDeparture.toString());

        // Verify SECOND call used new dates and new room criteria
        HotelAvailabilityRequestBody second = allRequests.get(1);
        assertThat(second.getArrival()).isEqualTo(newArrival.toString());
        assertThat(second.getDeparture()).isEqualTo(newDeparture.toString());

        // Verify room criteria mapped correctly to RoomSearch
        assertThat(second.getRooms()).isNotNull();
        assertThat(second.getRooms()).hasSize(expectedRoomSearch.size());
        assertThat(second.getRooms()).containsExactlyElementsIn(expectedRoomSearch);

        // And the view rebind is triggered
        verify(view).rebindAllSubscriptions();
    }

    @Test
    public void ifArrivalAndDepartureDateAndNoHotelAvailableThenDoNotUpdateHotelDetails() {
        mockHotelInfoFailureWithAvailabilityAndPackageSuccess();

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        // Verify the use cases were invoked during presenter creation
        verify(graphQLHotelDetailsUseCase).fetchHotelInfoFromGQL(anyString(), anyString(), anyString());
        verify(graphQLHDPUseCase).fetchPromotionsAndHotelAvailability(any(), any(), any(), any(), any(), any(), anyBoolean());

        // Given - new arrival and departure dates
        LocalDate newArrival = LocalDate.now().plusDays(10);
        LocalDate newDeparture = LocalDate.now().plusDays(11);

        // When - call bindHotelAvailability with new arrival and departure dates
        presenter.bindHotelAvailability(view, newArrival, newDeparture, null);

        // Then - since hotel info failed, the observable is filtered out
        // so the view should not be interacted with (no rebinds, no updates)
        verifyNoInteractions(view);
    }

    @Test
    public void showAnnouncementReturnsFalseWhenAnnouncementIsNull() {
        // Given - null announcement
        AnnouncementDomain announcement = null;

        // Then
        Assert.assertFalse(HotelDetailsPresenter.showAnnouncement(announcement, searchResultsInput));
    }

    @Test
    public void showAnnouncementReturnsFalseWhenShowAnnouncementIsFalse() {
        // Given - announcement with showAnnouncement = false
        AnnouncementDomain announcement = new AnnouncementDomain(
                "false", "", "", "", "Some announcement text", "");

        // Then
        Assert.assertFalse(HotelDetailsPresenter.showAnnouncement(announcement, searchResultsInput));
    }

    @Test
    public void showAnnouncementReturnsFalseWhenTextIsEmpty() {
        // Given - announcement with empty text
        AnnouncementDomain announcement = new AnnouncementDomain(
                "true", "", "", "", "", "");

        // Then
        Assert.assertFalse(HotelDetailsPresenter.showAnnouncement(announcement, searchResultsInput));
    }

    @Test
    public void showAnnouncementReturnsTrueWhenNoDateConstraints() {
        // Given - announcement with no date constraints
        AnnouncementDomain announcement = new AnnouncementDomain(
                "true", "", "", "", "Important announcement", "");

        // Then
        Assert.assertTrue(HotelDetailsPresenter.showAnnouncement(announcement, searchResultsInput));
    }

    @Test
    public void showAnnouncementReturnsTrueWhenDatesOverlap() {
        // Given - announcement with overlapping dates, searchResultsInput has arrivalDate of 2018-03-01
        AnnouncementDomain announcement = new AnnouncementDomain(
                "true", "01/02/2018", "15/03/2018", "", "Important announcement", "");

        Assert.assertTrue(HotelDetailsPresenter.showAnnouncement(announcement, searchResultsInput));
    }

    @Test
    public void showAnnouncementReturnsFalseWhenDatesDoNotOverlap() {
        AnnouncementDomain announcement = new AnnouncementDomain(
                "true", "01/01/2017", "15/01/2017", "", "Important announcement", "");

        Assert.assertFalse(HotelDetailsPresenter.showAnnouncement(announcement, searchResultsInput));
    }

    @Test
    public void ifRestaurantHasValidNameThenShowTabbedContentWithRestaurantInfo() {
        // Given
        mockHotelInfoRestaurantNoDisclaimerWithAvailabilityAndPackagesSuccess();

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        // When
        presenter.bindRestaurantContentUiModel(view);

        // Then - verify TabbedContentUiModel with correct restaurant info is added
        verify(view, times(1)).addUiModelToRecyclerView(argThat(uiModel -> {
            if (!(uiModel instanceof TabbedContentUiModel tabbedContent)) {
                return false;
            }
            return tabbedContent.tabTitles().contains("Thyme Bar & Grill")
                    && tabbedContent.imageUrl().contains("/content/dam/global/restaurants/THY/Thyme-logo-165x73.jpg")
                    && !tabbedContent.tabContentList().isEmpty()
                    && tabbedContent.tabContentList().toString().contains("Our delicious in-house restaurant");
        }));
    }

    @Test
    public void ifRestaurantInfoIsNullThenShowNoBreakfastMessage() {
        // Given - Hotel info with null restaurant info
        HotelInformationDomain hotelInfo = mock(HotelInformationDomain.class);

        when(hotelInfo.getBrand()).thenReturn("PI");
        when(hotelInfo.getRestaurantInfo()).thenReturn(null);
        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(hotelInfo));
        mockPromotionsAndAvailabilitySuccess();

        List<Content> noBreakfastContent = singletonList(Content.builder()
                .contentType(Content.ContentType.BODY_WITH_HTML)
                .text("No breakfast available").build());
        when(messageProvider.noBreakFastMessage()).thenReturn(noBreakfastContent);

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        // When
        presenter.bindRestaurantContentUiModel(view);

        // Then - verify PageInfoUiModel is added and noBreakFastMessage was used
        verify(view, times(1)).addUiModelToRecyclerView(any(PageInfoUiModel.class));
        verify(messageProvider).noBreakFastMessage();
    }

    @Test
    public void bindTopFacilitiesUiModelShowsFacilitiesWhenNotEmpty() {
        mockSuccessfulHotelInfoAndAvailability();
        when(messageProvider.shortDescription(any())).thenReturn("Short description");
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        presenter.bindTopFacilitiesUiModel(view);

        // Then
        ArgumentCaptor<FacilitiesUiModel> captor = ArgumentCaptor.forClass(FacilitiesUiModel.class);
        verify(view).addUiModelToRecyclerView(captor.capture());

        FacilitiesUiModel model = captor.getValue();

        // Check the fields from facilities are being populated and verified
        assertThat(model.order()).isEqualTo(9);
        assertThat(model.hotelDescription()).isEqualTo("Short description");
        assertThat(model.facilities()).isNotEmpty();
        assertThat(model.facilities().toString()).contains("Free parking");
        assertThat(model.facilities().toString()).contains("Free Wi-Fi");
    }

    @Test
    public void bindTopFacilitiesUiModelDoesNotShowWhenEmpty() {
        // Given
        mockHotelInfoNoFacilitiesWithAvailabilityAndPackagesSuccess();

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        // When
        presenter.bindTopFacilitiesUiModel(view);

        // Then - verify FacilitiesUiModel is never added when facilities are empty
        verify(view, never()).addUiModelToRecyclerView(any(FacilitiesUiModel.class));
    }

    @Test
    public void bindImportantInfoUiModelAddsImportantInfoToViewWhenInfoExists() {
        mockSuccessfulHotelInfoAndAvailability();
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        Disposable disposable = presenter.bindImportantInfoUiModel(view);

        assertThat(disposable).isNotNull();

        // Only infoItems filtered out of 2 in json because of date filter
        verify(view).addUiModelToRecyclerView(argThat(item -> {
            if (!(item instanceof ImportantInfoUiModel model)) {
                return false;
            }
            return model.order() == 3 && model.count() == 1;
        }));

        disposable.dispose();
    }

    @Test
    public void bindImportantInfoUiModelDoesNotAddToViewWhenNoInfoItemsOverlap() {
       HotelInformationDomain hotelInfo = mock(HotelInformationDomain.class);
        when(hotelInfo.getBrand()).thenReturn("PI");

        // Both items end before the search arrival date (March 1, 2018), thus will be filtered
        List<InfoItem> nonOverlappingItems = Arrays.asList(
                new InfoItem("Info 1", "", "01/01/2016", "31/12/2016"),
                new InfoItem("Info 2", "", "01/01/2017", "28/02/2017")
        );
        when(hotelInfo.getImportantInfo()).thenReturn(new ImportantInfoDomain(nonOverlappingItems));

        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(hotelInfo));
        mockPromotionsAndAvailabilitySuccess();

        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        Disposable disposable = presenter.bindImportantInfoUiModel(view);

        disposable.dispose();

        // Verify no ImportantInfoUiModel is added when no items overlap
        verify(view, never()).addUiModelToRecyclerView(any(ImportantInfoUiModel.class));
    }

    @Test
    public void makeHoldBookingCall_triggersHoldBookingUseCase_andStartsSummaryActivity_onSuccess() {
        mockSuccessfulHotelInfoAndAvailability();
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        setMockSummaryInput();

        when(graphQLHoldBookingUseCase.holdBooking(any(HoldBookingRequestBody.class)))
                .thenReturn(Single.just(new Pair<>(GraphQLPackagesMapperKt
                        .mapToPackagesGraphQL(packagesSuccessResponse.getData().getPackages(), 1),
                        "basketref123")));
       presenter.makeHoldBookingCall(view, bathroomSelectionInput);

        verify(view, times(1)).shouldShowLoadingSpinner(true);
        verify(graphQLHoldBookingUseCase, times(1)).holdBooking(any(HoldBookingRequestBody.class));
        verify(view, times(1)).shouldShowLoadingSpinner(false);
        verify(view, times(1)).startSummaryActivity(summaryInput);
    }

    @Test
    public void showErrorMessage_whenCreateReservationFails() {
        // Arrange
        mockSuccessfulHotelInfoAndAvailability();
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        setMockSummaryInput();

        Throwable error = new CreateReservationException();
        when(graphQLHoldBookingUseCase.holdBooking(any(HoldBookingRequestBody.class)))
                .thenReturn(Single.error(error));

        presenter.makeHoldBookingCall(view, bathroomSelectionInput);

        verify(view, times(1)).shouldShowLoadingSpinner(true);
        verify(graphQLHoldBookingUseCase, times(1)).holdBooking(any(HoldBookingRequestBody.class));
        verify(view, times(1)).shouldShowLoadingSpinner(false);
        verify(view, times(1)).showCreateReservationErrorDialog();
    }

    @Test
    public void launchGDP_whenBookingInfoFails() {
        // Arrange
        mockSuccessfulHotelInfoAndAvailability();
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        setMockSummaryInput();

        Throwable error = new BookingInformationException("basketref");
        when(graphQLHoldBookingUseCase.holdBooking(any(HoldBookingRequestBody.class)))
                .thenReturn(Single.error(error));

        presenter.makeHoldBookingCall(view, bathroomSelectionInput);

        verify(view, times(1)).shouldShowLoadingSpinner(true);
        verify(graphQLHoldBookingUseCase, times(1)).holdBooking(any(HoldBookingRequestBody.class));
        verify(view, times(1)).shouldShowLoadingSpinner(false);
        verify(view, times(1)).startGuestDetailsActivity(any());
    }

    @Test
    public void proceedToSummariesWhenOnlyPackagesIsNull() {
        mockSuccessfulHotelInfoAndAvailability();
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        setMockSummaryInput();

        when(graphQLHoldBookingUseCase.holdBooking(any(HoldBookingRequestBody.class)))
                .thenReturn(Single.just(new Pair<>(GraphQLPackagesMapperKt
                        .mapToPackagesGraphQL(packagesWithNullResponse.getData().getPackages(), 1),
                        "basketref123")));
        presenter.makeHoldBookingCall(view, bathroomSelectionInput);

        verify(view, times(1)).shouldShowLoadingSpinner(true);
        verify(graphQLHoldBookingUseCase, times(1)).holdBooking(any(HoldBookingRequestBody.class));
        verify(view, times(1)).shouldShowLoadingSpinner(false);
        verify(view, times(1)).startSummaryActivity(summaryInput);
    }

    @Test
    public void proceedToSummaries_whenPackagesIsNullButExtrasIsNotNull() {
        mockSuccessfulHotelInfoAndAvailability();
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromSearchResults);

        setMockSummaryInput();

        when(graphQLHoldBookingUseCase.holdBooking(any(HoldBookingRequestBody.class)))
                .thenReturn(Single.just(new Pair<>(GraphQLPackagesMapperKt
                        .mapToPackagesGraphQL(packagesWithNullMealsWithExtrasesponse.getData().getPackages(), 1),
                        "basketref123")));
        presenter.makeHoldBookingCall(view, bathroomSelectionInput);

        verify(view, times(1)).shouldShowLoadingSpinner(true);
        verify(graphQLHoldBookingUseCase, times(1)).holdBooking(any(HoldBookingRequestBody.class));
        verify(view, times(1)).shouldShowLoadingSpinner(false);
        verify(view, times(1)).startSummaryActivity(summaryInput);
    }

    @Test
    public void launchAdditionalInfo_whenBookingInfoFailsForBB() {
        // Arrange
        mockSuccessfulHotelInfoAndAvailability();

        when(businessStorage.getCustomerAccessLevel()).thenReturn(AccessLevel.STAYER);
        when(businessStorage.getCompany()).thenReturn(CustomerFixture.INSTANCE.bCustomer().getCompany());
        when(storage.getCustomer()).thenReturn(CustomerFixture.INSTANCE.bCustomer());
        when(storage.getPaymentProvider()).thenReturn("Planet");

        HotelDetailsPresenter presenter = createBBInstanceFrom(hotelDetailsInputFromDirectHotelSearch);

        setMockSummaryInput();

        Throwable error = new BookingInformationException("basketref");
        when(graphQLHoldBookingUseCase.holdBooking(any(HoldBookingRequestBody.class)))
                .thenReturn(Single.error(error));

        presenter.makeHoldBookingCall(view, bathroomSelectionInput);

        verify(view, times(1)).shouldShowLoadingSpinner(true);
        verify(graphQLHoldBookingUseCase, times(1)).holdBooking(any(HoldBookingRequestBody.class));
        verify(view, times(1)).shouldShowLoadingSpinner(false);
        verify(view, times(1)).startAdditionalInformationActivity(any());
    }

    @Test
    public void launchRnB_whenBookingInfoFailsForBB() {
        // Arrange
        // Arrange
        mockSuccessfulHotelInfoAndAvailability();

        when(businessStorage.getCustomerAccessLevel()).thenReturn(AccessLevel.STAYER);
        when(businessStorage.getCompany()).thenReturn(CustomerFixture.INSTANCE.bCustomerWithNoAdditionalQuestions().getCompany());
        when(storage.getCustomer()).thenReturn(CustomerFixture.INSTANCE.bCustomerWithNoAdditionalQuestions());
        when(storage.getPaymentProvider()).thenReturn("Planet");

        HotelDetailsPresenter presenter = createBBInstanceFrom(hotelDetailsInputFromDirectHotelSearch);

        setMockSummaryInput();

        Throwable error = new BookingInformationException("basketref");
        when(graphQLHoldBookingUseCase.holdBooking(any(HoldBookingRequestBody.class)))
                .thenReturn(Single.error(error));

        presenter.makeHoldBookingCall(view, bathroomSelectionInput);

        verify(view, times(1)).shouldShowLoadingSpinner(true);
        verify(graphQLHoldBookingUseCase, times(1)).holdBooking(any(HoldBookingRequestBody.class));
        verify(view, times(1)).shouldShowLoadingSpinner(false);
        verify(view, times(1)).startReviewAndBookActivity(any());
    }

    @Test
    public void bindSummaryOrRoomDisposable_triggersShowPrivilegeCardPopUp_forEmployeeRate() {
        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt
                        .mapToHotelInformationGQL(hotelInfoGraphQLContractResponse)));
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromDirectHotelSearch);

        presenter.rateClickObservable = Observable.just(bathroomSelectionInput);
        setMockSummaryInput();
        when(selectedRate.rateType()).thenReturn(EMPLOYEE_RATE_PLAN_CODE);

        presenter.bindSummaryOrRoomSelectionDisposable(view);
        verify(view).showDoNotForgetPrivilegeCardAlert(bathroomSelectionInput, false, false);
    }

    @Test
    public void onClickOfEmployeeRateContinue_triggersHoldBookingFlow() {
        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt
                        .mapToHotelInformationGQL(hotelInfoGraphQLContractResponse)));
        when(view.onClickEmployeeContinue()).thenReturn(Observable.just(new StartSummaryOrRoomSelectionEvent(
                bathroomSelectionInput, false, false)));
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromDirectHotelSearch);


        setMockSummaryInput();

        when(graphQLHoldBookingUseCase.holdBooking(any(HoldBookingRequestBody.class)))
                .thenReturn(Single.just(new Pair<>(GraphQLPackagesMapperKt
                        .mapToPackagesGraphQL(packagesSuccessResponse.getData().getPackages(), 1),
                        "basketref123")));

        presenter.bindEmployeeRateContinueClick(view);
        verify(view).onClickEmployeeContinue();
        verify(view).shouldShowLoadingSpinner(true);
        verify(graphQLHoldBookingUseCase, times(1)).holdBooking(any(HoldBookingRequestBody.class));

    }

    @Test
    public void onClickOfEmployeeRateContinue_triggersAlternateRoomFlow() {
        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt
                        .mapToHotelInformationGQL(hotelInfoGraphQLContractResponse)));
        when(view.onClickEmployeeContinue()).thenReturn(Observable.just(new StartSummaryOrRoomSelectionEvent(
                bathroomSelectionInput, true, false)));
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromDirectHotelSearch);

        presenter.bindEmployeeRateContinueClick(view);
        verify(view).onClickEmployeeContinue();
        verify(view).startAlternativeRoomSelectionActivity(any(BathroomSelectionInput.class));
        verify(graphQLHoldBookingUseCase, times(0)).holdBooking(any(HoldBookingRequestBody.class));

    }
    @Test
    public void onClickOfEmployeeRateContinue_triggers_AccessibleFlow() {
        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt
                        .mapToHotelInformationGQL(hotelInfoGraphQLContractResponse)));
        when(view.onClickEmployeeContinue()).thenReturn(Observable.just(new StartSummaryOrRoomSelectionEvent(
                bathroomSelectionInput, true, true)));
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromDirectHotelSearch);

        presenter.bindEmployeeRateContinueClick(view);
        verify(view).onClickEmployeeContinue();
        verify(view).startBathroomSelectionActivity(any(BathroomSelectionInput.class));
        verify(graphQLHoldBookingUseCase, times(0)).holdBooking(any(HoldBookingRequestBody.class));

    }

    @Test
    public void bindSummaryOrRoomDisposable_triggersHoldBooking() {
        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt
                        .mapToHotelInformationGQL(hotelInfoGraphQLContractResponse)));
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromDirectHotelSearch);
        presenter.rateClickObservable = Observable.just(bathroomSelectionInput);

        setMockSummaryInput();

        when(graphQLHoldBookingUseCase.holdBooking(any(HoldBookingRequestBody.class)))
                .thenReturn(Single.just(new Pair<>(GraphQLPackagesMapperKt
                        .mapToPackagesGraphQL(packagesSuccessResponse.getData().getPackages(), 1),
                        "basketref123")));

        presenter.bindSummaryOrRoomSelectionDisposable(view);

        verify(view).shouldShowLoadingSpinner(true);
        verify(graphQLHoldBookingUseCase, times(1)).holdBooking(any(HoldBookingRequestBody.class));

    }

    @Test
    public void bindSummaryOrRoomDisposable_triggersToStartBathroomSelectionFlow() {
        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt
                        .mapToHotelInformationGQL(hotelInfoGraphQLContractResponse)));
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromDirectHotelSearch);
        presenter.rateClickObservable = Observable.just(bathroomSelectionInput);
        presenter.isAccessibleFlow = true;
        setMockSummaryInput();

        presenter.bindSummaryOrRoomSelectionDisposable(view);
        verify(view).startBathroomSelectionActivity(any(BathroomSelectionInput.class));

    }


    @Test
    public void bindSummaryOrRoomDisposable_triggersToLaunchAlternateRoomSelectionFlow() {
        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt
                        .mapToHotelInformationGQL(hotelInfoGraphQLContractResponse)));
        HotelDetailsPresenter presenter = createInstanceFrom(hotelDetailsInputFromDirectHotelSearch);
        presenter.rateClickObservable = Observable.just(bathroomSelectionInput);
        presenter.showSelectLabel = true;
        setMockSummaryInput();

        presenter.bindSummaryOrRoomSelectionDisposable(view);
        verify(view).startAlternativeRoomSelectionActivity(any(BathroomSelectionInput.class));
    }

    @Test
    public void bindSummaryOrRoomDisposable_forBBStayer_triggersBBRestrictionAlert() {
        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt
                        .mapToHotelInformationGQL(hotelInfoGraphQLContractResponse)));
        when(businessStorage.getCustomerAccessLevel()).thenReturn(AccessLevel.STAYER);
        HotelDetailsPresenter presenter = createBBInstanceFrom(hotelDetailsInputFromDirectHotelSearch);

        presenter.rateClickObservable = Observable.just(bathroomSelectionInput);
        setMockSummaryInput();

        presenter.bindSummaryOrRoomSelectionDisposable(view);
        verify(view).showInnBusinessGuestRestrictionAlert();
    }


    private void mockSuccessfulHotelInfoAndAvailability() {
        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt
                        .mapToHotelInformationGQL(hotelInfoGraphQLContractResponse)));
        mockPromotionsAndAvailabilitySuccess();
    }

    private void mockPromotionsAndAvailabilitySuccess() {
        HotelAvailabilityDomain hotelAvailabilityDomain =
                GraphQLHotelAvailabilityMapperKt.mapToHotelAvailabilityGQL(hotelAvailabilityRatesAndRoomTypeDataResponse, null);
        GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess success =
                GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess.INSTANCE;
        when(graphQLHDPUseCase.fetchPromotionsAndHotelAvailability(any(), any(), any(), any(), any(), any(), anyBoolean()))
            .thenReturn(Observable.just(new HotelBookingAvailabilityState(
                success,
                hotelAvailabilityDomain,
                hotelAvailabilityDomain.getListOfRatesClassification(),
                hotelAvailabilityDomain.getListOfRoomTypeInfo(), EMPTY_STRING_DOMAIN, false, null, null)));
    }

    private void mockHotelInfoFailureWithAvailabilityAndPackageSuccess() {
        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt.mapToHotelInformationGQL(hotelInfoGraphQLContractResponseFailure)));
        mockPromotionsAndAvailabilitySuccess();
    }

    private void mockHotelInfoRestaurantNoDisclaimerWithAvailabilityAndPackagesSuccess() {
        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt
                        .mapToHotelInformationGQL(hotelInfoGraphQLContractResponseRestaurantNoDisclaimer)));
        mockPromotionsAndAvailabilitySuccess();
    }

    private void mockHotelInfoNoFacilitiesWithAvailabilityAndPackagesSuccess() {
        when(graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(anyString(), anyString(), anyString()))
                .thenReturn(Single.just(GraphQLHotelInfoMappersKt
                        .mapToHotelInformationGQL(hotelInfoGraphQLContractResponseFailure)));
        mockPromotionsAndAvailabilitySuccess();
    }

    private void mockCancelOnHoldReservationSuccess() {
        when(graphQLHDPUseCase.cancelOnHoldReservation(anyString(), anyString()))
                .thenReturn(Single.just(GraphQLCancelOnHoldReservationMapperKt.
                        mapToCancelOnHoldReservationGQL(cancelOnHoldReservationData)));
    }

    private void setMockSummaryInput() {
        when(bathroomSelectionInput.getProvisionalSummaryInput()).thenReturn(summaryInput);
        when(summaryInput.hotel()).thenReturn(selectedHotel);
        when(summaryInput.hotelBrand()).thenReturn("PI");
        when(summaryInput.totalAdults()).thenReturn(2);
        when(summaryInput.totalChildren()).thenReturn(0);
        when(summaryInput.totalNights()).thenReturn(2);
        when(summaryInput.hotel().name()).thenReturn("BANBRI");
        when(summaryInput.hotel().address()).thenReturn("Some address");
        when(summaryInput.arrivalDate()).thenReturn(LocalDate.of(2027, 6, 23));
        when(summaryInput.hotel().isHub()).thenReturn(false);

        when(selectedHotel.code()).thenReturn("BANBRI");
        when(selectedHotel.imageReference()).thenReturn("any image ref");

        when(summaryInput.arrivalDateGQ()).thenReturn("2027-06-23");
        when(summaryInput.departureDateGQ()).thenReturn("2027-06-25");
        when(summaryInput.rate()).thenReturn(selectedRate);
        when(selectedRate.rateType()).thenReturn("FLEXRATE");

        List<String> specialRequests = List.of("DB", "SNG");
        when(summaryInput.specialRequests()).thenReturn(List.of(specialRequests));

        List<DailyRateInput> dailyRateInputs = new ArrayList<>();
        List<RoomBooking> roomBookings = new ArrayList<>();
        dailyRateInputs.add(new DailyRateInput(LocalDate.of(2018, 1, 17),
                MappersKt.toPriceParcelable(PriceDomain.Companion.createWithGBPCurrency(160f))));
        roomBookings.add(new RoomBooking(1, 0, 0, false,
                RoomType.DOUBLE.name(), "", dailyRateInputs,
                CommonMappersKt.toParcelablePrice(PriceDomain.Companion.createDefault()),
                1, null));
        when(summaryInput.roomBookings()).thenReturn(roomBookings);
        when(summaryInput.isBusinessUser()).thenReturn(false);
        when(summaryInput.basketReference()).thenReturn("basketref");

        when(summaryInput.toBuilder()).thenReturn(summaryInputBuilder);
        when(summaryInputBuilder.build()).thenReturn(summaryInput);
        when(summaryInputBuilder.upsellItems(any())).thenReturn(summaryInputBuilder);
        when(summaryInputBuilder.extrasItems(any())).thenReturn(summaryInputBuilder);
        when(summaryInputBuilder.upsellsImages(any())).thenReturn(summaryInputBuilder);
        when(summaryInputBuilder.cityTaxForLeisure(any())).thenReturn(summaryInputBuilder);
        when(summaryInputBuilder.cityTaxForBusiness(any())).thenReturn(summaryInputBuilder);
        when(summaryInputBuilder.basketReference(any())).thenReturn(summaryInputBuilder);
        when(summaryInputBuilder.menus(any())).thenReturn(summaryInputBuilder);
        when(summaryInputBuilder.allergyInfo(any())).thenReturn(summaryInputBuilder);

    }

//---------------------


    HotelDetailsPresenter createInstanceFrom(@NonNull HotelDetailsInput input) {
        when(businessStorage.getOperaCompanyId()).thenReturn(EMPTY_STRING_DOMAIN);
        when(businessStorage.getBusinessCustomerEmail()).thenReturn(EMPTY_STRING_DOMAIN);
        HotelDetailsPresenter presenter = new HotelDetailsPresenter(
                graphQLHoldBookingUseCase,
                getStringResource,
                trackingAnalytics,
                firebaseLogger,
                messageProvider,
                contentManagedResourceRepository,
                crashlyticsLogger,
                storage,
                businessStorage,
                isFeatureOn,
                isCustomerLoggedIn,
                deviceLocaleProvider,
                stringResourceProvider,
                graphQLHDPUseCase,
                checkInCheckOutStringProvider,
                graphQLHotelDetailsUseCase,
                getRoomClassConfigUseCase,
                configuration
        );
        presenter.initParams(input);
        return presenter;
    }

    HotelDetailsPresenter createBBInstanceFrom(@NonNull HotelDetailsInput input) {
        when(businessStorage.getOperaCompanyId()).thenReturn("operaCompanyId123");
        when(businessStorage.getBusinessCustomerEmail()).thenReturn("somebbuser@yopmail.com");
        HotelDetailsPresenter presenter = new HotelDetailsPresenter(
                graphQLHoldBookingUseCase,
                getStringResource,
                trackingAnalytics,
                firebaseLogger,
                messageProvider,
                contentManagedResourceRepository,
                crashlyticsLogger,
                storage,
                businessStorage,
                isFeatureOn,
                isCustomerLoggedIn,
                deviceLocaleProvider,
                stringResourceProvider,
                graphQLHDPUseCase,
                checkInCheckOutStringProvider,
                graphQLHotelDetailsUseCase,
                getRoomClassConfigUseCase,
                configuration
        );
        presenter.initParams(input);
        return presenter;
    }

    private Restaurant getRestaurant(String name, List<Menu> menu) {
        return new Restaurant(name, "imagePath", "description", menu);
    }

    private List<Menu> getMenuList(String disclaimer) {
        return singletonList(new Menu("Breakfast", "path", "image.jpg", "Description", disclaimer));
    }

}
