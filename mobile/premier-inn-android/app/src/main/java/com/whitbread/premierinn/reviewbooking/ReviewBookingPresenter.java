package com.whitbread.premierinn.reviewbooking;

import static com.whitbread.premierinn.bookingdetails.BookingUiModelMapperKt.RESPONSE_SUCCESS;
import static com.whitbread.premierinn.common.Constants.GOOGLE_PAY;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type.BOOKING_FLOW;
import static com.whitbread.premierinn.common.analytics.AnalyticsConstants.Value.EMPLOYEE_RATE_CODE;
import static com.whitbread.premierinn.common.dynatrace.DynatraceAnalyticsConstants.REVENUE_ACTION;
import static com.whitbread.premierinn.common.dynatrace.DynatraceAnalyticsConstants.REVENUE_KEY;
import static com.whitbread.premierinn.common.utils.AppExtensions.buildCreateReservationGuestRequestBody;
import static com.whitbread.premierinn.common.utils.AppExtensions.findPaypalClientTokenIfPresent;
import static com.whitbread.premierinn.common.utils.StringUtils.EMPTY_STRING;
import static com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils.tripTypeLabel;
import static com.whitbread.premierinn.domain.common.Constants.ANDROID_APPS_CHANNEL;
import static com.whitbread.premierinn.domain.common.Constants.BUSINESS_FULL_NAME;
import static com.whitbread.premierinn.domain.common.Constants.LEISURE_FULL_NAME;
import static com.whitbread.premierinn.domain.common.OperaCommonExtensionsKt.SUB_CHANNEL;
import static com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.BOOKING_PRIVACY_FOOTER;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.braintreepayments.api.PayPalAccountNonce;
import com.braintreepayments.api.PayPalClient;
import com.braintreepayments.api.PayPalListener;
import com.braintreepayments.api.UserCanceledException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.jakewharton.rxrelay2.PublishRelay;
import com.whitbread.premierinn.BuildConfig;
import com.whitbread.premierinn.api.ErrorCodesKt;
import com.whitbread.premierinn.api.Urls;
import com.whitbread.premierinn.api.request.booking.Breakfast;
import com.whitbread.premierinn.api.response.AcceptedCreditCard;
import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager;
import com.whitbread.premierinn.common.AppConfiguration;
import com.whitbread.premierinn.common.AsyncResult;
import com.whitbread.premierinn.common.AsyncResultKt;
import com.whitbread.premierinn.common.BookingFlowInput;
import com.whitbread.premierinn.common.PaymentMethodType;
import com.whitbread.premierinn.common.PaymentTimingChoice;
import com.whitbread.premierinn.common.PaymentTimingRule;
import com.whitbread.premierinn.common.RoomBooking;
import com.whitbread.premierinn.common.analytics.FirebaseLogger;
import com.whitbread.premierinn.common.analytics.FirebaseParams;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.appsflyer.AppsFlyerBookingConfirmationParams;
import com.whitbread.premierinn.common.appsflyer.AppsFlyerHelper;
import com.whitbread.premierinn.common.dynatrace.DynatraceHelper;
import com.whitbread.premierinn.common.format.DateFormat;
import com.whitbread.premierinn.common.mapper.CommonMappersKt;
import com.whitbread.premierinn.common.mvp.Presenter;
import com.whitbread.premierinn.common.mvp.PresenterView;
import com.whitbread.premierinn.common.utils.AppExtensions;
import com.whitbread.premierinn.common.utils.CardTypeEnumOpera;
import com.whitbread.premierinn.common.utils.TrackingAnalyticsUtils;
import com.whitbread.premierinn.data.common.DomainMappers;
import com.whitbread.premierinn.data.common.ErrorLogger;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager;
import com.whitbread.premierinn.data.remote.RateContentItem;
import com.whitbread.premierinn.data.remote.graphql.ConfirmationSpinnerMessages;
import com.whitbread.premierinn.domain.authentication.UserType;
import com.whitbread.premierinn.domain.authentication.usecase.AuthenticateCustomer;
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn;
import com.whitbread.premierinn.domain.booking.usecase.StoreBookingMadeOnApp;
import com.whitbread.premierinn.domain.common.Address;
import com.whitbread.premierinn.domain.common.PriceDomain;
import com.whitbread.premierinn.domain.common.RateContentItemDomain;
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn;
import com.whitbread.premierinn.domain.countries.GetCountries;
import com.whitbread.premierinn.domain.countries.entity.CountryDomain;
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences;
import com.whitbread.premierinn.domain.customer.entity.Contact;
import com.whitbread.premierinn.domain.customer.entity.Customer;
import com.whitbread.premierinn.domain.customer.entity.FullName;
import com.whitbread.premierinn.domain.customer.entity.PaymentCard;
import com.whitbread.premierinn.domain.customer.usecase.CreateCustomer;
import com.whitbread.premierinn.domain.graphql.bookingDetails.usecase.GraphQLBookingDetailsUseCase;
import com.whitbread.premierinn.domain.graphql.findBooking.usecase.GraphQLFindBookingUseCase;
import com.whitbread.premierinn.domain.graphql.hdp.entity.ExtrasItemDomain;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Booker;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CancelInformationRequestBody;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationGuestRequestBody;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.DonationsRequestBody;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.FindBookingRequestBody;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.InitiatePaymentRequestBody;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PaymentMethodsRequestBody;
import com.whitbread.premierinn.domain.graphql.requestBodyModels.StayingGuests;
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.InitiatePaymentDomain;
import com.whitbread.premierinn.domain.graphql.reviewBooking.entity.InitiatePaypalPaymentDomain;
import com.whitbread.premierinn.domain.graphql.reviewBooking.usecase.GraphQLReviewBookingUseCase;
import com.whitbread.premierinn.domain.payment.entity.ProviderResponse;
import com.whitbread.premierinn.domain.payment.entity.ThreeCPaymentServiceResponseEntity;
import com.whitbread.premierinn.domain.payment.entity.ThreeCResponse;
import com.whitbread.premierinn.domain.recentsearch.usecase.ClearBookedRecentSearch;
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository;
import com.whitbread.premierinn.domain.resource.usecase.GetLongResource;
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource;
import com.whitbread.premierinn.editguest.EditGuestInput;
import com.whitbread.premierinn.hoteldetails.DonationsInput;
import com.whitbread.premierinn.hoteldetails.SelectedRate;
import com.whitbread.premierinn.paymentbreakdown.PaymentBreakdownInput;
import com.whitbread.premierinn.paymentdetails.PaymentDetailsInput;
import com.whitbread.premierinn.reviewbooking.analytics.BookingConfirmationAnalyticsData;
import com.whitbread.premierinn.reviewbooking.analytics.ReviewBookingAnalyticsData;
import com.whitbread.premierinn.reviewbooking.analytics.ThreeCpPaymentAnalyticData;
import com.whitbread.premierinn.reviewbooking.mappers.PaymentMethodExtensionsGQLKt;
import com.whitbread.premierinn.reviewbooking.mappers.PaymentMethodExtensionsKt;
import com.whitbread.premierinn.reviewbooking.view.DonationView;
import com.whitbread.premierinn.summary.SummaryExtensionsKt;
import com.whitbread.premierinn.summarybreakdown.SummaryBreakdownRoom;
import com.whitbread.premierinn.threeCp.MappersKt;
import com.whitbread.premierinn.threeCp.ThreeCpInput;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ActivityRetainedScoped;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import kotlin.Pair;
import kotlin.Unit;

@ActivityRetainedScoped
public class ReviewBookingPresenter extends Presenter<ReviewBookingPresenter.View> implements PayPalListener {

    static final int MAXIMUM_DINNER_BUDGET_PER_PERSON = 100;
    static final String KEY_IPAGE_POLLING_STATUS_FAILED = "analyticsData.conf.pollingBookingStatusFailed";
    static final String KEY_IPAGE_POLLING_STATUS_PENDING = "analyticsData.conf.pollingBookingStatusPending";
    static final String SERVER_ERROR = "500";
    static final String INTERNAL_SERVER_ERROR = "Internal Server error";
    private final IsCustomerLoggedIn isCustomerLoggedIn;
    private final GetStringResource getStringResource;
    private final GetLongResource getLongResource;
    private final TrackingAnalytics analytics;
    private final FirebaseLogger firebaseLogger;
    private final CompositeDisposable viewCompositeDisposable;
    private final CompositeDisposable networkCompositeDisposable;
    private final CompositeDisposable pollingDisposable;
    private final StoreBookingMadeOnApp storeBooking;
    private final ReviewBookingMessageProvider messageProvider;
    private final ErrorLogger logService;
    private final IsFeatureOn isFeatureOn;
    private final AppConfiguration configuration;
    private final BusinessPersistenceManager businessStorage;
    private ReviewBookingInput reviewBookingInput;
    private PaymentDetailsInput paymentDetailsInput;
    private String cvv = "";
    private PriceDomain totalPrice;
    private PriceDomain balanceOutstanding;
    private PriceDomain donationPrice = PriceDomain.Companion.createDefault();
    private PaymentTimingChoice paymentTimingChoice;
    private boolean alcoholSelected;
    private boolean carParkingSelected;
    private boolean wifiAccessSelected;
    private boolean dinnerAllowance;
    private boolean cnpRequired = false;
    private PriceDomain dinnerBudget = PriceDomain.Companion.createDefault();
    private Gson gson;
    private ContentManagedResourceRepository contentManagedResourceRepository;
    private Observable<ReviewBookingInput> inputMutatedObservable;
    final AtomicBoolean isCustomerLoggedInState = new AtomicBoolean(false);
    final SimplePersistenceManager storage;
    private final GetCountries getCountries;
    private ClearBookedRecentSearch clearBookedRecentSearch;
    private CreateCustomer createCustomer;
    private AuthenticateCustomer authenticateCustomer;
    private Boolean shouldCreateAccount = false;
    private String createAccountPassword;
    private Boolean savePaymentDetails = false;
    private GraphQLReviewBookingUseCase graphQLReviewBookingUseCase;
    private GraphQLBookingDetailsUseCase graphQLBookingDetailsUseCase;
    private final GraphQLFindBookingUseCase findBookingUsecase;
    private Boolean isBusinessCard = false;
    private Boolean isStoredCard = false;
    private Boolean isGooglePaySelected = false;
    private String selectedPaymentCardType = EMPTY_STRING;
    private List<RateContentItemDomain> rateContentItemDomain;
    private List<CountryDomain> countries;
    private DeviceLocaleProvider deviceLocaleProvider;
    private String donationPledgeString;
    private String sessionId;
    private String templateId;
    private boolean failedPollingDueToApiDown = false;
    private String token = EMPTY_STRING;
    private long attemptsMade;
    private long timerStart;
    private String uuidBasketReference;
    private boolean featureGooglePay;
    private String paypalNonce;
    private final PublishSubject<String> paypalNonceChange = PublishSubject.create();
    private final PublishSubject<Integer> onPaypalError = PublishSubject.create();
    private PayPalClient payPalClient;
    private boolean usePaypalInitiatePayment;
    private boolean featurePaypal;
    private boolean isPaymentOutage = false;
    private boolean isInnBusinessUser = false;
    private String selectedSavedCardHolderName = EMPTY_STRING;
    private boolean shouldShowDonation;

    @Inject
    public ReviewBookingPresenter(@NonNull GetStringResource getStringResource,
                                  @NonNull GetLongResource getLongResource,
                                  @NonNull IsCustomerLoggedIn isCustomerLoggedIn,
                                  @NonNull TrackingAnalytics trackingAnalytics,
                                  @NonNull FirebaseLogger firebaseLogger,
                                  @NonNull StoreBookingMadeOnApp storeBooking,
                                  @NonNull Gson gson,
                                  @NonNull ReviewBookingMessageProvider messageProvider,
                                  @NonNull ErrorLogger logService,
                                  @NonNull ContentManagedResourceRepository contentManagedResourceRepository,
                                  @NonNull SimplePersistenceManager storage,
                                  @NonNull BusinessPersistenceManager businessPersistenceManager,
                                  @NonNull GetCountries getCountries,
                                  @NonNull ClearBookedRecentSearch clearBookedRecentSearch,
                                  @NonNull CreateCustomer createCustomer,
                                  @NonNull AuthenticateCustomer authenticateCustomer,
                                  @NonNull DeviceLocaleProvider deviceLocaleProvider,
                                  @NonNull IsFeatureOn isFeatureOn,
                                  @NonNull GraphQLReviewBookingUseCase reviewBookingGraphQLUseCase,
                                  @NonNull GraphQLBookingDetailsUseCase graphQLBookingDetailsUseCase,
                                  @NonNull GraphQLFindBookingUseCase findBookingUseCase,
                                  @NonNull AppConfiguration appConfiguration,
                                  @NonNull CompositeDisposable viewCompositeDisposable,
                                  @NonNull CompositeDisposable networkCompositeDisposable,
                                  @NonNull CompositeDisposable pollingDisposable) {
        this.getStringResource = getStringResource;
        this.getLongResource = getLongResource;
        this.isCustomerLoggedIn = isCustomerLoggedIn;
        this.analytics = trackingAnalytics;
        this.firebaseLogger = firebaseLogger;
        this.storeBooking = storeBooking;
        this.gson = gson;
        this.messageProvider = messageProvider;
        this.logService = logService;
        this.contentManagedResourceRepository = contentManagedResourceRepository;
        this.storage = storage;
        this.businessStorage = businessPersistenceManager;
        this.getCountries = getCountries;
        this.clearBookedRecentSearch = clearBookedRecentSearch;
        this.createCustomer = createCustomer;
        this.authenticateCustomer = authenticateCustomer;
        this.graphQLReviewBookingUseCase = reviewBookingGraphQLUseCase;
        this.graphQLBookingDetailsUseCase = graphQLBookingDetailsUseCase;
        this.findBookingUsecase = findBookingUseCase;
        this.deviceLocaleProvider = deviceLocaleProvider;
        this.isFeatureOn = isFeatureOn;
        this.configuration = appConfiguration;
        this.viewCompositeDisposable = viewCompositeDisposable;
        this.networkCompositeDisposable = networkCompositeDisposable;
        this.pollingDisposable = pollingDisposable;
    }

    @Override
    public void onAttachView(View view) {
        retrieveRateDescriptionFromFirebase();
        this.countries = getCountries.fetchCountriesFromSharedPref();

        usePaypalInitiatePayment = isFeatureOn.invoke(ContentManagedResourceRepository.Key.USE_PAYPAL_INITIATE_PAYMENT_MUTATION);

        featurePaypal = isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_PAYPAL);

        featureGooglePay = isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_GOOGLE_PAY);
        shouldShowDonation = isFeatureOn.invoke(ContentManagedResourceRepository.Key.FEATURE_DONATION);
        donationPledgeString = shouldShowDonation ? getStringResource.invoke(ContentManagedResourceRepository.Key.DONATION_PLEDGE)
                : EMPTY_STRING;
        viewCompositeDisposable.add(AsyncResultKt.mapToAsyncResult(isCustomerLoggedIn.invoke().toObservable())
                .subscribe(result -> isCustomerLoggedInState.set(
                        result instanceof AsyncResult.Success && ((AsyncResult.Success<Boolean>) result).getData())
                )
        );
        Observable<ReviewBookingInput> updateWithGuestDetails = view.onGuestDetailsUpdated().map(this::toReviewBookingInput);

        inputMutatedObservable = Observable.merge(view.onInputUpdated(), updateWithGuestDetails).doOnNext(this::populateMutations);

        viewCompositeDisposable.add(inputMutatedObservable
                .subscribe(input -> {
                    uuidBasketReference = reviewBookingInput.uuidBasketReference();
                    isInnBusinessUser = input.isBusinessUser() != null ? input.isBusinessUser() : false;
                    if (isInnBusinessUser) {
                        view.showLoadingSpinner(true, false, EMPTY_STRING);
                        createReservationGuestForBB(view);
                    } else {
                        view.showLoadingSpinner(true, false, EMPTY_STRING);
                        makePaymentMethodsAndDonationsAndBookingConfirmationCall(view);
                    }
                }));

        view.showPrivacyPolicy(getStringResource.invoke(BOOKING_PRIVACY_FOOTER));
        logEnteredFinalBookingScreen();

        viewCompositeDisposable.add(inputMutatedObservable
                .subscribe(input -> {
                    createAccountPassword = input.paymentDetailsInput().accountPassword();
                    savePaymentDetails = input.paymentDetailsInput().savePaymentDetails();
                    shouldCreateAccount = input.paymentDetailsInput().shouldCreateAccount();
                }));


        viewCompositeDisposable.add(inputMutatedObservable
                .subscribe(input -> {
                                observePaymentSelectionChanged(view);
                                observePaymentTimingSelectionChange(view);
                        }
                ));

        viewCompositeDisposable.add(inputMutatedObservable
                .map(input -> {
                    Float fee = 0f;
                        ReviewBookModel reviewBookModel = new ReviewBookModel(input, fee, donationPrice,
                                totalPrice, deviceLocaleProvider, donationPledgeString);
                        return reviewBookModel;

                })
                .subscribe(model -> {
                    displayUiComponents(view, model);
                }));

        viewCompositeDisposable.add(inputMutatedObservable
                .subscribe(input -> {
                    view.setGuestDetails(input);
                    if (input.additionalInformation() != null) {
                        view.setAdditionalInformation(input.additionalInformation());
                    }
                    setDetailsFormat(view);

                    view.setOtherExtras(CommonMappersKt.toExtrasItemDomain(
                            paymentDetailsInput.bookingFlowInput().selectedExtras(), deviceLocaleProvider),
                            deviceLocaleProvider);

                    displayUpsellInfo(view, input);

                    displayBookingRules(view, input);
                    view.showPriceIncludesTaxesAndFeesMessage(true);
                }));

        viewCompositeDisposable.add(view.onPayPalButtonClick()
                .subscribe(__ -> {
                    view.myTokenizePayPalAccountWithVaultMethod();
                    view.collectDeviceDataForPayPal();
                    view.showPaypalButtonLoading(true);
                }));

        viewCompositeDisposable.add(observePayPalNonceChange()
                .subscribe(__ -> {
                    initiatePayPalPayment(view);
                    view.showPaypalButtonLoading(false);
                    view.showLoadingSpinner(true, true, "");
                }));

        viewCompositeDisposable.add(observeOnPaypalError()
                .subscribe(onPaypalError -> {
                    if (onPaypalError.equals(0)) {
                        view.showPaymentError();
                    }
                    view.showPaypalButtonLoading(false);
                }));


        observeDonationButtonClicks(view);

        observePaymentBreakdownClick(view);

        observeGuestDetailsEditClick(view);

        onAdditionalInformationEditClick(view);

        observeCardVerificationSuccessOpera(view);

        observeConfirmBookingCLick(view);

        observePostCodeEntryChange(view);

        logAnalytics();

        displayInfoBoxForBBAllowancesIfApplicable(view);
    }

    private void displayUiComponents(View view, ReviewBookModel model) {
        String promotionCode = paymentDetailsInput.bookingFlowInput().promotionCode();
        String promoTag = promotionCode != null ? paymentDetailsInput.bookingFlowInput().promotionTag() : null;
        view.display(model, null,
                isInnBusinessUser,
                paymentDetailsInput.bookingFlowInput().numNights(),
                paymentDetailsInput.bookingFlowInput().chosenRate().rateName(),
                paymentDetailsInput.bookingFlowInput().isHub(),
                paymentDetailsInput.bookingFlowInput().formattedBaseRate(),
                promotionCode,
                promoTag,
                viewCompositeDisposable, countries, featurePaypal, featureGooglePay);
    }

    private void showOrHideDonations(View view) {
        if (shouldShowDonation) {
            if (reviewBookingInput.operaDonations() != null) {
                if (!reviewBookingInput.operaDonations().getDonationPackages().isEmpty()) {
                    String donationTitle = getStringResource.invoke(ContentManagedResourceRepository.Key.DONATION_TITLE_OPERA);

                    DonationsInput donationsInput =
                            com.whitbread.premierinn.reviewbooking.mappers.ExtensionsKt
                                    .getMaxAndMinDonationsInput(reviewBookingInput.operaDonations().getDonationPackages());
                    view.setUpDonations(donationsInput);

                    view.setDonationInfo(donationTitle,
                            Urls.CONTENT_BASE_URL + reviewBookingInput.operaDonations().getImageSrc(),
                            reviewBookingInput.operaDonations().getDescription());
                    view.setDonationOptionVisible(true);
                } else {
                    view.setDonationOptionVisible(false);
                }
            } else {
                view.setDonationOptionVisible(false);
            }
        } else {
            view.setDonationOptionVisible(false);
        }
    }

    private void displayBookingRules(View view, ReviewBookingInput input) {
        SelectedRate rate = input.paymentDetailsInput().bookingFlowInput().chosenRate();
        view.showAmendAndCancellationMsg(rate.description());
    }

    private void retrieveRateDescriptionFromFirebase() {
        viewCompositeDisposable.add(contentManagedResourceRepository
                .getStringSingle(ContentManagedResourceRepository.Key.RATE_CONTENT.getValue())
                .map(it -> new Gson().fromJson(it, RateContentItem[].class))
                .map(Arrays::asList)
                .toObservable().flatMapIterable(rateContent -> rateContent)
                .map(DomainMappers::toRateContentInfoDomain)
                .toList()
                .subscribe(it -> rateContentItemDomain = it,
                        onError -> logService.logException(onError, "Retrieval of rate info from firebase failed")));
    }
    private void displayUpsellInfo(View view, ReviewBookingInput input) {
        List<UpsellItem> selectedUpsellItems = input.paymentDetailsInput().bookingFlowInput().selectedUpsellItems();

        if (!selectedUpsellItems.isEmpty()) {
            displayUpsellItem(view, selectedUpsellItems, input);
        }
    }

    private void observeDonationButtonClicks(View view) {
        viewCompositeDisposable.add(view.donationButtonClicks()
                .subscribe(goshDonation -> updateDonation(goshDonation.getDonationAmount(), view)));
    }

    private void observePaymentBreakdownClick(View view) {
        viewCompositeDisposable.add(view.onPaymentBreakdownClick()
                .map(input -> {
                            BookingFlowInput bookingFlowInput = reviewBookingInput.paymentDetailsInput().bookingFlowInput();
                            List<SummaryBreakdownRoom> summaryBreakdownRooms =
                                    SummaryBreakdownRoom.createSummaryBreakdownRooms(bookingFlowInput.roomBookings(),
                                            bookingFlowInput.accessibleRoomBookings(),
                                            bookingFlowInput.twinRoomBookings(),
                                            bookingFlowInput.arrivalDate(),
                                            reviewBookingInput.paymentDetailsInput().isTaxExempt(),
                                            deviceLocaleProvider);
                            int totalChildrenBreakfast = paymentDetailsInput.bookingFlowInput().breakfasts()
                                    .stream().mapToInt(Breakfast::children).sum();
                            return PaymentBreakdownInput.builder()
                                    .isHub(bookingFlowInput.isHub())
                                    .hotelBrand(bookingFlowInput.hotelBrand())
                                    .totalGuests(bookingFlowInput.numGuests())
                                    .totalAdults(bookingFlowInput.numAdults())
                                    .totalChildren(bookingFlowInput.numChildren())
                                    .totalNights(bookingFlowInput.numNights())
                                    .arrivalDate(bookingFlowInput.arrivalDate())
                                    .summaryBreakdownRooms(summaryBreakdownRooms)
                                    .totalPrice(CommonMappersKt.toBookingPrice(totalPrice))
                                    .chosenRateName(bookingFlowInput.chosenRate().rateName())
                                    .chosenRateCode(bookingFlowInput.chosenRate().code())
                                    .chosenRateDescription(bookingFlowInput.chosenRate().description())
                                    .isFreeForChildren(isAvailableForChildren())
                                    .isEmployeeRateSelected(bookingFlowInput.isEmployeeRateSelected())
                                    .selectedUpsells(bookingFlowInput.selectedUpsellItems())
                                    .selectedExtras(bookingFlowInput.selectedExtras())
                                    .donation(CommonMappersKt.toBookingPrice(donationPrice))
                                    .totalChildrenBreakfast(totalChildrenBreakfast)
                                    .build();
                        }
                )
                .subscribe(view::startPaymentBreakdownActivity));
    }

    private void observeGuestDetailsEditClick(View view) {
        viewCompositeDisposable.add(view.onGuestDetailsEditClick()
                .subscribe(__ -> view.startEditGuestActivity(EditGuestInput.create(paymentDetailsInput.guestDetailsList(),
                        paymentDetailsInput.bookerDetails(), paymentDetailsInput.isBookerStaying()))));
    }

    private void onAdditionalInformationEditClick(View view) {
        // TODO: When Additional Information Screen is completed
//        viewCompositeDisposable.add(view.onAdditionalInformationEditClick()
//                .subscribe(__ -> view.startEditGuestActivity(EditGuestInput.create(paymentDetailsInput.guestDetailsList(),
//                        paymentDetailsInput.bookerDetails(), paymentDetailsInput.isBookerStaying()))));
    }

    private void observeCardVerificationSuccessOpera(View view) {
        viewCompositeDisposable.add(view.onCardVerificationOkOpera()
                .subscribe(confirmation -> this.makeBookingOpera(view)));
    }

    private void observePostCodeEntryChange(View view) {
        viewCompositeDisposable.add(view.onPaymentAddressPostcodeFinderEntry()
                .subscribe(parcelableAddress -> {
                            view.setPaymentAddressFields(parcelableAddress);
                        }
                ));
    }

    private void initiatePayPalPayment(View view) {
        networkCompositeDisposable.add(
                (usePaypalInitiatePayment
                        ? graphQLReviewBookingUseCase.initiatePaypalPayment(createInitiatePaymentRequestBody(view))
                        : graphQLReviewBookingUseCase.initiatePayment(createInitiatePaymentRequestBody(view)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onSuccess -> {
                            if (onSuccess instanceof InitiatePaypalPaymentDomain) {
                                if (((InitiatePaypalPaymentDomain) onSuccess).getStatus().equals("NOT_REQUIRED")) {
                                    paypalPaymentPolling(view);
                                } else {
                                    view.showLoadingSpinner(false, false, "");
                                    view.showPaymentError();
                                }
                            } else if (onSuccess instanceof InitiatePaymentDomain) {
                                if (((InitiatePaymentDomain) onSuccess).getStatus().equals("NOT_REQUIRED")) {
                                    paypalPaymentPolling(view);
                                } else {
                                    view.showLoadingSpinner(false, false, "");
                                    view.showPaymentError();
                                }
                            }
                            }, throwable -> {
                                view.showLoadingSpinner(false, false, "");
                                view.showPaymentError();
                        })
        );

    }

    private void paypalPaymentPolling(View view) {
        reviewBookingInput = reviewBookingInput.toBuilder()
                .cvv(cvv)
                .donation(CommonMappersKt.toParcelablePrice(donationPrice))
                .businessBookingOptions(isSelectedPaymentABusinessCard(reviewBookingInput)
                        ? createBusinessBookingOptions(view) : null)
                .build();

        String surname = reviewBookingInput.paymentDetailsInput().bookerDetails().lastName();
        BookingFlowInput bookingFlowInput = reviewBookingInput.paymentDetailsInput().bookingFlowInput();
        startPollingForBasketStatusAndThenBookingConfirmation(surname, bookingFlowInput, view);
    }

    private void observeConfirmBookingCLick(View view) {

            networkCompositeDisposable.add(
                    view.onConfirmBookingClick().subscribe(onClick -> {
                        view.showLoading(true);
                        if (view.isPaymentComponentReadyForSubmission() && !view.isBusinessCustomerWithNoCardsAllowed()) {
                        networkCompositeDisposable.add(
                                graphQLReviewBookingUseCase.initiatePayment(createInitiatePaymentRequestBody(view))
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(onSuccess -> {
                                            if (Objects.equals(onSuccess.getStatus(), "NOT_REQUIRED")) {
                                                makeReservationWithoutCard(view);

                                            } else if (onSuccess.getPaymentRequiredDetails() != null
                                                    && onSuccess.getPaymentRequiredDetails().getIPageHtml() != null) {
                                                view.showLoading(false);
                                                ThreeCPaymentServiceResponseEntity threeCResponse =
                                                        new ThreeCPaymentServiceResponseEntity(
                                                                reviewBookingInput
                                                                        .paymentDetailsInput()
                                                                        .bookingFlowInput()
                                                                        .basketReference(),
                                                                new ProviderResponse(new ThreeCResponse(
                                                                        reviewBookingInput.paymentDetailsInput()
                                                                                .bookingFlowInput()
                                                                                .basketReference(), EMPTY_STRING,
                                                                        onSuccess.getPaymentRequiredDetails().getIPageHtml(),
                                                                        onSuccess.getPaymentRequiredDetails().getSessionId(),
                                                                        onSuccess.getPaymentRequiredDetails().getProviderUrl()
                                                                )), false);

                                                view.loadThreeCpIPage(MappersKt.toThreeCpInput(threeCResponse,
                                                                reviewBookingInput,
                                                                paymentDetailsInput.bookingFlowInput(),
                                                                isCustomerLoggedInState.get(),
                                                                isInnBusinessUser),
                                                        selectedPaymentCardType,
                                                        view.getPaymentComponent().getPaymentTimingSelection());
                                            } else {
                                                view.showPaymentError();
                                            }
                                        }, throwable -> {
                                            view.showPaymentError();
                                        }));
                        } else {
                            view.showPaymentComponentValidationError();
                            view.showLoading(false);
                        }
                    }));
    }

    private void observePaymentSelectionChanged(View view) {
        viewCompositeDisposable.add(view.onPaymentTypeSelectionChanged()
                .subscribe(paymentType -> {
                    view.togglePayPalButton(!paymentType.getPaymentOptionForPayPal().isEmpty());
                    view.setThreeCpConfirmButtonText(paymentType, shouldCreateAccount);
                    SelectedPaymentDetailsInput input = paymentType.getPaymentCardDetailsInput();
                    selectedPaymentCardType = input != null ? input.getCardType() : EMPTY_STRING;
                    isBusinessCard = input != null
                            && (Objects.requireNonNull(paymentType.getPaymentCardDetailsInput()).isSavedBusinessAccountCardOpera()
                            || Objects.requireNonNull(paymentType.getPaymentCardDetailsInput()).isSavedBusinessAccountCard());

                        if (paymentType.getTag().equals(PaymentTimingRule.RESERVE_WITHOUT_CARD.name().toLowerCase())) {
                            selectedPaymentCardType = paymentType.getTag().toString().toUpperCase();
                            view.setConfirmButtonText(false);
                        }
                        isGooglePaySelected = paymentType.isGooglePaySelected();
                        isStoredCard = paymentType.getTag().toString().toUpperCase().equals(PaymentMethodType.SAVED_CARD.name());
                        if (isStoredCard) {
                            selectedSavedCardHolderName = paymentType.getPaymentCardDetailsInput().getCardHolder();
                        }

                    // Update cnpRequired based on selected card
                    List<ParcelablePaymentMethod> paymentMethods = Objects.requireNonNull(paymentDetailsInput.paymentMethodsDetailInput())
                            .getParcelablePaymentMethods();
                    cnpRequired = false;
                    for (ParcelablePaymentMethod paymentMethod : paymentMethods) {
                        ParcelableCard card = paymentMethod.getParcelableCard();
                        if (card != null && selectedPaymentCardType.equalsIgnoreCase(card.getType())) {
                            cnpRequired = card.getCnpRequired();
                        }
                    }
                    displayInfoBoxForBBAllowancesIfApplicable(view);
                })
        );
    }

    private void observePaymentTimingSelectionChange(View view) {
        viewCompositeDisposable.add(view.onPaymentTimingSelectionChange()
        .subscribe(paymentTimingChoice -> {
            this.paymentTimingChoice = paymentTimingChoice;
            view.setPayNowInTotalPriceViewMessage(paymentTimingChoice == PaymentTimingChoice.PAY_NOW);
        }));
    }

    private InitiatePaymentRequestBody createInitiatePaymentRequestBody(View view) {
        String envt = configuration.getGraphQLUrl().replace("api", "www");

        dinnerAllowance = view.getPaymentComponent().getDinnerAllowance() && isSelectedPaymentABusinessCard(reviewBookingInput);
        alcoholSelected = view.getPaymentComponent().getAlcoholAllowed();
        wifiAccessSelected = view.getPaymentComponent().getWifiAccessAllowed();
        carParkingSelected = view.getPaymentComponent().getCarParkingAllowed();

        if (isInnBusinessUser && cnpRequired) {
            if (reviewBookingInput.paymentDetailsInput().bookingFlowInput().dinnerBudget() > 0) {
                dinnerBudget = new PriceDomain(Float.parseFloat(
                        String.valueOf(reviewBookingInput.paymentDetailsInput().bookingFlowInput().dinnerBudget())),
                        reviewBookingInput.paymentDetailsInput().bookingFlowInput().totalStayPrice(
                                paymentDetailsInput.isBusinessTrip()).getCurrency());
                dinnerAllowance = true;
                alcoholSelected = storage.getCustomer().getCompany().getRequestedCompany().getBookingAllowances().getAllowAlcohol();
                wifiAccessSelected = Boolean.TRUE.equals(reviewBookingInput.isWifiAvailable());
                carParkingSelected = storage.getCustomer().getCompany().getRequestedCompany().getBookingAllowances().getAllowCarParking();
            }

        } else if (dinnerAllowance) {
            dinnerBudget = new PriceDomain(Float.parseFloat(view.getPaymentComponent().getDinnerBudget()),
                    reviewBookingInput.paymentDetailsInput().bookingFlowInput().totalStayPrice(
                            paymentDetailsInput.isBusinessTrip()).getCurrency());
        }

        boolean cnpSelected = view.getPaymentComponent().getCnpAuth();
        String purchaseOrderNumber =
                isSelectedPaymentABusinessCard(reviewBookingInput) ? view.getPaymentComponent().getPurchaseOrderNumber() : EMPTY_STRING;
        String customerReferenceNumber =
                isSelectedPaymentABusinessCard(reviewBookingInput) ? view.getPaymentComponent().getCustomerReferenceNumber() : EMPTY_STRING;

        return com.whitbread.premierinn.reviewbooking.mappers.ExtensionsKt.toGraphQLPaymentDetails(
                reviewBookingInput, deviceLocaleProvider.getDeviceLanguage(),
                envt, view.getPaymentComponent().billingAddress(),
                view.getPaymentComponent().getPaymentTimingSelection(),
                isBusinessCard, dinnerAllowance, alcoholSelected,
                wifiAccessSelected, carParkingSelected, dinnerBudget.getAmount(),
                cnpSelected, reviewBookingInput.selectedCharityPackageCode(),
                paypalNonce, view.getDeviceDataForPayPal(),
                purchaseOrderNumber,
                customerReferenceNumber,
                isGooglePaySelected,
                reviewBookingInput.paymentDetailsInput().paymentMethodsDetailInput(),
                isStoredCard, selectedSavedCardHolderName, isInnBusinessUser, isCustomerLoggedInState.get()
        );
    }

    private void logAnalytics() {
        if (reviewBookingInput == null) {
            return;
        }

        final BookingFlowInput bookingFlowInput = Objects.requireNonNull(reviewBookingInput.paymentDetailsInput()).bookingFlowInput();

        int numberOfRooms;
        String lettingCode;
        if (bookingFlowInput.roomBookings().size() > 0) {
        numberOfRooms = bookingFlowInput.roomBookings().size();
        lettingCode = bookingFlowInput.roomBookings().get(0).getLettingType().getCode();
        } else if (bookingFlowInput.accessibleRoomBookings().size() > 0) {
            numberOfRooms = bookingFlowInput.accessibleRoomBookings().size();
            lettingCode = bookingFlowInput.accessibleRoomBookings().get(0).getLettingType().getCode();
        } else {
            numberOfRooms = bookingFlowInput.twinRoomBookings().size();
            lettingCode = bookingFlowInput.twinRoomBookings().get(0).getLettingType().getCode();
        }

        final ReviewBookingAnalyticsData data = ReviewBookingAnalyticsData.builder()
                .hotelCode(bookingFlowInput.hotelCode())
                .selectedRate(bookingFlowInput.chosenRate())
                .nights(bookingFlowInput.numNights())
                .rooms(numberOfRooms)
                .adults(bookingFlowInput.numAdults())
                .children(bookingFlowInput.numGuests() - bookingFlowInput.numAdults())
                .checkInDate(bookingFlowInput.arrivalDate())
                .selectedExtras(new Pair<>(bookingFlowInput.selectedUpsellItems(),
                        paymentDetailsInput.bookingFlowInput().selectedExtras()))

                .screenType(BOOKING_FLOW)
                .lettingType(lettingCode)
                .roomPrice(bookingFlowInput.totalRoomsCost(false))
                .selectedRooms(
                        Stream.of(bookingFlowInput.roomBookings(),
                                        bookingFlowInput.accessibleRoomBookings(),
                                        bookingFlowInput.twinRoomBookings())
                                .filter(Objects::nonNull)
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList()))
                .businessUser(isInnBusinessUser)
                .isEmployeeRateSelected(bookingFlowInput.isEmployeeRateSelected())
                .promoCode(bookingFlowInput.promotionCode())
                .promoName(bookingFlowInput.promotionTag())
                .rateTag(bookingFlowInput.rateTag())
                .build();

        analytics.track(ScreenState.REVIEW_BOOKING, data);
    }

    private void track3CPPayment(View view, String errorCode, String errorMsg) {
        List<ParcelablePaymentMethod> parcelablePaymentMethods = Objects
                .requireNonNull(Objects.requireNonNull(reviewBookingInput.paymentDetailsInput())
                .paymentMethodsDetailInput()).getParcelablePaymentMethods();
        int cards = 0;

        List<String> cardTypes = new ArrayList<>();
        if (isCustomerLoggedInState.get() && isStoredCard) {
            try {
                ParcelablePaymentMethod savedCard = PaymentMethodExtensionsKt.getSavedCard(parcelablePaymentMethods);
                cardTypes.add(Objects.requireNonNull(savedCard.getParcelableCard()).getCardType());
                cards = 1;

            } catch (NullPointerException exception) {
                logService.logException(exception, exception.getMessage());
            }
        }

        ThreeCpPaymentAnalyticData analyticData;

            if (Objects.equals(selectedPaymentCardType, CardTypeEnumOpera.PP.name())) {
                analyticData = new ThreeCpPaymentAnalyticData(paymentDetailsInput.bookingFlowInput().hotelCode(),
                        reviewBookingInput.guestHistoryNumber(),
                        BuildConfig.FLAVOR,
                        isCustomerLoggedInState.get(),
                        BOOKING_FLOW,
                        isInnBusinessUser,
                        EMPTY_STRING, EMPTY_STRING, cards, cardTypes, selectedPaymentCardType,
                        view.getPaymentComponent().getPaymentTimingSelection(), errorCode,
                        errorMsg, false, PaymentMethodType.PAYPAL.name().toLowerCase());
            } else if (isGooglePaySelected) {
                analyticData = new ThreeCpPaymentAnalyticData(paymentDetailsInput.bookingFlowInput().hotelCode(),
                        reviewBookingInput.guestHistoryNumber(),
                        BuildConfig.FLAVOR,
                        isCustomerLoggedInState.get(),
                        BOOKING_FLOW,
                        isInnBusinessUser,
                        EMPTY_STRING, EMPTY_STRING, cards, cardTypes, selectedPaymentCardType,
                        view.getPaymentComponent().getPaymentTimingSelection(), errorCode, errorMsg, false, GOOGLE_PAY);
            } else {
                analyticData = new ThreeCpPaymentAnalyticData(paymentDetailsInput.bookingFlowInput().hotelCode(),
                        reviewBookingInput.guestHistoryNumber(),
                        BuildConfig.FLAVOR,
                        isCustomerLoggedInState.get(),
                        BOOKING_FLOW,
                        isInnBusinessUser,
                        EMPTY_STRING, EMPTY_STRING, cards, cardTypes, selectedPaymentCardType,
                        view.getPaymentComponent().getPaymentTimingSelection(), errorCode, errorMsg, false, EMPTY_STRING, isPaymentOutage);
            }
        analytics.trackAction(ScreenState.PAYMENT_DETAILS_3CP, analyticData);
    }

    private void track3CPPaymentForPolling(View view) {
        List<ParcelablePaymentMethod> parcelablePaymentMethods = Objects
                .requireNonNull(Objects.requireNonNull(reviewBookingInput.paymentDetailsInput())
                        .paymentMethodsDetailInput()).getParcelablePaymentMethods();
        int cards = 0;

        List<String> cardTypes = new ArrayList<>();
        if (isCustomerLoggedInState.get() && isStoredCard) {
            try {
                ParcelablePaymentMethod savedCard = PaymentMethodExtensionsKt.getSavedCard(parcelablePaymentMethods);
                cardTypes.add(Objects.requireNonNull(savedCard.getParcelableCard()).getCardType());
                cards = 1;

            } catch (NullPointerException exception) {
                logService.logException(exception, exception.getMessage());
            }
        }

        ThreeCpPaymentAnalyticData analyticData =
                new ThreeCpPaymentAnalyticData(paymentDetailsInput.bookingFlowInput().hotelCode(),
                        reviewBookingInput.guestHistoryNumber(),
                        BuildConfig.FLAVOR,
                        isCustomerLoggedInState.get(),
                        BOOKING_FLOW,
                        isInnBusinessUser,
                        sessionId, templateId, cards, cardTypes, selectedPaymentCardType,
                        view.getPaymentComponent().getPaymentTimingSelection(),
                        null, null, true);
            analytics.trackAction(ScreenState.BOOKING_CONFIRMATION, analyticData);

    }

    private Boolean isAvailableForChildren() {
        UpsellItem upsellItem = reviewBookingInput.paymentDetailsInput().bookingFlowInput().selectedUpsellItem();
        if (upsellItem != null) {
            return upsellItem.isFreeForChildren();
        }

        return false;
    }

    private float calculateUpsellCost(@Nullable UpsellItem upsellItem) {
        if (upsellItem == null) {
            return 0f;
        }

        int numAdults = reviewBookingInput.paymentDetailsInput().bookingFlowInput().numAdults();
        int numNights = reviewBookingInput.paymentDetailsInput().bookingFlowInput().numNights();
        return upsellItem.calculateTotalUpsellCostForStay(numAdults, numNights);
    }

    private void displayUpsellItem(@NonNull View view, @NonNull List<UpsellItem> selectedUpsellItems,
                                   ReviewBookingInput input) {

        int numAdults = reviewBookingInput.paymentDetailsInput().bookingFlowInput().numAdults();
        int numGuests = reviewBookingInput.paymentDetailsInput().bookingFlowInput().numGuests();
        int numChildren = numGuests - numAdults;
        int numNights = reviewBookingInput.paymentDetailsInput().bookingFlowInput().numNights();

        view.showBreakfastExpenseWithOnlyAdultsPaying(deviceLocaleProvider, selectedUpsellItems, numNights);

        if (selectedUpsellItems.stream().anyMatch(UpsellItem::isFreeForChildren)
                && numChildren > 0) {
            view.showChildrenBreakfastExpense(input, numNights);
        }
    }

    @Override
    public void onDetachView() {
        viewCompositeDisposable.clear();
        networkCompositeDisposable.clear();
        pollingDisposable.clear();
    }

    @Override
    public void onDestroy() {
        if (!viewCompositeDisposable.isDisposed()) {
            viewCompositeDisposable.dispose();
        }
        if (!networkCompositeDisposable.isDisposed()) {
            networkCompositeDisposable.dispose();
        }
        if (!pollingDisposable.isDisposed()) {
            pollingDisposable.dispose();
        }
    }

    private void setDetailsFormat(View view) {
        boolean isBookerStaying = reviewBookingInput.paymentDetailsInput().isBookerStaying();
        boolean isBookerOnlyGuest = (reviewBookingInput.paymentDetailsInput().guestDetailsList().size() == 1 && isBookerStaying);

        // The default is to display format where booker is not a guest
        if (isBookerStaying) {
            if (isBookerOnlyGuest) {
                if (isInnBusinessUser) {
                    view.hideLeadGuest();
                } else {
                    view.showBookerAsOnlyGuest();
                }
            } else {
                view.showBookerAsOneOfManyGuests();
            }
        }
    }

    private void logEnteredFinalBookingScreen() {
        // Even though this event is called 'Add Payment Info', it's discussed we want
        // to use this event when the user enters the final screen of booking flow
        firebaseLogger.logEvent(FirebaseAnalytics.Event.ADD_PAYMENT_INFO);
    }

    private void createReservationGuestForBB(View view) {
        Customer customer = storage.getCustomer();
        Address address = customer.getAddress();
        String hotelCode = reviewBookingInput.paymentDetailsInput().bookingFlowInput().hotelCode();
        Booker bookerDetails = AppExtensions.bookerDetails(customer, address, businessStorage.getBusinessCustomerEmail());
        List<StayingGuests> stayingGuests = AppExtensions.stayingGuests(customer);

        CreateReservationGuestRequestBody createReservationGuestRequestBody = buildCreateReservationGuestRequestBody(
                bookerDetails, stayingGuests, uuidBasketReference, hotelCode);

        networkCompositeDisposable.add(graphQLReviewBookingUseCase.createReservationGuest(
                        createReservationGuestRequestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess -> {
                        makePaymentMethodsAndDonationsAndBookingConfirmationCall(view);
                }, onFailure -> {
                    view.showLoadingSpinner(false, false, EMPTY_STRING);
                    view.showMessageandTakeUserBackToPreviousScreen();
                }));
    }

    private void makePaymentMethodsAndDonationsAndBookingConfirmationCall(View view) {
        BookingFlowInput bookingFlowInput = Objects.requireNonNull(reviewBookingInput.paymentDetailsInput()).bookingFlowInput();
        ParcelableDonationsDomain parcelableDonationsDomain = null;
        DonationsRequestBody donationsRequestBody = null;
        if (!isInnBusinessUser) {
            donationsRequestBody = new DonationsRequestBody(
                    bookingFlowInput.hotelCode(),
                    deviceLocaleProvider.getCountryIfRegion(deviceLocaleProvider.getDeviceLocale()).toLowerCase(Locale.getDefault()),
                    deviceLocaleProvider.getDeviceLanguage(),
                    bookingFlowInput.chosenRate().rateType());
        }
        networkCompositeDisposable.add(graphQLReviewBookingUseCase.getPaymentMethodsWithDonationAndBookingConfirmation(
                        new PaymentMethodsRequestBody(
                                bookingFlowInput.basketReference(),
                                deviceLocaleProvider.getDeviceLanguage(),
                                deviceLocaleProvider.getCountryIfRegion(deviceLocaleProvider.getDeviceLocale())
                                        .toLowerCase(Locale.getDefault()),
                                ANDROID_APPS_CHANNEL,
                                isInnBusinessUser ? BUSINESS_FULL_NAME : LEISURE_FULL_NAME),
                        donationsRequestBody, isInnBusinessUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(paymentmethodAndBookingConfirmation -> {
                    if (paymentmethodAndBookingConfirmation.isPaymentMethodAvailable()
                            && paymentmethodAndBookingConfirmation.getBookingConfirmation() != null) {
                        PaymentDetailsInput updatedPaymentInput = reviewBookingInput.paymentDetailsInput().toBuilder()
                                .paymentMethodsDetailInput(PaymentMethodExtensionsGQLKt
                                        .toParcelableParcelablePaymentMethodsInput(paymentmethodAndBookingConfirmation)).build();
                        List<AcceptedCreditCard> acceptedCreditCard = PaymentMethodExtensionsGQLKt
                                .toAcceptedCreditCard(paymentmethodAndBookingConfirmation);
                        String bookingReference = paymentmethodAndBookingConfirmation.getBookingConfirmation().getBookingReference();
                        String paypalClientTokenIfPresent = findPaypalClientTokenIfPresent(paymentmethodAndBookingConfirmation);
                        if (paypalClientTokenIfPresent != null && !paypalClientTokenIfPresent.isEmpty()) {
                            view.initPaypalClent(paypalClientTokenIfPresent);
                            if (payPalClient == null) {
                                payPalClient = view.getPayPalClient();
                                payPalClient.setListener(this);
                            }
                        }
                        reviewBookingInput = reviewBookingInput.toBuilder()
                                .paymentDetailsInput(updatedPaymentInput)
                                .acceptedCreditCards(acceptedCreditCard)
                                .bookingReference(bookingReference)
                                .uuidBasketReference(uuidBasketReference)
                                .operaDonations(PaymentMethodExtensionsGQLKt
                                        .toParcelableDonationsDomain(paymentmethodAndBookingConfirmation.getDonations()))
                                .paypalClientToken(paypalClientTokenIfPresent)
                                .build();

                        populateMutations(reviewBookingInput);
                        showOrHideDonations(view);

                        Float fee = 0f;
                        ReviewBookModel reviewBookModel = new ReviewBookModel(reviewBookingInput, fee, donationPrice,
                                totalPrice, deviceLocaleProvider, donationPledgeString);
                        displayUiComponents(view, reviewBookModel);

                        view.showLoadingSpinner(false, false, EMPTY_STRING);

                    } else if (!paymentmethodAndBookingConfirmation.getError().isEmpty()) {
                        view.showLoadingSpinner(false, false, EMPTY_STRING);
                        view.showMessageandTakeUserBackToPreviousScreen();
                    } else {
                        view.showLoadingSpinner(false, false, EMPTY_STRING);
                        view.showMessageandTakeUserBackToPreviousScreen();
                    }
                }, error -> {
                    view.showLoadingSpinner(false, false, EMPTY_STRING);
                    view.showMessageandTakeUserBackToPreviousScreen();
                    logService.logException(error, "Payment methods call failure");
                }));
    }

    private void makeBookingOpera(@NonNull View view) {
        reviewBookingInput = reviewBookingInput.toBuilder()
                .cvv(cvv)
                .donation(CommonMappersKt.toParcelablePrice(donationPrice))
                .businessBookingOptions(isSelectedPaymentABusinessCard(reviewBookingInput) ? createBusinessBookingOptions(view) : null)
                .build();

        ConfirmationSpinnerMessages confirmationSpinnerMessages = new Gson()
                .fromJson(getStringResource.invoke(ContentManagedResourceRepository.
                        Key.CONFIRMATION_POLLING_MESSAGES_CONFIG), ConfirmationSpinnerMessages.class);
        view.showLoadingSpinner(true, false, confirmationSpinnerMessages.getMessages().get(0).getMessage());
        String surname = reviewBookingInput.paymentDetailsInput().bookerDetails().lastName();
        BookingFlowInput bookingFlowInput = reviewBookingInput.paymentDetailsInput().bookingFlowInput();
        startPollingForBasketStatusAndThenBookingConfirmation(surname, bookingFlowInput, view);
    }

    private void makeReservationWithoutCard(@NonNull View view) {
        reviewBookingInput = reviewBookingInput.toBuilder()
                .donation(CommonMappersKt.toParcelablePrice(donationPrice))
                .build();

        ConfirmationSpinnerMessages confirmationSpinnerMessages = new Gson()
                .fromJson(getStringResource.invoke(ContentManagedResourceRepository.
                        Key.CONFIRMATION_POLLING_MESSAGES_CONFIG), ConfirmationSpinnerMessages.class);
        view.showLoadingSpinner(true, false, confirmationSpinnerMessages.getMessages().get(0).getMessage());
        String surname = reviewBookingInput.paymentDetailsInput().bookerDetails().lastName();
        BookingFlowInput bookingFlowInput = reviewBookingInput.paymentDetailsInput().bookingFlowInput();
        isPaymentOutage = view.getPaymentComponent().isPaymentOutage();
        startPollingForBasketStatusAndThenBookingConfirmation(surname, bookingFlowInput, view);
    }

    private void startPollingForBasketStatusAndThenBookingConfirmation(
            String surname, BookingFlowInput bookingFlowInput, View view) {

        long initialPollStartTime = getLongResource.invoke(ContentManagedResourceRepository.Key.CONFIRMATION_POLLING_DELAY);
        long pollInterval = getLongResource.invoke(ContentManagedResourceRepository.Key.CONFIRMATION_POLLING_INTERVAL);
        ConfirmationSpinnerMessages confirmationSpinnerMessages = new Gson()
                .fromJson(getStringResource.invoke(ContentManagedResourceRepository.
                        Key.CONFIRMATION_POLLING_MESSAGES_CONFIG), ConfirmationSpinnerMessages.class);
        long maxAttempts = confirmationSpinnerMessages.getMaxAttempts();
        attemptsMade = 0;
        networkCompositeDisposable.add(graphQLReviewBookingUseCase
                .getBasketStatusRevisedPayments(uuidBasketReference).toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .delaySubscription(initialPollStartTime, TimeUnit.SECONDS)
                        .repeatWhen(it -> it.delay(pollInterval, TimeUnit.SECONDS))
                        .takeUntil(status -> {
                            if (attemptsMade >= maxAttempts) {
                                return true;
                            } else {
                                switch (status.getBasketStatus().name()) {
                                    case  "PAY_PENDING":
                                    case  "PROCESSING":
                                    default:
                                        return false;
                                    case "COMPLETED" :
                                    case "FAILED" :
                                    case "OPEN" :
                                        return true;
                                }
                            }
                        })
                        .doOnNext(__ -> {
                            attemptsMade++;
                            if (attemptsMade == 1) {
                                timerStart = System.currentTimeMillis();
                                    view.showLoadingSpinner(false, false, "");
                                    view.showLoadingSpinner(true, false,
                                        confirmationSpinnerMessages.getMessages().get(0).getMessage());
                            } else {
                                long elapsedTime =  System.currentTimeMillis() - timerStart;
                                if (elapsedTime <= confirmationSpinnerMessages.getMessages().get(1).getSeconds() * 1000) {
                                    view.showLoadingSpinner(true, false,
                                            confirmationSpinnerMessages.getMessages().get(1).getMessage());
                                } else {
                                    view.showLoadingSpinner(true, false,
                                            confirmationSpinnerMessages.getMessages().get(2).getMessage());
                                }
                            }
                        })
                        .retry(maxAttempts)
                        .lastElement().toObservable()
                .subscribe(success -> {
                    switch (success.getBasketStatus().name()) {
                        case "PAY_PENDING":
                        case "PROCESSING":
                            view.showMessageWhenBasketPollingStatusIsPending(
                                    reviewBookingInput.paymentDetailsInput().bookerDetails().email());
                            disableAppPromotionalIncentiveIfAvailable();
                            break;
                        case "COMPLETED":
                            getTokenAndMakeBookingConfirmationCall(surname, bookingFlowInput, view);
                            disableAppPromotionalIncentiveIfAvailable();
                            break;
                        case "FAILED":
                            view.showMessageWhenConfirmationSpinnerIsCancelledOrFailed();
                            view.showLoadingSpinner(false, false, "");
                            break;
                        case "OPEN":
                            view.showGenericError();
                            view.showLoadingSpinner(false, false, "");
                            break;
                        default:
                    }
                }, failure -> {
                    view.showMessageWhenConfirmationSpinnerIsCancelledOrFailed();
                    view.showLoadingSpinner(false, false, "");
                    logService.logException(failure, null);
                })
        );
    }

    private void disableAppPromotionalIncentiveIfAvailable() {
        if (Boolean.TRUE.equals(storage.isAppPromotionalIncentiveAvailable())) {
            storage.setIsAppPromotionalIncentiveAvailable(false);
        }
    }

    private void getTokenAndMakeBookingConfirmationCall(String surname,
                                                       BookingFlowInput bookingFlowInput, View view) {
        networkCompositeDisposable.add(findBookingUsecase.findBooking(new FindBookingRequestBody(
                        reviewBookingInput.bookingReference(), surname, bookingFlowInput.arrivalDate().toString(),
                        deviceLocaleProvider.getDeviceLanguage(),
                        deviceLocaleProvider.getDeviceLocale().getCountry().toLowerCase(),
                        new BookingChannelDetails(
                                isInnBusinessUser ? Channel.BB.name() : Channel.PI.name(),
                                SUB_CHANNEL,
                                deviceLocaleProvider.getDeviceLanguage().toLowerCase())))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(findBookingDomain -> {
                    token = findBookingDomain.getToken();
                    String confirmationNumber = findBookingDomain.getBookingReference();
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                            .ofPattern(DateFormat.DATE_TIME_WITH_OFFSET);
                    networkCompositeDisposable.add(graphQLBookingDetailsUseCase
                            .bookingConfirmationAndManageBooking(
                                    uuidBasketReference,
                                    deviceLocaleProvider.getDeviceLocale().getCountry().toLowerCase(),
                                    deviceLocaleProvider.getDeviceLanguage(),
                                    isInnBusinessUser ? Channel.BB.name() : Channel.PI.name(),
                                    bookingFlowInput.hotelName(),
                                    new CancelInformationRequestBody(
                                            uuidBasketReference,
                                            bookingFlowInput.hotelCode(),
                                            OffsetDateTime.now().format(dateTimeFormatter),
                                            Objects.requireNonNull(URLEncoder.encode(token, "UTF-8")),
                                            new BookingChannelDetails(
                                                    isInnBusinessUser ? Channel.BB.name() : Channel.PI.name(),
                                                    SUB_CHANNEL,
                                                    deviceLocaleProvider.getDeviceLanguage().toLowerCase())
                                    ))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnNext(result -> {
                                        if (!result.getLeadGuestFullName().equals(EMPTY_STRING)) {
                                            balanceOutstanding = result.getBalanceOutstanding();
                                            networkCompositeDisposable.add(
                                                    storeBooking.execute(result)
                                                            .subscribeOn(Schedulers.io())
                                                            .observeOn(AndroidSchedulers.mainThread())
                                                            .subscribe());
                                            clearBookedRecentSearch.execute(RecentSearchMapperKt.toRecentSearch(paymentDetailsInput))
                                                    .subscribeOn(Schedulers.io())
                                                    .subscribe();

                                            storage.setRefreshDashboard(true);

                                            if (shouldCreateAccount) {
                                                createCustomer(view, reviewBookingInput, savePaymentDetails, confirmationNumber);
                                            } else {
                                                logConfirmedBooking(confirmationNumber,
                                                        true, false);
                                                logAppsFlyerBookingConfirmation(view.getContext());
                                                logDynatraceBookingConfirmation();
                                                navigateToBookingsActivityOpera(view, null,
                                                         reviewBookingInput.bookingReference(), uuidBasketReference);
                                            }

                                                track3CPPayment(view, null, null);
                                        }
                                    }
                            ).subscribe());
                })
        );
    }

    private Boolean isSelectedPaymentABusinessCard(ReviewBookingInput input) {
        return input.isBusinessCard() || isBusinessCard;
    }

    private void show3CBookingErrorRevisedSolution(View view, String errorCode) {
        if (errorCode.equals(ErrorCodesKt.BOOKING_PAYMENT_FRAUD_CHECK)) {
            view.show3CPaymentFraudCheckError(messageProvider.getBookingError(errorCode, getStringResource),
                    contentManagedResourceRepository
                            .getString(ContentManagedResourceRepository.Key.CUSTOMER_SERVICE_NUMBER.getValue()));
        } else {
            view.showUnexpectedError(
                    messageProvider.getBookingError(errorCode, getStringResource));
        }
    }

    private BusinessBookingOptions createBusinessBookingOptions(View view) {
        String breakfastCode = null;
        List<Breakfast> selectedBreakfasts = reviewBookingInput.paymentDetailsInput().bookingFlowInput().breakfasts();
        if (selectedBreakfasts != null && !selectedBreakfasts.isEmpty()) {
            for (int i = 0; i < selectedBreakfasts.size(); i++) {
                if (selectedBreakfasts.get(i).adults() != 0) {
                    breakfastCode = selectedBreakfasts.get(i).code();
                    break;
                }
            }
        }

            return BusinessBookingOptions.builder()
                    .alcoholAllowed(view.getPaymentComponent().getAlcoholAllowed())
                    .atosPassword(view.getPaymentComponent().getCnpAuth() ? view.getPaymentComponent().getMemorableWord() : null)
                    .breakfastCode(breakfastCode)
                    .carParkingAllowed(view.getPaymentComponent().getCarParkingAllowed())
                    .cardNotPresentAuth(view.getPaymentComponent().getCnpAuth())
                    .dinnerAllowance(view.getPaymentComponent().getDinnerAllowance()
                            ? CommonMappersKt.toBookingPrice(dinnerBudget) : null)
                    .purchaseOrder(reviewBookingInput.cnpPurchaseOrderNumber())
                    .customerReference(reviewBookingInput.cnpCustomerReferenceNumber())
                    .wifiAccessAllowed(view.getPaymentComponent().getWifiAccessAllowed()).build();

    }

    private void logConfirmedBooking(@NonNull String confirmationNumber,
                             boolean prepaid,
                             boolean accountCreated) {
        BookingConfirmationAnalyticsData bookingConfirmationAnalyticsData
                = createBookingConfirmationAnalyticsBody(confirmationNumber, prepaid, accountCreated);
        logConfirmedBookingInFirebase(bookingConfirmationAnalyticsData);
        analytics.track(ScreenState.BOOKING_CONFIRMATION, bookingConfirmationAnalyticsData);
        analytics.trackCSQTransaction(totalPrice.getAmount(), totalPrice.getCurrency(), confirmationNumber);
    }

    public void logAppsFlyerBookingConfirmation(Context context) {
        AppsFlyerBookingConfirmationParams params = new AppsFlyerBookingConfirmationParams(reviewBookingInput.bookingReference(),
                totalPrice.getAmount(),
                totalPrice.getCurrency(),
                reviewBookingInput.paymentDetailsInput().bookingFlowInput().hotelCode());
        AppsFlyerHelper.INSTANCE.logBookingConfirmationEvent(context, params);
    }

    private void logDynatraceBookingConfirmation() {
        DynatraceHelper.INSTANCE.trackAction(totalPrice.getAmount(), REVENUE_ACTION, REVENUE_KEY);
    }

    private BookingConfirmationAnalyticsData createBookingConfirmationAnalyticsBody(@NonNull String confirmationNumber,
                                                                                    boolean prepaid,
                                                                                    boolean accountCreated) {

        BookingFlowInput bookingFlowInput = reviewBookingInput.paymentDetailsInput().bookingFlowInput();
        List<RoomBooking> allRoomBooking;
        if (bookingFlowInput.roomBookings().isEmpty()) {
            if (bookingFlowInput.accessibleRoomBookings() != null
                    && !bookingFlowInput.accessibleRoomBookings().isEmpty()) {
                allRoomBooking = SummaryExtensionsKt.addTwoRoomBookingListAndOrderIt(
                        bookingFlowInput.accessibleRoomBookings(),
                        bookingFlowInput.twinRoomBookings());
            } else {
                allRoomBooking = bookingFlowInput.twinRoomBookings();
            }
        } else {
            if (bookingFlowInput.accessibleRoomBookings() != null
                    && !bookingFlowInput.accessibleRoomBookings().isEmpty()) {
                allRoomBooking = SummaryExtensionsKt.addThreeRoomBookingListAndOrderIt(
                        bookingFlowInput.roomBookings(),
                        bookingFlowInput.accessibleRoomBookings(),
                        bookingFlowInput.twinRoomBookings());
            } else if (bookingFlowInput.twinRoomBookings() != null
                    && !bookingFlowInput.twinRoomBookings().isEmpty()) {
                allRoomBooking = SummaryExtensionsKt.addTwoRoomBookingListAndOrderIt(
                        bookingFlowInput.roomBookings(),
                        bookingFlowInput.twinRoomBookings());
            } else {
                allRoomBooking = bookingFlowInput.roomBookings();
            }
        }

        final Pair<Boolean, String> cardTypePair = new Pair<>(false, selectedPaymentCardType);

        String rateCode = bookingFlowInput.isEmployeeRateSelected()
                ? EMPLOYEE_RATE_CODE
                : bookingFlowInput.chosenRate().code();

        String lettingCode = allRoomBooking.get(0).getLettingType().getCode();
        return BookingConfirmationAnalyticsData.builder()
                .totalUpsellCost(bookingFlowInput.totalUpsellAmount())
                .arrivalDate(bookingFlowInput.arrivalDate())
                .hotelCode(bookingFlowInput.hotelCode())
                .rateCode(rateCode)
                .numNights(bookingFlowInput.numNights())
                .roomsBooked(allRoomBooking)
                .business(reviewBookingInput.paymentDetailsInput().isBusinessTrip())
                .totalPrice(totalPrice)
                .numGuests(bookingFlowInput.numGuests())
                .prepaid(prepaid)
                .paymentMethod(selectedPaymentCardType)
                .isPaymentOutage(isPaymentOutage)
                .confirmationNumber(confirmationNumber)
                .card(reviewBookingInput.cardType())
                .cardType(cardTypePair)
                .addedExtras(new Pair<>(bookingFlowInput.selectedUpsellItems(), bookingFlowInput.selectedExtras()))
                .totalCostExcludingCC(bookingFlowInput.totalStayPrice(paymentDetailsInput.isBusinessTrip()).getAmount())
                .bookingCompleteTime(new Date())
                .donationRevenue(0f)
                .isLoggedIn(isCustomerLoggedInState.get())
                .businessAlcoholSelected(alcoholSelected)
                .businessDinnerAllowanceSelected(dinnerAllowance)
                .businessUltimateWifi(wifiAccessSelected)
                .dinnerBudget(dinnerBudget)
                .businessParkingSelected(carParkingSelected)
                .lettingType(lettingCode)
                .roomPrice(bookingFlowInput.totalRoomsCost(false))
                .goshDonation(donationPrice.getAmount())
                .accountCreated(accountCreated)
                .promoCode(bookingFlowInput.promotionCode())
                .promoName(bookingFlowInput.promotionTag())
                .rateTag(bookingFlowInput.rateTag())
                .paymentTiming(paymentTimingChoice.name())
                .promoBookingComplete(bookingFlowInput.promotionCode() != null && !bookingFlowInput.promotionCode().isEmpty())
                .build();
    }

    private void logConfirmedBookingInFirebase(@NonNull BookingConfirmationAnalyticsData
                                                       bookingConfirmationAnalyticsData) {
        FirebaseParams firebaseParams = new FirebaseParams();
        firebaseParams.putInteger(FirebaseParams.ParamName.NUMBER_OF_BOOKINGS, 1);
        firebaseParams.putString(FirebaseParams.ParamName.CURRENCY, bookingConfirmationAnalyticsData.totalPrice().getCurrency());

        firebaseParams.putDouble(FirebaseParams.ParamName.PRICE_AMOUNT,
                ((Float) bookingConfirmationAnalyticsData.totalPrice().getAmount()).doubleValue());

        firebaseParams.putString(FirebaseParams.ParamName.HOTEL_CODE, bookingConfirmationAnalyticsData.hotelCode());
        firebaseParams.putFormattedDate(FirebaseParams.ParamName.ARRIVAL_DATE, bookingConfirmationAnalyticsData.arrivalDate());
        firebaseParams.putFormattedDate(FirebaseParams.ParamName.DEPARTURE_DATE,
                bookingConfirmationAnalyticsData.arrivalDate().plusDays(bookingConfirmationAnalyticsData.numNights()));
        firebaseParams.putInteger(FirebaseParams.ParamName.NUMBER_OF_NIGHTS, bookingConfirmationAnalyticsData.numNights());
        firebaseParams.putInteger(FirebaseParams.ParamName.NUMBER_OF_ROOMS, bookingConfirmationAnalyticsData.numRoomsBooked());
        firebaseParams.putInteger(FirebaseParams.ParamName.NUMBER_OF_GUESTS, bookingConfirmationAnalyticsData.numGuests());

        firebaseParams.putString(FirebaseParams.ParamName.RATE_TYPE, TrackingAnalyticsUtils.formatRateInfo(
                bookingConfirmationAnalyticsData.rateCode(),
                bookingConfirmationAnalyticsData.lettingType(),
                bookingConfirmationAnalyticsData.roomPrice()));

        firebaseParams.putString(FirebaseParams.ParamName.TRIP_TYPE, tripTypeLabel(bookingConfirmationAnalyticsData.business()));
        if (reviewBookingInput.cardInfo() != null && bookingConfirmationAnalyticsData.card() != null) {
            firebaseParams.putString(FirebaseParams.ParamName.PAYMENT_TYPE, bookingConfirmationAnalyticsData.card());
        }

        firebaseLogger.logEvent(FirebaseAnalytics.Event.PURCHASE, firebaseParams);
    }

    private ReviewBookingInput toReviewBookingInput(@NonNull EditGuestInput editGuestInput) {
        PaymentDetailsInput updatedPaymentDetailsInput = paymentDetailsInput.toBuilder()
                .bookerDetails(editGuestInput.bookerDetails())
                .guestDetailsList(editGuestInput.guestDetailsFormDataInputs())
                .isBookerStaying(editGuestInput.isBookerStaying())
                .build();
        return reviewBookingInput.toBuilder()
                .paymentDetailsInput(updatedPaymentDetailsInput)
                .build();
    }

    void updateDonation(@NonNull Float updatedDonationAmount, @NonNull View view) {
        totalPrice = new PriceDomain(totalPrice.getAmount() - donationPrice.getAmount()
                + updatedDonationAmount, totalPrice.getCurrency());
        donationPrice = new PriceDomain(updatedDonationAmount, totalPrice.getCurrency());
            List<ParcelableDonationPackageDomain> donationPackages =
                    reviewBookingInput.operaDonations().getDonationPackages();
            String charityPackageCode = com.whitbread.premierinn.reviewbooking.mappers.ExtensionsKt
                    .findCharityPackageCode(donationPackages, updatedDonationAmount);
            reviewBookingInput = reviewBookingInput.toBuilder().selectedCharityPackageCode(charityPackageCode).build();
        if (donationPrice.getAmount() > 0f) {
            view.animateTotalPriceChange();
        }

        boolean animateChange = donationPrice.getAmount() > 0f;
        view.setDonation(donationPrice, totalPrice, animateChange, deviceLocaleProvider, donationPledgeString);
    }

    private void populateMutations(@NonNull ReviewBookingInput reviewBookingInput) {
        this.reviewBookingInput = reviewBookingInput;

        this.paymentDetailsInput = reviewBookingInput.paymentDetailsInput();

        totalPrice = calculateTotalPrice(paymentDetailsInput);
    }

    private PriceDomain calculateTotalPrice(PaymentDetailsInput paymentDetailsInput) {
            if (paymentDetailsInput.paymentMethodsDetailInput() != null) {
                Float totalStayPrice = paymentDetailsInput.paymentMethodsDetailInput().getParcelableBookingConfirmation().getTotalCost();
                String currency = paymentDetailsInput.paymentMethodsDetailInput().getParcelableBookingConfirmation().getCurrencyCode();

                float totalPriceAmount = totalStayPrice + donationPrice.getAmount();
                return new PriceDomain(totalPriceAmount, currency);
            } else {
                return PriceDomain.Companion.createDefault();
            }
    }

    private void createCustomer(View view, ReviewBookingInput reviewBookingInput, Boolean savePaymentDetails,
                                String confirmationNumber) {
        view.showLoading(true);

        viewCompositeDisposable.add(
                createCustomerAccount(reviewBookingInput, savePaymentDetails)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(asyncResult -> {
                            if (asyncResult instanceof AsyncResult.Success) {
                                AsyncResultKt.mapToAsyncResult(authenticateCustomer.invoke(new AuthenticateCustomer.Params(
                                                paymentDetailsInput.bookerDetails().email(), createAccountPassword, UserType.LEISURE)))
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .doOnComplete(() -> {
                                            view.showLoading(false);
                                            logConfirmedBooking(confirmationNumber,
                                                    true, true);
                                            logAppsFlyerBookingConfirmation(view.getContext());
                                            logDynatraceBookingConfirmation();
                                                navigateToBookingsActivityOpera(view, RESPONSE_SUCCESS,
                                                        reviewBookingInput.bookingReference(), this.uuidBasketReference);
                                        }).subscribe();
                            }
                            if (asyncResult instanceof AsyncResult.Error) {
                                view.showLoading(false);
                                logConfirmedBooking(confirmationNumber,
                                        true, false);
                                logAppsFlyerBookingConfirmation(view.getContext());
                                logDynatraceBookingConfirmation();
                                navigateToBookingsActivityOpera(view,
                                        ((AsyncResult.Error) asyncResult).getError().getClass().getSimpleName(),
                                        reviewBookingInput.bookingReference(), uuidBasketReference);
                            }
                        }));
    }

    private Observable<AsyncResult> createCustomerAccount(ReviewBookingInput
                                                                  reviewBookingInput, Boolean savePaymentDetails) {
        PaymentDetailsInput paymentDetailsInput = reviewBookingInput.paymentDetailsInput();
        PaymentCard paymentCard = null;

        Customer customer = new Customer("",
                new FullName(
                        paymentDetailsInput.bookerDetails().title(),
                        paymentDetailsInput.bookerDetails().firstName(),
                        paymentDetailsInput.bookerDetails().lastName()
                ),
                new Contact(
                        paymentDetailsInput.bookerDetails().email(),
                        paymentDetailsInput.bookerDetails().phoneNumber(),
                        null
                ),
                new Address(
                        reviewBookingInput.cardHolderAddress().line1(),
                        reviewBookingInput.cardHolderAddress().line2(),
                        null,
                        reviewBookingInput.cardHolderAddress().city(),
                        null,
                        reviewBookingInput.cardHolderAddress().postcode(),
                        null,
                        reviewBookingInput.cardHolderAddress().countryCode()
                ),
                null,
                null,
                false,
                BookingPreferences.Companion.getEMPTY(),
                null,
                null,
                null,
                null,
                null
        );
        return AsyncResultKt.mapToAsyncResult(createCustomer.invoke(new CreateCustomer.Params(
                customer,
                createAccountPassword,
                deviceLocaleProvider.getDeviceLanguage())));
    }

    private void navigateToBookingsActivityOpera(@NonNull View view,
                                            @Nullable String accountResponse,
                                            String bookingReference, String uuidBasketReference) {
                view.startMyBookingsActivity(
                        bookingReference,
                        uuidBasketReference,
                        reviewBookingInput.paymentDetailsInput().bookerDetails().email(),
                        accountResponse, token);

    }

    public void clearPollingDisposables() {
        pollingDisposable.clear();
    }

    @Override
    public void onPayPalSuccess(@NonNull PayPalAccountNonce payPalAccountNonce) {
        // send nonce to server
        paypalNonce = payPalAccountNonce.getString();
        paypalNonceChange.onNext(paypalNonce);
    }

    @Override
    public void onPayPalFailure(@NonNull Exception error) {
        if (!(error instanceof UserCanceledException)) {
            onPaypalError.onNext(0);
            logService.logException(error, error.getMessage());
        } else {
            onPaypalError.onNext(1);
        }
    }

    Observable<String> observePayPalNonceChange() {
        return paypalNonceChange;
    }

    Observable<Integer> observeOnPaypalError() {
        return onPaypalError;
    }

    public void setPaymentCardType(String paymentCardType) {
        selectedPaymentCardType = paymentCardType;
    }

    private void displayInfoBoxForBBAllowancesIfApplicable(View view) {
        if (isInnBusinessUser) {
            view.showInfoBoxForBBAllowancesIfApplicable(cnpRequired,
                    paymentDetailsInput.bookingFlowInput().dinnerBudget() > 0f,
                    storage.getCustomer().getCompany().getRequestedCompany().getBookingAllowances().getAllowCarParking()
            );
        }
    }

    public interface View extends PresenterView {

        Context getContext();

        void startPaymentBreakdownActivity(@NonNull PaymentBreakdownInput paymentBreakdownInput);

        void display(@NonNull ReviewBookModel reviewBookModel, CardSummaryModel cardSummaryModel,
                     boolean isBusinessCustomer, int numNights, @NonNull String rateName,
                     boolean isHub, @Nullable String formattedBaseRate, @Nullable String promotionCode,
                     @Nullable String promoTag, CompositeDisposable viewCompositeDisposable,
                     List<CountryDomain> listOfCountries, boolean featurePaypal, boolean featureGooglePay);

        void setGuestDetails(@NonNull ReviewBookingInput reviewBookingInput);

        void setAdditionalInformation(List<AdditionalInformation> additionalInformation);

        void startEditGuestActivity(@NonNull EditGuestInput editGuestInput);

        void startMyBookingsActivity(@NonNull String bookingReference,
                                     @Nullable String uuidBookingReference,
                                     @NonNull String bookerEmail,
                                     @NonNull String accountResponse,
                                     String token);

        void showPaymentError();

        void showPaymentComponentValidationError();

        void showGenericError();

        void storageFailedError();

        void showMessageandTakeUserBackToPreviousScreen();

        void showMessageWhenPollingStatusIsPendingOrApiDown(String email);

        void showMessageWhenBasketPollingStatusIsPending(String email);

        void showMessageWhenConfirmationSpinnerIsCancelledOrFailed();

        void showThreeCPiPageLaunchFailError();

        void showUnexpectedError(String error);

        void show3CPaymentFraudCheckError(String error, String phoneNumber);

        void loadThreeCpIPage(@NonNull ThreeCpInput input, String selectedPaymentCardType, String paymentTimingSelection);

        void showPrivacyPolicy(@NonNull String htmlContent);

        void setOtherExtras(List<ExtrasItemDomain> selectedExtras, DeviceLocaleProvider deviceLocaleProvider);

        void setDonation(@NonNull PriceDomain donation,
                         @NonNull PriceDomain totalPrice,
                         boolean animateChange,
                         @NonNull DeviceLocaleProvider deviceLocaleProvider,
                         String donationPledgeString);

        void showBookerAsOnlyGuest();

        void showBookerAsOneOfManyGuests();

        void showChildrenBreakfastExpense(ReviewBookingInput input, int numberOfNights);

        Observable<ReviewBookingInput> onInputUpdated();

        Observable<EditGuestInput> onGuestDetailsUpdated();

        Observable<Address> onPaymentAddressPostcodeFinderEntry();

        void showPriceIncludesTaxesAndFeesMessage(boolean show);

        Observable<Unit> onPaymentBreakdownClick();

        Observable<Unit> onGuestDetailsEditClick();

        Observable<Unit> onAdditionalInformationEditClick();

        Observable<Unit> onConfirmBookingClick();

        Observable<Unit> onPayPalButtonClick();

        Observable<Boolean> onCardVerificationOkOpera();

        void showBreakfastExpenseWithOnlyAdultsPaying(DeviceLocaleProvider deviceLocaleProvider,
                                                      List<UpsellItem> selectedUpsellItems, int numNights);

        void showLoading(boolean value);

        void showPaypalButtonLoading(boolean value);

        void setPayNowInTotalPriceViewMessage(boolean value);

        Observable<DonationView.DonationType> donationButtonClicks();

        void setUpDonations(DonationsInput input);

        void setDonationInfo(String donationTitle, String donationImageUrl, String donationDescription);

        void setDonationOptionVisible(boolean isVisible);

        void animateTotalPriceChange();

        void setConfirmButtonText(boolean createAccount);
        void setThreeCpConfirmButtonText(@Nullable PaymentRadioButtonView selectedPaymentType,
                                         @NonNull boolean createAccount);

        void togglePayPalButton(boolean showPayPalButton);

        void showAmendAndCancellationMsg(@NonNull String message);

        PublishRelay<PaymentRadioButtonView> onPaymentTypeSelectionChanged();

        PublishRelay<PaymentTimingChoice> onPaymentTimingSelectionChange();

        void setPaymentAddressFields(Address address);

        Boolean isPaymentComponentReadyForSubmission();

        Boolean isBusinessCustomerWithNoCardsAllowed();

        Boolean isDinnerBudgetForSubmission();

        PaymentComponentView getPaymentComponent();

        void showLoadingSpinner(Boolean show, Boolean forPaypal, String message);

        void initPaypalClent(String paypalClientToken);

        PayPalClient getPayPalClient();

        void myTokenizePayPalAccountWithVaultMethod();

        void collectDeviceDataForPayPal();

        String getDeviceDataForPayPal();

        void hideLeadGuest();

        void showInfoBoxForBBAllowancesIfApplicable(boolean cnpRequired, boolean dinnerAllowed, boolean carParkingAllowed);
    }
}

