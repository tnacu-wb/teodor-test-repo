package com.whitbread.premierinn.personaldetails.observabletransformer;

import com.whitbread.premierinn.common.forms.Input;
import com.whitbread.premierinn.common.forms.InputState;
import com.whitbread.premierinn.common.forms.result.FormResult;
import com.whitbread.premierinn.domain.countries.entity.CountryDomain;
import com.whitbread.premierinn.personaldetails.action.NationalityChangedAction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import io.reactivex.Observable;


@RunWith(MockitoJUnitRunner.class)
public class NationalityChangedActionTransformerTest {

    private NationalityChangedActionTransformer nationalityChangedActionTransformer;

    @Before
    public void setup() {
        nationalityChangedActionTransformer = new NationalityChangedActionTransformer();
    }

    @Test
    public void testPassportFieldHidden() {
        CountryDomain countryNoPassportRequired = new CountryDomain("", "", "", false, "");

        Observable.just(NationalityChangedAction.create(countryNoPassportRequired))
                .compose(nationalityChangedActionTransformer)
                .test()
                .assertValue(FormResult.create(Collections.singletonList(InputState.builder()
                        .id(com.whitbread.premierinn.R.id.personal_details_passport_number)
                        .failedValidationType(Input.ValidationType.NONE)
                        .value("")
                        .state(InputState.State.INVISIBLE).build())));
    }

    @Test
    public void testPassportFieldShown() {
        CountryDomain countryNoPassportRequired = new CountryDomain("", "", "", true, "");

        Observable.just(NationalityChangedAction.create(countryNoPassportRequired))
                .compose(nationalityChangedActionTransformer)
                .test()
                .assertValue(FormResult.create(Collections.singletonList(InputState.builder()
                        .id(com.whitbread.premierinn.R.id.personal_details_passport_number)
                        .failedValidationType(Input.ValidationType.REQUIRED)
                        .state(InputState.State.VISIBLE).build())));
    }
}