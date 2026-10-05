package com.whitbread.premierinn.editguest;

import static com.whitbread.premierinn.data.common.Constants.LANGUAGE_ENGLISH;
import static com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput.Form.CONTACT_NUMBER;
import static com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput.Form.EMAIL;
import static com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput.Form.FIRST_NAME;
import static com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput.Form.LAST_NAME;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.whitbread.premierinn.common.analytics.AnalyticsConstants;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.guestdetails.adapter.RoomGuestDetailsData;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataInput;
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataOutput;
import com.whitbread.premierinn.utils.RxJavaTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;

@RunWith(MockitoJUnitRunner.class)
public class EditGuestPresenterTest {

    @Rule
    public RxJavaTestRule rxJavaTestRule = new RxJavaTestRule();

    @Mock private EditGuestPresenter.View view;
    @Mock private GuestDetailsFormDataInput input;
    @Mock private TrackingAnalytics analytics;
    @Mock private DeviceLocaleProvider deviceLocaleProvider;

    @Captor ArgumentCaptor<GuestDetailsFormDataInput> guestDetailsFormDataInputCaptor;

    private EditGuestPresenter editGuestPresenter;

    private CompositeDisposable viewCompositeDisposable = new CompositeDisposable();

    private EditGuestInput editGuestInput;

    private List<GuestDetailsFormDataInput> guestDetailsList = new ArrayList<>();

    @Before
    public void onSetup() {
        basicSetup();

        editGuestPresenter = new EditGuestPresenter(
                viewCompositeDisposable,
                analytics,
                deviceLocaleProvider
        );
        editGuestPresenter.initParams(editGuestInput);
    }

    @Test
    public void testLifecycle() {
        assertEquals(viewCompositeDisposable.size(), 0);
        editGuestPresenter.attachView(view);
        assertTrue(editGuestPresenter.isViewAttached());
        assertEquals(viewCompositeDisposable.size(), 3);
        editGuestPresenter.detachView();
        assertFalse(editGuestPresenter.isViewAttached());
        assertEquals(viewCompositeDisposable.size(), 0);
    }

    @Test
    public void testAttachViewWithBookerStaying() {
        editGuestInput = EditGuestInput.create(editGuestInput.guestDetailsFormDataInputs(),
                editGuestInput.bookerDetails(),
                true);
        editGuestPresenter = new EditGuestPresenter(
                viewCompositeDisposable,
                analytics,
                deviceLocaleProvider
        );
        editGuestPresenter.initParams(editGuestInput);
        editGuestPresenter.attachView(view);

        verify(view).displayGuestsForm(guestDetailsList, true);
        verify(analytics).track(AnalyticsConstants.ScreenState.GUEST_DETAILS, AnalyticsConstants.Type.BOOKING_FLOW);
    }

    @Test
    public void testAttachViewWithBookerNotStaying() {
        editGuestPresenter.attachView(view);
        verify(view).displayBookerAndGuestsForm(input, guestDetailsList);
        verify(analytics).track(AnalyticsConstants.ScreenState.GUEST_DETAILS, AnalyticsConstants.Type.BOOKING_FLOW);
    }

    @Test
    public void testOnClickUpdateDetailsAllValid() {
        when(view.getOnUpdateClick()).thenReturn(Observable.just(Unit.INSTANCE));
        populateValidBookerDetailsRoomFormMock();

        guestDetailsList.add(GuestDetailsFormDataInput.create("Mr", "asd", "asd", "valid@email.com", "075454654"));

        editGuestPresenter.attachView(view);
        verify(view).showBookerValidationError(false, FIRST_NAME);
        verify(view).showBookerValidationError(false, LAST_NAME);
        verify(view).showBookerValidationError(false, CONTACT_NUMBER);
        verify(view).showBookerValidationError(false, EMAIL);
        verify(view).updateRoomFormList(guestDetailsList.get(0), 0);
        verify(view).startReviewBookingActivity(EditGuestInput.create(guestDetailsList, input, false));
    }

    @Test
    public void testBookerStayingOnClickUpdateBookerDetailsNotValid() {
        editGuestInput = EditGuestInput.create(editGuestInput.guestDetailsFormDataInputs(),
                editGuestInput.bookerDetails(),
                true);
        when(view.getOnUpdateClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(input.firstName()).thenReturn("");
        when(input.lastName()).thenReturn("");
        when(input.phoneNumber()).thenReturn("asd");
        when(input.email()).thenReturn("invalid@com");

        guestDetailsList.add(GuestDetailsFormDataInput.create("Mr", "asd", "asd", "valid@email.com", "075454654"));

        editGuestPresenter.attachView(view);
        verify(view).showBookerValidationError(true, FIRST_NAME);
        verify(view).showBookerValidationError(true, LAST_NAME);
        verify(view).showBookerValidationError(true, CONTACT_NUMBER);
        verify(view).showBookerValidationError(true, EMAIL);
        verify(view).updateRoomFormList(guestDetailsList.get(0), 0);
    }

    @Test
    public void testBookerNotStayingOnClickUpdateBookerDetailsNotValid() {
        when(view.getOnUpdateClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(input.firstName()).thenReturn("");
        when(input.lastName()).thenReturn("");
        when(input.phoneNumber()).thenReturn("asd");
        when(input.email()).thenReturn("invalid@com");

        guestDetailsList.add(GuestDetailsFormDataInput.create("Mr", "asd", "asd", "valid@email.com", "075454654"));

        editGuestPresenter.attachView(view);
        verify(view).showBookerValidationError(true, FIRST_NAME);
        verify(view).showBookerValidationError(true, LAST_NAME);
        verify(view).showBookerValidationError(true, CONTACT_NUMBER);
        verify(view).showBookerValidationError(true, EMAIL);
        verify(view).updateRoomFormList(guestDetailsList.get(0), 0);
    }

    @Test
    public void testShowBookerErrorOnFocusChange() {
        String firstName = "james";
        String lastName = "bond";
        String number = "08888888888";
        String email = "james@bond.com";
        populateValidBookerDetailsRoomFormMock();
        when(view.getBookerForm()).thenReturn(Observable.just(
                GuestDetailsFormDataOutput.create(firstName, false, FIRST_NAME),
                GuestDetailsFormDataOutput.create(lastName, false, LAST_NAME),
                GuestDetailsFormDataOutput.create(number, false, CONTACT_NUMBER),
                GuestDetailsFormDataOutput.create(email, false, EMAIL)
        ));

        editGuestPresenter.attachView(view);
        verify(view).showBookerValidationError(false, FIRST_NAME);
        verify(view).showBookerValidationError(false, LAST_NAME);
        verify(view).showBookerValidationError(false, CONTACT_NUMBER);
        verify(view).showBookerValidationError(false, EMAIL);

        reset(view);
        basicSetup();
        firstName = "";
        lastName = "";
        number = "asd";
        email = "invalid.com";
        when(view.getBookerForm()).thenReturn(Observable.just(
                GuestDetailsFormDataOutput.create(firstName, false, FIRST_NAME),
                GuestDetailsFormDataOutput.create(lastName, false, LAST_NAME),
                GuestDetailsFormDataOutput.create(number, false, CONTACT_NUMBER),
                GuestDetailsFormDataOutput.create(email, false, EMAIL)
        ));
        editGuestPresenter.attachView(view);
        verify(view).showBookerValidationError(true, FIRST_NAME);
        verify(view).showBookerValidationError(true, LAST_NAME);
        verify(view).showBookerValidationError(true, CONTACT_NUMBER);
        verify(view).showBookerValidationError(true, EMAIL);
    }

    @Test
    public void testBookerStayingGuestEmailValidationFailsOnInvalidEmail() {
        editGuestInput = EditGuestInput.create(editGuestInput.guestDetailsFormDataInputs(),
                editGuestInput.bookerDetails(),
                true);
        when(view.getOnUpdateClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(input.firstName()).thenReturn("Mark");
        when(input.lastName()).thenReturn("OM");
        when(input.phoneNumber()).thenReturn("123456789");
        when(input.email()).thenReturn("valid@email.com");

        guestDetailsList.add(GuestDetailsFormDataInput.create("Mr", "asd", "asd", "invalidemail.com", "075454654"));

        editGuestPresenter.attachView(view);

        verify(view).updateRoomFormList(guestDetailsFormDataInputCaptor.capture(), eq(0));
        assertTrue(guestDetailsFormDataInputCaptor.getValue().errorEmail());
    }

    @Test
    public void testBookerNotStayingGuestEmailValidationFailsOnInvalidEmail() {
        when(view.getOnUpdateClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(input.firstName()).thenReturn("Mark");
        when(input.lastName()).thenReturn("OM");
        when(input.phoneNumber()).thenReturn("123456789");
        when(input.email()).thenReturn("valid@email.com");

        guestDetailsList.add(GuestDetailsFormDataInput.create("Mr", "asd", "asd", "invalidemail.com", "075454654"));

        editGuestPresenter.attachView(view);

        verify(view).updateRoomFormList(guestDetailsFormDataInputCaptor.capture(), eq(0));
        assertTrue(guestDetailsFormDataInputCaptor.getValue().errorEmail());
    }

    @Test
    public void testGuestEmailValidationPassesOnNoEmail() {
        when(view.getOnUpdateClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(input.firstName()).thenReturn("");
        when(input.lastName()).thenReturn("");
        when(input.phoneNumber()).thenReturn("asd");
        when(input.email()).thenReturn("valid@email.com");

        guestDetailsList.add(GuestDetailsFormDataInput.create("Mr", "asd", "asd", "", "075454654"));

        editGuestPresenter.attachView(view);

        verify(view).updateRoomFormList(guestDetailsFormDataInputCaptor.capture(), eq(0));
        assertFalse(guestDetailsFormDataInputCaptor.getValue().errorEmail());
    }

    @Test
    public void testChangeRoomsForm() {
        String firstName = "james";
        String lastName = "bond";
        String email = "james@bond.com";

        when(view.getRoomFormWithTextAndFocus()).thenReturn(Observable.just(
                RoomGuestDetailsData.create(GuestDetailsFormDataOutput.create(firstName, false, FIRST_NAME), 0),
                RoomGuestDetailsData.create(GuestDetailsFormDataOutput.create(lastName, false, LAST_NAME), 0),
                RoomGuestDetailsData.create(GuestDetailsFormDataOutput.create(email, false, EMAIL), 0)
        ));
        populateValidBookerDetailsRoomFormMock();
        editGuestPresenter.attachView(view);

        verify(view).updateRoomFormList(GuestDetailsFormDataInput.create("Mr", firstName, lastName, email, "en"), 0);
    }

    private void basicSetup() {
        editGuestInput = EditGuestInput.create(guestDetailsList, input, false);
        when(view.getBookerForm()).thenReturn(Observable.never());
        when(view.getRoomFormWithTextAndFocus()).thenReturn(Observable.never());
        when(view.getOnUpdateClick()).thenReturn(Observable.never());
        when(deviceLocaleProvider.getDeviceLanguage()).thenReturn(LANGUAGE_ENGLISH);
    }

    private void populateValidBookerDetailsRoomFormMock() {
        when(input.title()).thenReturn("Mr");
        when(input.firstName()).thenReturn("asd");
        when(input.lastName()).thenReturn("asd");
        when(input.phoneNumber()).thenReturn("0425456465");
        when(input.email()).thenReturn("valid@mail.com");
        guestDetailsList.add(GuestDetailsFormDataInput.create("Mr", "asd", "asd", "valid@email.com", "075454654"));
    }
}
