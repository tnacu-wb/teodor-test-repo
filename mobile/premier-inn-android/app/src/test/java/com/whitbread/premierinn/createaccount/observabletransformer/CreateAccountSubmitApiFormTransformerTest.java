package com.whitbread.premierinn.createaccount.observabletransformer;

import static com.whitbread.premierinn.common.forms.Input.ValidationType.EMAIL;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.PASSWORD;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.PHONE;
import static com.whitbread.premierinn.common.forms.Input.ValidationType.REQUIRED;
import static com.whitbread.premierinn.common.view.ToggleButtonView.State.LEFT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.api.response.customer.ContactDetail;
import com.whitbread.premierinn.api.response.customer.CustomerAddress;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.forms.Input;
import com.whitbread.premierinn.common.forms.result.SubmitFormResult;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.createaccount.CreateAccountMessageProvider;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.data.remote.ApiError;
import com.whitbread.premierinn.data.remote.ApiThrowable;
import com.whitbread.premierinn.domain.authentication.usecase.AuthenticateCustomer;
import com.whitbread.premierinn.domain.customer.usecase.CreateCustomer;
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
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;

@RunWith(MockitoJUnitRunner.class)
public class CreateAccountSubmitApiFormTransformerTest {

    private CreateAccountSubmitApiFormTransformer createAccountSubmitApiFormTransformer;
    @Rule
    public RxJavaTestRule rxJavaTestRule = new RxJavaTestRule();
    @Mock
    CreateCustomer createCustomer;
    @Mock
    AuthenticateCustomer authenticateCustomer;
    @Mock
    CreateAccountMessageProvider messageProvider;
    @Mock
    TrackingAnalytics trackingAnalytics;
    @Mock
    LogService crashlyticsLogger;
    @Mock
    DeviceLocaleProvider deviceLocaleProvider;

    @Before
    public void setup() {
        when(deviceLocaleProvider.getDeviceLanguage()).thenReturn(Locale.UK.getLanguage());

        createAccountSubmitApiFormTransformer = new CreateAccountSubmitApiFormTransformer(createCustomer, authenticateCustomer,
                messageProvider, trackingAnalytics, crashlyticsLogger, deviceLocaleProvider);
    }

    @Test
    public void createCustomerSuccessful_loginSuccessful() {
        ContactDetail contactDetail = getValidContactDetail();
        String password = "min8Characters";
        boolean emailMarketing = true;

        when(createCustomer.invoke(any(CreateCustomer.Params.class))).thenReturn(Completable.complete());
        when(authenticateCustomer.invoke(any(AuthenticateCustomer.Params.class)))
                .thenReturn(Completable.complete());

        List<Input> inputList = getSuccessfulInputs(contactDetail, password, emailMarketing);

        Observable.just(inputList)
                .compose(createAccountSubmitApiFormTransformer)
                .test()
                .assertValues(SubmitFormResult.inFlight(), SubmitFormResult.inFlight(), SubmitFormResult.serverSuccess());
    }

    @Test
    public void createCustomer_customerAlreadyRegistered() {
        ContactDetail contactDetail = getValidContactDetail();
        String password = "min8Characters";
        boolean emailMarketing = true;
        when(createCustomer.invoke(any(CreateCustomer.Params.class))).thenReturn(Completable.error(
                new ApiThrowable.Http(400, new ApiError(29, Collections.singletonList("is already registered")))));

        String errorMessage = "Error message";
        when(messageProvider.getCustomerRegisteredErrorMessage()).thenReturn(errorMessage);

        List<Input> inputList = getSuccessfulInputs(contactDetail, password, emailMarketing);
        Observable.just(inputList)
                .compose(createAccountSubmitApiFormTransformer)
                .test()
                .assertValues(SubmitFormResult.inFlight(), SubmitFormResult.serverError(errorMessage))
                .dispose();
    }

    @Test
    public void createCustomer_errorResponse() {
        ContactDetail contactDetail = getValidContactDetail();
        String password = "min8Characters";
        boolean emailMarketing = true;

        when(createCustomer.invoke(any(CreateCustomer.Params.class))).thenReturn(Completable.error(
                new ApiThrowable.Http(400, null)));

        String errorMessage = "Error message";
        when(messageProvider.getGenericErrorMessage()).thenReturn(errorMessage);

        List<Input> inputList = getSuccessfulInputs(contactDetail, password, emailMarketing);
        Observable.just(inputList)
                .compose(createAccountSubmitApiFormTransformer)
                .test()
                .assertValues(SubmitFormResult.inFlight(), SubmitFormResult.serverError(errorMessage))
                .dispose();
    }

    @Test
    public void createCustomerSuccess_loginError() {
        ContactDetail contactDetail = getValidContactDetail();
        String password = "min8Characters";
        boolean emailMarketing = true;
        when(createCustomer.invoke(any(CreateCustomer.Params.class))).thenReturn(Completable.complete());
        when(authenticateCustomer.invoke(any(AuthenticateCustomer.Params.class))).thenReturn(Completable.error(
                new ApiThrowable.Http(400, null)).delay(5, TimeUnit.SECONDS));

        String errorMessageLogin = "Error message login";
        when(messageProvider.getLoginError()).thenReturn(errorMessageLogin);

        List<Input> inputList = getSuccessfulInputs(contactDetail, password, emailMarketing);
        Observable.just(inputList)
                .compose(createAccountSubmitApiFormTransformer)
                .test()
                .assertValues(SubmitFormResult.inFlight(), SubmitFormResult.inFlight(), SubmitFormResult.serverError(errorMessageLogin));
    }

    @Test
    public void whenTwoPostcodesAvailable_throwError() {
        List<Input> inputList = new ArrayList<>(getSuccessfulInputs(getValidContactDetail(), "min8Characters", true));
        inputList.add(Input.builder()
                .id(R.id.address_form_postcode_input)
                .value("postcode 2")
                .validationTypes(EnumSet.of(REQUIRED))
                .build());
        Observable.just(inputList)
                .compose(createAccountSubmitApiFormTransformer)
                .test()
                .assertError(IllegalStateException.class);
    }

    private ContactDetail getValidContactDetail() {
        CustomerAddress address = CustomerAddress.builder()
                .countryCode("GB")
                .line1("Money Lane")
                .postCode("N1 7BD")
                .type("HOME").build();
        return ContactDetail.builder()
                .address(address)
                .email("rr@rich.com")
                .mobile("05648383833")
                .title("Mr")
                .firstName("Richie")
                .lastName("Rich").build();
    }

    private List<Input> getSuccessfulInputs(ContactDetail contactDetail, String password,
                                            boolean emailMarketing) {
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
