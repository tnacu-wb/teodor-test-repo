package com.whitbread.premierinn.reviewbooking;

import static com.whitbread.premierinn.api.response.customer.PaymentCard.BUSINESS_CARD;
import static com.whitbread.premierinn.common.utils.StringUtils.EMPTY_STRING;
import static com.whitbread.premierinn.data.common.Constants.BRAND_PI;
import static com.whitbread.premierinn.data.common.Constants.LANGUAGE_ENGLISH;
import static com.whitbread.premierinn.domain.common.Constants.COUNTRY_CODE_UK;
import static com.whitbread.premierinn.domain.common.Constants.GBP;
import static com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.BOOKING_PRIVACY_FOOTER;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import com.braintreepayments.api.PayPalAccountNonce;
import com.braintreepayments.api.PayPalClient;
import com.braintreepayments.api.UserCanceledException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.ReplayRelay;
import com.whitbread.premierinn.api.request.booking.BookingAddress;
import com.whitbread.premierinn.api.request.booking.Breakfast;
import com.whitbread.premierinn.api.response.AcceptedCreditCard;
import com.whitbread.premierinn.api.response.CardInfo;
import com.whitbread.premierinn.api.response.InstanceFactory;
import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.api.response.booking.DailyRate;
import com.whitbread.premierinn.api.response.customer.ParcelableCustomer;
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager;
import com.whitbread.premierinn.businessbooker.domain.customer.usecase.IsBusinessCustomerLoggedIn;
import com.whitbread.premierinn.common.AppConfiguration;
import com.whitbread.premierinn.common.BookingFlowInput;
import com.whitbread.premierinn.common.PaymentProvider;
import com.whitbread.premierinn.common.PaymentRatePlan;
import com.whitbread.premierinn.common.PaymentTimingChoice;
import com.whitbread.premierinn.common.RoomBooking;
import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.FirebaseLogger;
import com.whitbread.premierinn.common.analytics.FirebaseParams;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.mapper.CommonMappersKt;
import com.whitbread.premierinn.common.retrofitConfiguration.GsonAdapterFactory;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.common.utils.StringUtils;
import com.whitbread.premierinn.data.GsonFactory;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl;
import com.whitbread.premierinn.data.graphql.mapper.GraphQLBasketStatusRevisedPaymentsMapperKt;
import com.whitbread.premierinn.data.graphql.mapper.GraphQLPaymentMethodsMapperKt;
import com.whitbread.premierinn.data.remote.graphql.contracts.BasketStatusRevisedPaymentsGraphQLContract;
import com.whitbread.premierinn.data.remote.graphql.contracts.PaymentMethodsGraphQLContract;
import com.whitbread.premierinn.domain.authentication.usecase.AuthenticateCustomer;
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn;
import com.whitbread.premierinn.domain.booking.entity.Booking;
import com.whitbread.premierinn.domain.booking.usecase.StoreBookingMadeOnApp;
import com.whitbread.premierinn.domain.common.Address;
import com.whitbread.premierinn.domain.common.AmendRestrictions;
import com.whitbread.premierinn.domain.common.Guest;
import com.whitbread.premierinn.domain.common.LeadGuest;
import com.whitbread.premierinn.domain.common.LettingType;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.domain.common.RoomType;
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn;
import com.whitbread.premierinn.domain.countries.GetCountries;
import com.whitbread.premierinn.domain.countries.entity.CountryDomain;
import com.whitbread.premierinn.domain.customer.entity.Customer;
import com.whitbread.premierinn.domain.customer.usecase.CreateCustomer;
import com.whitbread.premierinn.domain.graphql.bookingDetails.usecase.GraphQLBookingDetailsUseCase;
import com.whitbread.premierinn.domain.graphql.findBooking.entity.FindBookingDomain;
import com.whitbread.premierinn.domain.graphql.findBooking.usecase.GraphQLFindBookingUseCase;
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.CreateReservationGuestDomain;
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PaymentMethodsWithDonationAndBookingConfirmationGQLDomain;
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.BasketStatusRevisedPaymentsDomain;
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.InitiatePaymentDomain;
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.PaymentRequiredDetailsDomain;
import com.whitbread.premierinn.domain.graphql.reviewBooking.usecase.GraphQLReviewBookingUseCase;
import com.whitbread.premierinn.domain.recentsearch.usecase.ClearBookedRecentSearch;
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository;
import com.whitbread.premierinn.domain.resource.usecase.GetLongResource;
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource;
import com.whitbread.premierinn.editguest.EditGuestInput;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataInput;
import com.whitbread.premierinn.hoteldetails.DailyRateInput;
import com.whitbread.premierinn.hoteldetails.MappersKt;
import com.whitbread.premierinn.hoteldetails.SelectedRate;
import com.whitbread.premierinn.paymentbreakdown.PaymentBreakdownInput;
import com.whitbread.premierinn.paymentdetails.PaymentDetailsInput;
import com.whitbread.premierinn.postcodefinder.ParcelableAddress;
import com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData;
import com.whitbread.premierinn.reviewbooking.view.DonationView;
import com.whitbread.premierinn.utils.RxJavaTestRule;
import com.whitbread.premierinn.utils.model.CustomerFixture;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import kotlin.Unit;

@RunWith(MockitoJUnitRunner.class)
public class ReviewBookingPresenterTest {

    private static final String HOTEL_IMAGE_REFERENCE = "ref/of/hotel/image";
    private static final String HOTEL_NAME = "Premier Inn Chadwell";
    private static final String HOTEL_CODE = "ABCDE";
    private static final String HOTEL_CODE_OPERA = "LONHOL";
    private static final String RATE_NAME = "Flex";
    private static final String RATE_DESCRIPTION = "DESCRIPTION";
    private static final String RATE_CLASSIFICATION = "A";
    private static final String BUSINESS_TRIP_TYPE = "Business";
    private static final String VISA_CARD_TYPE = "VT";
    private static final String ACCEPTED_CC_WITH_FEE = VISA_CARD_TYPE;
    private static final String RATE_CODE = "RT123";
    private static final String FORMATTED_RATE_CODE = "RT";
    private static final String BOOKING_CONFIRMATION_NUMBER = "123456";
    private static final float TOTAL_UPSELL_COST = 123f;
    private static final String PI_BREAKFAST_CODE = "11";
    private static final String FREE_BREAKFAST_CODE = "15";
    private static final String CNP_MEMORABLE_WORD = "My memorable word";
    private static final String CNP_INCORRECT_PWD_ERROR_BODY = "{\"code\":100,"
            + "\"details\":[\"CNP_VALIDATION_FAILED_INCORRECT_PASSWORD:"
            + " Incorrect password/memorable word|ATOSPWD\"]}";
    private final LocalDate arrivalDate = LocalDate.of(2017, 4, 5);
    private final LocalDate endDate = LocalDate.of(2017, 4, 8);
    private final int numAdults = 3;
    private final int numChildren = 2;
    private final String currency = "GBP";
    private final float amount = 22.50f;
    private final String cardLegend = "Mastercard Credit";
    private final float cardFeeAmount = 1.00f;
    private final String cardUrl = "content.com/MC.jpeg";
    private final String cardNumber = "469573053048";
    private final String nameOnCard = "John Smith";
    private final PriceDomain upsellPrice = PriceDomain.Companion.createWithGBPCurrency(TOTAL_UPSELL_COST);
    private final String rateType = "Flex";
    private final int numNights = 3;
    private final PriceDomain totalRatePrice = new PriceDomain(amount, currency);
    private List<RoomBooking> roomsBooked;
    private ReviewBookingInput reviewBookingInput;
    private PaymentDetailsInput paymentDetailsInput;
    private final Boolean marketingOptIn = false;
    private final PaymentProvider paymentProvider = PaymentProvider.THREE_C_P;
    private Customer customerEntity = CustomerFixture.INSTANCE.aCustomer();
    private List<CountryDomain> countryMock = new ArrayList<>();

    private String operaCharityPackageCode = "ZCHRY5";
    private Booking booking = new Booking("confirmationNumber", "surname", arrivalDate,
                                                 arrivalDate.plusDays(numNights), HOTEL_CODE, HOTEL_NAME, 1, 5, "Mr First surname",
            "Flex", new PriceDomain(22.5f, GBP), new PriceDomain(22.5f, GBP),
                new PriceDomain(22.5f, GBP), false, false,
            false, false, false, null, Collections.emptyList(),
            AmendRestrictions.Companion.createWithDefaults(false, false, false, false, false));

    @Mock ReviewBookingPresenter.View view;
    @Mock StoreBookingMadeOnApp storeBooking;
    @Mock PaymentRatePlan paymentRatePlan;
    @Mock PaymentComponentView paymentComponentView;

    @Captor ArgumentCaptor<PaymentBreakdownInput> paymentBreakdownInputCaptor;
    @Captor ArgumentCaptor<BookingConfirmationAnalyticsData> analyticsBody;
    @Captor ArgumentCaptor<ReviewBookingInput> reviewBookingInputCaptor;

    @Mock private BookingFlowInput bookingFlowInput;
    @Mock private ParcelablePaymentMethodsDetailsInput parcelablePaymentMethodsDetailsInput;
    @Mock private IsCustomerLoggedIn isCustomerLoggedIn;
    @Mock private SelectedRate selectedRate;
    @Mock private RoomBooking roomBooking;
    @Mock private DailyRateInput dailyRate;
    @Mock private TrackingAnalytics trackingAnalytics;
    @Mock private FirebaseLogger firebaseLogger;
    @Mock private CardInfo cardInfo;
    @Mock private GuestDetailsFormDataInput guestDetailsFormDataInput;
    @Mock private BookingAddress bookingAddress;
    @Mock private AcceptedCreditCard acceptedCreditCard;
    @Mock private UpsellItem selectedUpsellItem;
    @Mock private ReviewBookingMessageProvider messageProvider;
    @Mock private GetStringResource getStringResource;
    @Mock private GetLongResource getLongResource;
    @Mock private ParcelableCustomer parcelableCustomer;
    @Mock private Customer customer;
    @Mock private LogService logService;
    @Mock private SimplePersistenceManagerImpl storage;
    @Mock private BusinessPersistenceManager businessPersistenceManager;
    @Mock private ClearBookedRecentSearch clearBookedRecentSearch;
    @Mock private CreateCustomer createCustomer;
    @Mock ParcelableAddress postcodeAddressMock;
    @Mock private IsBusinessCustomerLoggedIn isBusinessCustomerLoggedIn;
    @Mock private AuthenticateCustomer authenticateCustomer;
    @Mock private GetCountries getCountries;
    @Mock private ContentManagedResourceRepository contentManagedResourceRepository;
    @Mock private DeviceLocaleProvider deviceLocaleProvider;
    @Mock private IsFeatureOn isFeatureOn;
    @Mock private GraphQLReviewBookingUseCase reviewBookingGraphQLUseCase;
    @Mock private GraphQLBookingDetailsUseCase graphQLBookingDetailsUseCase;
    @Mock private GraphQLFindBookingUseCase graphQLFindBookingUseCase;
    @Mock private AppConfiguration configuration;
    @Mock private ParcelableDonationsDomain parcelableDonationsDomain;
    @Mock private PayPalAccountNonce payPalAccountNonce;
    @Mock private Exception error;
    @Mock private UserCanceledException userCanceledException;
    @Mock private PayPalClient paypalClient;
    @Mock private FindBookingDomain findBookingDomain;
    @Mock private Context mockContext;

    @Captor
    ArgumentCaptor<ReviewBookingInput> reviewBookingInputArgumentCaptor;

    private Gson gson = GsonFactory.create(GsonAdapterFactory.create());

    @Rule
    public RxJavaTestRule rxJavaTestRule = new RxJavaTestRule();

    private CompositeDisposable viewCompositeDisposable = new CompositeDisposable();
    private CompositeDisposable networkCompositeDisposable = new CompositeDisposable();
    private CompositeDisposable pollingCompositeDisposable = new CompositeDisposable();

    private ReviewBookingPresenter presenter;

    private ReplayRelay<ReviewBookingInput> inputRelay;
    private ReplayRelay<EditGuestInput> guestDetailsUpdateRelay;
    private ReplayRelay<Address> paymentAddressRelay;
    private PublishRelay<PaymentRadioButtonView>  paymentRadioButtonViewPublishRelay;
    private PublishRelay<PaymentTimingChoice>  paymentTimingChoicePublishRelay;
    private BasketStatusRevisedPaymentsGraphQLContract.BasketStatusRevisedPaymentsData revisedPaymentsCompleted;
    private Booking bookingWithRooms;
    private PaymentMethodsGraphQLContract.PaymentMethodsData paymentMethodsAndBookingConfAndDonationsSuccess;
    private PaymentMethodsGraphQLContract.PaymentMethodsData paymentMethodsFailureAndBookingConfSuccess;
    private PaymentMethodsGraphQLContract.PaymentMethodsData paymentMethodsSuccessAndBookingConfFailure;
    private PaymentMethodsGraphQLContract.PaymentMethodsData paymentMethodsAndBookingConfSuccessWithDonationFailure;
    private String countryIsoCode = "GB";
    private String countryLegacyCode = "GB";
    private String city = "London";
    private String line1 = "120 High Holborn";
    private String line2 = "Chancery Lane";
    private String line4 = "line4";
    private String postcode = "ET2N 2AT";

    @Before
    public void onSetup() {
        when(selectedUpsellItem.code()).thenReturn(PI_BREAKFAST_CODE);
        when(selectedUpsellItem.freeBreakfastCode()).thenReturn(FREE_BREAKFAST_CODE);
        when(getCountries.fetchCountriesFromSharedPref()).thenReturn(countryMock);
        when(deviceLocaleProvider.getDeviceLocale()).thenReturn(Locale.UK);
        when(isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_DONATION)).thenReturn(true);
        when(deviceLocaleProvider.getDeviceLanguage()).thenReturn(LANGUAGE_ENGLISH);
        when(deviceLocaleProvider.getCountryIfRegion(any())).thenReturn(COUNTRY_CODE_UK);

        ParcelableDonationPackageDomain donation1 = new ParcelableDonationPackageDomain("code1", "GBP", 1f);
        ParcelableDonationPackageDomain donation2 = new ParcelableDonationPackageDomain("code2", "GBP", 3f);
        when(parcelableDonationsDomain.getDonationPackages()).thenReturn(Arrays.asList(donation1, donation2));
        setupPaymentDetailsInput();
        setupBookingFlow();
        setupReviewBookingInput();
        setupViewObservables();
        setupBookingRoom();
        setUpBookingWithRooms();

        inputRelay = ReplayRelay.create();
        guestDetailsUpdateRelay = ReplayRelay.create();
        paymentAddressRelay = ReplayRelay.create();
        paymentRadioButtonViewPublishRelay = PublishRelay.create();
        paymentTimingChoicePublishRelay = PublishRelay.create();

        initialisePresenter();

        roomsBooked = Collections.singletonList(roomBooking);

        when(view.onInputUpdated()).thenReturn(inputRelay);
        when(view.onGuestDetailsUpdated()).thenReturn(guestDetailsUpdateRelay);
        when(view.onPaymentAddressPostcodeFinderEntry()).thenReturn(paymentAddressRelay);
        when(view.onPaymentTypeSelectionChanged()).thenReturn(paymentRadioButtonViewPublishRelay);
        when(view.onPaymentTimingSelectionChange()).thenReturn(paymentTimingChoicePublishRelay);
        when(view.getPaymentComponent()).thenReturn(paymentComponentView);
        when(paymentComponentView.getPaymentTimingSelection()).thenReturn(PaymentTimingChoice.PAY_LATER.name());
        when(selectedRate.rateType()).thenReturn(rateType);
        when(selectedRate.rateName()).thenReturn(RATE_NAME);
        when(selectedRate.code()).thenReturn(rateType);
        when(selectedRate.description()).thenReturn(RATE_DESCRIPTION);
        when(bookingFlowInput.roomBookings()).thenReturn(roomsBooked);
        when(bookingFlowInput.hotelBrand()).thenReturn(BRAND_PI);
        when(dailyRate.getDate()).thenReturn(arrivalDate);
        when(getStringResource.invoke(any())).thenReturn("");
        when(view.donationButtonClicks()).thenReturn(Observable.never());
        when(guestDetailsFormDataInput.title()).thenReturn("Mr");
        when(guestDetailsFormDataInput.firstName()).thenReturn("Mark");
        when(guestDetailsFormDataInput.lastName()).thenReturn("O'Meara");
        when(guestDetailsFormDataInput.lastName()).thenReturn("O'Meara");
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.never());
        when(payPalAccountNonce.getString()).thenReturn("Nonce");
        setupDonations();

        revisedPaymentsCompleted = InstanceFactory.create(
                BasketStatusRevisedPaymentsGraphQLContract.BasketStatusRevisedPaymentsData.class,
                "apiTest/graphql/basket_status_revised_payments_completed.json");
        paymentMethodsAndBookingConfAndDonationsSuccess = InstanceFactory.create(
                PaymentMethodsGraphQLContract.PaymentMethodsData.class,
                "apiTest/paymentmethods/payment_methods_donation_and_booking_confirmation_success_gql.json");
        paymentMethodsFailureAndBookingConfSuccess = InstanceFactory.create(
                PaymentMethodsGraphQLContract.PaymentMethodsData.class,
                "apiTest/paymentmethods/payment_methods_not_available_gql.json");
        paymentMethodsSuccessAndBookingConfFailure = InstanceFactory.create(
                PaymentMethodsGraphQLContract.PaymentMethodsData.class,
                "apiTest/paymentmethods/payment_methods_success_donation_and_booking_confirmation_failure_gql.json");
        paymentMethodsAndBookingConfSuccessWithDonationFailure = InstanceFactory.create(
                PaymentMethodsGraphQLContract.PaymentMethodsData.class,
                "apiTest/paymentmethods/payment_methods_and_booking_confirmation_success_donation_unsuccessful_gql.json");
    }
    private void setupDonations() {
        when(contentManagedResourceRepository.getStringSingle(ContentManagedResourceRepository.Key.RATE_CONTENT.getValue()))
                .thenReturn(Single.just(
                        "[{\"classification\":\"A\",\"name\":\"Flex\","
                                + "\"description\":\"Pay now or on arrival, fully refundable with free cancellation up "
                                + "to 1pm on the day of arrival\","
                                + "\"bookingTermsMessage\":\"Booking terms 1\"},{\"classification\":\"F\",\"name\":\"Advance\","
                                + "\"description\":\"Pay now, fully refundable with free cancellation up to 28 full days before arrival\","
                                + "\"bookingTermsMessage\":\"\"}]"));
    }

    //TODO: Try convert to 3CP - Fixed below
//    @Test
//    public void testDefaultAttachViewNoConditions() {
//        float defaultCardFee = 0;
//        final CardSummaryModel cardSummaryModel = CardSummaryModel.create(reviewBookingInput);
//        //when(getStringResource.invoke(BOOKING_PRIVACY_FOOTER)).thenReturn("privacy footer");
//
//        startPresenter();
//
//        //todo: fix htmlContent
//        //verify(view).showPrivacyPolicy("privacy footer");
//        verify(view).setGuestDetails(reviewBookingInput);
//        verify(view).display(new ReviewBookModel(reviewBookingInput, defaultCardFee,
//                        PriceDomain.Companion.createDefault(), totalRatePrice, deviceLocaleProvider, "any pledge"),
//                cardSummaryModel, bookingFlowInput.numNights(), RATE_NAME, bookingFlowInput.isHub(),
//                cardSummaryModel.hasCardExpired(System.currentTimeMillis()), viewCompositeDisposable, countryMock,
//                false, false, false);
//    }


    @Test
    public void testViewDisplayCalledWhenInputMutatedObservableTriggered_WithPromo() {
        when(getStringResource.invoke(BOOKING_PRIVACY_FOOTER)).thenReturn("privacy footer");
        when(bookingFlowInput.numNights()).thenReturn(3);
        when(bookingFlowInput.formattedBaseRate()).thenReturn("£22.50");
        when(bookingFlowInput.promotionCode()).thenReturn("PROMO123");
        when(bookingFlowInput.promotionTag()).thenReturn("Special Offer");
        when(bookingFlowInput.isHub()).thenReturn(false);
        when(bookingFlowInput.chosenRate()).thenReturn(selectedRate);

        when(view.onGuestDetailsUpdated()).thenReturn(guestDetailsUpdateRelay);
        when(view.onInputUpdated()).thenReturn(inputRelay);
        when(view.onPaymentTypeSelectionChanged()).thenReturn(paymentRadioButtonViewPublishRelay);
        when(view.onPaymentTimingSelectionChange()).thenReturn(paymentTimingChoicePublishRelay);
        when(view.onPaymentBreakdownClick()).thenReturn(Observable.never());
        when(view.onGuestDetailsEditClick()).thenReturn(Observable.never());
        when(view.onConfirmBookingClick()).thenReturn(Observable.never());
        when(view.onPayPalButtonClick()).thenReturn(Observable.never());
        when(view.onCardVerificationOkOpera()).thenReturn(Observable.never());

        startPresenter();

        verify(view).display(any(ReviewBookModel.class), eq(null), eq(false),
                eq(3), eq(RATE_NAME), eq(false), eq("£22.50"),
                eq("PROMO123"), eq("Special Offer"), eq(viewCompositeDisposable),
                eq(countryMock), eq(false), eq(false));
    }

    @Test
    public void testViewDisplayCalledWhenInputMutatedObservableTriggered_WithNoPromo() {
        when(getStringResource.invoke(BOOKING_PRIVACY_FOOTER)).thenReturn("privacy footer");

        when(bookingFlowInput.numNights()).thenReturn(3);
        when(bookingFlowInput.isHub()).thenReturn(false);
        when(bookingFlowInput.chosenRate()).thenReturn(selectedRate);

        when(view.onGuestDetailsUpdated()).thenReturn(guestDetailsUpdateRelay);
        when(view.onInputUpdated()).thenReturn(inputRelay);
        when(view.onPaymentTypeSelectionChanged()).thenReturn(paymentRadioButtonViewPublishRelay);
        when(view.onPaymentTimingSelectionChange()).thenReturn(paymentTimingChoicePublishRelay);
        when(view.onPaymentBreakdownClick()).thenReturn(Observable.never());
        when(view.onGuestDetailsEditClick()).thenReturn(Observable.never());
        when(view.onConfirmBookingClick()).thenReturn(Observable.never());
        when(view.onPayPalButtonClick()).thenReturn(Observable.never());
        when(view.onCardVerificationOkOpera()).thenReturn(Observable.never());

        startPresenter();

        verify(view).display(any(ReviewBookModel.class), eq(null), eq(false),
                eq(3), eq(RATE_NAME), eq(false), eq(null),
                eq(null), eq(null), eq(viewCompositeDisposable),
                eq(countryMock), eq(false), eq(false));
    }

    @Test
    public void testShowPriceIncludesTaxesAndFees() {
        startPresenter();
        verify(view).showPriceIncludesTaxesAndFeesMessage(true);
    }

    @Test
    public void testCreateReservationGuestAndPaymentMethod_isCalledForBBUser() {
        when(storage.getCustomer()).thenReturn(CustomerFixture.INSTANCE.bCustomer());

        PaymentDetailsInput paymentDetailsInput =  mockPaymentDetailsInput()
                .toBuilder()
                .paymentMethodsDetailInput(null)
                .bookingFlowInput(bookingFlowInput)
                .build();

        when(bookingFlowInput.basketReference()).thenReturn("basketref");

        reviewBookingInput = reviewBookingInput
                .toBuilder()
                .isBusinessUser(true)
                .paymentDetailsInput(paymentDetailsInput).build();

        when(businessPersistenceManager.getBusinessCustomerEmail()).thenReturn("anyemail@bb.com");
        when(reviewBookingGraphQLUseCase.createReservationGuest(any()))
                .thenReturn(Single.just(new CreateReservationGuestDomain("basketref", null)));
        PaymentMethodsWithDonationAndBookingConfirmationGQLDomain paymentsDomain =
                GraphQLPaymentMethodsMapperKt.mapToPaymentMethodsGQL(paymentMethodsAndBookingConfAndDonationsSuccess);
        when(reviewBookingGraphQLUseCase.getPaymentMethodsWithDonationAndBookingConfirmation(any(), any(), any(Boolean.class)))
                .thenReturn(Single.just(paymentsDomain));

        startPresenter();
        verify(view).showLoadingSpinner(true, false, EMPTY_STRING);
        verify(reviewBookingGraphQLUseCase).createReservationGuest(any());
        verify(reviewBookingGraphQLUseCase).getPaymentMethodsWithDonationAndBookingConfirmation(any(), any(), anyBoolean());
        verify(view).showLoadingSpinner(false, false, EMPTY_STRING);
    }

    @Test
    public void testOnlyPaymentMethod_isCalledForLeisureUser() {
        PaymentDetailsInput paymentDetailsInput =  mockPaymentDetailsInput()
                .toBuilder()
                .paymentMethodsDetailInput(null)
                .bookingFlowInput(bookingFlowInput)
                .build();

        when(bookingFlowInput.basketReference()).thenReturn("basketref");

        reviewBookingInput = reviewBookingInput
                .toBuilder()
                .isBusinessUser(false)
                .paymentDetailsInput(paymentDetailsInput).build();

        PaymentMethodsWithDonationAndBookingConfirmationGQLDomain paymentsDomain =
                GraphQLPaymentMethodsMapperKt.mapToPaymentMethodsGQL(paymentMethodsAndBookingConfAndDonationsSuccess);
        when(reviewBookingGraphQLUseCase.getPaymentMethodsWithDonationAndBookingConfirmation(any(), any(), any(Boolean.class)))
                .thenReturn(Single.just(paymentsDomain));

        startPresenter();
        verify(view).showLoadingSpinner(true, false, EMPTY_STRING);
        verify(reviewBookingGraphQLUseCase, times(0)).createReservationGuest(any());
        verify(reviewBookingGraphQLUseCase).getPaymentMethodsWithDonationAndBookingConfirmation(any(), any(), anyBoolean());
        verify(view).showLoadingSpinner(false, false, EMPTY_STRING);
    }


    @Test
    public void testPopupIsDisplayed_whenCreateReservationGuestFailsForBBUser() {
        when(storage.getCustomer()).thenReturn(CustomerFixture.INSTANCE.bCustomer());

        PaymentDetailsInput paymentDetailsInput =  mockPaymentDetailsInput()
                .toBuilder()
                .paymentMethodsDetailInput(null)
                .bookingFlowInput(bookingFlowInput)
                .build();

        reviewBookingInput = reviewBookingInput
                .toBuilder()
                .isBusinessUser(true)
                .paymentDetailsInput(paymentDetailsInput).build();

        when(businessPersistenceManager.getBusinessCustomerEmail()).thenReturn("anyemail@bb.com");
        when(reviewBookingGraphQLUseCase.createReservationGuest(any()))
                .thenReturn(Single.error(new Exception()));

        startPresenter();

        verify(view).showLoadingSpinner(true, false, EMPTY_STRING);
        verify(reviewBookingGraphQLUseCase).createReservationGuest(any());
        verify(reviewBookingGraphQLUseCase, times(0))
                .getPaymentMethodsWithDonationAndBookingConfirmation(any(), any(), anyBoolean());
        verify(view).showLoadingSpinner(false, false, EMPTY_STRING);
        verify(view).showMessageandTakeUserBackToPreviousScreen();
    }


    @Test
    public void testPopupIsDisplayed_whenPaymentMethodFailsForBBUser() {
        when(storage.getCustomer()).thenReturn(CustomerFixture.INSTANCE.bCustomer());

        PaymentDetailsInput paymentDetailsInput =  mockPaymentDetailsInput()
                .toBuilder()
                .paymentMethodsDetailInput(null)
                .bookingFlowInput(bookingFlowInput)
                .build();

        when(bookingFlowInput.basketReference()).thenReturn("basketref");

        reviewBookingInput = reviewBookingInput
                .toBuilder()
                .isBusinessUser(true)
                .paymentDetailsInput(paymentDetailsInput).build();

        when(businessPersistenceManager.getBusinessCustomerEmail()).thenReturn("anyemail@bb.com");
        when(reviewBookingGraphQLUseCase.createReservationGuest(any()))
                .thenReturn(Single.just(new CreateReservationGuestDomain("basketref", null)));
        PaymentMethodsWithDonationAndBookingConfirmationGQLDomain paymentsDomain =
                GraphQLPaymentMethodsMapperKt.mapToPaymentMethodsGQL(paymentMethodsFailureAndBookingConfSuccess);
        when(reviewBookingGraphQLUseCase.getPaymentMethodsWithDonationAndBookingConfirmation(any(), any(), any(Boolean.class)))
                .thenReturn(Single.error(new Exception()));

        startPresenter();

        verify(view).showLoadingSpinner(true, false, EMPTY_STRING);
        verify(reviewBookingGraphQLUseCase).createReservationGuest(any());
        verify(reviewBookingGraphQLUseCase).getPaymentMethodsWithDonationAndBookingConfirmation(any(), any(), any(Boolean.class));
        verify(view).showLoadingSpinner(false, false, EMPTY_STRING);
        verify(view).showMessageandTakeUserBackToPreviousScreen();
    }

    @Test
    public void testPopupIsDisplayed_whenPaymentMethodFailsForLeisureUser() {
        PaymentDetailsInput paymentDetailsInput =  mockPaymentDetailsInput()
                .toBuilder()
                .paymentMethodsDetailInput(null)
                .bookingFlowInput(bookingFlowInput)
                .build();

        when(bookingFlowInput.basketReference()).thenReturn("basketref");

        reviewBookingInput = reviewBookingInput
                .toBuilder()
                .isBusinessUser(false)
                .paymentDetailsInput(paymentDetailsInput).build();

        PaymentMethodsWithDonationAndBookingConfirmationGQLDomain paymentsDomain =
                GraphQLPaymentMethodsMapperKt.mapToPaymentMethodsGQL(paymentMethodsAndBookingConfSuccessWithDonationFailure);
        when(reviewBookingGraphQLUseCase.getPaymentMethodsWithDonationAndBookingConfirmation(any(), any(), any(Boolean.class)))
                .thenReturn(Single.just(paymentsDomain));

        startPresenter();

        verify(view).showLoadingSpinner(true, false, EMPTY_STRING);
        verify(reviewBookingGraphQLUseCase, times(0)).createReservationGuest(any());
        verify(reviewBookingGraphQLUseCase).getPaymentMethodsWithDonationAndBookingConfirmation(any(), any(), any(Boolean.class));
        verify(view).showLoadingSpinner(false, false, EMPTY_STRING);
        verify(view, times(0)).showMessageandTakeUserBackToPreviousScreen();
    }

    @Test
    public void testPopupIsDisplayed_whenPaymentMethodBookingConfFailsForBBUser() {
        when(storage.getCustomer()).thenReturn(CustomerFixture.INSTANCE.bCustomer());

        PaymentDetailsInput paymentDetailsInput =  mockPaymentDetailsInput()
                .toBuilder()
                .paymentMethodsDetailInput(null)
                .bookingFlowInput(bookingFlowInput)
                .build();

        when(bookingFlowInput.basketReference()).thenReturn("basketref");

        reviewBookingInput = reviewBookingInput
                .toBuilder()
                .isBusinessUser(true)
                .paymentDetailsInput(paymentDetailsInput).build();

        when(businessPersistenceManager.getBusinessCustomerEmail()).thenReturn("anyemail@bb.com");
        when(reviewBookingGraphQLUseCase.createReservationGuest(any()))
                .thenReturn(Single.just(new CreateReservationGuestDomain("basketref", null)));
        PaymentMethodsWithDonationAndBookingConfirmationGQLDomain paymentsDomain =
                GraphQLPaymentMethodsMapperKt.mapToPaymentMethodsGQL(paymentMethodsSuccessAndBookingConfFailure);
        when(reviewBookingGraphQLUseCase.getPaymentMethodsWithDonationAndBookingConfirmation(any(), any(), any(Boolean.class)))
                .thenReturn(Single.error(new Exception()));

        startPresenter();

        verify(view).showLoadingSpinner(true, false, EMPTY_STRING);
        verify(reviewBookingGraphQLUseCase).createReservationGuest(any());
        verify(reviewBookingGraphQLUseCase).getPaymentMethodsWithDonationAndBookingConfirmation(any(), any(), any(Boolean.class));
        verify(view).showLoadingSpinner(false, false, EMPTY_STRING);
        verify(view).showMessageandTakeUserBackToPreviousScreen();

    }

    @Test
    public void testPopupIsDisplayed_whenPaymentMethodBookingConfFailsForLeisureUser() {
        PaymentDetailsInput paymentDetailsInput =  mockPaymentDetailsInput()
                .toBuilder()
                .paymentMethodsDetailInput(null)
                .bookingFlowInput(bookingFlowInput)
                .build();

        when(bookingFlowInput.basketReference()).thenReturn("basketref");

        reviewBookingInput = reviewBookingInput
                .toBuilder()
                .isBusinessUser(false)
                .paymentDetailsInput(paymentDetailsInput).build();

        PaymentMethodsWithDonationAndBookingConfirmationGQLDomain paymentsDomain =
                GraphQLPaymentMethodsMapperKt.mapToPaymentMethodsGQL(paymentMethodsAndBookingConfSuccessWithDonationFailure);
        when(reviewBookingGraphQLUseCase.getPaymentMethodsWithDonationAndBookingConfirmation(any(), any(), any(Boolean.class)))
                .thenReturn(Single.error(new Exception()));

        startPresenter();

        verify(view).showLoadingSpinner(true, false, EMPTY_STRING);
        verify(reviewBookingGraphQLUseCase, times(0)).createReservationGuest(any());
        verify(reviewBookingGraphQLUseCase).getPaymentMethodsWithDonationAndBookingConfirmation(any(), any(), any(Boolean.class));
        verify(view).showLoadingSpinner(false, false, EMPTY_STRING);
        verify(view).showMessageandTakeUserBackToPreviousScreen();

    }

//    *** These need to be revisited as the payment comp is used and details are in there
    //TODO: Try convert to 3CP
//    @Test
//    public void testBacsAdditionalInfoShown() {
//        reviewBookingInput = reviewBookingInput.toBuilder()
//                .cardType(BUSINESS_CARD)
//                .cnpPurchaseOrderNumber("Purchase order number")
//                .cnpTripPurpose("Trip purpose").build();
//
//        startPresenter();
//
//        verify(view).setPurchaseOrderNumber("Purchase order number");
//        verify(view).setTripPurpose("Trip purpose");
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void testBacsAdditionalInfoUpdated() {
//        startPresenter();
//
//        ReviewBookingInput updatedReviewBookingInput = reviewBookingInput.toBuilder()
//                .cardType(BUSINESS_CARD)
//                .cnpTripPurpose("Updated purpose")
//                .cnpPurchaseOrderNumber("Updated purchase number").build();
//
//        inputRelay.accept(updatedReviewBookingInput);
//
//        verify(view).setTripPurpose("Updated purpose");
//        verify(view).setPurchaseOrderNumber("Updated purchase number");
//    }
//*********************Till here
//
//&&&&&&&&&
//    These need to be modified but i dont think this is applicable anymore
    //TODO: Try convert to 3CP
//    @Test
//    public void callMakePaymentEndpointWhenPayLaterAndTheFeatureIsEnabled() {
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//        when(view.getCvv()).thenReturn(Observable.just(new Pair<>("123", false)));
//        when(storage.getAuthenticationRequiredFlag()).thenReturn(true);
//        reviewBookingInput = reviewBookingInput.toBuilder()
//                .cvv("123")
//                .userSelectedPaymentChoice(PaymentTimingChoice.PAY_LATER).build();
//
//        startPresenter();
//
//        verify(microServiceServiceMock).makePayment(reviewBookingInput, false);
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void doNotCallMakePaymentEndpointForBusinessCardEvenIf3dsFeatureIsEnabled() {
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//        when(view.getCvv()).thenReturn(Observable.just(new Pair<>("123", false)));
//        reviewBookingInput = reviewBookingInput.toBuilder()
//                .cvv("123")
//                .cardType(BUSINESS_CARD)
//                .userSelectedPaymentChoice(PaymentTimingChoice.PAY_LATER).build();
//
//        startPresenter();
//
//        verify(microServiceServiceMock, times(0)).makePayment(any(), anyBoolean());
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void callMakePaymentEndpointWhenPayNow() {
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//        when(view.getCvv()).thenReturn(Observable.just(new Pair<>("123", false)));
//        reviewBookingInput = reviewBookingInput.toBuilder()
//                .cvv("123")
//                .userSelectedPaymentChoice(PaymentTimingChoice.PAY_NOW).build();
//
//        startPresenter();
//
//        verify(microServiceServiceMock).makePayment(reviewBookingInput, true);
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void callMakeBookingEndpointWhenPayLaterAndTheFeatureIsEnabledButNo3ds() {
//// 3DS probably
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//        //when(view.getInvoiceOption()).thenReturn(Observable.just(State.RIGHT));
//        when(view.getCvv()).thenReturn(Observable.just(new Pair<>("123", false)));
//        reviewBookingInput = reviewBookingInput.toBuilder()
//                .cvv("123")
//                .userSelectedPaymentChoice(PaymentTimingChoice.PAY_LATER).build();
//
//        startPresenter();
//
//        verify(microServiceServiceMock).makeBooking(reviewBookingInput, false, false, null);
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void callMakeBookingEndpointWhenPayNowButNo3ds() {
//        // 3DS probably
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//        //when(view.getInvoiceOption()).thenReturn(Observable.just(State.RIGHT));
//        when(view.getCvv()).thenReturn(Observable.just(new Pair<>("123", false)));
//        reviewBookingInput = reviewBookingInput.toBuilder()
//                .cvv("123")
//                .userSelectedPaymentChoice(PaymentTimingChoice.PAY_NOW).build();
//
//        Response<MakePaymentResponse> response = (Response<MakePaymentResponse>)
//                JsonResponseHelper.responseOk(MakePaymentResponse.class, "apiTest/makePayment-success-no3ds.json");
//        when(microServiceServiceMock.makePayment(reviewBookingInput, true)).thenReturn(Single.just(response));
//
//        startPresenter();
//
//        verify(microServiceServiceMock).makeBooking(reviewBookingInput, false, true, null);
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void doNotCallMakePaymentEndpointWhenPayLaterAndTheFeatureIsDisabled() {
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//        when(view.getCvv()).thenReturn(Observable.just(new Pair<>("123", false)));
//
//        startPresenter();
//
//        verify(microServiceServiceMock, times(0)).makePayment(any(), anyBoolean());
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void callMakeBookingEndpointWhenPayLaterAndTheFeatureIsDisabled() {
//        // 3DS probably
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//        //when(view.getInvoiceOption()).thenReturn(Observable.just(State.RIGHT));
//        reviewBookingInput = reviewBookingInput.toBuilder()
//                .cvv("")
//                .userSelectedPaymentChoice(PaymentTimingChoice.PAY_LATER).build();
//
//        startPresenter();
//
//        verify(microServiceServiceMock).makeBooking(reviewBookingInput, false, false, null);
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void testMakePaymentNoThreeDeeS() {
//        String surname = "surname";
//        String email = "email@mail.com";
//
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//        reviewBookingInput = reviewBookingInput.toBuilder().userSelectedPaymentChoice(PaymentTimingChoice.PAY_NOW).build();
//
//        Response<MakePaymentResponse> paymentResponse = (Response<MakePaymentResponse>)
//                JsonResponseHelper.responseOk(MakePaymentResponse.class, "apiTest/makePayment-success-no3ds.json");
//
//        when(microServiceServiceMock.makePayment(any(), anyBoolean())).thenReturn(Single.just(paymentResponse));
//
//        Response<MakeBookingResponse> bookingResponse = (Response<MakeBookingResponse>)
//                JsonResponseHelper.responseOk(MakeBookingResponse.class, "apiTest/makeBooking-success.json");
//
//        when(microServiceServiceMock.makeBooking(any(), eq(true), eq(true), any())).thenReturn(Single.just(bookingResponse));
//        when(guestDetailsFormDataInput.title()).thenReturn("Mr");
//        when(guestDetailsFormDataInput.firstName()).thenReturn("First");
//        when(guestDetailsFormDataInput.lastName()).thenReturn(surname);
//        when(guestDetailsFormDataInput.email()).thenReturn(email);
//        when(clearBookedRecentSearch.execute(any())).thenReturn(Completable.complete());
//
//        startPresenter();
//
//        String confirmationNumber = bookingResponse.body().confirmationNumber();
//        Booking booking = new Booking(confirmationNumber, surname, arrivalDate,
//                arrivalDate.plusDays(numNights), HOTEL_CODE, HOTEL_NAME, 1, 5, "Mr First surname",
//                "Flex",
//                new com.whitbread.premierinn.domain.common.PriceDomain(22.5f, GBP),
//                new com.whitbread.premierinn.domain.common.PriceDomain(22.5f, GBP),
//                false, false, false, null, false, false, false, null,
//                AmendRestrictions.Companion.createWithDefaults(false, false, false, false, false));
//
//        verify(storeBooking).execute(booking);
//
//        verify(view).startMyBookingsActivity(booking.getBookingReference(), "",
//                reviewBookingInput.paymentDetailsInput().bookerDetails().email(), null, false, "");
//    }
//&&&&&&&&

//This is not a valid test here
//    @Test
//    public void testMakePaymentFailure() {
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//        // Fix this
//        startPresenter();
//
//        //verify(view).showGenericError(); // Replace with another kind of error as this one is not used in R&B
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void testMakeBookingCardVerificationRelay() {
//        // 3DS probably
//
//        String surname = "surname";
//        String email = "email@email.com";
//
//        Response<MakeBookingResponse> bookingResponse = (Response<MakeBookingResponse>)
//                JsonResponseHelper.responseOk(MakeBookingResponse.class, "apiTest/makeBooking-success.json");
//        when(microServiceServiceMock.makeBooking(any(), eq(true), eq(false), eq(null))).thenReturn(Single.just(bookingResponse));
//
//        when(guestDetailsFormDataInput.title()).thenReturn("Mr");
//        when(guestDetailsFormDataInput.firstName()).thenReturn("First");
//        when(guestDetailsFormDataInput.lastName()).thenReturn(surname);
//        when(guestDetailsFormDataInput.email()).thenReturn(email);
//        when(clearBookedRecentSearch.execute(any())).thenReturn(Completable.complete());
//
//        startPresenter();
//
//        String confirmationNumber = bookingResponse.body().confirmationNumber();
//
//        Booking booking = new Booking(confirmationNumber, surname, arrivalDate,
//                arrivalDate.plusDays(numNights), HOTEL_CODE, HOTEL_NAME, 1, 5, "Mr First surname",
//                "Flex", new com.whitbread.premierinn.domain.common.PriceDomain(22.5f, GBP),
//                new com.whitbread.premierinn.domain.common.PriceDomain(22.5f, GBP), false, false,
//                false, null, false, false, false, null,
//                AmendRestrictions.Companion.createWithDefaults(false, false, false, false, false));
//
//        verify(storeBooking).execute(booking);
//        verify(view).startMyBookingsActivity(booking.getBookingReference(), "",
//                reviewBookingInput.paymentDetailsInput().bookerDetails().email(), null, false, "");
//    }

    @Test
    public void testOnPaymentBreakdownClick() {
        when(view.onPaymentBreakdownClick()).thenReturn(Observable.just(Unit.INSTANCE));

        startPresenter();

        verify(view).startPaymentBreakdownActivity(any());
    }

    @Test
    public void testDonationSentToBreakdown() {
        when(view.donationButtonClicks()).thenReturn(Observable.just(new DonationView.DonationType("MAX_DONATION", 3f, "£3")));
        when(view.onPaymentBreakdownClick()).thenReturn(Observable.just(Unit.INSTANCE));

        startPresenter();

        verify(view).startPaymentBreakdownActivity(paymentBreakdownInputCaptor.capture());
        assertEquals(CommonMappersKt.toBookingPrice(new PriceDomain(3f, GBP)),
                paymentBreakdownInputCaptor.getValue().donation());
    }

    @Test
    public void testOnEditGuestsClick() {
        when(view.onGuestDetailsEditClick()).thenReturn(Observable.just(Unit.INSTANCE));

        startPresenter();

        verify(view).startEditGuestActivity(EditGuestInput.create(paymentDetailsInput.guestDetailsList(),
                paymentDetailsInput.bookerDetails(), paymentDetailsInput.isBookerStaying()));
    }

//    ******* Some more related to cnp
    //TODO: Try convert to 3CP
//    @Test
//    public void testCnpAuthMemorableWordSentInBooking() {
//        when(view.onCardNotPresentToggleButtonState()).thenReturn(Observable.just(true));
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//        when(view.getMemorableWord()).thenReturn(CNP_MEMORABLE_WORD);
//        reviewBookingInput = reviewBookingInput.toBuilder().cardType(BUSINESS_CARD).build();
//        startPresenter();
//
//        verify(microServiceServiceMock).makeBooking(reviewBookingInputCaptor.capture(), eq(true), eq(false), eq(null));
//        assertTrue(reviewBookingInputCaptor.getValue().businessBookingOptions().cardNotPresentAuth());
//        assertEquals(CNP_MEMORABLE_WORD, reviewBookingInputCaptor.getValue().businessBookingOptions().atosPassword());
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void testCnpOptionsBlankForBusinessAccountByDefault() {
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//        reviewBookingInput = reviewBookingInput.toBuilder().cardType(BUSINESS_CARD).build();
//
//        startPresenter();
//
//        BusinessBookingOptions expectedBusinessBookingOptions = BusinessBookingOptions.builder()
//                .alcoholAllowed(false)
//                .carParkingAllowed(false)
//                .cardNotPresentAuth(false)
//                .wifiAccessAllowed(false)
//                .breakfastCode(PI_BREAKFAST_CODE).build();
//
//        verify(microServiceServiceMock).makeBooking(reviewBookingInputCaptor.capture(), eq(true), eq(false), eq(null));
//        assertEquals(expectedBusinessBookingOptions, reviewBookingInputCaptor.getValue().businessBookingOptions());
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void testCnpOptionsNullForNonBusinessAccount() {
//        reviewBookingInput = reviewBookingInput.toBuilder().cardType(VISA_CARD_TYPE).build();
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//
//        startPresenter();
//
//        verify(microServiceServiceMock).makeBooking(reviewBookingInputCaptor.capture(), eq(true), eq(false), eq(null));
//        assertNull(reviewBookingInputCaptor.getValue().businessBookingOptions());
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void testCnpAllOptionsSentWithBooking() {
//        String tripPurpose = "Conference trip";
//        String purchaseOrder = "233442";
//
//        reviewBookingInput = reviewBookingInput.toBuilder()
//                .cardType(BUSINESS_CARD)
//                .cnpTripPurpose(tripPurpose)
//                .cnpPurchaseOrderNumber(purchaseOrder).build();
//
//        when(view.onAlcoholToggleChanged()).thenReturn(Observable.just(true));
//        when(view.onCardNotPresentToggleButtonState()).thenReturn(Observable.just(true));
//        when(view.getMemorableWord()).thenReturn(CNP_MEMORABLE_WORD);
//        when(view.onParkingToggleChanged()).thenReturn(Observable.just(true));
//        when(view.onDinnerAllowanceToggleChanged()).thenReturn(Observable.just(true));
//        when(view.onDinnerBudgetChanged()).thenReturn(Observable.just("95"));
//        when(view.onWifiToggleChanged()).thenReturn(Observable.just(true));
//
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//
//        startPresenter();
//
//        BusinessBookingOptions expectedBusinessBookingOptions = BusinessBookingOptions.builder()
//                .alcoholAllowed(true)
//                .cardNotPresentAuth(true)
//                .atosPassword(CNP_MEMORABLE_WORD)
//                .carParkingAllowed(true)
//                .dinnerAllowance(CommonMappersKt.toBookingPrice(PriceDomain.Companion.createWithGBPCurrency(95f)))
//                .wifiAccessAllowed(true)
//                .breakfastCode(PI_BREAKFAST_CODE)
//                .customerReference(tripPurpose)
//                .purchaseOrder(purchaseOrder).build();
//
//        verify(microServiceServiceMock).makeBooking(reviewBookingInputCaptor.capture(), eq(true), eq(false), eq(null));
//        assertEquals(expectedBusinessBookingOptions, reviewBookingInputCaptor.getValue().businessBookingOptions());
//    }
//    *******
    @Test
    public void testSetGuestDetails() {
        boolean isBookerStaying = false;
        PaymentDetailsInput paymentDetailsInput =  mockPaymentDetailsInput();
        reviewBookingInput = reviewBookingInput.toBuilder().paymentDetailsInput(paymentDetailsInput).build();
        List<GuestDetailsFormDataInput> guestDetailsFormDataInputList = Collections.singletonList(guestDetailsFormDataInput);

        startPresenter();
        EditGuestInput guestDetailsInput = EditGuestInput.create(guestDetailsFormDataInputList, guestDetailsFormDataInput,
                isBookerStaying);
        guestDetailsUpdateRelay.accept(guestDetailsInput);

        PaymentDetailsInput expectedPaymentDetailsInput = PaymentDetailsInput.builder()
                .bookingFlowInput(reviewBookingInput.paymentDetailsInput().bookingFlowInput())
                .paymentMethodsDetailInput(parcelablePaymentMethodsDetailsInput)
                .address(mockBookingAddress())
                .customer(parcelableCustomer)
                .bookerDetails(guestDetailsFormDataInput)
                .guestDetailsList(guestDetailsFormDataInputList)
                .isBookerStaying(isBookerStaying)
                .isBusinessTrip(false)
                .isTaxExempt(false)
                .marketingOptIn(true)
                .build();

        ReviewBookingInput expectedReviewBookingInput = reviewBookingInput.toBuilder()
                .paymentDetailsInput(expectedPaymentDetailsInput).build();

        InOrder inOrder = inOrder(view);
        inOrder.verify(view, times(1)).setGuestDetails(any());
        inOrder.verify(view).setGuestDetails(expectedReviewBookingInput);
    }

    @Test
    public void testOperaFlow() {
        when(bookingFlowInput.hotelCode()).thenReturn(HOTEL_CODE_OPERA);
        when(bookingFlowInput.basketReference()).thenReturn("sessionId123");
        when(bookingAddress.line1()).thenReturn("Test line 1");
        when(bookingAddress.line2()).thenReturn("Test line 2");
        when(bookingAddress.postcode()).thenReturn("EC1N2TD");
        when(bookingAddress.city()).thenReturn("London");
        when(bookingAddress.countryCode()).thenReturn("GB");
        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(view.getPaymentComponent()).thenReturn(paymentComponentView);
        when(paymentComponentView.billingAddress()).thenReturn(bookingAddress);
        when(paymentComponentView.getPaymentTimingSelection()).thenReturn(PaymentTimingChoice.PAY_LATER.name());
        when(configuration.getGraphQLUrl()).thenReturn("https://api.preprod.premierinn.digital");
        when(paymentComponentView.getDinnerAllowance()).thenReturn(true);
        when(paymentComponentView.getWifiAccessAllowed()).thenReturn(true);
        when(paymentComponentView.getCarParkingAllowed()).thenReturn(true);
        when(view.isPaymentComponentReadyForSubmission()).thenReturn(true);
        startPresenter();

        verify(view).showLoading(true);
        verify(reviewBookingGraphQLUseCase).initiatePayment(any());
    }

    @Test
    public void testEndToEndFlowWithPromoIncentiveSetToFalse() {
        setupEndToEndMocks();

        PublishSubject<Unit> confirmBookingSubject = PublishSubject.create();
        when(view.onConfirmBookingClick()).thenReturn(confirmBookingSubject);

        BasketStatusRevisedPaymentsDomain basketStatusRevisedPaymentsDomain = mockBasketStatusAsCompleted();

        startPresenter();

        paymentTimingChoicePublishRelay.accept(PaymentTimingChoice.PAY_NOW);
        confirmBookingSubject.onNext(Unit.INSTANCE);

        Assert.assertEquals("COMPLETED", basketStatusRevisedPaymentsDomain.getBasketStatus().name());

        verify(trackingAnalytics).track(eq(AnalyticsConstants.ScreenState.BOOKING_CONFIRMATION),
                any(BookingConfirmationAnalyticsData.class));
        verify(storage).setIsAppPromotionalIncentiveAvailable(false);
    }

    @Test
    public void testOnSuccessfulBooking_dbIsUpdated_dashboardIsUpdated() {
        setupEndToEndMocks();

        PublishSubject<Unit> confirmBookingSubject = PublishSubject.create();
        when(view.onConfirmBookingClick()).thenReturn(confirmBookingSubject);

        BasketStatusRevisedPaymentsDomain basketStatusRevisedPaymentsDomain = mockBasketStatusAsCompleted();

        startPresenter();

        paymentTimingChoicePublishRelay.accept(PaymentTimingChoice.PAY_NOW);
        confirmBookingSubject.onNext(Unit.INSTANCE);


        Assert.assertEquals("COMPLETED", basketStatusRevisedPaymentsDomain.getBasketStatus().name());

        verify(storeBooking).execute(any());
        verify(clearBookedRecentSearch).execute(any());
        verify(storage).setRefreshDashboard(true);
    }


    @Test
    public void testOnSuccessfulBooking_FirebaseIsCalled() {
        setupEndToEndMocks();

        PublishSubject<Unit> confirmBookingSubject = PublishSubject.create();
        when(view.onConfirmBookingClick()).thenReturn(confirmBookingSubject);

        mockBasketStatusAsCompleted();

        startPresenter();

        paymentTimingChoicePublishRelay.accept(PaymentTimingChoice.PAY_NOW);
        confirmBookingSubject.onNext(Unit.INSTANCE);

        verify(firebaseLogger).logEvent(eq(FirebaseAnalytics.Event.PURCHASE),
                any(FirebaseParams.class));

    }

    @Test
    public void testOnSuccessfulBooking_AnalyticsIsCalled() {
        setupEndToEndMocks();

        PublishSubject<Unit> confirmBookingSubject = PublishSubject.create();
        when(view.onConfirmBookingClick()).thenReturn(confirmBookingSubject);

        mockBasketStatusAsCompleted();

        startPresenter();

        paymentTimingChoicePublishRelay.accept(PaymentTimingChoice.PAY_NOW);
        confirmBookingSubject.onNext(Unit.INSTANCE);

        verify(trackingAnalytics).track(eq(AnalyticsConstants.ScreenState.BOOKING_CONFIRMATION),
                any(BookingConfirmationAnalyticsData.class));

    }

    @Test
    public void testShowBookerAsOnlyGuestFormatIsSet() {
        List<GuestDetailsFormDataInput> listWithOneElement = new ArrayList<>();
        listWithOneElement.add(guestDetailsFormDataInput);

        paymentDetailsInput = paymentDetailsInput.toBuilder()
                .isBookerStaying(true)
                .guestDetailsList(listWithOneElement).build();
        reviewBookingInput = reviewBookingInput.toBuilder().paymentDetailsInput(paymentDetailsInput).build();

        startPresenter();

        verify(view).showBookerAsOnlyGuest();
    }

    @Test
    public void testShowBookerAsOneOfManyGuestsFormatIsSet() {
        List<GuestDetailsFormDataInput> listWithTwoElements = new ArrayList<>();
        listWithTwoElements.add(guestDetailsFormDataInput);
        listWithTwoElements.add(guestDetailsFormDataInput);

        paymentDetailsInput = paymentDetailsInput.toBuilder()
                .isBookerStaying(true)
                .guestDetailsList(listWithTwoElements).build();
        reviewBookingInput = reviewBookingInput.toBuilder().paymentDetailsInput(paymentDetailsInput).build();

        startPresenter();

        verify(view).showBookerAsOneOfManyGuests();
    }

    @Test
    public void testUsesDefaultFormatIfBookerIsNotStaying() {
        List<GuestDetailsFormDataInput> listWithOneElement = new ArrayList<>();
        listWithOneElement.add(guestDetailsFormDataInput);

        paymentDetailsInput = paymentDetailsInput.toBuilder()
                .isBookerStaying(false)
                .guestDetailsList(listWithOneElement).build();
        reviewBookingInput = reviewBookingInput.toBuilder().paymentDetailsInput(paymentDetailsInput).build();

        startPresenter();

        verify(view, times(0)).showBookerAsOneOfManyGuests();
        verify(view, times(0)).showBookerAsOnlyGuest();
    }

   // OUT OF SCOPE - Functionality doesnt exist----

    @Test
    public void testPaymentTimingSelectionChange_PayNow() {
        startPresenter();

        paymentTimingChoicePublishRelay.accept(PaymentTimingChoice.PAY_NOW);

        verify(view).setPayNowInTotalPriceViewMessage(true);
    }

    @Test
    public void testPaymentTimingSelectionChange_PayLater() {
        startPresenter();

        paymentTimingChoicePublishRelay.accept(PaymentTimingChoice.PAY_LATER);

        verify(view).setPayNowInTotalPriceViewMessage(false);
    }

//Tried to do this but couldnt get ito make it work due to Appsflyer
//    @Test
//    public void testGetTokenAndMakeBookingConfirmationCall_onSuccess_startsNavigateToBookingActivity() {
//        when(bookingFlowInput.totalStayPrice(anyBoolean())).thenReturn(totalRatePrice);
//            when(view.getContext()).thenReturn(any(Context.class));
//            initialisePresenter();
//            presenter.reviewBookingInput = reviewBookingInput;
//            presenter.paymentDetailsInput = paymentDetailsInput;
//            presenter.uuidBasketReference = "uuidbasketreftest";
//            presenter.totalPrice = new PriceDomain(amount, currency);
//            presenter.paymentTimingChoice = PaymentTimingChoice.PAY_NOW;
//            FindBookingDomain findBookingDomain = new FindBookingDomain(
//                    "opera", "booking_reference",
//                    "uuidbasketreftest", "anyToken", HOTEL_CODE);
//            when(graphQLFindBookingUseCase.findBooking(any())).thenReturn(Single.just(findBookingDomain));
//
//            when(graphQLBookingDetailsUseCase.bookingConfirmationAndManageBooking(any(), any(), any(),
//                    any(), any(), any())).thenReturn(Observable.just(bookingWithRooms));
//
//            when(storeBooking.execute(any())).thenReturn(Completable.complete());
//            when(clearBookedRecentSearch.execute(any())).thenReturn(Completable.complete());
//
//            presenter.getTokenAndMakeBookingConfirmationCall(
//                    nameOnCard, bookingFlowInput, view);
//
//            verify(trackingAnalytics).track(eq(AnalyticsConstants.ScreenState.BOOKING_CONFIRMATION),
//                    any(BookingConfirmationAnalyticsData.class));
//
//            verify(view).startMyBookingsActivity(any(), any(), any(), any(), any());
//
//    }

//CNP TODO
    //TODO: Try convert to 3CP
//    @Test
//    public void testConfirmedBookingWithCardNotPresentOnSuccess() {
//        reviewBookingInput = reviewBookingInput.toBuilder().cardType(BUSINESS_CARD).build();
//        when(view.onCardNotPresentToggleButtonState()).thenReturn(Observable.just(true));
//        when(view.getMemorableWord()).thenReturn("somePass");
//        when(view.onCardNotPresentToggleButtonState()).thenReturn(Observable.just(true));
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//
//        startPresenter();
//
//        verify(view).showMemorableWordError(false);
//        verify(microServiceServiceMock).makeBooking(any(), eq(true), eq(false), eq(null));
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void testConfirmedBookingWithCardNotPresentOnError() {
//        reviewBookingInput = reviewBookingInput.toBuilder().cardType(BUSINESS_CARD).build();
//        when(view.onCardNotPresentToggleButtonState()).thenReturn(Observable.just(true));
//        when(view.getMemorableWord()).thenReturn("");
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//
//        startPresenter();
//
//        verify(view).showMemorableWordError(true);
//        verify(microServiceServiceMock, never()).makeBooking(any(), eq(true), eq(true), eq(null));
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void testConfirmedBookingLoggedToTrackingAnalytics() {
//        reviewBookingInput = reviewBookingInput.toBuilder().cardType(BUSINESS_CARD).build();
//        paymentDetailsInput = paymentDetailsInput.toBuilder().isBusinessTrip(true).build();
//        reviewBookingInput = reviewBookingInput.toBuilder().paymentDetailsInput(paymentDetailsInput).build();
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//        when(clearBookedRecentSearch.execute(any())).thenReturn(Completable.complete());
//
//        Response<MakeBookingResponse> bookingResponse = (Response<MakeBookingResponse>)
//                JsonResponseHelper.responseOk(MakeBookingResponse.class, "apiTest/makeBooking-success.json");
//
//        when(microServiceServiceMock.makeBooking(any(), eq(true), eq(false), eq(null))).thenReturn(Single.just(bookingResponse));
//
//        startPresenter();
//
//        verify(trackingAnalytics).track(eq(AnalyticsConstants.ScreenState.REVIEW_BOOKING), any(ReviewBookingAnalyticsData.class));
//        verify(trackingAnalytics).track(eq(AnalyticsConstants.ScreenState.BOOKING_CONFIRMATION), analyticsBody.capture());
//        assertExpectedValue(analyticsBody.getValue());
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void testAmendAndCancelMessageFromAvailability() {
//        when(contentManagedResourceRepository.getStringSingle(ContentManagedResourceRepository.Key.RATE_CONTENT.getValue()))
//                .thenReturn(Single.just(
//                        "[{\"classification\":\"A\",\"name\":\"Flex\","
//                                + "\"description\":\"Pay now or on arrival, fully refundable with free cancellation up "
//                                + "to 1pm on the day of arrival\","
//                                + "\"bookingTermsMessage\":\"\"},{\"classification\":\"F\",\"name\":\"Advance\","
//                                + "\"description\":\"Pay now, fully refundable with free cancellation
//                                up to 28 full days before arrival\","
//                                + "\"bookingTermsMessage\":\"\"}]"));
//        when(selectedRate.getAmendmentRule()).thenReturn(amendRule);
//        when(selectedRate.getCancellationRule()).thenReturn(bookingRule);
//        startPresenter();
//
//        verify(view).showRateDescriptionMessage("Some amend text" + "\n" + "Some cancel text");
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void testAmendAndCancelMessageFromFirebase() {
//        when(selectedRate.getAmendmentRule()).thenReturn(bookingRule);
//        when(selectedRate.getCancellationRule()).thenReturn(bookingRule);
//        startPresenter();
//
//        verify(view).showRateDescriptionMessage("Booking terms 1");
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void showMemorableWordWhenCardNotPresentToggleIsOn() {
//        reviewBookingInput = reviewBookingInput.toBuilder().cardType(VISA_CARD_TYPE).build();
//        when(view.onCardNotPresentToggleButtonState()).thenReturn(Observable.just(true));
//
//        startPresenter();
//
//        verify(view).showCnpSwitches(true);
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void hideMemorableWordWhenCardNotPresentToggleIsOff() {
//        reviewBookingInput = reviewBookingInput.toBuilder().cardType(VISA_CARD_TYPE).build();
//        when(view.onCardNotPresentToggleButtonState()).thenReturn(Observable.just(false));
//
//        startPresenter();
//
//        verify(view).resetCnpOptions();
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void showAlcoholOptionWhenDinnerAllowanceToggleIsOn() {
//        when(view.onDinnerAllowanceToggleChanged()).thenReturn(Observable.just(true));
//
//        startPresenter();
//
//        verify(view).showAlcoholOption(true);
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void showAlcoholOptionWhenDinnerAllowanceToggleIsOff() {
//        when(view.onDinnerAllowanceToggleChanged()).thenReturn(Observable.just(false));
//
//        startPresenter();
//
//        verify(view).showAlcoholOption(false);
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void enableAlcoholOptionToggleWhenDinnerBudgetInputted() {
//        when(view.onDinnerBudgetChanged()).thenReturn(Observable.just("1"));
//
//        startPresenter();
//
//        verify(view).enableIncludeAlcoholOption(true);
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void disableAlcoholOptionToggleWhenDinnerBudgetEmpty() {
//        when(view.onDinnerBudgetChanged()).thenReturn(Observable.just(""));
//
//        startPresenter();
//
//        verify(view).enableIncludeAlcoholOption(false);
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void showMaxValueErrorOnDinnerBudget() {
//        when(view.onDinnerBudgetChanged()).thenReturn(Observable.just("101"));
//
//        startPresenter();
//
//        verify(view).showDinnerAllowanceError(true, messageProvider.getErrorDinnerAllowanceMaxValue());
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void showMinValueErrorOnDinnerBudget() {
//        when(view.onDinnerBudgetChanged()).thenReturn(Observable.just("0"));
//
//        startPresenter();
//
//        verify(view).showDinnerAllowanceError(true, messageProvider.getErrorDinnerAllowanceMinValue());
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void showValidValueErrorOnDinnerBudget() {
//        when(view.onDinnerBudgetChanged()).thenReturn(Observable.just("lele"));
//
//        startPresenter();
//
//        verify(view).showDinnerAllowanceError(true, messageProvider.getErrorDinnerAllowanceEmptyField());
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void showNoErrorOnDinnerBudget() {
//        when(view.onDinnerBudgetChanged()).thenReturn(Observable.just("50"));
//
//        startPresenter();
//
//        verify(view).showDinnerAllowanceError(false, "");
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void testMaxDonation() {
//        when(view.donationButtonClicks()).thenReturn(Observable.just(DonationView.DonationType.MAX_DONATION));
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//        reviewBookingInput = reviewBookingInput.toBuilder().cardType(VISA_CARD_TYPE).build();
//
//        startPresenter();
//
//        verify(microServiceServiceMock).makeBooking(reviewBookingInputArgumentCaptor.capture(), eq(true), eq(false), eq(null));
//
//        assertEquals(CommonMappersKt.toParcelablePrice(DonationView.DonationType.MAX_DONATION.getDonationAmount()),
//                reviewBookingInputArgumentCaptor.getValue().donation());
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void testMinDonation() {
//        when(view.donationButtonClicks()).thenReturn(Observable.just(DonationView.DonationType.MIN_DONATION));
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//
//        startPresenter();
//
//        verify(microServiceServiceMock).makeBooking(reviewBookingInputArgumentCaptor.capture(), eq(true), eq(false), eq(null));
//
//        assertEquals(CommonMappersKt.toParcelablePrice(DonationView.DonationType.MIN_DONATION.getDonationAmount()),
//                reviewBookingInputArgumentCaptor.getValue().donation());
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void testNoDonationOptionSelected() {
//        when(view.donationButtonClicks()).thenReturn(Observable.never());
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//
//        startPresenter();
//
//        verify(microServiceServiceMock).makeBooking(reviewBookingInputArgumentCaptor.capture(), eq(true), eq(false), eq(null));
//
//        assertEquals(CommonMappersKt.toParcelablePrice(PriceDomain.Companion.createDefault()),
//                reviewBookingInputArgumentCaptor.getValue().donation());
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void testNoDonationSelected() {
//        when(view.donationButtonClicks()).thenReturn(Observable.just(DonationView.DonationType.NO_DONATION));
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//
//        startPresenter();
//
//        verify(microServiceServiceMock).makeBooking(reviewBookingInputArgumentCaptor.capture(), eq(true), eq(false), eq(null));
//
//        assertEquals(CommonMappersKt.toParcelablePrice(PriceDomain.Companion.createDefault()),
//                reviewBookingInputArgumentCaptor.getValue().donation());
//    }

    //TODO: Try convert to 3CP
//    @Test
//    public void testDonationThenNoDonationSelected() {
//        when(view.donationButtonClicks()).thenReturn(Observable.just(DonationView.DonationType.MAX_DONATION,
//                DonationView.DonationType.NO_DONATION));
//        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
//
//        startPresenter();
//
//        verify(microServiceServiceMock).makeBooking(reviewBookingInputArgumentCaptor.capture(), eq(true), eq(false), eq(null));
//
//        assertEquals(CommonMappersKt.toParcelablePrice(PriceDomain.Companion.createDefault()),
//                reviewBookingInputArgumentCaptor.getValue().donation());
//    }

    @Test
    public void testPaypalNonceChangeObserver() {
        when(bookingFlowInput.basketReference()).thenReturn("sessionId123");
        when(bookingAddress.line1()).thenReturn("Test line 1");
        when(bookingAddress.line2()).thenReturn("Test line 2");
        when(bookingAddress.postcode()).thenReturn("EC1N2TD");
        when(bookingAddress.city()).thenReturn("London");
        when(bookingAddress.countryCode()).thenReturn("GB");
        when(view.getPaymentComponent()).thenReturn(paymentComponentView);
        when(paymentComponentView.billingAddress()).thenReturn(bookingAddress);
        when(paymentComponentView.getPaymentTimingSelection()).thenReturn(PaymentTimingChoice.PAY_LATER.name());
        when(configuration.getGraphQLUrl()).thenReturn("https://api.preprod.premierinn.digital");
        when(paymentComponentView.getDinnerAllowance()).thenReturn(true);
        when(paymentComponentView.getWifiAccessAllowed()).thenReturn(true);
        when(paymentComponentView.getCarParkingAllowed()).thenReturn(true);

        startPresenter();

        TestObserver<String> testObserver = new TestObserver<>();
        presenter.observePayPalNonceChange().subscribe(testObserver);
        presenter.onPayPalSuccess(payPalAccountNonce);
        verify(reviewBookingGraphQLUseCase).initiatePayment(any());
        testObserver.assertValue("Nonce");

    }

    @Test
    public void testOnPaypalErrorObserver_Error() {
        startPresenter();

        TestObserver<Integer> testObserver = new TestObserver<>();
        presenter.observeOnPaypalError().subscribe(testObserver);
        presenter.onPayPalFailure(error);
        verify(logService).logException(any(), any());
        verify(view).showPaymentError();
        verify(view).showPaypalButtonLoading(false);
        testObserver.assertValue(0);
    }

    @Test
    public void testOnPaypalErrorObserver_UserCancelException() {
        startPresenter();

        TestObserver<Integer> testObserver = new TestObserver<>();
        presenter.observeOnPaypalError().subscribe(testObserver);
        presenter.onPayPalFailure(userCanceledException);
        verify(view).showPaypalButtonLoading(false);
        testObserver.assertValue(1);
    }

    @Test
    public void testDonationFeatureDisabled_DonationOptionNotVisible() {
        when(isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_DONATION)).thenReturn(false);

        PaymentDetailsInput paymentDetailsInput =  mockPaymentDetailsInput()
                .toBuilder()
                .paymentMethodsDetailInput(null)
                .bookingFlowInput(bookingFlowInput)
                .build();

        when(bookingFlowInput.basketReference()).thenReturn("basketref");

        reviewBookingInput = reviewBookingInput
                .toBuilder()
                .isBusinessUser(false)
                .paymentDetailsInput(paymentDetailsInput).build();

        PaymentMethodsWithDonationAndBookingConfirmationGQLDomain paymentsDomain =
                GraphQLPaymentMethodsMapperKt.mapToPaymentMethodsGQL(paymentMethodsAndBookingConfAndDonationsSuccess);
        when(reviewBookingGraphQLUseCase.getPaymentMethodsWithDonationAndBookingConfirmation(any(), any(), any(Boolean.class)))
                .thenReturn(Single.just(paymentsDomain));

        startPresenter();

        verify(view).setDonationOptionVisible(false);
    }

    @Test
    public void testDonationUpdate_AnimatesTotalPriceChange() {
        when(view.donationButtonClicks()).thenReturn(Observable.just(new DonationView.DonationType("MAX_DONATION",   3f, "£3")));

        when(isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_DONATION)).thenReturn(false);

        PaymentDetailsInput paymentDetailsInput =  mockPaymentDetailsInput()
                .toBuilder()
                .paymentMethodsDetailInput(null)
                .bookingFlowInput(bookingFlowInput)
                .build();

        when(bookingFlowInput.basketReference()).thenReturn("basketref");

        reviewBookingInput = reviewBookingInput
                .toBuilder()
                .isBusinessUser(false)
                .paymentDetailsInput(paymentDetailsInput).build();

        PaymentMethodsWithDonationAndBookingConfirmationGQLDomain paymentsDomain =
                GraphQLPaymentMethodsMapperKt.mapToPaymentMethodsGQL(paymentMethodsAndBookingConfAndDonationsSuccess);
        when(reviewBookingGraphQLUseCase.getPaymentMethodsWithDonationAndBookingConfirmation(any(), any(), any(Boolean.class)))
                .thenReturn(Single.just(paymentsDomain));

        startPresenter();

        verify(view).animateTotalPriceChange();
    }

    @Test
    public void testDonationUpdate_NoDonation_DoesNotAnimateTotalPriceChange() {
        when(view.donationButtonClicks()).thenReturn(Observable.just(new DonationView.DonationType("NO_DONATION", 0f, "£0")));

        when(isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_DONATION)).thenReturn(false);

        PaymentDetailsInput paymentDetailsInput =  mockPaymentDetailsInput()
                .toBuilder()
                .paymentMethodsDetailInput(null)
                .bookingFlowInput(bookingFlowInput)
                .build();

        when(bookingFlowInput.basketReference()).thenReturn("basketref");

        reviewBookingInput = reviewBookingInput
                .toBuilder()
                .isBusinessUser(false)
                .paymentDetailsInput(paymentDetailsInput).build();

        PaymentMethodsWithDonationAndBookingConfirmationGQLDomain paymentsDomain =
                GraphQLPaymentMethodsMapperKt.mapToPaymentMethodsGQL(paymentMethodsAndBookingConfAndDonationsSuccess);
        when(reviewBookingGraphQLUseCase.getPaymentMethodsWithDonationAndBookingConfirmation(any(), any(), any(Boolean.class)))
                .thenReturn(Single.just(paymentsDomain));

        startPresenter();

        verify(view, times(0)).animateTotalPriceChange();
    }


    @Test
    public void testNoDonationPackages_HidesDonationOption() {
        when(isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_DONATION)).thenReturn(false);

        PaymentDetailsInput paymentDetailsInput =  mockPaymentDetailsInput()
                .toBuilder()
                .paymentMethodsDetailInput(null)
                .bookingFlowInput(bookingFlowInput)
                .build();

        when(bookingFlowInput.basketReference()).thenReturn("basketref");

        reviewBookingInput = reviewBookingInput
                .toBuilder()
                .isBusinessUser(false)
                .paymentDetailsInput(paymentDetailsInput).build();

        PaymentMethodsWithDonationAndBookingConfirmationGQLDomain paymentsDomain =
                GraphQLPaymentMethodsMapperKt.mapToPaymentMethodsGQL(paymentMethodsAndBookingConfAndDonationsSuccess);
        when(reviewBookingGraphQLUseCase.getPaymentMethodsWithDonationAndBookingConfirmation(any(), any(), any(Boolean.class)))
                .thenReturn(Single.just(paymentsDomain));

        startPresenter();

        verify(view).setDonationOptionVisible(false);
    }

    @Test
    public void testNullOperaDonations_HidesDonationOption() {
        reviewBookingInput = reviewBookingInput.toBuilder().operaDonations(null).build();
        when(isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_DONATION)).thenReturn(false);

        PaymentDetailsInput paymentDetailsInput =  mockPaymentDetailsInput()
                .toBuilder()
                .paymentMethodsDetailInput(null)
                .bookingFlowInput(bookingFlowInput)
                .build();

        when(bookingFlowInput.basketReference()).thenReturn("basketref");

        reviewBookingInput = reviewBookingInput
                .toBuilder()
                .isBusinessUser(false)
                .paymentDetailsInput(paymentDetailsInput).build();

        PaymentMethodsWithDonationAndBookingConfirmationGQLDomain paymentsDomain =
                GraphQLPaymentMethodsMapperKt.mapToPaymentMethodsGQL(paymentMethodsAndBookingConfAndDonationsSuccess);
        when(reviewBookingGraphQLUseCase.getPaymentMethodsWithDonationAndBookingConfirmation(any(), any(), any(Boolean.class)))
                .thenReturn(Single.just(paymentsDomain));

        startPresenter();

        verify(view).setDonationOptionVisible(false);
    }

    @Test
    public void testOnDetachView_DisposesViewCompositeDisposable() {
        startPresenter();

        presenter.detachView();

        assertEquals(0, viewCompositeDisposable.size());
    }

    @Test
    public void testOnDestroy_DisposesNetworkCompositeDisposable() {
        startPresenter();

        presenter.onDestroy();

        assertTrue(networkCompositeDisposable.isDisposed());
    }

    @Test
    public void testPaypalButtonClick_ShowsLoadingAndCollectsDeviceData() {
        when(view.onPayPalButtonClick()).thenReturn(Observable.just(Unit.INSTANCE));

        startPresenter();

        verify(view).myTokenizePayPalAccountWithVaultMethod();
        verify(view).collectDeviceDataForPayPal();
        verify(view).showPaypalButtonLoading(true);
    }

    @Test
    public void testPaymentTypeSelectionChanged_TogglePayPalButton() {
        PaymentRadioButtonView paymentRadioButtonView = org.mockito.Mockito.mock(PaymentRadioButtonView.class);
        when(paymentRadioButtonView.getPaymentOptionForPayPal()).thenReturn(Arrays.asList("PAYPAL"));
        when(paymentRadioButtonView.getTag()).thenReturn("PayPal");

        startPresenter();
        paymentRadioButtonViewPublishRelay.accept(paymentRadioButtonView);

        verify(view).togglePayPalButton(true);
    }

    @Test
    public void testPaymentTypeSelectionChanged_HidePayPalButton_WhenNoPayPalOption() {
        PaymentRadioButtonView paymentRadioButtonView = org.mockito.Mockito.mock(PaymentRadioButtonView.class);
        when(paymentRadioButtonView.getPaymentOptionForPayPal()).thenReturn(Collections.emptyList());
        when(paymentRadioButtonView.getTag()).thenReturn("any");

        startPresenter();
        paymentRadioButtonViewPublishRelay.accept(paymentRadioButtonView);

        verify(view).togglePayPalButton(false);
    }

    @Test
    public void testPaymentTypeSelectionChanged_ReserveWithoutCard() {
        PaymentRadioButtonView paymentRadioButtonView = org.mockito.Mockito.mock(PaymentRadioButtonView.class);
        when(paymentRadioButtonView.getPaymentOptionForPayPal()).thenReturn(Collections.emptyList());
        when(paymentRadioButtonView.getTag()).thenReturn("reserve_without_card");
        when(paymentRadioButtonView.isGooglePaySelected()).thenReturn(false);

        startPresenter();
        paymentRadioButtonViewPublishRelay.accept(paymentRadioButtonView);

        verify(view).setConfirmButtonText(false);
    }

    @Test
    public void testInitiatePayment_ShowsPaymentError_OnFailure() {
        when(bookingFlowInput.hotelCode()).thenReturn(HOTEL_CODE_OPERA);
        when(bookingFlowInput.basketReference()).thenReturn("sessionId123");
        when(bookingAddress.line1()).thenReturn("Test line 1");
        when(bookingAddress.line2()).thenReturn("Test line 2");
        when(bookingAddress.postcode()).thenReturn("EC1N2TD");
        when(bookingAddress.city()).thenReturn("London");
        when(bookingAddress.countryCode()).thenReturn("GB");
        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(view.getPaymentComponent()).thenReturn(paymentComponentView);
        when(paymentComponentView.billingAddress()).thenReturn(bookingAddress);
        when(paymentComponentView.getPaymentTimingSelection()).thenReturn(PaymentTimingChoice.PAY_LATER.name());
        when(configuration.getGraphQLUrl()).thenReturn("https://api.preprod.premierinn.digital");
        when(paymentComponentView.getDinnerAllowance()).thenReturn(true);
        when(paymentComponentView.getWifiAccessAllowed()).thenReturn(true);
        when(paymentComponentView.getCarParkingAllowed()).thenReturn(true);
        when(view.isPaymentComponentReadyForSubmission()).thenReturn(true);
        when(view.isBusinessCustomerWithNoCardsAllowed()).thenReturn(false);
        when(reviewBookingGraphQLUseCase.initiatePayment(any())).thenReturn(Single.error(new RuntimeException("Payment failed")));

        startPresenter();

        verify(view).showPaymentError();
    }

    @Test
    public void testInitiatePayment_ShowsValidationError_WhenPaymentComponentNotReady() {
        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(view.isPaymentComponentReadyForSubmission()).thenReturn(false);

        startPresenter();

        verify(view).showPaymentComponentValidationError();
        verify(view).showLoading(false);
    }

    @Test
    public void testObserveConfirmBookingClick_MakeReservationWithoutCard_WhenStatusIsNotRequired() {
        when(bookingFlowInput.hotelCode()).thenReturn(HOTEL_CODE_OPERA);
        when(bookingFlowInput.basketReference()).thenReturn("sessionId123");
        when(bookingAddress.line1()).thenReturn("Test line 1");
        when(bookingAddress.line2()).thenReturn("Test line 2");
        when(bookingAddress.postcode()).thenReturn("EC1N2TD");
        when(bookingAddress.city()).thenReturn("London");
        when(bookingAddress.countryCode()).thenReturn("GB");
        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(view.getPaymentComponent()).thenReturn(paymentComponentView);
        when(paymentComponentView.billingAddress()).thenReturn(bookingAddress);
        when(paymentComponentView.getPaymentTimingSelection()).thenReturn(PaymentTimingChoice.PAY_LATER.name());
        when(configuration.getGraphQLUrl()).thenReturn("https://api.uat.premierinn.digital");
        when(paymentComponentView.getDinnerAllowance()).thenReturn(false);
        when(paymentComponentView.getWifiAccessAllowed()).thenReturn(false);
        when(paymentComponentView.getCarParkingAllowed()).thenReturn(false);
        when(view.isPaymentComponentReadyForSubmission()).thenReturn(true);
        when(view.isBusinessCustomerWithNoCardsAllowed()).thenReturn(false);
        verify(reviewBookingGraphQLUseCase, times(0))
                .getPaymentMethodsWithDonationAndBookingConfirmation(any(), any(), anyBoolean());
        InitiatePaymentDomain initiatePaymentDomain =
                new InitiatePaymentDomain("NOT_REQUIRED", null);
        when(reviewBookingGraphQLUseCase.initiatePayment(any())).thenReturn(Single.just(initiatePaymentDomain));
        mockBasketStatusAsCompleted();


        startPresenter();

//        2 times since one is for initiate payment and another for get basket status
        verify(view, times(3)).showLoadingSpinner(eq(true), eq(false), any());
    }

    @Test
    public void testObserveConfirmBookingClick_LoadThreeCpIPage_WhenPaymentRequiredDetailsPresent() {
        when(bookingFlowInput.hotelCode()).thenReturn(HOTEL_CODE_OPERA);
        when(bookingFlowInput.basketReference()).thenReturn("sessionId123");
        when(bookingAddress.line1()).thenReturn("Test line 1");
        when(bookingAddress.line2()).thenReturn("Test line 2");
        when(bookingAddress.postcode()).thenReturn("EC1N2TD");
        when(bookingAddress.city()).thenReturn("London");
        when(bookingAddress.countryCode()).thenReturn("GB");
        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(view.getPaymentComponent()).thenReturn(paymentComponentView);
        when(paymentComponentView.billingAddress()).thenReturn(bookingAddress);
        when(paymentComponentView.getPaymentTimingSelection()).thenReturn(PaymentTimingChoice.PAY_LATER.name());
        when(configuration.getGraphQLUrl()).thenReturn("https://api.preprod.premierinn.digital");
        when(paymentComponentView.getDinnerAllowance()).thenReturn(false);
        when(paymentComponentView.getWifiAccessAllowed()).thenReturn(false);
        when(paymentComponentView.getCarParkingAllowed()).thenReturn(false);
        when(view.isPaymentComponentReadyForSubmission()).thenReturn(true);
        when(view.isBusinessCustomerWithNoCardsAllowed()).thenReturn(false);

        PaymentRequiredDetailsDomain paymentRequiredDetailsDomain =
                new PaymentRequiredDetailsDomain("<html>IPage</html>", "session123", "https://provider.url");
        InitiatePaymentDomain initiatePaymentDomain =
                new InitiatePaymentDomain("REQUIRED", paymentRequiredDetailsDomain);
        when(reviewBookingGraphQLUseCase.initiatePayment(any())).thenReturn(Single.just(initiatePaymentDomain));

        startPresenter();

//        Similar to above
        verify(view).showLoading(true);
        verify(view).showLoading(false);
        verify(view).loadThreeCpIPage(any(), any(), any());
    }

    @Test
    public void testObserveConfirmBookingClick_ShowPaymentError_WhenNoIPageHtml() {
        when(bookingFlowInput.hotelCode()).thenReturn(HOTEL_CODE_OPERA);
        when(bookingFlowInput.basketReference()).thenReturn("sessionId123");
        when(bookingAddress.line1()).thenReturn("Test line 1");
        when(bookingAddress.line2()).thenReturn("Test line 2");
        when(bookingAddress.postcode()).thenReturn("EC1N2TD");
        when(bookingAddress.city()).thenReturn("London");
        when(bookingAddress.countryCode()).thenReturn("GB");
        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(view.getPaymentComponent()).thenReturn(paymentComponentView);
        when(paymentComponentView.billingAddress()).thenReturn(bookingAddress);
        when(paymentComponentView.getPaymentTimingSelection()).thenReturn(PaymentTimingChoice.PAY_LATER.name());
        when(configuration.getGraphQLUrl()).thenReturn("https://api.preprod.premierinn.digital");
        when(paymentComponentView.getDinnerAllowance()).thenReturn(false);
        when(paymentComponentView.getWifiAccessAllowed()).thenReturn(false);
        when(paymentComponentView.getCarParkingAllowed()).thenReturn(false);
        when(view.isPaymentComponentReadyForSubmission()).thenReturn(true);
        when(view.isBusinessCustomerWithNoCardsAllowed()).thenReturn(false);

        InitiatePaymentDomain initiatePaymentDomain =
                new InitiatePaymentDomain("REQUIRED", null);
        when(reviewBookingGraphQLUseCase.initiatePayment(any())).thenReturn(Single.just(initiatePaymentDomain));

        startPresenter();

        verify(view).showLoading(true);
        verify(view).showPaymentError();
    }

    @Test
    public void testObserveConfirmBookingClick_ShowPaymentError_WhenPaymentRequiredDetailsHasNullIPageHtml() {
        when(bookingFlowInput.hotelCode()).thenReturn(HOTEL_CODE_OPERA);
        when(bookingFlowInput.basketReference()).thenReturn("sessionId123");
        when(bookingAddress.line1()).thenReturn("Test line 1");
        when(bookingAddress.line2()).thenReturn("Test line 2");
        when(bookingAddress.postcode()).thenReturn("EC1N2TD");
        when(bookingAddress.city()).thenReturn("London");
        when(bookingAddress.countryCode()).thenReturn("GB");
        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(view.getPaymentComponent()).thenReturn(paymentComponentView);
        when(paymentComponentView.billingAddress()).thenReturn(bookingAddress);
        when(paymentComponentView.getPaymentTimingSelection()).thenReturn(PaymentTimingChoice.PAY_LATER.name());
        when(configuration.getGraphQLUrl()).thenReturn("https://api.preprod.premierinn.digital");
        when(paymentComponentView.getDinnerAllowance()).thenReturn(false);
        when(paymentComponentView.getWifiAccessAllowed()).thenReturn(false);
        when(paymentComponentView.getCarParkingAllowed()).thenReturn(false);
        when(view.isPaymentComponentReadyForSubmission()).thenReturn(true);
        when(view.isBusinessCustomerWithNoCardsAllowed()).thenReturn(false);

        PaymentRequiredDetailsDomain paymentRequiredDetailsDomain =
                new PaymentRequiredDetailsDomain(null, "session123", "https://provider.url");
        InitiatePaymentDomain initiatePaymentDomain =
                new InitiatePaymentDomain("REQUIRED", paymentRequiredDetailsDomain);
        when(reviewBookingGraphQLUseCase.initiatePayment(any())).thenReturn(Single.just(initiatePaymentDomain));

        startPresenter();

        verify(view).showLoading(true);
        verify(view).showPaymentError();
    }

    @Test
    public void testObserveConfirmBookingClick_ShowPaymentComponentValidationError_WhenComponentNotReadyAndNotBusinessCustomer() {
        when(view.onConfirmBookingClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(view.isPaymentComponentReadyForSubmission()).thenReturn(false);

        startPresenter();

        verify(view).showLoading(true);
        verify(view).showPaymentComponentValidationError();
        verify(view).showLoading(false);
    }

    private FirebaseParams createExpectedFirebaseParamsForBB() {
        FirebaseParams firebaseParams = new FirebaseParams();

        firebaseParams.putInteger(FirebaseParams.ParamName.NUMBER_OF_NIGHTS, numNights);
        firebaseParams.putInteger(FirebaseParams.ParamName.NUMBER_OF_BOOKINGS, 1);
        firebaseParams.putInteger(FirebaseParams.ParamName.NUMBER_OF_GUESTS, numAdults + numChildren);
        firebaseParams.putString(FirebaseParams.ParamName.CURRENCY, currency);
        firebaseParams.putString(FirebaseParams.ParamName.HOTEL_CODE, HOTEL_CODE);
        firebaseParams.putString(FirebaseParams.ParamName.RATE_TYPE, FORMATTED_RATE_CODE + "-DBS-22.50");
        firebaseParams.putDouble(FirebaseParams.ParamName.PRICE_AMOUNT, ((Float) amount).doubleValue());
        firebaseParams.putFormattedDate(FirebaseParams.ParamName.ARRIVAL_DATE, arrivalDate);
        firebaseParams.putFormattedDate(FirebaseParams.ParamName.DEPARTURE_DATE, endDate);
        firebaseParams.putInteger(FirebaseParams.ParamName.NUMBER_OF_ROOMS, 1);
        firebaseParams.putString(FirebaseParams.ParamName.TRIP_TYPE, "Business");
        firebaseParams.putString(FirebaseParams.ParamName.PAYMENT_TYPE, VISA_CARD_TYPE);

        return firebaseParams;
    }

    private FirebaseParams createExpectedFirebaseParamsForLeisure() {
        FirebaseParams expectedParams = new FirebaseParams();

        expectedParams.putInteger(FirebaseParams.ParamName.NUMBER_OF_BOOKINGS, 1);
        expectedParams.putString(FirebaseParams.ParamName.CURRENCY, currency);
        expectedParams.putDouble(FirebaseParams.ParamName.PRICE_AMOUNT, ((Float) amount).doubleValue());
        expectedParams.putString(FirebaseParams.ParamName.HOTEL_CODE, HOTEL_CODE);
        expectedParams.putFormattedDate(FirebaseParams.ParamName.ARRIVAL_DATE, arrivalDate);
        expectedParams.putFormattedDate(FirebaseParams.ParamName.DEPARTURE_DATE, endDate);
        expectedParams.putInteger(FirebaseParams.ParamName.NUMBER_OF_NIGHTS, numNights);
        expectedParams.putInteger(FirebaseParams.ParamName.NUMBER_OF_ROOMS, 1);
        expectedParams.putInteger(FirebaseParams.ParamName.NUMBER_OF_GUESTS, numAdults + numChildren);
        expectedParams.putString(FirebaseParams.ParamName.RATE_TYPE, FORMATTED_RATE_CODE + "-DBS-22.50");
        expectedParams.putString(FirebaseParams.ParamName.TRIP_TYPE, "Leisure");
        expectedParams.putString(FirebaseParams.ParamName.PAYMENT_TYPE, VISA_CARD_TYPE);

        return expectedParams;
    }

    private BookingConfirmationAnalyticsData createBookingConfirmationData(Boolean isBusiness) {
        return BookingConfirmationAnalyticsData.builder()
                .totalPrice(new PriceDomain(amount, currency))
                .hotelCode(HOTEL_CODE)
                .arrivalDate(arrivalDate)
                .numNights(numNights)
                .numGuests(numAdults + numChildren)
                .rateCode(RATE_CODE)
                .lettingType("DBS")
                .roomPrice(totalRatePrice)
                .business(isBusiness)
                .card(VISA_CARD_TYPE)
                .confirmationNumber(BOOKING_CONFIRMATION_NUMBER)
                .prepaid(true)
                .paymentMethod("CARD")
                .isPaymentOutage(false)
                .bookingCompleteTime(new java.util.Date())
                .roomsBooked(roomsBooked)
                .isLoggedIn(false)
                .businessDinnerAllowanceSelected(false)
                .businessAlcoholSelected(false)
                .dinnerBudget(PriceDomain.Companion.createDefault())
                .businessParkingSelected(false)
                .businessUltimateWifi(false)
                .goshDonation(0f)
                .accountCreated(false)
                .totalUpsellCost(0f)
                .totalCostExcludingCC(amount)
                .donationRevenue(0f)
                .addedExtras(null)
                .cardType(new kotlin.Pair<>(false, ""))
                .promoCode(null)
                .paymentTiming(PaymentTimingChoice.PAY_NOW.name())
                .build();
    }

    private void assertExpectedValue(BookingConfirmationAnalyticsData bookingConfirmationAnalyticsData) {
        assertEquals("arrivalDate", arrivalDate, bookingConfirmationAnalyticsData.arrivalDate());
        assertEquals("confirmationNumber", BOOKING_CONFIRMATION_NUMBER, bookingConfirmationAnalyticsData.confirmationNumber());
        assertTrue("business", bookingConfirmationAnalyticsData.business());
        assertEquals("hotelCode", HOTEL_CODE, bookingConfirmationAnalyticsData.hotelCode());
        assertEquals("donationRevenue", 0f, bookingConfirmationAnalyticsData.donationRevenue());
        assertEquals("cardType", BUSINESS_CARD, bookingConfirmationAnalyticsData.cardType());
        assertEquals("numGuests", numAdults + numChildren, bookingConfirmationAnalyticsData.numGuests());
        assertEquals("numNights", numNights, bookingConfirmationAnalyticsData.numNights());
        assertTrue("prepaid", bookingConfirmationAnalyticsData.prepaid());
        assertEquals("rateCode", selectedRate.code(), bookingConfirmationAnalyticsData.rateCode());
        assertEquals("roomsBooked", roomsBooked, bookingConfirmationAnalyticsData.roomsBooked());
        assertEquals("selectedUpsellItem", selectedUpsellItem, bookingConfirmationAnalyticsData.addedExtras().getFirst());
        assertEquals("totalCostExcludingCC", totalRatePrice.getAmount(), bookingConfirmationAnalyticsData.totalCostExcludingCC());
        assertEquals("totalPrice.amount", totalRatePrice.getAmount(), bookingConfirmationAnalyticsData.totalPrice().getAmount());
        assertEquals("totalUpsellCost", TOTAL_UPSELL_COST, bookingConfirmationAnalyticsData.totalUpsellCost());
        // NOTE: Not testing date
    }

    private void setupPaymentDetailsInput() {
        when(parcelablePaymentMethodsDetailsInput.getParcelableBookingConfirmation())
                .thenReturn(new ParcelableBookingConfirmation("GBP", 100f, "bookingRef1234"));
        paymentDetailsInput = PaymentDetailsInput.builder()
                .bookerDetails(guestDetailsFormDataInput)
                .bookingFlowInput(bookingFlowInput)
                .paymentMethodsDetailInput(parcelablePaymentMethodsDetailsInput)
                .isBusinessTrip(false)
                .isTaxExempt(false)
                .marketingOptIn(true)
                .guestDetailsList(Collections.emptyList())
                .paymentProvider(paymentProvider)
                .shouldCreateAccount(false)
                .accountPassword("Pirate")
                .savePaymentDetails(false)
                .address(bookingAddress).isBookerStaying(false).build();
    }

    private void setupBookingFlow() {

        DailyRate roomRate = DailyRate.create(LocalDate.now(),
                CommonMappersKt.toBookingPrice(PriceDomain.Companion.createWithGBPCurrency(2f)));

        RoomBooking roomBooking = new RoomBooking(
                1,
                1,
                0,
                false,
                RoomType.DOUBLE.name(),
                "",
                Collections.singletonList(MappersKt.toDailyRateInput(roomRate)),
                CommonMappersKt.toParcelablePrice(PriceDomain.Companion.createDefault()),
                1,
                null);

        List<Breakfast> breakfasts = Breakfast.createBreakfasts(selectedUpsellItem,
                Collections.singletonList(roomBooking));

        when(bookingFlowInput.isHub()).thenReturn(false);
        when(bookingFlowInput.hotelImageReference()).thenReturn(HOTEL_IMAGE_REFERENCE);
        when(bookingFlowInput.hotelName()).thenReturn(HOTEL_NAME);
        when(bookingFlowInput.hotelCode()).thenReturn(HOTEL_CODE);
        when(bookingFlowInput.chosenRate()).thenReturn(selectedRate);
        when(bookingFlowInput.arrivalDate()).thenReturn(arrivalDate);
        when(bookingFlowInput.numNights()).thenReturn(numNights);
        when(bookingFlowInput.numGuests()).thenReturn(numAdults + numChildren);
        when(bookingFlowInput.selectedUpsellItem()).thenReturn(selectedUpsellItem);
        when(bookingFlowInput.totalRoomsCost(anyBoolean())).thenReturn(totalRatePrice);
    }

    private void setupReviewBookingInput() {
        reviewBookingInput = ReviewBookingInput.builder()
                .paymentDetailsInput(paymentDetailsInput)
                .cardInfo(cardInfo)
                .cardUrl(cardUrl)
                .cardNumber(cardNumber)
                .nameOnCard(nameOnCard)
                .expiryDate("0318")
                .cardHolderAddress(bookingAddress)
                .donation(CommonMappersKt.toParcelablePrice(PriceDomain.Companion.createDefault()))
                .marketingOptIn(marketingOptIn)
                .cardType(VISA_CARD_TYPE)
                .operaDonations(parcelableDonationsDomain)
                .selectedCharityPackageCode(operaCharityPackageCode)
                .bookingReference("booking_reference")
                .uuidBasketReference("uuidbasketreftest")
                .build();
    }

    //TODO: Try convert to 3CP
    private void setupViewObservables() {
        when(view.onPaymentBreakdownClick()).thenReturn(Observable.never());
        when(view.onGuestDetailsEditClick()).thenReturn(Observable.never());
//        when(view.onBacsAdditionalInformationEditClick()).thenReturn(Observable.never());
//        when(view.onPaymentDetailsEditClick()).thenReturn(Observable.never());
        when(view.onConfirmBookingClick()).thenReturn(Observable.never());
        when(view.onPayPalButtonClick()).thenReturn(Observable.never());
        when(view.onCardVerificationOkOpera()).thenReturn(Observable.never());
//        when(view.getCvv()).thenReturn(Observable.never());
//        when(view.onCardNotPresentToggleButtonState()).thenReturn(Observable.just(false));
//        when(view.onDinnerAllowanceToggleChanged()).thenReturn(Observable.never());
//        when(view.onDinnerBudgetChanged()).thenReturn(Observable.never());
//        when(view.onAlcoholToggleChanged()).thenReturn(Observable.never());
//        when(view.onWifiToggleChanged()).thenReturn(Observable.never());
//        when(view.onParkingToggleChanged()).thenReturn(Observable.never());

    }

    private void setupBookingRoom() {
        when(roomBooking.getType()).thenReturn(RoomType.DOUBLE.name());
        when(roomBooking.totalRoomPrice(false)).thenReturn(totalRatePrice);
        when(roomBooking.getDailyRates()).thenReturn(Collections.singletonList(dailyRate));
        when(roomBooking.getAdults()).thenReturn(numAdults);
        when(roomBooking.getChildren()).thenReturn(numChildren);
        when(roomBooking.getLettingType()).thenReturn(new LettingType("DBS"));
        when(roomBooking.getLettingCode()).thenReturn("DOUBLE");
    }

    private void setUpBookingWithRooms() {
        bookingWithRooms = new Booking("confirmationNumber", "surname", arrivalDate,
                arrivalDate.plusDays(numNights), HOTEL_CODE, HOTEL_NAME, 1, 5,
                "Mr First surname", "Flex", new PriceDomain(22.5f, GBP), new PriceDomain(22.5f, GBP),
                new PriceDomain(22.5f, GBP), false, false,
                false, false, false, null, Collections.emptyList(),
                AmendRestrictions.Companion.createWithDefaults(false, false, false, false, false),
                Guest.Companion.createDefault(), Collections.singletonList(new Booking.Room("1", RoomType.DOUBLE, "DBL",
                1, 0, LeadGuest.Companion.createDefault(), null)), false, null);
    }

    private void setupEndToEndMocks() {
        when(storage.isAppPromotionalIncentiveAvailable()).thenReturn(true);
        when(bookingFlowInput.hotelCode()).thenReturn(HOTEL_CODE_OPERA);
        when(bookingFlowInput.basketReference()).thenReturn("sessionId123");
        when(bookingAddress.line1()).thenReturn("Test line 1");
        when(bookingAddress.line2()).thenReturn("Test line 2");
        when(bookingAddress.postcode()).thenReturn("EC1N2TD");
        when(bookingAddress.city()).thenReturn("London");
        when(bookingAddress.countryCode()).thenReturn("GB");
        when(view.getPaymentComponent()).thenReturn(paymentComponentView);
        when(paymentComponentView.billingAddress()).thenReturn(bookingAddress);
        when(paymentComponentView.getPaymentTimingSelection()).thenReturn(PaymentTimingChoice.PAY_LATER.name());
        when(configuration.getGraphQLUrl()).thenReturn("https://api.preprod.premierinn.digital");
        when(paymentComponentView.getDinnerAllowance()).thenReturn(true);
        when(paymentComponentView.getWifiAccessAllowed()).thenReturn(true);
        when(paymentComponentView.getCarParkingAllowed()).thenReturn(true);
        when(view.isPaymentComponentReadyForSubmission()).thenReturn(true);
        when(view.isBusinessCustomerWithNoCardsAllowed()).thenReturn(false);
        when(graphQLBookingDetailsUseCase.bookingConfirmationAndManageBooking(any(), any(), any(),
                any(), any(), any())).thenReturn(Observable.just(bookingWithRooms));
        when(storeBooking.execute(any())).thenReturn(Completable.complete());
        when(clearBookedRecentSearch.execute(any())).thenReturn(Completable.complete());
        when(bookingFlowInput.totalStayPrice(anyBoolean())).thenReturn(totalRatePrice);

        InitiatePaymentDomain initiatePaymentDomain =
                new InitiatePaymentDomain("NOT_REQUIRED", new PaymentRequiredDetailsDomain("", "", ""));
        when(reviewBookingGraphQLUseCase.initiatePayment(any())).thenReturn(Single.just(initiatePaymentDomain));

        FindBookingDomain findBookingDomain = new FindBookingDomain(
                "opera", "booking_reference",
                "uuidbasketreftest", "anyToken", HOTEL_CODE, false);
        when(graphQLFindBookingUseCase.findBooking(any())).
                thenReturn(Single.just(findBookingDomain));
    }

    private BasketStatusRevisedPaymentsDomain mockBasketStatusAsCompleted() {
        String json = "{\n"
                +
                "  \"messages\": [\n"
                +
                "    {\n"
                +
                "      \"order\": 0,\n"
                +
                "      \"seconds\": 10,\n"
                +
                "      \"message\": \"One moment...\"\n"
                +
                "    },\n"
                +
                "    {\n"
                +
                "      \"order\": 1,\n"
                +
                "      \"seconds\": 20,\n"
                +
                "      \"message\": \"Hold tight we're processing your order\"\n"
                +
                "    },\n"
                +
                "    {\n"
                +
                "      \"order\": 2,\n"
                +
                "      \"seconds\": 30,\n"
                +
                "      \"message\": \"Sorry for the delay - please bear with us\"\n"
                +
                "    }\n"
                +
                "  ]\n"
                +
                "}";
        when(getStringResource.invoke(ContentManagedResourceRepository.Key.CONFIRMATION_POLLING_MESSAGES_CONFIG)).thenReturn(json);
        BasketStatusRevisedPaymentsDomain basketStatusRevisedPaymentsDomain =
                GraphQLBasketStatusRevisedPaymentsMapperKt.mapToBasketStatusRevisedPaymentsGQL(revisedPaymentsCompleted);
        when(reviewBookingGraphQLUseCase.getBasketStatusRevisedPayments(any())).thenReturn(Single.just(basketStatusRevisedPaymentsDomain));

        return basketStatusRevisedPaymentsDomain;
    }

    private PaymentDetailsInput mockPaymentDetailsInput() {

        boolean isBookerStaying = false;
        List<GuestDetailsFormDataInput> guestDetailsFormDataInputList = Collections.singletonList(guestDetailsFormDataInput);
        when(parcelablePaymentMethodsDetailsInput.getParcelableBookingConfirmation())
                .thenReturn(new ParcelableBookingConfirmation("GBP", 100f, "bookingRef1234"));

        return PaymentDetailsInput.builder()
                .bookingFlowInput(bookingFlowInput)
                .bookerDetails(guestDetailsFormDataInput)
                .paymentMethodsDetailInput(parcelablePaymentMethodsDetailsInput)
                .guestDetailsList(Collections.emptyList())
                .address(mockBookingAddress())
                .customer(parcelableCustomer)
                .isBookerStaying(isBookerStaying)
                .isBusinessTrip(false)
                .bookerDetails(guestDetailsFormDataInput)
                .guestDetailsList(guestDetailsFormDataInputList)
                .isTaxExempt(false)
                .marketingOptIn(true)
                .build();
    }

    private BookingAddress mockBookingAddress() {
        when(bookingAddress.countryCode()).thenReturn(countryIsoCode);
        when(bookingAddress.legacyCountryCode()).thenReturn(countryLegacyCode);
        when(bookingAddress.city()).thenReturn(city);
        when(bookingAddress.line1()).thenReturn(line1);
        when(bookingAddress.line2()).thenReturn(line2);
        when(bookingAddress.postcode()).thenReturn(postcode);

        return BookingAddress.create(bookingAddress.countryCode(), bookingAddress.legacyCountryCode(),
                bookingAddress.line1(), StringUtils.valueOrDefault(bookingAddress.line2(), EMPTY_STRING), bookingAddress.city(),
                bookingAddress.postcode());
    }

    private void startPresenter() {
        inputRelay.accept(reviewBookingInput);
        presenter.attachView(view);
    }

    private void initialisePresenter() {
        presenter = new ReviewBookingPresenter(
                getStringResource,
                getLongResource,
                isCustomerLoggedIn,
                trackingAnalytics,
                firebaseLogger,
                storeBooking,
                gson,
                messageProvider,
                logService,
                contentManagedResourceRepository,
                storage,
                businessPersistenceManager,
                getCountries,
                clearBookedRecentSearch,
                createCustomer,
                authenticateCustomer,
                deviceLocaleProvider,
                isFeatureOn,
                reviewBookingGraphQLUseCase,
                graphQLBookingDetailsUseCase,
                graphQLFindBookingUseCase,
                configuration,
                viewCompositeDisposable,
                networkCompositeDisposable,
                pollingCompositeDisposable
        );
    }
}
