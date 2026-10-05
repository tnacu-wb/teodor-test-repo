package com.whitbread.premierinn.createaccount;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.response.customer.ContactDetail;
import com.whitbread.premierinn.api.response.customer.CustomerAddress;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.forms.FormInputErrorMessageProvider;
import com.whitbread.premierinn.common.forms.FormUiModel;
import com.whitbread.premierinn.common.forms.Input;
import com.whitbread.premierinn.common.forms.InputState;
import com.whitbread.premierinn.common.forms.action.Action;
import com.whitbread.premierinn.common.forms.action.ResetInputErrorAction;
import com.whitbread.premierinn.common.forms.action.SubmitFormAction;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.common.utils.StringUtils;
import com.whitbread.premierinn.createaccount.action.HomeWorkToggleAction;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.domain.authentication.usecase.AuthenticateCustomer;
import com.whitbread.premierinn.domain.countries.GetCountries;
import com.whitbread.premierinn.domain.countries.entity.CountryDomain;
import com.whitbread.premierinn.domain.customer.usecase.CreateCustomer;
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
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import kotlin.Unit;

import static com.whitbread.premierinn.common.forms.Input.ValidationType.EMAIL;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.PASSWORD;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.PHONE;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.REQUIRED;
import static com.whitbread.premierinn.common.view.ToggleButtonView.State.LEFT;
import static com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.GDPR_MY_DETAILS_USAGE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CreateAccountPresenterTest {

    @Rule
    public RxJavaTestRule rxJavaTestRule = new RxJavaTestRule();

    private CreateAccountPresenter presenter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Mock CreateAccountPresenter.View view;
    @Mock GetCountries getCountries;
    @Mock CreateAccountMessageProvider messageProvider;
    @Mock CreateCustomer createCustomer;
    @Mock AuthenticateCustomer authenticateCustomer;
    @Mock TrackingAnalytics trackingAnalytics;
    @Mock GetStringResource getStringResource;
    @Mock LogService crashlyticsLogger;
    @Mock FormInputErrorMessageProvider formInputErrorMessageProvider;
    @Mock DeviceLocaleProvider deviceLocaleProvider;

    @Before
    public void setup() {
        presenter = new CreateAccountPresenter(getCountries, compositeDisposable,
                messageProvider, createCustomer, authenticateCustomer, trackingAnalytics,
                getStringResource, crashlyticsLogger, deviceLocaleProvider, formInputErrorMessageProvider);

        when(view.onSetImmediately()).thenReturn(Observable.never());
        when(view.onFindAddressClick()).thenReturn(Observable.never());
        when(view.onPostcodeFindingSuccess()).thenReturn(Observable.never());
        when(view.onPostcodeFindingSuccess()).thenReturn(Observable.never());
        when(view.getActions()).thenReturn(Observable.never());
        when(getCountries.fetchCountriesFromSharedPref()).thenReturn(new ArrayList<>());
        when(getStringResource.invoke(GDPR_MY_DETAILS_USAGE)).thenReturn("");
        when(deviceLocaleProvider.getDeviceLanguage()).thenReturn(Locale.UK.getLanguage());
    }


    @Test
    public void test_onSetImmediately_titles() {
        when(view.onSetImmediately()).thenReturn(Observable.just(new Object()));
        presenter.attachView(view);

        verify(view).populateTitlesList(messageProvider.titlesList());
    }

    @Test
    public void test_onSetImmediately_populate_countries_field() {
        CountryDomain countryMock = mock(CountryDomain.class);
        List<CountryDomain> expectedCountries = Collections.singletonList(countryMock);
        when(view.onSetImmediately()).thenReturn(Observable.just(new Object()));
        when(getCountries.fetchCountriesFromSharedPref()).thenReturn(expectedCountries);

        presenter.attachView(view);

        verify(view).populateCountriesList(expectedCountries);
    }

    @Test
    public void test_onFindAddressClick() {
        when(view.onFindAddressClick()).thenReturn(Observable.just(Unit.INSTANCE));
        presenter.attachView(view);

        verify(view).startPostcodeFinderActivity();
    }

    @Test
    public void test_onFormUpdate_submitFormResult_inFlight() {
        ContactDetail contactDetail = getValidContactDetail();
        String pwd = "Password1";
        boolean specialoffersOptout = true;

        List<Input> inputs = getValidInputs(contactDetail, pwd, specialoffersOptout);
        when(view.getActions()).thenReturn(Observable.just(SubmitFormAction.create(inputs)));
        when(createCustomer.invoke(any(CreateCustomer.Params.class))).thenReturn(Completable.never());

        presenter.attachView(view);

        verify(view).update(FormUiModel.inProgress());
    }

    @Test
    public void test_onFormUpdate_submitFormResult_validationFailed() {
        List<Input> inputs = Collections.singletonList(Input.builder()
                .id(R.id.address_form_line1_input)
                .value("")
                .validationTypes(EnumSet.of(REQUIRED))
                .build());
        List<InputState> expectedInputStates = Collections.singletonList(InputState.failed(R.id.address_form_line1_input, REQUIRED));
        when(view.getActions()).thenReturn(Observable.just(SubmitFormAction.create(inputs)));

        presenter.attachView(view);

        verify(view).update(FormUiModel.updateForm(expectedInputStates, formInputErrorMessageProvider));
    }

    @Test
    public void test_onFormUpdate_submitFormResult_success() {
        ContactDetail contactDetail = getValidContactDetail();

        String password = "Password1";
        boolean specialOffers = true;
        List<Input> inputs = getValidInputs(contactDetail, password, specialOffers);

        when(createCustomer.invoke(any(CreateCustomer.Params.class))).thenReturn(Completable.complete());
        when(authenticateCustomer.invoke(any(AuthenticateCustomer.Params.class))).thenReturn(Completable.complete());
        when(view.getActions()).thenReturn(Observable.just(SubmitFormAction.create(inputs)));

        presenter.attachView(view);

        verify(view).update(FormUiModel.inProgress());
        verify(view).update(FormUiModel.success());
    }

    private ContactDetail getValidContactDetail() {
        return ContactDetail.builder()
                .address(getValidCustomerAddress())
                .email("whathever@gmail.com")
                .mobile("05648383833")
                .title("whathever")
                .firstName("firstName")
                .lastName("lastName").build();
    }

    private CustomerAddress getValidCustomerAddress() {
        return CustomerAddress.builder()
                .countryCode("whathever")
                .line1("whathever")
                .postCode("whathever")
                .type("HOME").build();
    }

    @Test
    public void test_onFormUpdate_submitFormResult_failed() {
        List<Input> inputs = getValidInputs(getValidContactDetail(), "Password1", true);
        when(view.getActions()).thenReturn(Observable.just(SubmitFormAction.create(inputs)));
        when(createCustomer.invoke(any(CreateCustomer.Params.class))).thenReturn(Completable.error(new Exception()));
        presenter.attachView(view);

        verify(view).update(FormUiModel.error());
    }

    @Test
    public void test_onFormUpdate_resetInputErrorResult() {
        int id = R.id.address_form_line1_input;
        InputState inputState = InputState.builder()
                .id(id)
                .state(InputState.State.VALID)
                .errorMessage(StringUtils.EMPTY_STRING)
                .build();
        List<Input> inputs = Collections.singletonList(Input.builder()
                .id(id)
                .value("")
                .validationTypes(EnumSet.of(REQUIRED))
                .build());

        List<InputState> expectedInputStates = Collections.singletonList(InputState.valid(inputState));
        Observable<Action> submitFormObservable = Observable.just(SubmitFormAction.create(inputs));
        Observable<Action> resetInputErrorObservable = Observable.just(ResetInputErrorAction.create(id));

        when(view.getActions()).thenReturn(submitFormObservable.mergeWith(resetInputErrorObservable));

        presenter.attachView(view);

        verify(view).update(FormUiModel.resetInputError(id, expectedInputStates));
    }


    @Test
    public void test_onFormUpdate_formResult() {
        List<InputState> expectedInputStates = Collections.singletonList(InputState.builder()
                .state(InputState.State.INVISIBLE)
                .id(R.id.address_form_company_input).build());

        when(view.getActions()).thenReturn(Observable.just(HomeWorkToggleAction.create(LEFT)));

        presenter.attachView(view);

        verify(view).update(FormUiModel.idle());
        verify(view).update(FormUiModel.builder()
                .state(FormUiModel.State.FORM_UPDATE)
                .inputStates(expectedInputStates).build());
    }

    @Test
    public void testLifecycle() {
        assertFalse(presenter.isViewAttached());
        assertEquals(0, compositeDisposable.size());
        presenter.attachView(view);
        assertTrue(presenter.isViewAttached());
        assertEquals(4, compositeDisposable.size());
        presenter.detachView();
        assertEquals(0, compositeDisposable.size());
        assertFalse(presenter.isViewAttached());
    }

    private List<Input> getValidInputs(ContactDetail contactDetail, String password, boolean specialOffers) {
        return Arrays.asList(
                Input.builder()
                        .id(R.id.account_titles_spinner)
                        .value(contactDetail.title())
                        .validationTypes(EnumSet.of(REQUIRED))
                        .build(),
                Input.builder()
                        .id(R.id.account_first_name_input)
                        .value(contactDetail.firstName())
                        .validationTypes(EnumSet.of(REQUIRED))
                        .build(),
                Input.builder()
                        .id(R.id.account_last_name_input)
                        .value(contactDetail.lastName())
                        .validationTypes(EnumSet.of(REQUIRED))
                        .build(),
                Input.builder()
                        .id(R.id.account_contact_number_input)
                        .value(contactDetail.mobile())
                        .validationTypes(EnumSet.of(REQUIRED, PHONE))
                        .build(),
                Input.builder()
                        .id(R.id.account_email_input)
                        .value(contactDetail.email())
                        .validationTypes(EnumSet.of(REQUIRED, EMAIL))
                        .build(),
                Input.builder()
                        .id(R.id.create_account_password_input)
                        .value(password)
                        .validationTypes(EnumSet.of(REQUIRED, PASSWORD))
                        .build(),
                Input.builder()
                        .id(R.id.address_form_line1_input)
                        .value(contactDetail.address().line1())
                        .validationTypes(EnumSet.of(REQUIRED))
                        .build(),
                Input.builder()
                        .id(R.id.address_form_lookup_postcode_input)
                        .value(contactDetail.address().postCode())
                        .validationTypes(EnumSet.of(REQUIRED))
                        .build(),
                Input.builder()
                        .id(R.id.address_form_countries_spinner)
                        .value(contactDetail.address().countryCode())
                        .validationTypes(EnumSet.of(REQUIRED))
                        .build(),
                Input.builder()
                        .id(R.id.address_form_home_work_toggle)
                        .value(LEFT)
                        .validationTypes(EnumSet.of(REQUIRED))
                        .build());
    }
}
