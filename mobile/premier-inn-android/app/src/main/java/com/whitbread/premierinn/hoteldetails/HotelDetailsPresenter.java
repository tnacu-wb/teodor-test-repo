package com.whitbread.premierinn.hoteldetails;

import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState;
import static com.whitbread.premierinn.common.dynatrace.DynatraceAnalyticsConstants.AVAILABILITY_ACTION;
import static com.whitbread.premierinn.common.dynatrace.DynatraceAnalyticsConstants.AVAILABILITY_KEY;
import static com.whitbread.premierinn.common.utils.AppExtensions.buildCallUiModel;
import static com.whitbread.premierinn.common.utils.AppExtensions.getCheckInCheckoutTimes;
import static com.whitbread.premierinn.common.utils.AppExtensions.mapToHotelContactDetailsDomainOpera;
import static com.whitbread.premierinn.common.utils.AppExtensions.retrieveCheckInCheckoutInfoFromFirebase;
import static com.whitbread.premierinn.common.utils.StringUtils.EMPTY_STRING;
import static com.whitbread.premierinn.data.common.Constants.BRAND_HUB;
import static com.whitbread.premierinn.data.common.Constants.BRAND_PI;
import static com.whitbread.premierinn.data.common.Constants.BRAND_PI_GERMANY;
import static com.whitbread.premierinn.data.common.Constants.BRAND_ZIP;
import static com.whitbread.premierinn.domain.common.Constants.EMPLOYEE_CODE;
import static com.whitbread.premierinn.domain.common.Constants.EMPLOYEE_RATE_PLAN_CODE;
import static com.whitbread.premierinn.domain.common.Constants.MILES_TO_KM;
import static com.whitbread.premierinn.domain.common.OperaCommonExtensionsKt.BUSINESS_RATE_NAME;
import static com.whitbread.premierinn.domain.common.OperaCommonExtensionsKt.SUB_CHANNEL;
import static com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain.PROMO_KIND_SITE_WIDE;
import static com.whitbread.premierinn.hoteldetails.hdpOperaExtensions.HDPExtensionsKt.buildCheckInOutModel;
import static com.whitbread.premierinn.hoteldetails.hdpOperaExtensions.HDPExtensionsKt.getHubFacilities;
import static com.whitbread.premierinn.hoteldetails.hdpOperaExtensions.HDPExtensionsKt.getPremierInnRoomFeatures;
import static com.whitbread.premierinn.hoteldetails.hdpOperaExtensions.HDPExtensionsKt.getZipFacilities;
import static com.whitbread.premierinn.landing.model.PromoCodeInputKt.CODE_FREE_BREAKFAST_INCENTIVE;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.whitbread.premierinn.R;
import com.whitbread.premierinn.additionalinformation.entity.EmployeeQuestionsModel;
import com.whitbread.premierinn.api.Urls;
import com.whitbread.premierinn.api.response.availability.Coordinates;
import com.whitbread.premierinn.api.response.availability.Facility;
import com.whitbread.premierinn.api.response.availability.HotelInfo;
import com.whitbread.premierinn.api.response.booking.BookingRoom;
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager;
import com.whitbread.premierinn.businessbooker.domain.company.ManagementInformationQuestion;
import com.whitbread.premierinn.businessbooker.domain.company.PriceCapLocations;
import com.whitbread.premierinn.common.AppConfiguration;
import com.whitbread.premierinn.common.BookingFlowInput;
import com.whitbread.premierinn.common.CheckInCheckOutStringProvider;
import com.whitbread.premierinn.common.Constants;
import com.whitbread.premierinn.common.RoomBooking;
import com.whitbread.premierinn.common.StringResourceProvider;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.FirebaseLogger;
import com.whitbread.premierinn.common.analytics.FirebaseParams;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.dynatrace.DynatraceHelper;
import com.whitbread.premierinn.common.format.DateFormat;
import com.whitbread.premierinn.common.format.PriceFormat;
import com.whitbread.premierinn.common.mapper.CommonMappersKt;
import com.whitbread.premierinn.common.mvp.Presenter;
import com.whitbread.premierinn.common.mvp.PresenterView;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.common.utils.AppExtensions;
import com.whitbread.premierinn.common.utils.HtmlUtils;
import com.whitbread.premierinn.common.utils.StringUtils;
import com.whitbread.premierinn.common.view.gallery.Image;
import com.whitbread.premierinn.criteria.roomselector.Room;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager;
import com.whitbread.premierinn.data.graphql.BookingInformationException;
import com.whitbread.premierinn.data.graphql.CreateReservationException;
import com.whitbread.premierinn.data.graphql.mapper.GraphQLHotelInfoMappersKt;
import com.whitbread.premierinn.data.hotel.mapper.HotelMappersKt;
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn;
import com.whitbread.premierinn.domain.common.AllCheckInCheckoutTimesInfoDomain;
import com.whitbread.premierinn.domain.common.OperaCommonExtensionsKt;
import com.whitbread.premierinn.domain.common.OperaRoomTypesKt;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.domain.common.RatePlanOpera;
import com.whitbread.premierinn.domain.common.RoomCriteria;
import com.whitbread.premierinn.domain.common.RoomOpera;
import com.whitbread.premierinn.domain.common.RoomType;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.AncillaryCloseOutItem;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.AnnouncementDomain;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.GalleryImageDomain;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelFacilityDomain;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelInformationDomain;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.ImportantInfoDomain;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.InfoItem;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.RestaurantMenuDomain;
import com.whitbread.premierinn.domain.common.hoteldetails.entity.TopSectionImageDomain;
import com.whitbread.premierinn.domain.common.hoteldetails.usecase.GraphQLHotelDetailsUseCase;
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn;
import com.whitbread.premierinn.domain.customer.entity.AccessLevel;
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences;
import com.whitbread.premierinn.domain.graphql.common.usecase.GraphQLHoldBookingUseCase;
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelAvailabilityDomain;
import com.whitbread.premierinn.domain.graphql.hdp.entity.RateClassificationsDomain;
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomRateDomain;
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomTypeInfoDomain;
import com.whitbread.premierinn.domain.graphql.hdp.usecase.GraphQLHDPUseCase;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationRequestBody;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HoldBookingRequestBody;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilityRequestBody;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelInfoDetails;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomClassConfigRequestBody;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomSearch;
import com.whitbread.premierinn.domain.graphql.roomClassConfig.entity.RoomClassConfig;
import com.whitbread.premierinn.domain.graphql.roomClassConfig.usecase.GetRoomClassConfigUseCase;
import com.whitbread.premierinn.domain.hotel.entity.Hotel;
import com.whitbread.premierinn.domain.hotel.entity.HotelBookingAvailabilityState;
import com.whitbread.premierinn.domain.hotel.entity.RoomVariant;
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository;
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key;
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource;
import com.whitbread.premierinn.hoteldetails.analytics.DiscountCodeAnalyticsData;
import com.whitbread.premierinn.hoteldetails.analytics.HDPMergedInfoToAnalyticsDataMapper;
import com.whitbread.premierinn.hoteldetails.discountcode.DiscountCodeInput;
import com.whitbread.premierinn.hoteldetails.discountcode.RoomSearchData;
import com.whitbread.premierinn.hoteldetails.event.AccesibilityClickEvent;
import com.whitbread.premierinn.hoteldetails.event.AccessibilityRoomTypeInfoClickEvent;
import com.whitbread.premierinn.hoteldetails.event.CheckAvailabilityClickEvent;
import com.whitbread.premierinn.hoteldetails.event.CoronavirusDismissClickEvent;
import com.whitbread.premierinn.hoteldetails.event.DiscountCodeClickEvent;
import com.whitbread.premierinn.hoteldetails.event.EditDatesClickEvent;
import com.whitbread.premierinn.hoteldetails.event.EditGuestClickEvent;
import com.whitbread.premierinn.hoteldetails.event.FacilitiesClickEvent;
import com.whitbread.premierinn.hoteldetails.event.FindOutMoreClickEvent;
import com.whitbread.premierinn.hoteldetails.event.HotelsNearbyClickEvent;
import com.whitbread.premierinn.hoteldetails.event.ImportantInfoClickEvent;
import com.whitbread.premierinn.hoteldetails.event.MainGalleryItemClickEvent;
import com.whitbread.premierinn.hoteldetails.event.MakePhoneCallEvent;
import com.whitbread.premierinn.hoteldetails.event.MapViewClickEvent;
import com.whitbread.premierinn.hoteldetails.event.OpenWebLinkEvent;
import com.whitbread.premierinn.hoteldetails.event.ParkingClickEvent;
import com.whitbread.premierinn.hoteldetails.event.RateClickEvent;
import com.whitbread.premierinn.hoteldetails.event.ReadMoreClickEvent;
import com.whitbread.premierinn.hoteldetails.event.RoomTypesClickEvent;
import com.whitbread.premierinn.hoteldetails.event.SelectRateClickEvent;
import com.whitbread.premierinn.hoteldetails.event.SendEmailEvent;
import com.whitbread.premierinn.hoteldetails.event.StartSummaryOrRoomSelectionEvent;
import com.whitbread.premierinn.hoteldetails.facilities.HotelFacilitiesModel;
import com.whitbread.premierinn.hoteldetails.facilities.RoomFeaturesModel;
import com.whitbread.premierinn.hoteldetails.hdpOperaExtensions.HDPExtensionsKt;
import com.whitbread.premierinn.hoteldetails.hotelfulldescription.HotelFullDescription;
import com.whitbread.premierinn.hoteldetails.hotelfulldescription.RoomRatePlan;
import com.whitbread.premierinn.hoteldetails.hotelmapfullscreen.MapStartInfo;
import com.whitbread.premierinn.hoteldetails.uimodel.AccessibilityInformationUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.CoronavirusUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.DateGuestUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.DiscountCodeUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.FacilitiesUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.FullyBookedUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.HotelAccessibilityUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.HotelFacilitiesUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.HotelGalleryUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.HotelParkingUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.ImportantInfoUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.PageInfoUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.RateBoxUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.RoomRatesUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.RoomTypesSectionUiModel;
import com.whitbread.premierinn.hoteldetails.uimodel.TabbedContentUiModel;
import com.whitbread.premierinn.landing.SearchPayload;
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput;
import com.whitbread.premierinn.searchresults.SearchResultsInput;
import com.whitbread.premierinn.summary.SummaryInput;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ActivityRetainedScoped;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import kotlin.Pair;

@ActivityRetainedScoped
public class HotelDetailsPresenter extends Presenter<HotelDetailsPresenter.View> {
    private final GraphQLHDPUseCase graphQLHDPUseCase;
    private final GetRoomClassConfigUseCase getRoomClassConfigUseCase;
    private int maxNights;
    private int maxArrivalDate;
    private String hotelBrandString;
    private final String operaCompanyId;
    private final boolean isInnBusinessUser;
    private String channel;
    private final GraphQLHoldBookingUseCase holdBookingUseCase;

    private String country;
    private Locale deviceLocale;
    private String deviceLanguage;
    private HotelDetailsInput hotelDetailsInput;
    private final GetStringResource getStringResource;
    private final TrackingAnalytics adobeAnalytics;
    private final FirebaseLogger firebaseLogger;
    private CompositeDisposable viewDisposable;
    private CompositeDisposable networkDisposable;
    private final HotelDetailsMessageProvider messageProvider;
    private Observable<HotelInformationDomain> hotelInformationDomainObservable;
    private Observable<HotelBookingAvailabilityState> hotelBookingAvailabilityStateObservable;
    private Observable<HotelDetailsInput> inputObservable;
    private Observable<List<List<RatesDisplayInfo>>> rateBoxUiModelObservable;
    protected Observable<BathroomSelectionInput> rateClickObservable;
    private LogService crashlyticsLogger;
    private SimplePersistenceManager storage;
    private BusinessPersistenceManager businessStorage;
    private List<RoomCriteria> currentRoomCriteria;
    protected boolean showSelectLabel;
    protected boolean isAccessibleFlow;
    private DeviceLocaleProvider deviceLocalProvider;
    private final StringResourceProvider stringResourceProvider;

    @VisibleForTesting
    protected Hotel.Brand hotelBrand;
    @VisibleForTesting
    protected List<RateClassificationsDomain> listOfRateClassification;
    private List<RoomSearch> roomSearchList;
    private IsFeatureOn isFeatureOn;
    private final IsCustomerLoggedIn isCustomerLoggedIn;
    private boolean hasCustomerLoggedIn = false;

    private List<RoomTypeInfoDomain> roomTypeInfoDomain;
    private String hotelcode;
    private AllCheckInCheckoutTimesInfoDomain allCheckInTimesInfo;
    private CheckInCheckOutStringProvider checkInCheckOutStringProvider;
    private static final int SECTION_ORDER_ZERO = 0;
    private static final int SECTION_ORDER_ONE = 1;
    private static final int SECTION_ORDER_TWO = 2;
    private static final int SECTION_ORDER_THREE = 3;
    private static final int SECTION_ORDER_FOUR = 4;
    private static final int SECTION_ORDER_FIVE = 5;
    private static final int SECTION_ORDER_SIX = 6;
    private static final int SECTION_ORDER_SEVEN = 7;
    private static final int SECTION_ORDER_EIGHT = 8;
    private static final int SECTION_ORDER_NINE = 9;
    private static final int SECTION_ORDER_TEN = 10;
    private static final int SECTION_ORDER_ELEVEN = 11;
    private static final int SECTION_ORDER_TWELVE = 12;
    private static final int SECTION_ORDER_THIRTEEN = 13;
    private static final int SECTION_ORDER_FOURTEEN = 14;
    private static final int SECTION_ORDER_FIFTEEN = 15;
    private static final int SECTION_ORDER_SIXTEEN = 16;
    private static final int SECTION_ORDER_SEVENTEEN = 17;
    private List<String> listOfRatePlanCodes;
    private Boolean isEmployeeRateEnabled;
    private Boolean isEmployeeRateSelected;
    private Boolean isAppPromotionalIncentiveActive;
    private final GraphQLHotelDetailsUseCase graphQLHotelDetailsUseCase;
    private final AppConfiguration appConfiguration;
    private final ContentManagedResourceRepository contentRepository;
    private Boolean isFreeBreakfastPromotionActive;
    private Boolean isSingleUseDiscountBoxVisible;
    private boolean skipPromotionsCheck;
    private View view;
    private Observable<List<List<RatesDisplayInfo>>> stdListObservable;
    private Observable<List<List<RatesDisplayInfo>>> alterListObservable;
    private AccessLevel innBusinessUserAccessLevel;
    private float dinnerAllowance = 0;
    private PriceCapLocations priceCapLocations;
    @VisibleForTesting
    protected String promoCode = EMPTY_STRING;
    @VisibleForTesting
    protected String promoKind = null;
    @VisibleForTesting
    protected String promoSuccessMessage = EMPTY_STRING;
    private String invalidDiscountCodeMessage;
    @VisibleForTesting
    List<RoomClassConfig> sortedRoomClassConfigs;
    private BookingFlowInput bookingFlowInput;

    @Inject
    public HotelDetailsPresenter(
            @NonNull GraphQLHoldBookingUseCase holdBookingUseCase, @NonNull GetStringResource getStringResource,
            @NonNull TrackingAnalytics adobeAnalytics,
            @NonNull FirebaseLogger firebaseLogger,
            @NonNull HotelDetailsMessageProvider messageProvider,
            @NonNull ContentManagedResourceRepository contentRepository,
            @NonNull LogService crashlyticsLogger,
            @NonNull SimplePersistenceManager storage,
            @NonNull BusinessPersistenceManager businessStorage,
            @NonNull IsFeatureOn isFeatureOn,
            @NonNull IsCustomerLoggedIn isCustomerLoggedIn,
            @NonNull DeviceLocaleProvider deviceLocalProvider,
            @NonNull StringResourceProvider stringResourceProvider,
            @NonNull GraphQLHDPUseCase graphQLHDPUseCase,
            @NonNull CheckInCheckOutStringProvider checkInCheckOutStringProvider,
            @NonNull GraphQLHotelDetailsUseCase graphQLHotelDetailsUseCase,
            @NonNull GetRoomClassConfigUseCase getRoomClassConfigUseCase,
            @NonNull AppConfiguration appConfiguration) {
        this.holdBookingUseCase = holdBookingUseCase;
        this.getStringResource = getStringResource;
        this.adobeAnalytics = adobeAnalytics;
        this.firebaseLogger = firebaseLogger;
        this.messageProvider = messageProvider;
        this.crashlyticsLogger = crashlyticsLogger;
        this.storage = storage;
        this.businessStorage = businessStorage;
        this.isFeatureOn = isFeatureOn;
        this.isCustomerLoggedIn = isCustomerLoggedIn;
        this.deviceLocalProvider = deviceLocalProvider;
        this.stringResourceProvider = stringResourceProvider;
        this.graphQLHDPUseCase = graphQLHDPUseCase;
        this.getRoomClassConfigUseCase = getRoomClassConfigUseCase;
        this.operaCompanyId = businessStorage.getOperaCompanyId();
        this.isInnBusinessUser = !businessStorage.getBusinessCustomerEmail().isEmpty();
        this.checkInCheckOutStringProvider = checkInCheckOutStringProvider;
        this.graphQLHotelDetailsUseCase = graphQLHotelDetailsUseCase;
        this.appConfiguration = appConfiguration;
        this.contentRepository = contentRepository;
    }

    public void initParams(@NonNull HotelDetailsInput hotelDetailsInput) {
        this.hotelDetailsInput = hotelDetailsInput;
        initPresenter();
    }

    private void initPresenter() {
        viewDisposable = new CompositeDisposable();
        networkDisposable = new CompositeDisposable();
        boolean isisEmployeeRateTurnedOnFb = isFeatureOn.invoke(Key.FEATURE_ALLOW_EMPLOYEE_OFFER);
        isSingleUseDiscountBoxVisible = isFeatureOn.invoke(Key.FEATURE_SHOW_SINGLE_USE_DISCOUNT_BOX_HDP)
                && HDPExtensionsKt.isBrandAllowedForDiscountBox(hotelDetailsInput.brand(), crashlyticsLogger, contentRepository);

        runPromoLogic(getStringResource, storage, isFeatureOn);

        isEmployeeRateEnabled = appConfiguration.isEmployeeOfferEnabled() && isisEmployeeRateTurnedOnFb;
        inputObservable = Observable.just(hotelDetailsInput).cache();
        hotelBrandString = hotelDetailsInput.brand().toLowerCase();
        hotelBrand = HotelMappersKt.mapToBrand(hotelDetailsInput.brand());
        maxNights = isInnBusinessUser ? businessStorage.getMaxNightsInnBusiness() : storage.getMaxNightsLeisure();

        maxArrivalDate = isInnBusinessUser ? businessStorage.getMaxArrivalDateInnBusiness() : storage.getMaxArrivalDateLeisure();

        innBusinessUserAccessLevel = businessStorage.getCustomerAccessLevel();

        channel = isInnBusinessUser ? Channel.BB.name() : Channel.PI.name();

        priceCapLocations = HDPExtensionsKt.getPriceCapLocations(businessStorage);

        deviceLanguage = deviceLocalProvider.getDeviceLanguage();
        deviceLocale = deviceLocalProvider.getDeviceLocale();
        country = deviceLocalProvider.getCountryIfRegion(deviceLocale).toLowerCase(deviceLocale);

        hotelInformationDomainObservable = graphQLHotelDetailsUseCase.fetchHotelInfoFromGQL(
                        country, hotelDetailsInput.hotelCode(), deviceLanguage)
                .toObservable().cache();

        allCheckInTimesInfo = retrieveCheckInCheckoutInfoFromFirebase(networkDisposable, crashlyticsLogger, contentRepository);

        listOfRatePlanCodes = isEmployeeRateEnabled ? List.of(EMPLOYEE_CODE) : null;


        getCustomerLoggedInState();

        makePromoAndAvailabilityCalls();

        ratesModelListObservableSetup();
    }

    private void makePromoAndAvailabilityCalls() {
        LocalDate arrivalDate = hotelDetailsInput.searchResultsInput() != null
                ? hotelDetailsInput.searchResultsInput().arrivalDate() : LocalDate.now();
        LocalDate departureDate = hotelDetailsInput.searchResultsInput() != null
                ? hotelDetailsInput.searchResultsInput().departureDate() : LocalDate.now().plusDays(1);
        roomSearchList = hotelDetailsInput.searchResultsInput() != null
                ? getRoomSearchCriteriaFromHotelDetails(hotelDetailsInput) : getDefaultRoomSearchCriteriaFromHotelDetails();

        hotelBookingAvailabilityStateObservable = graphQLHDPUseCase.fetchPromotionsAndHotelAvailability(
                new HotelAvailabilityRequestBody(
                        arrivalDate.toString(),
                        departureDate.toString(),
                        new HotelInfoDetails(hotelDetailsInput.hotelCode()),
                        roomSearchList,
                        new BookingChannelDetails(channel,
                                SUB_CHANNEL, deviceLanguage), hotelBrandString,
                        listOfRatePlanCodes, operaCompanyId),
                deviceLanguage, country, hotelDetailsInput.hotelCode(),
                channel, promoCode, skipPromotionsCheck).cache();

    }

    private void runPromoLogic(@NonNull GetStringResource getStringResource,
                               @NonNull SimplePersistenceManager storage,
                               @NonNull IsFeatureOn isFeatureOn) {

        String freeBreakfastPromoCode = storage.getFreeBreakfastPromotionCode();

        isAppPromotionalIncentiveActive = isFeatureOn.invoke(Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED)
                && Boolean.TRUE.equals(storage.isAppPromotionalIncentiveAvailable());

        isFreeBreakfastPromotionActive = CODE_FREE_BREAKFAST_INCENTIVE.equals(freeBreakfastPromoCode)
                && !isAppPromotionalIncentiveActive;

        // Set skip promotions check flag based on app incentive or free breakfast
        skipPromotionsCheck = isAppPromotionalIncentiveActive || isFreeBreakfastPromotionActive;

        if (isAppPromotionalIncentiveActive) {
            promoCode = getStringResource.invoke(Key.APP_PROMO_CODE);
        }

        if (isFreeBreakfastPromotionActive) {
            promoCode = freeBreakfastPromoCode;
        }
    }

    private void getCustomerLoggedInState() {
        networkDisposable.add(isCustomerLoggedIn.invoke()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    if (success) {
                        hasCustomerLoggedIn = true;
                    }
                }));
    }

    private Disposable bindLoadingView() {
        return hotelBookingAvailabilityStateObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(availabilityState -> {
                            if (availabilityState.isAvailabilityLoading()) {
                                view.shouldShowLoadingSpinner(true);
                            } else if (availabilityState.isAvailabilityStateValid()) {
                                view.shouldShowLoadingSpinner(false);
                            } else if (availabilityState.isHotelAvailabilityUnsuccessful()) {
                                view.shouldShowLoadingSpinner(false);
                            }
                        }
                );
    }

    @Override
    public void onAttachView(View view) {
        this.view = view;
        logViewingHotelDetails();
    }

    public Disposable[] bindAllCommonViewSubscriptions(View view) {

        setupRateClickObservable(view);

        return new Disposable[]{
                bindLoadingView(),
                bindHotelGalleryFromInput(view),
                bindImportantInfoUiModel(view),
                bindDiscountCodeUiModel(view),
                bindTopFacilitiesUiModel(view),
                bindRoomCriteria(), // migrated
                bindHotelCode(),
                bindToolbarTitles(view), // no migration
                bindHotelHeadingUiModelFromInput(view), // no migration
                bindAccessibilityUiModel(view), // migrated
                bindCallUsUiModel(view), // migrated
                bindReadMoreClicks(view), // migrated
                bindEditDatesClicks(view), // migrated
                bindCheckAvailabilityClicks(view), // migrated
                bindMainSelectRateClicks(view), // no migration
                bindHotelsNearByClicks(view), // no migration
                bindRoomFindOutMoreClicks(view), // no migration
                bindMakePhoneCallEvents(view), // no migration
                bindSendEmailEvents(view), // no migration
                bindOpenWebLinkEvents(view), // no migration
                bindEditGuestClick(view), // no migration
                bindSummaryOrRoomSelectionDisposable(view), // no migration
                bindOpenAccessibilityRoomTypeInfoClicks(view), // migrated
                bindMapUIModel(view), // migrated
                bindMapViewClicks(view), // migrated
                bindRestaurantContentUiModel(view), // migrated
                bindRestaurantGalleryOrHeaderUiModel(view), // migrated
                bindAnnouncementInfo(view), // migrated
                bindCoronavirusDismiss(view), // no migration
                bindCheckInOut(view),
                bindHotelFacilitiesUiModel(view),
                bindHotelFacilitiesClick(view),
                bindRoomTypesUiModel(view),
                bindRoomTypesClick(view),
                bindDateUiModel(view),
                bindHotelDateUiModelFromInput(view),
                bindParkingSectionUiModel(view),
                bindHotelParkingClick(view),
                bindAccessibilitySectionUiModel(view),
                bindHotelAccessibilityClick(view),
                bindRoomClassConfig(),
                bindDiscountCodeClick(view),
                bindEmployeeRateContinueClick(view)
        };
    }

    private Disposable bindHotelGalleryFromNetwork(@NonNull View view) {
        return hotelInformationDomainObservable.filter(allowIfHotelInfoStateIsSuccessful())
                .map(hotelInfo -> {
                    List<Image> images = new ArrayList<>();
                    if (hotelInfo.getGalleryImages() != null) {
                        images = toImages(hotelInfo.getGalleryImages());
                    }

                    return new HotelGalleryUiModel(
                            SECTION_ORDER_ZERO,
                            images,
                            hotelInfo.getName(),
                            HotelMappersKt.mapToBrand(hotelInfo.getBrand())
                    );
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::addUiModelToRecyclerView,
                        error ->
                                crashlyticsLogger.logException(error,
                                        "Error w/ Heading from Network w/Availability" + error.getMessage()));
    }

    private Disposable bindHotelGalleryFromInput(@NonNull View view) {
        return inputObservable.filter(inputObservable -> inputObservable.hotelImageUrl() != null)
                .map(hotelDetailsInput -> {
                    String hotelName = hotelDetailsInput.hotelName() != null ? hotelDetailsInput.hotelName() : EMPTY_STRING;
                    List<Image> images = hotelDetailsInput.hotelImageUrl() != null ? Collections.singletonList(
                            new Image(hotelDetailsInput.hotelImageUrl(), null)) : emptyList();
                    return new HotelGalleryUiModel(
                            SECTION_ORDER_ZERO,
                            images,
                            hotelName,
                            HotelMappersKt.mapToBrand(hotelDetailsInput.brand())
                    );
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::addUiModelToRecyclerView);
    }

    private List<Image> toImages(@NonNull List<GalleryImageDomain> galleryImageDomainList) {
        return HotelImageProcessor.convertToDisplayableImages(galleryImageDomainList);
    }

    private Disposable bindCheckInOut(@NonNull View view) {
        return hotelInformationDomainObservable
                .filter(allowIfHotelInfoStateIsSuccessful())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hotelInformationDomain -> updateCheckInCheckout(view, hotelInformationDomain.getBrand()),
                        error -> crashlyticsLogger.logException(error, "Error with Hotel Brand" + error.getMessage()));
    }

    private void updateCheckInCheckout(@NonNull View view, String hotelBrandString) {
        androidx.core.util.Pair<String, String> checkInOutValues =
                getCheckInCheckoutTimes(hotelBrandString, allCheckInTimesInfo, checkInCheckOutStringProvider, false);
        view.addUiModelToRecyclerView(
                buildCheckInOutModel(SECTION_ORDER_TEN, checkInOutValues.first, checkInOutValues.second)
        );
    }

    private Disposable bindRoomCriteria() {
        return hotelBookingAvailabilityStateObservable
                .subscribe(booking -> {
                            if (booking.isHotelAvailabilitySuccessfulHDP()) {
                                currentRoomCriteria = OperaCommonExtensionsKt
                                        .toRoomCriteria(Objects.requireNonNull(booking.getGetHotelAvailability()));
                                listOfRateClassification = booking.getGetRateInformation();
                            }
                        }
                );
    }

    public void updatedCustomerLoggedIn() {
        getCustomerLoggedInState();
    }

    @VisibleForTesting
    protected Disposable bindHotelCode() {
        return inputObservable.subscribe(
                onSuccess -> {
                    this.hotelcode = onSuccess.hotelCode();
                }, onError -> {
                });
    }

    private Disposable bindOpenAccessibilityRoomTypeInfoClicks(View view) {
        return view.onAccessibilityRoomTypeInfoClicked()
                .subscribe(event -> view.startRoomVariantDetailsBottomSheetFragment(hotelDetailsInput.hotelCode()));
    }

    private Disposable bindOpenWebLinkEvents(View view) {
        return view.onOpenWebLinkClicked()
                .subscribe(event -> view.openWebLink(event.getWebLink()));
    }

    private Disposable bindMakePhoneCallEvents(View view) {
        return view.onMakePhoneCallClicked()
                .subscribe(event -> view.makePhoneCall(event.getPhoneNumber()));
    }

    private Disposable bindSendEmailEvents(View view) {
        return view.onSendEmailClicked()
                .subscribe(event -> view.sendEmail(event.getEmailAddress()));
    }

    public Disposable[] bindAllBookingAvailabilityViewSubscriptions(View view) {
        return new Disposable[]{
                bindHotelGalleryFromNetwork(view),
                bindHotelHeadingUiModelFromNetwork(view),
                bindRatePlanOrFullyBookedUiModel(view),
//                bindRoomSubstitutionDisposable(view),
                clickImportantInfoDisposable(view)
        };
    }

    Disposable bindToolbarTitles(@NonNull View view) {
        if (hotelDetailsInput.searchResultsInput() != null) {
            return inputObservable
                    .map(input -> new Pair<>(getToolbarTitle(input),
                            messageProvider.getCommonStringsProvider().formattedInputSearchCriteria(input.searchResultsInput())))
                    .subscribe(pair -> view.showToolBarTitle(pair.getFirst()));
        }
        return null;
    }

    Disposable bindHotelHeadingUiModelFromNetwork(@NonNull View view) {
        if (hotelDetailsInput.searchResultsInput() == null) {
            return bindHotelHeadingUiModelFromNetworkNoAvailability(view);
        } else {
            return bindHotelHeadingUiModelFromNetworkWithAvailability(view);
        }
    }

    private Disposable bindHotelHeadingUiModelFromNetworkNoAvailability(View view) {
        return Observable.zip(
                        hotelInformationDomainObservable.filter(allowIfHotelInfoStateIsSuccessful()),
                        Observable.just(false),
                        inputObservable.map(input -> messageProvider.getCommonStringsProvider()
                                .formattedSearchCriteria(input.searchResultsInput())),
                        UiModelItemFactory.createHeadingUiModel(
                                SECTION_ORDER_ONE,
                                getDistanceFromSearchLocation()
                        )
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::addUiModelToRecyclerView,
                        error ->
                                crashlyticsLogger.logException(error,
                                        "Error with Hotel Heading from Network no Availability" + error.getMessage()));
    }

    private Disposable bindHotelHeadingUiModelFromInput(@NonNull View view) {
        return inputObservable.filter(inputObservable -> inputObservable.hotelImageUrl() != null)
                .zipWith(inputObservable.map(input -> messageProvider.getCommonStringsProvider()
                        .formattedSearchCriteria(input.searchResultsInput())), Pair::new)
                .map(UiModelItemFactory.createHeadingUiModelFromInput(SECTION_ORDER_ONE, getDistanceFromSearchLocation()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::addUiModelToRecyclerView);
    }

    private Disposable bindHotelDateUiModelFromInput(@NonNull View view) {
        return inputObservable
                .filter(inputObservable -> inputObservable.hotelImageUrl() != null)
                .map(input -> messageProvider.getCommonStringsProvider()
                        .formattedSearchCriteria(input.searchResultsInput()))
                .map(pair -> new DateGuestUiModel(SECTION_ORDER_TWO, pair.getFirst(), pair.getSecond()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::addUiModelToRecyclerView);
    }

    // The main mapping for the rooms
    public void ratesModelListObservableSetup() {
        networkDisposable.add(hotelBookingAvailabilityStateObservable
                .filter(state -> state.getAction() instanceof GraphQLHDPUseCase.AvailabilityAction.AvailabilitySuccess)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(availabilityState -> {
                            this.listOfRateClassification = availabilityState.getGetRateInformation();
                            this.roomTypeInfoDomain = availabilityState.getGetRoomTypeInformation();
                            this.promoCode = availabilityState.getGetPromoCodeFromAvailability();
                            this.promoKind = availabilityState.getGetPromoKindFromAvailability();
                            this.invalidDiscountCodeMessage = availabilityState.getInvalidDiscountCodeMessage();

                            stdListObservable = ratesToModelListItemsStandardObservable();
                            alterListObservable =  ratesToModelListItemsAlternateObservable();
                            rateBoxUiModelObservable = Observable.zip(alterListObservable, stdListObservable, (alt, std) -> {
                                alt.addAll(std);

                                Map<String, Integer> roomClassOrderMap = sortedRoomClassConfigs == null
                                        ? Collections.emptyMap()
                                        : sortedRoomClassConfigs.stream()
                                        .collect(Collectors.toMap(RoomClassConfig::getCode, RoomClassConfig::getOrder));

                                alt.sort(Comparator.comparingInt(list ->
                                        list.stream()
                                                .mapToInt(info -> roomClassOrderMap.getOrDefault(
                                                        info.getModel().roomVariant().getRoomClass(), Integer.MAX_VALUE))
                                                .min()
                                                .orElse(Integer.MAX_VALUE)
                                ));
                                return alt;
                            }).distinct().cache();

                            Disposable bindTrackingDisp = bindTracking();
                            if (bindTrackingDisp != null) {
                               networkDisposable.add(bindTrackingDisp);
                            }

                            Disposable bindRateDisp = bindRatePlanOrFullyBookedUiModel(view);
                            if (bindRateDisp != null) {
                                networkDisposable.add(bindRateDisp);
                            }
                        },
                        error -> {
                            crashlyticsLogger.log("Error in getting rates : " + error.getMessage());
                        }));
    }

    private Observable<List<List<RatesDisplayInfo>>> ratesToModelListItemsStandardObservable() {
        return mapToRatePlanOperaObservable()
                .zipWith(mapToRoomRateDomainObservable(), Pair::new)
                .flatMap(pair -> processAndGroupStandardRoomRates(pair.getFirst(), pair.getSecond()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<List<List<RatesDisplayInfo>>> processAndGroupStandardRoomRates(
            List<RatePlanOpera> ratePlansList, List<RoomRateDomain> roomRateDomainList) {

        return Observable.fromIterable(mapToRoomRatePlans(ratePlansList))
                .filter(roomRatePlan -> !roomRatePlan.isAlternativeRoomUpsell())
                .map(roomRatePlan -> createRatesDisplayInfoForStandardRate(roomRatePlan, roomRateDomainList))
                .groupBy(RatesDisplayInfo::getRoomVariantFromModel)
                .flatMapSingle(Observable::toList)
                .toList()
                .toObservable();
    }

    private RatesDisplayInfo createRatesDisplayInfoForStandardRate(RoomRatePlan roomRatePlan,
                                                                   List<RoomRateDomain> roomRateDomainList) {
        SelectedRate roomRate = roomRatePlan.getRoomRate();

        String promotionCode = getPromotionCode(roomRatePlan, roomRateDomainList);
        String promoTag = getPromoTagForRate(roomRate.code());

        RateBoxUiModel rateBoxUiModel = buildRateBoxUiModelForStandard(roomRatePlan, roomRate, false, promotionCode, promoTag);

        String lettingTypeCode = "";

        if (!roomRatePlan.getRooms().isEmpty()) {
            lettingTypeCode = roomRatePlan.getRooms().get(0).getLettingType().getCode();
        } else {
            crashlyticsLogger.log("No rooms in the rate plan");
        }

        return new RatesDisplayInfo(
                roomRatePlan.totalPrice(),
                lettingTypeCode,
                roomRate.code(),
                rateBoxUiModel
        );
    }

    private @NonNull RateBoxUiModel buildRateBoxUiModelForStandard(RoomRatePlan roomRatePlan,
                                                                   SelectedRate roomRate,
                                                                   Boolean isAlternate,
                                                                   String promotionCode,
                                                                   String promoTag) {
        String formattedBaseRate = getFormattedBaseRate(roomRatePlan);

        return RateBoxUiModel.builder()
                .rateType(roomRate.rateType())
                .pmsRoomType(roomRatePlan.getRooms().get(0).getLettingCode())
                .description(roomRate.description())
                .title(roomRate.rateName())
                .roomVariant(getRoomVariant(roomRatePlan.getRooms().get(0).getLettingCode(),
                        roomRatePlan.getRooms().get(0).getType(),
                        roomTypeInfoDomain))
                .alternativeType(isAlternate)
                .alternateRoomSelectionAvailable(showSelectLabel)
                .hasCityTax(roomRatePlan.totalCityTax().getAmount() > 0f)
                .price(PriceFormat.format(roomRatePlan.totalPrice(),
                        deviceLocalProvider))
                .formattedBaseRate(formattedBaseRate)
                .promotionCode(promotionCode)
                .promoTag(promoTag)
                .nights(hotelDetailsInput.searchResultsInput().nights())
                .rooms(hotelDetailsInput.searchResultsInput().numRooms())
                .rateClassification(roomRate.classification())
                .build();

    }

    private Observable<List<List<RatesDisplayInfo>>> ratesToModelListItemsAlternateObservable() {
        return mapToRatePlanOperaObservable()
                .zipWith(mapToRoomRateDomainObservable(), Pair::new)
                .flatMap(pair -> processAlternativeRoomRates(pair.getFirst(), pair.getSecond()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<List<List<RatesDisplayInfo>>> processAlternativeRoomRates(
            List<RatePlanOpera> ratePlansList, List<RoomRateDomain> roomRateDomainList) {
        List<RoomRatePlan> mappedRoomRatePlans = mapToRoomRatePlans(ratePlansList);
        List<RoomRatePlan> filteredAlternateRoomRatePlan = mappedRoomRatePlans.stream()
                .filter(RoomRatePlan::isAlternativeRoomUpsell).collect(Collectors.toList());

        return Observable.fromIterable(filteredAlternateRoomRatePlan)
                .flatMap(roomratePlan ->
                        createRatesDisplayInfoForAlternativeUpsell(roomratePlan, mappedRoomRatePlans, roomRateDomainList))
                // flatMap to handle multiple items per RoomRatePlan
                .groupBy(RatesDisplayInfo::getRoomClass)
                .flatMapSingle(Observable::toList)
                .toList()
                .toObservable();
    }

    private Observable<RatesDisplayInfo> createRatesDisplayInfoForAlternativeUpsell(RoomRatePlan roomRatePlan,
                                                                                    List<RoomRatePlan> alternativeRoomRatePlans,
                                                                                    List<RoomRateDomain> roomRateDomainList) {
        // This method now returns an Observable of RatesDisplayInfo,
        // as one RoomRatePlan can produce multiple for alternative upsells.
        List<RatesDisplayInfo> displayInfos = new ArrayList<>();

        String promotionCode = getPromotionCode(roomRatePlan, roomRateDomainList);
        String promoTag = getPromoTagForRate(roomRatePlan.getRoomRate().code());

        // group by letting code
        float totalOfStdRoom = HDPExtensionsKt.
                returnTotalAmountForStandardRooms(alternativeRoomRatePlans, roomRatePlan.getRoomRate().rateType());


        Map<String, Float> mapOfRoomClassToPrice = HDPExtensionsKt
                .convertToMapOfRoomClassToPrice(roomRatePlan.getRooms());

        for (RoomBooking roomBooking : roomRatePlan.getRooms()) {
            float amount = HDPExtensionsKt.getPriceBasedOnPmsRoomType(mapOfRoomClassToPrice, roomBooking.getType());
            float addedTotal = amount + totalOfStdRoom;
            PriceDomain totalRoomPrice = new PriceDomain(addedTotal, roomBooking.getDailyRates().get(0).getPrice().getCurrency());

            RateBoxUiModel rateBoxUiModel =
                    buildRateBoxUiModelForAlternative(roomRatePlan, roomBooking, totalRoomPrice, promotionCode, promoTag);

            displayInfos.add(new RatesDisplayInfo(
                    totalRoomPrice,
                    roomBooking.getLettingCode(),
                    roomRatePlan.getRoomRate().code(),
                    rateBoxUiModel
            ));
        }

        List<RatesDisplayInfo> ratesDisplayInfos = HDPExtensionsKt
                .filterAndReturnOnlyOneRateDisplay(displayInfos);

        return Observable.fromIterable(ratesDisplayInfos);
    }

    private RateBoxUiModel buildRateBoxUiModelForAlternative(RoomRatePlan roomRatePlan,
                                                             RoomBooking roomBooking,
                                                             PriceDomain totalRoomPrice,
                                                             String promotionCode,
                                                             String promoTag) {

        String formattedBaseRate = getFormattedBaseRate(roomRatePlan);

        SelectedRate roomRate = roomRatePlan.getRoomRate();
        return RateBoxUiModel.builder()
                .rateType(roomRate.rateType())
                .pmsRoomType(roomBooking.getLettingCode())
                .description(roomRate.description())
                .title(roomRate.rateName())
                .roomVariant(getRoomVariant(
                        roomBooking.getLettingCode(),
                        roomBooking.getType(),
                        this.roomTypeInfoDomain
                ))
                .alternativeType(true)
                .alternateRoomSelectionAvailable(this.showSelectLabel)
                .hasCityTax(roomRatePlan.totalCityTax().getAmount() > 0f)
                .price(PriceFormat.format(totalRoomPrice, this.deviceLocalProvider))
                .formattedBaseRate(formattedBaseRate)
                .promotionCode(promotionCode)
                .promoTag(promoTag)
                .nights(this.hotelDetailsInput.searchResultsInput().nights())
                .rooms(this.hotelDetailsInput.searchResultsInput().numRooms())
                .rateClassification(roomRate.classification())
                .build();
    }

    public Observable<HotelBookingAvailabilityState> getValidAvailabilityState(
            Observable<HotelBookingAvailabilityState> hotelBookingAvailabilityStateObservable) {
        return hotelBookingAvailabilityStateObservable
                .filter(HotelBookingAvailabilityState::isAvailabilityStateValid);
    }

    private Observable<List<RoomRateDomain>> mapToRoomRateDomainObservable() {
        return getValidAvailabilityState(hotelBookingAvailabilityStateObservable)
                .map(it -> Objects.requireNonNull(it.getGetHotelAvailability()).getRoomRateDomainList());
    }

    public Observable<List<RatePlanOpera>> mapToRatePlanOperaObservable() {
        return getValidAvailabilityState(hotelBookingAvailabilityStateObservable)
                .map(it -> OperaCommonExtensionsKt.convertToRatePlansOpera(Objects.requireNonNull(it.getGetHotelAvailability())));
    }

    @Nullable
    private String getFormattedBaseRate(RoomRatePlan roomRatePlan) {
        String formattedBaseRate = null;
        Float baseRateAmount = roomRatePlan.baseRateAmount();
        if (baseRateAmount != null) {
            formattedBaseRate = PriceFormat.format(baseRateAmount,
                    roomRatePlan.totalPrice().getCurrency(), deviceLocalProvider);
        }
        return formattedBaseRate;
    }

    @Nullable
    private String getPromotionCode(RoomRatePlan roomRatePlan, List<RoomRateDomain> roomRateDomainList) {
        return roomRateDomainList.stream()
                .filter(domain -> domain.getRatePlanCode().equals(roomRatePlan.getRatePlanCode()))
                .map(RoomRateDomain::getPromotionCode)
                .filter(code -> Objects.equals(code, promoCode))
                .findFirst()
                .orElse(null);
    }



    private String getPromoTagForRate(String rateCode) {
        if (listOfRateClassification == null || rateCode == null) {
            return EMPTY_STRING;
        }

        return listOfRateClassification.stream()
                .filter(rate -> rateCode.equals(rate.getRateClassification()))
                .map(RateClassificationsDomain::getRateTags)
                .filter(tags -> !tags.isEmpty())
                .map(tags -> tags.get(0))
                .findFirst()
                .orElse(EMPTY_STRING);
    }

    private RoomVariant getRoomVariant(String pmsRoomType, String roomClass, List<RoomTypeInfoDomain> listOfRoomTypeInfoDomain) {
        String roomLabel = HDPExtensionsKt.findRoomLabelFromRoomTypeInfo(
                listOfRoomTypeInfoDomain, pmsRoomType,
                roomClass);
        if (roomLabel.equals(EMPTY_STRING)) {
            roomLabel = stringResourceProvider.getString(R.string.hotel_details_standard_rooms_heading);
        }
        return new RoomVariant(roomLabel, roomClass);
    }

    @VisibleForTesting
    Disposable bindHotelHeadingUiModelFromNetworkWithAvailability(@NonNull View view) {
        return Observable.zip(
                        hotelInformationDomainObservable.filter(allowIfHotelInfoStateIsSuccessful()),
                        hotelBookingAvailabilityStateObservable.filter(allowIfAvailabilityStateIsSuccessfulOrFullyBooked())
                                .map(HotelBookingAvailabilityState::isLimitedAvailabilityTrue),
                        inputObservable.map(input -> messageProvider.getCommonStringsProvider()
                                .formattedSearchCriteria(
                                        input.searchResultsInput())),
                        UiModelItemFactory.createHeadingUiModel(
                                SECTION_ORDER_ONE,
                                getDistanceFromSearchLocation()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::addUiModelToRecyclerView,
                        error ->
                                crashlyticsLogger.logException(error,
                                        "Error w/ Heading from Network w/Availability" + error.getMessage()));
    }

    Disposable bindDateUiModel(@NonNull View view) {
        return inputObservable.map(input -> messageProvider.getCommonStringsProvider()
                        .formattedSearchCriteria(input.searchResultsInput()))
                .map(pair -> new DateGuestUiModel(SECTION_ORDER_TWO, pair.getFirst(), pair.getSecond()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::addUiModelToRecyclerView);
    }

    @VisibleForTesting
    Disposable bindRatePlanOrFullyBookedUiModel(@NonNull View view) {
        if (hotelDetailsInput.searchResultsInput() == null) {
            return hotelInformationDomainObservable
                    .filter(allowIfHotelInfoStateIsSuccessful())
                    .map(__ -> FullyBookedUiModel.create(SECTION_ORDER_FIVE, FullyBookedUiModel.Type.CHECK_AVAILABILITY))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(success -> view.addUiModelToRecyclerView(success),
                            error -> crashlyticsLogger.logException(error, "Error with RatePlan/Fully Booked" + error.getMessage()));
        }
        if (rateBoxUiModelObservable != null) {
            return rateBoxUiModelObservable
                    .map(rateGroups -> rateGroups.stream()
                            .map(group -> group.stream()
                                    .sorted((r1, r2) -> Boolean.compare(
                                            !(r1.getModel().title() != null
                                                    && r1.getModel().title().toLowerCase().contains(BUSINESS_RATE_NAME.toLowerCase())),
                                            !(r2.getModel().title() != null
                                                    && r2.getModel().title().toLowerCase().contains(BUSINESS_RATE_NAME.toLowerCase()))
                                    ))
                                    .collect(Collectors.toList()))
                            .collect(Collectors.toList()))
                    .distinct()
                    .map(this::toModel)
                    .map(this::toCityTaxPair)
                    .map(roomRatesCityTaxPair -> (UiModelListItem) RoomRatesUiModel.builder()
                            .roomRates(roomRatesCityTaxPair.getFirst())
                            .showCityTaxMessage(roomRatesCityTaxPair.getSecond())
                            .invalidDiscountCodeMessage(invalidDiscountCodeMessage)
                            .order(SECTION_ORDER_SIX).build())
                    .defaultIfEmpty(FullyBookedUiModel.create(SECTION_ORDER_SIX, FullyBookedUiModel.Type.FULLY_BOOKED))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            view::addUiModelToRecyclerView,
                            error -> crashlyticsLogger.logException(error, error.getMessage())
                    );
        } else {
            return null;
        }
    }

    private List<List<RateBoxUiModel>> toModel(List<List<RatesDisplayInfo>> combinedRateDisplayInfo) {
        List<List<RateBoxUiModel>> modelListOutput = new ArrayList<>();
        for (List<RatesDisplayInfo> ratesDisplayInfos : combinedRateDisplayInfo) {
            List<RateBoxUiModel> output = new ArrayList<>();
            for (RatesDisplayInfo ratesDisplayInfo : ratesDisplayInfos) {
                output.add(ratesDisplayInfo.getModel());
            }
            if (!modelListOutput.contains(output)) {
                modelListOutput.add(output);
            }
        }
        return modelListOutput;
    }

    @VisibleForTesting
    protected List<RoomRatePlan> mapToRoomRatePlans(List<RatePlanOpera> bookingRatePlans) {
        List<RoomRatePlan> roomRatePlans = new ArrayList<>();
        setSelectOrBookFlag(bookingRatePlans);
        for (RatePlanOpera bookingRatePlan : bookingRatePlans) {
            boolean isEmployeeCellCodePresent = OperaCommonExtensionsKt.isEmployeeCellCode(bookingRatePlan.getCellCode());
            String rateName = findRateNameFromRatesInformation(bookingRatePlan.getRateType(),
                    isEmployeeCellCodePresent, listOfRateClassification);
            String rateDescription = findRateDescriptionFromRatesInformation(
                    bookingRatePlan.getRateType(), isEmployeeCellCodePresent);
            SelectedRate selectedRate = SelectedRate.createFrom(bookingRatePlan, rateName,
                    rateDescription, bookingRatePlan.getRateType());
            roomRatePlans.addAll(RoomRatePlan.Factory.createListFrom(bookingRatePlan, hotelBrand, selectedRate));
        }

        return roomRatePlans;
    }

    private void setSelectOrBookFlag(List<RatePlanOpera> bookingRatePlans) {
        showSelectLabel = false;
        isAccessibleFlow = false;
        if (bookingRatePlans.get(0).getAccessibleRoomList().size() > 0
        && HDPExtensionsKt.findIfSpecialRequestContainsWetOrLow(bookingRatePlans)) {
            showSelectLabel = true;
            isAccessibleFlow = true;
        }
        if (bookingRatePlans.get(0).getTwinRoomList().size() > 0) {
            showSelectLabel = true;
        }
    }

    private Pair<List<List<RateBoxUiModel>>, Boolean> toCityTaxPair(List<List<RateBoxUiModel>> roomRatesModel) {
        for (List<RateBoxUiModel> room : roomRatesModel) {
            for (RateBoxUiModel rate : room) {
                if (rate.hasCityTax()) {
                    return new Pair(roomRatesModel, true);
                }
            }
        }

        return new Pair(roomRatesModel, false);
    }

    private Disposable bindAccessibilityUiModel(@NonNull View view) {
        return inputObservable
                .zipWith(hotelInformationDomainObservable.filter(allowIfHotelInfoStateIsSuccessful()), Pair::new)
                .filter(input -> input.getFirst().searchResultsInput().hasAccessibleRoom()
                        && input.getSecond().getHotelFacilities() != null)
                .flatMap(hotelInfo -> {
                    String phoneNumber = hotelInfo.getSecond().getContactDetails() != null
                            ? hotelInfo.getSecond().getContactDetails().getPhone() : null;
                    String email = messageProvider.accessibilityEmailAddress();
                    if (phoneNumber != null && !phoneNumber.isEmpty()) {
                        String callButtonLabel = messageProvider.accessibilityCallButtonLabel(true);
                        String description = messageProvider.accessibilityCallDescription(true, email);
                        return Observable.just(
                                new AccessibilityInformationUiModel(SECTION_ORDER_SEVEN, phoneNumber, callButtonLabel, email, description,
                                        stringResourceProvider.getString(R.string.accessibility_info_web_url)));
                    } else {
                        return Observable.fromCallable(() -> getStringResource.invoke(Key.CUSTOMER_SERVICE_NUMBER))
                                .map(customerServicePhone -> {
                                    String callButtonLabel = messageProvider.accessibilityCallButtonLabel(false);
                                    String description = messageProvider.accessibilityCallDescription(false, email);
                                    return new AccessibilityInformationUiModel(
                                            SECTION_ORDER_SEVEN, customerServicePhone, callButtonLabel, email,
                                            description, stringResourceProvider.getString(R.string.accessibility_info_web_url));
                                });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        view::addUiModelToRecyclerView,
                        error -> crashlyticsLogger.logException(error, error.getMessage()));
    }

    @VisibleForTesting
    protected Disposable bindTopFacilitiesUiModel(@NonNull View view) {
        return hotelInformationDomainObservable.filter(allowIfHotelInfoStateIsSuccessful())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hotelInfo -> {
                    if (hotelInfo.getHotelFacilities() != null && !hotelInfo.getHotelFacilities().isEmpty()) {

                        FacilitiesUiModel facilitiesUiModel = FacilitiesUiModel
                                .builder()
                                .order(SECTION_ORDER_NINE)
                                .hotelDescription(messageProvider.shortDescription(hotelInfo.getHotelDescription()))
                                .facilities(displayableFacilities(
                                        filterFacilities(hotelInfo.getHotelFacilities()))
                                )
                                .build();
                        view.addUiModelToRecyclerView(facilitiesUiModel);
                    }
                }, error -> crashlyticsLogger.logException(error, "Error with Facilities UI Model" + error.getMessage()));
    }

    private float getDistanceFromSearchLocation() {
        if (deviceLocalProvider.isLanguageGerman()) {
            return (float) (hotelDetailsInput.distanceFromSearchedLocation() * MILES_TO_KM);
        } else {
            return hotelDetailsInput.distanceFromSearchedLocation();
        }
    }

    private List<HotelFacilityDomain> filterFacilities(List<HotelFacilityDomain> rawList) {
        List<HotelFacilityDomain> filteredList = new ArrayList<>();

        filteredList.addAll(addFacility(rawList, Constants.PARKING_COP_CODE, Constants.PARKING_CPP_CODE, Constants.PARKING_CPF_CODE));
        filteredList.addAll(addRestaurantFacility(rawList));
        filteredList.addAll(addFacility(rawList, Constants.WIFI_WIA_CODE, Constants.WIFI_HUW_CODE));
        filteredList.addAll(addFacility(rawList, Constants.AIRCO_ACO_CODE, Constants.AIRCO_HAC_CODE));
        filteredList.addAll(addFacility(rawList, Constants.LIFT_LFT_CODE, Constants.LIFT_HUL_CODE));

        if (filteredList.size() > Constants.MAX_NR_OF_FACILITIES) {
            return filteredList.subList(0, Constants.MAX_NR_OF_FACILITIES);
        }

        return filteredList;
    }

    private List<HotelFacilityDomain> addRestaurantFacility(List<HotelFacilityDomain> facilities) {
        List<HotelFacilityDomain> filtered = new ArrayList<>();

        HotelFacilityDomain facility = getRestaurantFacility(facilities);

        if (facility != null) {
            filtered.add(
                    new HotelFacilityDomain(facility.getDescription(),
                            computeRestaurantTitle(facility),
                            facility.getCode(),
                            facility.isVisible(),
                            facility.getIcon())
            );
        }

        return filtered;
    }

    private HotelFacilityDomain getRestaurantFacility(List<HotelFacilityDomain> facilities) {
        return facilities.stream()
                .filter(it -> it.getCode().equals(Constants.RESTAURANT_RES_CODE)
                        || it.getCode().equals(Constants.RESTAURANT_HRS_CODE)
                        || it.getCode().equals(Constants.RESTAURANT_ZBF_CODE))
                .findFirst()
                .orElse(null);
    }

    private List<HotelFacilityDomain> addFacility(List<HotelFacilityDomain> facilities, String... codes) {
        List<HotelFacilityDomain> filtered = new ArrayList<>();
        for (HotelFacilityDomain item : facilities) {
            for (String code : codes) {
                if (item.isVisible() && item.getCode().equals(code)) {
                    filtered.add(item);
                }
            }
        }
        return filtered;
    }

    private String computeRestaurantTitle(HotelFacilityDomain item) {
        StringBuilder restaurantTitle = new StringBuilder();


        if (item.getCode().equals(Constants.RESTAURANT_RES_CODE) || item.getCode().equals(Constants.RESTAURANT_HRS_CODE)) {
            restaurantTitle.append(Constants.RESTAURANT);
        }

        if (item.getCode().equals(Constants.RESTAURANT_ZBF_CODE)) {
            if (restaurantTitle.length() > 0) {
                restaurantTitle.append("/");
            }
            restaurantTitle.append(Constants.BREAKFAST);
        }

        if (restaurantTitle.length() > 0) {
            restaurantTitle.append(StringUtils.SPACE);
            restaurantTitle.append(Constants.AVAILABLE);
        }
        return restaurantTitle.toString();
    }

    private void storeHotelPaymentProvider() {
        storage.storePaymentProvider("3CP");
    }

    private void storeAuthenticationRequiredAndHotelCountry() {
        hotelInformationDomainObservable.filter(allowIfHotelInfoStateIsSuccessful())
                .map(response -> {
                    storage.storeHotelCountry("GB");
                    storage.storeAuthenticationRequiredFlag(false);
                    return response;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                        },
                        error -> crashlyticsLogger.logException(error, "Error with Store Authentication " + error.getMessage()));
    }

    public Disposable bindAnnouncementInfo(@NonNull View view) {
        return hotelInformationDomainObservable.filter(allowIfHotelInfoStateIsSuccessful())
                .filter(hotelInformationDomain ->
                        showAnnouncement(hotelInformationDomain.getAnnouncement(),
                                Objects.requireNonNull(hotelDetailsInput.searchResultsInput())))
                .flatMap(hotelInformationDomain -> {
                            String string = HtmlUtils.parseTags(
                                    Objects.requireNonNull(hotelInformationDomain.getAnnouncement()).getText()).toString();
                            return Observable.just(
                                    new CoronavirusUiModel(SECTION_ORDER_FIVE, string.replaceAll("[\n\r]$", ""))
                            );
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::addUiModelToRecyclerView,
                        error -> crashlyticsLogger.logException(error, "Error with Corona Virus Ui Model " + error.getMessage()));
    }

    @VisibleForTesting
    static boolean showAnnouncement(@Nullable AnnouncementDomain announcement, @NonNull SearchResultsInput searchInput) {
        if (announcement == null || announcement.getShowAnnouncement().equals("false") || searchInput == null
                || announcement.getText().isEmpty() || announcement.getText().isBlank()) {
            return false;
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DateFormat.SLASHED_DAY_MONTH_YEAR);
        if (!announcement.getStartDate().equals("") && !announcement.getEndDate().equals("")) {
            LocalDate startDate = LocalDate.parse(announcement.getStartDate(), df);
            LocalDate endDate = LocalDate.parse(announcement.getEndDate(), df);
            // check do dates overlap
            LocalDate arrival = searchInput.arrivalDate();
            LocalDate departure = searchInput.departureDate();
            return !arrival.isAfter(endDate) && !departure.isBefore(startDate);
        } else {
            // no date constraint
            return true;
        }
    }

    @VisibleForTesting
    protected Disposable bindImportantInfoUiModel(@NonNull View view) {
        if (hotelDetailsInput.searchResultsInput() == null) {
            return null;
        }
        return hotelInformationDomainObservable.filter(allowIfHotelInfoStateIsSuccessful())
                .map(hotelInfo -> {
                    ImportantInfoDomain filteredImportantInfo = HDPExtensionsKt.filterImportantInfoByDate(
                            hotelInfo.getImportantInfo(), hotelDetailsInput.searchResultsInput());
                    return filteredImportantInfo == null ? null : ImportantInfoUiModel.builder()
                            .order(SECTION_ORDER_THREE)
                            .count(filteredImportantInfo.getInfoItems().size())
                            .build();
                })
                .filter(Objects::nonNull)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::addUiModelToRecyclerView,
                        error -> crashlyticsLogger.logException(error, "Error with Important Info Ui " + error.getMessage()));
    }

    @VisibleForTesting
    protected Disposable bindDiscountCodeUiModel(@NonNull View view) {
        if (!isSingleUseDiscountBoxVisible || isInnBusinessUser) {
            return null;
        }
        // Subscribe to valid hotel booking availability state and update the discount code UI model only when state is valid
        return getValidAvailabilityState(hotelBookingAvailabilityStateObservable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(availabilityState -> {
                    try {
                        // Determine if discount code box should be visible:
                        // Hide if sitewide promotion, app incentive, free breakfast, or employee offer is active
                        boolean shouldShowDiscountBox = !availabilityState.getSitewidePromoActive()
                                && !isAppPromotionalIncentiveActive
                                && !isFreeBreakfastPromotionActive
                                && !isEmployeeRateEnabled;

                        // Show discount code box with applied code or empty for user to enter
                        String appliedCode = promoCode != null && !promoCode.isEmpty() ? promoCode : EMPTY_STRING;

                        DiscountCodeUiModel discountCodeUiModel = DiscountCodeUiModel.builder()
                                .order(SECTION_ORDER_FOUR)
                                .appliedDiscountCode(appliedCode)
                                .visible(shouldShowDiscountBox)
                                .build();

                        view.addUiModelToRecyclerView(discountCodeUiModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @VisibleForTesting
    protected Disposable bindMapUIModel(@NonNull View view) {
        return inputObservable
                .zipWith(hotelInformationDomainObservable.filter(allowIfHotelInfoStateIsSuccessful()), Pair::new)
                .map(UiModelItemFactory.createMapUiModel(messageProvider, SECTION_ORDER_EIGHT))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> view.addUiModelToRecyclerView(success),
                        error -> crashlyticsLogger.logException(error, "Error with Map Ui" + error.getMessage()));
    }

    private Disposable bindRestaurantGalleryOrHeaderUiModel(@NonNull View view) {
        return Observable.zip(
                        hotelInformationDomainObservable.compose(
                                transformToImageUrlsList(HDPExtensionsKt.FILTER_RESTAURANT_IMAGES_BY_TAG)),
                        hotelBookingAvailabilityStateObservable.filter(allowIfAvailabilityStateIsSuccessfulOrFullyBooked()),
                        hotelInformationDomainObservable,
                        UiModelItemFactory.createRestaurantHeaderOrGalleryUiModel(SECTION_ORDER_FIFTEEN))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::addUiModelToRecyclerView,
                        error -> crashlyticsLogger.logException(error, "Error with Restaurant Gallery" + error.getMessage()));
    }

    @VisibleForTesting
    protected Disposable bindRestaurantContentUiModel(@NonNull View view) { //Restaurant Info Call
        return hotelInformationDomainObservable.filter(allowIfHotelInfoStateIsSuccessful())
                .map(restaurantTabbedContentUiModelMapper())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::addUiModelToRecyclerView,
                        error -> crashlyticsLogger.logException(error, "Error with Restaurant Content" + error.getMessage()));
    }

    private Function<HotelInformationDomain, UiModelListItem> restaurantTabbedContentUiModelMapper() {
        return hotelInfo -> {
            int listOrder = SECTION_ORDER_SIXTEEN;

            if (priceCapLocations != null) {
                dinnerAllowance = HDPExtensionsKt.getDinnerAllowance(priceCapLocations,
                        hotelInfo.getCounty(), hotelInfo.getAddress().getCountry());
            }

            if (hotelInfo.getRestaurantInfo() == null) {
                return PageInfoUiModel.builder().order(listOrder)
                        .contents(messageProvider.noBreakFastMessage()).build();
            } else {
                if (hotelInfo.getRestaurantInfo() != null && !hotelInfo.getRestaurantInfo().getName().equals("N/A")) {
                    List<String> tabTitlesList = Arrays.asList(messageProvider.getFoodOptionsTitle(),
                            hotelInfo.getRestaurantInfo().getName());

                    List<List<Content>> tabContents = Arrays.asList(
                            getFoodOptionsContent(hotelInfo.getRestaurantInfo().getMenus()),
                            getRestaurantContent(hotelInfo.getRestaurantInfo().getRestaurantDescription()));
                    return TabbedContentUiModel.builder().imageUrl(Urls.CONTENT_BASE_URL + hotelInfo.getRestaurantInfo().getLogoSrc())
                            .tabTitles(tabTitlesList).tabContentList(tabContents).order(listOrder).hubStyle(false).build();
                } else if (hotelInfo.getRestaurantInfo() == null) {
                    return PageInfoUiModel.builder().order(listOrder)
                            .contents(getFoodOptionsContent(hotelInfo.getRestaurantInfo().getMenus())).build();
                } else {
                    return PageInfoUiModel.builder()
                            .order(listOrder).imageUrl(Urls.CONTENT_BASE_URL + hotelInfo.getRestaurantInfo().getLogoSrc())
                            .contents(getRestaurantContent(hotelInfo.getRestaurantInfo().getRestaurantDescription())).build();
                }
            }
        };
    }

    private Disposable bindParkingSectionUiModel(@NonNull View view) {
        return hotelInformationDomainObservable
                .filter(allowIfHotelInfoStateIsSuccessful())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success ->
                                view.addUiModelToRecyclerView(new HotelParkingUiModel(SECTION_ORDER_THIRTEEN)),
                        error -> crashlyticsLogger.logException(error, "Parking model error" + error.getMessage()));
    }

    protected Disposable bindHotelParkingClick(@NonNull View view) {
        return view.onClickParking()
                .flatMap(__ -> hotelInformationDomainObservable.filter(allowIfHotelInfoStateIsSuccessful()))
                .subscribe(state -> {
                    view.showParkingBottomSheet(new HotelParkingModel(
                            messageProvider.parkingInfoContents(state.getParkingDescription())));

                });
    }

    private Disposable bindAccessibilitySectionUiModel(@NonNull View view) {
        return hotelInformationDomainObservable
                .filter(allowIfHotelInfoStateIsSuccessful())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success ->
                                view.addUiModelToRecyclerView(new HotelAccessibilityUiModel(SECTION_ORDER_FOURTEEN)),
                        error -> crashlyticsLogger.logException(error, "Accessibility model error" + error.getMessage()));
    }

    protected Disposable bindHotelAccessibilityClick(@NonNull View view) {
        return view.onClickAccessibility()
                .flatMap(__ -> hotelInformationDomainObservable.filter(allowIfHotelInfoStateIsSuccessful()))
                .subscribe(state -> {
                    view.showAccessibilityBottomSheet();
                });
    }

    protected Disposable bindDiscountCodeClick(@NonNull View view) {
        return view.onClickDiscountCode()
                .subscribe(__ -> {
                    if (hotelDetailsInput.searchResultsInput() != null) {
                        DiscountCodeInput input = createDiscountCodeInput();
                        view.showDiscountCodeBottomSheet(input);
                    }
                });
    }

    private DiscountCodeInput createDiscountCodeInput() {
        LocalDate arrivalDate = LocalDate.now();
        LocalDate departureDate = LocalDate.now().plusDays(1);

        if (hotelDetailsInput.searchResultsInput() != null) {
            arrivalDate = hotelDetailsInput.searchResultsInput().arrivalDate();
            departureDate = hotelDetailsInput.searchResultsInput().departureDate();
        }

        List<RoomSearch> roomSearchData = roomSearchList != null
                ? roomSearchList
                : getDefaultRoomSearchCriteriaFromHotelDetails();

        // Convert RoomSearch objects to RoomSearchData objects for DiscountCodeInput
        List<RoomSearchData> roomSearchDataList = new ArrayList<>();
        for (RoomSearch roomSearch : roomSearchData) {
            roomSearchDataList.add(new RoomSearchData(
                    roomSearch.getAdultsNumber(),
                    roomSearch.getChildrenNumber(),
                    roomSearch.getCotRequired(),
                    roomSearch.getRoomType()
            ));
        }

        return new DiscountCodeInput(
                hotelDetailsInput.hotelCode(),
                arrivalDate.toString(),
                departureDate.toString(),
                roomSearchDataList,
                channel,
                hotelBrandString,
                operaCompanyId,
                promoCode != null && !promoCode.isEmpty() ? promoCode : null,
                promoSuccessMessage != null && !promoSuccessMessage.isEmpty() ? promoSuccessMessage : null
        );
    }

    public void updateAvailabilityWithDiscountedRates(HotelBookingAvailabilityState newAvailabilityState,
                                                      @Nullable String successMessage) {

        if (newAvailabilityState == null || !newAvailabilityState.isHotelAvailabilitySuccessfulHDP()) {
            crashlyticsLogger.log("Invalid availability state received from discount code");
            return;
        }

        // Replace the cached observable with new discounted data
        hotelBookingAvailabilityStateObservable = Observable.just(newAvailabilityState).cache();

        // Update metadata from the new state
        this.listOfRateClassification = newAvailabilityState.getGetRateInformation();
        this.roomTypeInfoDomain = newAvailabilityState.getGetRoomTypeInformation();

        // Update promo code from the new state (will be empty when discount code is removed)
        this.promoCode = newAvailabilityState.getGetPromoCodeFromAvailability();
        this.promoKind = newAvailabilityState.getGetPromoKindFromAvailability();
        this.promoSuccessMessage = successMessage != null ? successMessage : EMPTY_STRING;
        this.invalidDiscountCodeMessage = newAvailabilityState.getInvalidDiscountCodeMessage();

        // Rebuild rate observables with the new discounted data
        ratesModelListObservableSetup();

        // Also rebuild the rate click observable to work with new availability data
        if (view != null) {
            // Refresh the discount code UI model to reflect current state
            viewDisposable.add(bindDiscountCodeUiModel(view));
            view.rebindAllSubscriptions();
        }
    }

    public void trackDiscountCodeBoxSuccess(@NonNull String promoCode,
                                            @Nullable String promoKind) {
        adobeAnalytics.trackAction(
                AnalyticsConstants.Action.DISCOUNT_CODE_BOX,
                new DiscountCodeAnalyticsData(
                        promoCode,
                        promoKind,
                        true,
                        null
                )
        );
    }

    public void trackDiscountCodeBoxError(@NonNull String promoCode,
                                          @Nullable String errorMessage,
                                          @Nullable String promoKind) {
        adobeAnalytics.trackAction(
                AnalyticsConstants.Action.DISCOUNT_CODE_BOX,
                new DiscountCodeAnalyticsData(
                        promoCode,
                        promoKind,
                        false,
                        errorMessage
                )
        );
    }

    protected Disposable bindEmployeeRateContinueClick(@NonNull View view) {
        return view.onClickEmployeeContinue()
                .subscribe(event -> {
                   if (event.isAccessibleFlow()) {
                       view.startBathroomSelectionActivity(event.getBathroomSelectionInput());
                   } else if (event.getSelectOrBook()) {
                       view.startAlternativeRoomSelectionActivity(event.getBathroomSelectionInput());
                   } else {
                       makeHoldBookingCall(view, event.getBathroomSelectionInput());
                   }
                },
                    error -> crashlyticsLogger.logException(error, "Employee Rate Click error" + error.getMessage()));
    }

    private Disposable bindCallUsUiModel(@NonNull View view) {
        return hotelInformationDomainObservable.filter(allowIfHotelInfoStateIsSuccessful())
                .map(hotelInfoState -> {
                            final boolean numberFreeOfCharge = (hotelDetailsInput.searchResultsInput() != null
                                    && hotelDetailsInput.searchResultsInput().hasAccessibleRoom())
                                    || hotelDetailsInput.searchResultsInput() == null;
                            return buildCallUiModel(SECTION_ORDER_SEVENTEEN, !numberFreeOfCharge,
                                    mapToHotelContactDetailsDomainOpera(hotelInfoState.getContactDetails()),
                                    getStringResource, messageProvider.getCommonStringsProvider());
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::addUiModelToRecyclerView,
                        error -> crashlyticsLogger.logException(error, "Error with Call Us" + error.getMessage()));
    }

    public Disposable bindReleaseBooking(@NonNull View view, @NonNull String basketReference) {
        return graphQLHDPUseCase.cancelOnHoldReservation(basketReference, hotelcode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> view.reBindBookingAvailabilitySubscriptions(),
                        throwable -> view.reBindBookingAvailabilitySubscriptions()
                );
    }

    public Disposable bindHotelAvailability(@NonNull View view, @Nullable LocalDate arrival, @Nullable LocalDate departure,
                                            @Nullable List<RoomCriteria> roomsCriteria) {
        return hotelInformationDomainObservable.filter(allowIfHotelInfoStateIsSuccessful())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hotelInfo -> {
                            LocalDate arrivalDate = (arrival != null) ? arrival
                                    : hotelDetailsInput.searchResultsInput().arrivalDate();
                            LocalDate departureDate = (departure != null) ? departure
                                    : hotelDetailsInput.searchResultsInput().departureDate();

                            // Determine promo code to pass after date change:
                            // - App Incentive/Free Breakfast: skipPromotionsCheck = true, pass the code
                            // - User discount code (UNIQUE/GENERIC): Pass to validate for new dates
                            // - Sitewide: Pass null to let API re-detect (don't validate and show error)
                            String promoCodeToPass = promoCode;
                            if (!skipPromotionsCheck && PROMO_KIND_SITE_WIDE.equals(promoKind)) {
                                // Don't pass sitewide promo code - let API re-detect it
                                // This prevents showing error messages for expired sitewide promos
                                promoCodeToPass = null;
                            }

                            if (roomsCriteria != null) {
                                currentRoomCriteria = roomsCriteria;
                                roomSearchList = HDPExtensionsKt.toRoomSearchCriteria(roomsCriteria);
                                hotelDetailsInput = HotelDetailsInput.fromSearchPayload(getSearchPayload(arrival, departure,
                                        hotelInfo.getName(), roomsCriteria), hotelDetailsInput.hotelCode()).build();
                                hotelBookingAvailabilityStateObservable = graphQLHDPUseCase
                                        .fetchPromotionsAndHotelAvailability(
                                                new HotelAvailabilityRequestBody(
                                                        arrivalDate.toString(),
                                                        departureDate.toString(),
                                                        new HotelInfoDetails(hotelDetailsInput.hotelCode()),
                                                        roomSearchList, new BookingChannelDetails(
                                                        isInnBusinessUser ? Channel.BB.name() : Channel.PI.name(), SUB_CHANNEL,
                                                        deviceLanguage), hotelBrandString, listOfRatePlanCodes, operaCompanyId),
                                                deviceLanguage, country, hotelDetailsInput.hotelCode(),
                                                channel, promoCodeToPass, skipPromotionsCheck).cache();
                            } else {
                                if (hotelDetailsInput.searchResultsInput() != null) {
                                    roomSearchList = getRoomSearchCriteriaFromHotelDetails(hotelDetailsInput);
                                    hotelBookingAvailabilityStateObservable = graphQLHDPUseCase
                                            .fetchPromotionsAndHotelAvailability(
                                                    new HotelAvailabilityRequestBody(
                                                            arrivalDate.toString(),
                                                            departureDate.toString(),
                                                            new HotelInfoDetails(hotelDetailsInput.hotelCode()),
                                                            roomSearchList, new BookingChannelDetails(
                                                            isInnBusinessUser ? Channel.BB.name() : Channel.PI.name(), SUB_CHANNEL,
                                                            deviceLanguage), hotelBrandString, listOfRatePlanCodes, operaCompanyId),
                                                    deviceLanguage, country, hotelDetailsInput.hotelCode(),
                                                    channel, promoCodeToPass, skipPromotionsCheck).cache();
                                } else {
                                    roomSearchList = getDefaultRoomSearchCriteriaFromHotelDetails();
                                    hotelBookingAvailabilityStateObservable = graphQLHDPUseCase
                                            .fetchPromotionsAndHotelAvailability(
                                                    new HotelAvailabilityRequestBody(
                                                            arrivalDate.toString(),
                                                            departureDate.toString(),
                                                            new HotelInfoDetails(hotelDetailsInput.hotelCode()), roomSearchList,
                                                            new BookingChannelDetails(
                                                                    isInnBusinessUser ? Channel.BB.name() : Channel.PI.name(),
                                                                    SUB_CHANNEL, deviceLanguage), hotelBrandString,
                                                            listOfRatePlanCodes, operaCompanyId),
                                                    deviceLanguage, country, hotelDetailsInput.hotelCode(),
                                                    channel, promoCodeToPass, skipPromotionsCheck).cache();
                                }
                                hotelDetailsInput = HotelDetailsInput.fromSearchPayload(getSearchPayload(arrival, departure,
                                        hotelInfo.getName(), currentRoomCriteria), hotelDetailsInput.hotelCode()).build();
                            }
                            inputObservable = Observable.just(hotelDetailsInput).cache();

                            // Update promoCode and promoKind from new availability state immediately
                            // This ensures they're in sync before rebindAllSubscriptions() uses them
                            networkDisposable.add(hotelBookingAvailabilityStateObservable
                                .take(1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(availabilityState -> {
                                    this.promoCode = availabilityState.getGetPromoCodeFromAvailability();
                                    this.promoKind = availabilityState.getGetPromoKindFromAvailability();

                                    ratesModelListObservableSetup();
                                    view.rebindAllSubscriptions();
                                }, error -> {
                                    crashlyticsLogger.logException(error, "Error updating promo state after date change");
                                    ratesModelListObservableSetup();
                                    view.rebindAllSubscriptions();
                                }));
                        }, throwable -> {
                            crashlyticsLogger.logException(throwable, "Error with Hotel Availability" + throwable.getMessage());
                            ratesModelListObservableSetup();
                            view.rebindAllSubscriptions();
                        }
                );
    }

    @VisibleForTesting
    Disposable bindRoomClassConfig() {
        RoomClassConfigRequestBody requestBody = new RoomClassConfigRequestBody(
                isInnBusinessUser ? Channel.BB.name() : Channel.PI.name(),
                hotelBrandString,
                country,
                deviceLocale.getLanguage()
        );

        return getRoomClassConfigUseCase.fetchRoomClassConfig(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(roomClassConfigs -> !roomClassConfigs.isEmpty())
                .subscribe(
                        roomClassConfigs -> sortedRoomClassConfigs = roomClassConfigs,
                        throwable -> crashlyticsLogger.log("Error loading RoomClassConfig: " + throwable)
                );
    }

    private List<RoomSearch> getRoomSearchCriteriaFromHotelDetails(HotelDetailsInput hotelDetailsInput) {
        List<Integer> adults = hotelDetailsInput.searchResultsInput().adults();
        List<Integer> children = hotelDetailsInput.searchResultsInput().children();
        List<Boolean> cots = hotelDetailsInput.searchResultsInput().cots();
        int roomSize = hotelDetailsInput.searchResultsInput().numRooms();
        List<String> roomTypes = hotelDetailsInput.searchResultsInput().roomTypeCodes();

        return HDPExtensionsKt.convertToRoomSearch(roomSize, adults, children, cots, roomTypes);
    }

    private List<RoomSearch> getDefaultRoomSearchCriteriaFromHotelDetails() {
        List<Integer> adults = new ArrayList<>(singletonList(1));
        List<Integer> children = new ArrayList<>(singletonList(0));
        List<Boolean> cots = new ArrayList<>(singletonList(false));
        int roomSize = 1;
        List<String> roomTypes = new ArrayList<>(singletonList(RoomType.DOUBLE.getCode()));

        return HDPExtensionsKt.convertToRoomSearch(roomSize, adults, children, cots, roomTypes);
    }

    private SearchPayload getSearchPayload(LocalDate arrival, LocalDate departure, String hotelName,
                                           List<RoomCriteria> roomsCriteria) {
        BookingPreferences bookingPreference = storage.getCustomerBookingPreferences();
        List<RoomCriteria> roomCriteriaPreference = new ArrayList<>();
        List<Integer> adults = new ArrayList<>();
        List<Integer> children = new ArrayList<>();
        List<Integer> infants = new ArrayList<>();
        List<Boolean> cots = new ArrayList<>();
        List<String> roomTypeCodes = new ArrayList<>();

        LocalDate arrivalDate = (arrival != null) ? arrival : hotelDetailsInput.searchResultsInput().arrivalDate();
        LocalDate departureDate = (departure != null) ? departure : hotelDetailsInput.searchResultsInput().departureDate();

        if (roomsCriteria != null) {
            roomCriteriaPreference.addAll(roomsCriteria);
        } else if (currentRoomCriteria != null) {
            roomCriteriaPreference.addAll(this.currentRoomCriteria);
        } else if (bookingPreference != null && bookingPreference.getRoomCriteriaPreference() != null) {
            roomCriteriaPreference.add(bookingPreference.getRoomCriteriaPreference());
        } else {
            roomCriteriaPreference.add(RoomCriteria.Companion.createWithDefaults());
        }

        for (RoomCriteria roomCriteria : roomCriteriaPreference) {
            adults.add(roomCriteria.getNumberOfAdults());
            children.add(roomCriteria.getNumberOfChildren());
            infants.add(roomCriteria.getNumberOfInfants());
            cots.add(roomCriteria.getIncludeCot());
            roomTypeCodes.add(OperaRoomTypesKt.toRoomStringGQL(roomCriteria.getRoomType()));
        }

        return new SearchPayload(arrivalDate, departureDate, hotelName, 0f, 0f, adults,
                children, infants, cots, roomTypeCodes, roomCriteriaPreference.size());
    }

    private Disposable bindTracking() {
        if (hotelDetailsInput.searchResultsInput() == null) {
            return null;
        }
        return Observable.zip(inputObservable,
                        hotelInformationDomainObservable.filter(allowIfHotelInfoStateIsSuccessful()),
                        hotelBookingAvailabilityStateObservable.filter(allowIfAvailabilityStateIsSuccessful()),
                        rateBoxUiModelObservable.distinct().map(orderedRatesList()),
                        (hotelDetailsInput, hotelInfoState, bookingAvailabilityState, rateBoxUiModel) -> new HDPMergedInfo(
                                hotelDetailsInput,
                                hotelInfoState,
                                Objects.requireNonNull(bookingAvailabilityState.getGetHotelAvailability()),
                                OperaCommonExtensionsKt.convertToRatePlansOpera(bookingAvailabilityState.getGetHotelAvailability()),
                                rateBoxUiModel,
                                storage.getFirebaseToken(),
                                bookingAvailabilityState.getGetPromoKindFromAvailability()))
                .map(new HDPMergedInfoToAnalyticsDataMapper(AnalyticsConstants.Type.LOOK_TO_BOOK, getStringResource, storage))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                            adobeAnalytics.track(ScreenState.HOTEL_DETAILS, data);
                            DynatraceHelper.INSTANCE.trackAction(1.0, AVAILABILITY_ACTION, AVAILABILITY_KEY);
                        },
                        error -> crashlyticsLogger.logException(error, "Error in merging hdp info to analytics"));
    }

    private Function<List<List<RatesDisplayInfo>>, List<RatesDisplayInfo>> orderedRatesList() {
        return lists -> {
            List<RatesDisplayInfo> combinedList = new ArrayList<>();
            for (List<RatesDisplayInfo> rateList : lists) {
                combinedList.addAll(rateList);
            }
            return combinedList;
        };
    }

    public Disposable bindRoomSubstitutionDisposable(@NonNull View view) {
        if (hotelDetailsInput.searchResultsInput() == null || hotelDetailsInput.searchResultsInput().roomTypeCodes() == null) {
            return null;
        }
        return null;
//            //TODO: Substitution is not clear as of now and hence adding this logic didnt make sense
//            return hotelBookingAvailabilityStateGQLObservable.filter(allowIfAvailabilityStateIsSuccessfulGQ())
//                    .filter(allowIfRatePlanAvailableGQ())
//                    .map(state -> HDPExtensionsKt.convertToRatePlansOpera(state.getGetHotelAvailability()))
//                    .filter(it -> !it.isEmpty())
//                    .map(rateplans -> HotelMappersKt.mapToRoomsOpera(rateplans))
//                    .zipWith(inputObservable.flatMapIterable(input -> input.searchResultsInput().roomTypeCodes()), Pair::new)
//                    .filter(allowIfFromAndToRoomTypeIsDifferentGQ())
//                    .map(pair -> RoomSubstitutionInfo.create(pair.getFirst().getNumber(),
//                            Room.typeLookUp(pair.getSecond()),
//                            RoomType.DOUBLE))
//                    .toList()
//                    .map(list -> RoomSubstitutionUiModel.builder().order(SECTION_ORDER_THREE).roomSubstitutionInfoList(list).build())
//                    .filter(model -> model.roomSubstitutionInfoList().size() > 0)
//                    .subscribe(view::addUiModelToRecyclerView);
    }

    private ObservableTransformer<BathroomSelectionInput, BathroomSelectionInput> allowIfBathroomSelectionRequired(Boolean allow) {

        return upstream -> upstream
                .flatMap(input -> inputObservable
                        .filter(it -> it.searchResultsInput() != null
                                && it.searchResultsInput().roomTypeCodes() != null)
                        .map(it -> it.searchResultsInput().roomTypeCodes())
                        .map(it -> new Pair<>(input, it)))
                .filter(it -> shouldShowBathroomSelectionScreen(it.getFirst().getParcelableRoom(), it.getSecond()) == allow)
                .map(Pair::getFirst);
    }

    private boolean shouldShowBathroomSelectionScreen(List<ParcelableRoomOpera> parcelableRooms,
                                                      List<String> searchedRoomTypeCodes) {
        return false;
    }

    @VisibleForTesting
    public void setupRateClickObservable(View view) {
        Observable<SelectedHotel> hotelInfoObservable = hotelInformationDomainObservable.
                filter(allowIfHotelInfoStateIsSuccessful())
                .map(hotelInfo -> SelectedHotel.builder()
                        .code(hotelDetailsInput.hotelCode())
                        .isHub(HotelMappersKt.mapToBrand(hotelInfo.getBrand()) == Hotel.Brand.HUB)
                        .name(hotelInfo.getName())
                        .address(GraphQLHotelInfoMappersKt.toCommaSeparatedAddress(hotelInfo.getAddress()))
                        .imageReference(hotelInfo.getGalleryImages().get(0).getImageSrc())
                        .accessibilityFacilities(new AccessibilityFacilities(false, false)).build());

        Observable<HotelBookingAvailabilityState> hotelBookingAvailabilityStateObservable = this.hotelBookingAvailabilityStateObservable
                .filter(allowIfAvailabilityStateIsSuccessful());

        Observable<List<AncillaryCloseOutItem>> ancillaryCloseOutObservable = hotelInformationDomainObservable
                .filter(allowIfHotelInfoStateIsSuccessful())
                .map(hotelInfoDomain -> {
                    List<AncillaryCloseOutItem> items = hotelInfoDomain.getAncillaryCloseOutItems();
                    return (items != null) ? items : emptyList();
                });

        rateClickObservable = view.onClickRateButton()
                .switchMap(event -> hotelBookingAvailabilityStateObservable.map(bookingAvailability ->
                        new Pair<>(event, bookingAvailability.getGetHotelAvailability())))
                .withLatestFrom(hotelInfoObservable, ancillaryCloseOutObservable,
                        (pair, selectedHotel, ancillaryCloseOutItems) ->
                                createBathroomSelectionInput(
                                        pair.getFirst().isAlternativeRoom(),
                                        lookUpClassification(pair.getFirst().getRateClassification(), pair.getSecond()),
                                        pair.getFirst().getPmsRoomType(),
                                        pair.getFirst().getRoomClass(),
                                        pair.getSecond(),
                                        selectedHotel,
                                        ancillaryCloseOutItems,
                                        pair.getFirst().getFormattedBaseRate(),
                                        pair.getFirst().getPromotionCode(),
                                        pair.getFirst().getPromotionTag()));

        storeAuthenticationRequiredAndHotelCountry();
        storeHotelPaymentProvider();
    }

    @Nullable
    public RoomRateDomain lookUpClassification(@NonNull String classification, HotelAvailabilityDomain hotelAvailabilityDomain) {
        if (hotelAvailabilityDomain != null) {
            for (RoomRateDomain ratePlan : hotelAvailabilityDomain.getRoomRateDomainList()) {
                if (classification.equals(EMPLOYEE_RATE_PLAN_CODE) && ratePlan.getCellCode().equals(EMPLOYEE_CODE)) {
                    return ratePlan;
                }
                if (ratePlan.getRatePlanCode().equalsIgnoreCase(classification)) {
                    return ratePlan;
                }
            }
        }

        return null;
    }

    private BathroomSelectionInput createBathroomSelectionInput(Boolean isAlternativeRoomSelected,
                                                                RoomRateDomain selectedRatePlan,
                                                                String pmsRoomTypeSelected,
                                                                String roomClass,
                                                                HotelAvailabilityDomain hotelAvailability,
                                                                SelectedHotel selectedHotel,
                                                                List<AncillaryCloseOutItem> ancillaryCloseOutItems,
                                                                @Nullable String formattedBaseRate,
                                                                @Nullable String promotionCode,
                                                                @Nullable String promotionTag) {

        isEmployeeRateSelected = selectedRatePlan.getCellCode().equals(EMPLOYEE_CODE);
        List<RatePlanOpera> ratePlans = OperaCommonExtensionsKt.convertToRatePlansOpera(hotelAvailability);
        List<RatePlanOpera> ratePlanOperasUpd = HDPExtensionsKt.filterRateForSelectedPmsRoomTypeForAlternateRooms(
                ratePlans, roomClass, isAlternativeRoomSelected);
        List<ParcelableRatePlanOpera> parcelableRatePlans = HDPExtensionsKt.toParcelableRatePlanOpera(ratePlanOperasUpd);
        List<ParcelableRoomOpera> parcelableRooms;

        parcelableRooms = HDPExtensionsKt.toStdParcelableRoom(
                parcelableRatePlans, selectedRatePlan.getRatePlanCode());

        List<ParcelableRoomOpera> parcelableRoomOperas = HDPExtensionsKt
                .addAccRoomFromStdIfAlternateRoomFlow(parcelableRatePlans,
                        selectedRatePlan.getRatePlanCode(), isAlternativeRoomSelected);

        List<ParcelableRoomOpera> parcelableTwinRoom = HDPExtensionsKt.toTwinParcelableRoom(
                parcelableRatePlans, selectedRatePlan.getRatePlanCode());

        List<ParcelableRoomType> parcelableRoomTypes = HDPExtensionsKt.toParcelableRoomType(selectedRatePlan.getRoomTypesDomainList());

        List<Integer> infantCountList = hotelDetailsInput.searchResultsInput().infants();
        boolean isEmployeeCellCodePresent = OperaCommonExtensionsKt.isEmployeeCellCode(selectedRatePlan.getCellCode());
        String rateName = findRateNameFromRatesInformation(selectedRatePlan.getRatePlanCode(),
                isEmployeeCellCodePresent, listOfRateClassification);
        String rateDescription = findRateDescriptionFromRatesInformation(selectedRatePlan.getRatePlanCode(), isEmployeeCellCodePresent);
        SelectedRate selectedRate = SelectedRate.createFrom(selectedRatePlan, rateName, rateDescription,
                selectedRatePlan.getRatePlanCode());
        LocalDate arrivalDate = hotelDetailsInput.searchResultsInput().arrivalDate();
        LocalDate departureDate = hotelDetailsInput.searchResultsInput().departureDate();
        int nights = hotelDetailsInput.searchResultsInput().nights();

        try {
            SummaryInput provisionalSummaryInput = SummaryInput.create(hotelAvailability, infantCountList,
                    selectedHotel, selectedRate, pmsRoomTypeSelected, isAlternativeRoomSelected,
                    HotelMappersKt.mapHotelBrandToBrandString(hotelBrand),
                    arrivalDate, departureDate, nights,
                    hotelDetailsInput.searchResultsInput().hasAccessibleRoom(),
                    hotelDetailsInput.searchResultsInput().hasTwinRoom(), ancillaryCloseOutItems,
                    hasCustomerLoggedIn, isAlternativeRoomSelected, isInnBusinessUser, isEmployeeRateSelected,
                    AppExtensions.getUpsellItemsAllowed(businessStorage),
                    dinnerAllowance, formattedBaseRate, promotionCode, promoKind, promotionTag, roomClass);
            return new BathroomSelectionInput(provisionalSummaryInput, parcelableRooms, parcelableRoomOperas,
                    parcelableTwinRoom, parcelableRoomTypes);
        } catch (Exception e) {
            crashlyticsLogger.logException(e, "Exception in createBathroomSelectionInput");
            return null;
        }
    }

    private String findRateNameFromRatesInformation(String rateType,
                                                    boolean isEmployeeCellCodePresent,
                                                    List<RateClassificationsDomain> rateClassificationList) {
        if (rateClassificationList != null) {
            this.listOfRateClassification = rateClassificationList;
        }
        if (isEmployeeCellCodePresent) {
            return HDPExtensionsKt.getRateNameAndRateDescPairFromRateClassification(
                    this.listOfRateClassification, EMPLOYEE_RATE_PLAN_CODE).getFirst();
        } else {
            return HDPExtensionsKt.getRateNameAndRateDescPairFromRateClassification(
                    this.listOfRateClassification, rateType).getFirst();
        }
    }

    private String findRateDescriptionFromRatesInformation(String rateType,
                                                           boolean isEmployeeCellCodePresent) {
        if (isEmployeeCellCodePresent) {
            return HDPExtensionsKt
                    .getRateNameAndRateDescPairFromRateClassification(listOfRateClassification, EMPLOYEE_RATE_PLAN_CODE).getSecond();
        } else {
            return HDPExtensionsKt
                    .getRateNameAndRateDescPairFromRateClassification(listOfRateClassification, rateType).getSecond();

        }
    }

    public Disposable bindSummaryOrRoomSelectionDisposable(@NonNull View view) {
        return rateClickObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(it -> {
                            if (isInnBusinessUser && innBusinessUserAccessLevel == AccessLevel.STAYER) {
                                view.showInnBusinessGuestRestrictionAlert();
                            } else {
                                if (it.getProvisionalSummaryInput().rate().rateType().equals(EMPLOYEE_RATE_PLAN_CODE)) {
                                    view.showDoNotForgetPrivilegeCardAlert(it, showSelectLabel, isAccessibleFlow);
                                } else {
                                    if (isAccessibleFlow) {
                                        // no create reservation and
                                        // call bathroom selection
                                        view.startBathroomSelectionActivity(it);
                                    } else if (showSelectLabel) {
                                        view.startAlternativeRoomSelectionActivity(it);
                                    } else {
                                        makeHoldBookingCall(view, it);
                                    }
                                }
                            }
                        },
                        throwable -> crashlyticsLogger.log(throwable.getMessage()));

    }

    @VisibleForTesting
    protected void  makeHoldBookingCall(@NonNull View view, BathroomSelectionInput bathroomSelectionInput) {
        view.shouldShowLoadingSpinner(true);

        CreateReservationRequestBody createReservationRequestBody =
                HDPExtensionsKt.constructCreateReservation(bathroomSelectionInput.getProvisionalSummaryInput(), deviceLanguage);

        // This code will change once we integrate booking info call
        HotelPackagesRequestBody packagesRequestBody =
                HDPExtensionsKt.createPackagesRequestBody(bathroomSelectionInput.getProvisionalSummaryInput(),
                        deviceLanguage, country);
        HoldBookingRequestBody holdBookingRequestBody = new HoldBookingRequestBody(
                createReservationRequestBody,
                packagesRequestBody,
                country,
                deviceLanguage,
                new BookingChannelDetails(channel, SUB_CHANNEL, deviceLanguage)
        );

        networkDisposable.add(holdBookingUseCase.holdBooking(holdBookingRequestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        pairOfPackagesAndBasketRef -> {
                            SummaryInput updatedSummaryInput = AppExtensions.updateSummaryInput(
                                    bathroomSelectionInput.getProvisionalSummaryInput(),
                                    pairOfPackagesAndBasketRef.getFirst(),
                                    CommonMappersKt.toAncillariesCloseout(
                                            bathroomSelectionInput.getProvisionalSummaryInput().ancillaryCloseOutItems()),
                                    AppExtensions.getUpsellItemsAllowed(businessStorage),
                                    pairOfPackagesAndBasketRef.getSecond());

                            view.shouldShowLoadingSpinner(false);
                            view.startSummaryActivity(updatedSummaryInput);

                        },
                        throwable -> {
                            view.shouldShowLoadingSpinner(false);
                            if (throwable instanceof CreateReservationException) {
                                view.showCreateReservationErrorDialog();
                            } else if (throwable instanceof BookingInformationException) {
                                BookingFlowInput bookingFlowInput = AppExtensions
                                        .constructBookingFlowInput(bathroomSelectionInput.getProvisionalSummaryInput(),
                                                ((BookingInformationException) throwable).getBasketReference());
                                if (isInnBusinessUser) {
                                    ReviewBookingInput reviewBookingInput = AppExtensions
                                            .constructReviewBookingInputWhenSkippingUpsellForBBUser(
                                                    bathroomSelectionInput.getProvisionalSummaryInput(), storage,
                                                    bookingFlowInput.basketReference(), deviceLanguage);
                                    if (showAdditionalInfoScreen()) {
                                        ReviewBookingInput updatedReviewBookInput = reviewBookingInput
                                                .toBuilder()
                                                .listOfEmployeeQuestionsModel(getListOfEmployeeQuestionsModel())
                                                .build();
                                        view.startAdditionalInformationActivity(updatedReviewBookInput);
                                    } else {
                                        view.startReviewAndBookActivity(reviewBookingInput);
                                    }
                                } else {
                                    if (hasCustomerLoggedIn) {
                                        view.startGuestDetailsActivity(bookingFlowInput);
                                    } else {
                                        this.bookingFlowInput = bookingFlowInput;
                                        view.startLoginActivity();
                                    }
                                }
                            }
                            crashlyticsLogger.log("Error in hold booking -  create reservation: " + throwable);
                        }
                ));
    }

    public Disposable bindBathroomSelectionDisposable(@NonNull View view) {
        return rateClickObservable
                .compose(allowIfBathroomSelectionRequired(true))
                .subscribe(view::startBathroomSelectionActivity);
    }

    private Disposable clickImportantInfoDisposable(@NonNull View view) {
        return view.onClickImportantHotelInfo()
                .withLatestFrom(hotelInformationDomainObservable
                        .filter(allowIfHotelInfoStateIsSuccessful()), Pair::new)
                .subscribe(pair -> {
                            view.showImportantInfoBottomSheetFragment(
                                    Objects.requireNonNull(HDPExtensionsKt.filterImportantInfoByDate(pair.getSecond().getImportantInfo(),
                                            Objects.requireNonNull(hotelDetailsInput.searchResultsInput())
                                    )).getInfoItems()
                            );
                        },
                        error -> crashlyticsLogger.logException(error, "Error with Important Info" + error.getMessage()));
    }

    private Disposable bindMapViewClicks(@NonNull View view) {
        return view.onClickMapView()
                .flatMap(__ -> hotelInformationDomainObservable.filter(allowIfHotelInfoStateIsSuccessful()))
                .subscribe(state -> {
                            processMapStartInfo(view, state);
                        },
                        error -> crashlyticsLogger.logException(error, "Error with MapView Clicks" + error.getMessage()));
    }

    @VisibleForTesting
    protected Disposable bindReadMoreClicks(@NonNull View view) {
        return view.onClickReadMore()
                .flatMap(__ -> hotelInformationDomainObservable.filter(allowIfHotelInfoStateIsSuccessful()))
                .subscribe(state -> view.showAboutThisHotelBottomSheetFragment(HotelFullDescription.create(state.getName(),
                                messageProvider.hotelDescription(state.getHotelDescription()),
                                messageProvider.hotelDirections(state.getDirections()),
                                messageProvider.parkingDescriptionFromHtml(state.getParkingDescription()))),
                        error -> crashlyticsLogger.logException(error, "Error with Read More" + error.getMessage()));
    }

    private Disposable bindMainSelectRateClicks(@NonNull View view) {
        return view.onClickMainSelectRateButton().subscribe(__ -> view.scrollToRates());
    }

    private Disposable bindRoomFindOutMoreClicks(@NonNull View view) {
        return view.onClickRoomFindOutMore()
                .subscribe(event -> view.startRoomVariantDetailsBottomSheetFragment(hotelDetailsInput.hotelCode()));
    }

    private Disposable bindEditDatesClicks(@NonNull View view) {
        return view.onClickEditDates()
                .flatMap(__ -> hotelInformationDomainObservable.filter(allowIfHotelInfoStateIsSuccessful()))
                .flatMap(__ -> inputObservable.map(HotelDetailsInput::searchResultsInput))
                .subscribe(searchResultsInput -> view.startCalendarActivity(
                                searchResultsInput.arrivalDate(), searchResultsInput.departureDate(), maxNights, maxArrivalDate),
                        error -> crashlyticsLogger.logException(error, "Error with Edit Dates" + error.getMessage()));
    }

    private Disposable bindCheckAvailabilityClicks(@NonNull View view) {
        return view.onClickCheckAvailability()
                .flatMap(__ -> hotelInformationDomainObservable.filter(allowIfHotelInfoStateIsSuccessful()))
                .subscribe(hotelInfoState -> {
                            view.startCalendarActivity(maxNights, maxArrivalDate);
                        },
                        error -> crashlyticsLogger.logException(error, "Error with Check Availability Clicks" + error.getMessage()));
    }

    private Disposable bindHotelsNearByClicks(@NonNull View view) {
        return view.onClickHotelNearby()
                .subscribe(pair -> view.goBack());
    }

    private Disposable bindCoronavirusDismiss(@NonNull View view) {
        return view.onClickCoronavirusDismiss()
                .subscribe((clickEvent -> view.removeUiModelFromRecyclerView(clickEvent.getPosition())));
    }

    private Disposable bindEditGuestClick(@NonNull View view) {
        return view.onEditGuestClicked()
                .subscribe(rooms -> view.openGuestAndRooms(currentRoomCriteria));
    }

    protected Disposable bindHotelFacilitiesUiModel(@NonNull View view) {
        return hotelInformationDomainObservable
                .filter(allowIfHotelInfoStateIsSuccessful())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                            if (success.getHotelFacilities() != null && !success.getHotelFacilities().isEmpty()) {
                                view.addUiModelToRecyclerView(new HotelFacilitiesUiModel(SECTION_ORDER_ELEVEN));
                            }
                        },
                        error -> crashlyticsLogger.logException(error, "Hotel Facilities model error" + error.getMessage()));
    }

    @VisibleForTesting
    protected Disposable bindHotelFacilitiesClick(@NonNull View view) {
        return view.onClickFacilities()
                .flatMap(__ -> hotelInformationDomainObservable.filter(allowIfHotelInfoStateIsSuccessful()))
                .subscribe(state -> {
                    if (state.getHotelFacilities() != null && !state.getHotelFacilities().isEmpty()) {
                        view.showHotelFacilitiesBottomSheet(new HotelFacilitiesModel(
                                displayableFacilities(state.getHotelFacilities()), getRoomFeatures(state.getBrand())
                        ));
                    }
                });
    }

    private Disposable bindRoomTypesUiModel(@NonNull View view) {
        return hotelInformationDomainObservable
                .filter(allowIfHotelInfoStateIsSuccessful())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hotelInformationDomain -> view.addUiModelToRecyclerView(new RoomTypesSectionUiModel(SECTION_ORDER_TWELVE)),
                        error -> crashlyticsLogger.logException(error, "Room Types model error" + error.getMessage()));
    }

    private Disposable bindRoomTypesClick(@NonNull View view) {
        return view.onClickRoomTypes()
                .subscribe(event -> view.startRoomVariantDetailsBottomSheetFragment(hotelDetailsInput.hotelCode()));
    }

    private List<RoomFeaturesModel> getRoomFeatures(String brand) {
        switch (brand) {
            case BRAND_HUB:
                return getHubFacilities(stringResourceProvider);
            case BRAND_ZIP:
                return getZipFacilities(stringResourceProvider);
            case BRAND_PI:
            case BRAND_PI_GERMANY:
            default:
                return getPremierInnRoomFeatures(stringResourceProvider);
        }
    }

    public BookingFlowInput getBookingFlowInput() {
        return bookingFlowInput;
    }

    @Override
    public void onDetachView() {
        viewDisposable.clear();
        networkDisposable.clear();
    }

    @Override
    public void onDestroy() {
        networkDisposable.clear();
    }

////////////////////////////////////////////////////////////////////////////////////////////////
// Helper Methods
////////////////////////////////////////////////////////////////////////////////////////////////

    private String getToolbarTitle(@NonNull HotelDetailsInput input) {
        return input.hotelName();
    }

    private Predicate<? super HotelInformationDomain> allowIfHotelInfoStateIsSuccessful() {
        return hotelInfo -> !hotelInfo.getBrand().equals(EMPTY_STRING);
    }

    private Predicate<? super HotelBookingAvailabilityState> allowIfAvailabilityStateIsSuccessful() {
        return HotelBookingAvailabilityState::isHotelAvailabilitySuccessfulHDP;
    }

    private Predicate<? super HotelBookingAvailabilityState> allowIfAvailabilityStateIsSuccessfulOrFullyBooked() {
        return HotelBookingAvailabilityState::isHotelSuccessfulOrFullyBooked;
    }


    private void processMapStartInfo(@NonNull View view, HotelInformationDomain state) {
        MapStartInfo mapStartInfo = hotelDetailsInput.hotelImageUrl() == null
                ? MapStartInfo.builder()
                .hotelCoordinate(Coordinates.create(state.getCoordinates().getLatitude(),
                        state.getCoordinates().getLongitude()))
                .hotelName(state.getName())
                .hotelBrand(hotelBrand)
                .distance(0f)
                .build()
                : MapStartInfo.builder()
                .hotelCoordinate(Coordinates.create(state.getCoordinates().getLatitude(),
                        state.getCoordinates().getLongitude()))
                .hotelName(state.getName())
                .hotelBrand(hotelBrand)
                .searchCoordinate(Coordinates.create(hotelDetailsInput.searchResultsInput().latitude(),
                        hotelDetailsInput.searchResultsInput().longitude()))
                .searchTerm(hotelDetailsInput.searchResultsInput().placeName())
                .distance(getDistanceFromSearchLocation()).build();

        view.startFullScreenMapActivity(mapStartInfo);
    }

    private List<Content> getRestaurantContent(String description) {
        if (description != null) {
            return Collections.singletonList(Content.builder()
                    .contentType(Content.ContentType.BODY_WITH_HTML)
                    .text(description).build());
        } else {
            return Collections.emptyList();
        }
    }

    private List<Content> getFoodOptionsContent(@NonNull List<RestaurantMenuDomain> items) {
        List<Content> contentList = new ArrayList<>();
        for (RestaurantMenuDomain item : items) {
            if (item != null) {
                contentList.add(Content.builder().contentType(Content.ContentType.HEADER).text(item.getName()).build());
                contentList.add(Content.builder().contentType(Content.ContentType.BODY_WITH_HTML).text(item.getDescription()).build());
            }
        }
        return contentList;
    }

    private void logViewingHotelDetails() {
        FirebaseParams firebaseParams = new FirebaseParams();
        firebaseParams.putString(FirebaseParams.ParamName.HOTEL_CODE, hotelDetailsInput.hotelCode());

        if (hotelDetailsInput.searchResultsInput() != null) {
            firebaseParams.putString(FirebaseParams.ParamName.SEARCH_TERM, hotelDetailsInput.searchResultsInput().placeName());
            firebaseParams.putFormattedDate(FirebaseParams.ParamName.ARRIVAL_DATE, hotelDetailsInput.searchResultsInput()
                    .arrivalDate());
            firebaseParams.putFormattedDate(FirebaseParams.ParamName.DEPARTURE_DATE, hotelDetailsInput.searchResultsInput()
                    .departureDate());
            firebaseParams.putInteger(FirebaseParams.ParamName.NUMBER_OF_NIGHTS, hotelDetailsInput.searchResultsInput().nights());
            firebaseParams.putInteger(FirebaseParams.ParamName.NUMBER_OF_ROOMS, hotelDetailsInput.searchResultsInput().numRooms());
            firebaseParams.putInteger(FirebaseParams.ParamName.NUMBER_OF_GUESTS, hotelDetailsInput.searchResultsInput().totalNumOfGuests());
        }

        firebaseLogger.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, firebaseParams);
    }

    private Function<BookingAvailabilityState, Iterable<? extends BookingRoom>> mapToRatePlanRooms() {
        return state -> state.bookingAvailability().ratePlans().get(0).getRooms();
    }

    private Boolean isNonSilentSubstitution(BookingRoom assignedRoom,
                                            String searchedRoomTypeCode) {
        return !BookingRoom.Status.ROOM_TYPE_GUARANTEED.equals(assignedRoom.getStatus())
                && !Room.typeLookUp(searchedRoomTypeCode).equals(assignedRoom.getType());
    }


    private Boolean isFromAndToRoomTypeDifferent(BookingRoom assignedRoom,
                                                 String searchedRoomTypeCode) {
        return !Room.typeLookUp(searchedRoomTypeCode).equals(Room.typeLookUp(assignedRoom.getType()));
    }

    private Predicate<Pair<BookingRoom, String>> allowIfNonSilentSubstitution() {
        return pair -> isNonSilentSubstitution(pair.getFirst(), pair.getSecond());
    }

    private Predicate<? super Pair<RoomOpera, String>> allowIfFromAndToRoomTypeIsDifferentGQ() {
        //TODO: This is just for now so that room substitution doesn't happen
        return pair -> pair.getFirst().getCost() == PriceDomain.Companion.createDefault();
    }

    public static ObservableTransformer<HotelInformationDomain, List<String>>
    transformToImageUrlsList(Predicate<Pair<TopSectionImageDomain,
            String>> tagFilterType) {
        return hotelSingle -> hotelSingle
                .filter(allowIfGetTopSectionImagesAndTagsExist())
                .map(HotelInformationDomain::getTopSectionImages)
                .flatMapIterable(images -> images)
                .flatMapIterable(TopSectionImageDomain::getTags, Pair::new)
                .filter(tagFilterType)
                .map(pair -> {
                    assert pair.getFirst() != null;
                    return Urls.CONTENT_BASE_URL + pair.getFirst().getImageSrc();
                })
                .distinct()
                .toList().toObservable();
    }

    public static ObservableTransformer<HotelInformationDomain, List<String>> transformToHotelImageList() {
        return hotelSingle -> hotelSingle
                .filter(allowIfGetTopSectionImagesAndTagsExist())
                .map(HotelInformationDomain::getTopSectionImages)
                .map(OperaCommonExtensionsKt::returnRestaurantFilteredTopSectionImages)
                .flatMapIterable(filteredTopSectionImages -> filteredTopSectionImages)
                .map(topSectionImageDomain -> Urls.CONTENT_BASE_URL + topSectionImageDomain.getImageSrc())
                .toList()
                .toObservable();
    }

    @NonNull
    private static Predicate<HotelInformationDomain> allowIfGetTopSectionImagesAndTagsExist() {
        return hotelInformationDomain -> hotelInformationDomain.getTopSectionImages() != null
                && !hotelInformationDomain.getTopSectionImages().isEmpty()
                && !hotelInformationDomain.getTopSectionImages().get(0).getTags().isEmpty();
    }

    private List<Facility> displayableFacilities(List<HotelFacilityDomain> listOfHotelFacilities) {
        List<Facility> displayableFacilities = new ArrayList<>();
        for (HotelFacilityDomain facility : listOfHotelFacilities) {
            if (facility != null && facility.isVisible()) {
                displayableFacilities.add(MappersKt.toFacility(facility));
            }
        }
        return displayableFacilities;
    }

    private boolean showAdditionalInfoScreen() {
        return getListOfEmployeeQuestionsModel() != null && !getListOfEmployeeQuestionsModel().isEmpty();
    }

    private List<EmployeeQuestionsModel> getListOfEmployeeQuestionsModel() {
        List<ManagementInformationQuestion> listOfManagementQuestion = AppExtensions.getListOfManageQuestions(businessStorage);

        return AppExtensions.toListOfEmployeeQuestionsModel(listOfManagementQuestion);
    }

    private boolean hasWetRooms(HotelInfo hotelInfo) {
        for (Facility facility : hotelInfo.facilities()) {
            if (Facility.Codes.WET_ROOMS_AVAILABLE.equals(facility.code())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasLoweredBaths(HotelInfo hotelInfo) {
        for (Facility facility : hotelInfo.facilities()) {
            if (Facility.Codes.LOWERED_BATHS_AVAILABLE.equals(facility.code())) {
                return true;
            }
        }
        return false;
    }

////////////////////////////////////////////////////////////////////////////////////////////////
// View Interface
////////////////////////////////////////////////////////////////////////////////////////////////
    public interface View extends PresenterView {

        void showToolBarTitle(@NonNull String title);

        void addUiModelToRecyclerView(@NonNull UiModelListItem item);

        void removeUiModelFromRecyclerView(@NonNull int position);

        void startLoginActivity();

        void startBathroomSelectionActivity(@NonNull BathroomSelectionInput bathroomSelectionInput);

        void startAlternativeRoomSelectionActivity(@NonNull BathroomSelectionInput bathroomSelectionInput);

        void startSummaryActivity(SummaryInput summaryInput);

        void openGuestAndRooms(@NonNull List<RoomCriteria> roomCriteria);

        Observable<MainGalleryItemClickEvent> onClickMainPhotoGallery();

        Observable<RateClickEvent> onClickRateButton();

        Observable<ImportantInfoClickEvent> onClickImportantHotelInfo();

        Observable<DiscountCodeClickEvent> onClickDiscountCode();

        Observable<MapViewClickEvent> onClickMapView();

        Observable<ReadMoreClickEvent> onClickReadMore();

        Observable<EditDatesClickEvent> onClickEditDates();

        Observable<CheckAvailabilityClickEvent> onClickCheckAvailability();

        Observable<HotelsNearbyClickEvent> onClickHotelNearby();

        Observable<SelectRateClickEvent> onClickMainSelectRateButton();

        Observable<FindOutMoreClickEvent> onClickRoomFindOutMore();

        Observable<CoronavirusDismissClickEvent> onClickCoronavirusDismiss();

        @NonNull
        Observable<EditGuestClickEvent> onEditGuestClicked();

        void showImportantInfoBottomSheetFragment(List<InfoItem> infoItems);

        void showAboutThisHotelBottomSheetFragment(@NonNull HotelFullDescription hotelFullDescription);

        void startFullScreenMapActivity(@NonNull MapStartInfo mapStartInfo);

        void reBindBookingAvailabilitySubscriptions();

        void rebindAllSubscriptions();

        void scrollToRates();

        void goBack();

        Observable<MakePhoneCallEvent> onMakePhoneCallClicked();

        void makePhoneCall(String number);

        Observable<SendEmailEvent> onSendEmailClicked();

        void sendEmail(String emailAddress);

        Observable<OpenWebLinkEvent> onOpenWebLinkClicked();

        void openWebLink(String url);

        Observable<AccessibilityRoomTypeInfoClickEvent> onAccessibilityRoomTypeInfoClicked();

        void startRoomVariantDetailsBottomSheetFragment(@NonNull String hotelCode);

        void startCalendarActivity(@NonNull LocalDate arrival, @NonNull LocalDate departure, Integer maxNights, Integer maxArrivalDate);

        void startGuestDetailsActivity(@NonNull BookingFlowInput bookingFlowInput);

        void startAdditionalInformationActivity(@NonNull ReviewBookingInput reviewBookingInput);

        void startReviewAndBookActivity(@NonNull ReviewBookingInput reviewBookingInput);

        void showHotelFacilitiesBottomSheet(HotelFacilitiesModel hotelFacilitiesModel);

        void showParkingBottomSheet(HotelParkingModel hotelParkingModel);

        void showAccessibilityBottomSheet();

        Observable<FacilitiesClickEvent> onClickFacilities();

        Observable<RoomTypesClickEvent> onClickRoomTypes();

        Observable<ParkingClickEvent> onClickParking();

        Observable<AccesibilityClickEvent> onClickAccessibility();

        Observable<StartSummaryOrRoomSelectionEvent> onClickEmployeeContinue();

        void showDoNotForgetPrivilegeCardAlert(@NonNull BathroomSelectionInput input,
                                               Boolean selectOrBook, Boolean isAccessibleFlow);

        void showInnBusinessGuestRestrictionAlert();

        void shouldShowLoadingSpinner(Boolean shouldShowLoadingSpinner);

        void showDiscountCodeBottomSheet(@NonNull DiscountCodeInput input);

        void startCalendarActivity(Integer maxNights, Integer maxArrivalDate);

        void showCreateReservationErrorDialog();

    }
}

