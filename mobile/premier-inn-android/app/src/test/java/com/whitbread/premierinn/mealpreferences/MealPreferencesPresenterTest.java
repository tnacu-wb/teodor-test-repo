package com.whitbread.premierinn.mealpreferences;

import com.whitbread.premierinn.api.response.InstanceFactory;
import com.whitbread.premierinn.bookingdetails.UpsellItemSummary;
import com.whitbread.premierinn.common.PreferencesContentProvider;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.data.common.DomainMappers;
import com.whitbread.premierinn.data.remote.AccountApiContract;
import com.whitbread.premierinn.data.remote.ApiThrowable;
import com.whitbread.premierinn.domain.common.RoomCriteria;
import com.whitbread.premierinn.domain.common.RoomType;
import com.whitbread.premierinn.domain.customer.usecase.GetCustomer;
import com.whitbread.premierinn.domain.customer.usecase.UpdateCustomerBookingPreferences;
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

import io.reactivex.Completable;
import io.reactivex.Observable;
import kotlin.Unit;

import static com.whitbread.premierinn.bookingdetails.UpsellItemSummary.UpsellItemType.CONTINENTAL_BREAKFAST;
import static com.whitbread.premierinn.bookingdetails.UpsellItemSummary.UpsellItemType.FREE_CHILD_BREAKFAST;
import static com.whitbread.premierinn.bookingdetails.UpsellItemSummary.UpsellItemType.MEAL_DEAL;
import static com.whitbread.premierinn.bookingdetails.UpsellItemSummary.UpsellItemType.PI_BREAKFAST;
import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class MealPreferencesPresenterTest {

    @Rule
    public RxJavaTestRule rxJavaTestRule = new RxJavaTestRule();
    private MealPreferencesPresenter presenter;
    private List<BreakfastRadioButtonInput> meals;
    @Mock
    private PreferencesContentProvider messageProvider;
    @Mock
    private GetCustomer getCustomer;
    @Mock
    private UpdateCustomerBookingPreferences updateCustomerBookingPreferences;
    @Mock
    private MealPreferencesPresenter.View view;
    @Mock
    private TrackingAnalytics trackingAnalytics;
    @Captor
    private ArgumentCaptor<UpdateCustomerBookingPreferences.Params> bookingPreferenceCaptor;
    private AccountApiContract.CustomerResponse successCustomerResponse;
    private AccountApiContract.CustomerResponse customerNoExistingBookingPrefs;

    @Before
    public void setup() {
        successCustomerResponse = InstanceFactory.create(AccountApiContract.CustomerResponse.class, "apiTest/customer-success.json");
        when(getCustomer.getCustomerFromSharedPref()).thenReturn(DomainMappers.toDomain(successCustomerResponse));
        presenter = new MealPreferencesPresenter(messageProvider, getCustomer, updateCustomerBookingPreferences, trackingAnalytics);

        meals = createMealsList();
        when(messageProvider.getBreakfastContents()).thenReturn(meals);
        when(view.onSaveChangesClick()).thenReturn(Observable.never());
    }

    @Test
    public void test_DisplayMealPreferenceList() {
        presenter.attachView(view);

        verify(view).showBreakfastOptions(meals);
    }

    @Test
    public void test_selectDefaultMealOption() {
        presenter.attachView(view);

        verify(view).selectBreakfastOption("11");
    }

    @Test
    public void test_selectDefaultMealOption_noPreference() {
        successCustomerResponse = InstanceFactory.create(AccountApiContract.CustomerResponse.class,
                "apiTest/customer-no-booking-prefs.json");
        when(getCustomer.getCustomerFromSharedPref()).thenReturn(DomainMappers.toDomain(successCustomerResponse));

        presenter = new MealPreferencesPresenter(messageProvider, getCustomer, updateCustomerBookingPreferences, trackingAnalytics);

        presenter.attachView(view);

        verify(view).selectBreakfastOption(UpsellItemSummary.UpsellItemType.NO_PREFERENCE.code());
    }

    @Test
    public void test_onSaveChangesClick_updateCustomerSuccess() {
        BreakfastRadioButtonInput radioButtonInput = BreakfastRadioButtonInput.builder()
                .code(MEAL_DEAL.code())
                .legend("")
                .price("")
                .description("").build();

        when(view.onSaveChangesClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(view.getOptionSelected()).thenReturn(Observable.just(radioButtonInput));

        when(updateCustomerBookingPreferences.invoke(any(UpdateCustomerBookingPreferences.Params.class)))
                .thenReturn(Completable.complete());

        presenter.attachView(view);

        verify(view).showButtonLoading(true);
        verify(view).showButtonLoading(false);
        verify(view).goToBookingsPreferences();
    }

    @Test
    public void test_onSaveChangesClick_updateCustomerError() {
        BreakfastRadioButtonInput radioButtonInput = BreakfastRadioButtonInput.builder()
                .code(MEAL_DEAL.code())
                .legend("")
                .price("")
                .description("").build();

        when(messageProvider.getErrorMessageUpdateCustomer()).thenReturn("error message");
        when(view.onSaveChangesClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(view.getOptionSelected()).thenReturn(Observable.just(radioButtonInput));
        when(updateCustomerBookingPreferences.invoke(any(UpdateCustomerBookingPreferences.Params.class)))
                .thenReturn(Completable.error(new ApiThrowable.Http(400, null)));

        presenter.attachView(view);

        verify(view).showButtonLoading(true);
        verify(view).showButtonLoading(false);
        verify(view).showError(messageProvider.getErrorMessageUpdateCustomer());
    }

    @Test
    public void test_onSaveChangesClick_sameSelection_dontUpdate() {
        BreakfastRadioButtonInput radioButtonInput = BreakfastRadioButtonInput.builder()
                .code(PI_BREAKFAST.code())
                .legend("")
                .price("")
                .description("").build();

        when(view.onSaveChangesClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(view.getOptionSelected()).thenReturn(Observable.just(radioButtonInput));

        presenter.attachView(view);

        verify(view).showButtonLoading(false);
        verify(view).goToBookingsPreferences();
    }

    @Test
    public void updateCustomer_NoExistingBookingPreferences() {
        customerNoExistingBookingPrefs = InstanceFactory.create(AccountApiContract.CustomerResponse.class,
                "apiTest/customer-no-booking-prefs.json");
        when(getCustomer.getCustomerFromSharedPref()).thenReturn(DomainMappers.toDomain(customerNoExistingBookingPrefs));
        when(view.onSaveChangesClick()).thenReturn(Observable.just(Unit.INSTANCE));
        when(updateCustomerBookingPreferences.invoke(any(UpdateCustomerBookingPreferences.Params.class))).thenReturn(Completable.never());

        BreakfastRadioButtonInput radioButtonInput = BreakfastRadioButtonInput.builder()
                .code(PI_BREAKFAST.code())
                .legend("")
                .price("")
                .description("").build();

        when(view.getOptionSelected()).thenReturn(Observable.just(radioButtonInput));

        presenter.attachView(view);

        verify(updateCustomerBookingPreferences).invoke(bookingPreferenceCaptor.capture());
        assertEquals(RoomCriteria.Companion.createWithDefaults(1, 0, 0, false, RoomType.DOUBLE, 1, ""),
                bookingPreferenceCaptor.getValue().getPreferences()
                        .getRoomCriteriaPreference());
    }

    private List<BreakfastRadioButtonInput> createMealsList() {
        List<BreakfastRadioButtonInput> breakfastOptions = new ArrayList<>();
        String[] codesArray = {PI_BREAKFAST.code(), MEAL_DEAL.code(), CONTINENTAL_BREAKFAST.code(), FREE_CHILD_BREAKFAST.code()};
        for (int i = 0; i < 4; i++) {
            String code = codesArray[i];
            BreakfastRadioButtonInput breakfastRadioButtonInput = BreakfastRadioButtonInput.builder()
                    .code(code)
                    .legend("")
                    .price("")
                    .description("").build();
            breakfastOptions.add(breakfastRadioButtonInput);
        }
        return breakfastOptions;
    }
}
