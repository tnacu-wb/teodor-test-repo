package com.whitbread.premierinn.personaldetails;

import static com.whitbread.premierinn.data.common.Constants.LANGUAGE_ENGLISH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.forms.FormInputErrorMessageProvider;
import com.whitbread.premierinn.common.forms.FormUiModel;
import com.whitbread.premierinn.common.forms.InputState;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.domain.common.Address;
import com.whitbread.premierinn.domain.countries.GetCountries;
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences;
import com.whitbread.premierinn.domain.customer.entity.Contact;
import com.whitbread.premierinn.domain.customer.entity.Customer;
import com.whitbread.premierinn.domain.customer.entity.FullName;
import com.whitbread.premierinn.domain.customer.usecase.GetMarketingPreference;
import com.whitbread.premierinn.domain.customer.usecase.UpdateCustomerPersonalDetails;
import com.whitbread.premierinn.domain.common.NewsletterPreferenceDomain;
import com.whitbread.premierinn.domain.graphql.marketingPreferences.entity.UpdateMarketingPreferencesResponseDomain;
import com.whitbread.premierinn.domain.graphql.marketingPreferences.usecase.GraphQLMarketingPreferencesUseCase;
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource;
import com.whitbread.premierinn.utils.RxJavaTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;

@RunWith(MockitoJUnitRunner.class)
public class PersonalDetailsPresenterTest {

    @Rule
    public RxJavaTestRule rxJavaTestRule = new RxJavaTestRule();

    @Mock
    UpdateCustomerPersonalDetails updateCustomerPersonalDetails;
    @Mock
    GraphQLMarketingPreferencesUseCase updateMarketingPreferencesUseCase;
    @Mock
    GetMarketingPreference getMarketingPreference;
    @Mock
    GetCountries getCountries;
    @Mock
    PersonalDetailsPresenter.View view;
    @Mock
    TrackingAnalytics trackingAnalytics;
    @Mock
    GetStringResource getStringResource;
    @Mock
    LogService crashlyticsLogger;
    @Mock
    FormInputErrorMessageProvider formInputErrorMessageProvider;
    @Mock
    DeviceLocaleProvider deviceLocaleProvider;
    private Customer customerDetails;

    private PersonalDetailsPresenter personalDetailsPresenter;


    @Before
    public void setup() {
        customerDetails = createCustomerDetails();
        when(view.onFindAddressClick()).thenReturn(Observable.never());
        when(view.onPostcodeFindingSuccess()).thenReturn(Observable.never());
        when(view.getActions()).thenReturn(Observable.never());
        when(view.onMarketingOptInChange()).thenReturn(Observable.never());
        when(getCountries.fetchCountriesFromSharedPref()).thenReturn(new ArrayList<>());
        when(getStringResource.invoke(any())).thenReturn("some text");
        when(deviceLocaleProvider.getDeviceLanguage()).thenReturn(LANGUAGE_ENGLISH);

        NewsletterPreferenceDomain mockPreference = new NewsletterPreferenceDomain("", Collections.emptyList());
        when(getMarketingPreference.invoke(anyString())).thenReturn(Single.just(mockPreference));

        UpdateMarketingPreferencesResponseDomain mockResponse = new UpdateMarketingPreferencesResponseDomain("Success");
        lenient().when(updateMarketingPreferencesUseCase.updateMarketingPreferences(
                        anyString(), anyBoolean(), anyBoolean(), anyList(), anyString()))
                .thenReturn(Single.just(mockResponse));

        personalDetailsPresenter = new PersonalDetailsPresenter(
                updateCustomerPersonalDetails,
                updateMarketingPreferencesUseCase,
                getMarketingPreference,
                new CompositeDisposable(),
                getCountries,
                trackingAnalytics,
                getStringResource,
                crashlyticsLogger,
                formInputErrorMessageProvider,
                deviceLocaleProvider
        );
        personalDetailsPresenter.initParams(customerDetails);
    }

    private Customer createCustomerDetails() {
        return new Customer(
                "123456",
                new FullName(
                        "Mr",
                        "Mark",
                        "O'Meara"),
                new Contact(
                        "mark@whitbreadtest.com",
                        "07490677777",
                        "07490677777"
                ),
                new Address(
                        "120 Holborn",
                        "",
                        "",
                        "ec1n2td",
                        "",
                        "ec1n2td",
                        "whitbread",
                        "32"
                ),
                BookingPreferences.Companion.getEMPTY()
        );
    }

    @Test
    public void testManualAddressExpandedFromStart() {
        personalDetailsPresenter.attachView(view);
        FormUiModel expectedUiModel = createInputStatesShowingAllAddressFields();
        verify(view).update(expectedUiModel);
    }

    @Test
    public void testPostcodeSearchOpensPostcodeActivity() {
        when(view.onFindAddressClick()).thenReturn(Observable.just(Unit.INSTANCE));

        personalDetailsPresenter.attachView(view);

        verify(view).startPostcodeFinderActivity();
    }

    private FormUiModel createInputStatesShowingAllAddressFields() {
        InputState wrapperState = InputState.builder().id(R.id.address_form_manual_address_wrapper).state(InputState.State.VISIBLE).build();
        InputState labelState = InputState.builder().id(R.id.address_form_manual_address_label).state(InputState.State.INVISIBLE).build();
        List<InputState> inputStateList = Arrays.asList(wrapperState, labelState);
        return FormUiModel.builder().state(FormUiModel.State.FORM_UPDATE).inputStates(inputStateList).build();
    }
}