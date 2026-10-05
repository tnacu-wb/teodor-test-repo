package com.whitbread.premierinn.guestdetails;

import static com.whitbread.premierinn.common.AddressFormDataOutput.Form.ADDRESS_LINE_1;
import static com.whitbread.premierinn.common.AddressFormDataOutput.Form.ADDRESS_LINE_2;
import static com.whitbread.premierinn.common.AddressFormDataOutput.Form.ADDRESS_LINE_3;
import static com.whitbread.premierinn.common.AddressFormDataOutput.Form.COUNTRY;
import static com.whitbread.premierinn.common.AddressFormDataOutput.Form.POSTCODE;
import static com.whitbread.premierinn.common.view.GuestDetailsTripTypeViewKt.TRIP_TYPE_BUSINESS;
import static com.whitbread.premierinn.common.view.GuestDetailsTripTypeViewKt.TRIP_TYPE_LEISURE;
import static com.whitbread.premierinn.common.view.GuestDetailsTripTypeViewKt.TRIP_TYPE_UNSELECTED;
import static com.whitbread.premierinn.data.common.Constants.LANGUAGE_ENGLISH;
import static com.whitbread.premierinn.domain.common.Constants.LANGUAGE_ENGLISH_DOMAIN;
import static com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.GDPR_GUEST_DETAILS_PRIVACY;
import static com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.GDPR_PRIVACY_FOOTER;
import static com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput.Form.CONTACT_NUMBER;
import static com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput.Form.EMAIL;
import static com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput.Form.FIRST_NAME;
import static com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput.Form.LAST_NAME;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;
import com.whitbread.premierinn.api.request.booking.BookingAddress;
import com.whitbread.premierinn.api.response.CardInfo;
import com.whitbread.premierinn.api.response.InstanceFactory;
import com.whitbread.premierinn.common.AddressField;
import com.whitbread.premierinn.common.AddressFormDataOutput;
import com.whitbread.premierinn.common.BookingFlowInput;
import com.whitbread.premierinn.common.PaymentProvider;
import com.whitbread.premierinn.common.PaymentTimingChoice;
import com.whitbread.premierinn.common.RoomBooking;
import com.whitbread.premierinn.common.StringResourceProvider;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.format.DateFormat;
import com.whitbread.premierinn.common.view.ToggleButtonView;
import com.whitbread.premierinn.data.common.DomainMappers;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager;
import com.whitbread.premierinn.data.graphql.mapper.GraphQLCreateReservationGuestMapperKt;
import com.whitbread.premierinn.data.remote.AccountApiContract;
import com.whitbread.premierinn.data.remote.graphql.contracts.CreateReservationGuestGraphQLContract;
import com.whitbread.premierinn.data.remote.graphql.contracts.PaymentMethodsGraphQLContract;
import com.whitbread.premierinn.domain.authentication.NoLongerValidCredentials;
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn;
import com.whitbread.premierinn.domain.countries.GetCountries;
import com.whitbread.premierinn.domain.countries.entity.CountryDomain;
import com.whitbread.premierinn.domain.customer.entity.Customer;
import com.whitbread.premierinn.domain.customer.usecase.GetCustomer;
import com.whitbread.premierinn.domain.graphql.anonymousNewsletterPreferences.entity.AnonymousNewsletterPreferencesDomain;
import com.whitbread.premierinn.domain.graphql.anonymousNewsletterPreferences.usecase.GraphQLAnonymousNewsletterPreferencesUseCase;
import com.whitbread.premierinn.domain.graphql.guestDetails.usecase.GraphQLGuestDetailsUseCase;
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource;
import com.whitbread.premierinn.guestdetails.adapter.RoomGuestDetailsData;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataInput;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput;
import com.whitbread.premierinn.hoteldetails.SelectedRate;
import com.whitbread.premierinn.paymentdetails.PaymentDetailsInput;
import com.whitbread.premierinn.postcodefinder.ParcelableAddress;
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput;
import com.whitbread.premierinn.utils.RxJavaTestRule;
import com.whitbread.premierinn.utils.model.CustomerFixture;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.TestObserver;
import kotlin.Pair;
import kotlin.Unit;

@RunWith(MockitoJUnitRunner.class)
public class GuestDetailsPresenterTest {

    @Rule
    public RxJavaTestRule rxJavaTestRule = new RxJavaTestRule();

    @Mock GuestDetailsPresenter.View viewMock;
    @Mock BookingFlowInput bookingFlowInputMock;
    @Mock GetStringResource getStringResource;
    @Mock RoomBooking bookingRoomMock;
    @Mock GetCountries getCountriesMock;
    @Mock GetCustomer getCustomer;
    @Mock IsCustomerLoggedIn isCustomerLoggedIn;
    @Mock ParcelableAddress postcodeAddressMock;
    @Mock SimplePersistenceManager persistenceManager;
    @Mock DeviceLocaleProvider deviceLocaleProvider;
    @Mock GraphQLGuestDetailsUseCase graphQLGuestDetailsUseCase;
    @Mock GraphQLAnonymousNewsletterPreferencesUseCase anonymousNewsletterPreferencesUseCase;
    @Mock private GuestDetailsFormDataInput bookerDetailsMock;
    @Mock BookingAddress bookingAddress;
    @Mock private CardInfo cardInfoMock;
    @Mock private SelectedRate selectedRate;
    AccountApiContract.CustomerResponse successCustomerResponse;
    AccountApiContract.CustomerResponse successCustomerResponseNoPaymentCard;
    AccountApiContract.CustomerResponse successCustomerResponseBusinessCard;
    AccountApiContract.CustomerResponse successCustomerResponseBusinessPreference;
    AccountApiContract.CustomerResponse successCustomerResponseLeisurePreference;
    AccountApiContract.CustomerResponse emptyCustomerResponse;

    @Captor
    ArgumentCaptor<GuestDetailsFormDataInput> guestDetailsFormDataInputCaptor;
    @Captor
    ArgumentCaptor<List<GuestDetailsFormDataInput>> guestDetailsFormDataInputListCaptor;
    @Captor
    ArgumentCaptor<Boolean> manualAddressSectionVisibilityCaptor;
    @Mock
    TrackingAnalytics trackingAnalyticsMock;
    @Mock
    StringResourceProvider stringResourceProvider;

    private GuestDetailsPresenter presenter;
    private CompositeDisposable viewCompositeDisposable = new CompositeDisposable();
    private List<RoomBooking> roomList = new ArrayList<>();
    private List<CountryDomain> countries = createFakeCountries("SRB", "PL", "GB", "ME");
    private final String hotelCodeForOpera = "LONHOL";
    Customer aCustomer = CustomerFixture.INSTANCE.aCustomer();
    private static final String HOTEL_CODE = "LONBLA";
    private ReviewBookingInput reviewBookingInput;
    private List<GuestDetailsFormDataInput> guestDetailsFormDataInputs = Collections.singletonList(bookerDetailsMock);
    private PaymentDetailsInput paymentDetailsInput;

    private PaymentMethodsGraphQLContract.PaymentMethodsData paymentMethodsDonAndBookingConfData;
    private PaymentMethodsGraphQLContract.PaymentMethodsData paymentMethodsAndBookingConfSuccessOnly;
    private PaymentMethodsGraphQLContract.PaymentMethodsData paymentMethodsUnavailable;
    private PaymentMethodsGraphQLContract.PaymentMethodsData bookingConfirmationUnavailable;

    private DateTimeFormatter dateFormatter;
    private boolean isBookerStaying = false;
    private String countryCode = "GB";

    private String countryIsoCode = "GB";

    private String rateCode = "FLEX RATE";
    private String rateName = "flex";
    private String rateDescription = "This is the Rate Description";
    private String postcode = "EC1A 2HB";
    private String addressLine1 = "120 High Holborn";
    private String addressLine2 = "Chancery Lane";
    private String addressLine3 = "London";

    private static final String TEST_EMAIL = "james@bond.com";
    private static final String BRAND_CODE = "PINN";


    private CreateReservationGuestGraphQLContract.CreateReservationGuestData createReservationGuestData;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        roomList.add(bookingRoomMock);
        when(bookingFlowInputMock.roomBookings()).thenReturn(roomList);
        when(bookingFlowInputMock.hotelCode()).thenReturn(HOTEL_CODE);
        when(bookingFlowInputMock.chosenRate()).thenReturn(selectedRate);
        when(bookingFlowInputMock.basketReference()).thenReturn("123");
        when(selectedRate.code()).thenReturn(rateCode);
        when(selectedRate.description()).thenReturn(rateDescription);
        when(selectedRate.rateName()).thenReturn(rateName);

        when(viewMock.getBookerForm()).thenReturn(Observable.empty());
        when(viewMock.getRoomFormWithTextAndFocus()).thenReturn(Observable.empty());
        when(viewMock.getAddressFormWithTextAndFocus()).thenReturn(Observable.empty());
        when(viewMock.onClickEnterAddressManual()).thenReturn(Observable.just(Unit.INSTANCE));
        when(viewMock.onToggleButtonAddressChange()).thenReturn(Observable.empty());
        when(viewMock.onCheckNotStaying()).thenReturn(Observable.empty());
        when(viewMock.onClickContinueButton()).thenReturn(Observable.empty());
        when(viewMock.onTripTypeChange()).thenReturn(Observable.never());
        when(viewMock.onMarketingOptIn()).thenReturn(Observable.never());
        when(getCustomer.invoke()).thenReturn(Single.never());
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.never());
        when(getStringResource.invoke(GDPR_PRIVACY_FOOTER)).thenReturn("privacy footer");
        when(getStringResource.invoke(GDPR_GUEST_DETAILS_PRIVACY)).thenReturn("guest details privacy");
        when(deviceLocaleProvider.getDeviceLanguage()).thenReturn(LANGUAGE_ENGLISH);
        when(getCountriesMock.fetchCountriesFromSharedPref()).thenReturn(countries);
        when(viewMock.isAcceptablePassword()).thenReturn(Observable.just(false));
        when(viewMock.getCreateAccountPassword()).thenReturn(Observable.just("Pirate"));
        when(persistenceManager.getPaymentProvider()).thenReturn(String.valueOf(PaymentProvider.THREE_C_P));

        paymentDetailsInput = PaymentDetailsInput.builder()
                .bookingFlowInput(bookingFlowInputMock)
                .address(BookingAddress.create(countryIsoCode, countryCode, addressLine1, addressLine2, addressLine3, postcode))
                .bookerDetails(bookerDetailsMock)
                .guestDetailsList(guestDetailsFormDataInputs)
                .isBusinessTrip(false)
                .isTaxExempt(false)
                .marketingOptIn(true)
                .isBookerStaying(isBookerStaying).build();

            String cardUrl = "";
            String cardNumber = "4444333322221111";
            String nameOnCard = "lolol";
            String expiryDate = "0425";
            String startDate = null;
            String issueNumber = null;
            boolean marketingOptIn = false;

            reviewBookingInput = ReviewBookingInput.builder()
                    .paymentDetailsInput(paymentDetailsInput)
                    .cardHolderAddress(bookingAddress)
                    .paymentDetailsInput(paymentDetailsInput)
                    .cardUrl(cardUrl)
                    .cardNumber(cardNumber)
                    .cardInfo(cardInfoMock)
                    .nameOnCard(nameOnCard)
                    .expiryDate(expiryDate)
                    .startDate(startDate)
                    .userSelectedPaymentChoice(PaymentTimingChoice.PAY_NOW)
                    .cardType("VI")
                    .issueNumber(issueNumber)
                    .marketingOptIn(marketingOptIn).build();


        successCustomerResponse
                = InstanceFactory.create(AccountApiContract.CustomerResponse.class, "apiTest/customer-success.json");
        emptyCustomerResponse
                = InstanceFactory.create(AccountApiContract.CustomerResponse.class, "apiTest/customer-empty-sp-response.json");
        successCustomerResponseBusinessPreference
                = InstanceFactory.create(AccountApiContract.CustomerResponse.class, "apiTest/customer-success-business-pref.json");
        successCustomerResponseLeisurePreference
                = InstanceFactory.create(AccountApiContract.CustomerResponse.class, "apiTest/customer-success-leisure-pref.json");
        successCustomerResponseNoPaymentCard = InstanceFactory.create(AccountApiContract.CustomerResponse.class,
                "apiTest/customer-success-no-payment-card.json");
        successCustomerResponseBusinessCard = InstanceFactory.create(AccountApiContract.CustomerResponse.class,
                "apiTest/customer-success-business-card.json");

        paymentMethodsDonAndBookingConfData = InstanceFactory.create(PaymentMethodsGraphQLContract.PaymentMethodsData.class,
                "apiTest/paymentmethods/payment_methods_donation_and_booking_confirmation_success_gql.json");

        paymentMethodsAndBookingConfSuccessOnly = InstanceFactory.create(PaymentMethodsGraphQLContract.PaymentMethodsData.class,
                "apiTest/paymentmethods/payment_methods_and_booking_confirmation_success_donation_unsuccessful_gql.json");

        paymentMethodsUnavailable = InstanceFactory.create(PaymentMethodsGraphQLContract.PaymentMethodsData.class,
                "apiTest/paymentmethods/payment_methods_not_available_gql.json");

        bookingConfirmationUnavailable = InstanceFactory.create(PaymentMethodsGraphQLContract.PaymentMethodsData.class,
                "apiTest/paymentmethods/booking_confirmation_not_available_gql.json");

        dateFormatter = DateTimeFormatter.ofPattern(DateFormat.SLASHED_YEAR_MONTH_DAY);

        presenter = new GuestDetailsPresenter(getStringResource, getCountriesMock,
                getCustomer, isCustomerLoggedIn,
                viewCompositeDisposable, trackingAnalyticsMock, stringResourceProvider, persistenceManager,
                deviceLocaleProvider, graphQLGuestDetailsUseCase, anonymousNewsletterPreferencesUseCase);
        presenter.initParams(bookingFlowInputMock);

        createReservationGuestData = InstanceFactory.create(CreateReservationGuestGraphQLContract.CreateReservationGuestData.class,
                "apiTest/graphql/create_reservation_guest_success_gql.json");
    }

    @Test
    public void testDisplayAddress() {
        String postcode = "postcode";
        String line1 = "line1";
        String line2 = "line2";
        String line4 = "line4";
        String company = "company";
        when(postcodeAddressMock.getPostcode()).thenReturn(postcode);
        when(postcodeAddressMock.getLine1()).thenReturn(line1);
        when(postcodeAddressMock.getLine2()).thenReturn(line2);
        when(postcodeAddressMock.getLine4()).thenReturn(line4);
        when(postcodeAddressMock.getCompanyName()).thenReturn(company);

        presenter.attachView(viewMock);
        presenter.displayAddress(postcodeAddressMock);

        verify(viewMock, times(3)).showManualAddressSection(true);
        verify(viewMock).setAddressFields(AddressField.builder()
                .postcode(postcode)
                .addressLine1(line1)
                .addressLine2(line2)
                .city(line4)
                .companyName(company).build());
    }

    @Test
    public void testOnClickContinueGuestStayingAllFailing() {
        when(bookingFlowInputMock.hotelCode()).thenReturn(hotelCodeForOpera);
        when(bookingFlowInputMock.basketReference()).thenReturn("123");
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(Boolean.FALSE));
        when(viewMock.onCheckNotStaying()).thenReturn(Observable.just(false));
        when(viewMock.onClickContinueButton()).thenReturn(Observable.just(Unit.INSTANCE));
        when(graphQLGuestDetailsUseCase.createReservationGuest(any()))
                .thenReturn(Single.just(GraphQLCreateReservationGuestMapperKt.mapToCreateReservationGuestGQL(createReservationGuestData)));

        initPresenter();

        presenter.attachView(viewMock);

        verify(viewMock, times(3)).showManualAddressSection(true);
        verify(viewMock).showBookerValidationError(true, FIRST_NAME);
        verify(viewMock).showBookerValidationError(true, LAST_NAME);
        verify(viewMock).showBookerValidationError(true, CONTACT_NUMBER);
        verify(viewMock).showBookerValidationError(true, EMAIL);
        verify(viewMock).showAddressValidationError(true, POSTCODE);
        verify(viewMock).showAddressValidationError(true, ADDRESS_LINE_1);
    }

    @Test
    public void testOnClickContinueGuestStayingAllOK() {
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(Boolean.FALSE));
        when(getCustomer.invoke()).thenReturn(Single.just(DomainMappers.toDomain(successCustomerResponse)));
        when(graphQLGuestDetailsUseCase.createReservationGuest(any()))
                .thenReturn(Single.just(GraphQLCreateReservationGuestMapperKt.mapToCreateReservationGuestGQL(createReservationGuestData)));

        initPresenter();

        populateFormsWithValidDetails();

        presenter.attachView(viewMock);

        verify(viewMock, times(2)).showBookerValidationError(false, FIRST_NAME);
        verify(viewMock, times(2)).showBookerValidationError(false, LAST_NAME);
        verify(viewMock, times(2)).showBookerValidationError(false, CONTACT_NUMBER);
        verify(viewMock, times(2)).showBookerValidationError(false, EMAIL);
        verify(viewMock).enableContinueButton(true);
    }

    @Test
    public void testOnClickContinueGuestNotStayingAllFailingExceptEmail() {
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(Boolean.FALSE));
        when(getCustomer.invoke()).thenReturn(Single.just(DomainMappers.toDomain(successCustomerResponse)));

        initPresenter();

        when(viewMock.onCheckNotStaying()).thenReturn(Observable.just(true));
        when(viewMock.onClickContinueButton()).thenReturn(Observable.just(Unit.INSTANCE));
        when(graphQLGuestDetailsUseCase.createReservationGuest(any()))
                .thenReturn(Single.just(GraphQLCreateReservationGuestMapperKt.mapToCreateReservationGuestGQL(createReservationGuestData)));

        presenter.attachView(viewMock);

        verify(viewMock).showBookerValidationError(true, FIRST_NAME);
        verify(viewMock).showBookerValidationError(true, LAST_NAME);
        verify(viewMock).showBookerValidationError(true, CONTACT_NUMBER);
        verify(viewMock).showBookerValidationError(true, EMAIL);

        verify(viewMock).updateRoomFormList(guestDetailsFormDataInputCaptor.capture(), eq(0));
        GuestDetailsFormDataInput guestDetailsFormDataInput = guestDetailsFormDataInputCaptor.getValue();
        assertTrue(guestDetailsFormDataInput.errorFirstName());
        assertTrue(guestDetailsFormDataInput.errorLastName());
        assertFalse(guestDetailsFormDataInput.errorEmail());
    }

    @Test
    public void testOnClickContinueGuestInvalidEmailFormatError() {
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(Boolean.FALSE));
        when(getCustomer.invoke()).thenReturn(Single.just(DomainMappers.toDomain(successCustomerResponse)));

        initPresenter();

        when(viewMock.onCheckNotStaying()).thenReturn(Observable.just(true));
        when(viewMock.onClickContinueButton()).thenReturn(Observable.just(Unit.INSTANCE));

        when(viewMock.getRoomFormWithTextAndFocus()).thenReturn(Observable.just(
                RoomGuestDetailsData.create(GuestDetailsFormDataOutput.create("james", false, FIRST_NAME), 0),
                RoomGuestDetailsData.create(GuestDetailsFormDataOutput.create("bond", false, LAST_NAME), 0),
                RoomGuestDetailsData.create(GuestDetailsFormDataOutput.create("invalidemail", false, EMAIL), 0)
        ));

        presenter.attachView(viewMock);

        verify(viewMock, times(3)).updateRoomFormList(guestDetailsFormDataInputCaptor.capture(), eq(0));
        GuestDetailsFormDataInput guestDetailsFormDataInput = guestDetailsFormDataInputCaptor.getValue();
        assertTrue(guestDetailsFormDataInput.errorEmail());
    }

    @Test
    public void testOnClickContinueGuestInfoValidNoTripTypeSelection() {
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(Boolean.FALSE));
        when(getCustomer.invoke()).thenReturn(Single.just(DomainMappers.toDomain(successCustomerResponse)));

        initPresenter();

        when(viewMock.onCheckNotStaying()).thenReturn(Observable.just(true));
        when(viewMock.onClickContinueButton()).thenReturn(Observable.just(Unit.INSTANCE));
        when(viewMock.getTripTypeSelection()).thenReturn(TRIP_TYPE_UNSELECTED);

        when(viewMock.getRoomFormWithTextAndFocus()).thenReturn(Observable.just(
                RoomGuestDetailsData.create(GuestDetailsFormDataOutput.create("james", false, FIRST_NAME), 0),
                RoomGuestDetailsData.create(GuestDetailsFormDataOutput.create("bond", false, LAST_NAME), 0),
                RoomGuestDetailsData.create(GuestDetailsFormDataOutput.create("my_email@gmail.com", false, EMAIL), 0)
        ));

        presenter.attachView(viewMock);

        verify(viewMock, times(1)).showTripTypeSelectionError();
    }

    @Test
    public void testOnClickContinueGuestNotStayingAllOK() {
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(Boolean.FALSE));
        when(getCustomer.invoke()).thenReturn(Single.just(DomainMappers.toDomain(successCustomerResponse)));
        when(graphQLGuestDetailsUseCase.createReservationGuest(any()))
                .thenReturn(Single.just(GraphQLCreateReservationGuestMapperKt.mapToCreateReservationGuestGQL(createReservationGuestData)));

        initPresenter();

        when(viewMock.onCheckNotStaying()).thenReturn(Observable.just(true));
        setValuesForBookerRoomAndAddress();
        when(viewMock.onClickContinueButton()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(viewMock);

        verify(viewMock, times(2)).showBookerValidationError(false, FIRST_NAME);
        verify(viewMock, times(2)).showBookerValidationError(false, LAST_NAME);
        verify(viewMock, times(2)).showBookerValidationError(false, CONTACT_NUMBER);
        verify(viewMock, times(2)).showBookerValidationError(false, EMAIL);

        verify(viewMock, times(4)).updateRoomFormList(guestDetailsFormDataInputCaptor.capture(), eq(0));
        GuestDetailsFormDataInput guestDetailsFormDataInput = guestDetailsFormDataInputCaptor.getValue();
        assertFalse(guestDetailsFormDataInput.errorFirstName());
        assertFalse(guestDetailsFormDataInput.errorLastName());
        assertFalse(guestDetailsFormDataInput.errorEmail());

    }

    @Test
    public void testOnClickContinueWithAddressValid() {
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(Boolean.FALSE));
        when(getCustomer.invoke()).thenReturn(Single.never());
        when(graphQLGuestDetailsUseCase.createReservationGuest(any()))
                .thenReturn(Single.just(GraphQLCreateReservationGuestMapperKt.mapToCreateReservationGuestGQL(createReservationGuestData)));

        initPresenter();

        populateFormsWithValidDetails();

        presenter.attachView(viewMock);

        verify(viewMock, times(2)).showBookerValidationError(false, FIRST_NAME);
        verify(viewMock, times(2)).showBookerValidationError(false, LAST_NAME);
        verify(viewMock, times(2)).showBookerValidationError(false, CONTACT_NUMBER);
        verify(viewMock, times(2)).showBookerValidationError(false, EMAIL);
        verify(viewMock, times(4)).showManualAddressSection(true);
        verify(viewMock, times(2)).showAddressValidationError(false, POSTCODE);
        verify(viewMock, times(2)).showAddressValidationError(false, ADDRESS_LINE_1);

    }

    @Test
    public void testBookerStayingWithValidBookerAddsBookerToRoomsList() {
        String firstName = "james";
        String lastName = "bond";
        String number = "08888888888";
        String email = "james@bond.com";
        when(viewMock.getBookerForm()).thenReturn(Observable.just(
                GuestDetailsFormDataOutput.create(firstName, false, FIRST_NAME),
                GuestDetailsFormDataOutput.create(lastName, false, LAST_NAME),
                GuestDetailsFormDataOutput.create(number, false, CONTACT_NUMBER),
                GuestDetailsFormDataOutput.create(email, false, EMAIL)
        ));
        presenter.attachView(viewMock);

        verify(viewMock, times(5)).setupRoomFormList(guestDetailsFormDataInputListCaptor.capture(), eq(true));
        GuestDetailsFormDataInput guestDetails = guestDetailsFormDataInputListCaptor.getValue().get(0);

        if (guestDetailsFormDataInputListCaptor.getValue().size() <= 1) {
            verify(viewMock).showSharingGuestDetailsPrivacyMessage(false);
        }

        assertEquals(guestDetails.firstName(), firstName);
        assertEquals(guestDetails.lastName(), lastName);
        assertEquals(guestDetails.email(), email);
    }

    @Test
    public void testTurningBookerStayingOffRemovesBooker() {
        String firstName = "james";
        String lastName = "bond";
        String number = "08888888888";
        String email = "james@bond.com";
        Relay<Boolean> check = PublishRelay.create();
        when(viewMock.getBookerForm()).thenReturn(Observable.just(
                GuestDetailsFormDataOutput.create(firstName, false, FIRST_NAME),
                GuestDetailsFormDataOutput.create(lastName, false, LAST_NAME),
                GuestDetailsFormDataOutput.create(number, false, CONTACT_NUMBER),
                GuestDetailsFormDataOutput.create(email, false, EMAIL)
        ));
        when(viewMock.onCheckNotStaying()).thenReturn(check);
        presenter.attachView(viewMock);

        verify(viewMock, times(5)).setupRoomFormList(guestDetailsFormDataInputListCaptor.capture(), eq(true));
        GuestDetailsFormDataInput guestDetails = guestDetailsFormDataInputListCaptor.getValue().get(0);
        assertEquals(firstName, guestDetails.firstName());
        assertEquals(lastName, guestDetails.lastName());
        assertEquals(email, guestDetails.email());

        check.accept(true);

        verify(viewMock).showSharingGuestDetailsPrivacyMessage(true);
        verify(viewMock).setupRoomFormList(guestDetailsFormDataInputListCaptor.capture(), eq(false));
        GuestDetailsFormDataInput guestDetailsCleared = guestDetailsFormDataInputListCaptor.getValue().get(0);
        assertEquals("", guestDetailsCleared.firstName());
        assertEquals("", guestDetailsCleared.lastName());
        assertEquals("", guestDetailsCleared.email());
    }

    @Test
    public void testOnContextToggleButtonAddressChange() {
        when(viewMock.onToggleButtonAddressChange()).thenReturn(Observable.just(
                ToggleButtonView.State.RIGHT
        ));
        presenter.attachView(viewMock);
        verify(viewMock, times(2)).showCompanyAddress(true);
        presenter.detachView();

        when(viewMock.onToggleButtonAddressChange()).thenReturn(Observable.just(
                ToggleButtonView.State.LEFT
        ));
        presenter.attachView(viewMock);
        verify(viewMock, times(2)).showCompanyAddress(false);
    }

    @Test
    public void testOnClickFindAddress() {
        presenter.attachView(viewMock);
    }

    @Test
    public void testAttachView() {
        presenter.attachView(viewMock);

        verify(viewMock).setupToolbar();
        verify(viewMock).setIsStayingState(true);
        verify(viewMock).setupRoomFormList(guestDetailsFormDataInputListCaptor.capture(), eq(true));
        verify(viewMock).showSharingGuestDetailsPrivacyMessage(guestDetailsFormDataInputListCaptor.getValue().size() > 1);

        verify(viewMock, times(2)).showManualAddressSection(manualAddressSectionVisibilityCaptor.capture());
        assertEquals(1, guestDetailsFormDataInputListCaptor.getValue().size());
    }

    @Test
    public void testLifecycle() {
        assertFalse(presenter.isViewAttached());
        assertEquals(0, viewCompositeDisposable.size());
        presenter.attachView(viewMock);
        assertTrue(presenter.isViewAttached());
        assertEquals(14, viewCompositeDisposable.size());
        presenter.detachView();
        assertFalse(presenter.isViewAttached());
        assertEquals(0, viewCompositeDisposable.size());
        presenter.destroy();
    }

    @Test
    public void successCustomerResponse() {
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(Boolean.TRUE));
        when(getCustomer.invoke()).thenReturn(Single.just(DomainMappers.toDomain(successCustomerResponse)));
        when(viewMock.onToggleButtonAddressChange()).thenReturn(Observable.never());
        when(viewMock.onCheckNotStaying()).thenReturn(Observable.never());
        when(viewMock.onClickContinueButton()).thenReturn(Observable.never());
        when(viewMock.onClickEnterAddressManual()).thenReturn(Observable.never());
        when(viewMock.getAddressFormWithTextAndFocus()).thenReturn(Observable.never());
        when(viewMock.getRoomFormWithTextAndFocus()).thenReturn(Observable.never());
        when(viewMock.getBookerForm()).thenReturn(Observable.never());
        when(viewMock.onMarketingOptIn()).thenReturn(Observable.never());
        when(getCountriesMock.fetchCountriesFromSharedPref()).thenReturn(new ArrayList<>());
        when(persistenceManager.getCustomer()).thenReturn(DomainMappers.toDomain(successCustomerResponse));

        initPresenter();

        presenter.attachView(viewMock);

        verify(viewMock, times(1)).showProgressLoading(eq(true));
        verify(viewMock, times(1)).showProgressLoading(eq(false));

        verify(viewMock, times(2)).setBookerDetails(any());
        verify(viewMock, times(1)).setAddressFields(any());

        verify(viewMock, times(1)).showPostcode(eq(true));
        verify(viewMock, times(1)).showManualAddressSection(eq(true));
    }

    @Test
    public void shouldShowHeaderAndFooterGdprContentAndLoadSharingGuestDetailOne() {
        presenter.onAttachView(viewMock);
        verify(viewMock).showPrivacyGenericFooterContent("privacy footer");
        verify(viewMock).loadSharingGuestDetailsPrivacyMessage("guest details privacy");
    }

    @Test
    public void getCustomerFails_startLoginActivityExpected() {
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(Boolean.TRUE));
        when(getCustomer.invoke()).thenReturn(Single.error(new NoLongerValidCredentials(new Exception())));
        when(persistenceManager.getCustomer()).thenReturn(DomainMappers.toDomain(emptyCustomerResponse));

        initPresenter();

        presenter.attachView(viewMock);

        verify(viewMock).showForceLoginMessage();
        verify(viewMock).startLogInActivity();
    }

    @Test
    public void whenCityTaxForBusinessIsFalse_andCityTaxForLeisureIsFalse_hideCityTaxAlertBanner() {
        when(bookingFlowInputMock.cityTaxForLeisure()).thenReturn(false);
        when(bookingFlowInputMock.cityTaxForBusiness()).thenReturn(false);

        presenter.attachView(viewMock);

        verify(viewMock).showCityTaxAlertBanner(null);
    }

    @Test
    public void whenCityTaxForBusinessIsTrue_andCityTaxForLeisureIsTrue_showCityTaxAlertBanner() {
        when(bookingFlowInputMock.cityTaxForLeisure()).thenReturn(true);
        when(bookingFlowInputMock.cityTaxForBusiness()).thenReturn(true);

        presenter.attachView(viewMock);

        verify(viewMock).showCityTaxAlertBanner(any());
    }

    @Test
    public void whenCityTaxForBusinessIsFalse_andCityTaxForLeisureIsTrue_showCityTaxAlertBanner() {
        when(bookingFlowInputMock.cityTaxForLeisure()).thenReturn(true);
        when(bookingFlowInputMock.cityTaxForBusiness()).thenReturn(false);

        presenter.attachView(viewMock);

        verify(viewMock).showCityTaxAlertBanner(any());
    }

    @Test
    public void whenCityTaxForBusinessIsTrue_andCityTaxForLeisureIsFalse_showCityTaxAlertBanner() {
        when(bookingFlowInputMock.cityTaxForLeisure()).thenReturn(false);
        when(bookingFlowInputMock.cityTaxForBusiness()).thenReturn(true);

        presenter.attachView(viewMock);

        verify(viewMock).showCityTaxAlertBanner(any());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void populateFormsWithValidDetails() {
        populateBookerFormWithValidDetails();
        populateAddressFormWithValidDetails();

        when(viewMock.onCheckNotStaying()).thenReturn(Observable.just(false));
        when(viewMock.onMarketingOptIn()).thenReturn(Observable.just(false));
        when(viewMock.onClickContinueButton()).thenReturn(Observable.just(Unit.INSTANCE));
    }

    private void setValuesForBookerRoomAndAddress() {
        when(viewMock.onClickEnterAddressManual()).thenReturn(Observable.just(Unit.INSTANCE));

        populateBookerFormWithValidDetails();
        when(viewMock.getRoomFormWithTextAndFocus()).thenReturn(Observable.just(
                RoomGuestDetailsData.create(GuestDetailsFormDataOutput.create("james", false, FIRST_NAME), 0),
                RoomGuestDetailsData.create(GuestDetailsFormDataOutput.create("bond", false, LAST_NAME), 0),
                RoomGuestDetailsData.create(GuestDetailsFormDataOutput.create("james@bond.com", false, EMAIL), 0)
        ));
        populateAddressFormWithValidDetails();
    }

    private void populateBookerFormWithValidDetails() {
        when(viewMock.getBookerForm()).thenReturn(Observable.just(
                GuestDetailsFormDataOutput.create("james", false, FIRST_NAME),
                GuestDetailsFormDataOutput.create("bond", false, LAST_NAME),
                GuestDetailsFormDataOutput.create("08888888888", false, CONTACT_NUMBER),
                GuestDetailsFormDataOutput.create("james@bond.com", false, EMAIL)
        ));
    }

    private void populateAddressFormWithValidDetails() {
        when(viewMock.getAddressFormWithTextAndFocus()).thenReturn(Observable.just(
                AddressFormDataOutput.create(1, COUNTRY),
                AddressFormDataOutput.create("E146FY", false, POSTCODE),
                AddressFormDataOutput.create("address line bla", false, ADDRESS_LINE_1),
                AddressFormDataOutput.create("asdasd", false, ADDRESS_LINE_2),
                AddressFormDataOutput.create("Porto", false, ADDRESS_LINE_3)
        ));
    }

    @Test
    public void testIfNoLoggedInUserThenReturnIndexOfUK() {
        List<CountryDomain> countriesMock = createFakeCountries("SRB", "PL", "GB", "ME");
        when(getCountriesMock.fetchCountriesFromSharedPref()).thenReturn(countriesMock);
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(Boolean.FALSE));
        TestObserver<Pair<List<CountryDomain>, Integer>> testObserver = presenter.countriesWithSelectedPosition().test();
        testObserver.assertValue(pair -> pair.getSecond() == 2);
    }

    @Test
    public void testIfNoLoggedInUserAndNoUKInTheListOfCountriesThenReturnMinusOne() {
        List<CountryDomain> countriesMock = createFakeCountries("SRB", "PL", "ME");
        when(getCountriesMock.fetchCountriesFromSharedPref()).thenReturn(countriesMock);
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(Boolean.FALSE));
        TestObserver<Pair<List<CountryDomain>, Integer>> testObserver = presenter.countriesWithSelectedPosition().test();
        testObserver.assertValue(pair -> pair.getSecond() == -1);
    }

    @Test
    public void testIfUserLoggedInThenReturnIndexOfTheirCountry() {
        List<CountryDomain> countriesMock = createFakeCountries("SRB", "RO", "HU", "PL", "GB", "ME");
        when(getCountriesMock.fetchCountriesFromSharedPref()).thenReturn(countriesMock);
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(Boolean.TRUE));
        when(getCustomer.invoke()).thenReturn(Single.just(mockCustomersCountryCode("PL")));

        initPresenter();

        TestObserver<Pair<List<CountryDomain>, Integer>> testObserver = presenter.countriesWithSelectedPosition().test();
        testObserver.assertValue(pair -> pair.getSecond() == 3); //todo fix - it was -1
    }

    @Test
    public void testIfUserLoggedInThenAndTheirCountryIsUkButItsNotInTheListThenReturnMinusOne() {
        List<CountryDomain> countriesMock = createFakeCountries("SRB", "RO", "ME");
        when(getCountriesMock.fetchCountriesFromSharedPref()).thenReturn(countriesMock);
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(Boolean.TRUE));
        when(getCustomer.invoke()).thenReturn(Single.just(mockCustomersCountryCode("GB")));
        initPresenter();
        TestObserver<Pair<List<CountryDomain>, Integer>> testObserver = presenter.countriesWithSelectedPosition().test();
        testObserver.assertValue(pair -> pair.getSecond() == -1);
    }

    @Test
    public void testIfUserLoggedInButApiCallFailsThenReturnIndexOfUk() {
        List<CountryDomain> countriesMock = createFakeCountries("SRB", "RO", "GB", "ME");
        when(getCountriesMock.fetchCountriesFromSharedPref()).thenReturn(countriesMock);
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(Boolean.TRUE));
        when(getCustomer.invoke()).thenReturn(Single.error(new Throwable()));
        initPresenter();
        TestObserver<Pair<List<CountryDomain>, Integer>> testObserver = presenter.countriesWithSelectedPosition().test();
        testObserver.assertValue(pair -> pair.getSecond() == 2);
    }

    @Test
    public void testIfUserLoggedInButApiCallFailsAndUkNotInTheListThenReturnMinus1() {
        List<CountryDomain> countriesMock = createFakeCountries("SRB", "RO", "ME");
        when(getCountriesMock.fetchCountriesFromSharedPref()).thenReturn(countriesMock);
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(Boolean.TRUE));
        when(getCustomer.invoke()).thenReturn(Single.error(new Throwable()));
        initPresenter();
        TestObserver<Pair<List<CountryDomain>, Integer>> testObserver = presenter.countriesWithSelectedPosition().test();
        testObserver.assertValue(pair -> pair.getSecond() == -1);
    }

    @Test
    public void testIfUserLoggedInAndTheirCountryNotInTheListOfCountriesThenReturnIndexOfUk() {
        List<CountryDomain> countriesMock = createFakeCountries("SRB", "RO", "PL", "GB", "ME");
        when(getCountriesMock.fetchCountriesFromSharedPref()).thenReturn(countriesMock);
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(Boolean.TRUE));
        when(getCustomer.invoke()).thenReturn(Single.just(mockCustomersCountryCode("HU")));

        initPresenter();

        TestObserver<Pair<List<CountryDomain>, Integer>> testObserver = presenter.countriesWithSelectedPosition().test();
        testObserver.assertValue(pair -> pair.getSecond() == 3);
    }

    @Test
    public void testIfUserLoggedInAndTheirCountryAndUkNotInTheListOfCountriesThenReturnMinusOne() {
        List<CountryDomain> countriesMock = createFakeCountries("SRB", "RO", "PL", "ME");
        when(getCountriesMock.fetchCountriesFromSharedPref()).thenReturn(countriesMock);
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(Boolean.TRUE));
        when(getCustomer.invoke()).thenReturn(Single.just(mockCustomersCountryCode("HU")));
        initPresenter();
        TestObserver<Pair<List<CountryDomain>, Integer>> testObserver = presenter.countriesWithSelectedPosition().test();
        testObserver.assertValue(pair -> pair.getSecond() == -1);
    }


    private List<CountryDomain> createFakeCountries(String... countryCodes) {
        List<CountryDomain> countries = new ArrayList<>();
        for (String countryCode : countryCodes) {
            countries.add(new CountryDomain(countryCode, countryCode, "fake", false, ""));
        }
        return countries;
    }

    private com.whitbread.premierinn.domain.customer.entity.Customer mockCustomersCountryCode(String countryCode) {
        return Customer.emptyCustomer(countryCode);
    }

    /************************** Opera tests ************************/

    @Test
    public void testIfCreateReservationGuestSendsFailedResponse_forOpera() {
        when(bookingFlowInputMock.hotelCode()).thenReturn(hotelCodeForOpera);
        when(bookingFlowInputMock.basketReference()).thenReturn("123");
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(Boolean.FALSE));
        when(viewMock.onClickContinueButton()).thenReturn(Observable.just(Unit.INSTANCE));
        when(graphQLGuestDetailsUseCase.createReservationGuest(any()))
                .thenReturn(Single.error(new Exception()));

        initPresenter();

        presenter.attachView(viewMock);

        verify(viewMock).showToastGraphQlError(anyString());
        verify(viewMock).showToastGenericError();
    }

    @Test
    public void testIfCreateReservationGuestIsSuccess_thenReviewAndBookIsLaunched_guestUser() {
        // Guest user (not logged in) - anonymous newsletter preferences SHOULD be called
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(Boolean.FALSE));
        when(getCustomer.invoke()).thenReturn(Single.just(DomainMappers.toDomain(successCustomerResponse)));
        when(graphQLGuestDetailsUseCase.createReservationGuest(any()))
                .thenReturn(Single.just(GraphQLCreateReservationGuestMapperKt.mapToCreateReservationGuestGQL(createReservationGuestData)));

        setupAnonymousNewsletterPreferencesMock();

        initPresenter();

        populateFormsWithValidDetails();

        presenter.attachView(viewMock);

        verify(viewMock).enableContinueButton(true);
        verify(viewMock, times(2)).showLoading(true);
        verify(viewMock).startReviewBookActivity(any());
        // Verify anonymous newsletter preferences WAS called for guest user when email was entered
        verify(anonymousNewsletterPreferencesUseCase, atLeast(1)).getAnonymousNewsletterPreferences(
                TEST_EMAIL, BRAND_CODE, countryCode, LANGUAGE_ENGLISH_DOMAIN);
    }

    @Test
    public void testIfCreateReservationGuestIsSuccess_thenReviewAndBookIsLaunched_loggedInUser() {
        // Logged-in user - anonymous newsletter preferences SHOULD also be called
        when(isCustomerLoggedIn.invoke()).thenReturn(Single.just(Boolean.TRUE));
        when(getCustomer.invoke()).thenReturn(Single.just(DomainMappers.toDomain(successCustomerResponse)));
        when(graphQLGuestDetailsUseCase.createReservationGuest(any()))
                .thenReturn(Single.just(GraphQLCreateReservationGuestMapperKt.mapToCreateReservationGuestGQL(createReservationGuestData)));
        when(persistenceManager.getCustomer()).thenReturn(DomainMappers.toDomain(successCustomerResponse));

        setupAnonymousNewsletterPreferencesMock();

        initPresenter();

        populateFormsWithValidDetails();

        presenter.attachView(viewMock);

        verify(viewMock).enableContinueButton(true);
        verify(viewMock, times(2)).showLoading(true);
        verify(viewMock).startReviewBookActivity(any());
        // Verify anonymous newsletter preferences WAS also called for logged-in user when email was entered
        verify(anonymousNewsletterPreferencesUseCase, atLeast(1)).getAnonymousNewsletterPreferences(
                TEST_EMAIL, BRAND_CODE, countryCode, LANGUAGE_ENGLISH_DOMAIN);
    }

    @Test
    public void onTripTypeChange_notStayingForWork_andCityTaxForLeisureIsTrue_andCityTaxForBusinessIsFalse_showInfoBannerOpera() {
        when(viewMock.onTripTypeChange()).thenReturn(Observable.just(TRIP_TYPE_LEISURE));
        when(bookingFlowInputMock.cityTaxForLeisure()).thenReturn(true);
        when(bookingFlowInputMock.cityTaxForBusiness()).thenReturn(false);

        initPresenter();

        presenter.attachView(viewMock);

        verify(viewMock).showCityTaxInfoBanner(any());
    }

    @Test
    public void onTripTypeChange_stayingForWork_andCityTaxForLeisureIsTrue_andCityTaxForBusinessIsFalse_showInfoBannerOpera() {
        when(viewMock.onTripTypeChange()).thenReturn(Observable.just(TRIP_TYPE_BUSINESS));
        when(bookingFlowInputMock.cityTaxForLeisure()).thenReturn(true);
        when(bookingFlowInputMock.cityTaxForBusiness()).thenReturn(false);

        initPresenter();

        presenter.attachView(viewMock);

        verify(viewMock).showCityTaxInfoBanner(null);
    }

    @Test
    public void onTripTypeChange_notStayingForWork_andCityTaxForLeisureIsFalse_andCityTaxForBusinessIsTrue_showInfoBannerOpera() {
        when(viewMock.onTripTypeChange()).thenReturn(Observable.just(TRIP_TYPE_LEISURE));
        when(bookingFlowInputMock.cityTaxForLeisure()).thenReturn(false);
        when(bookingFlowInputMock.cityTaxForBusiness()).thenReturn(true);

        initPresenter();

        presenter.attachView(viewMock);

        verify(viewMock).showCityTaxInfoBanner(null);
    }

    @Test
    public void onTripTypeChange_StayingForWork_andCityTaxForLeisureIsFalse_andCityTaxForBusinessIsTrue_showInfoBannerOpera() {
        when(viewMock.onTripTypeChange()).thenReturn(Observable.just(TRIP_TYPE_BUSINESS));
        when(bookingFlowInputMock.cityTaxForLeisure()).thenReturn(false);
        when(bookingFlowInputMock.cityTaxForBusiness()).thenReturn(true);

        initPresenter();

        presenter.attachView(viewMock);

        verify(viewMock).showCityTaxInfoBanner(any());
    }

    @Test
    public void onTripTypeChange_notStayingForWork_andCityTaxForLeisureIsTrue_andCityTaxForBusinessIsTrue_showInfoBannerOpera() {
        when(viewMock.onTripTypeChange()).thenReturn(Observable.just(TRIP_TYPE_LEISURE));
        when(bookingFlowInputMock.cityTaxForLeisure()).thenReturn(true);
        when(bookingFlowInputMock.cityTaxForBusiness()).thenReturn(true);

        initPresenter();

        presenter.attachView(viewMock);

        verify(viewMock).showCityTaxInfoBanner(any());
    }

    @Test
    public void onTripTypeChange_StayingForWork_andCityTaxForLeisureIsTrue_andCityTaxForBusinessIsTrue_showInfoBannerOpera() {
        when(viewMock.onTripTypeChange()).thenReturn(Observable.just(TRIP_TYPE_BUSINESS));
        when(bookingFlowInputMock.cityTaxForLeisure()).thenReturn(true);
        when(bookingFlowInputMock.cityTaxForBusiness()).thenReturn(true);

        initPresenter();

        presenter.attachView(viewMock);

        verify(viewMock).showCityTaxInfoBanner(any());
    }

    private void initPresenter() {
        presenter = new GuestDetailsPresenter(getStringResource, getCountriesMock,
                getCustomer,
                isCustomerLoggedIn, viewCompositeDisposable, trackingAnalyticsMock, stringResourceProvider, persistenceManager,
                deviceLocaleProvider, graphQLGuestDetailsUseCase, anonymousNewsletterPreferencesUseCase);
        presenter.initParams(bookingFlowInputMock);
    }

    private void setupAnonymousNewsletterPreferencesMock() {
        when(anonymousNewsletterPreferencesUseCase.getAnonymousNewsletterPreferences(
                anyString(), eq(BRAND_CODE), eq(countryCode), eq(LANGUAGE_ENGLISH_DOMAIN)))
                .thenReturn(Single.just(new AnonymousNewsletterPreferencesDomain(
                        false, false
                )));
        when(deviceLocaleProvider.getDeviceLanguage()).thenReturn(LANGUAGE_ENGLISH_DOMAIN);
    }
}

