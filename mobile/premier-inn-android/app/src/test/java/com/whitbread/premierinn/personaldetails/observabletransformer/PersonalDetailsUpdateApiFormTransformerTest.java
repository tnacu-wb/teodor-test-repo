package com.whitbread.premierinn.personaldetails.observabletransformer;

import com.whitbread.premierinn.R;
import com.whitbread.premierinn.common.analytics.TrackingAnalytics;
import com.whitbread.premierinn.common.forms.Input;
import com.whitbread.premierinn.common.forms.result.SubmitFormResult;
import com.whitbread.premierinn.common.service.LogService;
import com.whitbread.premierinn.data.remote.ApiThrowable;
import com.whitbread.premierinn.domain.customer.entity.Customer;
import com.whitbread.premierinn.domain.customer.usecase.UpdateCustomerPersonalDetails;
import com.whitbread.premierinn.utils.RxJavaTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PersonalDetailsUpdateApiFormTransformerTest {

    @Rule
    public RxJavaTestRule rxJavaTestRule = new RxJavaTestRule();

    @Mock
    UpdateCustomerPersonalDetails updateCustomerPersonalDetails;
    @Mock
    TrackingAnalytics trackingAnalytics;
    @Mock
    LogService crashlyticsLogger;
    private PersonalDetailsUpdateApiFormTransformer transformer;
    private final Customer existingCustomerDetails = Customer.emptyCustomer("GB");


    @Before
    public void setup() {
        transformer = new PersonalDetailsUpdateApiFormTransformer(updateCustomerPersonalDetails,
                existingCustomerDetails, trackingAnalytics, crashlyticsLogger, null);
    }

    @Test
    public void testValidInputSuccess() {
        when(updateCustomerPersonalDetails.invoke(any(UpdateCustomerPersonalDetails.Params.class))).thenReturn(Completable.complete());

        Observable.just(validDetailsInput())
                .compose(transformer)
                .test()
                .assertValueSequence(Arrays.asList(SubmitFormResult.inFlight(), SubmitFormResult.serverSuccess()));
    }

    @Test
    public void testUpdateCallFails() {
        PersonalDetailsUpdateApiFormTransformer transformer =
                new PersonalDetailsUpdateApiFormTransformer(updateCustomerPersonalDetails,
                        existingCustomerDetails, trackingAnalytics, crashlyticsLogger, null);

        when(updateCustomerPersonalDetails.invoke(any(UpdateCustomerPersonalDetails.Params.class))).thenReturn(
                Completable.error(new ApiThrowable.Http(500, null)));

        Observable.just(validDetailsInput())
                .compose(transformer)
                .test()
                .assertValueSequence(Arrays.asList(SubmitFormResult.inFlight(),
                        SubmitFormResult.serverError(PersonalDetailsUpdateApiFormTransformer.SERVER_ERROR_MSG)));
    }

    private List<Input> validDetailsInput() {
        List<Input> inputList = new ArrayList<>();

        inputList.add(Input.builder()
                .id(R.id.account_titles_spinner)
                .value("Mr").validationTypes(EnumSet.of(Input.ValidationType.NONE)).build());

        inputList.add(Input.builder()
                .id(R.id.account_first_name_input)
                .value("Mark").validationTypes(EnumSet.of(Input.ValidationType.NONE)).build());

        inputList.add(Input.builder()
                .id(R.id.account_last_name_input)
                .value("O'Meara").validationTypes(EnumSet.of(Input.ValidationType.NONE)).build());

        inputList.add(Input.builder()
                .id(R.id.account_contact_number_input)
                .value("97439874").validationTypes(EnumSet.of(Input.ValidationType.NONE)).build());

        inputList.add(Input.builder()
                .id(R.id.account_email_input)
                .value("mark.omeara@whitbreadtest.com").validationTypes(EnumSet.of(Input.ValidationType.NONE)).build());

        inputList.add(Input.builder()
                .id(R.id.address_form_countries_spinner)
                .value("GB").validationTypes(EnumSet.of(Input.ValidationType.NONE)).build());

        inputList.add(Input.builder()
                .id(R.id.address_form_lookup_postcode_input)
                .value("ec1n2td").validationTypes(EnumSet.of(Input.ValidationType.NONE)).build());

        inputList.add(Input.builder()
                .id(R.id.address_form_line1_input)
                .value("120 Holborn").validationTypes(EnumSet.of(Input.ValidationType.NONE)).build());

        inputList.add(Input.builder()
                .id(R.id.address_form_town_city_input)
                .value("London").validationTypes(EnumSet.of(Input.ValidationType.NONE)).build());

        inputList.add(Input.builder()
                .id(R.id.address_form_home_work_toggle)
                .value("HOME").validationTypes(EnumSet.of(Input.ValidationType.NONE)).build());

        return inputList;
    }

    private List<Input> invalidDetailsInput() {
        List<Input> inputList = new ArrayList<>();

        inputList.add(Input.builder()
                .id(R.id.account_titles_spinner)
                .value("").validationTypes(EnumSet.of(Input.ValidationType.REQUIRED)).build());

        inputList.add(Input.builder()
                .id(R.id.account_first_name_input)
                .value("Mark").validationTypes(EnumSet.of(Input.ValidationType.NONE)).build());

        inputList.add(Input.builder()
                .id(R.id.address_form_lookup_postcode_input)
                .value("ec1n2td").validationTypes(EnumSet.of(Input.ValidationType.NONE)).build());

        inputList.add(Input.builder()
                .id(R.id.address_form_countries_spinner)
                .value("GB").validationTypes(EnumSet.of(Input.ValidationType.NONE)).build());
        inputList.add(Input.builder()
                .id(R.id.address_form_home_work_toggle)
                .value("HOME").validationTypes(EnumSet.of(Input.ValidationType.NONE)).build());

        inputList.add(Input.builder()
                .id(R.id.address_form_line1_input)
                .value("120 Holborn").validationTypes(EnumSet.of(Input.ValidationType.NONE)).build());

        inputList.add(Input.builder()
                .id(R.id.account_last_name_input)
                .value("O'Meara").validationTypes(EnumSet.of(Input.ValidationType.NONE)).build());

        inputList.add(Input.builder()
                .id(R.id.account_email_input)
                .value("hi@hi.com").validationTypes(EnumSet.of(Input.ValidationType.NONE)).build());

        return inputList;
    }
}