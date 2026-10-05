package com.whitbread.premierinn.bookingpreferences;

import com.whitbread.premierinn.api.response.InstanceFactory;
import com.whitbread.premierinn.bookingdetails.UpsellItemSummary;
import com.whitbread.premierinn.common.StringResourceProvider;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.data.common.DomainMappers;
import com.whitbread.premierinn.data.remote.AccountApiContract;
import com.whitbread.premierinn.domain.common.RoomCriteria;
import com.whitbread.premierinn.domain.common.RoomType;
import com.whitbread.premierinn.domain.customer.entity.Customer;
import com.whitbread.premierinn.domain.customer.usecase.GetCustomer;
import com.whitbread.premierinn.utils.RxJavaTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookingPreferencesPresenterTest {

    private static final String MEAL_DEAL_DESCRIPTION = "Meal deal";

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Rule
    public RxJavaTestRule rxJavaTestRule = new RxJavaTestRule();
    @Mock
    TrackingAnalytics trackingAnalytics;
    @Mock
    BookingPreferencesPresenter.View view;
    @Mock
    GetCustomer getCustomer;
    @Mock
    StringResourceProvider stringResourceProvider;
    AccountApiContract.CustomerResponse successCustomerResponse;
    private BookingPreferencesPresenter presenter;

    @Before
    public void setup() {
        presenter = new BookingPreferencesPresenter(getCustomer, stringResourceProvider, compositeDisposable, trackingAnalytics);
        when(stringResourceProvider.getUpsellDescriptionFromType(UpsellItemSummary.UpsellItemType.MEAL_DEAL))
                .thenReturn(MEAL_DEAL_DESCRIPTION);

        successCustomerResponse = InstanceFactory.create(AccountApiContract.CustomerResponse.class,
                "apiTest/customer-success-meal-deal.json");

        when(getCustomer.getCustomerFromSharedPref()).thenReturn(DomainMappers.toDomain(successCustomerResponse));

        when(view.onBookingPrefChanged()).thenReturn(Observable.never());
        when(view.onChangeMealPrefsClick()).thenReturn(Observable.never());
        when(view.onChangeRoomRequirementsClick()).thenReturn(Observable.never());
    }

    @Test
    public void testNoPreferences() {
        successCustomerResponse = InstanceFactory.create(AccountApiContract.CustomerResponse.class,
                "apiTest/customer-no-booking-prefs.json");
        when(getCustomer.getCustomerFromSharedPref()).thenReturn(DomainMappers.toDomain(successCustomerResponse));

        presenter.attachView(view);

        verify(view).showNoMealPreference();
        verify(view).showNoRoomRequirements();
    }

    @Test
    public void testErrorResponse() {
        when(getCustomer.getCustomerFromSharedPref()).thenReturn(Customer.emptyCustomer("null"));

        presenter.attachView(view);

        verify(view).showError();
    }

    @Test
    public void testMealPreferenceDisplayed() {
        successCustomerResponse = InstanceFactory.create(AccountApiContract.CustomerResponse.class,
                "apiTest/customer-with-booking-prefs-no-room-req.json");
        when(getCustomer.getCustomerFromSharedPref()).thenReturn(DomainMappers.toDomain(successCustomerResponse));

        presenter.attachView(view);

        verify(view).showMealPreference(MEAL_DEAL_DESCRIPTION);
        verify(view).showNoRoomRequirements();
    }

    @Test
    public void testRoomRequirementsDisplayed() {
        RoomCriteria criteria = new RoomCriteria(2, 2,
                0, false, RoomType.FAMILY, 1, "", "PI");


        successCustomerResponse = InstanceFactory.create(AccountApiContract.CustomerResponse.class,
                "apiTest/customer-success-meal-deal-room-req.json");
        when(getCustomer.getCustomerFromSharedPref()).thenReturn(DomainMappers.toDomain(successCustomerResponse));

        presenter.attachView(view);

        verify(view).showRoomRequirements(criteria);
        verify(view).showNoMealPreference();
    }

    @Test
    public void testOnMealPreferenceClick() {
        when(view.onChangeMealPrefsClick()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(view);

        verify(view).startMealPreferencesActivity();
    }

    @Test
    public void testOnBookingPreferenceChange() {
        successCustomerResponse = InstanceFactory.create(AccountApiContract.CustomerResponse.class,
                "apiTest/customer-no-booking-prefs.json");
        when(getCustomer.getCustomerFromSharedPref()).thenReturn(DomainMappers.toDomain(successCustomerResponse));

        when(view.onBookingPrefChanged()).thenReturn(Observable.just(new Object()));

        presenter.attachView(view);

        verify(view, times(2)).showLoading(true);
        verify(view, times(2)).showLoading(false);
        verify(view, times(2)).showNoMealPreference();
        verify(view, times(2)).showNoRoomRequirements();
    }

    @Test
    public void testOnRoomPreferencesClick() {
        when(view.onChangeRoomRequirementsClick()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(view);

        verify(view).startRoomPreferencesActivity();
    }

    @Test
    public void testLifeCycle() {
        assertFalse(presenter.isViewAttached());
        assertEquals(compositeDisposable.size(), 0);

        presenter.attachView(view);
        assertTrue(presenter.isViewAttached());
        assertEquals(compositeDisposable.size(), 4);

        presenter.detachView();
        assertFalse(presenter.isViewAttached());
        assertEquals(compositeDisposable.size(), 0);
    }
}